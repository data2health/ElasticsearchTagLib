package edu.uiowa.elasticsearch;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import edu.uiowa.elasticsearch.util.SearchField;

@SuppressWarnings("serial")

public class ElasticSearchField extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticSearchField.class);

	ElasticIndex theIndex = null;
	String fieldName = null;
	String value = null;
	float boost = 1;
	
	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);
		
		theIndex.searchFields.add(new SearchField(fieldName, value, boost));
		
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public float getBoost() {
		return boost;
	}

	public void setBoost(float boost) {
		this.boost = boost;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getFormattedValue() {
		return value;
	}

}
