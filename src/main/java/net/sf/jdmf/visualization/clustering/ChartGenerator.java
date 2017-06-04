/*
 * ChartGenerator.java
 *
 * Created: 2006-05-23
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
package net.sf.jdmf.visualization.clustering;

import java.util.List;
import java.util.Vector;

import net.sf.jdmf.data.output.clustering.Cluster;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Generates various charts using JFreeChart based on the output data.
 * 
 * @author quorthon
 */
public class ChartGenerator {
    /**
     * Generates a XY chart visualizing the clusters found by a clustering
     * algorithm. Limited to two dimensions (because the chart is 2D). 
     * 
     * @param clusters clusters found by a clustering algorithm
     * @param firstAttributeIndex the index of the 1st dimension to visualize
     * @param firstAttributeName the name of the 1st dimension to visualize
     * @param secondAttributeIndex the index of the 2nd dimension to visualize
     * @param secondAttributeName the name of the 2nd dimension to visualize
     * @return a 2D XY chart visualizing the clusters found
     */
	private Vector<String> _attrName;
    public JFreeChart generateXYChart( List<Cluster> clusters, 
            Integer firstAttributeIndex, String firstAttributeName,
            Integer secondAttributeIndex, String secondAttributeName ) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        for ( Cluster cluster : clusters ) {
            XYSeries series = new XYSeries( cluster.getName() );
            
            for ( Vector<Double> point : cluster.getPoints() ) {
                series.add( point.get( firstAttributeIndex ), 
                    point.get( secondAttributeIndex ) );
            }
            
            dataset.addSeries( series );
        }
        
        XYToolTipGenerator xyToolTipGenerator = new XYToolTipGenerator()
        {
            public String generateToolTip(XYDataset dataset, int series, int item)
            {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(String.format("<html><p style='color:#0000ff;'>Series: '%s'</p>", dataset.getSeriesKey(series)));
                Cluster cl = clusters.get(series);
                Vector<Double> point = cl.getPoints().get(item);
                for ( int i=0; i < point.size(); i++ ) {
                    //stringBuilder.append(String.format("Attr:'%d'<br/>", d));
                	try {
                	String attr = _attrName.get(i);
                	stringBuilder.append(attr+" "+point.get(i)+"<br/>");
                	} catch (Exception e) {
                		// Do nothing 
                	}
                }
                stringBuilder.append("</html>");
                return stringBuilder.toString();
            }
        };
        
        /***
        return ChartFactory.createScatterPlot( "Cluster Analysis", 
            firstAttributeName, secondAttributeName, dataset, 
            PlotOrientation.VERTICAL, true, true, false );
        ***/
        JFreeChart jfc = ChartFactory.createScatterPlot( "Cluster Analysis", 
                firstAttributeName, secondAttributeName, dataset, 
                PlotOrientation.VERTICAL, true, true, false );
        
        XYItemRenderer render = jfc.getXYPlot().getRenderer();
        render.setBaseToolTipGenerator(xyToolTipGenerator);
        return jfc;
    }
    
    /**
     * Generates a pie chart showing the percentage of points falling into each
     * cluster.
     * 
     * @param clusters clusters found by a clustering algorithm
     * @return a pie chart showing the distribution of points into clusters
     */
    public JFreeChart generatePieChart( List<Cluster> clusters ) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for ( Cluster cluster : clusters ) {
            dataset.setValue( cluster.getName(), cluster.getPointPercentage() );
        }
        
        return ChartFactory.createPieChart3D( "Cluster Analysis", dataset, 
            true, true, false );
    }
    
	public void setAttributes (Vector<String> attrName ) {
    	_attrName = (Vector<String>) attrName;
    }

}
