/*
 * AttributeValueIncidence.java
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
 * $LastChangedRevision: 3 $
 * $LastChangedDate: 2006-07-02 21:52:16 +0200 (nie, 02 lip 2006) $
 */
package net.sf.jdmf.data.input.attribute.incidence;

/**
 * Represents the incidence of an attribute value (the number of times 
 * this value has occurred).
 * 
 * @author quorthon
 */
public class AttributeValueIncidence {
    private Comparable attributeValue;
    private Integer incidence = 0;
    
    public AttributeValueIncidence( Comparable attributeValue ) {
        this.attributeValue = attributeValue;
    }
    
    /**
     * Checks if the current attribute value is equal to this attribute value
     * and increases its incidence, if appropriate.
     */
    public void checkAttributeValue( Comparable currentAttributeValue ) {
        if ( attributeValue.equals( currentAttributeValue ) ) {
            ++incidence;
        }
    }
    
    @Override
    public boolean equals( Object obj ) {
        AttributeValueIncidence valueIncidence = (AttributeValueIncidence) obj;
        
        return this.attributeValue.equals( valueIncidence.attributeValue );
    }
    
    /**
     * Resets the incidence to zero.
     */
    public void reset() {
        incidence = 0;
    }
    
    @Override
    public String toString() {
        return "( " + attributeValue + ", " + incidence + " )";
    }
    
    public Comparable getAttributeValue() {
        return attributeValue;
    }
    
    public Integer getCurrentIncidence() {
        return incidence;
    }
}
