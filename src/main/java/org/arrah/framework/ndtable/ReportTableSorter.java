package org.arrah.framework.ndtable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class ReportTableSorter extends TableSorter implements Serializable {
	// In future more type can be added like sorting based on region
	// Different sorting type

	private static final long serialVersionUID = 1L;
	public static final String INTEGER_TYPE = "1";
	public static final String NUMBER_TYPE = "2";
	public static final String NUM_STRING_TYPE = "3";
	public static final String DEFAULT_TYPE = "100";

	private Map<String, String> columnComparators = new HashMap<String, String>();

	public ReportTableSorter(TableModel t) {
		super(t);
		setComparator();
	}

	public ReportTableSorter(TableModel t, JTableHeader h) {
		super(t, h);
		setComparator();
	}

	public void setComparator() {

		columnComparators.put("Frequency", INTEGER_TYPE);
		columnComparators.put("Record Value", NUMBER_TYPE);
		columnComparators.put("% Freq.", NUMBER_TYPE);
		columnComparators.put("Metric Value", NUMBER_TYPE);
		columnComparators.put("Percentile %", INTEGER_TYPE);
		columnComparators.put("Samples Below", INTEGER_TYPE);
		columnComparators.put("Record Upper Value", NUMBER_TYPE);
		columnComparators.put("Size", INTEGER_TYPE);
		columnComparators.put("Precision", INTEGER_TYPE);
		columnComparators.put("Radix", INTEGER_TYPE);
		columnComparators.put("Bytes", INTEGER_TYPE);
		columnComparators.put("Ordinal Pos", INTEGER_TYPE);
		columnComparators.put("Record", INTEGER_TYPE);
		columnComparators.put("Unique", INTEGER_TYPE);
		columnComparators.put("Null", INTEGER_TYPE);
		columnComparators.put("Zero", INTEGER_TYPE);
		columnComparators.put("Empty", INTEGER_TYPE);
		columnComparators.put("Pattern", INTEGER_TYPE);
		columnComparators.put("Cardinality", INTEGER_TYPE);
		columnComparators.put("Pages", INTEGER_TYPE);
		columnComparators.put("Index", INTEGER_TYPE);

		columnComparators.put("Pattern_1", NUM_STRING_TYPE);
		columnComparators.put("Pattern_2", NUM_STRING_TYPE);
		columnComparators.put("Pattern_3", NUM_STRING_TYPE);
		
		columnComparators.put("Count", NUMBER_TYPE);
		columnComparators.put("Percentage", NUMBER_TYPE);

	}

	protected Comparator getComparator(int column) {

		Class column_class = tableModel.getColumnClass(column);
		if (Comparable.class.isAssignableFrom(column_class)) {
			return COMPARABLE_COMAPRATOR;
		}

		String columnType = tableModel.getColumnName(column);
		String comparator = (String) columnComparators.get(columnType);
		if (comparator != null) {
			int t = Integer.parseInt(comparator);
			switch (t) {
			case 1:
				return INTEGER_COMPARATOR;
			case 2:
				return DOUBLE_COMPARATOR;
			case 3:
				return NUM_STRING_COMPARATOR;
			default:
				return LEXICAL_COMPARATOR;

			}
		}
		return LEXICAL_COMPARATOR; // default value
	}

	public static final Comparator INTEGER_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			String o1_s = (String) o1;
			String o2_s = (String) o2;

			if (o1_s.compareTo("") == 0 && o2_s.compareTo("") == 0)
				return 0;
			else if (o1_s.compareTo("N/A") == 0 && o2_s.compareTo("N/A") == 0)
				return 0;
			else if (o1_s.compareTo("") == 0)
				return -1;
			else if (o1_s.compareTo("N/A") == 0)
				return -1;
			else if (o2_s.compareTo("") == 0)
				return 1;
			else if (o2_s.compareTo("N/A") == 0)
				return 1;
			else {
				try {
					return Integer.parseInt(o1_s) - Integer.parseInt(o2_s);
				} catch (NumberFormatException e) {
					System.out
							.println("\n WARNING: Could not parse as Integer - Parsing as String");
					return LEXICAL_COMPARATOR.compare(o1_s, o2_s);
				}
			}
		}
	};
	public static final Comparator DOUBLE_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			String o1_s = (String) o1;
			String o2_s = (String) o2;

			if (o1_s.compareTo("") == 0 && o2_s.compareTo("") == 0)
				return 0;
			else if (o1_s.compareTo("") == 0)
				return -1;
			else if (o2_s.compareTo("") == 0)
				return 1;
			else {
				try {
					double ret = Double.parseDouble(o1_s)
							- Double.parseDouble(o2_s);
					if (ret > 0)
						return 1;
					if (ret < 0)
						return -1;
					return 0;
				} catch (NumberFormatException e) {
					System.out
							.println("\n WARNING:Could not parse as Double - Parsing as String");
					return LEXICAL_COMPARATOR.compare(o1_s, o2_s);
				}
			}
		}
	};
	public static final Comparator NUM_STRING_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			String o1_s = o1.toString();
			String o2_s = o2.toString();

			if (o1_s.compareTo("") == 0 && o2_s.compareTo("") == 0)
				return 0;
			else if (o1_s.compareTo("") == 0)
				return -1;
			else if (o2_s.compareTo("") == 0)
				return 1;

			/*
			 * If String starts with "(num)" then do sorting on number else
			 * treat at Lexical String
			 */
			if (o1_s.startsWith("(") == true && o2_s.startsWith("(") == true) {
				return INTEGER_COMPARATOR.compare(
						o1_s.substring(1, o1_s.indexOf(')')),
						o2_s.substring(1, o2_s.indexOf(')')));
			} else {
				return LEXICAL_COMPARATOR.compare(o1_s, o2_s);
			}

		}
	};

}
