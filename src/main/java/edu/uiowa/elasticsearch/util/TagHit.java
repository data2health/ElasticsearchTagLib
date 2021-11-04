package edu.uiowa.elasticsearch.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagHit {
	public String url;
	public String label;
	
	public String toString() {
		return "url: " + url + "\tlabel: " + label;
	}
}
