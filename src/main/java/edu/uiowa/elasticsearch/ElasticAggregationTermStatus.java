package edu.uiowa.elasticsearch;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")

public class ElasticAggregationTermStatus extends BodyTagSupport {
	static Logger logger = LogManager.getLogger(ElasticSearch.class);
	ElasticAggregation theAggregation = null;
	ElasticAggregationTermIterator theIterator = null;
	HttpServletRequest request = null;

	public int doStartTag() throws JspTagException {
		theAggregation = (ElasticAggregation) findAncestorWithClass(this, ElasticAggregation.class);
		theIterator = (ElasticAggregationTermIterator) findAncestorWithClass(this, ElasticAggregationTermIterator.class);

		if (theAggregation == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}
		try {
			logger.info("request " + request);
			Enumeration<String> paramEnum = request.getParameterNames();
			while (paramEnum.hasMoreElements()) {
				String param = paramEnum.nextElement();
				if (param.equals(theAggregation.theAggregation.getName())) {
					logger.info("\tparameter: " + param);
					for (String value : request.getParameterValues(param)) {
						if (value.equals(theIterator.bucket.getKeyAsString())) {
							logger.info("\tmatched: " + value);
							pageContext.getOut().print("checked");
							return SKIP_BODY;
						}
					}
				}
			}
			pageContext.getOut().print("");
		} catch (IOException e) {
		}

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
