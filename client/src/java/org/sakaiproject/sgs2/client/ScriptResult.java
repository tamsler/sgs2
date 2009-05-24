package org.sakaiproject.sgs2.client;

import java.io.Serializable;

public class ScriptResult implements AsyncCallbackResult, Serializable {

	private static final long serialVersionUID = 1L;

	String name;
	String script;
	String error;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
