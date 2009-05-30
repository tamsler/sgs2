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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

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

@RemoteServiceRelativePath("shell")
public interface GroovyShellService extends RemoteService {
	
	enum ActionType { AUTO_SAVE("auto_save"), USER_SAVE("user_save"), USER_SAVE_AS("user_save_as"), SCRIPT_EXECUTION("script_execution");
	
		public String name;
				
		ActionType(String name) {
			this.name = name;
		}
	}

	public ScriptExecutionResult run(String sourceCode, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public ScriptParseResult parse(String sourceCode, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public SaveResult save(String uuid, String sourceCode, ActionType actionType, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public SaveResult autoSave(String uuid, String sourceCode, ActionType actionType, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public SaveResult saveAs(String uuid, String name, String sourceCode, ActionType actionType, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public InitAutoSaveResult initAutoSave(String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public LatestScriptResult getLatestScript(String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public MarkAsFavoriteResult markAsFavorite(String uuid, String name, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public ScriptResult getScript(String name, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	public FavoriteResult getFavorite(String secureToken)
		throws RpcSecurityException, Server500Exception;
}
