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
import org.sakaiproject.sgs2.client.async.result.AsyncCallbackResult;
import org.sakaiproject.sgs2.client.async.result.FavoriteResult;
import org.sakaiproject.sgs2.client.async.result.InitAutoSaveResult;
import org.sakaiproject.sgs2.client.async.result.LatestScriptResult;
import org.sakaiproject.sgs2.client.async.result.MarkAsFavoriteResult;
import org.sakaiproject.sgs2.client.async.result.SaveResult;
import org.sakaiproject.sgs2.client.async.result.ScriptExecutionResult;
import org.sakaiproject.sgs2.client.async.result.ScriptParseResult;
import org.sakaiproject.sgs2.client.async.result.ScriptResult;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
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
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;


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
	private CheckBox autoSaveConfigCheckBox = null;
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

	private TabPanel resultTabPanel = null;
	
	private MenuBar menu = null;
	private MenuBar fileMenu = null;
	private MenuBar editMenu = null;
	private Sgs2MenuBar scriptsMenu = null;
	
	private HTML status = null;
	
	private String scriptUuid = null;
	
	private FlexTable statusFlexTable = null;
	private FlexTable buttonFlexTable =  null;
	
	// I18N
	I18nConstants i18nC = null;
	I18nMessages i18nM = null;
		
	// Timers
	private Timer autoSaveTimer = null;
	private Timer statusAutoSaveTimer = null;
	private Timer statusSaveTimer = null;
	
	// Constructor
	public Sgs2() {
		
		// This is a reference for the declared servlet in Sgs2.gwt.xml
		((ServiceDefTarget) groovyShellService).setServiceEntryPoint(GWT.getModuleBaseURL() + "shell");
		
		// Fix Sakai parent iFrame height
		configureSakaiParentIframe(AppConstants.SAKAI_PARENT_IFRAME_HEIGHT);
		
		i18nC = GWT.create(I18nConstants.class);
		i18nM = GWT.create(I18nMessages.class);
		
		mainVerticalPanel = new VerticalPanel();
		inputVerticalPanel = new VerticalPanel();
		menuAndStatusPanel = new HorizontalPanel();
		statusPanel = new VerticalPanel();
		
		status = new HTML("");
		
		scriptLabel = new Label(i18nC.scriptLabel());
		scriptName = new Label("");
		
		autoSaveConfigCheckBox = new CheckBox(i18nC.autoSaveConfigLabel());
		
		textArea = new TextArea();
		
		runButton = new Button(i18nC.runButton());
		parseButton = new Button(i18nC.parseButton());
		clearButton = new Button(i18nC.clearButton());
		
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
		
		statusFlexTable = new FlexTable();
		buttonFlexTable = new FlexTable();
	}
	
	public void onModuleLoad() {
		
		// Get favorite scripts
		groovyShellService.getFavorite(getSecureToken(), getFavoriteAsyncCallback());
		
		// Get latest script
		groovyShellService.getLatestScript(getSecureToken(), getLatestScriptAsyncCallback());
				
		// Setup Auto Save Timer
		autoSaveConfigCheckBox.setValue(Boolean.TRUE);
		autoSaveConfigCheckBox.addClickHandler(getAutoSaveConfigClickHandler());
		autoSaveTimer = getAutoSaveTimer();
		autoSaveTimer.scheduleRepeating(AppConstants.AUTO_SAVE_INTERVAL);
		
		// Configure Menus
		fileMenu.setAnimationEnabled(true);
		editMenu.setAnimationEnabled(true);
		scriptsMenu.setAnimationEnabled(true);
		
		// File Menu items
		fileMenu.addItem(i18nC.fileMenuNew(), getMenuFileNewCommand());
		fileMenu.addItem(i18nC.fileMenuSave(), getMenuFileSaveCommand());
		fileMenu.addItem(i18nC.fileMenuSaveAs(), getMenuFileSaveAsCommand());
		fileMenu.addItem(i18nC.fileMenuInfo(), getMenuFileInfoCommand());
		
		// Edit Menu items
		editMenu.addItem(i18nC.editMenuAddToScriptsMenu(), getMenuEditAddToScriptsMenu());
		
		// Scripts Menu items		
		scriptsMenu.addItem(i18nC.scriptsMenuCookies(), getMenuScriptsCookies());
	
		// Make a new menu bar, adding a few cascading menus to it.
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(true);
	    menu.addItem(i18nC.mainMenuFile(), fileMenu);
	    menu.addItem(i18nC.mainMenuEdit(), editMenu);
	    menu.addItem(i18nC.mainMenuScripts(), scriptsMenu);
		
		// Attach and configure widgets
		statusPanel.add(status);
		
		resultTabPanel.add(outputFlowPanel, i18nC.executionOutput());
		resultTabPanel.add(resultFlowPanel, i18nC.executionResult());
		resultTabPanel.add(stackTraceFlowPanel, i18nC.executionStackTrace());
		resultTabPanel.add(historyFlowPanel, i18nC.executionHistory());
		resultTabPanel.add(consoleFlowPanel, i18nC.executionConsole());
		resultTabPanel.setAnimationEnabled(true);
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
		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.add(statusPanel);
		menuAndStatusPanel.add(decoratorPanel);
		menuAndStatusPanel.setCellHorizontalAlignment(menu, HorizontalPanel.ALIGN_LEFT);
		menuAndStatusPanel.setCellHorizontalAlignment(decoratorPanel, HorizontalPanel.ALIGN_RIGHT);
		menuAndStatusPanel.setSpacing(3);
		menuAndStatusPanel.setHeight("35px");
		menuAndStatusPanel.setWidth("100%");
		
		// Button Panel
		buttonFlexTable.setWidth("100%");
		FlexCellFormatter buttonCellFormatter = buttonFlexTable.getFlexCellFormatter();
		buttonCellFormatter.setWidth(0, 1, "70px");
		buttonCellFormatter.setWidth(0, 2, "70px");
		buttonCellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonCellFormatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonCellFormatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		clearButton.setWidth("60px");
		parseButton.setWidth("60px");
		runButton.setWidth("60px");
		buttonFlexTable.setWidget(0, 0, clearButton);
		buttonFlexTable.setWidget(0, 1, parseButton);
		buttonFlexTable.setWidget(0, 2, runButton);
		
		// Adding widgets to vertical panel
		inputVerticalPanel.add(menuAndStatusPanel);

		statusFlexTable.setWidth("100%");
		FlexCellFormatter statusCellFormatter = statusFlexTable.getFlexCellFormatter();
		statusCellFormatter.setWidth(0, 0, "50px");
		statusCellFormatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		statusFlexTable.setWidget(0, 0, scriptLabel);
		statusFlexTable.setWidget(0, 1, scriptName);
		statusFlexTable.setWidget(0, 2, autoSaveConfigCheckBox);
		
		inputVerticalPanel.setWidth("100%");
		inputVerticalPanel.add(statusFlexTable);
		inputVerticalPanel.add(textArea);
		inputVerticalPanel.add(buttonFlexTable);
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
	
	private void configureSakaiParentIframe(int setHeight) {
		
		// Resize parent Sakai iframe
		Document doc = getWindowParentDocument();
		NodeList<Element> nodeList = doc.getElementsByTagName("iframe");
		for(int i = 0; i < nodeList.getLength(); i++) {
			IFrameElement iframe = (IFrameElement) nodeList.getItem(i);
			if(iframe.getId().startsWith("Main")) {
				iframe.setAttribute("height", setHeight + "px");
				iframe.setAttribute("style", "height: " + setHeight + "px;");
				iframe.getStyle().setPropertyPx("height", setHeight);
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
	
	private ClickHandler getAutoSaveConfigClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				boolean isChecked = ((CheckBox)event.getSource()).getValue().booleanValue();
				if(isChecked) {
					if(null == autoSaveTimer) {
						addConsoleMessage(i18nC.autoSaveConfigEnabled());
						autoSaveTimer = getAutoSaveTimer();
						autoSaveTimer.scheduleRepeating(AppConstants.AUTO_SAVE_INTERVAL);
					}
				}
				else {
					if(null != autoSaveTimer) {
						addConsoleMessage(i18nC.autoSaveConfigDisabled());
						autoSaveTimer.cancel();
						autoSaveTimer = null;
					}
				}
			}
		};
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
	
	private Command getMenuScriptsCookies() {
		return new Command() {
			public void execute() {
				Collection<String> cookyNames = Cookies.getCookieNames();
				if(cookyNames.size() == 0) {
					consoleFlowPanel.add(new HTML(i18nC.commandCookiesMsg1() + new Date().toString()));
				}
				else {
					consoleFlowPanel.add(new HTML(i18nC.commandCookiesMsg2() + new Date().toString()));
					for(String cookyName : cookyNames) {
						consoleFlowPanel.add(new HTML(i18nC.commandCookiesName() + cookyName + "<br/>"+ i18nC.commandCookiesValue() + Cookies.getCookie(cookyName)));
					}
				}
				resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
			}
		};
	}
	
	private Command getMenuEditAddToScriptsMenu() {
		return new Command() {
			public void execute() {
				if(null == scriptName || "".equals(scriptName.getText())) {
					final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
					dialogBox.setTitle(i18nC.dialogText());
					dialogBox.setButtonText(i18nC.dialogCloseButton());
					dialogBox.addContent(new HTML(i18nC.commandEditAddToScripts()));
					dialogBox.center();
					dialogBox.show();
				}
				else {
					groovyShellService.markAsFavorite(scriptUuid, scriptName.getText(), getSecureToken(), getMarkAsFavoriteAsyncCallback());
				}
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
				dialogBox.setTitle(i18nC.dialogText());
				dialogBox.setButtonText(i18nC.dialogCloseButton());
				dialogBox.addContent(new HTML(i18nC.dialogInfoTitle()));
				dialogBox.addContent(new HTML(i18nC.dialogInfoContentAuthor()));
				dialogBox.addContent(new HTML(i18nC.dialogInfoContentUrl()));
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
				dialogBox.setTitle(i18nC.dialogText());
				dialogBox.setButtonText(i18nC.dialogCancelButton());
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
				horizontalPanel.setSpacing(3);
				horizontalPanel.add(new Label(i18nC.dialogSaveName()));
				final TextBox textBox = new TextBox();
				textBox.setText((null != scriptName ? scriptName.getText() : ""));
				textBox.setMaxLength(AppConstants.SCRIPT_NAME_LENGTH);
				horizontalPanel.add(textBox);
				dialogBox.addContent(horizontalPanel);
				dialogBox.addButton(i18nC.dialogSaveButton(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						String name = textBox.getText();
						// Check input name legnth < 255
						if(name.length() > AppConstants.SCRIPT_NAME_LENGTH) {
							displayErrorDialog(i18nC.dialogErrorMsg());
						}
						else {
							if(null != name && !"".equals(name)) {
								groovyShellService.saveAs(scriptUuid, name, textArea.getText(), ActionType.USER_SAVE_AS, getSecureToken(), getSaveAsAsyncCallback());
								dialogBox.hide();
							}
							else {
								addConsoleMessage(i18nC.commandSaveAsMsg());
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
				status.setHTML(i18nC.timerAutoSave());
				groovyShellService.autoSave(scriptUuid, textArea.getText(), ActionType.AUTO_SAVE, getSecureToken(), getAutoSaveAsyncCallback());
			}
		};
	}
	
	private AsyncCallback<FavoriteResult> getFavoriteAsyncCallback() {
		return new AsyncCallback<FavoriteResult>() {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
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
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(ScriptResult result) {
				checkResult(result, null);

				scriptName.setText(result.getName());
				textArea.setText(result.getScript());
				textArea.setFocus(true);
			}
		};
	}
	
	private AsyncCallback<MarkAsFavoriteResult> getMarkAsFavoriteAsyncCallback() {
		return new AsyncCallback<MarkAsFavoriteResult>() {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(final MarkAsFavoriteResult result) {
				checkResult(result, null);

				if(menuBarHasName(scriptsMenu, result.getName())) {
					addConsoleMessage(i18nM.asyncCallbackMarkAsFavorite(result.getName()));
				}
				else {
					scriptsMenu.addItem(result.getName(), new Command() {
						public void execute() {
							groovyShellService.getScript(result.getName(), getSecureToken(), getScriptResultAsyncCallback());
						}
					});

					addConsoleMessage(i18nM.asyncCallbackMarkAsFavorite(result.getName()));
				}
			}
		};
	}

	private AsyncCallback<SaveResult> getSaveAsyncCallback() {
		return new AsyncCallback<SaveResult>() {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(SaveResult result) {
				checkResult(result, null);

				status.setHTML(i18nC.asyncCallbackSave());
				statusSaveTimer = new Timer() {
					@Override
					public void run() {
						status.setHTML("");
					}
				};
				statusSaveTimer.schedule(AppConstants.AUTO_SAVE_STATUS_CHANGE);
			}
		};
	}
	
	private AsyncCallback<SaveResult> getAutoSaveAsyncCallback() {
		return new AsyncCallback<SaveResult>() {

			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(SaveResult result) {
				checkResult(result, null);
				statusAutoSaveTimer = new Timer() {
					@Override
					public void run() {
						status.setHTML("");
					}
				};
				statusAutoSaveTimer.schedule(AppConstants.AUTO_SAVE_STATUS_CHANGE);
			}			
		};
	}

	private AsyncCallback<SaveResult> getSaveAsAsyncCallback() {

		return new AsyncCallback<SaveResult>() {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(SaveResult result) {

				checkResult(result, null);
				
				if(result.getNameExists()) {
					final Sgs2DialogBox dialogBox = new Sgs2DialogBox();
					dialogBox.setTitle(i18nC.dialogText());
					dialogBox.setButtonText(i18nC.dialogCloseButton());
					dialogBox.addContent(new HTML(i18nC.asyncCallbackSaveAsMsg1()));
					dialogBox.center();
					dialogBox.show();
				}
				else {
					addConsoleMessage(i18nC.asyncCallbackSaveAsMsg2());
					scriptName.setText(result.getName());
				}
			}
		};
	}
	
	private AsyncCallback<LatestScriptResult> getLatestScriptAsyncCallback() {
		return new AsyncCallback<LatestScriptResult> () {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
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
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(InitAutoSaveResult result) {
				
				checkResult(result, null);
				
				scriptUuid = result.getScriptUuid();
				
				if(null == scriptUuid || "".equals(scriptUuid)) {
					
					addConsoleMessage(i18nC.asyncCallbackInitAutoSaveMsg());
				}
			}
		};
	}
	
	private AsyncCallback<ScriptParseResult> getParseAsyncCallback() {
		return new AsyncCallback<ScriptParseResult>() {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
			}
			public void onSuccess(ScriptParseResult result) {

				checkResult(result, null);
				
				String stackTrace = result.getStackTrace();
				if(null == stackTrace || "".equals(stackTrace)) {
					addConsoleMessage(i18nC.asyncCallbackParseMsg());
				}
				else {
					addConsoleMessage(result.getStackTrace());
				}
			}
		};
	}
	
	
	private AsyncCallback<ScriptExecutionResult> getRunAsyncCallback() {
		return new AsyncCallback<ScriptExecutionResult>() {
			public void onFailure(Throwable caught) {
				addConsoleMessage(caught.getMessage());
				runButton.setEnabled(true);
			}

			public void onSuccess(ScriptExecutionResult result) {
				
				checkResult(result, new ErrorAction() {
					public void run() {
						runButton.setEnabled(true);
					}
				});
				
				// History
				historyFlowPanel.insert(new HTML(i18nC.historyStackTrace() + result.getStackTrace()), 0);
				historyFlowPanel.insert(new HTML(i18nC.historyResult() + result.getResult()), 0);
				historyFlowPanel.insert(new HTML(i18nC.historyOutput() + result.getOutput()), 0);
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
			addConsoleMessage(i18nC.checkResultMsg() + new Date().toString());
			
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
		dialogBox.setTitle(i18nC.dialogTextError());
		dialogBox.setButtonText(i18nC.dialogCloseButton());
		dialogBox.addContent(new HTML(errorMessage));
		dialogBox.center();
		dialogBox.show();
	}
	
	private String getSecureToken() {
		return Cookies.getCookie(AppConstants.SECURE_TOKEN_NAME);
	}
	
	private void addConsoleMessage(String message) {
		consoleFlowPanel.add(new HTML(message));
		resultTabPanel.selectTab(TabbedPanel.CONSOLE.position);
	}
	
	// JSNI
	private native Document getWindowParentDocument() /*-{
		return $wnd.parent.document
	}-*/;
	
//	private native boolean matches(String regExp, String value) /*-{
//		var pattern = new RegExp(regExp);
//		return value.search(pattern) != -1;
//	}-*/;
	
}
