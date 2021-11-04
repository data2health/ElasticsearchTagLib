package edu.uiowa.elasticsearch.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.json.JSONObject;
import org.json.JSONTokener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._core.SearchResponse;
import co.elastic.clients.elasticsearch._core.search.Hit;

public class Test {
	static Logger logger = Logger.getLogger(Test.class);
	protected static LocalProperties prop_file = null;

	public static void main(String[] args) throws IOException {
		PropertyConfigurator.configure(args[0]);
		test3(queryString(args));
	}
	
	static String queryString(String[] args) {
		StringBuffer queryString = new StringBuffer();
		for (int i = 1; i < args.length; i++)
			queryString.append(args[i] + " ");
		return queryString.toString();
	}
	
	static void test1() throws IOException {
		ElasticsearchClient client = ClientFactory.getClient();

		logger.info(client.info().name());

		SearchResponse<Object> search = client.search(s -> s.index("cd2h-*").query(q -> q.term(t -> t.field("label").value("eichmann"))), Object.class);
		
		for (Hit<Object> hit : search.hits().hits()) {
			String index = hit.index();
			double score = hit.score();

			logger.info("index: " + index + "\tscore: " + score);
			logger.debug(hit.source());
			
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String, Object>) hit.source();
			logger.info("\turl: "+map.get("url"));
			logger.info("\tlabel: "+map.get("label"));
			logger.info("\tacademic_article: "+map.get("academic_article"));
		}

		ClientFactory.close();
	}

	static void test2() throws IOException {
		ElasticsearchClient client = ClientFactory.getClient();

		logger.info(client.info().clusterName());
		logger.info(client.info().tagline());
		logger.info(client.info().name());

		SearchResponse<TagHit> search = client.search(s -> s.index("cd2h-*").query(q -> q.term(t -> t.field("label").value("eichmann"))), TagHit.class);
		
		for (Hit<TagHit> hit : search.hits().hits()) {
			String index = hit.index();
			double score = hit.score();

			logger.info("index: " + index + "\tscore: " + score);
			logger.info("\t"+hit.source());
		}

		ClientFactory.close();
	}
	
	static void test3(String queryString) throws IOException {
		RestHighLevelClient client = RestClientFactory.getClient();
		
		logger.info(client.info(RequestOptions.DEFAULT).getClusterName());
		logger.info(client.info(RequestOptions.DEFAULT).getNodeName());
		logger.info(client.info(RequestOptions.DEFAULT).getTagline());

		org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest("cd2h-*"); 
		
		MultiMatchQueryBuilder matcher = new MultiMatchQueryBuilder(queryString, "*");
		matcher.type(Type.CROSS_FIELDS);
		matcher.operator(Operator.AND);
		
		TermsQueryBuilder termer = new TermsQueryBuilder("study_type.keyword", "Interventional");
		
		TermsQueryBuilder termser = new TermsQueryBuilder("overall_status.keyword", "Terminated", "Completed");

		BoolQueryBuilder booler = new BoolQueryBuilder();
		booler.must(matcher);
		booler.filter(termser);
		booler.filter(termer);
		
		SuggestionBuilder<?> termSuggestionBuilder = SuggestBuilders.termSuggestion("label").text(queryString);
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("suggest_label", termSuggestionBuilder);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(booler); 
		searchRequest.source(searchSourceBuilder);
		searchSourceBuilder.size(100);
		searchSourceBuilder.suggest(suggestBuilder);

		TermsAggregationBuilder aggregation = AggregationBuilders.terms("status").field("overall_status.keyword");
		searchSourceBuilder.aggregation(aggregation);
		
		aggregation = AggregationBuilders.terms("type").field("study_type.keyword");
		searchSourceBuilder.aggregation(aggregation);
		
		aggregation = AggregationBuilders.terms("index").field("_index");
		searchSourceBuilder.aggregation(aggregation);
		
		org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();
		logger.info("hit count: " + hits.getTotalHits().value);
		for (SearchHit hit : hits) {
			JSONObject object = new JSONObject(new JSONTokener(new StringReader(hit.getSourceAsString())));
		    logger.info(hit.getIndex());
		    logger.debug(hit.getIndex() + "\t" + object.toString(3));
		    logger.info("\turl: " + object.optString("url"));
		    logger.info("\tlabel: " + object.optString("label"));
		    logger.info("\tactivity: " + object.optString("activity"));
		}
		
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
		
		logger.info("suggestions:");
		Suggest suggest = searchResponse.getSuggest(); 
		TermSuggestion termSuggestion = suggest.getSuggestion("suggest_label"); 
		for (TermSuggestion.Entry entry : termSuggestion.getEntries()) { 
		    for (TermSuggestion.Entry.Option option : entry) { 
		        String suggestText = option.getText().string();
		        logger.info("\t" + suggestText);
		    }
		}
		client.close();
	}

}
