/*
 * InputData.java
 *
 * Created: 2006-04-20
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.attribute.Instance;

/**
 * The base input data for data mining algorithms.
 * 
 * @author quorthon
 */
public class InputData {
    /**
     * Normal attributes.
     */
    protected List<Attribute> attributes = new ArrayList<Attribute>();
    /**
     * Decisions - attributes that participate directly in classifications.
     */
    protected List<Attribute> decisions = new ArrayList<Attribute>();
    /**
     * The instance that will be classified by a classification algorithm.
     */
    protected Instance instanceToBeClassified;
    
    public void addAttribute( Attribute attribute ) {
        attributes.add( attribute );
    }
    
    /**
     * Takes a particular attribute and marks it as a decision. The attribute
     * is removed from the set of available attributes and added to the set of
     * available decisions.
     * 
     * @param attributeName the name of the attribute being changed
     */
    public void setAttributeAsDecision( String attributeName ) {
        for ( Attribute attribute : attributes ) {
            if ( attribute.getName().equals( attributeName ) ) {
                attributes.remove( attribute );
                decisions.add( attribute );
                break;
            }
        }
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public List<Attribute> getDecisions() {
        return decisions;
    }
    
    public Instance getInstanceToBeClassified() {
        return instanceToBeClassified;
    }

    public void setInstanceToBeClassified( Instance instanceToBeClassified ) {
        this.instanceToBeClassified = instanceToBeClassified;
    }
}
