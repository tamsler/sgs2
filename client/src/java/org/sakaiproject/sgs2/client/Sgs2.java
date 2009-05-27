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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.sakaiproject.sgs2.client.GroovyShellService.ActionType;
import org.sakaiproject.sgs2.client.ui.widget.Sgs2DialogBox;
import org.sakaiproject.sgs2.client.ui.widget.Sgs2MenuBar;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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
	private Label scriptLabel = null;
	private Label scriptName = null;
	private TextArea textArea = null; 
	private Button runButton = null;
	private Button parseButton = null;
	private Button clearButton = null;
	
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
	private HorizontalPanel scriptNamePanel = null;

	private TabPanel resultTabPanel = null;
	
	private MenuBar menu = null;
	private MenuBar fileMenu = null;
	private MenuBar editMenu = null;
	private Sgs2MenuBar scriptsMenu = null;
	
	private HTML status = null;
	
	private String scriptUuid = null;
	
	// One minute
	private int autoSaveInterval = 60000;
	
	// 5 seconds
	private int autoSaveStatusChange = 5000;
	private int saveStatusChange = 5000;
	
	// I18N
	I18nConstants i18n = null;
		
	// Timers
	private Timer autoSaveTimer = null;
	private Timer statusAutoSaveTimer = null;
	private Timer statusSaveTimer = null;
	
	// Constructor
	public Sgs2() {
		
		// This is a reference for the declared servlet in Sgs2.gwt.xml
		((ServiceDefTarget) groovyShellService).setServiceEntryPoint(GWT.getModuleBaseURL() + "rpc/sgs2");
		
		// Fix Sakai parent iFrame height
		configureSakaiParentIframe();
		
		i18n = GWT.create(I18nConstants.class);
		
		mainVerticalPanel = new VerticalPanel();
		inputVerticalPanel = new VerticalPanel();
		menuAndStatusPanel = new HorizontalPanel();
		buttonPanel = new HorizontalPanel();
		statusPanel = new VerticalPanel();
		scriptNamePanel = new HorizontalPanel();
		
		status = new HTML("");
		
		scriptLabel = new Label("Name:");
		scriptName = new Label("");
		
		textArea = new TextArea();
		
		runButton = new Button("Run");
		parseButton = new Button("Parse");
		clearButton = new Button("Clear");
		
		resultTabPanel = new TabPanel();
		
		outputFlowPanel = new FlowPanel();
		resultFlowPanel = new FlowPanel();
		stackTraceFlowPanel = new FlowPanel();
		historyFlowPanel = new FlowPanel();
		consoleFlowPanel = new FlowPanel();
		
		menu = new MenuBar();
		fileMenu = new MenuBar(true);
		editMenu = new MenuBar(true);
		scriptsMenu = new Sgs2MenuBar(true);
	}
	
	public void onModuleLoad() {
		
		// Get favorite scripts
		groovyShellService.getFavorite(getSecureToken(), getFavoriteAsyncCallback());
		
		// Get latest script
		groovyShellService.getLatestScript(getSecureToken(), getLatestScriptAsyncCallback());
				
		// Setup Auto Save Timer
		autoSaveTimer = getAutoSaveTimer();
		autoSaveTimer.scheduleRepeating(autoSaveInterval);
		
		// Configure Menus
		fileMenu.setAnimationEnabled(true);
		editMenu.setAnimationEnabled(true);
		scriptsMenu.setAnimationEnabled(true);
		
		// File Menu items
		fileMenu.addItem("New", getMenuFileNewCommand());
		fileMenu.addItem("Save", getMenuFileSaveCommand());
		fileMenu.addItem("Save As", getMenuFileSaveAsCommand());
		fileMenu.addItem("Info", getMenuFileInfoCommand());
		
		// Edit Menu items
		editMenu.addItem("Add to Scripts Menu", new Command() {
			public void execute() {
				if(null == scriptName || "".equals(scriptName.getText())) {
					final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
					dialogBox.setTitle(i18n.dialogText());
					dialogBox.setButtonText(i18n.dialogCloseButton());
					dialogBox.addContent(new HTML("<i><b>INFO</b></i></br>Name the script by selecting File -> Save As"));
					dialogBox.center();
					dialogBox.show();
				}
				else {
					groovyShellService.markAsFavorite(scriptUuid, scriptName.getText(), getSecureToken(), getMarkAsFavoriteAsyncCallback());
				}
			}
		});
		
		// Scripts Menu items		
		scriptsMenu.addItem("Show Client Cookies", new Command() {
			public void execute() {
				Collection<String> cookyNames = Cookies.getCookieNames();
				if(cookyNames.size() == 0) {
					consoleFlowPanel.add(new HTML("There are no client cookies : " + new Date().toString()));
				}
				else {
					consoleFlowPanel.add(new HTML("List of client cookies : " + new Date().toString()));
					for(String cookyName : cookyNames) {
						consoleFlowPanel.add(new HTML("Name = " + cookyName + "  Value = " + Cookies.getCookie(cookyName)));
					}
				}
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
		});
		
		// Make a new menu bar, adding a few cascading menus to it.
		menu.setAnimationEnabled(true);
	    menu.addItem("File", fileMenu);
	    menu.addItem("Edit", editMenu);
	    menu.addItem("Scripts", scriptsMenu);
		
		// Attach and configure widgets
		statusPanel.add(status);
		
		resultTabPanel.add(outputFlowPanel, "Output");
		resultTabPanel.add(resultFlowPanel, "Result");
		resultTabPanel.add(stackTraceFlowPanel, "Stack Trace");
		resultTabPanel.add(historyFlowPanel, "History");
		resultTabPanel.add(consoleFlowPanel, "Console");
		
		resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);
		
		runButton.addClickHandler(getRunClickHandler());
		parseButton.addClickHandler(getParseClickHandler());
		clearButton.addClickHandler(getClearClickHandler());

		// Text Area Configuration
		textArea.setWidth("100%");
		textArea.setVisibleLines(25);
		
		// Tab Panel Configuration
		resultTabPanel.setWidth("100%");
		
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
		buttonPanel.add(clearButton);
		buttonPanel.add(parseButton);
		buttonPanel.add(runButton);
		buttonPanel.setCellHorizontalAlignment(runButton, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setCellHorizontalAlignment(parseButton, HasHorizontalAlignment.ALIGN_RIGHT);
		
		// Adding widgets to vertical panel
		inputVerticalPanel.add(menuAndStatusPanel);
		scriptNamePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		scriptNamePanel.setSpacing(3);
		scriptNamePanel.add(scriptLabel);
		scriptNamePanel.add(scriptName);
		inputVerticalPanel.setWidth("100%");
		inputVerticalPanel.add(scriptNamePanel);
		inputVerticalPanel.add(textArea);
		inputVerticalPanel.add(buttonPanel);
		mainVerticalPanel.setWidth("100%");
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
	
	private void resetPanels() {
		// Reset panels except the history one
		outputFlowPanel.clear();
		resultFlowPanel.clear();
		stackTraceFlowPanel.clear();
		consoleFlowPanel.clear();
		resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);
	}
	
	private void beforeRun() {	
		runButton.setEnabled(false);
		resetPanels();
	}
	
	private ClickHandler getClearClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				textArea.setText("");
				textArea.setFocus(true);
			}
		};
	}
	
	private ClickHandler getParseClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				String sourceCode = textArea.getText();
				groovyShellService.parse(sourceCode, getSecureToken(), getParseAsyncCallback());
			}
		};
	}
	
	private ClickHandler getRunClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				beforeRun();
				String sourceCode = textArea.getText();
				groovyShellService.run(sourceCode, getSecureToken(), getRunAsyncCallback());
			}
		};
	}
	
	private Command getMenuFileNewCommand() {
		return new Command() {
			public void execute() {
				textArea.setText("");
				scriptName.setText("");
				resetPanels();
				groovyShellService.initAutoSave(getSecureToken(), getInitAutoSaveAsyncCallback());
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
				dialogBox.addContent(new HTML(i18n.dialogInfoContentAuthor()));
				dialogBox.addContent(new HTML(i18n.dialogInfoContentUrl()));
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
				groovyShellService.save(scriptUuid, textArea.getText(), ActionType.USER_SAVE, getSecureToken(), getSaveAsyncCallback());
			}
		};
	}
	
	private Command getMenuFileSaveAsCommand() {
		return new Command() {
			public void execute() {
				final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
				dialogBox.setTitle(i18n.dialogText());
				dialogBox.setButtonText(i18n.dialogCancelButton());
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
				horizontalPanel.setSpacing(3);
				horizontalPanel.add(new Label(i18n.dialogSaveName()));
				final TextBox textBox = new TextBox();
				textBox.setMaxLength(254);
				horizontalPanel.add(textBox);
				dialogBox.addContent(horizontalPanel);
				dialogBox.addButton(i18n.dialogSaveButton(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						String name = textBox.getText();
						// Check input name legnth < 255
						if(name.length() > 254) {
							displayErrorDialog("Name length restriction : < 255 characters");
						}
						else {
							if(null != name && !"".equals(name)) {
								groovyShellService.saveAs(scriptUuid, name, textArea.getText(), ActionType.USER_SAVE_AS, getSecureToken(), getSaveAsAsyncCallback());
								dialogBox.hide();
							}
							else {
								consoleFlowPanel.add(new HTML("WARN: SaveAs : Selected name is either null or empty"));
								resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
							}
						}
					}
				});
				
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
				groovyShellService.autoSave(scriptUuid, textArea.getText(), ActionType.AUTO_SAVE, getSecureToken(), getAutoSaveAsyncCallback());
			}
		};
	}
	
	private AsyncCallback<FavoriteResult> getFavoriteAsyncCallback() {
		return new AsyncCallback<FavoriteResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(FavoriteResult result) {
				Collection<String> scriptNames = result.getFavorite();
				for(final String scriptName : scriptNames) {
					if(!menuBarHasName(scriptsMenu, scriptName)) {
						scriptsMenu.addItem(scriptName, new Command() {
							public void execute() {
								groovyShellService.getScript(scriptName, getSecureToken(), getScriptResultAsyncCallback());
							}
						});
					}
				}
			}
		};
	}
	
	private AsyncCallback<ScriptResult> getScriptResultAsyncCallback() {
		return new AsyncCallback<ScriptResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(ScriptResult result) {
				checkResult(result, null);
				if(null != result.getError() && !"".equals(result.getError())) {
					consoleFlowPanel.add(new HTML("Error ocured during getScript .."));
					consoleFlowPanel.add(new HTML(result.getError()));
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
				else {
					scriptName.setText(result.getName());
					textArea.setText(result.getScript());
					textArea.setFocus(true);
				}
			}
		};
	}
	
	private AsyncCallback<MarkAsFavoriteResult> getMarkAsFavoriteAsyncCallback() {
		return new AsyncCallback<MarkAsFavoriteResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(final MarkAsFavoriteResult result) {
				checkResult(result, null);
				if(null != result.getError() && !"".equals(result.getError())) {
					consoleFlowPanel.add(new HTML("Error ocured during markAsFavorite .."));
					consoleFlowPanel.add(new HTML(result.getError()));
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
				else {
					if(menuBarHasName(scriptsMenu, result.getName())) {
						consoleFlowPanel.add(new HTML("INFO: Script [" + result.getName() + "] already exists in the scripts menu"));
						resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
					}
					else {
						scriptsMenu.addItem(result.getName(), new Command() {
							public void execute() {
								groovyShellService.getScript(result.getName(), getSecureToken(), getScriptResultAsyncCallback());
							}
						});

						consoleFlowPanel.add(new HTML("INFO: Added script [" + result.getName() + "] to the Scripts menu"));
						resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
					}
				}
			}
		};
	}
	
	private AsyncCallback<SaveResult> getSaveAsyncCallback() {
		return new AsyncCallback<SaveResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(SaveResult result) {
				checkResult(result, null);

				if(null != result.getError() && !"".equals(result.getError())) {
					consoleFlowPanel.add(new HTML(result.getError()));
					status.setHTML("Error ocured during save ...");
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
				else {

					status.setHTML("Saved");
					statusSaveTimer = new Timer() {
						@Override
						public void run() {
							status.setHTML("");
						}
					};
					statusSaveTimer.schedule(saveStatusChange);
				}
			}
		};
	}
	
	private AsyncCallback<SaveResult> getAutoSaveAsyncCallback() {
		return new AsyncCallback<SaveResult>() {

			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(SaveResult result) {
				checkResult(result, null);
				
				if(null != result.getError() && !"".equals(result.getError())) {
					consoleFlowPanel.add(new HTML(result.getError()));
					status.setHTML("Error ocured during auto saveAs ...");
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
				else {

					statusAutoSaveTimer = new Timer() {
						@Override
						public void run() {
							status.setHTML("");
						}
					};

					statusAutoSaveTimer.schedule(autoSaveStatusChange);
				}
			}
			
		};
	}

	private AsyncCallback<SaveResult> getSaveAsAsyncCallback() {

		return new AsyncCallback<SaveResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(SaveResult result) {

				checkResult(result, null);

				if(null != result.getError() && !"".equals(result.getError())) {
					consoleFlowPanel.add(new HTML(result.getError()));
					status.setHTML("Error ocured during save ...");
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
				else if(result.getNameExists()) {
					final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
					dialogBox.setTitle(i18n.dialogText());
					dialogBox.setButtonText(i18n.dialogCloseButton());
					dialogBox.addContent(new HTML("<i><b>INFO</b></i></br>Name already exists. Please choose a new name"));
					dialogBox.center();
					dialogBox.show();
				}
				else {
					consoleFlowPanel.add(new HTML("INFO: Source code has been saved"));
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
					scriptName.setText(result.getName());
				}

			}
		};
	}
	
	private AsyncCallback<LatestScriptResult> getLatestScriptAsyncCallback() {
		return new AsyncCallback<LatestScriptResult> () {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(LatestScriptResult result) {
				
				checkResult(result, null);
				
				// In case there is NO previous script that this user has created, we start a new one
				if(!result.getHasScript()) {
					groovyShellService.initAutoSave(getSecureToken(), getInitAutoSaveAsyncCallback());
				}
				else {
					scriptUuid = result.getScriptUuid();
					textArea.setText(result.getScript());
					textArea.setFocus(true);
					scriptName.setText((null == result.getName()) ? "" : result.getName() );
				}
			}
		};
	}
	
	private AsyncCallback<InitAutoSaveResult> getInitAutoSaveAsyncCallback() {
		return new AsyncCallback<InitAutoSaveResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
			public void onSuccess(InitAutoSaveResult result) {
				
				checkResult(result, null);
				
				scriptUuid = result.getScriptUuid();
				
				if(null == scriptUuid || "".equals(scriptUuid)) {
					
					consoleFlowPanel.add(new HTML("ERROR: initAutoSaveAsyncCallback() : autoSaveUuid is null or the empty string"));
					resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				}
			}
		};
	}
	
	private AsyncCallback<ScriptParseResult> getParseAsyncCallback() {
		return new AsyncCallback<ScriptParseResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
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
	
	
	private AsyncCallback<ScriptExecutionResult> getRunAsyncCallback() {
		return new AsyncCallback<ScriptExecutionResult>() {
			public void onFailure(Throwable caught) {
				consoleFlowPanel.add(new HTML(caught.getMessage()));
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
				runButton.setEnabled(true);
			}

			public void onSuccess(ScriptExecutionResult result) {
				
				checkResult(result, new ErrorAction() {
					public void run() {
						runButton.setEnabled(true);
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
				runButton.setEnabled(true);
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

	// Helper methods
	private boolean menuBarHasName(Sgs2MenuBar menuBar, String name) {
	
		List<MenuItem> menuItems = menuBar.getItems();
		for(MenuItem menuItem : menuItems) {
			if(menuItem.getText().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	private void displayErrorDialog(String errorMessage) {
		
		final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
		dialogBox.setTitle(i18n.dialogTextError());
		dialogBox.setButtonText(i18n.dialogCloseButton());
		dialogBox.addContent(new HTML(errorMessage));
		dialogBox.center();
		dialogBox.show();
	}
	
	private String getSecureToken() {
		return Cookies.getCookie("JSESSIONID");
	}
	
	// JSNI
	private native Document getWindowParentDocument() /*-{
		return $wnd.parent.document
	}-*/;
	
}
