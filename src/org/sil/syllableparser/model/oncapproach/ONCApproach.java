// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.oncapproach;

import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
// @XmlAccessorType(XmlAccessType.FIELD)
public class ONCApproach extends Approach {

	/**
	 * Clear out all data in this Sonority Hierarchy approach
	 */
	public void clear() {
//		shSonorityHierarchy.clear();
	}

	public void load(ONCApproach shApproachLoaded) {
//		ObservableList<SHNaturalClass> shSonorityHierarchyLoadedData = shApproachLoaded
//				.getSHSonorityHierarchy();
//		for (SHNaturalClass shNaturalClass : shSonorityHierarchyLoadedData) {
//			shSonorityHierarchy.add(shNaturalClass);
//		}
	}

	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getONCPredictedSyllabification();
	}

	public ObservableList<SHNaturalClass> getONCSonorityHierarchy() {
		return getLanguageProject().getSHApproach().getSHSonorityHierarchy();
	}

	public SHNaturalClass getNaturalClassContainingSegment(Segment seg1) {
		LanguageProject langProj = this.getLanguageProject();
		SHApproach shApproach = langProj.getSHApproach();
		return shApproach.getNaturalClassContainingSegment(seg1);
	}
}
