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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

@SuppressWarnings("serial")

public class ElasticAggregationTermIterator extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticAggregationTermIterator.class);
	List<Aggregation> theAggregations = null;
	ElasticAggregation theAggregation = null;
	private int hitFence = 0;
	private int hitOffset = 0;
	Terms.Bucket bucket = null;
	List<Terms.Bucket> buckets = null;

	int limitCriteria = Integer.MAX_VALUE;
	int startCriteria = 1;
	double thresholdCriteria = 0.0;
	private static final Log log = LogFactory.getLog(ElasticAggregationTermIterator.class);

	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		log.trace("limit: " + limitCriteria);
		log.trace("start: " + startCriteria);
		log.trace("threshold: " + thresholdCriteria);
		theAggregation = (ElasticAggregation) findAncestorWithClass(this, ElasticAggregation.class);

		if (theAggregation == null) {
			throw new JspTagException("Elastic Term Iterator tag not nesting in Aggregation instance");
		}

		if (startCriteria < 1) // a missing parameter in the requesting URL
			// results in this getting set to 0.
			startCriteria = 1;
		hitFence = startCriteria - 1;
		Terms terms = (Terms) theAggregation.theAggregation;
		buckets = (List<Bucket>) terms.getBuckets();
		if (hitFence < buckets.size()) {
			bucket = buckets.get(hitFence++);

			return EVAL_BODY_INCLUDE;
		}
		
		return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		if (limitCriteria <= 0 || (limitCriteria > 0 && hitFence >= limitCriteria + startCriteria - 1)) {
			clearServiceState();
			return SKIP_BODY;
		}

		if (hitFence < buckets.size()) {
			bucket = buckets.get(hitFence++);

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
