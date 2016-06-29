/*
 * Instance.java
 *
 * Created: 2006-04-27
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A set of single values from different attributes (similar to a row in 
 * a table).
 * 
 * @author quorthon
 */
public class Instance implements Comparable<Instance> {
    private Map<String, Comparable> attributeValues 
        = new LinkedHashMap<String, Comparable>();
    
    public Instance() {
        super();
    }
    
    public Instance( Map<String, Comparable> attributeValues ) {
        this();
        this.attributeValues = attributeValues;
    }
    
    public boolean containsAttribute( String attributeName ) {
        return attributeValues.containsKey( attributeName );
    }
    
    public Set<String> getAttributeNames() {
        return attributeValues.keySet();
    }
    
    public Comparable getValue( String attributeName ) {
        return attributeValues.get( attributeName );
    }
    
    /**
     * Compares an instance to this instance using the attribute values.
     */
    @SuppressWarnings("unchecked")
    public int compareTo( Instance instance ) {
        Iterator<Comparable> thisValuesIterator 
            = this.attributeValues.values().iterator();
        Iterator<Comparable> rowValuesIterator 
            = instance.attributeValues.values().iterator();
        
        Integer thisValuesSize = this.attributeValues.size();
        Integer rowValuesSize = instance.attributeValues.size();
        
        int comparisonResult = thisValuesSize.compareTo( rowValuesSize );
        
        while ( thisValuesIterator.hasNext() ) {
            if ( comparisonResult != 0 ) {
                break;
            }
            
            Comparable thisValue = thisValuesIterator.next();
            Comparable rowValue = rowValuesIterator.next();
            
            comparisonResult = thisValue.compareTo( rowValue );
        }
        
        return comparisonResult;
    }
    
    @Override
    public boolean equals( Object obj ) {
        Instance instance = (Instance) obj;
        
        return this.attributeValues.equals( instance.attributeValues );
    }
    
    public int length() {
        return attributeValues.size();
    }
    
    public void addAttributeValue( String attributeName, 
            Comparable attributeValue ) {
        attributeValues.put( attributeName, attributeValue );
    }
}
