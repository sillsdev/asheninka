package sil.org.syllableparser.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlTransient;

import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.utility.StringUtilities;

public class Environment extends SylParserObject {

	private final StringProperty envName;
	private final StringProperty description;
	private final StringProperty environmentRepresentation;
	private EnvironmentContext leftContext;
	private EnvironmentContext rightContext;

	private boolean valid = false;
	private final String ksSlash = "/ ";
	private final String ksBar = "_";
	private final String ksClassOpen = "[";
	private final String ksClassClose = "]";
	private final String ksOptionalOpen = "(";
	private final String ksOptionalClose = ")";
	private final String ksWordBoundary = "#";
	private final String ksSpace = " ";

	public Environment() {
		super();
		this.envName = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.environmentRepresentation = new SimpleStringProperty("");
		this.leftContext = new EnvironmentContext();
		this.rightContext = new EnvironmentContext();
		createUUID();
	}

	public Environment(String envName, String description, String envRepresentation,
			EnvironmentContext left, EnvironmentContext right) {
		super();
		this.envName = new SimpleStringProperty(envName);
		this.description = new SimpleStringProperty(description);
		this.environmentRepresentation = new SimpleStringProperty(envRepresentation);
		this.leftContext = left;
		this.rightContext = right;
		createUUID();
	}

	public String getEnvironmentName() {
		return envName.get();
	}

	public StringProperty environmentNameProperty() {
		return envName;
	}

