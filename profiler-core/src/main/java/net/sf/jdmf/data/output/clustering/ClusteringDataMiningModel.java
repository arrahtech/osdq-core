/*
 * ClusteringDataMiningModel.java
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
 * $LastChangedRevision: 4 $
 * $LastChangedDate: 2006-07-03 20:10:28 +0200 (pon, 03 lip 2006) $
 */
package net.sf.jdmf.data.output.clustering;

import java.util.ArrayList;
import java.util.List;

import net.sf.jdmf.data.output.DataMiningModel;

/**
 * Defines a data mining model specific to clustering algorithms.
 * 
 * @author quorthon
 * @see net.sf.jdmf.data.output.clustering.Cluster
 */
public class ClusteringDataMiningModel extends DataMiningModel {
    protected List<String> attributeNameOrder = new ArrayList<String>();
    protected List<Cluster> clusters = new ArrayList<Cluster>();

    public void addAttributeNameToOrder( String attributeName ) {
        attributeNameOrder.add( attributeName );
    }
    
    public void addCluster( Cluster cluster ) {
        clusters.add( cluster );
    }
    
    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters( List<Cluster> clusters ) {
        this.clusters = clusters;
    }

    public List<String> getAttributeNameOrder() {
        return attributeNameOrder;
    }

    public void setAttributeNameOrder( List<String> attributeNameOrder ) {
        this.attributeNameOrder = attributeNameOrder;
    }
}
