package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")

public class ElasticHitRank extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	ElasticArrayIterator theArrayIterator = null;
	private static final Log log = LogFactory.getLog(ElasticHitRank.class);

	public int doStartTag() throws JspTagException {
		theIterator = (ElasticIterator) findAncestorWithClass(this, ElasticIterator.class);

		if (theIterator == null) {
			throw new JspTagException("Lucene HitRank tag not nesting in Iterator instance");
		}

		try {
		    pageContext.getOut().print(theIterator.getHitRank());
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
