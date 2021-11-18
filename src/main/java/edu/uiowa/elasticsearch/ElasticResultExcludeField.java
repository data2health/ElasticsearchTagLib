package edu.uiowa.elasticsearch;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")

public class ElasticResultExcludeField extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticResultExcludeField.class);

	ElasticIndex theIndex = null;
	String fieldName = null;
	
	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);
		theIndex.resultExcludeFields.add(fieldName);
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
