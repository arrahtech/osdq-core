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
 * This file is used for NLP stop words
 * as part of NLP excercise.
 */
package org.arrah.framework.nlp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;


public class StopWords {
    private Hashtable<String, Boolean> h;
    private static StopWords instance;
    private List<String> skip2letter = new ArrayList<String> ();
 	
    public StopWords()
    {
        h = new Hashtable<String, Boolean>();
        h.put("0", true);
        h.put("1", true);
        h.put("2", true);
        h.put("3", true);
        h.put("4", true);
        h.put("5", true);
        h.put("6", true);
        h.put("7", true);
        h.put("8", true);
        h.put("9", true);
        
        h.put("0.", true);
        h.put("1.", true);
        h.put("2.", true);
        h.put("3.", true);
        h.put("4.", true);
        h.put("5.", true);
        h.put("6.", true);
        h.put("7.", true);
        h.put("8.", true);
        h.put("9.", true);
        
        h.put("zero", true);
        h.put("one", true);
        h.put("two", true);
        h.put("three", true);
        h.put("four", true);
        h.put("five", true);
        h.put("six", true);
        h.put("seven", true);
        h.put("eight", true);
        h.put("nine", true);
        h.put("ten", true);
        
        h.put("i", true);
        h.put("ii", true);
        h.put("iii", true);
        h.put("iv", true);
        h.put("i.", true);
        h.put("ii.", true);
        h.put("iii.", true);
        h.put("iv.", true);
        

        h.put("a", true);
        h.put("about", true);
        h.put("above", true);
        h.put("after", true);
        h.put("again", true);
        h.put("against", true);
        h.put("all", true);
        h.put("am", true);
        h.put("an", true);
        h.put("and", true);
        h.put("any", true);
        h.put("are", true);
        h.put("aren't", true);
        h.put("as", true);
        h.put("at", true);
        h.put("be", true);
        h.put("because", true);
        h.put("been", true);
        h.put("before", true);
        h.put("being", true);
        h.put("below", true);
        h.put("between", true);
        h.put("both", true);
        h.put("but", true);
        h.put("by", true);
        h.put("can't", true);
        h.put("can", true);
        h.put("cannot", true);
        h.put("could", true);
        h.put("couldn't", true);
        h.put("did", true);
        h.put("didn't", true);
        h.put("do", true);
        h.put("does", true);
        h.put("doesn't", true);
        h.put("doing", true);
        h.put("don't", true);
        h.put("down", true);
        h.put("during", true);
        h.put("each", true);
        h.put("few", true);
        h.put("for", true);
        h.put("from", true);
        h.put("further", true);
        h.put("had", true);
        h.put("hadn't", true);
        h.put("has", true);
        h.put("hasn't", true);
        h.put("have", true);
        h.put("haven't", true);
        h.put("having", true);
        h.put("he", true);
        h.put("he'd", true);
        h.put("he'll", true);
        h.put("he's", true);
        h.put("her", true);
        h.put("here", true);
        h.put("here's", true);
        h.put("hers", true);
        h.put("herself", true);
        h.put("him", true);
        h.put("himself", true);
        h.put("his", true);
        h.put("how", true);
        h.put("how's", true);
        h.put("i", true);
        h.put("i'd", true);
        h.put("i'll", true);
        h.put("i'm", true);
        h.put("i've", true);
        h.put("if", true);
        h.put("in", true);
        h.put("into", true);
        h.put("is", true);
        h.put("isn't", true);
        h.put("it", true);
        h.put("it's", true);
        h.put("its", true);
        h.put("itself", true);
        h.put("let's", true);
        h.put("me", true);
        h.put("more", true);
        h.put("most", true);
        h.put("mustn't", true);
        h.put("my", true);
        h.put("myself", true);
        h.put("no", true);
        h.put("nor", true);
        h.put("not", true);
        h.put("of", true);
        h.put("off", true);
        h.put("on", true);
        h.put("once", true);
        h.put("only", true);
        h.put("or", true);
        h.put("other", true);
        h.put("ought", true);
        h.put("our", true);
        h.put("ours ", true);
        h.put(" ourselves", true);
        h.put("out", true);
        h.put("over", true);
        h.put("own", true);
        h.put("same", true);
        h.put("shan't", true);
        h.put("she", true);
        h.put("she'd", true);
        h.put("she'll", true);
        h.put("she's", true);
        h.put("should", true);
        h.put("shouldn't", true);
        h.put("so", true);
        h.put("some", true);
        h.put("say", true);
        h.put("said", true);
        h.put("such", true);
        h.put("than", true);
        h.put("that", true);
        h.put("that's", true);
        h.put("the", true);
        h.put("their", true);
        h.put("theirs", true);
        h.put("them", true);
        h.put("themselves", true);
        h.put("then", true);
        h.put("there", true);
        h.put("there's", true);
        h.put("these", true);
        h.put("they", true);
        h.put("they'd", true);
        h.put("they'll", true);
        h.put("they're", true);
        h.put("they've", true);
        h.put("this", true);
        h.put("those", true);
        h.put("through", true);
        h.put("to", true);
        h.put("too", true);
        h.put("under", true);
        h.put("until", true);
        h.put("up", true);
        h.put("very", true);
        h.put("was", true);
        h.put("wasn't", true);
        h.put("we", true);
        h.put("we'd", true);
        h.put("we'll", true);
        h.put("we're", true);
        h.put("we've", true);
        h.put("were", true);
        h.put("weren't", true);
        h.put("what", true);
        h.put("what's", true);
        h.put("when", true);
        h.put("when's", true);
        h.put("where", true);
        h.put("where's", true);
        h.put("which", true);
        h.put("while", true);
        h.put("who", true);
        h.put("who's", true);
        h.put("whom", true);
        h.put("why", true);
        h.put("why's", true);
        h.put("with", true);
        h.put("within", true);
        h.put("won't", true);
        h.put("would", true);
        h.put("wouldn't", true);
        h.put("you", true);
        h.put("you'd", true);
        h.put("you'll", true);
        h.put("you're", true);
        h.put("you've", true);
        h.put("your", true);
        h.put("yours", true);
        h.put("yourself", true);
        h.put("yourselves ", true);
        h.put("yourselves", true);
        h.put("and/or", true);
        h.put("his/her", true);
        h.put("will", true);
        h.put("may", true);
        
        
        
        //skip2letter.add("qa");skip2letter.add("vp");skip2letter.add("it");skip2letter.add("ui");

    }
    
