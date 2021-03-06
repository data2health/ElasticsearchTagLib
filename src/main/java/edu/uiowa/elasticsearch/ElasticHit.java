package edu.uiowa.elasticsearch;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")

public class ElasticHit extends BodyTagSupport {
	ElasticSearch theSearch = null;
	ElasticIterator theIterator = null;
	ElasticArrayIterator theArrayIterator = null;
	String label = null;
	String delimiter = ".";
	static Logger logger = LogManager.getLogger(ElasticHit.class);

	public int doStartTag() throws JspTagException {
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		theIterator = (ElasticIterator) findAncestorWithClass(this, ElasticIterator.class);
		theArrayIterator = (ElasticArrayIterator) findAncestorWithClass(this, ElasticArrayIterator.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}

		try {
			if (label.equals("score")) {
				logger.debug("elastic score hit: " + label + ": " + theIterator.theHit.getScore());
				pageContext.getOut().print(theIterator.theHit.getScore());
			} else if (label.equals("_index")) {
				logger.debug("elastic index hit: " + label + ": " + theIterator.theHit.getIndex());
				pageContext.getOut().print(theIterator.theHit.getIndex());
			} else if (label.equals("_id")) {
				logger.debug("elastic id hit: " + label + ": " + theIterator.theHit.getId());
				pageContext.getOut().print(theIterator.theHit.getId().replaceAll("[^a-zA-Z0-9_]+", "")); // in case the id contains problematic characters
			} else {
				logger.debug("delimiter: " + delimiter);
				if (!label.contains(delimiter)) {
					if (label.equals("")) {
						logger.debug("elastic hit: " + label + ": " + theArrayIterator.current);
						pageContext.getOut().print(theArrayIterator.current);
					} else {
						String display = (theArrayIterator == null ? theIterator.theDocument.optString(label) : ((JSONObject)(theArrayIterator.current)).optString(label));
						logger.debug("elastic hit: " + label + ": " + display);
						pageContext.getOut().print(display);
					}
				} else {
					String[] nodes = label.split(delimiter.equals(".") ? "\\"+delimiter : delimiter);
					logger.debug("elasic hit path: " + stringToArray(nodes));
					Object current = theIterator.theDocument.opt(nodes[0]);
					for (int i = 0; i < nodes.length; i++) {
						if (current == null || current == org.json.JSONObject.NULL) {
							logger.debug("elastic hit: " + nodes[i] + ": <missing>");
							pageContext.getOut().print("");							
						} else if (current instanceof JSONObject) {
							JSONObject object = (JSONObject)current;
							logger.debug("elasic hit path element: " + nodes[i]);
							current = object.opt(nodes[i+1]);
						} else if (current instanceof JSONArray) {
							logger.debug("elasic hit array path element: " + nodes[i]);
							if (((JSONArray) current).length() == 0) {
								logger.debug("elastic hit: " + nodes[i] + ": <array empty>");
								pageContext.getOut().print("");
								current = null;
							} else
								current = ((JSONArray) current).get(0);
							i--;
						} else {
							logger.debug("elastic hit: " + label + ": " + ((String)current));
							pageContext.getOut().print(((String)current));
						}
					}
				}
			}
		} catch (CorruptIndexException e) {
			logger.error("Corruption Exception", e);
		} catch (IOException e) {
			logger.error("IO Exception", e);
		}

		return SKIP_BODY;
	}
	
	public static String stringToArray(String[] array) {
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
