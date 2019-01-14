package org.arrah.framework.dataquality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;


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
 * This file is used to validate PAN fields
 * 
 */



/**
 * Partially implements PAN validation as per 
 * https://www.quora.com/How-is-the-last-character-of-a-PAN-card-number-calculated
 * 
 * @author aruny
 *
 */
public class PANValidator  {
	
	private static Pattern pattern = Pattern.compile("^([A-Z]{3})([ABCFGHLJPT])([A-Z]{1})(\\d{4})([A-Z]{1})$");
	
	private enum EntityType {
		
		A ("Assosiation of Persons"),
		B ("Body of Individuals"),
		C ("Company"),
		F ("Firm"), 
		G ("Government"),
		H ("Hindu Undivided Family"),
		L ("Local Authority"),
		J ("Artificial Juridicial Person"),
		P ("Individual or Proprietor"),
		T ("Trust");
		
		private final String entityDesc;
		
		private EntityType(String entityDesc) {
			this.entityDesc = entityDesc;
		}
		
		@Override
		public String toString() {
			return entityDesc;
		}
	}
	
	// Validate without name
	public HashMap<String, String> validate(String id) {

		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put("isValid", "false");
		if (id != null && !id.isEmpty()) {
			id = id.toUpperCase();
			Matcher matcher = pattern.matcher(id);
			if (matcher.matches()) {
				// if entity name is known, we can also validate 5th char
				// validate (String,String) does that
				responseMap.put("isValid", "true");
				responseMap.put("entityType", getEntityType(id));
				responseMap.put("entityDesc", getEntityDesc(id));
			} else {
				responseMap.put("isValid", "false");
				responseMap.put("entityDesc", "PAN Format not valid");
			}
		} else {
			responseMap.put("entityDesc", "PAN Format is Empty");
		}
		return responseMap;
		
	}
	
	// Validate with name
	public HashMap<String, String> validate(String id, String name) {
		HashMap<String, String> responseMap = validate(id);
		String valid= responseMap.get("isValid");
		
		// if false return if true then check name
		if (valid == null || "".equals(valid) || valid.compareToIgnoreCase("true") != 0 ||
				name == null || "".equals(name) == true)
			return responseMap;
		
		String[] nametok = name.trim().split("\\s+");
		String nameC = getNameChar(id);
		//First name, last name is not accurate so loop thru all
		for (int i=0; i<nametok.length; i++) {
			if ( nameC.compareToIgnoreCase(nametok[i].substring(0, 1)) == 0 ) 
				return responseMap;
		}
		responseMap.put("isValid", "false");
		responseMap.put("entityDesc", "Name Character did not match");
		return responseMap;
	}

	private String getEntityType(String id) {
		return Enum.valueOf(EntityType.class, id.substring(3, 4)).name();
	}

	private String getEntityDesc(String id) {
		return Enum.valueOf(EntityType.class, id.substring(3, 4)).toString();
	}
	
	private String getNameChar(String id) {
		return id.substring(4, 5);
	}

}
