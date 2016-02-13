package org.arrah.framework.analytics;

/***************************************************
*     Copyright to Amish Choudhary    2014        *
*                                                 *
* Any part of code or file can be changed,        *
* redistributed, modified with the copyright      *
* information intact                              *
*                                                 *
* Author$ : Amish Choudhary                       *
*                                                 *
**************************************************/

/*
* This class provides Location based record 
* grouping and Analysis
*
*/

import java.util.*;

public class LocationAnalysis {	
		/*
		* Returns the results
		*/
		public class LocationGroup
		{
			private double centerLatitude;
			private double centerLongitude;
			private double radius;
			private List<List<String>> resultGroup;
			
			LocationGroup(double cLat, double cLong, double cradius)
			{
				centerLatitude = cLat;
				centerLongitude = cLong;
				radius = cradius;
				resultGroup = new ArrayList<List<String>>();
			}
			
			public double getCenterLatitude() {
				return centerLatitude;
			}
			
			public void setCenterLatitude(double centerLatitude) {
				this.centerLatitude = centerLatitude;
			}
			
			public double getCenterLongitude() {
				return centerLongitude;
			}
			
			public void setCenterLongitude(double centerLongitude) {
				this.centerLongitude = centerLongitude;
			}
			
			public double getRadius() {
				return radius;
			}

			public void setRadius(double radius) {
				this.radius = radius;
			}

			public List<List<String>> getResultGroup() {
				return resultGroup;
			}
			
			public void addResultGroupMember(List<String> resultGroup) {
				this.resultGroup.add(resultGroup);
			}	

		}
		
		/*
		* Comparator for comparing rows
		*/
		
		public static class  LocationComparator
		{
			
			public double compare(double cLat, double cLong, double cradius,Object[] record, int latIndex, int longIndex)
			{

    
                double rLat=Math.toRadians(Double.parseDouble(record[latIndex].toString()));
                double rLong=Math.toRadians(Double.parseDouble(record[longIndex].toString()));

                
                double centerLat=Math.toRadians(cLat);
                double centerLong=Math.toRadians(cLong );

                // http://en.wikipedia.org/wiki/Great-circle_distance
                /**
                 * The great-circle or orthodromic distance is the shortest distance between two points on the surface 
                 * of a sphere, measured along the surface of the sphere (as opposed to a straight line through
                 *  the sphere's interior). The distance between two points in Euclidean space is the length of
                 *   a straight line between them, but on the sphere there are no straight lines. 
                 *   In non-Euclidean geometry, straight lines are replaced with geodesics. 
                 *   Geodesics on the sphere are the great circles (circles on the sphere whose centers 
                 *   coincide with the center of the sphere).
                 * 
                 * 
                 */
                	
                double distance = 6371*2*Math.asin(Math.sqrt(Math.pow(Math.sin((Math.abs(rLat-centerLat))/2),2)+Math.cos(rLat)*Math.cos(centerLat)
                					*Math.pow(Math.sin((Math.abs(rLong-centerLong))/2),2)));
                
                return (  distance - cradius);
			}
			
		};
				
		// For Unit testing
		public static void main(String ... args)
		{
			/*
			List<Object[]> lRecordList = new ArrayList<Object[]>();
			
			double clat = Double.valueOf(args[1]);
			double clong = Double.valueOf(args[2]);
			double cRad = Double.valueOf(args[3]);
			LocationAnalysis.LocationComparator lcomp = new LocationAnalysis.LocationComparator();
			try(Scanner lFile = new Scanner( new File(args[0])))
			{
				while(lFile.hasNextLine())
				{
				 
					Object[] record = 	lFile.nextLine().split("\\s+");
					if(lcomp.compare(clat, clong, cRad, record , 7, 7) <=0)
					{
						lRecordList.add(record);
						System.out.println("Within Radius " + record);
					}
				}
			}
			catch (Exception excp)
			{
				excp.printStackTrace();
			} */
		} 
	} // end of class LocationAnalysis
