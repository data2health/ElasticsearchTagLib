package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;

@SuppressWarnings("serial")

public class ElasticAggregationCount extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticAggregation theAggregation = null;

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theAggregation = (ElasticAggregation) findAncestorWithClass(this, ElasticAggregation.class);

		if (theAggregation == null) {
			throw new JspTagException("Aggregation Count tag not nesting in an Aggregation instance");
		}
		try {
			pageContext.getOut().print(((Terms)theAggregation.theAggregation).getBuckets().size());
		} catch (IOException e) {
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
