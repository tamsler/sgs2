package org.sakaiproject.sgs2.client;

import java.io.Serializable;

public class InitAutoSaveResult implements AsyncCallbackResult, Serializable  {
	
	private String scriptUuid;

	public String getScriptUuid() {
		return scriptUuid;
	}

	public void setScriptUuid(String scriptUuid) {
		this.scriptUuid = scriptUuid;
	}
}
