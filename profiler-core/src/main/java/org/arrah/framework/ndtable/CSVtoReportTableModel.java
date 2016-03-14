package org.arrah.framework.ndtable;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/* This file is used for importing user defined  
 * text file into ReportTableModel 
 *
 */

// For Reading File

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import com.opencsv.CSVReader;

public class CSVtoReportTableModel {
	private int previewRowNumber = 15; // will show 15 lines in preview
	private int displayRowNumber = 100; // will show 15 lines in preview
	private int skipRowNumber = 0; // Skip no rows by default
	private String FIELD_SEP = ","; // Default field separator
	private String COMMENT_STR = "#"; // // Default comment String
	private int fixedcolWidth = 0;
	private File f = null;

	private BufferedReader br;
	private Vector<ColumnAttr> vc;

	private boolean fieldSelection = false;
	private boolean adv_fieldSelection = false;
	private boolean fixedWidthSelection = false;
	private boolean adv_widthSelection = false;
	private boolean commentSelection = false;
	private boolean skipRowSelection = false;
	private boolean previewRowSelection = false;
	private boolean displayRowSelection = false;
	private boolean firstRowColumnName = false;
	private boolean strictParsing = false;

	public CSVtoReportTableModel(File fileToLoad) {
		f = fileToLoad;
		vc = new Vector<ColumnAttr>();
	};

