/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class Filter extends TemplateFilter {

	protected FilterAction action;
	
	public Filter() {
		super();
		action = new FilterAction(0, true);
	}

	public Filter(String templateFilterName, String sType, String description,
			String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, TemplateFilterType type) {
		super(templateFilterName, sType, description, templateFilterRepresentation, slots, type);
		action = new FilterAction(0, true);
	}

	@XmlElement(name="action")
	public FilterAction getAction() {
		return action;
	}

	public void setAction(FilterAction action) {
		this.action = action;
	}
}
