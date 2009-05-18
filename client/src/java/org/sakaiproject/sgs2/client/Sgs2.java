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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
	
	// GWT RPC proxy
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
	
	// Callbacks
	private AsyncCallback<ScriptExecutionResult> submitAsyncCallback = null;
	private AsyncCallback<ScriptParseResult> parseAsyncCallback = null;
	private AsyncCallback<String> initAutoSaveAsyncCallback = null;
	
	// Commands
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
		
		configureSakaiParentIframe();
		
		submitAsyncCallback = getSubmitAsyncCallback();
		parseAsyncCallback = getParseAsyncCallback();
		initAutoSaveAsyncCallback = getInitAutoSaveAsyncCallback();
		
		menuFileSaveCommand = getMenuFileSaveCommand();
		menuFileInfoCommand = getMenuFileInfoCommand();
		
		submitClickHandler = getSubmitClickHandler();
		parseClickHandler = getParseClickHandler();
	}
	
	public void onModuleLoad() {
		
		// Getting a UUID from the server 
		groovyShellService.initAutoSave(initAutoSaveAsyncCallback);
				
		// Setup Auto Save Timer
		autoSaveTimer = getAutoSaveTimer();
		autoSaveTimer.scheduleRepeating(autoSaveInterval);
		
		// Layouts and Widges
		mainVerticalPanel = new VerticalPanel();
		inputVerticalPanel = new VerticalPanel();
		menuAndStatusPanel = new HorizontalPanel();
		buttonPanel = new HorizontalPanel();
		statusPanel = new VerticalPanel();
		status = new HTML("");
		statusPanel.add(status);
		
		// File Menu items
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Save", menuFileSaveCommand);
		fileMenu.addItem("Info", menuFileInfoCommand);
		
		// Analyze Menu items
		// TODO : Make this like a plugin
		MenuBar analyzeMenu = new MenuBar(true);
		analyzeMenu.addItem("Hello World", new Command() {
			public void execute() {
				beforeSubmit();
				groovyShellService.submit("println 'Hello World'", submitAsyncCallback);
			}
		});
		analyzeMenu.addItem("4 + 4", new Command() {
			public void execute() {
				beforeSubmit();
				groovyShellService.submit("4 + 4", submitAsyncCallback);
			}
		});
		
		// Make a new menu bar, adding a few cascading menus to it.
		MenuBar menu = new MenuBar();
	    menu.addItem("File", fileMenu);
	    menu.addItem("Analyze", analyzeMenu);

		textArea = new TextArea();
		
		submitButton = new Button("Submit");
		parseButton = new Button("Parse");
		
		resultTabPanel = new TabPanel();
		
		outputFlowPanel = new FlowPanel();
		resultFlowPanel = new FlowPanel();
		stackTraceFlowPanel = new FlowPanel();
		historyFlowPanel = new FlowPanel();
		consoleFlowPanel = new FlowPanel();
		
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
				groovyShellService.parse(sourceCode, parseAsyncCallback);
			}
		};
	}
	
	private ClickHandler getSubmitClickHandler() {
		return new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				
				beforeSubmit();
				
				String sourceCode = textArea.getText();
				groovyShellService.submit(sourceCode, submitAsyncCallback);
			}
		};
	}
	
	private Command getMenuFileInfoCommand() {
		return new Command() {
			public void execute() {
				// TODO
				Window.alert("You selected the Info menu item!");
			}
		};
	}
	
	private Command getMenuFileSaveCommand() {
		return new Command() {
			public void execute() {
				// TODO
				Window.alert("You selected the Save menu item!");
			}
		};
	}
	
	private Timer getAutoSaveTimer() {
		return new Timer() {

			@Override
			public void run() {
				status.setHTML("Auto Saving ...");
				groovyShellService.autoSave(autoSaveUuid, textArea.getText(), new AsyncCallback<AutoSaveResult>() {
					public void onFailure(Throwable caught) {
						status.setHTML("Auto Saving Error: Server");

					}
					public void onSuccess(AutoSaveResult result) {
						statusTimer = new Timer() {
							@Override
							public void run() {
								status.setHTML("");
							}
						};
						statusTimer.schedule(autoSaveStatusChange);
					}
				});
			}
		};
	}
	
	private AsyncCallback<String> getInitAutoSaveAsyncCallback() {
		return new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				status.setHTML("Auto Saving Error: init");
			}

			public void onSuccess(String result) {
				autoSaveUuid = result;
				GWT.log("initAutoSave uuid = " + result, null);
			}
		};
	}
	
	private AsyncCallback<ScriptParseResult> getParseAsyncCallback() {
		return new AsyncCallback<ScriptParseResult>() {
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
			public void onSuccess(ScriptParseResult result) {
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
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				submitButton.setEnabled(true);
			}

			public void onSuccess(ScriptExecutionResult result) {
				
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
	
	
	// JSNI
	private native Document getWindowParentDocument() /*-{
		return $wnd.parent.document
	}-*/;
	
}
