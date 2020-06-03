/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.TemplateFilterMatcher;
import org.sil.syllableparser.service.parsing.ONCTracer;
import org.sil.syllableparser.service.parsing.ONCSyllabifierState;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 * A constituent in a syllable using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public abstract class ONCConstituent {
	protected List<ONCSegmentInSyllable> graphemes = new ArrayList<ONCSegmentInSyllable>();
	protected List<Filter> failFilters = new ArrayList<Filter>();
	protected List<Filter> repairFilters = new ArrayList<Filter>();

	public List<ONCSegmentInSyllable> getGraphemes() {
		return graphemes;
	}

	public void setGraphemes(List<ONCSegmentInSyllable> graphemesInConstituent) {
		this.graphemes = graphemesInConstituent;
	}
	
	public List<Filter> getFailFilters() {
		return failFilters;
	}

	public void setFailFilters(List<Filter> failFilters) {
		this.failFilters = failFilters;
	}

	public List<Filter> getRepairFilters() {
		return repairFilters;
	}

	public void setRepairFilters(List<Filter> repairFilters) {
		this.repairFilters = repairFilters;
	}

	public boolean exists() {
		if (graphemes.size() > 0)
			return true;
		return false;
	}
	
	public void add(ONCSegmentInSyllable oncSegmentInSyllable) {
		graphemes.add(oncSegmentInSyllable);
	}
	
	protected void createLingTreeDescription(StringBuilder sb, String sType) {
		if (graphemes.size() > 0) {
			sb.append("(");
			sb.append(sType);
			for (ONCSegmentInSyllable oncSegmentInSyllable : graphemes) {
				sb.append("(\\L ");
				sb.append(oncSegmentInSyllable.getSegment().getSegment());
				sb.append("(\\G ");
				sb.append(oncSegmentInSyllable.getGrapheme());
				sb.append("))");
			}
			sb.append(")");
		}
	}
	
	protected void getONCPattern(StringBuilder sb, String sType) {
		for (int i=0; i < graphemes.size(); i++) {
			sb.append(sType);
		}
	}
	
	public abstract void applyAnyRepairFilters(List<ONCSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded);

	public ONCSyllabifierState applyAnyFailFilters(List<ONCSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, ONCSyllabifierState currentState, ONCSyllable syl,
			ONCSyllabificationStatus status, LinkedList<ONCSyllable> syllablesInCurrentWord,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		ONCTracer tracer = ONCTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : failFilters) {
			int iItemsInFilter = f.getSlots().size();
			int iSegmentsInConstituent = getGraphemes().size();
			if (iSegmentsInConstituent >= iItemsInFilter) {
				int iStart = iSegmentInWord - (iItemsInFilter - 1);
				if (matcher.matches(f, segmentsInWord.subList(iStart, iSegmentInWord + 1),
						sonorityComparer, sspComparisonNeeded)) {
					currentState = ONCSyllabifierState.FILTER_FAILED;
					if (!syllablesInCurrentWord.contains(syl)) {
						if (syl.getSegmentsInSyllable().size() > 0) {
							syllablesInCurrentWord.add(syl);
						}
					}
					tracer.setOncState(ONCSyllabifierState.FILTER_FAILED);
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
