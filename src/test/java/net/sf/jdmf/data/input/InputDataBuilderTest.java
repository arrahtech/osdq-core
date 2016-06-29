/*
 * InputDataBuilderTest.java
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
 * $LastChangedRevision: 1 $
 * $LastChangedDate: 2006-05-23 23:25:36 +0200 (wto, 23 maj 2006) $
 */
package net.sf.jdmf.data.input;

import java.util.Collections;
import java.util.List;

import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.input.InputDataBuilder;
import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.attribute.AttributeType;
import net.sf.jdmf.data.input.comparators.AttributeNameComparator;
import net.sf.jdmf.data.sources.DataSource;

import junit.framework.TestCase;

public class InputDataBuilderTest extends TestCase {
    private InputDataBuilder inputDataBuilder;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        inputDataBuilder = new InputDataBuilder();
    }
    
    public void testImportFromDataSource() {
        DataSource dataSource = new MockDataSource();
        
        InputData inputData 
            = inputDataBuilder.build( dataSource );
        
        List<Attribute> attributes = inputData.getAttributes();
        assertEquals( 4, attributes.size() );
        
        Collections.sort( attributes, new AttributeNameComparator() );
        
        Attribute firstAttribute = attributes.get( 0 );
        assertEquals( "boolean", firstAttribute.getName() );
        assertEquals( 4, firstAttribute.getValues().size() );
        assertEquals( AttributeType.NOMINAL, firstAttribute.getType() );
        
        Attribute secondAttribute = attributes.get( 1 );
        assertEquals( "date", secondAttribute.getName() );
        assertEquals( 1, secondAttribute.getValues().size() );
        assertEquals( AttributeType.NUMERIC, secondAttribute.getType() );
        
        Attribute thirdAttribute = attributes.get( 2 );
        assertEquals( "double", thirdAttribute.getName() );
        assertEquals( 2, thirdAttribute.getValues().size() );
        assertEquals( AttributeType.NUMERIC, thirdAttribute.getType() );
        
        Attribute fifthAttribute = attributes.get( 3 );
        assertEquals( "string", fifthAttribute.getName() );
        assertEquals( 5, fifthAttribute.getValues().size() );
        assertEquals( AttributeType.NOMINAL, fifthAttribute.getType() );
    }
}
