/*
 * AttributeTypeFactoryTest.java
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
 * $LastChangedRevision: 5 $
 * $LastChangedDate: 2006-07-10 22:49:03 +0200 (pon, 10 lip 2006) $
 */
package net.sf.jdmf.data.input.attribute;

import java.util.Date;

import net.sf.jdmf.data.input.attribute.AttributeType;
import net.sf.jdmf.data.input.attribute.AttributeTypeFactory;

import junit.framework.TestCase;

public class AttributeTypeFactoryTest extends TestCase {
    private AttributeTypeFactory attributeTypeFactory;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        attributeTypeFactory = new AttributeTypeFactory();
    }
    
    public void testGetAttributeTypeForDouble() {
        AttributeType attributeType 
            = attributeTypeFactory.getAttributeType( Double.class );
        
        assertEquals( AttributeType.NUMERIC, attributeType );
    }
    
    public void testGetAttributeTypeForString() {
        AttributeType attributeType 
            = attributeTypeFactory.getAttributeType( String.class );
        
        assertEquals( AttributeType.NOMINAL, attributeType );
    }
    
    public void testGetAttributeTypeForDate() {
        AttributeType attributeType 
            = attributeTypeFactory.getAttributeType( Date.class );
        
        assertEquals( AttributeType.NUMERIC, attributeType );
    }
    
    public void testGetAttributeTypeForBoolean() {
        AttributeType attributeType 
            = attributeTypeFactory.getAttributeType( Boolean.class );
        
        assertEquals( AttributeType.NOMINAL, attributeType );
    }
    
    public void testGetAttributeTypeForNull() {
        AttributeType attributeType 
            = attributeTypeFactory.getAttributeType( null );
    
        assertEquals( AttributeType.EMPTY, attributeType );        
    }
}
