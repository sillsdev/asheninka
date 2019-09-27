// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */package org.sil.syllableparser.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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
	private final String ksSlash = "/ ";
	private final String ksBar = "_";
	private final String ksClassOpen = "[";
	private final String ksClassClose = "]";
	private final String ksOptionalOpen = "(";
	private final String ksOptionalClose = ")";
	private final String ksWordBoundary = "#";
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

	public boolean matches(String sBefore, String sAfter, List<GraphemeNaturalClass> classes) {
		boolean fMatches = false;
//		int iContextSize = leftContext.getEnvContext().size();
//		fMatches = matchLeftContext(iContextSize - 1, sBefore, classes);
//		if (fMatches) {
//			fMatches = matchRightContext(0, rightContext.getEnvContext().size(), sAfter, classes);
//		}
		return fMatches;
	}

	private boolean matchLeftContext(int iItemToTest, String sStringToMatch,
			List<GraphemeNaturalClass> classes) {
		boolean fMatches = false;
//		if (iItemToTest < 0) {
//			// left context has nothing more to match
//			if (StringUtilities.isNullOrEmpty(sStringToMatch)) {
//				fMatches = true;
//			} else if (!leftContext.isWordBoundary()) {
//				fMatches = true; // doesn't need to be word initial and isn't
//			}
//		} else {
//			EnvironmentContextGraphemeOrNaturalClass gnc = leftContext.getEnvContext().get(
//					iItemToTest);
//			if (gnc.isGrapheme()) {
//				String sGrapheme = gnc.getGraphemeString();
//				if (sStringToMatch.endsWith(sGrapheme)) {
//					// have a matching grapheme, try next one to the left
//					fMatches = tryNextOneToLeft(iItemToTest, sStringToMatch, classes, sGrapheme);
//				} else if (gnc.isOptional()) {
//					fMatches = matchLeftContext(iItemToTest - 1, sStringToMatch, classes);
//				}
//			} else {
//				// have a grapheme natural class
//				final String sClassName = gnc.getGraphemeString();
//				fMatches = matchClassLeftContext(iItemToTest, sStringToMatch, classes, sClassName);
//				if (!fMatches && gnc.isOptional()) {
//					fMatches = matchLeftContext(iItemToTest - 1, sStringToMatch, classes);
//				}
//			}
//		}
		return fMatches;
	}

//	private boolean tryNextOneToLeft(int iItemToTest, String sStringToMatch,
//			List<GraphemeNaturalClass> classes, String sGrapheme) {
//		boolean fMatches;
//		int iLen = sGrapheme.length();
//		int iRemainingLen = sStringToMatch.length() - iLen;
//		String sRemaining;
//		if (iRemainingLen <= 0) {
//			sRemaining = "";
//		} else {
//			sRemaining = sStringToMatch.substring(0, iRemainingLen);
//		}
//		fMatches = matchLeftContext(iItemToTest - 1, sRemaining, classes);
//		return fMatches;
//	}

//	private boolean matchClassLeftContext(int iItemToTest, String sStringToMatch,
//			List<GraphemeNaturalClass> classes, final String sClassName) {
//		boolean fMatches = false;
//		List<GraphemeNaturalClass> matches = classes.stream()
//				.filter(n -> n.getNCName().equals(sClassName)).collect(Collectors.toList());
//		for (GraphemeNaturalClass nc : matches) {
//			for (SylParserObject spo : nc.getGraphemesOrNaturalClasses()) {
//				if (spo instanceof Grapheme) {
//					String sGrapheme = ((Grapheme) spo).getForm();
//					if (sStringToMatch.endsWith(sGrapheme)) {
//						// have a matching grapheme, try next one to the left
//						fMatches = tryNextOneToLeft(iItemToTest, sStringToMatch, classes, sGrapheme);
//						if (fMatches) {
//							break;
//						}
//					}
//				} else {
//					final String sNewClassName = ((GraphemeNaturalClass) spo).getNCName();
//					fMatches = matchClassLeftContext(iItemToTest, sStringToMatch, classes,
//							sNewClassName);
//					if (fMatches) {
//						break;
//					}
//				}
//			}
//			if (fMatches) {
//				break;
//			}
//		}
//		return fMatches;
//	}

	private boolean matchRightContext(int iItemToTest, int iNumberOfItems, String sStringToMatch,
			List<GraphemeNaturalClass> classes) {
		boolean fMatches = false;
//		if (iItemToTest < 0 || iItemToTest >= iNumberOfItems) {
//			if (StringUtilities.isNullOrEmpty(sStringToMatch)) {
//				fMatches = true;
//			} else if (!rightContext.isWordBoundary()) {
//				fMatches = true; // doesn't need to be word final and isn't
//			}
//		} else {
//			EnvironmentContextGraphemeOrNaturalClass gnc = rightContext.getEnvContext().get(
//					iItemToTest);
//			if (gnc.isGrapheme()) {
//				String sGrapheme = gnc.getGraphemeString();
//				if (sStringToMatch.startsWith(sGrapheme)) {
//					// have a matching grapheme, try next one to the right
//					int iLen = sGrapheme.length();
//					fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems,
//							sStringToMatch.substring(iLen), classes);
//				} else if (gnc.isOptional()) {
//					fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems, sStringToMatch,
//							classes);
//				}
//			} else {
//				// have a grapheme natural class
//				final String sClassName = gnc.getGraphemeString();
//				fMatches = matchClassRightContext(iItemToTest, iNumberOfItems, sStringToMatch,
//						classes, sClassName);
//				if (!fMatches && gnc.isOptional()) {
//					fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems, sStringToMatch,
//							classes);
//				}
//
//			}
//		}
		return fMatches;
	}

