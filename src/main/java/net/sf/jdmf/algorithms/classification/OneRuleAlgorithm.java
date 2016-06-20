/*
 * OneRuleAlgorithm.java
 *
 * Created: 2006-04-20
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
 * $LastChangedRevision: 5 $
 * $LastChangedDate: 2006-07-10 22:49:03 +0200 (pon, 10 lip 2006) $
 */
package net.sf.jdmf.algorithms.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.jdmf.algorithms.AbstractDataMiningAlgorithm;
import net.sf.jdmf.algorithms.classification.util.AttributeValuePartitioner;
import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.attribute.AttributeType;
import net.sf.jdmf.data.input.attribute.AttributeValuePair;
import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.output.DataMiningModel;
import net.sf.jdmf.data.output.Rule;


/**
 * Implements the 1R classification algorithm as described in <i>Data Mining: 
 * Practical Machine Learning Tools and Techniques (Second Edition)</i> 
 * by Ian H. Witten and Eibe Frank (Morgan Kaufmann, 2005).
 * <pre>
 * 
 * The idea is simple:
 * 1. Take an attribute.
 * 2. Select a value of this attribute.
 * 3. Select a value of the decision attribute.
 * 4. Calculate the coverage of an instance containing both attribute values.
 * 5. Repeat 3-4 for all decision values.
 * 6. Choose the instance with the highest coverage (randomly, if there are 
 * more winners) and convert it to a rule.
 * 7. Repeat 2-6 for all values of this attribute. 
 * 8. Calculate the error sum for all rules based on this attribute. The error
 * rate for each rule is defined as the number of times the attribute value 
 * occurs minus the coverage of the rule.
 * 9. Repeat 1-8 for all attributes.
 * 10. Choose the attribute with the smallest error sum and output 
 * classification rules based on this attribute.
 * 
 * </pre>
 * 
 * If the attribute is numeric, the values are partitioned to achieve
 * a reasonable number of rules (instead of calculating coverage, which would
 * result in quite a few rules...).
 * 
 * @author quorthon
 * @see net.sf.jdmf.algorithms.classification.util.AttributeValuePartitioner
 */
public class OneRuleAlgorithm extends AbstractDataMiningAlgorithm {
    protected AttributeValuePartitioner attributeValuePartitioner 
        = new AttributeValuePartitioner();
    
    /**
     * @see net.sf.jdmf.algorithms.DataMiningAlgorithm#analyze(net.sf.jdmf.data.input.InputData)
     */
    public DataMiningModel analyze( InputData inputData ) {
        DataMiningModel dataMiningModel = new DataMiningModel();
        List<Rule> rules = dataMiningModel.getRules();

        for ( Attribute decision : inputData.getDecisions() ) {
            List<Rule> singleDecisionRules 
                = analyzeDecision( decision, inputData.getAttributes() );
            
            rules.addAll( singleDecisionRules );
        }
        
        return dataMiningModel;
    }
    
    /**
     * Analyzes a single decision.
     */
    protected List<Rule> analyzeDecision( Attribute decision, 
            List<Attribute> attributes ) {
        List<Rule> singleDecisionRules = new ArrayList<Rule>();
        Integer lowestErrorSum = 0;
        
        Map<List<Rule>, Integer> errorSum 
            = calculateErrorSumForAttributes( decision, attributes );
        
        for ( Map.Entry<List<Rule>, Integer> errorSumEntry 
                : errorSum.entrySet() ) {
            if ( singleDecisionRules.isEmpty() 
                    || errorSumEntry.getValue().compareTo( 
                        lowestErrorSum ) < 0 ) {
                lowestErrorSum = errorSumEntry.getValue();
                
                singleDecisionRules.clear();
                singleDecisionRules.addAll( errorSumEntry.getKey() );
            } else if ( errorSumEntry.getValue().compareTo( 
                    lowestErrorSum ) == 0 ) {
                singleDecisionRules.addAll( errorSumEntry.getKey() );
            }
        }
        
        return singleDecisionRules;
    }
    
    /**
     * Calculates the error sum for all attributes.
     */
    protected Map<List<Rule>, Integer> calculateErrorSumForAttributes( 
            Attribute decision, List<Attribute> attributes ) {
        Map<List<Rule>, Integer> errorSum 
            = new LinkedHashMap<List<Rule>, Integer>();
        
        for ( Attribute attribute : attributes ) {
            List<Rule> rulesForAttribute 
                = generateRulesForAttribute( attribute, decision );
            
            List<Attribute> requiredAttributes = new ArrayList<Attribute>();
            requiredAttributes.add( attribute );
            requiredAttributes.add( decision );
            
            List<Instance> instances 
                = attributeConverter.convertToInstances( requiredAttributes );
            
            Integer errorSumForAttribute 
                = calculateErrorSum( rulesForAttribute, instances );
            
            errorSum.put( rulesForAttribute, errorSumForAttribute );
        }
        
        return errorSum;
    }
    
