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
import org.sakaiproject.sgs2.client.SaveResult;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.sgs2.client.LatestScriptResult;
import org.sakaiproject.sgs2.client.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.ScriptParseResult;
import org.sakaiproject.sgs2.client.GroovyShellService.ActionType;
import org.sakaiproject.sgs2.client.model.Script;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

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
		// Getting userEid. If we cannot find the userId from the userEid, we just log the userEid
		String userEid = userDirectoryService.getCurrentUser().getEid();
		try {
			script.setUserId(userDirectoryService.getUserId(userEid));
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			script.setUserId(userEid);
		}
		script.setScript(sourceCode);
		script.setOutput((null == output || "".equals(output)) ? null : output.toString());
		script.setResult((null == result || "".equals(result)) ? null : result.toString());
		script.setStackTrace((null == stackTrace || "".equals(stackTrace)) ? null : stackTrace.toString());
		script.setActionType(ActionType.SCRIPT_EXECUTION.name);
		script.setActionDate(new Date());
		
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
		scriptExecutionResult.setOutput((null == output || "".equals(output)) ? null : output.toString());
		scriptExecutionResult.setResult((null == result || "".equals(result)) ? null : result.toString());
		scriptExecutionResult.setStackTrace((null == stackTrace || "".equals(stackTrace)) ? null : stackTrace.toString());
		
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
	
	public SaveResult save(String uuid, String name, String sourceCode, ActionType actionType) {
		
		LOG.info("Auto Save uuid = " + uuid);
		
		// Persisting script information
		Script script = new Script();
		script.setId(new Long(uuid));
		// Getting userEid. If we cannot find the userId from the userEid, we just log the userEid
		String userEid = userDirectoryService.getCurrentUser().getEid();
		try {
			script.setUserId(userDirectoryService.getUserId(userEid));
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			script.setUserId(userEid);
		}
		script.setScript(sourceCode);
		script.setActionType(actionType.name);
		script.setActionDate(new Date());

		SaveResult autoSaveResult = new SaveResult();
		autoSaveResult.setResult("");
		
		try {

			groovyShellManager.update(script);
		}
		catch(Exception e) {
			e.printStackTrace();
			autoSaveResult.setResult(e.getMessage());
			LOG.error("Was not able to save script object");
		}

		return autoSaveResult;
	}
	
	public String initAutoSave() {
		
		Script script = new Script();
		String userEid = userDirectoryService.getCurrentUser().getEid();
		try {
			script.setUserId(userDirectoryService.getUserId(userEid));
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			script.setUserId(userEid);
		}
		script.setScript("");
		script.setActionType(ActionType.AUTO_SAVE.name);
		script.setActionDate(new Date());
		
		Long sequence = groovyShellManager.save(script);
		
		String uuid = sequence.toString();
		
		LOG.info("Init Auto Save : uuid = " + uuid);
		
		return uuid;
	}
	
	public LatestScriptResult getLatestScript() {
		
		Script script = null;
		LatestScriptResult latestScriptResult = new LatestScriptResult();
		
		String userEid = userDirectoryService.getCurrentUser().getEid();
		String userId = null;
		try {
			userId = userDirectoryService.getUserId(userEid);
			script = groovyShellManager.getLatestScript(userId);
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			script = groovyShellManager.getLatestScript(userEid);
		}
		
		if(null == script) {
			return null;
		}
		else {
			
			latestScriptResult.setScriptUuid(script.getId().toString());
			latestScriptResult.setScript(script.getScript());
			return latestScriptResult;
		}
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
