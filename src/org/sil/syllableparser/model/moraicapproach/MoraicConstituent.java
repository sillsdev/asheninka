/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.moraicapproach;

import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.SyllableConstituent;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.TemplateFilterMatcher;
import org.sil.syllableparser.service.parsing.MoraicSyllabifierState;
import org.sil.syllableparser.service.parsing.MoraicTracer;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 * A constituent in a syllable using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public abstract class MoraicConstituent extends SyllableConstituent {

	public void add(MoraicSegmentInSyllable moraicSegmentInSyllable) {
		graphemes.add(moraicSegmentInSyllable);
	}
	
	protected void getMoraicPattern(StringBuilder sb, String sType) {
		for (int i=0; i < graphemes.size(); i++) {
			sb.append(sType);
		}
	}
	
	public MoraicSyllabifierState applyAnyFailFilters(List<MoraicSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, MoraicSyllabifierState currentState, MoraicSyllable syl,
			MoraicSyllabificationStatus status, LinkedList<MoraicSyllable> syllablesInCurrentWord,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		MoraicTracer tracer = MoraicTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : failFilters) {
			int iItemsInFilter = f.getSlots().size();
			int iSegmentsInConstituent = getGraphemes().size();
			if (iSegmentsInConstituent >= iItemsInFilter) {
				int iStart = iSegmentInWord - (iItemsInFilter - 1);
				if (matcher.matches(f, segmentsInWord.subList(iStart, iSegmentInWord + 1),
						sonorityComparer, sspComparisonNeeded)) {
					currentState = MoraicSyllabifierState.FILTER_FAILED;
					if (!syllablesInCurrentWord.contains(syl)) {
						if (syl.getSegmentsInSyllable().size() > 0) {
							syllablesInCurrentWord.add(syl);
						}
					}
					tracer.setStatus(status);
					tracer.setTemplateFilterUsed(f);
					tracer.setSuccessful(false);
					tracer.recordStep();
				}
			}
		}
		return currentState;
	}
}
