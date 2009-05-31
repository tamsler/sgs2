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

import com.google.gwt.i18n.client.Constants;

public interface I18nConstants extends Constants {
	
	String scriptLabel();
	
	String runButton();
	String parseButton();
	String clearButton();
	
	String fileMenuNew();
	String fileMenuSave();
	String fileMenuSaveAs();
	String fileMenuInfo();
	
	String editMenuAddToScriptsMenu();
	
	String scriptsMenuCookies();
	
	String mainMenuFile();
	String mainMenuEdit();
	String mainMenuScripts();
	
	String executionOutput();
	String executionResult();
	String executionStackTrace();
	String executionHistory();
	String executionConsole();
	
	String commandCookiesMsg1();
	String commandCookiesMsg2();
	String commandCookiesName();
	String commandCookiesValue();
	
	String commandEditAddToScripts();
	
	String dialogText();
	String dialogTextError();
	String dialogCloseButton();
	String dialogInfoTitle();
	String dialogInfoContentAuthor();
	String dialogInfoContentUrl();
	String dialogSaveButton();
	String dialogErrorMsg();
	String dialogCancelButton();
	String dialogSaveName();
	
	String commandSaveAsMsg();
	
	String timerAutoSave();
	
	String asyncCallbackSave();
	
	String asyncCallbackSaveAsMsg1();
	String asyncCallbackSaveAsMsg2();
	
	String asyncCallbackInitAutoSaveMsg();
	
	String asyncCallbackParseMsg();

	String historyStackTrace();
	String historyResult();
	String historyOutput();
	
	String checkResultMsg();

}
