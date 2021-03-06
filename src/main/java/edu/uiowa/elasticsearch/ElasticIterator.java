/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.elasticsearch;

import java.io.StringReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;
import org.json.JSONTokener;

@SuppressWarnings("serial")

public class ElasticIterator extends BodyTagSupport {
	ElasticSearch theSearch = null;
	SearchHits theHits = null;
	SearchHit theHit = null;
	public int hitFence = 0;
	private int hitOffset = 0;
	double thresholdFence = 0.0;
	JSONObject theDocument = null;
	String label = null;
	String var = null;

	int limitCriteria = Integer.MAX_VALUE;
	int startCriteria = 1;
	double thresholdCriteria = 0.0;
	static Logger logger = LogManager.getLogger(ElasticIterator.class);
    private int scope = PageContext.PAGE_SCOPE;

	public int doStartTag() throws JspException {
		logger.trace("limit: " + limitCriteria);
		logger.trace("start: " + startCriteria);
		logger.trace("threshold: " + thresholdCriteria);
		theSearch = (ElasticSearch) findAncestorWithClass(this, ElasticSearch.class);
		if (limitCriteria == Integer.MAX_VALUE)
			limitCriteria = theSearch.getLimitCriteria();

		if (theSearch == null) {
			throw new JspTagException("Elastic Iterator tag not nesting in Search instance");
		}

		if (startCriteria < 1) // a missing parameter in the requesting URL
			// results in this getting set to 0.
			startCriteria = 1;
		hitFence = startCriteria - 1;
		theHits = theSearch.hits;
		if (hitFence < theHits.getTotalHits().value) {
			theHit = theHits.getHits()[hitFence++];
			thresholdFence = theHit.getScore();
			theDocument = new JSONObject(new JSONTokener(new StringReader(theHit.getSourceAsString())));

			if (thresholdFence < thresholdCriteria) {
				clearServiceState();
				return SKIP_BODY;
			}

			if (var != null)
				pageContext.setAttribute(var, this, scope);
			return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		if (limitCriteria <= 0 || (limitCriteria > 0 && hitFence >= limitCriteria + startCriteria - 1)) {
			return SKIP_BODY;
		}

		if (hitFence < theHits.getTotalHits().value) {
			theHit = theHits.getHits()[hitFence++];
			thresholdFence = theHit.getScore();

			if (thresholdFence < thresholdCriteria) {
				return SKIP_BODY;
			}

			theDocument = new JSONObject(new JSONTokener(new StringReader(theHit.getSourceAsString())));
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
		if (var != null)
			pageContext.removeAttribute(var);
		var = null;
		this.theHits = null;
		this.label = null;
		theHit = null;
		hitFence = 0;
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

	public double getThresholdCriteria() {
		return thresholdCriteria;
	}

	public void setThresholdCriteria(double thresholdCriteria) {
		this.thresholdCriteria = thresholdCriteria;
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
		return hitFence == 0;
	}
	
	public int getCount() {
		return (int) theHits.getTotalHits().value;
	}

}
