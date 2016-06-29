/*
 * RuleElement.java
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
import java.util.Stack;

import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.operators.EqualityOperator;
import net.sf.jdmf.data.operators.LogicalOperator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A single rule element that consists of an item, a logical operator that links
 * this element to other elements within a rule; alternatively, a rule element
 * may contain a list of subelements. The simplest rule element consists of a
 * single item:
 * <pre>
 * (first_colour = 'red')
 * </pre>
 * Rule elements define how items and logical operators are grouped within
 * a rule and evaluated. Example:
 * <pre>
 * (first_colour = 'red') AND (second_colour = 'blue' OR second_colour = 'cyan')
 * </pre>
 * There are two rule elements here: the first defines a single item 
 * (<code>first_colour = 'red'</code>) and has no logical operator. The second 
 * rule element does not define an item, but has a logical operator 
 * (<code>AND</code>) and a list of two subelements. The first subelement
 * defines a single item (<code>second_colour = 'blue'</code>) and has no 
 * logical operator, while the second subelement also defines a single item
 * (<code>second_colour = 'cyan'</code>) and a logical operator 
 * (<code>OR</code>).
 * 
 * Grouping is quite important if we are not satisfied with the default 
 * evaluation mode (from left to right; all logical operators are equally
 * important here).
 * 
 * Classes: <code>Rule</code> and <code>RuleElement</code> offer a few helpful 
 * methods allowing developers to define rules like sentences. As long as those
 * methods are used to define rules, there is no need to worry about 
 * implementation details (and when it comes to the details - it is worth 
 * mentioning that the logical operator is always defined in the same element as
 * the right operand of the operator). Subelements need to be defined separately
 * and added to the parent rule element explicitly. 
 * 
 * @author quorthon
 * @see net.sf.jdmf.data.output.Rule
 * @see net.sf.jdmf.data.output.AbstractRuleElementEvaluable
 * @see net.sf.jdmf.data.output.Item
 */
public class RuleElement extends AbstractRuleElementEvaluable {
    private static Log log = LogFactory.getLog( RuleElement.class );
    
    private Item item;
    private LogicalOperator logicalOperator;
    private List<RuleElement> subElements = new ArrayList<RuleElement>();
    
    public RuleElement() {
        super();
    }
    
    public RuleElement( Item item ) {
        this.item = item;
    }
    
    public RuleElement( Item item, LogicalOperator logicalOperator ) {
        this.item = item;
        this.logicalOperator = logicalOperator;
    }
    
    /**
     * @see net.sf.jdmf.data.output.Evaluable#evaluate(net.sf.jdmf.data.input.attribute.Instance)
     */
    public boolean evaluate( Instance instance ) {
        if ( subElements.isEmpty() ) {
            log.debug( "item: " + item );
            
            return item.evaluate( instance );
        }
        
        log.debug( "subElements: " + subElements );
        
        return evaluateRuleElements( subElements, instance );
    }

    /**
     * Defines the attribute name of an item.
     */
    public RuleElement attribute( String attributeName ) {
        item = new Item();
        item.setAttributeName( attributeName );
        
        return this;
    }
    
    /**
     * Defines the relation between the attribute name and value within an item.
     */
    public RuleElement equals( Comparable attributeValue ) {
        item.setEqualityOperator( EqualityOperator.EQUAL );
        item.setAttributeValue( attributeValue );
        
        return this;
    }
    
    /**
     * Defines the relation between the attribute name and value within an item.
     */
    public RuleElement doesNotEqual( Comparable attributeValue ) {
        item.setEqualityOperator( 
            EqualityOperator.NOT_EQUAL );
        item.setAttributeValue( attributeValue );
        
        return this;
    }
    
    /**
     * Defines the relation between the attribute name and value within an item.
     */
    public RuleElement isGreaterThan( Comparable attributeValue ) {
        item.setEqualityOperator( 
            EqualityOperator.GREATER_THAN );
        item.setAttributeValue( attributeValue );
        
        return this;
    }
    
