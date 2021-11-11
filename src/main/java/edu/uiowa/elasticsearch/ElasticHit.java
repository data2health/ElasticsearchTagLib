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

public class ElasticHit extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	String label = null;
	String delimiter = "/";
	private static final Log log = LogFactory.getLog(ElasticHit.class);

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticIterator) findAncestorWithClass(this, ElasticIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}

		try {
			if (label.equals("score")) {
				log.debug("elastic hit: " + label + ": " + theIterator.theHit.getScore());
				pageContext.getOut().print(theIterator.theHit.getScore());
			} else if (label.equals("_index")) {
				log.debug("elastic hit: " + label + ": " + theIterator.theHit.getIndex());
				pageContext.getOut().print(theIterator.theHit.getIndex());
			} else if (label.equals("_id")) {
				log.debug("elastic hit: " + label + ": " + theIterator.theHit.getId());
				pageContext.getOut().print(theIterator.theHit.getId().replaceAll("[.:/]+", "")); // in case the id contains problematic characters
			} else {
				log.debug("delimiter: " + delimiter);
				if (!label.contains(delimiter)) {
					log.debug("elastic hit: " + label + ": " + theIterator.theDocument.optString(label));
					pageContext.getOut().print(theIterator.theDocument.optString(label));
				} else {
					String[] nodes = label.split(delimiter);
					log.debug("elasic hit path: " + stringToArray(nodes));
					Object current = theIterator.theDocument.opt(nodes[0]);
					for (int i = 0; i < nodes.length; i++) {
						if (current == null || current == org.json.JSONObject.NULL) {
							log.debug("elastic hit: " + nodes[i] + ": <missing>");
							pageContext.getOut().print("");							
						} else if (current instanceof JSONObject) {
							JSONObject object = (JSONObject)current;
							log.debug("elasic hit path element: " + nodes[i]);
							current = object.opt(nodes[i+1]);
						} else if (current instanceof JSONArray) {
							log.debug("elasic hit array path element: " + nodes[i]);
							if (((JSONArray) current).length() == 0) {
								log.debug("elastic hit: " + nodes[i] + ": <array empty>");
								pageContext.getOut().print("");
								current = null;
							} else
								current = ((JSONArray) current).get(0);
							i--;
						} else {
							log.debug("elastic hit: " + label + ": " + ((String)current));
							pageContext.getOut().print(((String)current));
						}
					}
				}
			}
		} catch (CorruptIndexException e) {
			log.error("Corruption Exception", e);
		} catch (IOException e) {
			log.error("IO Exception", e);
		}

		return SKIP_BODY;
	}
	
	String stringToArray(String[] array) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < array.length; i++)
			result.append((i == 0 ? "" : ", ")+array[i]);
		return result.toString();
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

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
