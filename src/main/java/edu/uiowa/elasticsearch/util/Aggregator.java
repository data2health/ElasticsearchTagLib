package edu.uiowa.elasticsearch.util;

import java.util.Vector;

public class Aggregator {
	String displayName = null;
	Vector<String> fieldNames = new Vector<String>();
	int size = 0;

	public Aggregator(String displayName, String fieldName, int size) {
		this.displayName = displayName;
		this.fieldNames.add(fieldName);
		this.size = size;
	}
	
	public void addFieldName(String fieldName) {
		fieldNames.add(fieldName);
	}

	public String getFieldName() {
		return fieldNames.elementAt(0);
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public boolean isComposite() {
		return false;
//		return fieldNames.size() > 1;
	}
	
	public String[] getFieldNames() {
		if (fieldNames == null || fieldNames.size() == 0)
			return null;
		return fieldNames.toArray(new String[fieldNames.size()]);
	}
}
