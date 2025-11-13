// Copyright (c) 2018-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

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

	public Set<Segment> getMissingSegmentsFromSonorityHierarchy() {
		Set<Segment> segmentsInInventory = new HashSet<Segment>();
		Set<Segment> segmentsInHierarchy = new HashSet<Segment>();
		segmentsInInventory.addAll(getLanguageProject().getActiveSegmentsInInventory());
		for (SHNaturalClass nc : getActiveSHNaturalClasses()) {
			segmentsInHierarchy.addAll(nc.getSegments());
		}
		segmentsInInventory.removeAll(segmentsInHierarchy);
		return segmentsInInventory;
	}

	public List<SegmentInSHNaturalClass> getDuplicateSegmentsFromSonorityHierarchy() {
		Map<Segment, SHNaturalClass> segmentNatClassesInHierarchy = new HashMap<Segment, SHNaturalClass>();
		List<SegmentInSHNaturalClass> duplicateSegsInClasses = new ArrayList<SegmentInSHNaturalClass>();
		for (SHNaturalClass nc : getActiveSHNaturalClasses()) {
			for (Segment seg : nc.getSegments()) {
				if (segmentNatClassesInHierarchy.containsKey(seg)) {
					SegmentInSHNaturalClass segInClass = new SegmentInSHNaturalClass(seg, segmentNatClassesInHierarchy.get(seg));
					if (!duplicateSegsInClasses.contains(segInClass)) {
						duplicateSegsInClasses.add(segInClass);
					}
					SegmentInSHNaturalClass segInClass2 = new SegmentInSHNaturalClass(seg, nc);
					duplicateSegsInClasses.add(segInClass2);
				} else {
					segmentNatClassesInHierarchy.put(seg, nc);
				}
			}
		}
		return duplicateSegsInClasses;
	}
}
