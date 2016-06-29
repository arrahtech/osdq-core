/*
 * AttributeTypeFactory.java
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
package net.sf.jdmf.data.input.attribute;

import java.util.Date;


/**
 * Determines the attribute type based on the class of its values.
 * 
 * @author quorthon
 */
public class AttributeTypeFactory {
    public AttributeType getAttributeType( Class attributeClass ) {
        AttributeType attributeType = AttributeType.EMPTY;
        
        if ( Double.class.equals( attributeClass ) 
                || Date.class.equals( attributeClass ) ) {
            attributeType = AttributeType.NUMERIC;
        } else if ( String.class.equals( attributeClass ) 
                || Boolean.class.equals( attributeClass ) ) {
            attributeType = AttributeType.NOMINAL;
        }

        return attributeType;
    }
}
