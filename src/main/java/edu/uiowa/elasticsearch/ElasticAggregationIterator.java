/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.elasticsearch;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.aggregations.Aggregation;

@SuppressWarnings("serial")

public class ElasticAggregationIterator extends BodyTagSupport {
	ElasticSearch theSearch = null;
	List<Aggregation> theAggregations = null;
	Aggregation theAggregation = null;
	private int hitFence = 0;
	private int hitOffset = 0;

	int limitCriteria = Integer.MAX_VALUE;
	int startCriteria = 1;
	double thresholdCriteria = 0.0;
	static Logger logger = LogManager.getLogger(ElasticAggregationIterator.class);

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
		theAggregations = theSearch.aggregations.asList();
		if (hitFence < theAggregations.size()) {
			theAggregation = theAggregations.get(hitFence++);

			return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		if (limitCriteria <= 0 || (limitCriteria > 0 && hitFence >= limitCriteria + startCriteria - 1)) {
			clearServiceState();
			return SKIP_BODY;
		}

		if (hitFence < theAggregations.size()) {
			theAggregation = theAggregations.get(hitFence++);

			return EVAL_BODY_AGAIN;
		}
		clearServiceState();
		return SKIP_BODY;
	}

	private void clearServiceState() {
		this.theAggregations = null;
		theAggregation = null;
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

}