	public ReportTableModel loadFileIntoTable(int is_preview) {
		ReportTableModel showT = null;
		String line = null;
		boolean headerSet = false;
		int lineCount = 0;
		int headerSize = 0;
		int vec_c = (vc == null) ? 0 : vc.size();
		int validLine = 0;
		int totalLine = 0;

		if (previewRowSelection == true) {
			if (previewRowNumber <= 0) // Nothing to preview
				previewRowNumber = 15; // Default Value
		}
		
		if (displayRowSelection == true) {
			if (displayRowNumber <= 0) // Nothing to preview
				displayRowNumber = 100; // Default Value
		}
		
		if (skipRowSelection == true) {
			if (skipRowNumber <= 0) // Nothing to skip
				skipRowSelection = false;
		}
		if (commentSelection == true) {
			if (COMMENT_STR == null || COMMENT_STR.equals(""))
				commentSelection = false;
		}
		if (fieldSelection == true) {
			if (FIELD_SEP == null || FIELD_SEP.equals(""))
				FIELD_SEP = ";";
		}

		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) { // how to read multi-line feed
				totalLine++;
				if (commentSelection == true) {
					if (line.startsWith(COMMENT_STR) == true)
						continue;
				}
				line = line.trim();
				
				if (line.equals("")) {
					System.out.println("Empty Record at:" +totalLine);
					continue;
				}
					
				lineCount++;
				if (skipRowSelection == true) {
					if (lineCount <= skipRowNumber)
						continue;
				}
				if (displayRowSelection == true) {
					if (validLine >= displayRowNumber) // reached the number to display
						break;
				}

				// line = line.replaceAll("\\s+", " "); // Keep original format
				/*
				 * Now all the needed rows has been skipped let parse the right
				 * one and populate table
				 */
				int colI = 0;
				ArrayList<String> columnA = new ArrayList<String>();
				int lineIndex = 0;
				int lineLength = line.length(); // it will work only for single line record
				ColumnAttr cob; // Column Attribute Object
				boolean lastCol = false;
				String[] f_column;

				if (fieldSelection == true) { // Field Selection Chosen
					columnA.clear();
					while (true) {
						if (adv_fieldSelection == true) {
							if (colI < vec_c) {
								FIELD_SEP = ((ColumnAttr) vc.get(colI))
										.getSep();
								if (FIELD_SEP == null || FIELD_SEP.equals(""))
									lastCol = true;
							} else
								lastCol = true;
						}
						try {
							if (lastCol == false)
								f_column = line.split(FIELD_SEP, 2);
							else
								f_column = new String[] { line };
						} catch (PatternSyntaxException pe) {
							JOptionPane.showMessageDialog(null,
									pe.getMessage(), "Regex Error",
									JOptionPane.ERROR_MESSAGE);
							return null;
						}
						columnA.add(colI, f_column[0]);
						colI++;
						
						if (f_column.length == 1)
							break;
						if (f_column[1] == null || f_column[1].equals(""))
							break;
						line = f_column[1];
					} // End of While loop

				} else { // Width Separator
					columnA.clear();
					int colWidth = 0;
					if (fixedWidthSelection == true)
						colWidth = fixedcolWidth;
					if (colWidth <= 0)
						colWidth = lineLength; // default value of Width
					while (lineIndex < lineLength) {
						if (adv_widthSelection == true) {
							if (colI < vec_c) {
								colWidth = ((ColumnAttr) vc.get(colI))
										.getWidth();
								if (colWidth <= 0)
									colWidth = lineLength - lineIndex;
							} else {
								colWidth = lineLength - lineIndex;
							}
						}
						String column;
						if ((lineIndex + colWidth) < lineLength)
							column = line.substring(lineIndex, lineIndex
									+ colWidth);
						else
							column = line.substring(lineIndex, lineLength);

						lineIndex += colWidth;
						columnA.add(colI, column);
						colI++;
					}
				} // End of Width separator

				if (headerSet == false) {
					if (firstRowColumnName == true) {
						headerSize = columnA.size();
						if (is_preview == 0) // it is preview
							showT = new ReportTableModel(columnA.toArray());
						else
							showT = new ReportTableModel(columnA.toArray(),
									true, true);
						headerSet = true;
						continue;
					} else {
						ArrayList<String> headerA = new ArrayList<String>();
						for (int i = 0; i < vec_c; i++) {
							cob = (ColumnAttr) vc.get(i);
							String headerN = cob.getName();
							if (headerN == null || headerN.equals(""))
								headerN = "Column " + (i + 1);
							headerA.add(i, headerN);
						}
						if (is_preview == 0)
							showT = new ReportTableModel(headerA.toArray());
						else
							showT = new ReportTableModel(headerA.toArray(),
									true, true);
						headerSize = vec_c;
						headerSet = true;
					}
				} // End of Header Selection

				// Here goes the logic for Lenient and Strict parsing
				// If lenient - try to adjust the columns into table
				if (strictParsing == true)
					if (colI != headerSize) {
						System.out.println("\n Skipped:" + columnA);
						continue; // return for strict match
					}
				if (colI == headerSize) {
					showT.addFillRow(columnA.toArray());
				} else if (colI < headerSize) {
					while (colI < headerSize) {
						columnA.add(colI++, null);
					}
					showT.addFillRow(columnA.toArray());
				} else {
					while (colI > headerSize) {
						columnA.remove(--colI);
					}
					showT.addFillRow(columnA.toArray());
				}
				validLine++;
				if (is_preview == 0 && validLine == previewRowNumber)
					break;
			}
			System.out.println("\n" + validLine + " of Total " + totalLine
					+ " Parsed SuccessFully.");
			br.close();
		} catch (IOException ie) {
			System.out.println("\n IO Error:" + ie.getMessage());
			return null;

		}
		return showT;
	}

	public void setFieldSelection(boolean fieldSelection, String field_sep) {
		this.fieldSelection = fieldSelection;
		FIELD_SEP = field_sep;
	}

	public boolean isFieldSelection() {
		return fieldSelection;
	}

	public void setCommentSelection(boolean commentSelection, String comment_str) {
		this.commentSelection = commentSelection;
		COMMENT_STR = comment_str;
	}

	public boolean isCommentSelection() {
		return commentSelection;
	}

	public void setSkipRowSelection(boolean skipRowSelection, int skipRows) {
		this.skipRowSelection = skipRowSelection;
		skipRowNumber = skipRows;
	}

	public boolean isSkipRowSelection() {
		return skipRowSelection;
	}

	public void setPreviewRowSelection(boolean previewRowSelection,
			int previewRows) {
		this.previewRowSelection = previewRowSelection;
		previewRowNumber = previewRows;
	}
	
	public boolean isPreviewRowSelection() {
		return previewRowSelection;
	}
	
	public void setDisplayRowSelection(boolean displayRowSelection,
			int displayRows) {
		this.displayRowSelection = displayRowSelection;
		displayRowNumber = displayRows;
	}

	public boolean isDisplayRowSelection() {
		return displayRowSelection;
	}
	
	public void setFirstRowColumnName(boolean firstRowColumnName,
			Vector<ColumnAttr> column_v) {
		this.firstRowColumnName = firstRowColumnName;
		vc = column_v;
	}

	public boolean isFirstRowColumnName() {
		return firstRowColumnName;
	}

	public void setStrictParsing(boolean strictParsing) {
		this.strictParsing = strictParsing;
	}

	public boolean isStrictParsing() {
		return strictParsing;
	}

	public void setAdv_fieldSelection(boolean adv_fieldSelection,
			Vector<ColumnAttr> column_v) {
		this.adv_fieldSelection = adv_fieldSelection;
		vc = column_v;
	}

	public boolean isAdv_fieldSelection() {
		return adv_fieldSelection;
	}

	public void setAdv_widthSelection(boolean adv_widthSelection,
			Vector<ColumnAttr> column_v) {
		this.adv_widthSelection = adv_widthSelection;
		vc = column_v;
	}

	public boolean isAdv_widthSelection() {
		return adv_widthSelection;
	}

	public void setfixedWidthSelection(boolean fixedwidthSelection,
			int fixedWidth) {
		this.fixedWidthSelection = fixedwidthSelection;
		fixedcolWidth = fixedWidth;
	}

	public boolean isfixedWidthSelection() {
		return fixedWidthSelection;
	}
	
	// Open CSV format to create ReportTableModel
	public ReportTableModel loadOpenCSVIntoTable() {
		ReportTableModel showT = null;
		try {
			CSVReader reader = new CSVReader(new FileReader(f));
			String [] nextLine = null;
			int colL=0;
			boolean headerset = false;
			while ((nextLine = reader.readNext()) != null) {
				if (headerset == false) { // 1st line is header
					showT = new ReportTableModel(nextLine, true, true);
					headerset = true;
					colL = nextLine.length;
					continue;
				}
				if (colL == nextLine.length)
					showT.addFillRow(nextLine);
				else
					System.out.println("No of Column not matching:" + nextLine);
			}
			reader.close();
		} catch (IOException ie) {
			System.out.println("\n IO Error:" + ie.getMessage());
			return null;

		}
			
		return showT;
		
	}
}
