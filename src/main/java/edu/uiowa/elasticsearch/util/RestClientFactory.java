package edu.uiowa.elasticsearch.util;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;

public class RestClientFactory {
    static Logger logger = LogManager.getLogger(RestClientFactory.class);
    protected static LocalProperties prop_file = null;
    
    static private RestClient restClient = null;
    static RestHighLevelClient client = null;
    
    public static RestHighLevelClient getClient() {
    	return getClient("elasticsearch_hal");
    }

    public static RestHighLevelClient getClient(String clientName) {
    	if (client != null)
    		return client;

    	prop_file = PropertyLoader.loadProperties(clientName);

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(prop_file.getProperty("es_user"), prop_file.getProperty("es_password")));

		RestClientBuilder builder = RestClient.builder(new HttpHost(prop_file.getProperty("es_host"), 9200))
				.setHttpClientConfigCallback(new HttpClientConfigCallback() {
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				}); // Create the low-level client

		client = new RestHighLevelClient(builder);
    	return client;
    }
    
    public static void close() throws IOException {
    	restClient.close();
    }
    
    public static String getIndexPattern() {
    	return prop_file.getProperty("index_pattern");
    }
}
