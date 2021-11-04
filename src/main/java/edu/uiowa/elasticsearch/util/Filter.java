package edu.uiowa.elasticsearch.util;

import java.util.HashSet;
import java.util.Set;

public class Filter {
	String fieldName = null;
	Set<String> terms = new HashSet<String>();

	public Filter(String fieldName) {
		this.fieldName = fieldName;
	}

	public Filter(String fieldName, String termString) {
		this.fieldName = fieldName;
		terms.add(termString);
	}

	public Filter(String fieldName, String termString, String delimiter) {
		this.fieldName = fieldName;
		
		if (delimiter == null) {
			terms.add(termString);
		} else {
			for (String term : termString.split(delimiter)) {
				terms.add(term);
			}
		}
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void addTerm(String term) {
		terms.add(term);
	}
	
	public void addTerms(String termString, String delimiter) {
		for (String term : termString.split(delimiter)) {
			terms.add(term);
		}
	}
	
	public Set<String> getTerms() {
		return terms;
	}
}
