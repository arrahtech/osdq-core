/*
 * DataMiningAlgorithm.java
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
 * $LastChangedRevision: 5 $
 * $LastChangedDate: 2006-07-10 22:49:03 +0200 (pon, 10 lip 2006) $
 */
package net.sf.jdmf.algorithms;

import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.output.DataMiningModel;

/**
 * Base interface for all data mining algorithms.
 * 
 * @see net.sf.jdmf.algorithms.AbstractDataMiningAlgorithm
 * @author quorthon
 */
public interface DataMiningAlgorithm {
    /**
     * Analyzes input data (attributes and decisions) and produces output data
     * (rules, decision trees, clusters, ...). 
     */
    DataMiningModel analyze( InputData inputData );
}
