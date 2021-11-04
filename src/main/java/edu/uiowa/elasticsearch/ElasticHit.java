package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class ElasticHit extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	String label = null;
	String value = null;
	private static final Log log = LogFactory.getLog(ElasticHit.class);

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticIterator) findAncestorWithClass(this, ElasticIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}

		try {
			if (label.equals("score"))
				pageContext.getOut().print(theIterator.theHit.getScore());
			else {
				log.debug("lucene hit: " + label + ": " + theIterator.theDocument.optString(label));
				pageContext.getOut().print(theIterator.theDocument.optString(label));
			}
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
