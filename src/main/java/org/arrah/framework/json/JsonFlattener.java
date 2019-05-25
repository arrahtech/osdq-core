package org.arrah.framework.json;


/* This files is used for reading,writing json files
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;



public class JsonFlattener {

	List<Map<String, String>> flatJson = new ArrayList<Map<String, String>>();
	
	
    public  List<Map<String, String>> getFlatJson() {
		return flatJson;
	}

	public  Map<String, String> flatten(JsonObject doc) {
        HashMap<String, String> fields = new HashMap<String, String>();
        LinkedList<String> keyPath = new LinkedList<String>();
        flatten(doc, keyPath, fields);
        return fields;
    }

    public  void flatten(JsonElement doc, LinkedList<String> keyPath, HashMap<String, String> result) {
    	 
    	if (doc == null || doc.isJsonNull()) {
            if (!keyPath.isEmpty()) {
                result.put(getKeyFor(keyPath), "");
            }
        } else if (doc.isJsonObject()) {
            for (Entry<String, JsonElement> e : doc.getAsJsonObject().entrySet()) {
                keyPath.add(e.getKey());
                flatten(e.getValue(), keyPath, result);
                keyPath.removeLast();
            }
            if(keyPath.isEmpty())
            {
            flatJson.add(result);
     	   result=new HashMap<String, String>();
            }
        } else if (doc.isJsonArray()) {
            int idx = 0;
            String lastKey = keyPath.isEmpty() ? "array" : keyPath.getLast();
            for (JsonElement e : doc.getAsJsonArray()) {
                if (!keyPath.isEmpty()) {
                    keyPath.removeLast();
                }
                if(!lastKey.equals("array"))
                {   	
                keyPath.add(lastKey + "[" + idx + "]");
            }else{
            	keyPath.add(lastKey);
            }
                flatten(e, keyPath, result);
                ++idx;
               if(lastKey.equals("array"))
               {
            	   flatJson.add(result);
            	   result=new HashMap<String, String>();
               }
            }
        } else {
            result.put(getKeyFor(keyPath), doc.getAsString());
        }
		
    }

    private  String getKeyFor(LinkedList<String> keyPath) {
        StringBuilder sb = new StringBuilder();
        for (String s : keyPath) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(s);
        }
        return sb.toString();
    }

}