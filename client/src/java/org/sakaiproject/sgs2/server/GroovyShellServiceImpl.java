/**********************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sgs2.server;

import org.gwtwidgets.server.spring.GWTSpringController;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.user.api.UserDirectoryService;

public class GroovyShellServiceImpl extends GWTSpringController implements GroovyShellService {

	private static final long serialVersionUID = 1L;
	private UserDirectoryService userDirectoryService;
	
	public String submit(String sourceCode) {

		System.out.println("DEBUG: source code = " + sourceCode);
		return userDirectoryService.getCurrentUser().getDisplayName();
	}
	
	// DI
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {

		this.userDirectoryService = userDirectoryService;
	}

}
