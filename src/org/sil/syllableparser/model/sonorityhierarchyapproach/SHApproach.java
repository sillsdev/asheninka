// Copyright (c) 2018-2019 SIL International
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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

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

	public SHNaturalClass getNaturalClassContainingSegment(Segment segment) {
		if (segment == null) {
			return null;
		}
		List<SHNaturalClass> natClassesWithSegment = shSonorityHierarchy.stream()
				.filter(nc -> nc.isActive() && nc.getSegments().contains(segment))
				.collect(Collectors.toList());
		if (natClassesWithSegment.size() == 0) {
			return null;
		}
		return natClassesWithSegment.get(0);
	}

}
