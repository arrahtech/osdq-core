package org.arrah.framework.dataquality;

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

/*
 * This file is used to create entity resolution
 * using lucene indexes.
 * exact, similar, range bound ( number and date)
 *
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.Rdbms_conn;

public class EntityResolutionLucene {
	private ReportTableModel _rt = null;
	
	public static class MappingClass {
		
		private int mappingType = 0; // Exact 0, fuzzy 1 3, range bound 4 startswith 4 endswith 5
		private Object lowerrange = null, upperrange = null;
		private String _indexfield = null, _columnName = null;
		private int startswith =0, endsswith =0;
		public MappingClass (String field, String column) {
			_indexfield = field;
			_columnName = column;
			
		}

		public MappingClass() {
			// default
		}

		public int getMappingType() {
			return mappingType;
		}

		public void setMappingType(int mappingType) {
			this.mappingType = mappingType;
		}

		public Object getLowerrange() {
			return lowerrange;
		}

		public void setLowerrange(Object lowerrange) {
			this.lowerrange = lowerrange;
		}

		public Object getUpperrange() {
			return upperrange;
		}

		public void setUpperrange(Object upperrange) {
			this.upperrange = upperrange;
		}
		
		// This function will return index field name from ReportTable Column Name
		public String getIndexField(String column) {
			if (column.equals(_columnName))
				return _indexfield;
			else return null; // not matching
			
		}
		public String getIndexField() {
				return _indexfield;
		}
		public String getColname() {
			return _columnName;
	}

		public int getStartswith() {
			return startswith;
		}

		public void setStartswith(String startswith) {
			this.startswith = Integer.parseInt(startswith);
		}

		public int getEndsswith() {
			return endsswith;
		}

		public void setEndsswith(String endsswith) {
			this.endsswith = Integer.parseInt(endsswith);
		}
		
	}
	
    // For ReportTable Input
    public EntityResolutionLucene(ReportTableModel rt) {
    	_rt = rt;
    	
    }
    
	/* This function is added to search the query string in the report table
	 * For now no one uses this function. It is a place holder.
	 */

    // Prepare query for entity resolution
    public String prepareLQuery(MappingClass[] mappin, int rowIndex) {
    	String finalQuery = "";
    		
	    	for (MappingClass mc : mappin) {
	    		String colN = mc.getColname();
	    		int colI = _rt.getColumnIndex(colN);
	    		Object ov = _rt.getModel().getValueAt(rowIndex, colI);
	    		if ("".equals(finalQuery) == false)
	    			finalQuery += " AND " +mapQuery(mc,ov);
	    		else
	    			finalQuery += mapQuery(mc,ov);
	    	}
    	//System.out.println("Query:"+finalQuery);
		return finalQuery;

    }
    // prepare qquery for single mapping class
    public String mapQuery(MappingClass mapping, Object obj) {
    	
        String queryString = "";
    	String indexF = mapping.getIndexField();
    	int type = mapping.getMappingType();

        // float fuzzyval = 0.600f; // default fuzzy value
        int fuzzyval = 4; // default fuzzy value float depreacted
        String lv = null; String hv = null;
        if (obj == null) return queryString;

       	String term = obj.toString();

        switch (type) {
        	case 0:	 // not applicable
        		break;
            case 1: // Exact match
                term.trim();
                queryString = indexF + ":\"" + term + "\"";
                break;
            case 2:
            case 3: // It may have multi-words
                term.trim();
                term = term.replaceAll(",", " ");
                term = term.replaceAll("\\s+", " ");
                String[] token = term.split(" ");
                String newTerm = "";
                for (int i = 0; i < token.length; i++) {
                    if (token[i] == null || "".equals(token[i]))
                        continue;

                    if (newTerm.equals("") == false && type == 3)
                        newTerm += " AND ";
                    if (newTerm.equals("") == false && type == 2)
                        newTerm += " OR ";
                    newTerm += indexF + ":"
                            + QueryParser.escape(token[i]) + "~"+fuzzyval+ " "; // For Fuzzy Logic
                }
                queryString = newTerm;
                break;
            case 4:// It may have range Bound query
                lv = mapping.getLowerrange().toString();
                hv = mapping.getUpperrange().toString();
                String ls = boundValue(obj,lv,0);
                String hs = boundValue(obj,hv,0);
                newTerm = indexF + ":[" + ls+ " TO " + hs+ "]";

                queryString = newTerm;
                break;
            case 5: //starts with
            	int l = mapping.getStartswith();
            	newTerm  = term.substring(0, l);
            	queryString = indexF + ":"+ QueryParser.escape(newTerm.trim()) + "*";
            	break;
            case 6: //ends with
            	l = mapping.getEndsswith();
            	newTerm = term.substring(term.length() - l, term.length());
            	queryString = indexF + ":*"+ QueryParser.escape(newTerm.trim()) ;
            	break;
            case 7:// It may have range Bound query
                lv = mapping.getLowerrange().toString();
                hv = mapping.getUpperrange().toString();
                ls = boundValue(obj,lv,1);
                hs = boundValue(obj,hv,1);
                newTerm = indexF + ":[" + ls+ " TO " + hs+ "]";

                queryString = newTerm;
                break;
            default:
                break;

        }
        
        return queryString;
    }
    private String boundValue(Object obj, String val,int type) {
    	if (obj == null) return "";
		if (type == 0) { // Number Type
			try{
				double d = Double.parseDouble(val);
				double exv = Double.parseDouble(obj.toString());
				return new Double(exv+d).toString();
			} catch (Exception e) {
				return obj.toString();
			}
		}
		else if (type == 1) {// Date type
			try {
	        	String format = Rdbms_conn.getHValue("DateFormat");
	        	if (format == null || "".equals(format))
	        		format = "yyyyMMddHHmmss"; // Lucene Default
	        	
        		SimpleDateFormat df = new SimpleDateFormat(format);
        		Date value = df.parse(obj.toString());
				long ext = value.getTime();
				long t = Long.parseLong(val);
				df.applyPattern("yyyyMMddHHmmss"); // Now format for lucene
				return df.format(new java.util.Date(ext+(t*1000))); // Convert to milli second
			} catch (Exception e) {
				return obj.toString();
			}
		}
    	
		return val;
    }

} // End of Entity Resolution Lucene