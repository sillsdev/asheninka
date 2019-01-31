/**
 * Copyright (c) 2018-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import javafx.collections.ObservableList;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class SHSonorityComparer {

	SHApproach shApproach;
	ObservableList<SHNaturalClass> hierarchy;

	public SHSonorityComparer(SHApproach shApproach) {
		this.shApproach = shApproach;
		hierarchy = shApproach.getSHSonorityHierarchy();
	}

	/**
	 * @param langProj
	 */
	public SHSonorityComparer(LanguageProject langProj) {
		shApproach = langProj.getSHApproach();
		hierarchy = shApproach.getSHSonorityHierarchy();
	}

	public SHComparisonResult compare(Segment seg1, Segment seg2) {
		if (seg1 == null) {
			return SHComparisonResult.LESS;
		}
		if (seg2 == null) {
			return SHComparisonResult.MORE;
		}
		SHNaturalClass natClass1 = shApproach.getNaturalClassContainingSegment(seg1);
		SHNaturalClass natClass2 = shApproach.getNaturalClassContainingSegment(seg2);
		if (natClass1 == null) {
			return SHComparisonResult.MISSING1;
		}
		if (natClass2 == null) {
			return SHComparisonResult.MISSING2;
		}
		int pos1 = hierarchy.indexOf(natClass1);
		int pos2 = hierarchy.indexOf(natClass2);
		if (pos1 < pos2) {
			return SHComparisonResult.MORE;
		}
		if (pos1 > pos2) {
			return SHComparisonResult.LESS;
		}
		return SHComparisonResult.EQUAL;
	}
}
