package org.arrah.framework.dataquality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;


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
	

	public HashMap<String, String> validate(String id) {

		HashMap<String, String> responseMap = new HashMap<>();
		responseMap.put("isValid", "false");
		if (id != null && !id.isEmpty()) {
			id = id.toUpperCase();
			Matcher matcher = pattern.matcher(id);
			if (matcher.matches()) {
				//TODO: if entity name is known, we can also validate 5th char
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

	private String getEntityType(String id) {
		return Enum.valueOf(EntityType.class, id.substring(3, 4)).name();
	}

	private String getEntityDesc(String id) {
		return Enum.valueOf(EntityType.class, id.substring(3, 4)).toString();
	}

}
