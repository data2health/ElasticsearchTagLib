package edu.uiowa.elasticsearch;

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

import edu.uiowa.elasticsearch.util.Aggregator;

@SuppressWarnings("serial")

public class ElasticAggregator extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticAggregator.class);

	ElasticIndex theIndex = null;
	String displayName = null;
	String fieldName = null;
	int size = 100;
	
	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);
		
		Hashtable<String, Aggregator> aggregations = theIndex.aggregations;
		Aggregator current = aggregations.get(displayName);
		if (current == null) {
			current = new Aggregator(displayName, fieldName, size);
			aggregations.put(displayName, current);
		} else {
			current.addFieldName(fieldName);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
