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
 * This file is used to Similarity Check
 * for Lucene Index directory or RAM
 *
 */


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
// import org.apache.lucene.search.Hits; Deprecated
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.arrah.framework.ndtable.ReportTableModel;
import org.arrah.framework.rdbms.JDBCRowset;
import org.arrah.framework.rdbms.Rdbms_conn;


public class SimilarityCheckLucene {
    private ReportTableModel _rt, outputRT;
    private String[] colName;
    private String[] colType;
    private int rowC;
    private IndexWriter writer;
    private IndexSearcher searcher;
    //private RAMDirectory idx; // now it will be generic
    private BaseDirectory idx;
    private Vector<Integer> skipVC = null,mrowI = null;
    private IndexReader reader;

    private Hashtable<Integer, Integer> parentMap;
    boolean isRowSet = false;
    private JDBCRowset _rows;

    // For ReportTable Input
    public SimilarityCheckLucene(ReportTableModel rt) {
        _rt = rt;
        colName = getColName();
        rowC = _rt.getModel().getRowCount();
    }

    // For Rowset Table Input
    public SimilarityCheckLucene(JDBCRowset rowSet) {
        isRowSet = true;
        _rows = rowSet;
        colName = _rows.getColName();
        colType = _rows.getColType();
        rowC = _rows.getRowCount();
    }

    private String[] getColName() {
        int colC = _rt.getModel().getColumnCount();
        colName = new String[colC];
        colType = new String[colC];

        for (int i = 0; i < colC; i++) {
            colName[i] = _rt.getModel().getColumnName(i);
            colType[i] = _rt.getModel().getColumnClass(i).getName();
        }
        return colName;
    }

