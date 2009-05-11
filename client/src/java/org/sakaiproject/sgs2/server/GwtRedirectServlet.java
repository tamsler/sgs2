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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;

public class GwtRedirectServlet extends DefaultServlet {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_PAGE = "Sgs2.html";
	
	protected void doGet(HttpServletRequest request,
			 			 HttpServletResponse response)
		throws IOException, ServletException 
	{
		String relativePath = getRelativePath(request);	  
		
		if (relativePath.equals("/")) {

			response.sendRedirect(request.getRequestURI() + "/" + DEFAULT_PAGE);			
			return;
		}	
			
		serveResource(request, response, true);
	}

}
