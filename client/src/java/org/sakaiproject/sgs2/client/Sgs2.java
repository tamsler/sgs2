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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sgs2 implements EntryPoint {

	private enum TabbedPanel { OUTPUT(0) , RESULT(1), STACK_TRACE(3), HISTORY(4), CONSOLE(5);
		
		public int position;
		
		TabbedPanel(int position) {
			
			this.position = position;
		}
	}
	
	// GWT RPC proxy
	private GroovyShellServiceAsync groovyShellService = GWT.create(GroovyShellService.class);

	private AsyncCallback<ScriptExecutionResult> asyncCallback = null;
	private Button submitButton = null;
	
	public void onModuleLoad() {
		
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
		
		// This is a reference for the declared servlet in Sgs2.gwt.xml
		((ServiceDefTarget) groovyShellService).setServiceEntryPoint(GWT.getModuleBaseURL() + "rpc/sgs2");
		
		// Layout
		VerticalPanel mainVerticalPanel = new VerticalPanel();
		VerticalPanel inputVerticalPanel = new VerticalPanel();
		
		// Widgets
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Save", new Command() {
			public void execute() {
				Window.alert("You selected the Save menu item!");
			}
		});

		fileMenu.addItem("Info", new Command() {
			public void execute() {
				Window.alert("You selected the Info menu item!");
			}
		});
		
		MenuBar analyzeMenu = new MenuBar(true);
		analyzeMenu.addItem("Hello World", new Command() {
			public void execute() {
				submitButton.setEnabled(false);
				groovyShellService.submit("println 'Hello World'", asyncCallback);
			}
		});
		analyzeMenu.addItem("4 + 4", new Command() {
			public void execute() {
				submitButton.setEnabled(false);
				groovyShellService.submit("4 + 4", asyncCallback);
			}
		});
		
		// Make a new menu bar, adding a few cascading menus to it.
		MenuBar menu = new MenuBar();
	    menu.addItem("File", fileMenu);
	    menu.addItem("Analyze", analyzeMenu);

		final TextArea textArea = new TextArea();
		
		submitButton = new Button("Submit");
		
		final TabPanel resultTabPanel = new TabPanel();
		
		final FlowPanel outputFlowPanel = new FlowPanel();
		final FlowPanel resultFlowPanel = new FlowPanel();
		final FlowPanel stackTraceFlowPanel = new FlowPanel();
		final FlowPanel historyFlowPanel = new FlowPanel();
		final FlowPanel consoleFlowPanel = new FlowPanel();
		
		resultTabPanel.add(outputFlowPanel, "Output");
		resultTabPanel.add(resultFlowPanel, "Result");
		resultTabPanel.add(stackTraceFlowPanel, "Stack Trace");
		resultTabPanel.add(historyFlowPanel, "History");
		resultTabPanel.add(consoleFlowPanel, "Console");
		
		resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);

		asyncCallback = new AsyncCallback<ScriptExecutionResult>() {

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
		
		// Handlers
		ClickHandler clickHandler = new ClickHandler() {

			public void onClick(ClickEvent event) {

				submitButton.setEnabled(false);
				String sourceCode = textArea.getText();
				
				// Reset panels except the history one
				outputFlowPanel.clear();
				resultFlowPanel.clear();
				stackTraceFlowPanel.clear();
				consoleFlowPanel.clear();
				
				resultTabPanel.selectTab(TabbedPanel.OUTPUT.position);

				groovyShellService.submit(sourceCode, asyncCallback);
			}
		};

		submitButton.addClickHandler(clickHandler);

		// CSS
		submitButton.addStyleName("submitButton");

		// Text Area Configuration
		textArea.setCharacterWidth(80);
		textArea.setVisibleLines(25);
		
		// Tab Panel Configuration
		resultTabPanel.setWidth("100%");
		inputVerticalPanel.setWidth(Integer.toString(textArea.getOffsetHeight()) + "px");
		
		// Adding widgets to vertical panel
		inputVerticalPanel.add(menu);
		inputVerticalPanel.add(textArea);
		inputVerticalPanel.add(submitButton);
		mainVerticalPanel.add(inputVerticalPanel);
		mainVerticalPanel.add(resultTabPanel);
		
		// Configure vertical panel
		mainVerticalPanel.setSpacing(5);
		inputVerticalPanel.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_RIGHT);
		inputVerticalPanel.setSpacing(5);

		// Adding widget(s) to root panel
		RootPanel.get("verticalPanel").add(mainVerticalPanel);
		
		// Focus the cursor on the text area when the application loads
		textArea.setFocus(true);
		textArea.selectAll();
	}

	private native Document getWindowParentDocument() /*-{
	
		return $wnd.parent.document
	}-*/;
	
}
