/*
 * KMeansAlgorithm.java
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
package net.sf.jdmf.algorithms.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.sf.jdmf.algorithms.AbstractDataMiningAlgorithm;
import net.sf.jdmf.algorithms.clustering.centroid.InitialCentroidChoiceStrategy;
import net.sf.jdmf.algorithms.clustering.centroid.impl.FirstRandomOthersDistantChoiceStrategy;
import net.sf.jdmf.data.input.InputData;
import net.sf.jdmf.data.input.attribute.Attribute;
import net.sf.jdmf.data.input.clustering.ClusteringInputData;
import net.sf.jdmf.data.output.DataMiningModel;
import net.sf.jdmf.data.output.clustering.Cluster;
import net.sf.jdmf.data.output.clustering.ClusteringDataMiningModel;
import net.sf.jdmf.util.MathCalculator;

/**
 * Implements the k-means clustering algorithm as described in <i>Data Mining: 
 * Practical Machine Learning Tools and Techniques (Second Edition)</i> 
 * by Ian H. Witten and Eibe Frank (Morgan Kaufmann, 2005).
 * <pre>
 * 1. Convert attributes to points (values are expected to be of type Double).
 * 2. Choose initial centroids based on the current choice strategy.
 * 3. Set new cluster centroids. For each point, calculate its distance from 
 * current centroids and choose the cluster with the nearest centroid.
 * 4. Find new cluster centroids (this implementation uses the mean of all
 * coordinates of points in each cluster).
 * 5. Calculate distance sum between old and new cluster centroids. If the sum
 * is greater than or equals the minimum distance sum specified in the 
 * algorithm's configuration, repeat 3-5.
 * </pre>
 * 
 * The predicted number of clusters is specified in the input data.
 * 
 * @author quorthon
 * @see net.sf.jdmf.algorithms.clustering.ClusteringExample
 * @see net.sf.jdmf.data.input.clustering.ClusteringInputData
 * @see net.sf.jdmf.data.output.clustering.Cluster
 * @see net.sf.jdmf.data.output.clustering.ClusteringDataMiningModel
 */
public class KMeansAlgorithm extends AbstractDataMiningAlgorithm {
    private InitialCentroidChoiceStrategy initialCentroidChoiceStrategy 
        = new FirstRandomOthersDistantChoiceStrategy();
    private MathCalculator mathCalculator = new MathCalculator();
    
    private Double minimumDistanceSumBetweenOldAndNewClusterCentroids = 0.0001;
    
    /**
     * @see net.sf.jdmf.algorithms.DataMiningAlgorithm#analyze(net.sf.jdmf.data.input.InputData)
     */
    public DataMiningModel analyze( InputData inputData ) {
        ClusteringInputData clusteringInputData 
            = (ClusteringInputData) inputData;
        
        List<Attribute> attributes = clusteringInputData.getAttributes();
        
        List<Vector<Double>> points 
            = attributeConverter.convertToPoints( attributes );
        
        List<Cluster> clusters 
            = findClusters( points, clusteringInputData.getNumberOfClusters() );
        
        ClusteringDataMiningModel dataMiningModel 
            = new ClusteringDataMiningModel();
        dataMiningModel.setClusters( clusters );
        
        List<String> attributeNames = new ArrayList<String>();
        
        for ( Attribute attribute : attributes ) {
            attributeNames.add( attribute.getName() );
        }
        
        dataMiningModel.setAttributeNameOrder( attributeNames );
        
        return dataMiningModel;
    }

    protected List<Cluster> findClusters( List<Vector<Double>> points, 
            Integer numberOfClusters ) {
        List<Vector<Double>> clusterCentroids 
            = initialCentroidChoiceStrategy.chooseInitialCentroids( 
                points, numberOfClusters );
        
        List<Cluster> clusters = new ArrayList<Cluster>();

        for ( int i = 0; i < clusterCentroids.size(); ++i ) {
            Cluster cluster = new Cluster();
            cluster.setName( Integer.toString( i + 1 ) );
            
            clusters.add( cluster );
        }
        
        Double distanceSumBetweenOldAndNewClusterCentroids = null;
        
        do {
            setNewClusterCentroids( clusterCentroids, clusters );
            
            putPointsInClusters( points, clusterCentroids, clusters );
            
            distanceSumBetweenOldAndNewClusterCentroids = 0.0;
            
            distanceSumBetweenOldAndNewClusterCentroids 
                = calculateDistanceSumBetweenOldAndNewClusterCentroids( points, 
                    clusterCentroids, clusters );
        } while ( distanceSumBetweenOldAndNewClusterCentroids 
            >= minimumDistanceSumBetweenOldAndNewClusterCentroids );
        
        return clusters;
    }

