// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.npapproach;

import java.util.List;

import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */

public class NPApproach extends Approach {

	LanguageProject langProj;
	SHApproach shApproach;

	/**
	 * Clear out all data in this Sonority Hierarchy approach
	 */
	public void clear() {
		//	Nothing to do that is not already done
	}

	public void load(NPApproach npApproachLoaded) {
		langProj = this.getLanguageProject();
		shApproach = langProj.getSHApproach();
	}

	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getNPPredictedSyllabification();
	}

	public ObservableList<SHNaturalClass> getNPSonorityHierarchy() {
		return getLanguageProject().getSHApproach().getSHSonorityHierarchy();
	}

	public List<SHNaturalClass> getActiveSHNaturalClasses() {
		return shApproach.getActiveSHNaturalClasses();
	}

	public SHNaturalClass getNaturalClassContainingSegment(Segment seg1) {
		return shApproach.getNaturalClassContainingSegment(seg1);
	}

	public List<CVNaturalClass> getActiveCVNaturalClasses() {
		return getLanguageProject().getCVApproach().getActiveCVNaturalClasses();
	}
}
