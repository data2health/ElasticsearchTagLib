package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class ElasticIndexName extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticNameIterator theIterator = null;
	static Logger logger = LogManager.getLogger(ElasticIndexName.class);

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticNameIterator) findAncestorWithClass(this, ElasticNameIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Index name tag not nesting in index name iterator instance");
		}

		try {
			pageContext.getOut().print(theIterator.getTheIndexName());
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
