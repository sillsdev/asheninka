// Copyright (c) 2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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
public class SHApproach extends Approach {

	private ObservableList<SHNaturalClass> shSonorityHierarchy = FXCollections
			.observableArrayList();

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.languageProject = (LanguageProject) parent;
	}

	@XmlElementWrapper(name = "shSonorityHierarchy")
	@XmlElement(name = "shNaturalClass")
	public ObservableList<SHNaturalClass> getSHSonorityHierarchy() {
		return shSonorityHierarchy;
	}

	public void setSHSonorityHierarchy(ObservableList<SHNaturalClass> shSonorityHierarchy) {
		this.shSonorityHierarchy = shSonorityHierarchy;
	}

	public List<SHNaturalClass> getActiveSHNaturalClasses() {
		return shSonorityHierarchy.stream()
				.filter(natClass -> natClass.isActive() && natClass.segs.size() != 0)
				.collect(Collectors.toList());
	}

	/**
	 * Clear out all data in this Sonority Hierarchy approach
	 */
	public void clear() {
		shSonorityHierarchy.clear();
	}

	public void load(SHApproach shApproachLoaded) {
		ObservableList<SHNaturalClass> shSonorityHierarchyLoadedData = shApproachLoaded
				.getSHSonorityHierarchy();
		for (SHNaturalClass shNaturalClass : shSonorityHierarchyLoadedData) {
			shSonorityHierarchy.add(shNaturalClass);
		}
	}

	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getSHPredictedSyllabification();
	}
}
