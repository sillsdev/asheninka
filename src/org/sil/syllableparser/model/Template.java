/**
 * Copyright (c) 2019-2020 SIL International
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

	protected TemplateType templateType = TemplateType.ONSET;

	public Template() {
		super();
	}

	public Template(String templateFilterName, String sType, String description,
			String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, TemplateType type) {
		super(templateFilterName, sType, description, templateFilterRepresentation, slots);
		templateType = type;
	}

	public TemplateType getTemplateFilterType() {
		return templateType;
	}

	public void setTemplateFilterType(TemplateType templateType) {
		this.templateType = templateType;
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
		Template tf = (Template) obj;
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
