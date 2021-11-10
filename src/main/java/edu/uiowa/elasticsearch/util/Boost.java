package edu.uiowa.elasticsearch.util;

public class Boost {
	String fieldName = null;
	float boost = 0;

	public Boost(String fieldName, float boost) {
		this.fieldName = fieldName;
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
	
}
