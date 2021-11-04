package edu.uiowa.elasticsearch;

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import edu.uiowa.elasticsearch.util.Filter;

@SuppressWarnings("serial")

public class ElasticFilter extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticFilter.class);

	ElasticIndex theIndex = null;
	String fieldName = null;
	String termString = null;
	String delimiterPattern = null;
	
	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);
		
		Hashtable<String, Filter> filters = theIndex.filters;
		Filter current = filters.get(fieldName);
		if (current == null) {
			current = new Filter(fieldName);
			filters.put(fieldName, current);
		}
		if (delimiterPattern == null) {
			current.addTerm(termString);
		} else {
			current.addTerms(termString, delimiterPattern);
		}
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

	public String getDelimiterPattern() {
		return delimiterPattern;
	}

	public void setDelimiterPattern(String delimiterPattern) {
		this.delimiterPattern = delimiterPattern;
	}

	public String getTermString() {
		return termString;
	}

	public void setTermString(String termString) {
		this.termString = termString;
	}

}
