package edu.uiowa.elasticsearch.util;

public class SearchField {
	String fieldName = null;
	String value = null;
	float boost = 0;

	public SearchField(String fieldName, String value, float boost) {
		this.fieldName = fieldName;
		this.value = value;
		this.boost = boost;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	public float getBoost() {
		return boost;
	}

	public void setBoost(float boost) {
		this.boost = boost;
	}
	
	
	public Object getFormattedValue() {
		if (value.matches("[0-9]+"))
			return new Integer(Integer.parseInt(value));
		return value;
	}

}
