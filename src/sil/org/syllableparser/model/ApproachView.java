// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */

public class ApproachView {
	
	private final StringProperty viewName;
	private final StringProperty viewHandler;
	
	/**
     * Default constructor.
     */
    public ApproachView() {
        this(null, null);
    }
    
    /**
     * Constructor with some initial data.
     * 
     * @param viewName
     * @param viewHandler
     */
    public ApproachView(String viewName, String viewHandler) {
		super();
		this.viewName = new SimpleStringProperty(viewName);
		this.viewHandler = new SimpleStringProperty(viewHandler);
	}

	public String getViewName() {
		return viewName.get();
	}

	public void setViewName(String viewName) {
		this.viewName.set(viewName);
	}

	public String getViewHandler() {
		return viewHandler.get();
	}

	public void setViewHandler(String viewHandler) {
		this.viewHandler.set(viewHandler);
	}

	@Override
	public String toString() {
		return getViewName();
	}
}
