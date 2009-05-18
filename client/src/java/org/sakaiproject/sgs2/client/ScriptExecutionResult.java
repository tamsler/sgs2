package org.sakaiproject.sgs2.client;

import java.io.Serializable;

public class ScriptExecutionResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String result;
	private String output;
	private String stackTrace;
	private String error;
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getOutput() {
		return output;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
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
