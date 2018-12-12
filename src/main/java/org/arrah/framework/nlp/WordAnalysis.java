package org.arrah.framework.nlp;

/***********************************************
 *     Copyright to Vivek Kumar Singh  2018    *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with copyright      *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This file is used for analysing words
 * as part of NLP excercise.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.profile.StatisticalAnalysis;


public class WordAnalysis {

	public WordAnalysis() {
		
	} // Constructor
	
	// this function will return RTM after analysis
	public  static ReportTableModel analyseWord(ReportTableModel rtm, int inputIndex, String splitval) {
		int rowc = rtm.getModel().getRowCount();
		if (rowc == 0) return rtm;
		
		Hashtable<String,Integer> wordCH = new Hashtable<String,Integer>();
		Hashtable<String,List<Integer>> wordRowH = new Hashtable<String,List<Integer>> ();
		
		for (int i=0; i < rowc; i++) {
			
			Object o = rtm.getModel().getValueAt(i, inputIndex);
			if (o == null) continue;
			String os = o.toString().trim();
			if ("".equals(os) ) continue;
			
			String[] spl = os.split(splitval);
			for (String s:spl) {
				s = s.trim(); // making search case-senstive
				Integer c = wordCH.get(s);
				if (c == null) { // it is not there
					wordCH.put(s, 1);
					List<Integer> rowid = new ArrayList<Integer> ();
					rowid.add(i);
					wordRowH.put(s, rowid);
				} else { // already atleast one entry is there
					wordCH.put(s, ++c);
					List<Integer> rowid = wordRowH.get(s);
					rowid.add(i); // duplicate rows are allowed
				}
			}
			
		}
		
		// Now create the reportTableModel
		String[] colName = new String[]{"Word","Term Frequecy","Unique Rows Appeared","Rowid-Frequency"};
		ReportTableModel newRTM = new ReportTableModel(colName,true,true);
		
		for (String w: wordCH.keySet()) {
			List<Integer> rowid = wordRowH.get(w);
			// Now do Analysis for Number
			StatisticalAnalysis sa = doanalysisofNumber(rowid);
			Object[] obj = new Object[4];
			obj[0] = w; obj[1] = wordCH.get(w); obj[2] = sa.getUniqCount();
			
			ReportTableModel freq = sa.getFrequencyTable();
			int freqc = freq.getModel().getRowCount();
			
			String rowidFreq= "";
			for (int i=0; i < freqc; i++) {
				Object[] s = freq.getRow(i);
				rowidFreq += s[0]+"-"+s[1]+":";//avoid comma
			}
			
			obj[3] = rowidFreq;
			newRTM.addFillRow(obj);
		}
		
		
		 return newRTM;
	}
	
	private static StatisticalAnalysis doanalysisofNumber(List<Integer> list) {
		Object[] colv = list.toArray();
		return new StatisticalAnalysis(colv);
		
	}
	
	// this function will return RTM after word count
		public  static ReportTableModel countWord(ReportTableModel rtm, int inputIndex, String splitval, int outputIndex) {
			int rowc = rtm.getModel().getRowCount();
			if (rowc == 0) return rtm;
			
			Hashtable<String,Integer> wordCH = new Hashtable<String,Integer>();
			
			for (int i=0; i < rowc; i++) {
				wordCH.clear();
				Object o = rtm.getModel().getValueAt(i, inputIndex);
				if (o == null) continue;
				String os = o.toString().trim();
				if ("".equals(os) ) continue;
				
				String[] spl = os.split(splitval);
				for (String s:spl) {
					s = s.trim(); // making search case-senstive
					Integer c = wordCH.get(s);
					if (c == null) { // it is not there
						wordCH.put(s, 1);
					} else { // already atleast one entry is there
						wordCH.put(s, ++c);
					}
				}
				
				String wcformat = "";
				for (String w: wordCH.keySet()) 
					wcformat += w+"-"+wordCH.get(w)+":";//avoid comma
				rtm.setValueAt(wcformat, i, outputIndex);
			}
			
			return rtm;
		}
	
	// this function will return RTM after drop words
		public  static ReportTableModel dropwords(ReportTableModel rtm, int inputIndex, int outputIndex) {
			int rowc = rtm.getModel().getRowCount();
			if (rowc == 0) return rtm;
			
			for (int i=0; i < rowc; i++) {
				Object o = rtm.getModel().getValueAt(i, inputIndex);
				if (o == null) continue;
				String os = o.toString().trim();
				if ("".equals(os) ) continue;
				
				//System.out.println("Before:"+os);
				String wcformat = StopWords.dropStopWordsfromLine(os);
				rtm.setValueAt(wcformat, i, outputIndex);
			}
			
			return rtm;
		}
	

} // End of WordAnalysis
