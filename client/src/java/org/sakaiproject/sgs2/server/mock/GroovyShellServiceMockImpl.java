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
import groovy.lang.Script;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.sakaiproject.sgs2.client.GroovyShellService;
import org.sakaiproject.sgs2.client.InitAutoSaveResult;
import org.sakaiproject.sgs2.client.LatestScriptResult;
import org.sakaiproject.sgs2.client.SaveResult;
import org.sakaiproject.sgs2.client.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.ScriptParseResult;
import org.sakaiproject.sgs2.server.GroovyShellServiceImpl;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GroovyShellServiceMockImpl extends RemoteServiceServlet implements GroovyShellService {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(GroovyShellServiceImpl.class);

	// FIXME : need to be able to store script name as well
	private Map<String, String> autoSaveMap = new HashMap<String, String>();

	// Mock Impl
	public ScriptExecutionResult submit(String sourceCode, String secureToken) {
		
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
		
		ScriptExecutionResult scriptExecutionResult = new ScriptExecutionResult();
		scriptExecutionResult.setOutput((null == output || "".equals(output)) ? null : output.toString());
		scriptExecutionResult.setResult((null == result || "".equals(result)) ? null : result.toString());
		scriptExecutionResult.setStackTrace((null == stackTrace || "".equals(stackTrace)) ? null : stackTrace.toString());
		
		return scriptExecutionResult;
	}
	
	// Mock Impl
	public ScriptParseResult parse(String sourceCode, String secureToken) {
			
		StringWriter stackTrace = new StringWriter();
		
		Script script = null;
		
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
	public SaveResult save(String uuid, String name, String sourceCode, ActionType actionType, String secureToken) {
		
		SaveResult autoSaveResult = new SaveResult();
		try {
			autoSaveMap.put(uuid, sourceCode);
		}
		catch(Exception e) {
			autoSaveResult.setError(e.getMessage());
		}
		
		autoSaveResult.setName(name);
		autoSaveResult.setActionType(actionType);
		return autoSaveResult;
	}
	
	// Mock Impl
	public InitAutoSaveResult initAutoSave(String secureToken) {
		InitAutoSaveResult initAutoSaveResult = new InitAutoSaveResult();
		String uuid = UUID.randomUUID().toString();
		initAutoSaveResult.setScriptUuid(uuid);
		autoSaveMap.put(uuid, "");
		return initAutoSaveResult;
	}

	public LatestScriptResult getLatestScript(String secureToken) {
		// TODO : the autoSaveMap should store a script object with user data and time stamp
		
		LatestScriptResult latestScriptResult = new LatestScriptResult();
		
		if(autoSaveMap.size() == 0) {
			
			latestScriptResult.setHasScript(Boolean.FALSE);
		}
		else {
			
			latestScriptResult.setHasScript(Boolean.TRUE);
			Set<String> uuids = autoSaveMap.keySet();
			
			// FIXME : need to store script name as well
			
			for(String uuid : uuids) {
				latestScriptResult.setScriptUuid(uuid);
				latestScriptResult.setScript(autoSaveMap.get(uuid));
				break;
			}
		}
		
		return latestScriptResult;
	}
}
