/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.elasticsearch;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")

public class ElasticArrayIterator extends BodyTagSupport {
	ElasticIterator theIterator = null;
	ElasticArrayIterator theArrayIterator = null;
	SearchHits theHits = null;
	SearchHit theHit = null;
	private int hitFence = 0;
	private int hitOffset = 0;
	JSONObject parentObject = null;
	JSONArray parentArray = null;
	Object current = null;
	JSONArray theArray = new JSONArray();
	String label = null;
	String delimiter = ".";
	String var = null;

	int limitCriteria = Integer.MAX_VALUE;
	int startCriteria = 1;
	private static final Log log = LogFactory.getLog(ElasticArrayIterator.class);
    private int scope = PageContext.PAGE_SCOPE;

	public int doStartTag() throws JspException {
		log.trace("limit: " + limitCriteria);
		log.trace("start: " + startCriteria);

		Tag ancestor = this.getParent();
		do {
			if (ancestor == null) {
				throw new JspTagException("Elastic ArrayIterator tag not nested in an iterator instance");				
			} else if (ancestor instanceof ElasticArrayIterator) {
				theArrayIterator = (ElasticArrayIterator) ancestor;
			} else if (ancestor instanceof ElasticIterator){
				theIterator = (ElasticIterator) ancestor;
			} else {
				ancestor = ancestor.getParent();
			}
		} while (theIterator == null && theArrayIterator == null);
		log.debug("ancestor: " + ancestor + "\tmain: " + (ancestor instanceof ElasticIterator) + "\tarray: " + (ancestor instanceof ElasticArrayIterator));
		log.debug(theArrayIterator + "\t" + theIterator);
		
			log.debug("delimiter: " + delimiter);
			if (!label.contains(delimiter)) {
				current = theIterator != null ? theIterator.theDocument.get(label) : ((JSONObject)theArrayIterator.theArray.get(theArrayIterator.hitFence-1)).opt(label);
				log.debug("elastic array hit: " + label + ": " + current);
			} else {
				String[] nodes = label.split(delimiter.equals(".") ? "\\"+delimiter : delimiter);
				log.debug("elasic array hit path: " + ElasticHit.stringToArray(nodes));
				current = theIterator.theDocument.opt(nodes[0]);
				for (int i = 0; i < nodes.length; i++) {
					if (current == null || current == org.json.JSONObject.NULL) {
						log.debug("elastic hit: " + nodes[i] + ": <missing>");
					} else if (current instanceof JSONObject) {
						JSONObject object = (JSONObject)current;
						log.debug("elasic hit path element: " + nodes[i]);
						current = object.opt(nodes[i+1]);
					} else if (current instanceof JSONArray) {
						log.debug("elasic hit array path element: " + nodes[i]);
						if (((JSONArray) current).length() == 0) {
							log.debug("elastic hit: " + nodes[i] + ": <array empty>");
						} else
							break;
						i--;
					} else {
						log.debug("elastic hit: " + label + ": " + ((String)current));
					}
				}
			}

			log.debug("current: " + current);
			if (current != null) {
				if (startCriteria < 1) // a missing parameter in the requesting URL
					// results in this getting set to 0.
					startCriteria = 1;
				hitFence = startCriteria - 1;
				if (current != org.json.JSONObject.NULL)
					theArray = (JSONArray) current;
				if (hitFence < theArray.length()) {
					current = theArray.get(hitFence++);
					if (var != null)
						pageContext.setAttribute(var, this, scope);
					return EVAL_BODY_INCLUDE;
				}
			}
			return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		if (limitCriteria <= 0 || (limitCriteria > 0 && hitFence >= limitCriteria + startCriteria - 1)) {
			return SKIP_BODY;
		}

		if (hitFence < theArray.length()) {
			current = theArray.get(hitFence++);
			return EVAL_BODY_AGAIN;
		}
		
		return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException, JspException {
		if (var != null)
			pageContext.removeAttribute(var);
		
		clearServiceState();
       return super.doEndTag();
	}

	private void clearServiceState() {
		theIterator = null;
		theArrayIterator = null;
		this.theHits = null;
		this.label = null;
		theHit = null;
		theArray = new JSONArray();
		current = null;
		hitFence = 0;
		delimiter = ".";
	}

	public int getHitRank() {
		return hitOffset + hitFence;
	}

	public int getRankOffset() {
		return hitOffset;
	}

	public void setRankOffset(int hitOffset) {
		this.hitOffset = hitOffset;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getLimitCriteria() {
		return limitCriteria;
	}

	public void setLimitCriteria(int limitCriteria) {
		this.limitCriteria = limitCriteria;
	}

	public int getStartCriteria() {
		return startCriteria;
	}

	public void setStartCriteria(int startCriteria) {
		this.startCriteria = startCriteria;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public boolean getIsFirst() {
		return hitFence == 1;
	}
	
	public boolean getIsLast() {
		return hitFence >= theArray.length() || hitFence >= limitCriteria;
	}
	
	public int getCount() {
		return (int) theArray.length();
	}

}
