/*
 * AttributeValueProbabilityComparator.java
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
package net.sf.jdmf.data.input.attribute.probability;

import java.util.Comparator;

import net.sf.jdmf.data.input.attribute.incidence.AttributeValueIncidence;
import net.sf.jdmf.data.input.attribute.incidence.AttributeValueIncidenceComparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Compares two probability values ignoring the attribute values used.
 * 
 * @author quorthon
 */
public class AttributeValueProbabilityComparator 
        implements Comparator<AttributeValueProbability> {
    private static Log log 
        = LogFactory.getLog( AttributeValueIncidenceComparator.class );

    public int compare( AttributeValueProbability first, 
            AttributeValueProbability second ) {
        log.debug( "first AVL: " + first.toString() );
        log.debug( "second AVL: " + second.toString() );
        
        return first.getProbability().compareTo( second.getProbability() );
    }
}
