package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class ElasticHitRank extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	ElasticArrayIterator theArrayIterator = null;
	static Logger logger = LogManager.getLogger(ElasticHitRank.class);

	public int doStartTag() throws JspTagException {
		theIterator = (ElasticIterator) findAncestorWithClass(this, ElasticIterator.class);

		if (theIterator == null) {
			throw new JspTagException("Lucene HitRank tag not nesting in Iterator instance");
		}

		try {
		    pageContext.getOut().print(theIterator.getHitRank());
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