    public static Hashtable<String, Boolean> businessWords() {
    	Hashtable<String, Boolean> bwH = new Hashtable<String, Boolean>();
        // Business words should move to Next functions
    	
    	/** Not companies
    	bwH.put("macy\'s", true);
    	bwH.put("macy's", true);
    	bwH.put("macy", true);
    	bwH.put("macyâs", true);
    	bwH.put("bj's", true);
    	bwH.put("netsuite", true);
    	bwH.put("cbs", true);
    	**/
    	bwH.put("360", true);
    	bwH.put("cdl", true); // may be imp

 

    	return bwH;
    }

    public static Hashtable<String, String> replaceWords() {
    	Hashtable<String, String> bwH = new Hashtable<String, String>();
        // Business words should move to Next functions
    	
    	bwH.put("mgr", "manager");
    	bwH.put("vp", "vicepresident");
    	bwH.put("vp,", "vicepresident");
    	bwH.put("sr", "senior");
    	bwH.put("jr,", "junior");
    	bwH.put("mgr.", "manager");
    	bwH.put("vp.", "vice president");
    	bwH.put("svp.", "senior vice president");
    	bwH.put("sv.", "senior vice president");
    	bwH.put("sr.", "senior");
    	bwH.put("jr.", "junior");
    	bwH.put("sr.,", "senior");
    	bwH.put("jr.,", "junior");
    	bwH.put("rep.", "representative");
    	bwH.put("rep.", "representative");
    	bwH.put("auto,", "automotive");
    	bwH.put("func,", "function");
    	bwH.put("emp,", "employee");
    	bwH.put("ops,", "operation");
    	bwH.put("ft,", "fulltime");
    	bwH.put("asst,", "assistant");
    	bwH.put("asst", "assistant");
    	bwH.put("asst.", "assistant");
    	bwH.put("dir.", "director");
    	bwH.put("dir", "director");
    	bwH.put("vicepresident", "vice president");

    	
    	return bwH;
    	
    }
    
