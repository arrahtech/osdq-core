package org.arrah.framework.datagen;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2016    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used to split RTM into
 * training data, validation data and test data
 * If timeseries data, it will put latest data
 * into test otherwise put random split
 * 
 */

import java.util.Date;
import java.util.Random;

import org.arrah.framework.ndtable.ReportTableModel;


public class SplitRTM {


	public SplitRTM() {
		
	} // Constructor
	
	// This function will assume duplicate records has been already removed
	// It will first shuffle the columns and get % accordingly.
	public ReportTableModel[] splitRandom(ReportTableModel rtm, int[] splitperc ) {
		int rowc= rtm.getModel().getRowCount();
		int colc = rtm.getModel().getColumnCount();
		int [] colIndex = new int[colc];
		for (int i = 0; i < colc; i++ ) {
			colIndex[i] = i;
		}
		rtm = ShuffleRTM.shuffleColumns(rtm, colIndex,0,rowc);
		
		// Randomization done, now sample it
		int sampleno = splitperc.length;
		int splitvalue[] = new int[sampleno];
		
		// may have rounding error because of floor 
		//but for large dataset it should not matter.
		for (int i=0; i < splitvalue.length; i++) {
			int buckno= (int) Math.floor(splitperc[i] * rowc / 100);
			if (i == 0)
				splitvalue[i] = buckno;
			else
				splitvalue[i] = splitvalue[i-1] + buckno; // Cumulative
		}
		
		// now create sample table
		// should we make it multi-threaded ???
		ReportTableModel[] rtmsplit = new ReportTableModel[sampleno];
		int rowindex=0;
		for (int i=0; i < sampleno; i++) {
			rtmsplit[i] = new ReportTableModel(rtm.getAllColName(),true,true);
			while (rowindex < rowc) {
				if (rowindex == splitvalue[i]) // now reached end of this loop or if there is 0
					break;
				rtmsplit[i].addFillRow(rtm.getRow(rowindex));
				rowindex++;
			}
			
		}
		return rtmsplit;
	}
	
	// This function will split RTM by date
	// epoch to first in date[0] next set in date[1]
	// date[last] - most recent data total split would be n+1
	// It will assume date is in sorted order
	public ReportTableModel[] splitByDate(ReportTableModel rtm, int dateCol, java.util.Date[] splitdate ) {
		
		// if will scan data in  chronological order
		int rowc = rtm.getModel().getRowCount();
		int splitc = splitdate.length;
		
		// now create sample table
		// should we make it multi-threaded ???
		
		ReportTableModel[] rtmsplit = new ReportTableModel[splitc+1]; // n+1 split
		for (int i=0; i < splitc+1; i++) {
			rtmsplit[i] = new ReportTableModel(rtm.getAllColName(),true,true);
		}
		
		for (int i=0; i < rowc; i++) {
			Object o = rtm.getModel().getValueAt(i, dateCol);
			boolean outbound = true;
			if (o == null) continue;
			try {
				
				for (int j=0; j < splitc; j++) {
					if (((Date)o).before(splitdate[j]) == true) {
						rtmsplit[j].addFillRow(rtm.getRow(i));
						outbound = false;
						break;
					}
				}
				// did not fit in any bucket as it is after most recent
				if (outbound == true) {
					rtmsplit[splitc].addFillRow(rtm.getRow(i));
				}
				
				
			} catch (Exception e) {
				System.out.println("Exception:" + e.getLocalizedMessage());
			}
		}
		
		return rtmsplit;
	}
	
	   // This util function will split the rtm in multiple rtm
    public static ReportTableModel[] splitRTM(ReportTableModel rtm, int count) {
        ReportTableModel[] newrtm = null;
        int rowc = rtm.getModel().getRowCount();

        if (count < 0  || rtm == null || rowc <= 0 || rtm.getModel().getColumnCount() <= 0)  {
            newrtm= new ReportTableModel[1];
            newrtm[0] = rtm; // no split required
            return newrtm;
        }

        int grp1 = Math.floorDiv(rowc,count);
        newrtm= new ReportTableModel[count];

        String[] colName = rtm.getAllColNameStr();

        for (int i=0; i < count; i++ ) {
            ReportTableModel newRTM = new ReportTableModel(colName,true,true);
            newrtm[i] = newRTM;
            for (int j=i*grp1; j < i*grp1 + grp1; j++) {
                newrtm[i].addFillRow(rtm.getRow(j));
            }
        }
        for (int j=count*grp1; j <rowc; j++) {
            newrtm[count-1].addFillRow(rtm.getRow(j));
        }

        return newrtm;
    }
    

    // This util function will create a random sample RTM
    public static ReportTableModel sampleRTM(ReportTableModel rtm, int count) {
        if (count < 0  || rtm == null || rtm.getModel().getRowCount() <= 0
                || rtm.getModel().getColumnCount() <= 0) return rtm; // no sampling needed

        String[] colName = rtm.getAllColNameStr();
        ReportTableModel newRTM = new ReportTableModel(colName,true,true);

        for (int i=0; i < count; i++ ) {
            int randInt = new Random().nextInt(count);
            Object[] row = rtm.getRow(randInt);
            newRTM.addFillRow(row);
        }

        return newRTM;
    }

	
} // End of SplitRTM
