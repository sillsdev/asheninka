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
	protected FilterType templateFilterType = FilterType.SYLLABLE;
	
	public Filter() {
		super();
		action = new FilterAction(0, true);
	}

	public Filter(String templateFilterName, String sType, String description,
			String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, FilterType type) {
		super(templateFilterName, sType, description, templateFilterRepresentation, slots);
		templateFilterType = type;
		action = new FilterAction(0, true);
	}

	@XmlElement(name="action")
	public FilterAction getAction() {
		return action;
	}

	public void setAction(FilterAction action) {
		this.action = action;
	}

	public FilterType getTemplateFilterType() {
		return templateFilterType;
	}

	public void setTemplateFilterType(FilterType templateFilterType) {
		this.templateFilterType = templateFilterType;
	}

	@Override
	public int hashCode() {
		String sCombo = templateFilterName.getValueSafe() + templateFilterRepresentation.getValueSafe();
		return sCombo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		boolean result = true;
		Filter tf = (Filter) obj;
		if (!getTemplateFilterName().equals(tf.getTemplateFilterName())) {
			result = false;
		} else {
			if (!getTemplateFilterRepresentation().equals(tf.getTemplateFilterRepresentation())) {
				result = false;
			} else {
				if (!getTemplateFilterType().equals(tf.getTemplateFilterType())) {
					result = false;
				}
			}
		}
		return result;
	}
}