    protected Double calculateDistanceSumBetweenOldAndNewClusterCentroids( 
            List<Vector<Double>> points, List<Vector<Double>> clusterCentroids, 
            List<Cluster> clusters ) {
        Double distanceSumBetweenOldAndNewClusterCentroids = 0.0;
        
        clusterCentroids.clear();
        
        for ( Cluster cluster : clusters ) {
            Vector<Double> newClusterCentroid 
                = findNewClusterCentroid( cluster );
            
            Double distanceBetweenOldAndNewClusterCentroid 
                = mathCalculator.calculateDistance( 
                    cluster.getCentroid(), newClusterCentroid );
            
            distanceSumBetweenOldAndNewClusterCentroids 
                += distanceBetweenOldAndNewClusterCentroid;
            
            clusterCentroids.add( newClusterCentroid );
            
            cluster.setPointPercentage( 
                (double) cluster.getPoints().size() / points.size() * 100 );
        }
        return distanceSumBetweenOldAndNewClusterCentroids;
    }

    protected Vector<Double> findNewClusterCentroid( Cluster cluster ) {
        Vector<Double> newClusterCentroid = null;
        
        if ( cluster.getPoints().size() > 0 ) {
            newClusterCentroid = mathCalculator.calculateCentroid( 
                cluster.getPoints() );
        } else {
            newClusterCentroid = cluster.getCentroid();
        }
        return newClusterCentroid;
    }

    protected void setNewClusterCentroids( 
        List<Vector<Double>> clusterCentroids, List<Cluster> clusters ) {
        for ( int i = 0; i < clusters.size(); ++i ) {
            Cluster cluster = clusters.get( i );
            cluster.setCentroid( clusterCentroids.get( i ) );
            cluster.getPoints().clear();
        }
    }
    protected void putPointsInClusters( List<Vector<Double>> points, 
            List<Vector<Double>> clusterCentroids, List<Cluster> clusters ) {
        for ( Vector<Double> point : points ) {
            Integer nearestClusterIndex = null;
            Double minimalDistanceFromClusterCentroid = null;
            
            for ( int i = 0; i < clusterCentroids.size(); ++i ) {
                Double distanceFromClusterCentroid 
                    = mathCalculator.calculateDistance( 
                        point, clusterCentroids.get( i ) );
                
                if ( ( minimalDistanceFromClusterCentroid == null ) 
                        || ( distanceFromClusterCentroid 
                            < minimalDistanceFromClusterCentroid ) ) {
                    minimalDistanceFromClusterCentroid 
                        = distanceFromClusterCentroid;
                    
                    nearestClusterIndex = i;
                }
            }
            
            clusters.get( nearestClusterIndex ).addPoint( point );
        }
    }


    @Override
    protected String getName() {
        return "k-Means Algorithm";
    }

    public InitialCentroidChoiceStrategy getInitialCentroidChoiceStrategy() {
        return initialCentroidChoiceStrategy;
    }

    public void setInitialCentroidChoiceStrategy(
        InitialCentroidChoiceStrategy initialCentroidChoiceStrategy ) {
        this.initialCentroidChoiceStrategy = initialCentroidChoiceStrategy;
    }

    public Double getMinimumDistanceSumBetweenOldAndNewClusterCentroids() {
        return minimumDistanceSumBetweenOldAndNewClusterCentroids;
    }

    public void setMinimumDistanceSumBetweenOldAndNewClusterCentroids(
            Double minimumDistanceSumBetweenOldAndNewClusterCentroids ) {
        this.minimumDistanceSumBetweenOldAndNewClusterCentroids 
            = minimumDistanceSumBetweenOldAndNewClusterCentroids;
    }
}
