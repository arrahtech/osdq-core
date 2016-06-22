/*
 * AbstractDataMiningAlgorithm.java
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
package net.sf.jdmf.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.jdmf.data.input.attribute.AttributeConverter;
import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.output.Item;
import net.sf.jdmf.data.output.Rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Base abstract class for data mining algorithms. All algorithms should extend
 * this class.
 * 
 * @author quorthon
 */
public abstract class AbstractDataMiningAlgorithm 
        implements DataMiningAlgorithm {
    private static Log log 
        = LogFactory.getLog( AbstractDataMiningAlgorithm.class );
    
    protected AttributeConverter attributeConverter = new AttributeConverter();

    /**
     * Unused, but probably useful in GUIs.
     * 
     * @return the current algorithm's name
     */
    protected abstract String getName();

    /**
     * Calculates the number of instances covered by the rule.
     * This method will be moved to another class.
     */
    protected Integer calculateCoverage( Rule rule, List<Instance> instances ) {
        Integer coverage = 0;
        
        for ( Instance currentInstance : instances ) {
            if ( rule.evaluate( currentInstance ) ) {
                ++coverage;
            }
        }
        
        log.debug( "coverage( " + rule + " ) = " + coverage );
        
        return coverage;
    }
    
    /**
     * Calculates the percentage (represented by a fraction 0..1) of instances
     * covered by the rule in the whole set.
     * This method will be moved to another class.
     */
    protected Double calculateAccuracy( Rule rule, List<Instance> instances ) {
        Double accuracy = calculateCoverage( rule, instances ).doubleValue();
        
        if ( accuracy == 0.0 ) {
            return accuracy;
        }
        
        Rule conditionsOnly = new Rule();
        conditionsOnly.setConditions( rule.getConditions() );
        
        accuracy /= calculateCoverage( conditionsOnly, instances );
        
        log.debug( "accuracy( " + rule + " ) = " + accuracy );
        
        return accuracy;
    }
    
    /**
     * Extracts instances covered by the rule from the whole set.
     * This method will be moved to another class.
     */
    protected List<Instance> getInstancesCoveredByRule( Rule rule, 
            List<Instance> instances ) {
        List<Instance> instancesCovered = new ArrayList<Instance>();
        
        for ( Instance currentInstance : instances ) {
            if ( rule.evaluate( currentInstance ) ) {
                instancesCovered.add( currentInstance );
            }
        }
        
        return instancesCovered;
    }
    
    /**
     * Calculates the number of instances covered by the item set.
     * This method will be moved to another class.
     */
    protected Integer calculateCoverage( Set<Item> itemSet, 
            List<Instance> instances ) {
        Integer coverage = 0;
        
        for ( Instance currentInstance : instances ) {
            boolean isItemSetCovered = true;
            
            for ( Item item : itemSet ) {
                isItemSetCovered &= item.evaluate( currentInstance );
            }
            
            if ( isItemSetCovered ) {
                ++coverage;
            }
        }
        
        log.debug( "coverage( " + itemSet + " ) = " + coverage );
        
        return coverage;
    }
}
