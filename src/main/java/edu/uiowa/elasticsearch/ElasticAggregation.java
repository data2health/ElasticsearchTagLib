package edu.uiowa.elasticsearch;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.elasticsearch.search.aggregations.Aggregation;

@SuppressWarnings("serial")

public class ElasticAggregation extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticAggregationIterator theIterator = null;
	Aggregation theAggregation = null;

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticAggregationIterator) findAncestorWithClass(this, ElasticAggregationIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}
		
		theAggregation = theIterator.theAggregation;

		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