    private Document createDocument(int rowId, Object[] row) {
        Document doc = new Document();
        if (row == null)
            return doc;
        try {
        	
            //FieldType crosscol = new FieldType();
            //crosscol.setStored(true);
            //crosscol.setTokenized(false);

            //doc.add(new Field("at__rowid__", Integer.toString(rowId),
            //Field.Store.YES, Field.Index.UN_TOKENIZED)); // for cross column search
            doc.add(new StringField("at__rowid__", Integer.toString(rowId),Field.Store.YES) );
            // crosscol));

            //FieldType forhive = new FieldType();
            //forhive.setStored(true);
            //forhive.setTokenized(true);

            for (int i = 0; i < row.length; i++) {
                if (row[i] != null && colName[i] != null)
                    //doc.add(new Field(colName[i], row[i].toString(),
                    // Field.Store.NO, Field.Index.TOKENIZED)); we have to do hive query
                    // Field.Store.YES, Field.Index.TOKENIZED));
                	if(row[i] instanceof java.lang.Number ) {
                		doc.add(new DoubleField(colName[i], new Double(row[i].toString()),Field.Store.YES));
                	}
                	else if (row[i] instanceof java.util.Date)
                		doc.add(new TextField(colName[i], DateTools.dateToString((Date) row[i],DateTools.Resolution.SECOND),Field.Store.YES));
                	else
                		doc.add(new TextField(colName[i], row[i].toString(),Field.Store.YES));
                //forhive));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n " + e.getMessage());
            System.out.println("\n Error: Document Creation Error");
        }
        return doc;
    }

    public void makeIndex() {
        if (createIndex() == false)
            return;
        addDocument();
        closeIndex();
    }
    
    // This function will create lucene index on file with indexname
    public void makeIndex(String indexname) {
        if (createIndex(indexname) == false)
            return;
        addDocument();
        closeIndex();
    }

 // This function will create lucene index on file with indexname or return true if it already exists
    private boolean createIndex(String indexname ) {
        Path indexPath = Paths.get("./luceneIndex/"+indexname+"/");
        try {
        	idx =  FSDirectory.open(indexPath);
        	if ( idx.listAll().length > 0) {
        		System.out.println("Index:"+indexPath+" exists.");
        		return false;
        	}
            IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
            conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(idx,conf);
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n ERROR: Index Open Exception");
            return false;
        }
        return true;

    }

    private boolean createIndex() {
        // Construct a RAMDirectory to hold the in-memory representation
        // of the index.
        idx = new RAMDirectory();
        try {
            // writer = new IndexWriter(idx, new StandardAnalyzer(), true);
            IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
            conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(idx,conf);
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n ERROR: Index Open Exception");
            return false;
        }
        return true;

    }

    public void closeIndex() {
        try {
            // writer.optimize(); deprecated
            writer.commit();
            writer.close();
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n ERROR: Index Close Exception");
        }
    }

    /* Since it is in sequence it will work for hive */
    public void addDocument() {
        // Create multiple threads(10) if rowId > 100
        try {
            if (rowC <= 100 || isRowSet == true) { // Rowset has synch problem so no thread
                for (int i = 0; i < rowC; i++)
                    if (isRowSet == false)
                        writer.addDocument(createDocument(i, _rt.getRow(i)));
                    else
                        writer.addDocument(createDocument(i + 1,_rows.getRow(i + 1)));
            } else {
                final int THREADCOUNT = 10;
                Thread[] tid = new Thread[THREADCOUNT];
                final int rowthread = rowC / THREADCOUNT;
                for (int i = 0; i < THREADCOUNT; i++) {
                    final int tindex = i;
                    tid[tindex] = new Thread(new Runnable() {
                        public void run() {
                            if (tindex < THREADCOUNT - 1)
                                for (int j = tindex * rowthread; j < tindex
                                        * rowthread + rowthread; j++)
                                    try {
                                        writer.addDocument(createDocument(j,_rt.getRow(j)));
                                    } catch (Exception e) {
                                        System.out.println(" Lucene Exeception:"+e.getMessage());
                                    }
                            else
                                for (int j = tindex * rowthread; j < rowC; j++)
                                    try {
                                        writer.addDocument(createDocument(j,_rt.getRow(j)));
                                    } catch (Exception e) {
                                        System.out.println("Lucene Exeception:"+e.getMessage());
                                    }
                        }
                    });
                    tid[i].start();
                }
                for (int i = 0; i < THREADCOUNT; i++)
                    try {
                        tid[i].join();
                    } catch (Exception e) {
                        System.out.println(" Thread Exeception:"+e.getMessage());
                    }
            }
        } catch (Exception e) {
            System.out.println(" Add Document Exeception:"+e.getMessage());
        }
    }

	/* This function is added to search the query string in the report table
	 * For now no one uses this function. It is a place holder.
	 */

    public void searchTableIndex(String query) {
        if (openIndex() == false)
            return;
        // Add a boolean column for delete
        String[] newColN = new String[colName.length + 1];
        newColN[0] = "Delete Editable"; // This column you can change
        for (int i = 0; i < colName.length; i++)
            newColN[i + 1] = colName[i];

        outputRT = new ReportTableModel(newColN, false, true);
        skipVC = new Vector<Integer>();
        parentMap = new Hashtable<Integer, Integer>();

        // Search in complete Index

        String queryString = query;
        if (queryString == null || queryString.equals("") == true)
            return;

        Query qry = parseQuery(queryString);
        Hits hit = searchIndex(qry);
        if (hit == null || hit.length() <= 1)
            return; // It will match self
        // Iterate over the Documents in the Hits object

        for (int j = 0; j < hit.length(); j++) {
            try {
                Document doc = hit.doc(j);
                String rowid = doc.get("at__rowid__");
                parentMap.put(outputRT.getModel().getRowCount(),
                        Integer.parseInt(rowid));
                Object[] row = null;
                if (isRowSet == false)
                    row = _rt.getRow(Integer.parseInt(rowid));
                else {

                    if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
                        row = _rows.getRow(Integer.parseInt(rowid));
                    } else {
                        // will not work for Hive as rowset can not move bothway
                        // Hive the info should be taken from document itself by colname
                        row = new Object[colName.length];
                        for (int k =0; k < colName.length; k++)
                            row[k] = doc.get(colName[k]);
                    }
                }
                Object[] newRow = new Object[row.length + 1];
                boolean del = false;
                newRow[0] = del;
                for (int k = 0; k < row.length; k++)
                    newRow[k + 1] = row[k];
                outputRT.addFillRow(newRow);
                skipVC.add(Integer.parseInt(rowid));
            } catch (Exception e) {
                System.out.println("\n " + e.getMessage());
                System.out.println("\n Error: Can not open Document");
            }
        }
        outputRT.addNullRow();

        closeSeachIndex();

    }

    public boolean openIndex() {
        try {
            reader = DirectoryReader.open(idx);
            searcher = new IndexSearcher(reader);
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n Error: Can not open Index Searcher");
            return false;
        }
        return true;
    }

    // This class is overwritten for range query. Now for numerical data type it should do number range
    public Query parseQuery(String query) {
        try {
        	
            QueryParser qp = new QueryParser(colName[0], new StandardAnalyzer()) {
            	public Query parse(String query) throws ParseException {
            		return super.parse(query);
            		
            	}
            	
            	protected Query getRangeQuery(String field,
                        String part1,
                        String part2,
                        boolean startInclusive,
                        boolean endInclusive)
                             throws ParseException {
            		try {
            			if ( reader.document(1).getField(field).numericValue() != null ) {
            				return NumericRangeQuery.newDoubleRange(field, new Double(part1), new Double(part2), startInclusive, endInclusive);
            			} else
            				return super.getRangeQuery(field,part1,part2,startInclusive,endInclusive);
            		} catch (Exception e) {
            			return super.getRangeQuery(field,part1,part2,startInclusive,endInclusive);
            		}
            		
            	}
            };
            qp.setAllowLeadingWildcard(true);
            return qp.parse(query);
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n Error: Can not Parse Query");
            return null;
        }
    }