    public boolean isStopWord(String s)
    {
        boolean ret = h.get(s.toLowerCase())==null? false: true;
        if(s.length()==1) ret = true;
        return ret;
    }

    public static StopWords getInstance()
    {
		if(instance == null)
			instance = new StopWords();
		return instance;	
    }
    
    public static List<String> dropStopWords(List<String> withdrop)
    {
    	ArrayList<String> afterstop = new ArrayList<String>();
    	StopWords sw = StopWords.getInstance();
    	
    	for(String content:withdrop){ //for each old line of content in contents
  		  if (content == null || content.isEmpty()) continue;
  		  content = content.trim();
  		  if (content.length() <= 1 ) continue;
  		  
  		content = content.replace("/", " ");
  		content = content.replace(", ", " ");
  		content = content.replace(",", " ");
  		content = content.replace(":", " ");
  		content = content.replace("—", " ");
  		content = content.replace("—", " ");
  		content = content.replace("--", " ");
  		content = content.replace("-", " ");
  		  
  		  String newLine="";
  		  StringTokenizer st = new StringTokenizer(content);
  		  while (st.hasMoreTokens()) {
  			newLine = newLine.trim(); // if last word is match in stop word
  			  String nextword = st.nextToken();
  			  	nextword = nextword.trim();
  			  	nextword = nextword.toLowerCase();
  			  	
  			if (nextword.equals(" ") ||  nextword.equals("\t") || nextword.matches(".*[0-9].*") || nextword.length() < 2)
  				  continue;

  			  char c = nextword.charAt(nextword.length() -1);
  			  while (Character.isLowerCase(c) == false) {
  				nextword = nextword.substring(0,nextword.length() -1);
  				if (nextword.length() > 1)
  					c = nextword.charAt(nextword.length() -1);
  				else 
  					break;
  			  }
  			  if (nextword.length() < 2) continue ; // no single character words
  			
		  if (sw.isStopWord(nextword) == true )
    			  nextword ="";
		  newLine = newLine+" "+nextword;// print the line after dropping stop words
	    }
  		  afterstop.add(newLine.trim());
  	  }
    	return afterstop;
    }
    public static List<String> cleanUpMetaData(String line) {
    	StopWords sw = StopWords.getInstance();
    	
    	String [] strToken = line.split(" ");
    	List<String> clean = new ArrayList<String>();
    	Hashtable<String, Boolean> bwH = businessWords();
    	for (String s: strToken) {
    		s = s.trim();
    		if (s.length() <= 2 && sw.skip2letter.indexOf(s) == -1) continue;
    		char c = s.charAt(s.length() -1);
    		if (Character.isLetterOrDigit(c) == false)
    			s = s.substring(0, s.length() - 1);
    		
    		c = s.charAt(0);
    		if (Character.isLetterOrDigit(c) == false)
    			s = s.substring(1, s.length());
    		
    		s = s.toLowerCase();
    		if (s.length() <= 2 && sw.skip2letter.indexOf(s) == -1) continue;
    		
    		// Drop business words
    		if (bwH.get(s) == null) 
    			clean.add(s);
    	}
    	
    	/***
    	for (String s: clean)
    		System.out.print(s+" ");
    	System.out.println("");
    	System.out.println("----");
    	***/
    	return clean;
    }
    
    public static String dropKeys(String line, List<String> keys) {
    	if (keys == null) return line;
    	// there might be double quote in lines replace with
    	line = line.replaceAll("\"", "");
    	for (String key:keys) {
    		if (key == null || "".equals(key) || key.length() <= 1) continue;
    		line = " "+line.trim()+" ";
    		key = key.toLowerCase().trim();

    		/***
    		// Startwith
    		if (line.startsWith(key+" "))
    			line = line.replaceAll(key+" ","");
    		//ends with
    		if (line.endsWith(" "+key))
    			line = line.replaceAll(" "+key,"");
    		***/
    				
    		// if the word is in between
    		key = " "+key+" ";
    		if (line.contains(key)) {
	    		line = line.replaceAll(key," ");
    		}
    		
    	}
    	return line.trim().replaceAll("\\s+", " ");
    }
    
