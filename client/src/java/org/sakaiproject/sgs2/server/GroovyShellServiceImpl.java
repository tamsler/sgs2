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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.gwtwidgets.server.spring.GWTSpringController;
import org.sakaiproject.sgs2.client.AutoSaveResult;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.sgs2.client.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.ScriptParseResult;
import org.sakaiproject.sgs2.client.model.Script;
import org.sakaiproject.user.api.UserDirectoryService;

public class GroovyShellServiceImpl extends GWTSpringController implements GroovyShellService {

	private static final Log LOG = LogFactory.getLog(GroovyShellServiceImpl.class);

	private static final long serialVersionUID = 1L;
	private UserDirectoryService userDirectoryService;
	private GroovyShellManager groovyShellManager;
	
	public ScriptExecutionResult submit(String sourceCode) {
		
		StringWriter output = new StringWriter();
		Binding binding = new Binding();
		binding.setVariable("out", new PrintWriter(output));
		
		StringWriter stackTrace = new StringWriter();
		PrintWriter errorWriter = new PrintWriter(stackTrace);
		
		Object result = null;
		
		try {
			
			result = new GroovyShell(binding).evaluate(sourceCode);
			
		} catch (MultipleCompilationErrorsException e) {
			
			  stackTrace.append(e.getMessage());
			  
		} catch (Throwable t) {
			  
			  t.printStackTrace(errorWriter);
		}
		
		// Persisting script information
		Script script = new Script();
		script.setScript(sourceCode);
		script.setUserEid(userDirectoryService.getCurrentUser().getEid());
		script.setExecutionDate(new Date());
		
		try {
			
			groovyShellManager.save(script);
		}
		catch(Exception e) {
			e.printStackTrace();
			stackTrace.append(e.getMessage());
			LOG.error("Was not able to save script object");
		}
		
		// Sending result back to the client
		ScriptExecutionResult scriptExecutionResult = new ScriptExecutionResult();
		scriptExecutionResult.setOutput(output.toString());
		scriptExecutionResult.setResult((null == result) ? null : result.toString());
		scriptExecutionResult.setStackTrace(stackTrace.toString());
		
		return scriptExecutionResult;
	}
	
	public ScriptParseResult parse(String sourceCode) {
		
		StringWriter stackTrace = new StringWriter();
		
		groovy.lang.Script script = null;
		
		try {
			
			script = new GroovyShell().parse(sourceCode);
			
		} catch (CompilationFailedException cfe) {
			
			  stackTrace.append(cfe.getMessage());
			  
		} catch (Exception e) {
			  
			 stackTrace.append(e.getMessage());
		}
		
		ScriptParseResult scriptParseResult = new ScriptParseResult();
		scriptParseResult.setStackTrace(stackTrace.toString());
		
		return scriptParseResult;
	}
	
	public AutoSaveResult autoSave(String uuid, String sourceCode) {
		
		LOG.info("Auto Save uuid = " + uuid);
		
		// FIXME : Add DB persistence
		AutoSaveResult autoSaveResult = new AutoSaveResult();
		autoSaveResult.setResult(uuid);
		return autoSaveResult;
	}
	
	public String initAutoSave() {
				
		String uuid = UUID.randomUUID().toString();
		
		LOG.info("Init Auto Save : uuid = " + uuid);
		
		return uuid;
	}
	
	// DI
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}
	
	// DI
	public void setGroovyShellManager(GroovyShellManager groovyShellManager) {
		this.groovyShellManager = groovyShellManager;
	}
}
