/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.TemplateFilterMatcher;
import org.sil.syllableparser.service.parsing.ONCSyllabifierState;
import org.sil.syllableparser.service.parsing.ONCTracer;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 * A coda in a rime using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public class Coda extends ONCConstituent {

	public void createLingTreeDescription(StringBuilder sb) {
		super.createLingTreeDescription(sb, "C");
	}
	public void getONCPattern(StringBuilder sb) {
		super.getONCPattern(sb, "c");
	}

	@Override
	public void applyAnyRepairFilters(List<ONCSegmentInSyllable> segmentsInWord, int iSegmentInWord,
			ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord, SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		ONCTracer tracer = ONCTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : repairFilters) {
			int iItemsInFilter = f.getSlots().size();
			int iSegmentsInConstituent = getGraphemes().size();
			if (iSegmentsInConstituent >= iItemsInFilter) {
				int iStart = iSegmentInWord - (iItemsInFilter - 1);
				if (matcher.matches(f, segmentsInWord.subList(iStart, iSegmentInWord + 1), sonorityComparer, SHComparisonResult.MORE)) {
					Optional<TemplateFilterSlotSegmentOrNaturalClass> cSlot = f.getSlots().stream()
							.filter(s -> s.isRepairLeftwardFromHere()).findFirst();
					if (!cSlot.isPresent())
						continue;
					TemplateFilterSlotSegmentOrNaturalClass slot = cSlot.get();
					int iSlotPos = f.getSlots().indexOf(slot);
					ONCSegmentInSyllable segment = segmentsInWord.get(iStart + iSlotPos);
					if (segment.getSegment().isNucleus() || segment.getSegment().isCoda()) {
						// Most likely the segment will be a coda.  If it were a nucleus, we
						// would never get here because it would be parsed as a nucleus first.
						if (getGraphemes().size() > 1) {
							applyRepairToNucleus(syl, tracer, f, segment);
						}
					} else {
						tracer.initStep(
								ONCSyllabifierState.FILTER_FAILED,
								ONCSyllabificationStatus.CODA_FILTER_REPAIR_COULD_NOT_APPLY,
								f);
						tracer.recordStep();
						break;
					}
				} else {
					tracer.initStep(
							ONCSyllabifierState.FILTER_FAILED,
							ONCSyllabificationStatus.CODA_FILTER_REPAIR_COULD_NOT_APPLY,
							f);
					tracer.recordStep();
				}
			}
		}
	}
	
	public void applyRepairToNucleus(ONCSyllable syl, ONCTracer tracer, Filter f,
			ONCSegmentInSyllable segment) {
		Nucleus nuc = syl.getRime().getNucleus();
		nuc.add(segment);
		getGraphemes().remove(0);
		tracer.initStep(ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.CODA_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
	}


}
