/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterAction;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class NPFilter extends Filter {

	protected FilterAction action;
	protected FilterType templateFilterType = FilterType.ONSET;
	
	public NPFilter() {
		super();
		action = new FilterAction(0, false);
	}

	public NPFilter(String templateFilterName, String sType, String description,
			String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, FilterType type) {
		super(templateFilterName, sType, description, templateFilterRepresentation, slots, type);
		action = new FilterAction(0, false);
	}
}
