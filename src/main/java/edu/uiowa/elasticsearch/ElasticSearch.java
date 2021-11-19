package edu.uiowa.elasticsearch;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import edu.uiowa.elasticsearch.util.Aggregator;
import edu.uiowa.elasticsearch.util.SearchField;
import edu.uiowa.elasticsearch.util.Filter;

@SuppressWarnings("serial")

public class ElasticSearch extends BodyTagSupport {
	static Logger logger = Logger.getLogger(ElasticSearch.class);

	ElasticIndex theIndex = null;
	SearchHits hits = null;
	Aggregations aggregations = null;
	String label = null;
	String indexPattern = null;
	String queryString = null;
	int limitCriteria = Integer.MAX_VALUE;
	boolean fetchSource = true;
	boolean fieldWildCard = true;

	public int doStartTag() throws JspException {
		theIndex = (ElasticIndex) findAncestorWithClass(this, ElasticIndex.class);

		if (theIndex == null) {
		    throw new JspTagException("Elasticsearch Search tag not nested in Index instance");
		}
		
		logger.info("search called: " + queryString);

		try {
			RestHighLevelClient client = theIndex.client;
			
			logger.info(client.info(RequestOptions.DEFAULT).getClusterName());
			logger.info(client.info(RequestOptions.DEFAULT).getNodeName());
			logger.info(client.info(RequestOptions.DEFAULT).getTagline());

			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(theIndex.getIndexPattern()); 
			
			MultiMatchQueryBuilder matcher = new MultiMatchQueryBuilder(queryString);
			if (fieldWildCard)
				matcher.field("*");
			for (SearchField searchField : theIndex.searchFields) {
				matcher.field(searchField.getFieldName(), searchField.getBoost());
			}
			matcher.type(Type.CROSS_FIELDS);
			matcher.operator(Operator.AND);

			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			
			if (theIndex.filters.size() == 0) {
				searchSourceBuilder.query(matcher); 				
			} else {
				BoolQueryBuilder booler = new BoolQueryBuilder();
				booler.must(matcher);
				
				for(Filter filter : theIndex.filters.values()) {
					TermsQueryBuilder termQuery = new TermsQueryBuilder(filter.getFieldName(), filter.getTerms());
					booler.filter(termQuery);
				}
				searchSourceBuilder.query(booler); 				
			}
			
			searchRequest.source(searchSourceBuilder);
			searchSourceBuilder.size(limitCriteria);
			searchSourceBuilder.fetchSource(fetchSource);
			if (theIndex.haveResultFields())
				searchSourceBuilder.fetchSource(theIndex.getResultIncludeFields(), theIndex.getResultExcludeFields());
			
			for (Aggregator aggregation : theIndex.aggregations.values()) {
				TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(aggregation.getDisplayName()).field(aggregation.getFieldName());
				aggregationBuilder.size(aggregation.getSize());
				searchSourceBuilder.aggregation(aggregationBuilder);				
			}

			logger.info("query: " + searchSourceBuilder.query().toString());
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			hits = searchResponse.getHits();
			if (searchResponse.getAggregations() != null)
				aggregations = searchResponse.getAggregations();
			logger.info("hit count: " + hits.getTotalHits().value);

			for (Aggregation agg : searchResponse.getAggregations()) {
			    String type = agg.getType();
			    logger.info("aggregation: " + type + "\tname: " + agg.getName());
			    
					Terms terms = searchResponse.getAggregations().get(agg.getName());
					@SuppressWarnings("unchecked")
					Collection<Terms.Bucket> buckets = (Collection<Terms.Bucket>) terms.getBuckets();
					for (Terms.Bucket bucket : buckets) {
			    	    logger.info("\t" + bucket.getKeyAsString() +" ("+bucket.getDocCount()+")");
					}
			}
			
			return EVAL_BODY_INCLUDE;
		} catch (IOException e) {
			logger.error("IO Exception", e);
		} catch (Exception e) {
			logger.error("Problem Parsing" + queryString, e);
		}

		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		clearServiceState();
		return super.doEndTag();
	}

	private void clearServiceState() {
		this.label = null;
		indexPattern = null;
		queryString = null;
	}

	public String getIndexPattern() {
		return indexPattern;
	}

	public void setIndexPattern(String indexPattern) {
		this.indexPattern = indexPattern;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
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

	public boolean isFetchSource() {
		return fetchSource;
	}

	public void setFetchSource(boolean fetchSource) {
		this.fetchSource = fetchSource;
	}

	public boolean isFieldWildCard() {
		return fieldWildCard;
	}

	public void setFieldWildCard(boolean fieldWildCard) {
		this.fieldWildCard = fieldWildCard;
	}

}