    public static List<String> dropKeysList(List<String> content, List<String> keys) {
    	if (keys == null) return content;
    	List<String> newcontent = new ArrayList<String> ();
    	
    	for (String keytoserach:content) {
    		if (keytoserach == null || "".equals(keytoserach) || keytoserach.length() <= 1) continue;
    		if (keys.indexOf(keytoserach) == -1 )
    			newcontent.add(keytoserach);
    	}
    	return newcontent;
    }
    
    public static String dropTitles(String line) {
    	String[] tiltlewords = new String[] { "consultant",
    			"managers",
    			"manager",
    			"manager,",
    			"manager.",
    			"manager-",
    			"manager/",
    			"director",
    			"director,",
    			"director.",
    			"director-",
    			"director/",
    			"mgr ",
    			"senior",
    			"junior",
    			"sr ",
    			"sr. ",
    			"vicepresident",
    			"vice president",
    			"vicepresident/",
    			"president",
    			"president/",
    			"vp",
    			"v.p",
    			"vp/",
    			"role",
    			"lead",
    			"graduate"
    	};
    	// there might be double quote in lines replace with
    	line = line.replaceAll("\"", "");
    	for (String key:tiltlewords) {
    		line = line.replaceAll(key," ");
    	}
    	
    	return line.trim();
    	
    }
    
    public static String replaceKeys(String key, Hashtable<String, String> replaceTable) {
    	if (replaceTable == null) return key;
    	if (replaceTable.containsKey(key.toLowerCase()) == false)
    		return key;
    	else
    	return replaceTable.get(key);
    	
    }
    
    public static String replaceKeysFromLine(String line, Hashtable<String, String> replaceTable) {
    	if (replaceTable == null) return line;
    	String newline="";
    	String[] keys = line.split(" ");
    	
    	for (String  key:keys) {
    		key = replaceKeys(key,replaceTable);
    		newline = newline+key+" "; 
    	}
    	return newline.trim();
    	
    }
    public static String dropStopWordsfromLine(String content)
    {
    	StopWords sw = StopWords.getInstance();
    	
		  if (content == null || content.isEmpty()) return "";
		  content = content.trim();
		  if (content.length() <= 1 ) return "";
    		  
			content = content.replace("/", " ");
			content = content.replace(", ", " ");
			content = content.replace(",", " ");
			content = content.replace(":", " ");
			content = content.replace("—", " ");
			content = content.replace("—", " ");
			content = content.replace("--", " ");
			content = content.replace("-", " ");
    		  
    		  String newLine="";
    		  StringTokenizer st = new StringTokenizer(content);
    		  while (st.hasMoreTokens()) {
    			  String nextword = st.nextToken();
    			  	nextword = nextword.trim();
    			  	nextword = nextword.toLowerCase();
    			  	
    			if (nextword.equals(" ") ||  nextword.equals("\t") || nextword.matches(".*[0-9].*") || nextword.length() < 2)
    				  continue;

    			  char c = nextword.charAt(nextword.length() -1);
    			  while (Character.isLowerCase(c) == false) {
    				nextword = nextword.substring(0,nextword.length() -1);
    				if (nextword.length() > 1)
    					c = nextword.charAt(nextword.length() -1);
    				else 
    					break;
    			  }
    			  if (nextword.length() < 2) continue ; // no single character words
    			
  		  if (sw.isStopWord(nextword) == true )
      			  nextword ="";
  		  newLine = newLine+" "+nextword;// print the line after dropping stop words
  		  newLine = newLine.trim(); // if last word is match in stop word
  	    }
    	 return newLine;
    }
    
    
    
    public static void main(String[] argv) {
    	
    	
    } // End of Main
}