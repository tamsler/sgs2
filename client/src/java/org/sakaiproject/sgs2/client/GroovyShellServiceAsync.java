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
import org.sakaiproject.sgs2.client.async.result.FavoriteResult;
import org.sakaiproject.sgs2.client.async.result.InitAutoSaveResult;
import org.sakaiproject.sgs2.client.async.result.LatestScriptResult;
import org.sakaiproject.sgs2.client.async.result.MarkAsFavoriteResult;
import org.sakaiproject.sgs2.client.async.result.SaveResult;
import org.sakaiproject.sgs2.client.async.result.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.async.result.ScriptParseResult;
import org.sakaiproject.sgs2.client.async.result.ScriptResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GroovyShellServiceAsync {
	
	void run(String name, String sourceCode, String secureToken, AsyncCallback<ScriptExecutionResult> callback);
	
	void parse(String sourceCode, String secureToken, AsyncCallback<ScriptParseResult> callback);
	
	void save(String uuid, String sourceCode, ActionType actionType, String secureToken, AsyncCallback<SaveResult> callback);
	
	void autoSave(String uuid, String sourceCode, ActionType actionType, String secureToken, AsyncCallback<SaveResult> callback);
	
	void saveAs(String uuid, String name, String sourceCode, ActionType actionType, String secureToken, AsyncCallback<SaveResult> callback);
		
	void initAutoSave(String secureToken, AsyncCallback<InitAutoSaveResult> callback);
	
	void getLatestScript(String secureToken, AsyncCallback<LatestScriptResult> callback);
	
	void markAsFavorite(String uuid, String name, String secureToken, AsyncCallback<MarkAsFavoriteResult> callback);
	
	void getScript(String name, String secureToken, AsyncCallback<ScriptResult> callback);
	
	void getFavorite(String SecureToken, AsyncCallback<FavoriteResult> callback);
}
