package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class ElasticIndexName extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticNameIterator theIterator = null;
	private static final Log log = LogFactory.getLog(ElasticIndexName.class);

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticNameIterator) findAncestorWithClass(this, ElasticNameIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Index name tag not nesting in index name iterator instance");
		}

		try {
			pageContext.getOut().print(theIterator.getTheIndexName());
		} catch (CorruptIndexException e) {
			log.error("Corruption Exception", e);
		} catch (IOException e) {
			log.error("IO Exception", e);
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
