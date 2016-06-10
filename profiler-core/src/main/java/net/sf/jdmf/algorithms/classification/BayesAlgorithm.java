/*
 * BayesAlgorithm.java
 *
 * Created: 2006-05-02
 *
 * Java Data Mining Framework (http://jdmf.sourceforge.net)
 * Copyright (C) 2006  Janusz Marchewa
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * $Author: quorthon $
 * $LastChangedRevision: 6 $
 * $LastChangedDate: 2006-07-23 21:39:58 +0200 (nie, 23 lip 2006) $
 */
package net.sf.jdmf.algorithms.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jdmf.algorithms.AbstractDataMiningAlgorithm;
import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.attribute.AttributeType;
import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.input.attribute.probability.AttributeValueProbability;
import net.sf.jdmf.data.input.attribute.probability.AttributeValueProbabilityComparator;
import net.sf.jdmf.data.output.DataMiningModel;
import net.sf.jdmf.data.output.Rule;
import net.sf.jdmf.data.output.RuleElement;
import net.sf.jdmf.util.MathCalculator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Implements the Naive Bayes classification algorithm as described in 
 * <i>Data Mining: Practical Machine Learning Tools and Techniques 
 * (Second Edition)</i> by Ian H. Witten and Eibe Frank (Morgan Kaufmann, 2005).
 * <pre>
 * 1. Calculate probabilities for all decision values. The probability for a 
 * single decision value is a product of likelihoods calculated for all 
 * attribute values in the instance being classified. Each likelihood can be
 * defined as: the number of times an attribute value resulted in a particular
 * decision value divided by the number of occurences of this decision value.
 * The whole product is multiplied by the number of occurences of this decision
 * value and divided by the number of instances.  
 * 2. Output a classification rule based on the decision value with the highest 
 * probability.
 * </pre>
 * 
 * If an attribute is numeric, the likelihood is calculated using its normal 
 * distribution.
 * 
 * Why naive? Because the algorithm assumes that all attributes are equally 
 * important. If the likelihood of an attribute value equals 0, then the whole
 * decision value probability is also 0.
 * 
 * @author quorthon
 * @see net.sf.jdmf.data.input.attribute.probability.AttributeValueProbability
 */
public class BayesAlgorithm extends AbstractDataMiningAlgorithm {
    private static Log log = LogFactory.getLog( BayesAlgorithm.class );
    
    protected MathCalculator mathCalculator = new MathCalculator();
    
    /**
     * @see net.sf.jdmf.algorithms.DataMiningAlgorithm#analyze(net.sf.jdmf.data.input.InputData)
     */
    public DataMiningModel analyze( InputData inputData ) {
        DataMiningModel dataMiningModel = new DataMiningModel();
        List<Rule> rules = dataMiningModel.getRules();

        for ( Attribute decision : inputData.getDecisions() ) {
            Rule ruleBasedOnDecision 
                = analyzeDecision( decision, inputData.getAttributes(), 
                    inputData.getInstanceToBeClassified() );
            
            rules.add( ruleBasedOnDecision );
        }
        
        return dataMiningModel;
    }
    
    /**
     * Analyzes a single decision.
     */
    protected Rule analyzeDecision( Attribute decision, 
            List<Attribute> attributes, Instance instanceToBeClassified ) {
        List<AttributeValueProbability> decisionValueProbabilities 
            = calculateDecisionValueProbabilities( 
                decision, attributes, instanceToBeClassified );
        
        log.debug( 
            "decisionValueProbabilities: " + decisionValueProbabilities );
        
        Collections.sort( decisionValueProbabilities, 
            new AttributeValueProbabilityComparator() );
        AttributeValueProbability highestProbability 
            = decisionValueProbabilities.get( 
                decisionValueProbabilities.size() - 1 ); 
        
        Rule ruleBasedOnDecision = new Rule();
        RuleElement currentRuleElement = null;
        
        for ( String attributeName 
                : instanceToBeClassified.getAttributeNames() ) {
            if ( currentRuleElement == null ) {
                currentRuleElement = ruleBasedOnDecision.defineIf()
                    .attribute( attributeName )
                    .equals( instanceToBeClassified.getValue( attributeName ) );
            } else {
                currentRuleElement = currentRuleElement
                    .and( ruleBasedOnDecision ).attribute( attributeName )
                    .equals( instanceToBeClassified.getValue( attributeName ) );
            }
        }
        
        ruleBasedOnDecision.defineThen().attribute( decision.getName() )
            .equals( highestProbability.getAttributeValue() );
        
        return ruleBasedOnDecision;
    }

