/*
 * DataMiningModel.java
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
 * $LastChangedRevision: 4 $
 * $LastChangedDate: 2006-07-03 20:10:28 +0200 (pon, 03 lip 2006) $
 */
package net.sf.jdmf.data.output;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Defines a data mining model (output data produced by data mining algorithms,
 * e.g. a list of rules).
 * 
 * @author quorthon
 */
public class DataMiningModel {
    protected List<Rule> rules = new ArrayList<Rule>();
    
    public void addRule( Rule rule ) {
        rules.add( rule );
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules( List<Rule> rules ) {
        this.rules = rules;
    }
}
