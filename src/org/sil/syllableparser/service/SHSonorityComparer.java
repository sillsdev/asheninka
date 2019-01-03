/**
 * Copyright (c) 2018 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

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

	public SHComparisonResult compare(Segment seg1, Segment seg2) {
		if (seg1 == null) {
			return SHComparisonResult.LESS;
		}
		if (seg2 == null) {
			return SHComparisonResult.MORE;
		}
		List<SHNaturalClass> natClass1 = hierarchy.stream()
				.filter(nc -> nc.isActive() && nc.getSegments().contains(seg1))
				.collect(Collectors.toList());
		List<SHNaturalClass> natClass2 = hierarchy.stream()
				.filter(nc -> nc.isActive() && nc.getSegments().contains(seg2))
				.collect(Collectors.toList());
		if (natClass1.size() == 0) {
			// same as if null
			return SHComparisonResult.MISSING1;
		}
		if (natClass2.size() == 0) {
			// same as if null
			return SHComparisonResult.MISSING2;
		}
		int pos1 = hierarchy.indexOf(natClass1.get(0));
		int pos2 = hierarchy.indexOf(natClass2.get(0));
		if (pos1 < pos2) {
			return SHComparisonResult.MORE;
		}
		if (pos1 > pos2) {
			return SHComparisonResult.LESS;
		}
		return SHComparisonResult.EQUAL;
	}
}
