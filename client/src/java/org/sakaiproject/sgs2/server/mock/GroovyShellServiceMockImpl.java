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

package org.sakaiproject.sgs2.server.mock;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.sakaiproject.sgs2.client.FavoriteResult;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.sgs2.client.InitAutoSaveResult;
import org.sakaiproject.sgs2.client.LatestScriptResult;
import org.sakaiproject.sgs2.client.MarkAsFavoriteResult;
import org.sakaiproject.sgs2.client.SaveResult;
import org.sakaiproject.sgs2.client.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.ScriptParseResult;
import org.sakaiproject.sgs2.client.ScriptResult;
import org.sakaiproject.sgs2.client.StopResult;
import org.sakaiproject.sgs2.client.exceptions.RpcSecurityException;
import org.sakaiproject.sgs2.server.GroovyShellResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GroovyShellServiceMockImpl extends RemoteServiceServlet implements GroovyShellService {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(GroovyShellServiceMockImpl.class);

	private Map<String, ScriptMock> autoSaveMap = new HashMap<String, ScriptMock>();

	private ScriptMock latestScriptRef = null;
	
	private Map<String, Thread> runMap = new ConcurrentHashMap<String, Thread>();

	// Mock Impl
	public ScriptExecutionResult run(String uuid, final String sourceCode, String secureToken) 
		throws RpcSecurityException {
		
		final ScriptExecutionResult scriptExecutionResult = new ScriptExecutionResult();
		
		final StringWriter output = new StringWriter();
		Binding binding = new Binding();
		binding.setVariable("out", new PrintWriter(output));
		final StringWriter stackTrace = new StringWriter();
		final PrintWriter errorWriter = new PrintWriter(stackTrace);
		final GroovyShellResult groovyShellResult = new GroovyShellResult();

		
		final GroovyShell groovyShell = new GroovyShell(binding);
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {

					groovyShellResult.setResult(groovyShell.evaluate(sourceCode));

				} catch (MultipleCompilationErrorsException e) {

					stackTrace.append(e.getMessage());

				} catch (Throwable t) {

					t.printStackTrace(errorWriter);
				}
			}
			
		};
		
		
		runMap.put(uuid, thread);
		
		thread.setName(uuid);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		scriptExecutionResult.setOutput((null == output || "".equals(output)) ? null : output.toString());
		scriptExecutionResult.setResult((null == groovyShellResult.getResult() || "".equals(groovyShellResult.getResult())) ? null : groovyShellResult.getResult().toString());
		scriptExecutionResult.setStackTrace((null == stackTrace || "".equals(stackTrace)) ? null : stackTrace.toString());
		
		return scriptExecutionResult;
	}

	// Mock Impl
	public ScriptParseResult parse(String sourceCode, String secureToken) 
		throws RpcSecurityException {

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

	// Mock Impl
	public SaveResult save(String uuid, String sourceCode, ActionType actionType, String secureToken) 
		throws RpcSecurityException {

		SaveResult autoSaveResult = new SaveResult();
		ScriptMock script = null;

		if(autoSaveMap.containsKey(uuid)) {
			script = autoSaveMap.get(uuid);
		}
		else {
			System.out.println("ERROR: Trying to save a script that doesn't exists yet.");
			script = new ScriptMock();
		}

		script.setScript(sourceCode);
		script.setActionType(actionType.name);

		latestScriptRef = script;

		try {
			// In case it's a new script otherwise we don't have to do this
			autoSaveMap.put(uuid, script);
		}
		catch(Exception e) {
			autoSaveResult.setError(e.getMessage());
		}

		autoSaveResult.setActionType(actionType);
		return autoSaveResult;
	}

	// Mock Impl
	public InitAutoSaveResult initAutoSave(String secureToken) 
		throws RpcSecurityException {

		InitAutoSaveResult initAutoSaveResult = null;


		initAutoSaveResult = new InitAutoSaveResult();

		String uuid = UUID.randomUUID().toString();
		initAutoSaveResult.setScriptUuid(uuid);

		ScriptMock script = new ScriptMock();
		latestScriptRef = script;
		script.setId(uuid);
		autoSaveMap.put(uuid, script);


		return initAutoSaveResult;
	}

	public LatestScriptResult getLatestScript(String secureToken) 
		throws RpcSecurityException {

		LatestScriptResult latestScriptResult = new LatestScriptResult();

		if(null == latestScriptRef) {
			latestScriptResult.setHasScript(Boolean.FALSE);

		}
		else {

			latestScriptResult.setName(latestScriptRef.getName());
			latestScriptResult.setHasScript(Boolean.TRUE);
			latestScriptResult.setScript(latestScriptRef.getScript());
			latestScriptResult.setScriptUuid(latestScriptRef.getId().toString());
		}

		return latestScriptResult;
	}

	public MarkAsFavoriteResult markAsFavorite(String uuid, String name, String secureToken) 
		throws RpcSecurityException {

		MarkAsFavoriteResult markAsFavoriteResult = new MarkAsFavoriteResult();
		if(autoSaveMap.containsKey(uuid)) {

			ScriptMock script = autoSaveMap.get(uuid);
			script.setFavorite(Boolean.TRUE);
			markAsFavoriteResult.setName(script.getName());
		}
		else {
			markAsFavoriteResult.setError("Script does not exist");
		}

		return markAsFavoriteResult;
	}

	public ScriptResult getScript(String name, String secureToken) 
		throws RpcSecurityException {

		ScriptResult scriptResult = new ScriptResult();
		scriptResult.setName(name);

		Set<String> uuids = autoSaveMap.keySet();

		for(String uuid : uuids) {
			ScriptMock script = autoSaveMap.get(uuid);
			if(script.getName().equals(name)) {
				scriptResult.setScript(script.getScript());
				return scriptResult;
			}
		}


		scriptResult.setError("Wasn't abel to fine script with name = " + name);

		return scriptResult;
	}

	public SaveResult autoSave(String uuid, String sourceCode, ActionType actionType, String secureToken) 
		throws RpcSecurityException {

		SaveResult saveResult = new SaveResult();

		if(autoSaveMap.containsKey(uuid)) {
			ScriptMock script = autoSaveMap.get(uuid);
			script.setScript(sourceCode);
			script.setActionType(actionType.name);
			saveResult.setName(script.getName());
			latestScriptRef = script;
		}
		else {

		}

		saveResult.setActionType(actionType.AUTO_SAVE);
		return saveResult;
	}

	public SaveResult saveAs(String uuid, String name, String sourceCode, ActionType actionType, String secureToken) 
		throws RpcSecurityException {

		SaveResult saveResult = new SaveResult();
		saveResult.setActionType(actionType);
		saveResult.setName(name);

		Set<String> uuids = autoSaveMap.keySet();
		for(String key : uuids) {
			ScriptMock script = autoSaveMap.get(key);
			if(name.equals(script.getName())) {
				saveResult.setNameExists(Boolean.TRUE);
				return saveResult;
			}
		}

		if(autoSaveMap.containsKey(uuid)) {
			ScriptMock script = autoSaveMap.get(uuid);
			script.setName(name);
			script.setScript(sourceCode);
			script.setActionType(actionType.name);
			latestScriptRef = script;
		}
		else {
			saveResult.setError("Script does not exists");
		}
		saveResult.setNameExists(Boolean.FALSE);
		return saveResult;
	}

	public FavoriteResult getFavorite(String secureToken)
			throws RpcSecurityException {
		
		FavoriteResult favoriteResult = new FavoriteResult();
		
		Collection<String> scriptNames = new ArrayList<String>();
		
		Set<String> uuids = autoSaveMap.keySet();
		
		for(String key : uuids) {
			
			ScriptMock script = autoSaveMap.get(key);
			
			if(script.getFavorite()) {
				
				scriptNames.add(script.getName());
			}
		}
		
		favoriteResult.setFavorite(scriptNames);
		
		return favoriteResult;
	}

	public StopResult stop(String uuid, String secureToken) throws RpcSecurityException {
	
		StopResult stopResult = new StopResult();
		try {
		if(runMap.containsKey(uuid)) {
			
			Thread thread = runMap.get(uuid);
			// FIXME: need to find a better way to do that
			// but everything else I have tried so far won't "stop" the thread
			thread.stop();

		}
		else {
			LOG.error("Didn't find script execution resources for uuid = " + uuid);
		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return stopResult;
	}
}
