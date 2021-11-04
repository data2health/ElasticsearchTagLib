package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

@SuppressWarnings("serial")

public class ElasticAggregationTerm extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticAggregationTermIterator theIterator = null;

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticAggregationTermIterator) findAncestorWithClass(this, ElasticAggregationTermIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}
		try {
			pageContext.getOut().print(theIterator.bucket.getKeyAsString());
		} catch (IOException e) {
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
