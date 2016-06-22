/*
 * MockDataSource.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jdmf.data.sources.DataSource;


public class MockDataSource implements DataSource {
    public Map<String, List<Comparable>> getAttributes() {
        Map<String, List<Comparable>> attributes 
            = new LinkedHashMap<String, List<Comparable>>();
        
        attributes.put( "double", buildValueList( Double.valueOf( 0.0 ), 2 ) );
        attributes.put( "string", buildValueList( "string", 5 ) );
        attributes.put( "date", 
            buildValueList( new Date( System.currentTimeMillis() ), 1 ) );
        attributes.put( "boolean", buildValueList( Boolean.TRUE, 4 ) );
        
        return attributes;
    }

    private List<Comparable> buildValueList( 
            Comparable value, int valueCount ) {
        List<Comparable> values = new ArrayList<Comparable>();
        
        for ( int i = 0; i < valueCount; ++i ) {
            values.add( value );
        }
        
        return values;
    }
}