    public Hits searchIndex(Query query) {
        if (query == null)
            return null;
        Hits hit = null;
        try {
            // hit = searcher.search(query);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            hit = new Hits(topDocs);
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n Error: Can not Search  Index");
            return null;
        }
        return hit;
    }

    public void closeSeachIndex() {
        try {
            // searcher.close(); deprecated
            reader.close();
        } catch (Exception e) {
            System.out.println("\n " + e.getMessage());
            System.out.println("\n Error: Can not Close Search  Index");
        }
    }
    public void setRowset(JDBCRowset rowset) {
        _rows= rowset;
    }

    // Prepare query for fuzzy search with colname
    public String prepareLQuery(String rawquery, String colTitle) {
        rawquery = rawquery.replaceAll(",", " ");
        rawquery = rawquery.trim().replaceAll("_", " "); // StandardAnalyzer treats _ as new word
        rawquery = rawquery.replaceAll("\\s+", " ");
        String[] token = rawquery.split(" ");
        String newTerm = "";

        // prepare query for colName
        for (int i = 0; i < token.length; i++) {
            if (token[i] == null || "".equals(token[i]))
                continue;

            if (newTerm.equals("") == false )
                newTerm += " AND ";

            newTerm += colTitle + ":"
                    // + QueryParser.escape(token[i]) + "~0.6 "; // For Fuzzy Logic
                    + QueryParser.escape(token[i]) + "~3 "; // For Fuzzy Logic
        }
        return newTerm;
    }

    // This function will an array of objects that matched the query
    public Object[][]  searchTableObject(String fuzzyQ) {
        if (openIndex() == false)
            return null;
        if (fuzzyQ == null || fuzzyQ.equals("") == true)
            return null;

        /*** Not needed

         if (isRowSet == true) { // Only if this is rowset
         if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") == 0 ) {
         // for Hive
         if (_rows != null) _rows.close();
         try {
         createNewRowset(queryForRowset);
         } catch (SQLException e) {
         System.out.println("New Rowset Exception:"+e.getLocalizedMessage());
         return;
         }
         }
         }
         ***/

        Query qry = parseQuery(fuzzyQ);
        Hits hit = searchIndex(qry);
        if (hit == null )
            return null;
        int hitc = hit.length();
        Object[][] returnObj = new Object[hitc][];
        mrowI = new Vector<Integer>();

        for (int j = 0; j < hitc; j++) {
            try {
                Document doc = hit.doc(j);
                String rowid = doc.get("at__rowid__");

                Object[] row = null;
                if (isRowSet == false)
                    row = _rt.getRow(Integer.parseInt(rowid));
                else {
                    if (Rdbms_conn.getHValue("Database_Type").compareToIgnoreCase("hive") != 0 ) {
                        row = _rows.getRow(Integer.parseInt(rowid));
                    } else {
                        // will not work for Hive as rowset can not move bothways
                        // Hive the info should be taken from document itself by colname
                        row = new Object[colName.length];
                        for (int k =0; k < colName.length; k++)
                            row[k] = doc.get(colName[k]);
                    }
                }
                returnObj[j] = row;
                mrowI.add(Integer.parseInt(rowid));

            } catch (Exception e) {
                System.out.println(" Error: Can not open Document:" + e.getLocalizedMessage());
                return null;
            }
        }
        closeSeachIndex();
        return returnObj;
    }

    /** to get the row ID of matched columnms
     // It will only return last set of matched columns
     // If searchTableObject is called in loop
     * then it is calling function responsibility to
     * do sanity check
     */
    public Vector<Integer> getMatchedRowIndex() {
        return mrowI;
    }

    // A public class to replace Hits that is depreaceted

    public class Hits {
        int length =0;
        ScoreDoc[] dochits = null;

        public Hits (TopDocs td) {
            if (td != null) {
                length = td.totalHits;
                dochits = td.scoreDocs;
            }
        }
        public int length() {
            return length;
        }
        public Document doc(int i) throws IOException {
            if (dochits == null || length < i)
                return null;
            int docId = dochits[i].doc;
            return searcher.doc(docId);

        }
    }


} // End of Similarity Check