package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class ElasticDocument extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	static Logger logger = LogManager.getLogger(ElasticDocument.class);
	
	boolean escape = false;

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticIterator) findAncestorWithClass(this, ElasticIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Elastic Document tag not nesting in Search instance");
		}

		try {
			logger.info("response document: " + theIterator.theDocument.toString(3));
			if (escape)
				pageContext.getOut().print(theIterator.theDocument.toString(3).replaceAll("<", "&lt")); // presenting embedded tags can break things
			else
				pageContext.getOut().print(theIterator.theDocument.toString(3));
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

	public boolean isEscape() {
		return escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

}
