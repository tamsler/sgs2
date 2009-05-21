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

import java.io.Serializable;

public class LatestScriptResult implements AsyncCallbackResult, Serializable {

	private static final long serialVersionUID = 1L;

	private String scriptUuid;
	private String script;
	private Boolean hasScript;

	public String getScriptUuid() {
		return scriptUuid;
	}

	public void setScriptUuid(String scriptUuid) {
		this.scriptUuid = scriptUuid;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Boolean getHasScript() {
		return hasScript;
	}

	public void setHasScript(Boolean hasScript) {
		this.hasScript = hasScript;
	}
}
