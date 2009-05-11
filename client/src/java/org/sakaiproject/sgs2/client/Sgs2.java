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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sgs2 implements EntryPoint {

	// GWT RPC proxy
	private GroovyShellServiceAsync groovyShellService = GWT.create(GroovyShellService.class);


	public void onModuleLoad() {

		// This is a reference for the declared servlet in Sgs2.gwt.xml
		((ServiceDefTarget) groovyShellService).setServiceEntryPoint(GWT.getModuleBaseURL() + "rpc/sgs2");

		// Layout
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("700px");
		
		// Widgets
		final TextArea textArea = new TextArea();
		final Button submitButton = new Button("Submit");
		
		// Handlers
		ClickHandler clickHandler = new ClickHandler() {

			public void onClick(ClickEvent event) {

				submitButton.setEnabled(false);
				String sourceCode = textArea.getText();

				groovyShellService.submit(sourceCode, new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						Window.alert("onFailure: groovyShellService.submit(...)");
						submitButton.setEnabled(true);
					}

					public void onSuccess(String result) {
						Window.alert("onSuccess: result = " + result);
						submitButton.setEnabled(true);
					}
				});
			}
		};
		
		submitButton.addClickHandler(clickHandler);
				
		// CSS
		submitButton.addStyleName("submitButton");

		// Text Area Configuration
		textArea.setWidth("100%");
		textArea.setVisibleLines(25);
		
		// Adding widgets to vertical panel
		verticalPanel.add(textArea);
		verticalPanel.add(submitButton);
		
		// Configure vertical panel
		verticalPanel.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.setSpacing(5);

		// Adding widget(s) to root panel
		RootPanel.get("verticalPanel").add(verticalPanel);
		
		// Focus the cursor on the text area when the application loads
		textArea.setFocus(true);
		textArea.selectAll();
	}
}