    /**
     * Calculates probabilities for all decision values.
     */
    protected List<AttributeValueProbability> 
            calculateDecisionValueProbabilities( 
                Attribute decision, List<Attribute> attributes, 
                Instance instanceToBeClassified ) {
        List<Attribute> attributesWithDecision 
            = new ArrayList<Attribute>( attributes );
        attributesWithDecision.add( decision );
        
        Set<String> attributeNames 
            = new HashSet<String>( instanceToBeClassified.getAttributeNames() );
        attributeNames.add( decision.getName() );
        
        List<Attribute> filteredAttributes = filterSelectedAttributes( 
            attributesWithDecision, attributeNames );
        
        List<Instance> instances 
            = attributeConverter.convertToInstances( filteredAttributes );
        
        filteredAttributes.remove( decision );
        
        Map<Comparable, Integer> decisionValuesCount 
            = decision.getValuesCount();
        
        List<AttributeValueProbability> probabilities 
            = new ArrayList<AttributeValueProbability>();
        
        Double likelihoodSum = 0.0;
        
        for ( Comparable decisionValue : decision.getDistinctValues() ) {
            Double decisionValueLikelihood = 1.0;
            
            for ( Attribute filteredAttribute : filteredAttributes ) {
                Double attributeValueLikelihood 
                    = calculateAttributeValueLikelihood( filteredAttribute, 
                        decision.getName(), decisionValue, 
                        decisionValuesCount.get( decisionValue ), 
                        instanceToBeClassified, instances );
                
                decisionValueLikelihood *= attributeValueLikelihood;
            }
            
            log.debug( "count( " + decisionValue + " ) = " 
                + decisionValuesCount.get( decisionValue ) );
            log.debug( "valuesCount = " + instances.size() );
            
            decisionValueLikelihood 
                *= decisionValuesCount.get( decisionValue );
            decisionValueLikelihood /= instances.size();
            
            log.debug( "likelihood( " + decisionValue + " ) = " 
                + decisionValueLikelihood );
            
            AttributeValueProbability probability 
                = new AttributeValueProbability( 
                    decisionValue, decisionValueLikelihood );
            
            probabilities.add( probability );
            
            likelihoodSum += decisionValueLikelihood;
        }
        
        for ( AttributeValueProbability probability : probabilities ) {
            probability.setProbability( 
                probability.getProbability() / likelihoodSum );
        }
        
        return probabilities;
    }

    /**
     * Calculates the likelihood for a single attribute value.
     */
    protected Double calculateAttributeValueLikelihood( Attribute attribute, 
            String decisionName, Comparable decisionValue, 
            Integer decisionValueCount, Instance instanceToBeClassified,
            List<Instance> instances ) {
        if ( attribute.getType().equals( AttributeType.NUMERIC ) ) {
            List<Comparable> selectedValues = attributeConverter
                .groupFirstAttributeValuesBySecondAttributeValue( 
                    attribute.getName(), decisionName, decisionValue, 
                    instances );
            
            return mathCalculator.calculateNormalDistribution( 
                selectedValues.toArray( 
                    new Comparable[ selectedValues.size() ] ), 
                (Double) instanceToBeClassified.getValue( 
                    attribute.getName() ) );
        }
        
        String attributeName = attribute.getName();
        
        Rule rule = generateRule( 
            attributeName, 
            instanceToBeClassified.getValue( attributeName ), 
            decisionName, decisionValue );
        
        Integer ruleCoverage = calculateCoverage( rule, instances );
        
        return ruleCoverage.doubleValue() / decisionValueCount;
    }
    
    /**
     * Filters attributes leaving only those explicitly specified.
     */
    protected List<Attribute> filterSelectedAttributes( 
            List<Attribute> attributes, Set<String> attributeNames ) {
        List<Attribute> selectedAttributes = new ArrayList<Attribute>();
        
        for ( Attribute attribute : attributes ) {
            if ( attributeNames.contains( attribute.getName() ) ) {
                selectedAttributes.add( attribute );
            }
        }
        
        return selectedAttributes;
    }
    
    /**
     * Generates a rule: IF ( attributeName = attributeValue ) 
     * THEN ( decisionName = decisionValue ). 
     */
    protected Rule generateRule( 
            String attributeName, Comparable attributeValue, 
            String decisionName, Comparable decisionValue ) {
        Rule rule = new Rule();
        rule.defineIf().attribute( attributeName ).equals( attributeValue );
        rule.defineThen().attribute( decisionName ).equals( decisionValue );
           
        return rule;
    }
    
    @Override
    protected String getName() {
        return "Bayes Algorithm";
    }
}
