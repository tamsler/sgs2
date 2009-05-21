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

import java.util.Date;

import org.sakaiproject.sgs2.client.GroovyShellService.ActionType;
import org.sakaiproject.sgs2.client.ui.widget.Sgs2DialogBox;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sgs2 implements EntryPoint {

	private enum TabbedPanel { OUTPUT(0) , RESULT(1), STACK_TRACE(2), HISTORY(3), CONSOLE(4);
		
		public int position;
		
		TabbedPanel(int position) {
			
			this.position = position;
		}
	}
	
	// GWT RPC Proxy
	private GroovyShellServiceAsync groovyShellService = GWT.create(GroovyShellService.class);

	// UI
	private TextArea textArea = null; 
	private Button submitButton = null;
	private Button parseButton = null;
	
	private FlowPanel outputFlowPanel = null;
	private FlowPanel resultFlowPanel = null;
	private FlowPanel stackTraceFlowPanel = null;
	private FlowPanel historyFlowPanel = null;
	private FlowPanel consoleFlowPanel = null;
	
	private VerticalPanel mainVerticalPanel = null;
	private VerticalPanel inputVerticalPanel = null;
	private HorizontalPanel menuAndStatusPanel = null;
	private VerticalPanel statusPanel = null;
	private HorizontalPanel buttonPanel = null;

	private TabPanel resultTabPanel = null;
	
	private HTML status = null;
	
	private String autoSaveUuid = null;
	
	// One minute
	private int autoSaveInterval = 60000;
	
	// 5 seconds
	private int autoSaveStatusChange = 5000;
	
	// I18N
	I18nConstants i18n = null;
	
	// Callbacks
	private AsyncCallback<ScriptExecutionResult> submitAsyncCallback = null;
	private AsyncCallback<ScriptParseResult> parseAsyncCallback = null;
	private AsyncCallback<InitAutoSaveResult> initAutoSaveAsyncCallback = null;
	private AsyncCallback<LatestScriptResult> getLatestScript = null;
	private AsyncCallback<SaveResult> saveAsyncCallback = null;
	
	// Commands
	private Command menuFileNewCommand = null;
	private Command menuFileSaveCommand = null;
	private Command menuFileInfoCommand = null;
	
	// ClickHandlers
	private ClickHandler submitClickHandler = null;
	private ClickHandler parseClickHandler = null;
	
	// Timers
	private Timer autoSaveTimer = null;
	private Timer statusTimer = null;
	
	// Constructor
	public Sgs2() {
		
		// This is a reference for the declared servlet in Sgs2.gwt.xml
		((ServiceDefTarget) groovyShellService).setServiceEntryPoint(GWT.getModuleBaseURL() + "rpc/sgs2");
		
		// Fix Sakai parent iFrame height
		configureSakaiParentIframe();
		
		i18n = GWT.create(I18nConstants.class);
		
		submitAsyncCallback = getSubmitAsyncCallback();
		parseAsyncCallback = getParseAsyncCallback();
		initAutoSaveAsyncCallback = getInitAutoSaveAsyncCallback();
		getLatestScript = getLatestScriptAsyncCallback();
		saveAsyncCallback = getSaveAsyncCallback();
		
		menuFileNewCommand = getMenuFileNewCommand();
		menuFileSaveCommand = getMenuFileSaveCommand();
		menuFileInfoCommand = getMenuFileInfoCommand();
		
		submitClickHandler = getSubmitClickHandler();
		parseClickHandler = getParseClickHandler();
		
		mainVerticalPanel = new VerticalPanel();
		inputVerticalPanel = new VerticalPanel();
		menuAndStatusPanel = new HorizontalPanel();
		buttonPanel = new HorizontalPanel();
		statusPanel = new VerticalPanel();
		
		status = new HTML("");
		
		textArea = new TextArea();
		
		submitButton = new Button("Submit");
		parseButton = new Button("Parse");
		
		resultTabPanel = new TabPanel();
		
		outputFlowPanel = new FlowPanel();
		resultFlowPanel = new FlowPanel();
		stackTraceFlowPanel = new FlowPanel();
		historyFlowPanel = new FlowPanel();
		consoleFlowPanel = new FlowPanel();
	}
	
	public void onModuleLoad() {
		
		// Get latest script
		groovyShellService.getLatestScript(getSecureToken(), getLatestScript);
				
		// Setup Auto Save Timer
		autoSaveTimer = getAutoSaveTimer();
		autoSaveTimer.scheduleRepeating(autoSaveInterval);
		
		// File Menu items
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("New", menuFileNewCommand);
		fileMenu.addItem("Save", menuFileSaveCommand);
		fileMenu.addItem("Info", menuFileInfoCommand);
		
		// Analyze Menu items
		// TODO : Make this like a plugin
		MenuBar analyzeMenu = new MenuBar(true);
		analyzeMenu.addItem("Hello World", new Command() {
			public void execute() {
				beforeSubmit();
				groovyShellService.submit("println 'Hello World'", getSecureToken(), submitAsyncCallback);
			}
		});
		analyzeMenu.addItem("4 + 4", new Command() {
			public void execute() {
				beforeSubmit();
				groovyShellService.submit("4 + 4", getSecureToken(), submitAsyncCallback);
			}
		});
		
		// Make a new menu bar, adding a few cascading menus to it.
		MenuBar menu = new MenuBar();
	    menu.addItem("File", fileMenu);
	    menu.addItem("Analyze", analyzeMenu);
		
		// Attach and configure widgets
		statusPanel.add(status);
		
		resultTabPanel.add(outputFlowPanel, "Output");
		resultTabPanel.add(resultFlowPanel, "Result");
		resultTabPanel.add(stackTraceFlowPanel, "Stack Trace");
		resultTabPanel.add(historyFlowPanel, "History");
		resultTabPanel.add(consoleFlowPanel, "Console");
		
		resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);
		
		submitButton.addClickHandler(submitClickHandler);
		parseButton.addClickHandler(parseClickHandler);

		// Text Area Configuration
		textArea.setCharacterWidth(80);
		textArea.setVisibleLines(25);
		
		// Tab Panel Configuration
		resultTabPanel.setWidth("100%");
		inputVerticalPanel.setWidth(Integer.toString(textArea.getOffsetHeight()) + "px");
		
		// Horizontal Menu And Status Panel
		statusPanel.setWidth("200px");
		status.setHeight("12px");
		menuAndStatusPanel.add(menu);
		menuAndStatusPanel.add(statusPanel);
		menuAndStatusPanel.setCellHorizontalAlignment(menu, HasHorizontalAlignment.ALIGN_LEFT);
		menuAndStatusPanel.setCellHorizontalAlignment(statusPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		menuAndStatusPanel.setSpacing(3);
		menuAndStatusPanel.setHeight("35px");
		menuAndStatusPanel.setWidth("100%");
		
		// Button Panel
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setWidth("100%");
		buttonPanel.setSpacing(3);
		buttonPanel.add(parseButton);
		buttonPanel.add(submitButton);
		buttonPanel.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setCellHorizontalAlignment(parseButton, HasHorizontalAlignment.ALIGN_RIGHT);
		
		// Adding widgets to vertical panel
		inputVerticalPanel.add(menuAndStatusPanel);
		inputVerticalPanel.add(textArea);
		inputVerticalPanel.add(buttonPanel);
		mainVerticalPanel.add(inputVerticalPanel);
		mainVerticalPanel.add(resultTabPanel);
		
		// Configure vertical panel
		mainVerticalPanel.setSpacing(3);
		inputVerticalPanel.setSpacing(3);

		// Adding widget(s) to root panel
		RootPanel.get("verticalPanel").add(mainVerticalPanel);
		
		// Focus the cursor on the text area when the application loads
		textArea.setFocus(true);
		textArea.selectAll();
	}

	// Methods
	
	private void configureSakaiParentIframe() {
		
		// Resize parent Sakai iframe
		Document doc = getWindowParentDocument();
		NodeList<Element> nodeList = doc.getElementsByTagName("iframe");
		for(int i = 0; i < nodeList.getLength(); i++) {
			IFrameElement iframe = (IFrameElement) nodeList.getItem(i);
			if(iframe.getId().startsWith("Main")) {
				iframe.setAttribute("style", "height: 620px;");
				break;
			}
		}
	}
	
	private void beforeSubmit() {
		
		submitButton.setEnabled(false);

		// Reset panels except the history one
		outputFlowPanel.clear();
		resultFlowPanel.clear();
		stackTraceFlowPanel.clear();
		consoleFlowPanel.clear();
		resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);
	}
	
	private ClickHandler getParseClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				String sourceCode = textArea.getText();
				groovyShellService.parse(sourceCode, getSecureToken(), parseAsyncCallback);
			}
		};
	}
	
	private ClickHandler getSubmitClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				beforeSubmit();
				String sourceCode = textArea.getText();
				groovyShellService.submit(sourceCode, getSecureToken(), submitAsyncCallback);
			}
		};
	}
	
	private Command getMenuFileNewCommand() {
		return new Command() {
			public void execute() {
				textArea.setText("");
				groovyShellService.initAutoSave(getSecureToken(), initAutoSaveAsyncCallback);
			}
		};
	}
	
	private Command getMenuFileInfoCommand() {
		return new Command() {
			public void execute() {
				final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
				dialogBox.setTitle(i18n.dialogText());
				dialogBox.setButtonText(i18n.dialogCloseButton());
				dialogBox.addContent(new HTML(i18n.dialogInfoTitle()));
				dialogBox.addContent(new HTML(i18n.dialogInfoContent()));
				dialogBox.addButtonClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						dialogBox.hide();
					}
				});
				dialogBox.center();
				dialogBox.show();
			}
		};
	}
	
	private Command getMenuFileSaveCommand() {
		return new Command() {
			public void execute() {
				final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
				dialogBox.setTitle(i18n.dialogText());
				dialogBox.setButtonText(i18n.dialogCloseButton());
				dialogBox.addContent(new HTML("<b>Not implemented yet</b>"));
				dialogBox.center();
				dialogBox.show();
			}
		};
	}
	
	private Timer getAutoSaveTimer() {
		return new Timer() {

			@Override
			public void run() {
				status.setHTML("Auto Saving ...");
				groovyShellService.save(autoSaveUuid, null, textArea.getText(), ActionType.AUTO_SAVE, getSecureToken(), saveAsyncCallback);
			}
		};
	}
	
	private AsyncCallback<SaveResult> getSaveAsyncCallback() {
		
		return new AsyncCallback<SaveResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML("GWT RPC ERROR: save(...)"));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(SaveResult result) {
				
				checkResult(result, null);
				
				if(null != result.getResult() && !"".equals(result.getResult())) {
					consoleFlowPanel.add(new HTML(result.getResult()));
					status.setHTML("Error ocured during auto save ...");
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
				
				statusTimer = new Timer() {
					@Override
					public void run() {
						status.setHTML("");
					}
				};
				statusTimer.schedule(autoSaveStatusChange);
			}
		};
	}
	
	private AsyncCallback<LatestScriptResult> getLatestScriptAsyncCallback() {
		return new AsyncCallback<LatestScriptResult> () {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML("GWT RPC ERROR: getLatestScript(...)"));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(LatestScriptResult result) {
				
				checkResult(result, null);
				
				// In case there is NO previous script that this user has created, we start a new one
				if(!result.getHasScript()) {
					groovyShellService.initAutoSave(getSecureToken(), initAutoSaveAsyncCallback);
				}
				else {
					autoSaveUuid = result.getScriptUuid();
					textArea.setText(result.getScript());
				}
			}
		};
	}
	
	private AsyncCallback<InitAutoSaveResult> getInitAutoSaveAsyncCallback() {
		return new AsyncCallback<InitAutoSaveResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML("GWT RPC ERROR: initAutoSave(...)"));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(InitAutoSaveResult result) {
				
				checkResult(result, null);
				
				autoSaveUuid = result.getScriptUuid();
				
				if(null == autoSaveUuid || "".equals(autoSaveUuid)) {
					
					consoleFlowPanel.add(new HTML("ERROR: initAutoSaveAsyncCallback() : autoSaveUuid is null or the empty string"));
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
			}
		};
	}
	
	private AsyncCallback<ScriptParseResult> getParseAsyncCallback() {
		return new AsyncCallback<ScriptParseResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML("GWT RPC ERROR: parse(...)"));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(ScriptParseResult result) {

				checkResult(result, null);
				
				String stackTrace = result.getStackTrace();
				if(null == stackTrace || "".equals(stackTrace)) {
					consoleFlowPanel.add(new HTML("PARSE Result: OK"));
				}
				else {
					consoleFlowPanel.add(new HTML(result.getStackTrace()));
				}
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
		};
	}
	
	
	private AsyncCallback<ScriptExecutionResult> getSubmitAsyncCallback() {
		return new AsyncCallback<ScriptExecutionResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML("GWT RPC ERROR: submit(...)"));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				submitButton.setEnabled(true);
			}

			public void onSuccess(ScriptExecutionResult result) {
				
				checkResult(result, new ErrorAction() {
					public void run() {
						submitButton.setEnabled(true);
					}
				});
				
				// History
				historyFlowPanel.insert(new HTML("STACK TRACE: " + result.getStackTrace()), 0);
				historyFlowPanel.insert(new HTML("RESULT: " + result.getResult()), 0);
				historyFlowPanel.insert(new HTML("OUTPUT: " + result.getOutput()), 0);
				historyFlowPanel.insert(new HTML("<br />==== " + new Date().toString() + " ===="), 0);

				if(null != result.getOutput() && !"".equals(result.getOutput())) {
					outputFlowPanel.add(new HTML(result.getOutput()));
				}

				if(null != result.getResult() && !"".equals(result.getResult())) {
					resultFlowPanel.add(new HTML(result.getResult()));
				}

				if(null != result.getStackTrace() && !"".equals(result.getStackTrace())) {
					stackTraceFlowPanel.add(new HTML(result.getStackTrace()));
				}
				
				// Select first panel with data
				if(outputFlowPanel.getWidgetCount() > 0) { 
					resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);
				}
				else if(resultFlowPanel.getWidgetCount() > 0) {
					resultTabPanel.selectTab(TabbedPanel.RESULT.position);
				}
				else if(stackTraceFlowPanel.getWidgetCount() > 0) {
					resultTabPanel.selectTab(TabbedPanel.STACK_TRACE.position);
				}
				
				// Enabling the button again
				submitButton.setEnabled(true);
			}
		};
	}
	
	protected void checkResult(AsyncCallbackResult asyncCallbackResult, ErrorAction errorAction) {
		
		if(null == asyncCallbackResult) {
			consoleFlowPanel.add(new HTML("ERROR: Server encountere a security issue : " + new Date().toString()));
			resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			
			if(null != errorAction) {
				errorAction.run();
			}
			
			return;
		}
	}
	
	private String getSecureToken() {
		return Cookies.getCookie("JSESSIONID");
	}
	
	// JSNI
	private native Document getWindowParentDocument() /*-{
		return $wnd.parent.document
	}-*/;
	
}
