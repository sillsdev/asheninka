/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.moraicapproach;

import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.oncapproach.ONCConstituent;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 */
public class Mora extends ONCConstituent {

	public void createLingTreeDescription(StringBuilder sb) {
		super.createLingTreeDescription(sb, "μ");
	}

	/* (non-Javadoc)
	 * @see org.sil.syllableparser.model.oncapproach.ONCConstituent#applyAnyRepairFilters(java.util.List, int, org.sil.syllableparser.model.oncapproach.ONCSyllable, java.util.LinkedList, org.sil.syllableparser.service.parsing.SHSonorityComparer, org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult)
	 */
	@Override
	public void applyAnyRepairFilters(List<ONCSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		// TODO Auto-generated method stub

	}

}
