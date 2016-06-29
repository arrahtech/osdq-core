/*
 * PRISMAlgorithm.java
 *
 * Created: 2006-05-08
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
 * $LastChangedRevision: 7 $
 * $LastChangedDate: 2006-07-24 22:11:57 +0200 (pon, 24 lip 2006) $
 */
package net.sf.jdmf.algorithms.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.jdmf.algorithms.AbstractDataMiningAlgorithm;
import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.output.DataMiningModel;
import net.sf.jdmf.data.output.Rule;
import net.sf.jdmf.data.output.RuleElement;
import net.sf.jdmf.data.output.RuleInfo;


/**
 * Implements the PRISM classification algorithm as described in <i>Data Mining: 
 * Practical Machine Learning Tools and Techniques (Second Edition)</i> 
 * by Ian H. Witten and Eibe Frank (Morgan Kaufmann, 2005).
 * 
 * The algorithm finds rules that cover as many instances as possible. All rules
 * should be 100% accurate (not always possible). A list of rules is generated
 * for each decision value (the order of rules is IMPORTANT). 
 * 
 * <pre>
 * 1. Take a decision value.
 * 2. Start with a rule: IF true THEN ( decisionName = decisionValue ).
 * 3. Calculate the current rule's accuracy.
 * 4. If accuracy = 0% or there are no attributes left, output the current rule
 * list.
 * 5. If accuracy = 100%, remove all instances covered by this rule, add the 
 * rule to the list and continue point 2 with the remaining instances.
 * 6. If 0% &lt; accuracy &lt; 100%, extend the current rule for each attribute
 * value from remaining attributes.
 * 7. Find an extended rule with the highest accuracy and make it the current
 * rule. 
 * 8. Remove the attribute involved in the extended rule.
 * 9. Repeat 3-8.
 * 10. Repeat 1-9 for all decision values.
 * </pre>
 * 
 * @author quorthon
 */
public class PRISMAlgorithm extends AbstractDataMiningAlgorithm {
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
        
        for ( Comparable decisionValue : decision.getDistinctValues() ) {
            List<Rule> rulesBasedOnDecisionValue 
                = buildRulesBasedOnDecisionValue( attributes, decision, 
                    decisionValue );
            singleDecisionRules.addAll( rulesBasedOnDecisionValue );
        }
        
        return singleDecisionRules;
    }

    /**
     * Builds rules based on a single decision value.
     */
    protected List<Rule> buildRulesBasedOnDecisionValue( 
        List<Attribute> attributes, 
        Attribute decision, Comparable decisionValue ) {
        List<Attribute> attributesWithDecision 
            = new ArrayList<Attribute>( attributes );
        attributesWithDecision.add( decision );
        
        List<Instance> instances 
            = attributeConverter.convertToInstances( attributesWithDecision );
        
        List<Attribute> remainingAttributes 
            = new ArrayList<Attribute>( attributes );
        
        List<Rule> rulesBasedOnDecision = new ArrayList<Rule>();
        
        Rule currentRule = new Rule();
        currentRule.defineThen().attribute( decision.getName() )
            .equals( decisionValue );
        
        Double currentRuleAccuracy 
            = calculateAccuracy( currentRule, instances );
        
        while ( currentRuleAccuracy != 0.0 ) {
            if ( currentRuleAccuracy == 1.0 ) {
                rulesBasedOnDecision.add( currentRule );
                
                List<Instance> instancesCoveredByCurrentRule 
                    = getInstancesCoveredByRule( currentRule, instances );
                
                instances.removeAll( instancesCoveredByCurrentRule );
                
                currentRule = new Rule();
                currentRule.defineThen().attribute( decision.getName() )
                    .equals( decisionValue );
                
                remainingAttributes = new ArrayList<Attribute>( attributes );
            } else if ( remainingAttributes.isEmpty() ) { 
               break; 
            } else {
                List<RuleInfo> ruleInfos = new ArrayList<RuleInfo>();
                
                for ( Attribute attribute : remainingAttributes ) {
                    for ( Comparable attributeValue 
                            : attribute.getDistinctValues() ) {
                        Rule extendedRule = extendRule( currentRule, 
                            attribute.getName(), attributeValue );
                        
                        RuleInfo ruleInfo = generateRuleInfo( 
                            extendedRule, instances );
                        ruleInfos.add( ruleInfo );
                    }
                }
                
                Collections.sort( ruleInfos );
                
                RuleInfo bestRuleInfo = ruleInfos.get( ruleInfos.size() - 1 );
                
                currentRule = bestRuleInfo.getRule();
                
                RuleElement currentRuleLastCondition 
                    = currentRule.getLastCondition();
                
                String usedAttributeName 
                    = currentRuleLastCondition.getItem()
                        .getAttributeName();
                
                Iterator<Attribute> remainingAttributesIterator 
                    = remainingAttributes.iterator();
                
                while ( remainingAttributesIterator.hasNext() ) {
                    Attribute attribute = remainingAttributesIterator.next();
                    
                    if ( attribute.getName().equals( usedAttributeName ) ) {
                        remainingAttributesIterator.remove();
                        
                        break;
                    }
                }
            }
            
            currentRuleAccuracy = calculateAccuracy( currentRule, instances );
        }
        
        return rulesBasedOnDecision;
    }

    /**
     * Provides additional information about a rule (coverage and accuracy).
     */
    protected RuleInfo generateRuleInfo( Rule rule, List<Instance> instances ) {
        Integer ruleCoverage = calculateCoverage( rule, instances );
        Double ruleAccuracy = calculateAccuracy( rule, instances );
        
        RuleInfo ruleInfo = new RuleInfo( rule, ruleCoverage, ruleAccuracy );
        
        return ruleInfo;
    }

    /**
     * Extends a rule with another condition.
     */
    protected Rule extendRule( Rule rule, String attributeName, 
            Comparable attributeValue ) {
        Rule extendedRule = new Rule();
        extendedRule.setConditions( 
            new ArrayList<RuleElement>( rule.getConditions() ) );
        extendedRule.setConsequences( 
            new ArrayList<RuleElement>( rule.getConsequences() ) );
        
        if ( extendedRule.getConditions().isEmpty() ) {
            extendedRule.defineIf().attribute( attributeName )
                .equals( attributeValue );
        } else {
            RuleElement lastCondition = rule.getLastCondition();
            lastCondition.and( extendedRule ).attribute( attributeName )
                .equals( attributeValue );
        }
        
        return extendedRule;
    }
    
    @Override
    protected String getName() {
        return "PRISM Algorithm";
    }
}
