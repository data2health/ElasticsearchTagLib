package edu.uiowa.elasticsearch;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")

public class ElasticResultIncludeField extends BodyTagSupport {
	static Logger logger = LogManager.getLogger(ElasticResultIncludeField.class);

	ElasticIndex theIndex = null;
	String fieldName = null;
	
	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);
		theIndex.resultIncludeFields.add(fieldName);
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

}
