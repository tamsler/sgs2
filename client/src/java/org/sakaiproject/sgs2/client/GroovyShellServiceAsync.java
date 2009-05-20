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

package org.sakaiproject.sgs2.client;

import org.sakaiproject.sgs2.client.GroovyShellService.ActionType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GroovyShellServiceAsync {
	
	void submit(String sourceCode, AsyncCallback<ScriptExecutionResult> callback);
	
	void parse(String sourceCode, AsyncCallback<ScriptParseResult> callback);
	
	void save(String uuid, String name, String sourceCode, ActionType actionType, AsyncCallback<SaveResult> callback);
	
	void initAutoSave(AsyncCallback<String> callback);
	
	void getLatestScript(AsyncCallback<LatestScriptResult> callback);
}
