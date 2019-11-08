// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */package org.sil.syllableparser.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.utility.StringUtilities;

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

	private final StringProperty templateFilterName;
	private final StringProperty type;
	private final StringProperty description;
	private final StringProperty templateFilterRepresentation;
	private SimpleListProperty<TemplateFilterSlotSegmentOrNaturalClass> segmentsAndNaturalClasses = 
			new SimpleListProperty<TemplateFilterSlotSegmentOrNaturalClass>();
	ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots = FXCollections.observableArrayList();
	private TemplateFilterType templateFilterType = TemplateFilterType.SYLLABLE;

	private boolean valid = false;
	private final String ksClassOpen = "[";
	private final String ksClassClose = "]";
	private final String ksOptionalOpen = "(";
	private final String ksOptionalClose = ")";
	private final String ksSpace = " ";

	public TemplateFilter() {
		super();
		this.templateFilterName = new SimpleStringProperty("");
		this.type = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.templateFilterRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public TemplateFilter(String templateFilterName, String sType, String description, String templateFilterRepresentation,
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, TemplateFilterType type) {
		super();
		this.templateFilterName = new SimpleStringProperty(templateFilterName);
		this.type = new SimpleStringProperty(sType);
		this.description = new SimpleStringProperty(description);
		this.templateFilterRepresentation = new SimpleStringProperty(templateFilterRepresentation);
		this.slots = slots;
		templateFilterType = type;
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

	public TemplateFilterType getTemplateFilterType() {
		return templateFilterType;
	}

	public void setTemplateFilterType(TemplateFilterType templateFilterType) {
		this.templateFilterType = templateFilterType;
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

	public boolean matches(String sBefore, String sAfter, List<CVNaturalClass> classes) {
		boolean fMatches = false;
		fMatches = matchDescription(0, slots.size(), sAfter, classes);
		return fMatches;
	}

	private boolean matchDescription(int iItemToTest, int iNumberOfItems, String sStringToMatch,
			List<CVNaturalClass> classes) {
		boolean fMatches = false;
		if (iItemToTest < 0 || iItemToTest >= iNumberOfItems) {
			if (StringUtilities.isNullOrEmpty(sStringToMatch)) {
				fMatches = true;
			}
		} else {
			TemplateFilterSlotSegmentOrNaturalClass gnc = slots.get(iItemToTest);
			if (gnc.isSegment()) {
				String sGrapheme = gnc.getSegmentString();
				if (sStringToMatch.startsWith(sGrapheme)) {
					// have a matching segment, try next one to the right
					int iLen = sGrapheme.length();
					fMatches = matchDescription(iItemToTest + 1, iNumberOfItems,
							sStringToMatch.substring(iLen), classes);
				} else if (gnc.isOptional()) {
					fMatches = matchDescription(iItemToTest + 1, iNumberOfItems, sStringToMatch,
							classes);
				}
			} else {
				// have a grapheme natural class
				final String sClassName = gnc.getSegmentString();
				fMatches = matchClass(iItemToTest, iNumberOfItems, sStringToMatch,
						classes, sClassName);
				if (!fMatches && gnc.isOptional()) {
					fMatches = matchDescription(iItemToTest + 1, iNumberOfItems, sStringToMatch,
							classes);
				}
			}
		}
		return fMatches;
	}

	private boolean matchClass(int iItemToTest, int iNumberOfItems,
			String sStringToMatch, List<CVNaturalClass> classes, final String sClassName) {
		boolean fMatches = false;
		List<CVNaturalClass> matches = classes.stream()
				.filter(n -> n.getNCName().equals(sClassName)).collect(Collectors.toList());
		for (CVNaturalClass nc : matches) {
			for (SylParserObject spo : nc.getSegmentsOrNaturalClasses()) {
				if (spo instanceof Segment) {
					String sGrapheme = ((Segment) spo).getSegment();
					if (sStringToMatch.startsWith(sGrapheme)) {
						// have a matching grapheme, try next one to the right
						int iLen = sGrapheme.length();
						fMatches = matchDescription(iItemToTest + 1, iNumberOfItems,
								sStringToMatch.substring(iLen), classes);

						if (fMatches) {
							break;
						}
					}
				} else {
					final String sNewClassName = ((CVNaturalClass) spo).getNCName();
					fMatches = matchClass(iItemToTest, iNumberOfItems, sStringToMatch,
							classes, sNewClassName);
					if (fMatches) {
						break;
					}
				}
			}
			if (fMatches) {
				break;
			}
		}
		return fMatches;
	}

	public void rebuildRepresentationFromContext() {
		StringBuilder sb = new StringBuilder();
		rebuildRightContext(sb);
		setTemplateFilterRepresentation(sb.toString());
	}


	private void rebuildSegmentOrClass(StringBuilder sb,
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

	private void rebuildRightContext(StringBuilder sb) {
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
		TemplateFilter tf = (TemplateFilter) obj;
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
