/*
 * AprioriAlgorithm.java
 *
 * Created: 2006-05-10
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
 * $LastChangedRevision: 8 $
 * $LastChangedDate: 2006-07-25 22:53:46 +0200 (wto, 25 lip 2006) $
 */
package net.sf.jdmf.algorithms.association;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.jdmf.algorithms.AbstractDataMiningAlgorithm;
import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.operators.EqualityOperator;
import net.sf.jdmf.data.output.Item;
import net.sf.jdmf.data.output.DataMiningModel;
import net.sf.jdmf.data.output.Rule;
import net.sf.jdmf.data.output.RuleElement;

import org.apache.commons.collections.CollectionUtils;

/**
 * Implements the Apriori association algorithm as described in <i>Data Mining: 
 * Practical Machine Learning Tools and Techniques (Second Edition)</i> 
 * by Ian H. Witten and Eibe Frank (Morgan Kaufmann, 2005).
 * <pre>
 * 1. Take all attributes and generate 1-item sets from them.
 * 2. Remove item sets with smaller coverage then required.
 * 3. Generate rules based on the remaining item sets.
 * 4. Mark rules accurate enough as association rules.
 * 5. Generate 2-item sets using the remaining 1-item sets.
 * 6. Repeat 2-3. First generate rules with one consequence, then with two etc.
 * If a rule with a set of consequences is not accurate enough, then any rule
 * with a superset of those consequences does not meet those requirements 
 * either.
 * 7. Repeat 4, then 5-6 for 3-item sets and so on (if there are n attributes,
 * then (n - 1)-item sets can be generated).
 * 8. Output all generated association rules with the required coverage
 * and accuracy. 
 * </pre>
 * 
 * @author quorthon
 */
public class AprioriAlgorithm extends AbstractDataMiningAlgorithm {
    protected Integer minRuleCoverage;
    protected Double minRuleAccuracy;

    /**
     * @see net.sf.jdmf.algorithms.DataMiningAlgorithm#analyze(net.sf.jdmf.data.input.InputData)
     */
    public DataMiningModel analyze( InputData inputData ) {
        List<Attribute> attributes = inputData.getAttributes();
        
        List<Instance> instances 
            = attributeConverter.convertToInstances( attributes );
        
        DataMiningModel dataMiningModel = new DataMiningModel();
        List<Rule> associationRules = dataMiningModel.getRules();
        
        List<Set<Item>> previousItemSets = new ArrayList<Set<Item>>();
        
        for ( Attribute attribute : attributes ) {
            for ( Comparable attributeValue : attribute.getDistinctValues() ) {
                Item item = new Item( attribute.getName(), attributeValue, 
                    EqualityOperator.EQUAL );
                
                Set<Item> itemSet = new LinkedHashSet<Item>();
                itemSet.add( item );
                
                if ( calculateCoverage( itemSet, instances ) 
                        >= minRuleCoverage ) {
                    Rule rule = generateRule( item );
                    
                    if ( calculateAccuracy( rule, instances ) 
                            >= minRuleAccuracy ) {
                        associationRules.add( rule );
                    }
                    
                    previousItemSets.add( itemSet );
                }
            }
        }
        
        for ( int i = 0; i < attributes.size() - 2; ++i ) {
            List<Set<Item>> currentItemSets = findCurrentItemSets( 
                previousItemSets, instances );
            
            List<Rule> rulesForCurrentItemSets 
                = findRulesForCurrentItemSets( currentItemSets, instances );
            
            associationRules.addAll( rulesForCurrentItemSets );
            
            previousItemSets = currentItemSets;
        }
        
        return dataMiningModel;
    }

    /**
     * Finds k-item sets based on (k - 1)-item sets.
     */
    @SuppressWarnings("unchecked")
    protected List<Set<Item>> findCurrentItemSets( 
            List<Set<Item>> previousItemSets, List<Instance> instances ) {
        List<Set<Item>> currentItemSets = new ArrayList<Set<Item>>();
        
        while ( previousItemSets.size() > 1 ) {
            Set<Item> firstItemSet = null;
            
            Iterator<Set<Item>> itemSetsIterator = previousItemSets.iterator();
            
            while ( itemSetsIterator.hasNext() ) {
                Set<Item> itemSet = itemSetsIterator.next();
                
                if ( firstItemSet != null ) {
                    Set<Item> currentItemSet = new LinkedHashSet<Item>();
                    currentItemSet.addAll( 
                        CollectionUtils.union( firstItemSet, itemSet ) );
                    
                    if ( ( calculateCoverage( currentItemSet, instances ) 
                                >= minRuleCoverage )
                            && ( currentItemSets.contains( currentItemSet ) 
                                == false ) ) {
                        currentItemSets.add( currentItemSet );
                    }
                } else {
                    firstItemSet = itemSet;
                }
            }
            
            previousItemSets.remove( firstItemSet );
        }
        
        
        return currentItemSets;
    }
    
