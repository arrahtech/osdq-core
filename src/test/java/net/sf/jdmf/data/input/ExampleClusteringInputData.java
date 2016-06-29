/*
 * ExampleClusteringInputData.java
 *
 * Created: 2006-05-21
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
 * $LastChangedRevision: 1 $
 * $LastChangedDate: 2006-05-23 23:25:36 +0200 (wto, 23 maj 2006) $
 */
package net.sf.jdmf.data.input;

import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.clustering.ClusteringInputData;

public class ExampleClusteringInputData extends ClusteringInputData {
    public ExampleClusteringInputData() {
        super();
        
        prepareAttributes();
    }

    private void prepareAttributes() {
        Attribute firstAttribute = prepareFirstAttribute();
        Attribute secondAttribute = prepareSecondAttribute();
        
        addAttribute( firstAttribute );
        addAttribute( secondAttribute );
    }

    private Attribute prepareFirstAttribute() {
        Attribute firstAttribute = new Attribute();
        firstAttribute.setName( "first" );
        
        double[] attributeValues = { 
            10, 1, 5, 7, 1, 8, 16, 3, 14, 12, 8, 9, 22, 38, 30, 32, 36, 25, 37,
            29, 24, 31, 33, 27, 35, 40, 49, 44, 46, 36, 41, 35, 39, 38, 48, 47,
            11, 2, 32, 4, 47, 5, 7, 9, 12, 45, 39, 22, 29, 19
        };
        
        for ( int i = 0; i < attributeValues.length; i++ ) {
            firstAttribute.addValue( attributeValues[ i ] );
        }
        
        return firstAttribute;
    }
    
    private Attribute prepareSecondAttribute() {
        Attribute secondAttribute = new Attribute();
        secondAttribute.setName( "second" );
        
        double[] attributeValues = { 
            13, 20, 22, 14, 28, 24, 17, 17, 21, 27, 12, 26, 14, 9, 3, 5, 12, 10,
            7, 13, 11, 8, 9, 7, 30, 45, 35, 48, 36, 47, 31, 39, 44, 42, 34, 32,
            2, 48, 15, 22, 33, 29, 17, 8, 32, 26, 6, 31, 41, 37
        };
        
        for ( int i = 0; i < attributeValues.length; i++ ) {
            secondAttribute.addValue( attributeValues[ i ] );
        }
        
        return secondAttribute;
    }
}
