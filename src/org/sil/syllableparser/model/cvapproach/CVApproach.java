// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.cvapproach;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
// @XmlAccessorType(XmlAccessType.FIELD)
public class CVApproach extends Approach {

	private ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();
	private ObservableList<CVSyllablePattern> cvSyllablePatterns = FXCollections
			.observableArrayList();

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.languageProject = (LanguageProject) parent;
	}

	/**
	 * @return the cvNaturalClassesData
	 */
	@XmlElementWrapper(name = "cvNaturalClasses")
	@XmlElement(name = "cvNaturalClass")
	public ObservableList<CVNaturalClass> getCVNaturalClasses() {
		return cvNaturalClasses;
	}

	/**
	 * @param cvSegmentInventoryData
	 *            the cvSegmentInventoryData to set
	 */
	public void setCVNaturalClasses(ObservableList<CVNaturalClass> cvNaturalClassesData) {
		this.cvNaturalClasses = cvNaturalClassesData;
	}

	public List<CVNaturalClass> getActiveCVNaturalClasses() {
		return cvNaturalClasses.stream().filter(naturalClass -> naturalClass.isActive())
				.collect(Collectors.toList());
	}

	@XmlElementWrapper(name = "cvSyllablePatterns")
	@XmlElement(name = "cvSyllablePattern")
	public ObservableList<CVSyllablePattern> getCVSyllablePatterns() {
		return cvSyllablePatterns;
	}

	public void setCVSyllablePatterns(ObservableList<CVSyllablePattern> cvSyllablePatterns) {
		this.cvSyllablePatterns = cvSyllablePatterns;
	}

	public List<CVSyllablePattern> getActiveCVSyllablePatterns() {
		return cvSyllablePatterns.stream()
				.filter(pattern -> pattern.isActive() && pattern.ncs.size() != 0)
				.collect(Collectors.toList());
	}

	/**
	 * Clear out all data in this CV approach
	 */
	public void clear() {
		cvNaturalClasses.clear();
		cvSyllablePatterns.clear();
	}

	/**
	 * @param cvApproach
	 */
	public void load(CVApproach cvApproachLoaded) {
		ObservableList<CVNaturalClass> cvNaturalClassesLoadedData = cvApproachLoaded
				.getCVNaturalClasses();
		for (CVNaturalClass cvNaturalClass : cvNaturalClassesLoadedData) {
			cvNaturalClasses.add(cvNaturalClass);
		}
		ObservableList<CVSyllablePattern> cvSyllablePatternsLoadedData = cvApproachLoaded
				.getCVSyllablePatterns();
		for (CVSyllablePattern cvSyllablePattern : cvSyllablePatternsLoadedData) {
			cvSyllablePatterns.add(cvSyllablePattern);
		}
	}


	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getCVPredictedSyllabification();
	}
}
