/*
 * AttributeConverter.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.jdmf.data.output.clustering.Cluster;


/**
 * Performs various conversions involving attributes.
 * 
 * @author quorthon
 */
public class AttributeConverter {
    /**
     * Converts attributes to instances.
     * 
     * @see net.sf.jdmf.data.input.attribute.Attribute
     * @see net.sf.jdmf.data.input.attribute.Instance
     */
    public List<Instance> convertToInstances( 
            List<Attribute> attributes ) {
        List<Instance> instances = new ArrayList<Instance>();
        
        int firstAttributeValuesCount = attributes.get( 0 ).getValues().size();
        
        for ( int i = 0; i < firstAttributeValuesCount; ++i ) {
            Instance instance = new Instance();
            
            for ( Attribute attribute : attributes ) {
                List<Comparable> attributeValues = attribute.getValues();
                
                instance.addAttributeValue( 
                    attribute.getName(), attributeValues.get( i ) );
            }
            
            instances.add( instance );
        }
        
        return instances;
    }
    
    /**
     * Converts clusters to attributes using the given name order.
     * 
     * @see net.sf.jdmf.data.output.clustering.Cluster
     * @see net.sf.jdmf.data.input.attribute.Attribute
     */
    public List<Attribute> convertToAttributes( List<Cluster> clusters, 
            List<String> attributeNameOrder ) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        
        for ( String attributeName : attributeNameOrder ) {
            Attribute attribute = new Attribute();
            attribute.setName( attributeName );
            
            attributes.add( attribute );
        }
        
        Attribute clusterDecision = new Attribute();
        clusterDecision.setName( Cluster.ATTRIBUTE_NAME );
        
        attributes.add( clusterDecision );
        
        for ( Cluster cluster : clusters ) {
            for ( Vector<Double> point : cluster.getPoints() ) {
                for ( int i = 0; i < attributeNameOrder.size(); ++i ) {
                    attributes.get( i ).addValue( point.get( i ) );
                }
                
                attributes.get( attributeNameOrder.size() )
                    .addValue( cluster.getName() );
            }
        }
        
        
        return attributes;
    }
    
    
    
    /**
     * Converts two attributes to value pairs.
     * 
     * @see net.sf.jdmf.data.input.attribute.Attribute
     * @see net.sf.jdmf.data.input.attribute.AttributeValuePair
     */
    public List<AttributeValuePair> convertToValuePairs( 
            Attribute firstAttribute, Attribute secondAttribute ) {
        List<AttributeValuePair> attributeValuePairs 
            = new ArrayList<AttributeValuePair>();
        
        Iterator<Comparable> firstAttributeValuesIterator 
            = firstAttribute.getValues().iterator();
        Iterator<Comparable> secondAttributeValuesIterator 
            = secondAttribute.getValues().iterator();
        
        while ( firstAttributeValuesIterator.hasNext() ) {
            Comparable firstAttributeValue 
                = firstAttributeValuesIterator.next();
            Comparable secondAttributeValue 
                = secondAttributeValuesIterator.next();
            
            AttributeValuePair attributeValuePair = new AttributeValuePair( 
                firstAttributeValue, secondAttributeValue );
            attributeValuePairs.add( attributeValuePair );
        }
        
        return attributeValuePairs;
    }
    
    /**
     * Converts attributes to points in a nD space (n = attributes.size()).
     * 
     * @see net.sf.jdmf.data.input.attribute.Attribute
     */
    public List<Vector<Double>> convertToPoints( List<Attribute> attributes ) {
        List<Vector<Double>> points = new ArrayList<Vector<Double>>();
        
        Attribute firstAttribute = attributes.get( 0 );
        
        for ( int i = 0; i < firstAttribute.getValues().size(); ++i ) {
            points.add( new Vector<Double>() );
        }
        
        for ( Attribute attribute : attributes ) {
            List<Comparable> attributeValues = attribute.getValues();
            
            for ( int i = 0; i < attributeValues.size(); ++i ) {
                points.get( i ).add( (Double) attributeValues.get( i ) );
            }
        }
        
        return points;
    }
    
    /**
     * @return first attribute values corresponding to the second attribute
     *      value in the given set of instances
     */
    public List<Comparable> groupFirstAttributeValuesBySecondAttributeValue( 
            String firstAttributeName, String secondAttributeName,  
            Comparable selectedValue, List<Instance> instances ) {
        List<Comparable> groupedValues = new ArrayList<Comparable>();
        
        for ( Instance instance : instances ) {
            Comparable secondAttributeValue 
                = instance.getValue( secondAttributeName );
            
            if ( secondAttributeValue.equals( selectedValue ) ) {
                groupedValues.add( instance.getValue( firstAttributeName ) );
            }
        }
        
        return groupedValues;
    }
}