    /**
     * Finds association rules for k-item sets.
     */
    protected List<Rule> findRulesForCurrentItemSets( 
            List<Set<Item>> currentItemSets, List<Instance> instances ) {
        List<Rule> rulesForCurrentItemSets = new ArrayList<Rule>();
        
        for ( Set<Item> currentItemSet : currentItemSets ) {
            Rule noConditionsRule = generateRule( new LinkedHashSet<Item>(), 
                currentItemSet );
            
            if ( calculateAccuracy( noConditionsRule, instances ) 
                    >= minRuleAccuracy ) {
                rulesForCurrentItemSets.add( noConditionsRule );
            }
            
            rulesForCurrentItemSets.addAll( generateDescendantRules( 
                currentItemSet, new LinkedHashSet<Item>(), instances ) );
        }
        
        return rulesForCurrentItemSets;
    }

    /**
     * Generates descendant rules. If there are k conditions and l consequences,
     * rules with (k - 1) conditions and (l + 1) consequences are generated,
     * one for each condition converted to a consequence. Rules not accurate
     * enough are removed.
     */
    protected List<Rule> generateDescendantRules( Set<Item> conditionItemSet, 
            Set<Item> consequenceItemSet, List<Instance> instances ) {
        List<Rule> descendantRules = new ArrayList<Rule>();
        
        if ( conditionItemSet.size() == 1 ) {
            return descendantRules;
        }
        
        for ( Item conditionItem : conditionItemSet ) {
            Set<Item> descendantConditionItemSet 
                = new LinkedHashSet<Item>( conditionItemSet );
            descendantConditionItemSet.remove( conditionItem );
            
            Set<Item> descendantConsequenceItemSet 
                = new LinkedHashSet<Item>( consequenceItemSet );
            descendantConsequenceItemSet.add( conditionItem );
            
            Rule descendantRule = generateRule( descendantConditionItemSet, 
                descendantConsequenceItemSet );
            
            Double descendantRuleAccuracy 
                = calculateAccuracy( descendantRule, instances );
            
            if ( descendantRuleAccuracy >= minRuleAccuracy ) {
                descendantRules.add( descendantRule );
                
                descendantRules.addAll( generateDescendantRules( 
                    descendantConditionItemSet, descendantConsequenceItemSet, 
                    instances ) );
            }
        }
        
        return descendantRules;
    }
    
    /**
     * Generates a rule based on the condition and consequence item sets.
     */
    protected Rule generateRule( Set<Item> conditionItemSet, 
            Set<Item> consequenceItemSet ) {
        Rule rule = new Rule();
        RuleElement currentRuleElement = null;
        
        for ( Item conditionItem : conditionItemSet ) {
            if ( currentRuleElement != null ) {
                currentRuleElement = currentRuleElement.and( rule )
                    .attribute( conditionItem.getAttributeName() )
                    .equals( conditionItem.getAttributeValue() );
            } else {
                currentRuleElement = rule.defineIf()
                    .attribute( conditionItem.getAttributeName() )
                    .equals( conditionItem.getAttributeValue() );
            }
        }
        
        currentRuleElement = null;
        
        for ( Item consequenceItem : consequenceItemSet ) {
            if ( currentRuleElement != null ) {
                currentRuleElement = currentRuleElement.and( rule )
                    .attribute( consequenceItem.getAttributeName() )
                    .equals( consequenceItem.getAttributeValue() );
            } else {
                currentRuleElement = rule.defineThen()
                    .attribute( consequenceItem.getAttributeName() )
                    .equals( consequenceItem.getAttributeValue() );
            }
        }
        
        return rule;
    }
    
    /**
     * Generates a rule with a single consequence.
     */
    protected Rule generateRule( Item item ) {
        Rule rule = new Rule();
        rule.defineThen().attribute( item.getAttributeName() )
            .equals( item.getAttributeValue() );
        
        return rule;
    }
    
    @Override
    protected String getName() {
        return "Apriori Algorithm";
    }

    public Integer getMinRuleCoverage() {
        return minRuleCoverage;
    }

    public void setMinRuleCoverage( Integer maxRuleCount ) {
        this.minRuleCoverage = maxRuleCount;
    }

    public Double getMinRuleAccuracy() {
        return minRuleAccuracy;
    }

    public void setMinRuleAccuracy( Double minRuleAccuracy ) {
        this.minRuleAccuracy = minRuleAccuracy;
    }
}
