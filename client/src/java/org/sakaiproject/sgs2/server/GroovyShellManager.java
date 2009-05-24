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

import java.util.Collection;
import java.util.List;

import org.sakaiproject.sgs2.client.model.Script;

public interface GroovyShellManager {

	public Long save(Script script);
	public void update(Script script);
	public void delete(Script script);
	public Collection<Script> getScripts(String userId);
	public Script getLatestScript(String userId);
	public Script getScript(String uuid);
	public Script getScript(String userId, String name);
	public List<String> getFavorite(String userId);

}
