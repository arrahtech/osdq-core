/*
 * AttributeValueIncidenceComparator.java
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

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Compares two incidence values ignoring the attribute values used.
 * 
 * @author quorthon
 */
public class AttributeValueIncidenceComparator 
        implements Comparator<AttributeValueIncidence> {
    private static Log log 
        = LogFactory.getLog( AttributeValueIncidenceComparator.class );
    
    public int compare( AttributeValueIncidence first, 
            AttributeValueIncidence second ) {
        log.debug( "first AVI: " + first.toString() );
        log.debug( "second AVI: " + second.toString() );
        
        return first.getCurrentIncidence().compareTo( 
            second.getCurrentIncidence() );
    }
}
