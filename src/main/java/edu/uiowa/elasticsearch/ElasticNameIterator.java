/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.elasticsearch;

import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

@SuppressWarnings("serial")

public class ElasticNameIterator extends BodyTagSupport {
	ElasticIndex theIndex = null;
	Vector<String> theIndices = null;
	private int hitFence = 0;
	private int hitOffset = 0;
	int startCriteria = 1;
	String theIndexName = null;

	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);

		if (theIndex == null) {
			throw new JspTagException("Elastic Iterator tag not nesting in Search instance");
		}

		if (startCriteria < 1) // a missing parameter in the requesting URL
			// results in this getting set to 0.
			startCriteria = 1;
		hitFence = startCriteria - 1;
		theIndices = theIndex.indices;
		if (hitFence < theIndices.size()) {
			theIndexName = theIndices.elementAt(hitFence++);

			return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		if (hitFence < theIndices.size()) {
			theIndexName = theIndices.elementAt(hitFence++);

			return EVAL_BODY_AGAIN;
		}
		clearServiceState();
		return SKIP_BODY;
	}

	private void clearServiceState() {
		theIndex = null;
		theIndices = null;
		theIndexName = null;
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

	public int getStartCriteria() {
		return startCriteria;
	}

	public void setStartCriteria(int startCriteria) {
		this.startCriteria = startCriteria;
	}

	public String getTheIndexName() {
		return theIndexName;
	}

	public void setTheIndexName(String theIndexName) {
		this.theIndexName = theIndexName;
	}

}
