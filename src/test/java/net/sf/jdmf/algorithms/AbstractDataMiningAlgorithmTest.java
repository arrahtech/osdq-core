/*
 * AbstractDataMiningAlgorithmTest.java
 *
 * Created: 2006-05-01
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
package net.sf.jdmf.algorithms;

import java.util.ArrayList;
import java.util.List;

import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.output.Rule;

import junit.framework.TestCase;

public class AbstractDataMiningAlgorithmTest extends TestCase {
    private MockDataMiningAlgorithm algorithm;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        algorithm = new MockDataMiningAlgorithm();
    }
    
    public void testCalculateCoverage() {
        List<Instance> instances = new ArrayList<Instance>();
        instances.add( buildInstance( "true", "no" ) );
        instances.add( buildInstance( "false", "yes" ) );
        instances.add( buildInstance( "true", "no" ) );
        instances.add( buildInstance( "true", "yes" ) );
        instances.add( buildInstance( "false", "yes" ) );
        
        Rule ruleForSelectedValue = buildRule( "true", "yes" );
        
        Integer calculatedCoverage 
            = algorithm.calculateCoverage( ruleForSelectedValue, instances );
        
        assertEquals( Integer.valueOf( 1 ), calculatedCoverage );
        
        ruleForSelectedValue = buildRule( "false", "no" );
        
        calculatedCoverage 
            = algorithm.calculateCoverage( ruleForSelectedValue, instances );
        
        assertEquals( Integer.valueOf( 0 ), calculatedCoverage );
        
        ruleForSelectedValue = buildRule( "false", "yes" );
        
        calculatedCoverage 
            = algorithm.calculateCoverage( ruleForSelectedValue, instances );
        
        assertEquals( Integer.valueOf( 2 ), calculatedCoverage );
    }
    
    public void testCalculateAccuracy() {
        List<Instance> instances = new ArrayList<Instance>();
        instances.add( buildInstance( "true", "no" ) );
        instances.add( buildInstance( "false", "yes" ) );
        instances.add( buildInstance( "true", "no" ) );
        instances.add( buildInstance( "true", "yes" ) );
        instances.add( buildInstance( "false", "yes" ) );
        
        Rule ruleForSelectedValue = buildRule( "true", "yes" );
        
        Double calculatedAccuracy 
            = algorithm.calculateAccuracy( ruleForSelectedValue, instances );
        
        assertEquals( 1.0 / 3.0, calculatedAccuracy.doubleValue() );
        
        ruleForSelectedValue = buildRule( "false", "no" );
        
        calculatedAccuracy 
            = algorithm.calculateAccuracy( ruleForSelectedValue, instances );
        
        assertEquals( 0.0, calculatedAccuracy.doubleValue() );
        
        ruleForSelectedValue = buildRule( "false", "yes" );
        
        calculatedAccuracy 
            = algorithm.calculateAccuracy( ruleForSelectedValue, instances );
        
        assertEquals( 1.0, calculatedAccuracy.doubleValue() );
    }
    
    private Instance buildInstance( 
            Comparable firstValue, Comparable secondValue ) {
        Instance instance = new Instance();
        
        instance.addAttributeValue( "attr1", firstValue );
        instance.addAttributeValue( "attr2", secondValue );
        
        return instance;
    }
    
    private Rule buildRule( Comparable firstValue, Comparable secondValue ) {
        Rule rule = new Rule();
        rule.defineIf().attribute( "attr1" ).equals( firstValue );
        rule.defineThen().attribute( "attr2" ).equals( secondValue );
        
        return rule;
    }
}
