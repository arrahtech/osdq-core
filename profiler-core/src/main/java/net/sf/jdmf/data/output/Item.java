/*
 * Item.java
 *
 * Created: 2006-04-25
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
 * $LastChangedRevision: 3 $
 * $LastChangedDate: 2006-07-02 21:52:16 +0200 (nie, 02 lip 2006) $
 */
package net.sf.jdmf.data.output;

import java.util.List;

import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.operators.EqualityOperator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Defines an item - an attribute name, value and a relation between them
 * (equal, not equal etc.).
 * 
 * @author quorthon
 */
public class Item implements Evaluable {
    private static Log log 
        = LogFactory.getLog( Item.class );
    
    private String attributeName;
    private Comparable attributeValue;
    private EqualityOperator equalityOperator;
    
    public Item() {
        super();
    }
    
    public Item( String attributeName, 
            Comparable attributeValue, EqualityOperator equalityOperator ) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.equalityOperator = equalityOperator;
    }
    
    public boolean evaluate( Instance instance ) {
        log.debug( "left: " + instance.getValue( this.attributeName ) );
        log.debug( "right: " + this.attributeValue );
        
        return equalityOperator.evaluate( 
            instance.getValue( this.attributeName ), this.attributeValue );
    }
    
    @Override
    public String toString() {
        String output = attributeName;
        output += " ";
        output += equalityOperator.toString();
        output += " ";
        output += attributeValue.toString();
        
        return output;
    }
    
    public String getAttributeName() {
        return attributeName;
    }
    public void setAttributeName( String attributeName ) {
        this.attributeName = attributeName;
    }
    public Comparable getAttributeValue() {
        return attributeValue;
    }
    public void setAttributeValue( Comparable attributeValue ) {
        this.attributeValue = attributeValue;
    }
    public EqualityOperator getEqualityOperator() {
        return equalityOperator;
    }
    public void setEqualityOperator( EqualityOperator equalityOperator ) {
        this.equalityOperator = equalityOperator;
    }
}
