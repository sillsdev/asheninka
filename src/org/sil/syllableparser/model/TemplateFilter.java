// Copyright (c) 2019-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */package org.sil.syllableparser.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * an Entity
 */
public class TemplateFilter extends SylParserObject {

	protected final StringProperty templateFilterName;
	protected final StringProperty type;
	protected final StringProperty description;
	protected final StringProperty templateFilterRepresentation;
	protected SimpleListProperty<TemplateFilterSlotSegmentOrNaturalClass> segmentsAndNaturalClasses =
			new SimpleListProperty<TemplateFilterSlotSegmentOrNaturalClass>();
	protected ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots = FXCollections.observableArrayList();
	protected boolean valid = false;
	protected final String ksClassOpen = "[";
	protected final String ksClassClose = "]";
	protected final String ksOptionalOpen = "(";
	protected final String ksOptionalClose = ")";
	protected final String ksSpace = " ";

	public TemplateFilter() {
		super();
		this.templateFilterName = new SimpleStringProperty("");
		this.type = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.templateFilterRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public TemplateFilter(String templateFilterName, String sType, String description, String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots) {
		super();
		this.templateFilterName = new SimpleStringProperty(templateFilterName);
		this.type = new SimpleStringProperty(sType);
		this.description = new SimpleStringProperty(description);
		this.templateFilterRepresentation = new SimpleStringProperty(templateFilterRepresentation);
		this.slots = slots;
		createUUID();
	}

	public String getTemplateFilterName() {
		return templateFilterName.get();
	}

	public StringProperty templateFilterNameProperty() {
		return templateFilterName;
	}

	public void setTemplateFilterName(String templateFilterName) {
		this.templateFilterName.set(templateFilterName);
	}

	public String getType() {
		return type.get();
	}

	public StringProperty typeProperty() {
		return type;
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public String getTemplateFilterRepresentation() {
		return templateFilterRepresentation.get();
	}

	public StringProperty templateFilterRepresentationProperty() {
		return templateFilterRepresentation;
	}

	public void setTemplateFilterRepresentation(String templateFilterRepresentation) {
		this.templateFilterRepresentation.set(templateFilterRepresentation);
	}

	/**
	 * @return
	 */
	public StringProperty templateFilterProperty() {
		return this.templateFilterName;
	}

	public ObservableList<TemplateFilterSlotSegmentOrNaturalClass> getSegmentsAndNaturalClasses() {
		return segmentsAndNaturalClasses;
	}

	public SimpleListProperty<TemplateFilterSlotSegmentOrNaturalClass> getSegmentsAndNaturalClassesProperty() {
		return segmentsAndNaturalClasses;
	}

	public void setSegmentsAndNaturalClasses(ObservableList<TemplateFilterSlotSegmentOrNaturalClass> segmentsAndNaturalClasses) {
		this.segmentsAndNaturalClasses.set(segmentsAndNaturalClasses);
	}
	
	public ObservableList<TemplateFilterSlotSegmentOrNaturalClass> getSlots() {
		return slots;
	}

	@XmlElementWrapper(name = "slots")
	@XmlElement(name="slot")
	public void setSlots(ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots) {
		this.slots = slots;
	}


	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void rebuildRepresentationFromContext() {
		StringBuilder sb = new StringBuilder();
		rebuildRightContext(sb);
		setTemplateFilterRepresentation(sb.toString());
	}


	protected void rebuildSegmentOrClass(StringBuilder sb,
			TemplateFilterSlotSegmentOrNaturalClass ecgnc) {
		if (ecgnc.isOptional()) {
			sb.append(ksOptionalOpen);
		}
		if (ecgnc.isSegment()) {
			sb.append(ecgnc.getSegmentString());
		} else {
			sb.append(ksClassOpen);
			sb.append(ecgnc.getCVNaturalClass() != null ? ecgnc.getCVNaturalClass()
					.getNCName() : ecgnc.getSegmentString());
			sb.append(ksClassClose);
		}
		if (ecgnc.isOptional()) {
			sb.append(ksOptionalClose);
		}
	}

	protected void rebuildRightContext(StringBuilder sb) {
		for (TemplateFilterSlotSegmentOrNaturalClass ecgnc : slots) {
			sb.append(" ");
			rebuildSegmentOrClass(sb, ecgnc);
		}
	}

	public boolean usesCVNaturalClass(CVNaturalClass gnc) {
		boolean matches = slots.stream().anyMatch(
				ecgnc -> ecgnc.getCVNaturalClass()!= null && ecgnc.getCVNaturalClass().equals(gnc));
		return matches;
	}

	/**
	 * @param selectedValue
	 */
	public  void setTemplateFilterType(TemplateType selectedValue) {
		// sub-classes implement this
	}

	/**
	 * @param selectedValue
	 */
	public void setTemplateFilterType(FilterType selectedValue) {
		// sub-classes implement this
	}

	@Override
	public String getSortingValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(getType());
		sb.append(Constants.SORT_VALUE_DIVIDER);
		sb.append(getTemplateFilterName());
		return sb.toString();
	}
}
