package org.sakaiproject.sgs2.server.mock;

import org.sakaiproject.sgs2.client.GroovyShellService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GroovyShellServiceMockImpl extends RemoteServiceServlet implements GroovyShellService {

	private static final long serialVersionUID = 1L;

	public String submitSourceCode(String sourceCode) {

		return "Groovy Shell Service ...";
	}

}
