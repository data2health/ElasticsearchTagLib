package edu.uiowa.elasticsearch.util;

public class Aggregator {
	String displayName = null;
	String fieldName = null;

	public Aggregator(String displayName, String fieldName) {
		this.displayName = displayName;
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
}
