package org.sakaiproject.sgs2.client.ui.widget;

import org.sakaiproject.sgs2.client.I18nConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Sgs2DialogBox extends DialogBox {

	private Button button = null;
	private VerticalPanel mainVerticalPanel = null;
	private VerticalPanel verticalPanel = null;
	private HorizontalPanel horizontalPanel = null;
	private HandlerRegistration handlerRegistration = null;
	
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
		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setWidth("100%");
		horizontalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		horizontalPanel.add(button);
		mainVerticalPanel.add(horizontalPanel);
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
}
