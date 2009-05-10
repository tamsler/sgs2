package org.sakaiproject.sgs2.server;

import org.sakaiproject.sgs2.client.GreetingService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.gwtwidgets.server.spring.GWTSpringController;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends GWTSpringController implements GreetingService {
	
	private UserDirectoryService userDirectoryService;

	public String greetServer(String input) {
		System.out.println("DEBUG: input = " + input);
		System.out.println("DEBUG: current user = " + userDirectoryService.getCurrentUser().getDisplayName());
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}
	
	// DI
	
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}
}
