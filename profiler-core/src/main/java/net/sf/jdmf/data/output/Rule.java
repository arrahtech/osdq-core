/*
 * Rule.java
 *
 * Created: 2006-04-24
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
 * $LastChangedRevision: 9 $
 * $LastChangedDate: 2006-07-31 23:01:36 +0200 (pon, 31 lip 2006) $
 */
package net.sf.jdmf.data.output;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.operators.LogicalOperator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Defines a rule as a set of conditions and consequences. Example:
 * <pre>
 * IF (first_colour = 'red') AND (second_colour = 'blue')
 *     THEN (mixed_colour = 'magenta') OR (colour_count > 256)
 * </pre>
 * Conditions appear between IF and THEN, consequences - after THEN. 
 * The expressions between logical operators are called rule elements.
 * 
 * Defining rules is quite simple. Classes: <code>Rule</code> and 
 * <code>RuleElement</code> offer a few helpful methods allowing developers 
 * to define rules like sentences. The above example could be defined like this:
 * <pre>
 * Rule magentaRule = new Rule();
 * magentaRule.defineIf().attribute( "first_colour" ).equals( "red" )
 *     .and( magentaRule ).attribute( "second_colour" ).equals( "blue" );
 * magentaRule.defineThen().attribute( "mixed_colour" ).equals( "magenta" )
 *     .or( magentaRule ).attribute( "colour_count" ).isGreaterThan( 256 );
 * </pre>
 * 
 * @author quorthon
 * @see net.sf.jdmf.data.output.RuleElement
 */
public class Rule extends AbstractRuleElementEvaluable {
    private static Log log = LogFactory.getLog( Rule.class );
    
    private List<RuleElement> conditions = new ArrayList<RuleElement>();
    private List<RuleElement> consequences = new ArrayList<RuleElement>();
    
    /**
     * @see net.sf.jdmf.data.output.Evaluable#evaluate(net.sf.jdmf.data.input.attribute.Instance)
     */
    public boolean evaluate( Instance instance ) {
        log.debug( "conditions: " + conditions.toString() );
        log.debug( "consequences: " + consequences.toString() );
        
        return evaluateRuleElements( conditions, instance ) 
            && evaluateRuleElements( consequences, instance );
    }
    
    /**
     * Allows to define conditions. REMOVES all previous conditions. Appending
     * conditions can be achieved only by retrieving the last condition and 
     * using methods like and(), or(), xor().
     * 
     * @see net.sf.jdmf.data.output.RuleElement
     */
    public RuleElement defineIf() {
        conditions.clear();
        
        RuleElement condition = new RuleElement();
        conditions.add( condition );
        
        return condition;
    }
    
    /**
     * Allows to define consequences. REMOVES all previous consequences. 
     * Appending consequences can be achieved only by retrieving the last 
     * consequence and using methods like and(), or(), xor().
     * 
     * @see net.sf.jdmf.data.output.RuleElement
     */
    public RuleElement defineThen() {
        consequences.clear();
        
        RuleElement consequence = new RuleElement();
        consequences.add( consequence );
        
        return consequence;
    }
    
    public RuleElement getLastCondition() {
        return conditions.get( conditions.size() - 1 );
    }
    
    public RuleElement getLastConsequence() {
        return consequences.get( consequences.size() - 1 );
    }
    
    /**
     * Merges the given rule into this rule (appends the conditions and
     * consequences of the given rule to this rule's conditions and 
     * consequences).
     */
    public void merge( Rule rule ) {
        List<RuleElement> conditionsBeingMerged = rule.getConditions();
        
        if ( conditionsBeingMerged.isEmpty() == false ) {
            
            RuleElement firstMergedElement = new RuleElement( 
                conditionsBeingMerged.get( 0 ).getItem(), LogicalOperator.AND );
            
            Iterator<RuleElement> subElementsIterator 
                = conditionsBeingMerged.get( 0 ).subElementsIterator();
            
            while ( subElementsIterator.hasNext() ) {
                firstMergedElement.addSubElement( subElementsIterator.next() );
            }
            
            int firstMergedElementIndex = this.conditions.size();
            
            this.conditions.addAll( conditionsBeingMerged );
            this.conditions.set( firstMergedElementIndex, firstMergedElement );
        }
        
        List<RuleElement> consequencesBeingMerged = rule.getConsequences();
        
        if ( consequencesBeingMerged.isEmpty() == false ) {
            RuleElement firstMergedElement = new RuleElement( 
                consequencesBeingMerged.get( 0 ).getItem(), 
                LogicalOperator.AND );
            
            Iterator<RuleElement> subElementsIterator 
                = consequencesBeingMerged.get( 0 ).subElementsIterator();
            
            while ( subElementsIterator.hasNext() ) {
                firstMergedElement.addSubElement( subElementsIterator.next() );
            }
            
            int firstMergedElementIndex = this.consequences.size();
            
            this.consequences.addAll( consequencesBeingMerged );
            this.consequences.set( 
                firstMergedElementIndex, firstMergedElement );
        }
    }
    
    public void addCondition( RuleElement condition ) {
        conditions.add( condition );
    }
    
    public void addConsequence( RuleElement consequence ) {
        consequences.add( consequence );
    }
    
    @Override
    public String toString() {
        String output = "IF ";
        output += conditions.toString();
        output += " THEN ";
        output += consequences.toString();
        output += "\n";
        
        return output;
    }

    public List<RuleElement> getConditions() {
        return conditions;
    }

    public List<RuleElement> getConsequences() {
        return consequences;
    }

    public void setConditions( List<RuleElement> conditions ) {
        this.conditions = conditions;
    }

    public void setConsequences( List<RuleElement> consequences ) {
        this.consequences = consequences;
    }
}
