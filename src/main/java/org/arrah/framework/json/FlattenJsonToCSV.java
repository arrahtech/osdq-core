package org.arrah.framework.json;


/* This files is used for writing flattened Json to CSV
 *
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.arrah.framework.ndtable.ReportTableModel;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FlattenJsonToCSV {

    private  ReportTableModel rtm = null;

    /**
     * Convert the given List of String keys-values as a CSV String.
     *
     * @param flatJson   The List of key-value pairs generated from the JSON String
     *
     * @return The generated CSV string
     */
    public  String getCSV(List<Map<String, String>> flatJson) {
        // Use the default separator
        return getCSV(flatJson, ",");
    }

    /**
     * Convert the given List of String keys-values as a CSV String.
     *
     * @param flatJson   The List of key-value pairs generated from the JSON String
     * @param separator  The separator can be: ',', ';' or '\t'
     *
     * @return The generated CSV string
     */
    public  String getCSV(List<Map<String, String>> flatJson, String separator) {
        Set<String> headers = collectOrderedHeaders(flatJson);
        String csvString = StringUtils.join(headers.toArray(), separator) + "\n";
        
        rtm = new ReportTableModel(headers.toArray(),true,true);

        for (Map<String, String> map : flatJson) {
        	String rowStr = getSeperatedColumns(headers, map, separator);
        	rowStr =rowStr+separator; // add to last
        	rtm.addFillRow(rowStr.split(separator,-1));
            csvString = csvString + rowStr + "\n";
        }

        //System.out.println(csvString);
        return csvString;
    }

    /**
     * Write the given CSV string to the given file.
     *
     * @param csvString  The csv string to write into the file
     * @param fileName   The file to write (included the path)
     */
    public  void writeToFile(String csvString, String fileName) {
        try {
        	java.io.FileWriter fs = new java.io.FileWriter(new File(fileName));
        	fs.write(csvString);
        	fs.flush(); fs.close();
        } catch (IOException e) {
        	System.out.println("IO Exception:"+e.getLocalizedMessage());
        }
    }
    
    /**
     * Write the given CSV from a flat json to the given file.
     * 
     * @param flatJson
     * @param separator
     * @param fileName 
     * @param headers
     */
    public  void writeLargeFile(List<Map<String, String>> flatJson, String separator, String fileName, Set<String> headers){
    	String csvString;
        csvString = StringUtils.join(headers.toArray(), separator) + "\n";
       // File file = new File(fileName);
        
        try {
            // ISO8859_1 char code to Latin alphabet
            // FileUtils.write(file, csvString, "ISO8859_1");
            
            Files.write(Paths.get(fileName), csvString.getBytes("ISO8859_1"), StandardOpenOption.CREATE);
            
            for (Map<String, String> map : flatJson) {
            	csvString = "";
            	csvString = getSeperatedColumns(headers, map, separator) + "\n";
            	Files.write(Paths.get(fileName), csvString.getBytes("ISO8859_1"), StandardOpenOption.APPEND);
            }            
        } catch (IOException e) {
        	System.out.println("IO Exception:"+e.getLocalizedMessage());
        }
    }    

    /**
     * Get separated comlumns used a separator (comma, semi column, tab).
     *
     * @param headers The CSV headers
     * @param map     Map of key-value pairs contains the header and the value
     *
     * @return a string composed of columns separated by a specific separator.
     */
    private  String getSeperatedColumns(Set<String> headers, Map<String, String> map, String separator) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
            String value = map.get(header) == null ? "" : map.get(header).replaceAll("[\\,\\;\\r\\n\\t\\s]+", " "); 
            items.add(value);
        }

        return StringUtils.join(items.toArray(), separator);
    }

    /**
     * Get the CSV header.
     *
     * @param flatJson
     *
     * @return a Set of headers
     */
    public  Set<String> collectHeaders(List<Map<String, String>> flatJson) {
        Set<String> headers = new LinkedHashSet<String>();

        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }

        return headers;
    }

    /**
     * Get the CSV ordered header
     *
     * @param flatJson
     *
     * @return a Set of ordered headers
     */
    public  Set<String> collectOrderedHeaders(List<Map<String, String>> flatJson) {
    	Set<String> headers = new TreeSet<String>();
        for (Map<String, String> map : flatJson) {
        	headers.addAll(map.keySet());
        }
        return headers;
    } 
    
    public  ReportTableModel getRTM(String byteJson) {
    	rtm = null;
    	
    	//JsonReader reader = new JsonReader(new StringReader(byteJson));
    	// reader.setLenient(false);
    	// JsonElement element = jsonParser.parse(reader);
    	
    	
		// Flatten the json
		HashMap<String, String> flattened = new HashMap<String, String>();
		LinkedList<String> keyPath = new LinkedList<String>();
		JsonFlattener jsFlat = new JsonFlattener();
		
		try {
			JsonElement doc = new JsonParser().parse(byteJson);
			//System.out.println(doc.toString());
			
			jsFlat.flatten(doc, keyPath, flattened);
			List<Map<String, String>> flatJson = jsFlat.getFlatJson();
			
//			for (Map<String, String> l : flatJson)
//			for (String s : l.keySet()) 
//				System.out.println(s+" - "+ l.get(s));
			
			getCSV(flatJson);
		} catch (Exception e) {
			System.out.println("Exception:"+e.getLocalizedMessage());
		}
    	return rtm;
    }
}