package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class ElasticCount extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	static Logger logger = LogManager.getLogger(ElasticCount.class);

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);

		if (theSearch == null) {
			throw new JspTagException("Elastic Count tag not nesting in Search instance");
		}

		try {
			pageContext.getOut().print(theSearch.hits.getTotalHits().value);
		} catch (CorruptIndexException e) {
			logger.error("Corruption Exception", e);
		} catch (IOException e) {
			logger.error("IO Exception", e);
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