    /**
     * Defines the relation between the attribute name and value within an item.
     */
    public RuleElement isGreaterThanOrEqualTo( Comparable attributeValue ) {
        item.setEqualityOperator( 
            EqualityOperator.GREATER_THAN_OR_EQUAL );
        item.setAttributeValue( attributeValue );
        
        return this;
    }
    
    /**
     * Defines the relation between the attribute name and value within an item.
     */
    public RuleElement isLowerThan( Comparable attributeValue ) {
        item.setEqualityOperator( 
            EqualityOperator.LOWER_THAN );
        item.setAttributeValue( attributeValue );
        
        return this;
    }
    
    /**
     * Defines the relation between the attribute name and value within an item.
     */
    public RuleElement isLowerThanOrEqualTo( Comparable attributeValue ) {
        item.setEqualityOperator( 
            EqualityOperator.LOWER_THAN_OR_EQUAL );
        item.setAttributeValue( attributeValue );
        
        return this;
    }
    
    /**
     * Appends a new rule element using the AND operator.
     */
    public RuleElement and( Rule parentRule ) {
        RuleElement ruleElement = classifyRuleElement( parentRule );
        ruleElement.setLogicalOperator( LogicalOperator.AND );
        
        return ruleElement;
    }
    
    /**
     * Appends a new rule element using the OR operator.
     */
    public RuleElement or( Rule parentRule ) {
        RuleElement ruleElement = classifyRuleElement( parentRule );
        ruleElement.setLogicalOperator( LogicalOperator.OR );
        
        return ruleElement;
    }
    
    /**
     * Appends a new rule element using the XOR operator.
     */
    public RuleElement xor( Rule parentRule ) {
        RuleElement ruleElement = classifyRuleElement( parentRule );
        ruleElement.setLogicalOperator( LogicalOperator.XOR );
        
        return ruleElement;
    }

    /**
     * Determines whether a new rule element should be appended to conditions
     * or consequences.
     */
    protected RuleElement classifyRuleElement( Rule parentRule ) {
        List<RuleElement> conditions = parentRule.getConditions();
        List<RuleElement> consequences = parentRule.getConsequences();
        
        RuleElement ruleElement = new RuleElement();
        
        if ( conditions.contains( this ) 
                && ( conditions.indexOf( this ) == conditions.size() - 1 ) ) {
            conditions.add( ruleElement );
        } else if ( consequences.contains( this ) 
                && ( consequences.indexOf( this ) 
                    == consequences.size() - 1 ) ) {
            consequences.add( ruleElement );
        } else {
            throw new RuleDefinitionException( "Could not define rule - " 
                + "the previous rule element is not defined" );
        }
        
        return ruleElement;
    }
    
    @Override
    public boolean equals( Object obj ) {
        RuleElement ruleElement = (RuleElement) obj;
        
        return ( this.item.equals( ruleElement.item ) ) 
            && ( this.subElements.equals( ruleElement.subElements ) );
    }
    
    @Override
    public String toString() {
        String output = "";
        
        if ( logicalOperator != null ) {
            output += " ";
            output += logicalOperator.toString();
            output += " ";
        }
        
        if ( item != null ) {
            output += item.toString();
        } else {
            output += "( ";
            
            for ( RuleElement subElement : subElements ) {
                output += subElement.toString();
            }
            
            output += " )";
        }
        
        return output;
    }
    
    public void addSubElement( RuleElement subElement ) {
        subElements.add( subElement );
    }
    
    public Iterator<RuleElement> subElementsIterator() {
        return subElements.iterator();
    }
    
    public Item getItem() {
        return item;
    }
    public void setItem( Item item ) {
        this.item = item;
    }
    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }
    public void setLogicalOperator( LogicalOperator logicalOperator ) {
        this.logicalOperator = logicalOperator;
    }
}
