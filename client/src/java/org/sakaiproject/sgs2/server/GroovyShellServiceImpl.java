package org.sakaiproject.sgs2.server;

import org.gwtwidgets.server.spring.GWTSpringController;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.user.api.UserDirectoryService;

public class GroovyShellServiceImpl extends GWTSpringController implements GroovyShellService {

	private static final long serialVersionUID = 1L;
	private UserDirectoryService userDirectoryService;
	
	public String submitSourceCode(String sourceCode) {

		return userDirectoryService.getCurrentUser().getDisplayName();
	}
	
	// DI
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {

		this.userDirectoryService = userDirectoryService;
	}

}
