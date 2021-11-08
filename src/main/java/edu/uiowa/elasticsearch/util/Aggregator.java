package edu.uiowa.elasticsearch.util;

public class Aggregator {
	String displayName = null;
	String fieldName = null;
	int size = 0;

	public Aggregator(String displayName, String fieldName, int size) {
		this.displayName = displayName;
		this.fieldName = fieldName;
		this.size = size;
	}

	public String getFieldName() {
		return fieldName;
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
	
}
