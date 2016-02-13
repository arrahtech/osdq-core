package org.arrah.framework.rdbms;

/***********************************************
 *     Copyright to Vivek Kumar Singh          *
 *                                             *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This class defines the Entity-Relation of Table that is 
 * used for displaying inside RelationPane
 * 
 */

public class TableRelationInfo {
	private static int DEFAULT_SIZE = 128;

	public String tableName;
	public int tableId;
	public String[] pk = new String[DEFAULT_SIZE];
	public String[] pk_index = new String[DEFAULT_SIZE];
	public String[] pk_ex = new String[DEFAULT_SIZE];
	public String[] pk_exTable = new String[DEFAULT_SIZE];
	public String[] pk_exKey = new String[DEFAULT_SIZE];
	public String[] fk = new String[DEFAULT_SIZE];
	public String[] fk_pKey = new String[DEFAULT_SIZE];
	public String[] fk_pTable = new String[DEFAULT_SIZE];
	public boolean isShown = false;
	public boolean isRelated = false;
	public boolean hasPKey = false;
	public boolean hasFKey = false;
	public boolean hasExpKey = false;
	public int fk_c = 0;
	public int pk_c = 0;
	public int exp_c = 0;
	public int r_i = -1;
	public int level = 0; // 0 for rootLeval , 1 for children level one

	public TableRelationInfo() {
	};

	public TableRelationInfo(String t_name) {
		tableName = t_name;
	};

	public void print_table() {
		System.out.println("\n ___ " + tableName + " ____");

		for (int i = 0; i < pk_c; i++)
			System.out.println("\n PK --" + pk[i]);

		if (isRelated == false)
			System.out.println("\n Table NOT Related");

		for (int i = 0; i < exp_c; i++)
			System.out.println("\n Exported PK -- " + "\"" + pk_ex[i] + "\" \""
					+ pk_exTable[i] + "\"  \" " + pk_exKey[i] + "\"");
		for (int i = 0; i < fk_c; i++)
			System.out.println("\n FK --" + "\"" + fk[i] + "\" \"" + fk_pKey[i]
					+ "\" \"" + fk_pTable[i] + "\"");
	}

}
