/*
 * InputDataBuilder.java
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
package net.sf.jdmf.data.input;

import java.util.List;
import java.util.Map;

import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.sources.DataSource;


/**
 * Builds the input data for data mining algorithms.
 * 
 * @author quorthon
 * @see net.sf.jdmf.data.input.InputData
 * @see net.sf.jdmf.data.sources.DataSource
 */
public class InputDataBuilder {
    /**
     * Builds the input data using a particular data source.
     * 
     * @param dataSource the data source providing all required data
     * @return the form of input data accepted by data mining algorithms
     */
    public InputData build( DataSource dataSource ) {
        InputData inputData = new InputData();
        
        Map<String, List<Comparable>> attributes = dataSource.getAttributes();
        
        for ( Map.Entry<String, List<Comparable>> attributeEntry 
                : attributes.entrySet() ) {
            Attribute attribute = new Attribute( 
                attributeEntry.getKey(), attributeEntry.getValue() );
            
            inputData.addAttribute( attribute );
        }
        
        return inputData;
    }
}
