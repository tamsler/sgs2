package org.sakaiproject.sgs2.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("shell")
public interface GroovyShellService extends RemoteService {

	String submitSourceCode(String sourceCode);
}
