/**
 * Copyright (c) 2019-2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.TemplateFilterMatcher;
import org.sil.syllableparser.service.parsing.ONCTracer;
import org.sil.syllableparser.service.parsing.ONCSyllabifierState;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

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
	int iSegmentMatchesInTemplate;
	
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

	public int getSegmentMatchesInTemplate() {
		return iSegmentMatchesInTemplate;
	}

	public void createLingTreeDescription(StringBuilder sb) {
		if (graphemes.size() > 0) {
			sb.append("(");
			sb.append("O");
			for (CVSegmentInSyllable cvSegmentInSyllable : graphemes) {
				sb.append("(\\L ");
				sb.append(cvSegmentInSyllable.getSegment().getSegment());
				sb.append("(\\G ");
				sb.append(cvSegmentInSyllable.getGrapheme());
				sb.append("))");
			}
			sb.append(")");
		}
	}
	
	public void getONCPattern(StringBuilder sb) {
		super.getONCPattern(sb, "o");
	}

	@Override
	public void applyAnyRepairFilters(List<ONCSegmentInSyllable> segmentsInWord, int iSegmentInWord,
			ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord, SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		ONCTracer tracer = ONCTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : repairFilters) {
			int iItemsInFilter = f.getSlots().size();
			// find repair slot position
			int iConstituentBeginPosition = findConstituentBeginPosition(f);
			int iSegmentsInConstituent = getGraphemes().size();
			if (iSegmentsInConstituent >= iItemsInFilter || iConstituentBeginPosition > 0) {
				int iStart = iSegmentInWord - (iItemsInFilter - 1);
				if (iConstituentBeginPosition > 0) {
					iStart = iSegmentInWord - iConstituentBeginPosition;
				}
				if (iStart < 0)
					continue;
				int iEnd = iSegmentInWord + iItemsInFilter - iConstituentBeginPosition;
				iEnd = Math.min(iEnd, segmentsInWord.size());
				if (matcher.matches(f, segmentsInWord.subList(iStart, iEnd), sonorityComparer, null)) {
					if (syllablesInCurrentWord.size() <= 0) {
						tracer.initStep(
								ONCSyllabifierState.FILTER_FAILED,
								ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE,
								f);
						tracer.recordStep();
						break;
					}
					ONCSyllable previousSyl = syllablesInCurrentWord.getLast();
					Optional<TemplateFilterSlotSegmentOrNaturalClass> oSlot = f.getSlots().stream()
							.filter(s -> s.isRepairLeftwardFromHere()).findFirst();
					if (!oSlot.isPresent())
						continue;
					TemplateFilterSlotSegmentOrNaturalClass slot = oSlot.get();
					int iSlotPos = f.getSlots().indexOf(slot);
					ONCSegmentInSyllable segment = segmentsInWord.get(iStart + iSlotPos);
					if (codasAllowed && segment.getSegment().isCoda()) {
						if (getGraphemes().size() > 1) {
							applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segmentsInWord, iStart + iSlotPos, iConstituentBeginPosition);
						} else if (nextMatchingSegmentCanBeOnset(f.getSlots(), iSlotPos, segmentsInWord, iStart)) {
							applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segmentsInWord, iStart + iSlotPos, -1);
						} else {
							switch (opType) {
							case ALL_BUT_FIRST_HAS_ONSET:  // fall through
							case EVERY_SYLLABLE_HAS_ONSET:
								tracer.initStep(
										ONCSyllabifierState.FILTER_FAILED,
										ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
										f);
								tracer.recordStep();
								break;
							case ONSETS_NOT_REQUIRED:
								applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segmentsInWord, iStart + iSlotPos, -1);
								break;
							}
						}
					} else if (segment.getSegment().isNucleus()) {
						// NOTE: this code has not been tested via a unit test
						// Until we have templates working which can place a
						// segment which can be either an onset or a nucleus as
						// an onset, we will not get here; the segment is always
						// added as a nucleus
						switch (opType) {
						case ALL_BUT_FIRST_HAS_ONSET:  // fall through
						case EVERY_SYLLABLE_HAS_ONSET:
							if (syllablesInCurrentWord.indexOf(previousSyl) != 0) {
								applyRepairToNucleus(syl, tracer, f, previousSyl, segment);
							} else {
								tracer.initStep(
										ONCSyllabifierState.FILTER_FAILED,
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
								ONCSyllabifierState.FILTER_FAILED,
								ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE,
								f);
						tracer.recordStep();
						break;
					}
				}
			}
		}
	}

	protected int findConstituentBeginPosition(Filter f) {
		int iConstituentBeginPosition = 1;
		for (TemplateFilterSlotSegmentOrNaturalClass slot : f.getSlots()) {
			if (slot.isConstituentBeginsHere()) {
				return iConstituentBeginPosition;
			}
			iConstituentBeginPosition++;
		}
		return -1;
	}

	private boolean nextMatchingSegmentCanBeOnset(
			ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots, int iSlotPos,
			List<ONCSegmentInSyllable> segmentsInWord, int iStart) {
		iSlotPos++;
		if (iSlotPos >= slots.size()) {
			return false;
		}
		ONCSegmentInSyllable segment = segmentsInWord.get(iStart + iSlotPos);
		if (!segment.getSegment().isOnset()) {
			return false;
		}
		return true;
	}

	public void applyRepairToNucleus(ONCSyllable syl, ONCTracer tracer, Filter f,
			ONCSyllable previousSyl, ONCSegmentInSyllable segment) {
		Nucleus nuc = previousSyl.getRime().getNucleus();
		nuc.add(segment);
		getGraphemes().remove(0);
		syl.getSegmentsInSyllable().remove(0);
		tracer.initStep(ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
	}

	public void applyRepairToCoda(ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord,
			ONCTracer tracer, Filter f, List<ONCSegmentInSyllable> segmentsInWord, int iRepairPosition, int iConstituentBeginPosition) {
		int iSlotPosition = iRepairPosition;
		if (iConstituentBeginPosition >= 0) {
			iSlotPosition = iRepairPosition - iConstituentBeginPosition;
		}
		while (iSlotPosition <= iRepairPosition && getGraphemes().size() > 0) {
			ONCSegmentInSyllable segInSyl = segmentsInWord.get(iSlotPosition);
			syllablesInCurrentWord.getLast().getRime().getCoda().add(segInSyl);
			syllablesInCurrentWord.getLast().getSegmentsInSyllable().add(segInSyl);
			getGraphemes().remove(0);
			syl.getSegmentsInSyllable().remove(0);
			iSlotPosition++;
		}
		tracer.initStep(ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
	}

	public boolean applyAnyTemplates(Segment seg1, Segment seg2, SHComparisonResult result,
			List<ONCSegmentInSyllable> segmentsInWord, ONCSyllable syl, int i,
			SHSonorityComparer sonorityComparer, ONCTracer tracer,
			ONCSyllabifierState currentState, ONCApproach oncApproach) {
		iSegmentMatchesInTemplate = 0;
		if (seg1 == null || seg2 == null || !seg1.isOnset() || !seg2.isOnset())
			return false;
		if (templates.size() > 0) {
			int iSegmentsInWord = segmentsInWord.size();
			for (Template t : templates) {
				int iItemsInTemplate = t.getSlots().size();
				if (iItemsInTemplate >= 2) {
					TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
					if (matcher.matches(t, segmentsInWord.subList(i, iSegmentsInWord),
							sonorityComparer, null)) {
						iSegmentMatchesInTemplate = matcher.getMatchCount();
						if (tracer.isTracing()) {
							tracer.setSegment1(seg1);
							SHNaturalClass shClass = oncApproach
									.getNaturalClassContainingSegment(seg1);
							tracer.getTracingStep().setNaturalClass1(shClass);
							tracer.setOncState(currentState);
							tracer.setStatus(ONCSyllabificationStatus.ONSET_TEMPLATE_MATCHED);
							tracer.setTemplateFilterUsed(t);
							tracer.setSuccessful(true);
							tracer.recordStep();
							tracer.setSegment1(seg1);
							tracer.getTracingStep().setNaturalClass1(shClass);
							tracer.setSegment2(seg2);
							tracer.getTracingStep().setComparisonResult(result);
							tracer.getTracingStep().setNaturalClass2(
									oncApproach.getNaturalClassContainingSegment(seg2));
							tracer.setOncState(currentState);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}
