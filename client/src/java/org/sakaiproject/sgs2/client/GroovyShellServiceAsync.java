package org.sakaiproject.sgs2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GroovyShellServiceAsync {
	
	void submitSourceCode(String sourceCode, AsyncCallback<String> callback);
}
