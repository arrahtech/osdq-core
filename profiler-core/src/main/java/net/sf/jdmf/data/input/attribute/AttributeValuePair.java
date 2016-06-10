/*
 * AttributeValuePair.java
 *
 * Created: 2006-05-01
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
package net.sf.jdmf.data.input.attribute;

/**
 * Represents a pair of two attribute values.
 * 
 * @author quorthon
 */
public class AttributeValuePair implements Comparable<AttributeValuePair> {
    private Comparable firstValue;
    private Comparable secondValue;
    
    public AttributeValuePair() {
        super();
    }
    
    public AttributeValuePair( Comparable firstValue, Comparable secondValue ) {
        this();
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }
    
    @SuppressWarnings("unchecked")
    public int compareTo( AttributeValuePair valuePair ) {
        int comparisonResult 
            = this.firstValue.compareTo( valuePair.firstValue );
        
        if ( comparisonResult == 0 ) {
            comparisonResult 
                = this.secondValue.compareTo( valuePair.secondValue );
        }
        
        return comparisonResult;
    }
    
    @Override
    public String toString() {
        return "( " + firstValue + ", " + secondValue + " )";
    }
    
    public Comparable getFirstValue() {
        return firstValue;
    }
    public void setFirstValue( Comparable firstValue ) {
        this.firstValue = firstValue;
    }
    public Comparable getSecondValue() {
        return secondValue;
    }
    public void setSecondValue( Comparable secondValue ) {
        this.secondValue = secondValue;
    }
}
