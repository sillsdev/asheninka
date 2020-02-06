/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.service.TemplateFilterMatcher;
import org.sil.syllableparser.service.parsing.ONCTracer;
import org.sil.syllableparser.service.parsing.ONCType;

/**
 * @author Andy Black
 *
 * An onset in a syllable using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public class Onset extends ONCConstituent {
	
	boolean codasAllowed;
	OnsetPrincipleType opType;
	
	public boolean areCodasAllowed() {
		return codasAllowed;
	}

	public void setCodasAllowed(boolean codasAllowed) {
		this.codasAllowed = codasAllowed;
	}

	public OnsetPrincipleType getOpType() {
		return opType;
	}

	public void setOpType(OnsetPrincipleType opType) {
		this.opType = opType;
	}

	public void createLingTreeDescription(StringBuilder sb) {
		super.createLingTreeDescription(sb, "O");
	}
	
	public void getONCPattern(StringBuilder sb) {
		super.getONCPattern(sb, "o");
	}

	@Override
	public void applyAnyRepairFilters(List<ONCSegmentInSyllable> segmentsInWord, int iSegmentInWord,
			ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord) {
		ONCTracer tracer = ONCTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : repairFilters) {
			int iItemsInFilter = f.getSlots().size();
			int iSegmentsInConstituent = getGraphemes().size();
			if (iSegmentsInConstituent >= iItemsInFilter) {
				int iStart = iSegmentInWord - (iItemsInFilter - 1);
				if (matcher.matches(f, segmentsInWord.subList(iStart, iSegmentInWord + 1))) {
					if (syllablesInCurrentWord.size() <= 0) {
						tracer.initStep(
								ONCType.FILTER_FAILED,
								ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE,
								f);
						tracer.recordStep();
						break;
					}
					ONCSyllable previousSyl = syllablesInCurrentWord.getLast();
					TemplateFilterSlotSegmentOrNaturalClass slot = f.getSlots().stream()
							.findFirst().filter(s -> s.isRepairLeftwardFromHere()).get();
					int iSlotPos = f.getSlots().indexOf(slot);
					ONCSegmentInSyllable segment = segmentsInWord.get(iStart + iSlotPos);
					if (codasAllowed && segment.getSegment().isCoda()) {
						if (getGraphemes().size() > 1) {
							applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segment);
						} else {
							switch (opType) {
							case ALL_BUT_FIRST_HAS_ONSET:  // fall through
							case EVERY_SYLLABLE_HAS_ONSET:
								tracer.initStep(
										ONCType.FILTER_FAILED,
										ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
										f);
								tracer.recordStep();
								break;
							case ONSETS_NOT_REQUIRED:
								applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segment);
								break;
							}
						}
					} else if (segment.getSegment().isNucleus()) {
						// NOTE: this code has not been tested via a unit test
						// Until we have templates working which can place a
						// segment which can be either an onset or a nucleus as
						// an onset, we will no get here; the segment is always
						// added as a nucleus
						switch (opType) {
						case ALL_BUT_FIRST_HAS_ONSET:  // fall through
						case EVERY_SYLLABLE_HAS_ONSET:
							if (syllablesInCurrentWord.indexOf(previousSyl) != 0) {
								applyRepairToNucleus(syl, tracer, f, previousSyl, segment);
							} else {
								tracer.initStep(
										ONCType.FILTER_FAILED,
										ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
										f);
								tracer.recordStep();
							}
							break;
						case ONSETS_NOT_REQUIRED:
							applyRepairToNucleus(syl, tracer, f, previousSyl, segment);
							break;
						}
					} else {
						tracer.initStep(
								ONCType.FILTER_FAILED,
								ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE,
								f);
						tracer.recordStep();
						break;
					}
				}
			}
		}
	}

	public void applyRepairToNucleus(ONCSyllable syl, ONCTracer tracer, Filter f,
			ONCSyllable previousSyl, ONCSegmentInSyllable segment) {
		Nucleus nuc = previousSyl.getRime().getNucleus();
		nuc.add(segment);
		getGraphemes().remove(0);
		syl.getSegmentsInSyllable().remove(0);
		tracer.initStep(ONCType.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
	}

	public void applyRepairToCoda(ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord,
			ONCTracer tracer, Filter f, ONCSegmentInSyllable segment) {
		syllablesInCurrentWord.getLast().getRime().getCoda().add(segment);
		syllablesInCurrentWord.getLast().getSegmentsInSyllable().add(segment);
		getGraphemes().remove(0);
		syl.getSegmentsInSyllable().remove(0);
		tracer.initStep(ONCType.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
	}
}
