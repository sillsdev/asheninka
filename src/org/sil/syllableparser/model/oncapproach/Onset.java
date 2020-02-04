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
	public void checkRepairFilters(List<ONCSegmentInSyllable> segmentsInWord, int iSegmentInWord,
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
							syllablesInCurrentWord.getLast().getRime().getCoda().add(segment);
							syllablesInCurrentWord.getLast().getSegmentsInSyllable().add(segment);
							getGraphemes().remove(0);
							syl.getSegmentsInSyllable().remove(0);
							tracer.initStep(ONCType.FILTER_REPAIR_APPLIED,
									ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
							tracer.recordStep();
						} else {
							switch (opType) {
							case ALL_BUT_FIRST_HAS_ONSET:
								if (syllablesInCurrentWord.indexOf(previousSyl) != 0) {
									Coda coda = previousSyl.getRime().getCoda();
									coda.add(segment);
									getGraphemes().remove(0);
									tracer.initStep(ONCType.FILTER_REPAIR_APPLIED,
											ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
									tracer.recordStep();
								} else {
									tracer.initStep(
											ONCType.FILTER_FAILED,
											ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
											f);
									tracer.recordStep();
								}
								break;
							case EVERY_SYLLABLE_HAS_ONSET:
								break;
							case ONSETS_NOT_REQUIRED:
								Coda coda = previousSyl.getRime().getCoda();
								coda.add(segment);
								getGraphemes().remove(0);
								tracer.initStep(ONCType.FILTER_REPAIR_APPLIED,
										ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
								tracer.recordStep();
								break;
							}
						}
					} else if (segment.getSegment().isNucleus()) {
						switch (opType) {
						case ALL_BUT_FIRST_HAS_ONSET:
							if (syllablesInCurrentWord.indexOf(previousSyl) != 0) {
								Nucleus nuc = previousSyl.getRime().getNucleus();
								nuc.add(segment);
								getGraphemes().remove(0);
								tracer.initStep(ONCType.FILTER_REPAIR_APPLIED,
										ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
								tracer.recordStep();
							} else {
								tracer.initStep(
										ONCType.FILTER_FAILED,
										ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
										f);
								tracer.recordStep();
							}
							break;
						case EVERY_SYLLABLE_HAS_ONSET:
							break;
						case ONSETS_NOT_REQUIRED:
							Nucleus nuc = previousSyl.getRime().getNucleus();
							nuc.add(segment);
							getGraphemes().remove(0);
								tracer.initStep(ONCType.FILTER_REPAIR_APPLIED, ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
								tracer.recordStep();
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
}
