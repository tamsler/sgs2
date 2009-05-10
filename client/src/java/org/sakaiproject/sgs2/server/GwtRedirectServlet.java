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
			System.out.println("DEBUG: requestURI = " + request.getRequestURI());
			response.sendRedirect(request.getRequestURI() + "/" + DEFAULT_PAGE);
			
			return;
		}	
			
		serveResource(request, response, true);
	}

}
