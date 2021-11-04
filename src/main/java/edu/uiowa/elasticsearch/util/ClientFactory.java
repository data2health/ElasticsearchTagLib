package edu.uiowa.elasticsearch.util;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

import co.elastic.clients.base.RestClientTransport;
import co.elastic.clients.base.Transport;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;

public class ClientFactory {
    static Logger logger = Logger.getLogger(Test.class);
    protected static LocalProperties prop_file = null;
    
    static private RestClient restClient = null;
    static ElasticsearchClient client = null;

    public static ElasticsearchClient getClient() {
    	if (client != null)
    		return client;

    	prop_file = PropertyLoader.loadProperties("elasticsearch_hal");

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(prop_file.getProperty("es_user"), prop_file.getProperty("es_password")));

		RestClientBuilder builder = RestClient.builder(new HttpHost(prop_file.getProperty("es_host"), 9200))
				.setHttpClientConfigCallback(new HttpClientConfigCallback() {
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				}); // Create the low-level client

		restClient = builder.build();

		// Create the transport with a Jackson mapper
		Transport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

		// And create the API client
		ElasticsearchClient client = new ElasticsearchClient(transport);

    	return client;
    }
    
    public static void close() throws IOException {
    	restClient.close();
    }
}
