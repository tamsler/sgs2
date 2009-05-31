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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.gwtwidgets.server.spring.GWTSpringController;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.sgs2.client.async.result.FavoriteResult;
import org.sakaiproject.sgs2.client.async.result.InitAutoSaveResult;
import org.sakaiproject.sgs2.client.async.result.LatestScriptResult;
import org.sakaiproject.sgs2.client.async.result.MarkAsFavoriteResult;
import org.sakaiproject.sgs2.client.async.result.SaveResult;
import org.sakaiproject.sgs2.client.async.result.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.async.result.ScriptParseResult;
import org.sakaiproject.sgs2.client.async.result.ScriptResult;
import org.sakaiproject.sgs2.client.exceptions.RpcSecurityException;
import org.sakaiproject.sgs2.client.exceptions.Server500Exception;
import org.sakaiproject.sgs2.client.model.Script;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

public class GroovyShellServiceImpl extends GWTSpringController implements GroovyShellService {

	private static final Log LOG = LogFactory.getLog(GroovyShellServiceImpl.class);

	private static final long serialVersionUID = 1L;
	private UserDirectoryService userDirectoryService;
	private SessionManager sessionManager;
	private SecurityService securityService;
	private GroovyShellManager groovyShellManager;
	
	// API Impl
	public ScriptExecutionResult run(String sourceCode, String secureToken)
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
		StringWriter output = new StringWriter();
		Binding binding = new Binding();
		binding.setVariable("out", new PrintWriter(output));
		
		StringWriter stackTrace = new StringWriter();
		PrintWriter errorWriter = new PrintWriter(stackTrace);
		
		GroovyShell groovyShell = new GroovyShell(binding);
		Object result = null;
		long start = 0;
		long end = 0;
		
		try {
			
			start = System.currentTimeMillis();
			result = groovyShell.evaluate(sourceCode);
			end = System.currentTimeMillis();
			
			
		} catch (MultipleCompilationErrorsException e) {
			
			  stackTrace.append(e.getMessage());
			  
		} catch (Throwable t) {
			  
			  t.printStackTrace(errorWriter);
		}
		
		long executionTimeInMillis = 0;
		
		if(0 != start && 0 != end) {
			executionTimeInMillis = end - start;
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
		script.setOutput((null == output || "".equals(output)) ? null : "[" + executionTimeInMillis + "ms] " + output.toString());
		script.setResult((null == result || "".equals(result)) ? null : "[" + executionTimeInMillis + "ms] " + result.toString());
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
		scriptExecutionResult.setOutput((null == output || "".equals(output)) ? null : "[" + executionTimeInMillis + "ms] " + output.toString());
		scriptExecutionResult.setResult((null == result || "".equals(result)) ? null : "[" + executionTimeInMillis + "ms] " + result.toString());
		scriptExecutionResult.setStackTrace((null == stackTrace || "".equals(stackTrace)) ? null : stackTrace.toString());
		
		return scriptExecutionResult;
	}
	
	// API Impl
	public ScriptParseResult parse(String sourceCode, String secureToken)
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
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
	
	// API Impl
	public SaveResult save(String uuid, String sourceCode, ActionType actionType, String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
		return autoSave(uuid, sourceCode, actionType, secureToken);
	}
	
	// API Impl
	public InitAutoSaveResult initAutoSave(String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
		// Create Script object for query
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
		
		// Query
		Long sequence = groovyShellManager.save(script);
		
		// Setup RPC result
		InitAutoSaveResult initAutoSaveResult = new InitAutoSaveResult();
		initAutoSaveResult.setScriptUuid(sequence.toString());
		
		return initAutoSaveResult;
	}
	
	// API Impl
	public LatestScriptResult getLatestScript(String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
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
			latestScriptResult.setHasScript(Boolean.FALSE);
		}
		else {
			latestScriptResult.setHasScript(Boolean.TRUE);
			latestScriptResult.setScriptUuid(script.getId().toString());
			latestScriptResult.setScript(script.getScript());
			latestScriptResult.setName(script.getName());
		}
		