	public void setEnvironmentName(String envName) {
		this.envName.set(envName);
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

	public String getEnvironmentRepresentation() {
		return environmentRepresentation.get();
	}

	public StringProperty environmentRepresentationProperty() {
		return environmentRepresentation;
	}

	public void setEnvironmentRepresentation(String environmentRepresentation) {
		this.environmentRepresentation.set(environmentRepresentation);
	}

	public EnvironmentContext getLeftContext() {
		return leftContext;
	}

	// @XmlTransient
	public void setLeftContext(EnvironmentContext leftContext) {
		this.leftContext = leftContext;
	}

	public EnvironmentContext getRightContext() {
		return rightContext;
	}

	// @XmlTransient
	public void setRightContext(EnvironmentContext rightContext) {
		this.rightContext = rightContext;
	}

	/**
	 * @return
	 */
	public StringProperty environmentProperty() {
		return this.envName;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean matches(String sBefore, String sAfter, List<GraphemeNaturalClass> classes) {
		boolean fMatches = false;
		int iContextSize = leftContext.getEnvContext().size();
		fMatches = matchLeftContext(iContextSize - 1, sBefore, classes);
		if (fMatches) {
			fMatches = matchRightContext(0, rightContext.getEnvContext().size(), sAfter, classes);
		}
		return fMatches;
	}

	private boolean matchLeftContext(int iItemToTest, String sStringToMatch,
			List<GraphemeNaturalClass> classes) {
		boolean fMatches = false;
		if (iItemToTest < 0) {
			// left context has nothing more to match
			if (StringUtilities.isNullOrEmpty(sStringToMatch)) {
				fMatches = true;
			} else if (!leftContext.isWordBoundary()) {
				fMatches = true; // doesn't need to be word initial and isn't
			}
		} else {
			EnvironmentContextGraphemeOrNaturalClass gnc = leftContext.getEnvContext().get(
					iItemToTest);
			if (gnc.isGrapheme()) {
				String sGrapheme = gnc.getGraphemeString();
				if (sStringToMatch.endsWith(sGrapheme)) {
					// have a matching grapheme, try next one to the left
					int iLen = sGrapheme.length();
					iLen = (iLen == sStringToMatch.length()) ? 0 : iLen;
					fMatches = matchLeftContext(iItemToTest - 1, sStringToMatch.substring(0, iLen),
							classes);
				} else if (gnc.isOptional()) {
					fMatches = matchLeftContext(iItemToTest - 1, sStringToMatch, classes);
				}
			} else {
				// have a grapheme natural class
				final String sClassName = gnc.getGraphemeString();
				fMatches = matchClassLeftContext(iItemToTest, sStringToMatch, classes, sClassName);
				if (!fMatches && gnc.isOptional()) {
					fMatches = matchLeftContext(iItemToTest - 1, sStringToMatch, classes);
				}
			}
		}
		return fMatches;
	}

	private boolean matchClassLeftContext(int iItemToTest, String sStringToMatch,
			List<GraphemeNaturalClass> classes, final String sClassName) {
		boolean fMatches = false;
		List<GraphemeNaturalClass> matches = classes.stream()
				.filter(n -> n.getNCName().equals(sClassName)).collect(Collectors.toList());
		for (GraphemeNaturalClass nc : matches) {
			for (SylParserObject spo : nc.getGraphemesOrNaturalClasses()) {
				if (spo instanceof Grapheme) {
					String sGrapheme = ((Grapheme) spo).getForm();
					if (sStringToMatch.endsWith(sGrapheme)) {
						// have a matching grapheme, try next one to the left
						int iLen = sGrapheme.length();
						fMatches = matchLeftContext(iItemToTest - 1,
								sStringToMatch.substring(0, iLen), classes);
						if (fMatches) {
							break;
						}
					}
				} else {
					final String sNewClassName = ((GraphemeNaturalClass) spo).getNCName();
					fMatches = matchClassLeftContext(iItemToTest, sStringToMatch, classes,
							sNewClassName);
				}
			}
			if (fMatches) {
				break;
			}
		}
		return fMatches;
	}

	private boolean matchRightContext(int iItemToTest, int iNumberOfItems, String sStringToMatch,
			List<GraphemeNaturalClass> classes) {
		boolean fMatches = false;
		if (iItemToTest < 0 || iItemToTest >= iNumberOfItems) {
			if (StringUtilities.isNullOrEmpty(sStringToMatch)) {
				fMatches = true;
			} else if (!rightContext.isWordBoundary()) {
				fMatches = true; // doesn't need to be word final and isn't
			}
		} else {
			EnvironmentContextGraphemeOrNaturalClass gnc = rightContext.getEnvContext().get(
					iItemToTest);
			if (gnc.isGrapheme()) {
				String sGrapheme = gnc.getGraphemeString();
				if (sStringToMatch.startsWith(sGrapheme)) {
					// have a matching grapheme, try next one to the right
					int iLen = sGrapheme.length();
					fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems,
							sStringToMatch.substring(iLen), classes);
				} else if (gnc.isOptional()) {
					fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems, sStringToMatch,
							classes);
				}
			} else {
				// have a grapheme natural class
				final String sClassName = gnc.getGraphemeString();
				fMatches = matchClassRightContext(iItemToTest, iNumberOfItems, sStringToMatch,
						classes, sClassName);
				if (!fMatches && gnc.isOptional()) {
					fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems, sStringToMatch,
							classes);
				}

			}
		}
		return fMatches;
	}

	private boolean matchClassRightContext(int iItemToTest, int iNumberOfItems,
			String sStringToMatch, List<GraphemeNaturalClass> classes, final String sClassName) {
		boolean fMatches = false;
		List<GraphemeNaturalClass> matches = classes.stream()
				.filter(n -> n.getNCName().equals(sClassName)).collect(Collectors.toList());
		for (GraphemeNaturalClass nc : matches) {
			for (SylParserObject spo : nc.getGraphemesOrNaturalClasses()) {
				if (spo instanceof Grapheme) {
					String sGrapheme = ((Grapheme) spo).getForm();
					if (sStringToMatch.startsWith(sGrapheme)) {
						// have a matching grapheme, try next one to the right
						int iLen = sGrapheme.length();
						fMatches = matchRightContext(iItemToTest + 1, iNumberOfItems,
								sStringToMatch.substring(iLen), classes);

						if (fMatches) {
							break;
						}
					}
				} else {
					final String sNewClassName = ((GraphemeNaturalClass) spo).getNCName();
					fMatches = matchClassRightContext(iItemToTest, iNumberOfItems, sStringToMatch,
							classes, sNewClassName);
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
		sb.append(ksSlash);
		rebuildLeftContext(sb);
		sb.append(ksBar);
		rebuildRightContext(sb);
		setEnvironmentRepresentation(sb.toString());
	}

	private void rebuildLeftContext(StringBuilder sb) {
		List<EnvironmentContextGraphemeOrNaturalClass> reversedLeftContextItems = leftContext
				.getEnvContext();
		// Collections.reverse(reversedLeftContextItems);
		if (leftContext.isWordBoundary()) {
			sb.append(ksWordBoundary);
			if (reversedLeftContextItems.size() == 0) {
				sb.append(ksSpace);
			}
		}
		for (EnvironmentContextGraphemeOrNaturalClass ecgnc : reversedLeftContextItems) {
			rebuildGraphemeOrClass(sb, ecgnc);
			sb.append(" ");
		}
	}

	private void rebuildGraphemeOrClass(StringBuilder sb,
			EnvironmentContextGraphemeOrNaturalClass ecgnc) {
		if (ecgnc.isOptional()) {
			sb.append(ksOptionalOpen);
		}
		if (ecgnc.isGrapheme()) {
			sb.append(ecgnc.getGraphemeString());
		} else {
			sb.append(ksClassOpen);
			sb.append(ecgnc.getGraphemeNaturalClass() != null ? ecgnc.getGraphemeNaturalClass()
					.getNCName() : ecgnc.getGraphemeString());
			sb.append(ksClassClose);
		}
		if (ecgnc.isOptional()) {
			sb.append(ksOptionalClose);
		}
	}

	private void rebuildRightContext(StringBuilder sb) {
		for (EnvironmentContextGraphemeOrNaturalClass ecgnc : rightContext.getEnvContext()) {
			sb.append(" ");
			rebuildGraphemeOrClass(sb, ecgnc);
		}
		if (rightContext.isWordBoundary()) {
			if (rightContext.getEnvContext().size() == 0) {
				sb.append(ksSpace);
			}
			sb.append(ksWordBoundary);
		}
	}

	public boolean usesGraphemeNaturalClass(GraphemeNaturalClass gnc) {
		boolean leftMatches = leftContext.envContext.stream().anyMatch(
				ecgnc -> ecgnc.getGraphemeNaturalClass().equals(gnc));
		if (leftMatches) {
			return true;
		}
		boolean rightMatches = rightContext.envContext.stream().anyMatch(
				ecgnc -> ecgnc.getGraphemeNaturalClass().equals(gnc));
		return rightMatches;
	}

	@Override
	public int hashCode() {
		String sCombo = envName.getValueSafe() + environmentRepresentation.getValueSafe();
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
		Environment env = (Environment) obj;
		if (!getEnvironmentName().equals(env.getEnvironmentName())) {
			result = false;
		} else {
			if (!getEnvironmentRepresentation().equals(env.getEnvironmentRepresentation())) {
				result = false;
			}
		}
		return result;
	}

}
