package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

@SuppressWarnings("serial")

public class ElasticAggregationCount extends BodyTagSupport {
	static Logger logger = LogManager.getLogger(ElasticAggregationCount.class);

	ElasticSearch theSearch = null;
	ElasticAggregation theAggregation = null;

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theAggregation = (ElasticAggregation) findAncestorWithClass(this, ElasticAggregation.class);

		if (theAggregation == null) {
			throw new JspTagException("Aggregation Count tag not nesting in an Aggregation instance");
		}
		try {
			switch (theAggregation.theAggregation.getType()) {
			case "sterms":
				pageContext.getOut().print(((Terms)theAggregation.theAggregation).getBuckets().size());
				break;
			case "composite":
				break;
			default:
				logger.error("aggregation type not handled: " + theAggregation.theAggregation.getType());
			}
		} catch (IOException e) {
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