		return latestScriptResult;
	}

	// API Impl
	public MarkAsFavoriteResult markAsFavorite(String uuid, String name, String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
		MarkAsFavoriteResult markAsFavoriteResult = new MarkAsFavoriteResult();
		markAsFavoriteResult.setName(name);
		
		Script script = null;
		
		try {
			script = groovyShellManager.getScript(uuid);
		}
		catch(Exception e) {
			throw new Server500Exception(e.getMessage());
		}
		
		script.setFavorite(Boolean.TRUE);
		
		try {
			groovyShellManager.update(script);
		}
		catch(Exception e) {
			LOG.error("Was not able to mark script as favorite");
			throw new Server500Exception(e.getMessage());
		}
		
		return markAsFavoriteResult;
	}
	
	// API Impl
	public ScriptResult getScript(String name, String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
		ScriptResult scriptResult = new ScriptResult();
		scriptResult.setName(name);
		
		Script script = null;
		
		String userEid = userDirectoryService.getCurrentUser().getEid();
		String userId = null;
		try {
			
			userId = userDirectoryService.getUserId(userEid);
			
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			userId = userEid;
		}
		
		try {
			
			script = groovyShellManager.getScript(userId, name);
		}
		catch(Exception e) {
			throw new Server500Exception(e.getMessage());
		}
		
		if(null != script) {
			scriptResult.setScript(script.getScript());
		}
		
		return scriptResult;
	}
	
	// API Impl
	public SaveResult autoSave(String uuid, String sourceCode, ActionType actionType, String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
	
		SaveResult saveResult = new SaveResult();
		saveResult.setActionType(actionType);
		
		// get script
		Script script = null;
		
		try {
			script = groovyShellManager.getScript(uuid);
		}
		catch(Exception e) {
			throw new Server500Exception(e.getMessage());
		}
		
		if(null == script) {
			throw new Server500Exception("ERROR: Was not able to get script with uuid = " + uuid);
		}
		
		// update script arguments
		script.setId(new Long(uuid));
		script.setScript(sourceCode);
		script.setActionType(actionType.name);
		script.setActionDate(new Date());
		
		// update
		try {

			groovyShellManager.update(script);
		}
		catch(Exception e) {
			LOG.error("Was not able to auto save script object : uuid = " + uuid);
			throw new Server500Exception(e.getMessage());
		}
		
		return saveResult;
	}

	// API Impl
	public SaveResult saveAs(String uuid, String name, String sourceCode, ActionType actionType, String secureToken) 
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
	
		// Getting userEid. If we cannot find the userId from the userEid, we just log the userEid
		String userId = null;
		String userEid = userDirectoryService.getCurrentUser().getEid();
		
		try {
			userId = userDirectoryService.getUserId(userEid);
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			userId = userEid;
		}
		
		SaveResult saveResult = new SaveResult();
		saveResult.setActionType(actionType);
		Script script = null;

		// check if file name exists
		try {
			
			script = groovyShellManager.getScript(userId, name);
		}
		catch(Exception e) {
			throw new Server500Exception(e.getMessage());
		}
		
		if(null != script) {
			saveResult.setNameExists(Boolean.TRUE);
			return saveResult;
		}
		else {
			saveResult.setNameExists(Boolean.FALSE);
		}
		
		// get script with uuid
		try {
			script = groovyShellManager.getScript(uuid);
		}
		catch(Exception e) {
			throw new Server500Exception(e.getMessage());
		}
		
		if(null == script) {
			throw new Server500Exception("ERROR: was not able to get script with uuid = " + uuid);
		}
		
		
		// set script arguments
		script.setId(new Long(uuid));
		script.setUserId(userId);
		script.setScript(sourceCode);
		script.setActionType(actionType.name);
		script.setActionDate(new Date());
		script.setName(name);
		
		// update save result
		saveResult.setName(name);
		
		// update/save script
		try {

			groovyShellManager.update(script);
		}
		catch(Exception e) {
			LOG.error("Was not able to save script object");
			throw new Server500Exception(e.getMessage());
		}
		
		return saveResult;
	}

	// API Impl
	public FavoriteResult getFavorite(String secureToken)
		throws RpcSecurityException, Server500Exception {
		
		isSecure(secureToken);
		
		FavoriteResult favoriteResult = new FavoriteResult();
		
		// Getting userEid. If we cannot find the userId from the userEid, we just log the userEid
		String userId = null;
		String userEid = userDirectoryService.getCurrentUser().getEid();
		
		try {
			userId = userDirectoryService.getUserId(userEid);
		} catch (UserNotDefinedException e1) {
			LOG.error("Was not able to get userId from userEid : userEid = " + userEid);
			userId = userEid;
		}
		
		favoriteResult.setFavorite(groovyShellManager.getFavorite(userId));
		
		return favoriteResult;
	}

	// Helper Methods
	/* 
	 * First, we check if both the client and server session match
	 * Second, we check if current user is the admin user
	 */
	private void isSecure(String clientSecureToken) 
		throws RpcSecurityException, Server500Exception {
		
		if((null == sessionManager) || (null == securityService) || null == userDirectoryService) {
			throw new RpcSecurityException("Security Exception: SGS2 RPC");
		}
			
		String currentSessionId = sessionManager.getCurrentSession().getId();
		String currentUserEid = userDirectoryService.getCurrentUser().getEid();
		boolean isSuperUser = securityService.isSuperUser();
		
		if(null == currentSessionId ||
		   null == currentUserEid ||
		   !isSuperUser ||
		   "".equals(currentSessionId) ||
		   "".equals(currentUserEid) ||
		   !clientSecureToken.startsWith(currentSessionId)) {
			
			LOG.warn("SGS2: Security Exception: CST=" + clientSecureToken + " SST=" + currentSessionId);
			throw new RpcSecurityException("Security Exception: SGS2 RPC");
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
	
	// DI
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	// DI
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
}
