package org.sakaiproject.sgs2.client;

import java.io.Serializable;

public class ScriptParseResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String stackTrace;
	private String error;

	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
}
