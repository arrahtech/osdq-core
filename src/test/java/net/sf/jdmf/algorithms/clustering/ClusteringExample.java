/*
 * ClusteringExample.java
 *
 * Created: 2006-05-04
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
package net.sf.jdmf.algorithms.clustering;

import net.sf.jdmf.data.input.ExampleClusteringInputData;
import net.sf.jdmf.data.input.clustering.ClusteringInputData;
import net.sf.jdmf.data.output.clustering.ClusteringDataMiningModel;
import net.sf.jdmf.visualization.clustering.ChartGenerator;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

public class ClusteringExample {
    
    public static void main( String[] args ) {
        KMeansAlgorithm algorithm = new KMeansAlgorithm();
        ChartGenerator chartGenerator = new ChartGenerator();
        
        ClusteringInputData inputData = new ExampleClusteringInputData();
        inputData.setNumberOfClusters( 3 );
        
        ClusteringDataMiningModel dataMiningModel 
            = (ClusteringDataMiningModel) algorithm.analyze( inputData );
        
        JFreeChart xyChart = chartGenerator.generateXYChart( 
            dataMiningModel.getClusters(), 0, "first", 1, "second" );
        
        ChartFrame chartFrame = new ChartFrame( "Clustering example", xyChart );
        chartFrame.pack();
        chartFrame.setVisible( true );
        
        JFreeChart pieChart = chartGenerator.generatePieChart( 
            dataMiningModel.getClusters() );
        
        ChartFrame anotherChartFrame 
            = new ChartFrame( "Clustering example", pieChart );
        anotherChartFrame.pack();
        anotherChartFrame.setVisible( true );
    }
}