    /**
     * Calculates the error sum for a single attribute.
     */
    protected Integer calculateErrorSum( List<Rule> rulesForAttribute, 
            List<Instance> instances ) {
        Integer errorCountForAttribute = instances.size();
        
        for ( Rule ruleForAttribute : rulesForAttribute ) {
            errorCountForAttribute 
                -= calculateCoverage( ruleForAttribute, instances );
        }
        
        return errorCountForAttribute;
    }
    
    /**
     * Generates rules for a single attribute.
     */
    protected List<Rule> generateRulesForAttribute( 
            Attribute attribute, Attribute decision ) {
        List<Rule> rulesBasedOnAttribute = new ArrayList<Rule>();
        
        AttributeType attributeType = attribute.getType();
        
        if ( attributeType.equals( AttributeType.NOMINAL ) ) {
            for ( Comparable attributeValue : attribute.getDistinctValues() ) {
                Comparable bestDecisionValue 
                    = chooseBestDecisionValueForAttributeValue( decision, 
                        attribute, attributeValue );
                
                Rule rule = new Rule();
                rule.defineIf().attribute( attribute.getName() )
                    .equals( attributeValue );
                rule.defineThen().attribute( decision.getName() )
                    .equals( bestDecisionValue );
                
                rulesBasedOnAttribute.add( rule );
            }
        } else if ( attributeType.equals( AttributeType.NUMERIC ) ) {
            List<AttributeValuePair> valuePairs 
                = attributeConverter.convertToValuePairs( attribute, decision );
            
            rulesBasedOnAttribute = attributeValuePartitioner.partitionValues( 
                attribute.getName(), decision.getName(), valuePairs, 3 );
        }
        
        return rulesBasedOnAttribute;
    }
    
    /**
     * Chooses the best decision value for the selected attribute value (coming
     * from the instance with the highest coverage containing the selected
     * attribute value as well).
     */
    protected Comparable chooseBestDecisionValueForAttributeValue( 
            Attribute decision, Attribute attribute, 
            Comparable selectedAttributeValue ) {
        Map<Comparable, Integer> coverageForAttributeValue 
            = calculateCoverageForAttributeValue( 
                decision, attribute, selectedAttributeValue );
        
        Comparable bestDecisionValue = null;
        Integer bestDecisionValueCoverage = null;
        
        List<Comparable> exAequoDecisionValues = new ArrayList<Comparable>();
        
        for ( Map.Entry<Comparable, Integer> coverageEntry 
                : coverageForAttributeValue.entrySet() ) {
            if ( bestDecisionValue == null 
                    || coverageEntry.getValue().compareTo( 
                        bestDecisionValueCoverage ) > 0 ) {
                bestDecisionValue = coverageEntry.getKey();
                bestDecisionValueCoverage = coverageEntry.getValue();
                
                exAequoDecisionValues.clear();
                exAequoDecisionValues.add( coverageEntry.getKey() );
            } else if ( coverageEntry.getValue().compareTo( 
                    bestDecisionValueCoverage ) == 0 ) {
                exAequoDecisionValues.add( coverageEntry.getKey() );
            }
        }
        
        if ( exAequoDecisionValues.size() > 1 ) {
            Random random = new Random();
            
            bestDecisionValue = exAequoDecisionValues.get( 
                random.nextInt( exAequoDecisionValues.size() ) ); 
        }
        
        return bestDecisionValue;
    }

    /**
     * Calculates coverage for the selected attribute value and all decision
     * values.
     */
    protected Map<Comparable, Integer> calculateCoverageForAttributeValue( 
            Attribute decision, Attribute attribute, 
            Comparable selectedAttributeValue ) {
        Map<Comparable, Integer> coverageForSelectedAttributeValue 
            = new LinkedHashMap<Comparable, Integer>();
        
        for ( Comparable decisionValue : decision.getDistinctValues() ) {
            List<Attribute> attributes = new ArrayList<Attribute>();
            attributes.add( attribute );
            attributes.add( decision );
            
            List<Instance> instances 
                = attributeConverter.convertToInstances( attributes );
            
            Rule ruleForSelectedValue = new Rule();
            ruleForSelectedValue.defineIf().attribute( attribute.getName() )
                .equals( selectedAttributeValue );
            ruleForSelectedValue.defineThen().attribute( decision.getName() )
                .equals( decisionValue );
            
            Integer decisionValueCoverage 
                = calculateCoverage( ruleForSelectedValue, instances );
            
            coverageForSelectedAttributeValue.put( 
                decisionValue, decisionValueCoverage );
        }
        
        return coverageForSelectedAttributeValue;
    }
    
    @Override
    protected String getName() {
        return "One Rule Algorithm";
    }
}
