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

package org.sakaiproject.sgs2.client.ui.widget;

import org.sakaiproject.sgs2.client.I18nConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class Sgs2DialogBox extends DialogBox {

	private Button button = null;
	private VerticalPanel mainVerticalPanel = null;
	private VerticalPanel verticalPanel = null;
	private HandlerRegistration handlerRegistration = null;
	private FlexTable buttonFlexTable = null;
	
	// I18N
	I18nConstants i18n = null;
	
	public Sgs2DialogBox() {
		
		i18n = GWT.create(I18nConstants.class);
		
		setText(i18n.dialogText());
		setAnimationEnabled(true);
		mainVerticalPanel = new VerticalPanel();
		button = new Button(i18n.dialogCloseButton());
		button.getElement().setId("closeButton");
		verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("dialogVPanel");
		verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		mainVerticalPanel.add(verticalPanel);
		
		buttonFlexTable = new FlexTable();
		buttonFlexTable.setCellSpacing(3);
		buttonFlexTable.setWidth("100%");
		FlexCellFormatter buttonCellFormatter = buttonFlexTable.getFlexCellFormatter();
		buttonCellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonFlexTable.setWidget(0, 0, button);		
		mainVerticalPanel.add(buttonFlexTable);
		setWidget(mainVerticalPanel);
		handlerRegistration = button.addClickHandler(getButtonClickHandler());
		
	}
	
	private ClickHandler getButtonClickHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		};
	}
	
	public void setTitle(String title) {
		setText(title);
	}
	
	public void setButtonText(String label) {
		button.setText(label);
	}
	
	public void addContent(Widget widget) {
		verticalPanel.add(widget);
	}
	
	public void addButtonClickHandler(ClickHandler clickHandler) {
		handlerRegistration.removeHandler();
		handlerRegistration = button.addClickHandler(clickHandler);
	}
	
	public void addButton(String label, ClickHandler clickHandler) {
		Button newButton = new Button(label);
		newButton.addClickHandler(clickHandler);
		int columnIndex = buttonFlexTable.getCellCount(0);
		FlexCellFormatter buttonCellFormatter = buttonFlexTable.getFlexCellFormatter();
		buttonCellFormatter.setHorizontalAlignment(0, columnIndex, HasHorizontalAlignment.ALIGN_RIGHT);
		buttonCellFormatter.setWidth(0, columnIndex, "50px");
		buttonFlexTable.setWidget(0, columnIndex, newButton);
	}
}
