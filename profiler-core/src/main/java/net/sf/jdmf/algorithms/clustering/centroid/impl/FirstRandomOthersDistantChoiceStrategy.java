/*
 * FirstRandomOthersDistantChoiceStrategy.java
 *
 * Created: 2006-05-20
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
 * $LastChangedRevision: 8 $
 * $LastChangedDate: 2006-07-25 22:53:46 +0200 (wto, 25 lip 2006) $
 */
package net.sf.jdmf.algorithms.clustering.centroid.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.sf.jdmf.algorithms.clustering.centroid.InitialCentroidChoiceStrategy;
import net.sf.jdmf.data.output.clustering.Cluster;
import net.sf.jdmf.util.MathCalculator;

/**
 * A simple, but effective initial centroid choice strategy.
 * 
 * The first centroid is randomly chosen from available points, subsequent 
 * centroids are points with the biggest total distance from all centroids
 * already chosen.
 * 
 * @author quorthon
 */
public class FirstRandomOthersDistantChoiceStrategy 
        implements InitialCentroidChoiceStrategy {
    private MathCalculator mathCalculator = new MathCalculator();
    
    /**
     * @see net.sf.jdmf.algorithms.clustering.centroid.InitialCentroidChoiceStrategy#chooseInitialCentroids(java.util.List, java.lang.Integer)
     */
    public List<Vector<Double>> chooseInitialCentroids(
            List<Vector<Double>> points, Integer numberOfClusters ) {
        Random random = new Random();
        
        Vector<Double> firstClusterCentroid 
            = points.get( random.nextInt( points.size() ) );
        
        List<Vector<Double>> initialCentroids = new ArrayList<Vector<Double>>();
        initialCentroids.add( firstClusterCentroid );
        
        for ( int i = 1; i < numberOfClusters; ++i ) {
            Vector<Double> nextClusterCentroid = null;
            
            Double highestDistanceSumFromPreviousClusterCentroids = 0.0;
            
            for ( Vector<Double> point : points ) {
                Double distanceSumFromPreviousClusterCentroids = 0.0;
                
                for ( Vector<Double> initialCentroid : initialCentroids ) {
                    distanceSumFromPreviousClusterCentroids 
                        += mathCalculator.calculateDistance( 
                            point, initialCentroid );
                }
                
                if ( distanceSumFromPreviousClusterCentroids 
                        > highestDistanceSumFromPreviousClusterCentroids ) {
                    nextClusterCentroid = point;
                    
                    highestDistanceSumFromPreviousClusterCentroids 
                        = distanceSumFromPreviousClusterCentroids;
                }
            }
            
            initialCentroids.add( nextClusterCentroid );
        }
        
        return initialCentroids;
    }

}
