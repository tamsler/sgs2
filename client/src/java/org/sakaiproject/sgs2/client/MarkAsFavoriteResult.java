package org.sakaiproject.sgs2.client;

import java.io.Serializable;

public class MarkAsFavoriteResult implements AsyncCallbackResult, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String name;
	String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
