/*
 * Cluster.java
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
 * $LastChangedRevision: 3 $
 * $LastChangedDate: 2006-07-02 21:52:16 +0200 (nie, 02 lip 2006) $
 */
package net.sf.jdmf.data.output.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;

/**
 * Defines a cluster (currently a group of points relatively close to the 
 * centroid; generally - a group of instances being similar to each other in 
 * a way, but different from other instances; this cluster definition has also 
 * a name and information about the percentage of all points falling into 
 * this cluster).
 * 
 * @author quorthon
 */
public class Cluster {
    public static final String ATTRIBUTE_NAME = "CLUSTER";
    
    private String name;
    private Vector<Double> centroid;
    private List<Vector<Double>> points = new ArrayList<Vector<Double>>();
    private Double pointPercentage;
    
    public void addPoint( Vector<Double> point ) {
        points.add( point );
    }
    
    @Override
    public String toString() {
        String output = "Cluster: " + name + "(" + pointPercentage + "%)\n";
        
        return output;
    }
    
    public List<Vector<Double>> getPoints() {
        return points;
    }
    public void setPoints( List<Vector<Double>> points ) {
        this.points = points;
    }
    public String getName() {
        return name;
    }
    public void setName( String name ) {
        this.name = name;
    }

    public Vector<Double> getCentroid() {
        return centroid;
    }

    public void setCentroid( Vector<Double> centroid ) {
        this.centroid = centroid;
    }

    public Double getPointPercentage() {
        return pointPercentage;
    }

    public void setPointPercentage( Double pointPercentage ) {
        this.pointPercentage = pointPercentage;
    }
}
