/*
 * AbstractRuleElementEvaluable.java
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
 * $LastChangedRevision: 4 $
 * $LastChangedDate: 2006-07-03 20:10:28 +0200 (pon, 03 lip 2006) $
 */
package net.sf.jdmf.data.output;

import java.util.List;
import java.util.Stack;

import net.sf.jdmf.data.input.attribute.Instance;
import net.sf.jdmf.data.operators.LogicalOperator;


/**
 * An abstract class that defines how to evaluate an expression consisting of
 * rule elements against an instance.
 * 
 * @author quorthon
 */
public abstract class AbstractRuleElementEvaluable implements Evaluable {
    /**
     * Evaluates a list of rule elements against an instance. The evaluation
     * goes from left to right, all operators are equally important. If there
     * are no elements to evaluate, returns true.
     * 
     * @see net.sf.jdmf.data.output.RuleElement
     */
    protected boolean evaluateRuleElements( List<RuleElement> ruleElements, 
            Instance instance ) {
        Stack<Boolean> evaluationStack = new Stack<Boolean>();
        
        for ( RuleElement ruleElement : ruleElements ) {
            evaluationStack.push( ruleElement.evaluate( instance ) );
            
            if ( evaluationStack.size() == 2 ) {
                boolean secondEvaluationResult = evaluationStack.pop();
                boolean firstEvaluationResult = evaluationStack.pop();
                
                LogicalOperator logicalOperator 
                    = ruleElement.getLogicalOperator();
                
                evaluationStack.push( logicalOperator.evaluate( 
                    firstEvaluationResult, secondEvaluationResult ) );
            }
        }
        
        if ( evaluationStack.isEmpty() ) {
            return true;
        }
        
        return evaluationStack.pop();
    }
}
