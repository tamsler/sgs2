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

@RemoteServiceRelativePath("shell")
public interface GroovyShellService extends RemoteService {
	
	enum ActionType { AUTO_SAVE("auto_save"), USER_SAVE("user_save"), SCRIPT_EXECUTION("script_execution");
	
		public String name;
				
		ActionType(String name) {
			this.name = name;
		}
	}

	public ScriptExecutionResult submit(String sourceCode);
	
	public ScriptParseResult parse(String sourceCode);
	
	public SaveResult save(String uuid, String name, String sourceCode, ActionType actionType);
	
	public String initAutoSave();
	
	public LatestScriptResult getLatestScript();
}
