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

/**
 * The Interface GroovyShellService.
 */
@RemoteServiceRelativePath("shell")
public interface GroovyShellService extends RemoteService {
	
	/**
	 * The Enum ActionType.
	 */
	enum ActionType { AUTO_SAVE("auto_save"), USER_SAVE("user_save"), USER_SAVE_AS("user_save_as"), SCRIPT_EXECUTION("script_execution");
	
		/** The name. */
		public String name;
				
		/**
		 * Instantiates a new action type.
		 * 
		 * @param name the name
		 */
		ActionType(String name) {
			this.name = name;
		}
	}

	/**
	 * Run.
	 * 
	 * @param name the name
	 * @param sourceCode the source code
	 * @param secureToken the secure token
	 * 
	 * @return the script execution result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public ScriptExecutionResult run(String name, String sourceCode, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Parses the.
	 * 
	 * @param sourceCode the source code
	 * @param secureToken the secure token
	 * 
	 * @return the script parse result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public ScriptParseResult parse(String sourceCode, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Save.
	 * 
	 * @param uuid the uuid
	 * @param sourceCode the source code
	 * @param actionType the action type
	 * @param secureToken the secure token
	 * 
	 * @return the save result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public SaveResult save(String uuid, String sourceCode, ActionType actionType, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Auto save.
	 * 
	 * @param uuid the uuid
	 * @param sourceCode the source code
	 * @param actionType the action type
	 * @param secureToken the secure token
	 * 
	 * @return the save result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public SaveResult autoSave(String uuid, String sourceCode, ActionType actionType, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Save as.
	 * 
	 * @param uuid the uuid
	 * @param name the name
	 * @param sourceCode the source code
	 * @param actionType the action type
	 * @param secureToken the secure token
	 * 
	 * @return the save result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public SaveResult saveAs(String uuid, String name, String sourceCode, ActionType actionType, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Inits the auto save.
	 * 
	 * @param secureToken the secure token
	 * 
	 * @return the inits the auto save result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public InitAutoSaveResult initAutoSave(String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Gets the latest script.
	 * 
	 * @param secureToken the secure token
	 * 
	 * @return the latest script
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public LatestScriptResult getLatestScript(String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Mark as favorite.
	 * 
	 * @param uuid the uuid
	 * @param name the name
	 * @param secureToken the secure token
	 * 
	 * @return the mark as favorite result
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public MarkAsFavoriteResult markAsFavorite(String uuid, String name, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Gets the script.
	 * 
	 * @param name the name
	 * @param secureToken the secure token
	 * 
	 * @return the script
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public ScriptResult getScript(String name, String secureToken)
		throws RpcSecurityException, Server500Exception;
	
	/**
	 * Gets the favorite.
	 * 
	 * @param secureToken the secure token
	 * 
	 * @return the favorite
	 * 
	 * @throws RpcSecurityException the rpc security exception
	 * @throws Server500Exception the server500 exception
	 */
	public FavoriteResult getFavorite(String secureToken)
		throws RpcSecurityException, Server500Exception;
}
