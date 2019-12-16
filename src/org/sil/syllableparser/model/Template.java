/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class Template extends TemplateFilter {

	public Template() {
		super();
	}

	public Template(String templateFilterName, String sType, String description,
			String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, TemplateFilterType type) {
		super(templateFilterName, sType, description, templateFilterRepresentation, slots, type);
	}

}