//	private boolean matchClassRightContext(int iItemToTest, int iNumberOfItems,
//			String sStringToMatch, List<GraphemeNaturalClass> classes, final String sClassName) {
//		boolean fMatches = false;
//		List<GraphemeNaturalClass> matches = classes.stream()
//				.filter(n -> n.getNCName().equals(sClassName)).collect(Collectors.toList());
//		for (GraphemeNaturalClass nc : matches) {
//			for (SylParserObject spo : nc.getGraphemesOrNaturalClasses()) {
//				if (spo instanceof Grapheme) {
//					String sGrapheme = ((Grapheme) spo).getForm();
//					if (sStringToMatch.startsWith(sGrapheme)) {
//						// have a matching grapheme, try next one to the right
//						int iLen = sGrapheme.length();
//						fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems,
//								sStringToMatch.substring(iLen), classes);
//
//						if (fMatches) {
//							break;
//						}
//					}
//				} else {
//					final String sNewClassName = ((GraphemeNaturalClass) spo).getNCName();
//					fMatches = matchClassRightContext(iItemToTest, iNumberOfItems, sStringToMatch,
//							classes, sNewClassName);
//					if (fMatches) {
//						break;
//					}
//				}
//			}
//			if (fMatches) {
//				break;
//			}
//		}
//		return fMatches;
//	}

	public void rebuildRepresentationFromContext() {
		StringBuilder sb = new StringBuilder();
		sb.append(ksSlash);
		rebuildLeftContext(sb);
		sb.append(ksBar);
		rebuildRightContext(sb);
		setTemplateFilterRepresentation(sb.toString());
	}

	private void rebuildLeftContext(StringBuilder sb) {
//		List<EnvironmentContextGraphemeOrNaturalClass> reversedLeftContextItems = leftContext
//				.getEnvContext();
//		// Collections.reverse(reversedLeftContextItems);
//		if (leftContext.isWordBoundary()) {
//			sb.append(ksWordBoundary);
//			if (reversedLeftContextItems.size() == 0) {
//				sb.append(ksSpace);
//			}
//		}
//		for (EnvironmentContextGraphemeOrNaturalClass ecgnc : reversedLeftContextItems) {
//			rebuildGraphemeOrClass(sb, ecgnc);
//			sb.append(" ");
//		}
	}

	private void rebuildGraphemeOrClass(StringBuilder sb,
			EnvironmentContextGraphemeOrNaturalClass ecgnc) {
//		if (ecgnc.isOptional()) {
//			sb.append(ksOptionalOpen);
//		}
//		if (ecgnc.isGrapheme()) {
//			sb.append(ecgnc.getGraphemeString());
//		} else {
//			sb.append(ksClassOpen);
//			sb.append(ecgnc.getGraphemeNaturalClass() != null ? ecgnc.getGraphemeNaturalClass()
//					.getNCName() : ecgnc.getGraphemeString());
//			sb.append(ksClassClose);
//		}
//		if (ecgnc.isOptional()) {
//			sb.append(ksOptionalClose);
//		}
	}

	private void rebuildRightContext(StringBuilder sb) {
//		for (EnvironmentContextGraphemeOrNaturalClass ecgnc : rightContext.getEnvContext()) {
//			sb.append(" ");
//			rebuildGraphemeOrClass(sb, ecgnc);
//		}
//		if (rightContext.isWordBoundary()) {
//			if (rightContext.getEnvContext().size() == 0) {
//				sb.append(ksSpace);
//			}
//			sb.append(ksWordBoundary);
//		}
	}

	public boolean usesGraphemeNaturalClass(GraphemeNaturalClass gnc) {
//		boolean leftMatches = leftContext.envContext.stream().anyMatch(
//				ecgnc -> ecgnc.getGraphemeNaturalClass()!= null && ecgnc.getGraphemeNaturalClass().equals(gnc));
//		if (leftMatches) {
//			return true;
//		}
//		boolean rightMatches = rightContext.envContext.stream().anyMatch(
//				ecgnc -> ecgnc.getGraphemeNaturalClass()!= null && ecgnc.getGraphemeNaturalClass().equals(gnc));
//		return rightMatches;
		return false;
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
