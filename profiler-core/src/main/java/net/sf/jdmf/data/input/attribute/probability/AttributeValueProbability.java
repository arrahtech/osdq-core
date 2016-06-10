/*
 * AttributeValueProbability.java
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
package net.sf.jdmf.data.input.attribute.probability;

/**
 * Associates an attribute value with the probability that an instance will be
 * classified as this attribute value.
 * 
 * @author quorthon
 */
public class AttributeValueProbability {
    private Comparable attributeValue;
    private Double probability;
    
    public AttributeValueProbability() {
        super();
    }
    
    public AttributeValueProbability( Comparable attributeValue, 
            Double probability ) {
        this();
        this.attributeValue = attributeValue;
        this.probability = probability;
    }
    
    @Override
    public String toString() {
        return "( " + attributeValue + ": " + probability + " )";
    }
    
    public Comparable getAttributeValue() {
        return attributeValue;
    }
    public void setAttributeValue( Comparable attributeValue ) {
        this.attributeValue = attributeValue;
    }
    public Double getProbability() {
        return probability;
    }
    public void setProbability( Double probability ) {
        this.probability = probability;
    }
}
