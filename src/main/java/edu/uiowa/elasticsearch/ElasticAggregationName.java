package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

@SuppressWarnings("serial")

public class ElasticAggregationName extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticAggregation theAggregation = null;

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theAggregation = (ElasticAggregation) findAncestorWithClass(this, ElasticAggregation.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}
		try {
			pageContext.getOut().print(theAggregation.theAggregation.getName());
		} catch (IOException e) {
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
