package edu.uiowa.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import edu.uiowa.elasticsearch.util.Aggregator;
import edu.uiowa.elasticsearch.util.SearchField;
import edu.uiowa.elasticsearch.util.Filter;
import edu.uiowa.elasticsearch.util.RestClientFactory;

@SuppressWarnings("serial")

public class ElasticIndex extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticIndex.class);
	String propertyName = null;
	RestHighLevelClient client = null;
	public Vector<String> indices = new Vector<String>();
	Hashtable<String, Filter> filters = new Hashtable<String, Filter>();
	Hashtable<String, Aggregator> aggregations = new Hashtable<String, Aggregator>();
	List<SearchField> searchFields = new ArrayList<SearchField>();
	
	public int doStartTag() throws JspException {
		client = RestClientFactory.getClient(propertyName);
		logger.info("property file: " + propertyName);
		try {
			logger.info("Elasticsearch cluster name: " + client.info(RequestOptions.DEFAULT).getClusterName());
			logger.info("Elasticsearch node name: " + client.info(RequestOptions.DEFAULT).getNodeName());
			logger.info("Elasticsearch tagline: " + client.info(RequestOptions.DEFAULT).getTagline());

			GetIndexRequest request = new GetIndexRequest("*");
		    GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
		    String[] indices = response.getIndices();
		    logger.info("indices:");
		    for (String index : indices) {
		    	if (index.startsWith("."))
		    		continue;
			    logger.info("\t" + index);
			    this.indices.add(index);
		    }
		} catch (IOException e) {
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		clearServiceState();
		return super.doEndTag();
	}
	
	private void clearServiceState() {
		client = null;
		indices = new Vector<String>();
		filters = new Hashtable<String, Filter>();
		aggregations = new Hashtable<String, Aggregator>();
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
