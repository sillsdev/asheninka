// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SyllabificationParameters;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.Coda;
import org.sil.syllableparser.model.oncapproach.Nucleus;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.oncapproach.ONCConstituent;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSegmentUsageType;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.oncapproach.Onset;
import org.sil.syllableparser.model.oncapproach.Rime;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.TemplateFilterMatcher;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class ONCSyllabifier implements Syllabifiable {

	private LanguageProject languageProject;
	private ONCApproach oncApproach;
	private ONCSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	ONCTracer tracer = null;
	private SyllabificationParameters sylParams;
	private boolean codasAllowed;
	private boolean onsetMaximization;
	private OnsetPrincipleType opType;
	private ONCSyllabifierState currentState;

	LinkedList<ONCSyllable> syllablesInCurrentWord = new LinkedList<ONCSyllable>(
			Arrays.asList(new ONCSyllable(null)));
	List<ONCSegmentInSyllable> segmentsInWordFinalAppendx = new LinkedList<ONCSegmentInSyllable>();
	String sSyllabifiedWord;
	private List<Filter> codaFailFilters = new ArrayList<Filter>();
	private List<Filter> codaRepairFilters = new ArrayList<Filter>();
	private List<Filter> nucleusFailFilters = new ArrayList<Filter>();
	private List<Filter> nucleusRepairFilters = new ArrayList<Filter>();
	private List<Filter> onsetFailFilters = new ArrayList<Filter>();
	private List<Filter> onsetRepairFilters = new ArrayList<Filter>();
	private List<Filter> rimeFailFilters = new ArrayList<Filter>();
	private List<Filter> rimeRepairFilters = new ArrayList<Filter>();
	private List<Filter> syllableFailFilters = new ArrayList<Filter>();
	private List<Filter> syllableRepairFilters = new ArrayList<Filter>();
	private List<Template> codaTemplates = new ArrayList<Template>();
	private List<Template> nucleusTemplates = new ArrayList<Template>();
	private List<Template> onsetTemplates = new ArrayList<Template>();
	private List<Template> rimeTemplates = new ArrayList<Template>();
	private List<Template> syllableTemplates = new ArrayList<Template>();
	private List<Template> wordFinalTemplates = new ArrayList<Template>();
	private List<Template> wordInitialTemplates = new ArrayList<Template>();
	private TemplateFilterMatcher matcher;

	public ONCSyllabifier(ONCApproach oncApproach) {
		super();
		tracer = ONCTracer.getInstance();
		tracer.resetSteps();
		this.oncApproach = oncApproach;
		languageProject = oncApproach.getLanguageProject();
		sylParams = languageProject.getSyllabificationParameters();
		codasAllowed = sylParams.isCodasAllowed();
		onsetMaximization = sylParams.isOnsetMaximization();
		opType = sylParams.getOnsetPrincipleEnum();
		segmenter = new ONCSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		sonorityComparer = new SHSonorityComparer(oncApproach.getLanguageProject());
		sSyllabifiedWord = "";
		matcher = TemplateFilterMatcher.getInstance();
		matcher.setActiveSegments(languageProject.getActiveSegmentsInInventory());
		matcher.setActiveClasses(languageProject.getCVApproach().getActiveCVNaturalClasses());
		initializeFilters();
		initializeTemplates();
	}

	protected void initializeTemplates() {
		codaTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.CODA)
				.collect(Collectors.toList());
		nucleusTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.NUCLEUS)
				.collect(Collectors.toList());
		onsetTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.ONSET)
				.collect(Collectors.toList());
		rimeTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.RIME)
				.collect(Collectors.toList());
		syllableTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.SYLLABLE)
				.collect(Collectors.toList());
		wordFinalTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.WORDFINAL)
				.collect(Collectors.toList());
		wordInitialTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.WORDINITIAL)
				.collect(Collectors.toList());
	}

	protected void initializeFilters() {
		codaFailFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.CODA && !f.getAction().isDoRepair())
				.collect(Collectors.toList());
		codaRepairFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.CODA && f.getAction().isDoRepair())
				.collect(Collectors.toList());
		nucleusFailFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS && !f.getAction().isDoRepair())
				.collect(Collectors.toList());
		nucleusRepairFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS && f.getAction().isDoRepair())
				.collect(Collectors.toList());
		onsetFailFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.ONSET && !f.getAction().isDoRepair())
				.collect(Collectors.toList());
		onsetRepairFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.ONSET && f.getAction().isDoRepair())
				.collect(Collectors.toList());
		rimeFailFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.RIME && !f.getAction().isDoRepair())
				.collect(Collectors.toList());
		rimeRepairFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.RIME && f.getAction().isDoRepair())
				.collect(Collectors.toList());
		syllableFailFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.SYLLABLE && !f.getAction().isDoRepair())
				.collect(Collectors.toList());
		syllableRepairFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.SYLLABLE && f.getAction().isDoRepair())
				.collect(Collectors.toList());
	}

	public List<ONCSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<ONCSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<ONCTracingStep> getTracingSteps() {
		return tracer.getTracingSteps();
	}

	// following used for Unit Testing
	public void setTracingStep(ONCTracingStep tracingStep) {
		tracer.setTracingStep(tracingStep);
	}

	public void setDoTrace(boolean tracing) {
		tracer.setTracing(tracing);
	}

	public List<Filter> getCodaFilters() {
		return codaFailFilters;
	}

	public List<Filter> getNucleusFilters() {
		return nucleusFailFilters;
	}

	public List<Filter> getOnsetFilters() {
		return onsetFailFilters;
	}

	public List<Filter> getRimeFilters() {
		return rimeFailFilters;
	}

	public List<Filter> getSyllableFilters() {
		return syllableFailFilters;
	}

	public List<Template> getCodaTemplates() {
		return codaTemplates;
	}

	public List<Template> getNucleusTemplates() {
		return nucleusTemplates;
	}

	public List<Template> getOnsetTemplates() {
		return onsetTemplates;
	}

	public List<Template> getRimeTemplates() {
		return rimeTemplates;
	}

	public List<Template> getSyllableTemplates() {
		return syllableTemplates;
	}

	public List<Template> getWordFinalTemplates() {
		return wordFinalTemplates;
	}

	public List<Template> getWordInitialTemplates() {
		return wordInitialTemplates;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		tracer.resetSteps();
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<ONCSegmentInSyllable> segmentsInWord = (List<ONCSegmentInSyllable>) segmenter
					.getSegmentsInWord();
			fSuccess = parseIntoSyllables(segmentsInWord);
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<ONCSegmentInSyllable> segmentsInWord) {
		if (segmentsInWord.size() == 0) {
			return false;
		}
		boolean fResult = syllabify(segmentsInWord);
		return fResult;
	}

	public boolean syllabify(List<ONCSegmentInSyllable> segmentsInWord) {
		currentState = ONCSyllabifierState.UNKNOWN;
		syllablesInCurrentWord.clear();
		segmentsInWordFinalAppendx.clear();
		tracer.resetSteps();
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		Segment seg1 = segmentsInWord.get(0).getSegment();
		if (seg1 == null) {
			return false;
		}
		ONCSyllable syl = createNewSyllable();
		if (opType == OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET && !seg1.isOnset()) {
			tracer.setSegment1(seg1);
			tracer.setStatus(ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
			tracer.recordStep();
			return false;
		}
		SHNaturalClass natClass = oncApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			tracer.setSegment1(seg1);
			tracer.setStatus(ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
			tracer.recordStep();
			return false;
		}
		int i = 0;
		while (i < segmentCount) {
			seg1 = segmentsInWord.get(i).getSegment();
			Segment seg2 = null;
			if (i < segmentCount - 1) {
				seg2 = segmentsInWord.get(i + 1).getSegment();
			}
			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
			tracer.initStep(seg1, oncApproach.getNaturalClassContainingSegment(seg1), seg2,
					oncApproach.getNaturalClassContainingSegment(seg2), result,
					ONCSyllabificationStatus.UNKNOWN, currentState);

			switch (currentState) {
			case UNKNOWN:
			case ONSET:
				if (seg1.isOnset() && (result == SHComparisonResult.LESS)) {
					currentState = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else if (applyAnyOnsetTemplates(seg1, seg2, result, segmentsInWord, i)) {
					currentState = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else {
					i--;
					currentState = ONCSyllabifierState.NUCLEUS;
					if (seg1.isOnset()) {
						if (result == SHComparisonResult.MISSING1) {
							tracer.setStatus(ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
							tracer.recordStep();
							return false;
						} else {
							tracer.setStatus(ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
						}
					} else {
						tracer.setStatus(ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
					}
					tracer.recordStep();
				}
				break;
			case ONSET_OR_NUCLEUS:
				if (seg1.isOnset() && (result == SHComparisonResult.LESS)) {
					currentState = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else if (seg1.isNucleus()) {
					syl = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else if (applyAnyWordFinalTemplates(segmentsInWord, i-1)) {
					currentState = ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED;
				} else {
					// Probably never get here, but just in case...
					tracer.setStatus(ONCSyllabificationStatus.EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND);
					tracer.recordStep();
					return false;
				}
				break;
			case NUCLEUS:
				if (seg1.isNucleus()) {
					syl = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else {
					tracer.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);
					tracer.recordStep();
					return false;
				}
				break;
			case NUCLEUS_OR_CODA:
				if (seg1.isNucleus()) {
					syl = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else if (seg1.isCoda() && codasAllowed) {
					if (seg1.isOnset() && result == SHComparisonResult.LESS) {
						if (onsetMaximization) {
							i--;
							syllablesInCurrentWord.add(syl);
							currentState = syl.getRime().applyAnyFailFilters(segmentsInWord, i,
									currentState, syl, ONCSyllabificationStatus.RIME_FILTER_FAILED,
									syllablesInCurrentWord);
							currentState = syl.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
									ONCSyllabificationStatus.SYLLABLE_FILTER_FAILED,
									syllablesInCurrentWord);
							if (currentState != ONCSyllabifierState.FILTER_FAILED) {
								syl = createNewSyllable();
								currentState = updateTypeForNewSyllable();
								tracer.setStatus(ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE);
								tracer.recordStep();
							}
						} else if (!seg2.isOnset()
								&& opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
							i--;
							syllablesInCurrentWord.add(syl);
							currentState = syl.getRime().applyAnyFailFilters(segmentsInWord, i,
									currentState, syl, ONCSyllabificationStatus.RIME_FILTER_FAILED,
									syllablesInCurrentWord);
							currentState = syl.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
									ONCSyllabificationStatus.SYLLABLE_FILTER_FAILED,
									syllablesInCurrentWord);
							if (currentState != ONCSyllabifierState.FILTER_FAILED) {
								syl = createNewSyllable();
								currentState = ONCSyllabifierState.ONSET_OR_NUCLEUS;
								tracer.setStatus(ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE);
								tracer.recordStep();
							}
						} else {
							currentState = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i, currentState);
							if (currentState != ONCSyllabifierState.FILTER_FAILED
									&& currentState != ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED) {
								syl = createNewSyllable();
								currentState = updateTypeForNewSyllable();
							}
						}
					} else {
						if (result == SHComparisonResult.MISSING2) {
							tracer.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);
							tracer.recordStep();
							return false;
						}
						else {
							if (result == SHComparisonResult.MORE) {
								currentState = addSegmentToSyllableAsCoda(segmentsInWord, syl, i);
							} else {
								currentState = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i, currentState);
								if (currentState != ONCSyllabifierState.FILTER_FAILED
										&& currentState != ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED) {
									syl = createNewSyllable();
									currentState = updateTypeForNewSyllable();
								}
							}
						}
					}
				} else {
					i--;
					syllablesInCurrentWord.add(syl);
					currentState = syl.getRime().applyAnyFailFilters(segmentsInWord, i, currentState,
							syl, ONCSyllabificationStatus.RIME_FILTER_FAILED,
							syllablesInCurrentWord);
					currentState = syl
							.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
									ONCSyllabificationStatus.SYLLABLE_FILTER_FAILED,
									syllablesInCurrentWord);
					if (currentState != ONCSyllabifierState.FILTER_FAILED) {
						syl = createNewSyllable();
						currentState = ONCSyllabifierState.ONSET;
						if (codasAllowed) {
							tracer.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
						} else {
							tracer.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE);
						}
						tracer.recordStep();
					}
				}
				break;
			case CODA_OR_ONSET:
				Segment seg0 = segmentsInWord.get(i-1).getSegment();
				SHComparisonResult resultBack = sonorityComparer.compare(seg0, seg1);
				if (seg1.isCoda()
						&& codasAllowed
						&& resultBack == SHComparisonResult.MORE
						&& (result == SHComparisonResult.MORE || result ==SHComparisonResult.EQUAL)) {
					if (applyAnyOnsetTemplates(seg1, seg2, result, segmentsInWord, i)) {
						syllablesInCurrentWord.add(syl);
						syl = createNewSyllable();
						i--;
						currentState = ONCSyllabifierState.ONSET;
					} else {
						currentState = addSegmentToSyllableAsCoda(segmentsInWord, syl, i);
					}
				} else {
					i--;
					syllablesInCurrentWord.add(syl);
					currentState = syl.getRime().applyAnyFailFilters(segmentsInWord, i, currentState,
							syl, ONCSyllabificationStatus.RIME_FILTER_FAILED,
							syllablesInCurrentWord);
					currentState = syl
							.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
									ONCSyllabificationStatus.SYLLABLE_FILTER_FAILED,
									syllablesInCurrentWord);
					if (currentState != ONCSyllabifierState.FILTER_FAILED) {
						syl = createNewSyllable();
						currentState = ONCSyllabifierState.ONSET_OR_NUCLEUS;
						tracer.setStatus(ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
						tracer.recordStep();
					}
				}
				break;
			case CODA: // never actually used...
//				if (codasAllowed) {
//					if (seg1.isCoda() && result == SHComparisonResult.LESS) {
//						syl = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i);
//						currentState = ONCType.ONSET_OR_NUCLEUS;
//					} else {
//						if (fDoTrace) {
//							traceInfo.setStatus(ONCSyllabificationStatus.EXPECTED_CODA_NOT_FOUND);
//							syllabifierTraceInfoList.add(traceInfo);
//						}
//						return false;
//					}
//				} else {
//					i--;
//					currentState = ONCType.ONSET_OR_NUCLEUS;
//					if (fDoTrace) {
//						traceInfo
//								.setStatus(ONCSyllabificationStatus.EXPECTED_CODA_BUT_CODAS_NOT_ALLOWED);
//						syllabifierTraceInfoList.add(traceInfo);
//						traceInfo = new ONCTracingStep();
//					}
//				}
				break;
			case FILTER_FAILED:
				return false;
			case TEMPLATE_FAILED:
				return false;
			case WORD_FINAL_TEMPLATE_APPLIED:
				return true;
			default:
				break;
			}
			i++;
		}
		if (currentState == ONCSyllabifierState.TEMPLATE_FAILED ||
				currentState == ONCSyllabifierState.FILTER_FAILED)
			return false;
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
			Segment seg = segmentsInWord.get(segmentCount - 1).getSegment();
			tracer.setSegment1(seg);
			tracer.setStatus(ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);
			tracer.recordStep();
		}
		return true;
	}

	public ONCSyllable createNewSyllable() {
		ONCSyllable syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
		syl.setFailFilters(syllableFailFilters);
		syl.setRepairFilters(syllableRepairFilters);
		syl.getOnset().setFailFilters(onsetFailFilters);
		syl.getOnset().setRepairFilters(onsetRepairFilters);
		syl.getOnset().setCodasAllowed(codasAllowed);
		syl.getOnset().setOpType(opType);
		syl.getRime().setFailFilters(rimeFailFilters);
		syl.getRime().setRepairFilters(rimeRepairFilters);
		syl.getRime().getNucleus().setFailFilters(nucleusFailFilters);
		syl.getRime().getNucleus().setRepairFilters(nucleusRepairFilters);
		syl.getRime().getCoda().setFailFilters(codaFailFilters);
		syl.getRime().getCoda().setRepairFilters(codaRepairFilters);
		return syl;
	}

	protected ONCSyllabifierState addSegmentToSyllableAsCoda(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, int i) {
		ONCSyllabifierState currentState;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
		Coda coda = syl.getRime().getCoda();
		coda.add(segmentsInWord.get(i));
		currentState = ONCSyllabifierState.CODA_OR_ONSET;
		tracer.setSegment1(segmentsInWord.get(i).getSegment());
		tracer.setOncState(ONCSyllabifierState.CODA);
		tracer.setStatus(ONCSyllabificationStatus.ADDED_AS_CODA);
		tracer.recordStep();
		currentState = coda.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
				ONCSyllabificationStatus.CODA_FILTER_FAILED, syllablesInCurrentWord);
		return currentState;
	}

	protected ONCSyllable addSegmentToSyllableAsNucleus(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, Segment seg1, int i) {
		if (nucleusTemplates.size() > 0) {
			Template templateMatched = null;
			boolean startNewSyllable = false;
			int iSegmentsInWord = segmentsInWord.size();
			int iSegmentsInConstituent = syl.getRime().getNucleus().getGraphemes().size();
			for (Template t: nucleusTemplates) {
				int iItemsInTemplate = t.getSlots().size();
				if (iSegmentsInConstituent < iItemsInTemplate) {
					int iStart = i - iSegmentsInConstituent;
					int iEnd = Math.min(iStart + iItemsInTemplate, iSegmentsInWord);
					if (matcher.matches(t, segmentsInWord.subList(iStart, iEnd))) {
						templateMatched = t;
						break;
					}
				} else {
					startNewSyllable = true;
				}
			}
			if (templateMatched == null) {
				if (startNewSyllable) {
					syllablesInCurrentWord.add(syl);
					if (opType == OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
						syl = createNewSyllable();
						if (tracer.isTracing())
							createTemplateTracerStep(
									seg1,
									ONCSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_ANOTHER_NUCLEUS_CREATE_NEW_SYLLABLE);
					} else {
						currentState = ONCSyllabifierState.TEMPLATE_FAILED;
						if (tracer.isTracing()) {
							tracer.setStatus(ONCSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_NUCLEUS_ONSET_REQUIRED_BUT_WONT_BE_ONE);
							tracer.recordStep();
						}
						return syl;
					}
				} else {
					currentState = ONCSyllabifierState.TEMPLATE_FAILED;
					if (tracer.isTracing()) {
						tracer.setStatus(ONCSyllabificationStatus.NUCLEUS_TEMPLATES_ALL_FAIL);
						tracer.recordStep();
					}
					return syl;
				}
			} else {
				if (tracer.isTracing())
					tracer.setTemplateFilterUsed(templateMatched);
					createTemplateTracerStep(
							seg1,
							ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED);
			}
		}
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.NUCLEUS);
		syl.add(segmentsInWord.get(i));
		Nucleus nucleus = syl.getRime().getNucleus();
		nucleus.add(segmentsInWord.get(i));
		currentState = ONCSyllabifierState.NUCLEUS_OR_CODA;
		tracer.setOncState(ONCSyllabifierState.NUCLEUS);
		tracer.setStatus(ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		tracer.recordStep();
		currentState = nucleus.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, syllablesInCurrentWord);
		return syl;
	}

	public void createTemplateTracerStep(Segment seg1, ONCSyllabificationStatus newStatus) {
		// Need to remember segments, natural classes, and comparison
		SHNaturalClass nc1 = tracer.getTracingStep().getNaturalClass1();
		Segment seg2 = tracer.getTracingStep().getSegment2();
		SHNaturalClass nc2 = tracer.getTracingStep().getNaturalClass2();
		SHComparisonResult comparisonResult = tracer.getTracingStep().getComparisonResult();
		// create new step
		tracer.setStatus(newStatus);
		tracer.recordStep();
		// reset the values
		tracer.getTracingStep().setSegment1(seg1);
		tracer.getTracingStep().setNaturalClass1(nc1);
		tracer.getTracingStep().setSegment2(seg2);
		tracer.getTracingStep().setNaturalClass2(nc2);
		tracer.getTracingStep().setComparisonResult(comparisonResult);
	}

	protected ONCSyllabifierState addSegmentToSyllableAsOnset(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, Segment seg1, int i) {
		ONCSyllabifierState currentState;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.ONSET);
		syl.add(segmentsInWord.get(i));
		Onset onset = syl.getOnset();
		onset.add(segmentsInWord.get(i));
		currentState = ONCSyllabifierState.ONSET_OR_NUCLEUS;
		tracer.setOncState(ONCSyllabifierState.ONSET);
		tracer.setStatus(ONCSyllabificationStatus.ADDED_AS_ONSET);
		tracer.recordStep();
		onset.applyAnyRepairFilters(segmentsInWord, i, syl, syllablesInCurrentWord);

		currentState = onset.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
				ONCSyllabificationStatus.ONSET_FILTER_FAILED, syllablesInCurrentWord);
		return currentState;
	}

	protected ONCSyllabifierState updateTypeForNewSyllable() {
		ONCSyllabifierState currentState;
		if (opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
			currentState = ONCSyllabifierState.ONSET_OR_NUCLEUS;
		} else {
			currentState = ONCSyllabifierState.ONSET;
		}
		return currentState;
	}

	protected ONCSyllabifierState addSegmentToSyllableAsCodaStartNewSyllable(
			List<ONCSegmentInSyllable> segmentsInWord, ONCSyllable syl, int i, ONCSyllabifierState currentState) {
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
		Coda coda = syl.getRime().getCoda();
		coda.add(segmentsInWord.get(i));
		syllablesInCurrentWord.add(syl);
		tracer.setSegment1(segmentsInWord.get(i).getSegment());
		tracer.setOncState(ONCSyllabifierState.CODA);
		tracer.setStatus(ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);
		tracer.recordStep();
		currentState = coda.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
				ONCSyllabificationStatus.CODA_FILTER_FAILED, syllablesInCurrentWord);
		currentState = syl.getRime().applyAnyFailFilters(segmentsInWord, i, currentState, syl,
				ONCSyllabificationStatus.RIME_FILTER_FAILED, syllablesInCurrentWord);
		currentState = syl.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
				ONCSyllabificationStatus.SYLLABLE_FILTER_FAILED, syllablesInCurrentWord);
		if (applyAnyWordFinalTemplates(segmentsInWord, i)) {
			currentState = ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED;
		}
		return currentState;
	}

	public boolean applyAnyOnsetTemplates(Segment seg1, Segment seg2, SHComparisonResult result, List<ONCSegmentInSyllable> segmentsInWord, int i) {
		if (!seg1.isOnset() || result != SHComparisonResult.EQUAL || !seg2.isOnset())
			return false;
		if (onsetTemplates.size() > 0) {
			int iSegmentsInWord = segmentsInWord.size();
			for (Template t: onsetTemplates) {
				int iItemsInTemplate = t.getSlots().size();
				if (iItemsInTemplate >= 2) {
					if (matcher.matches(t, segmentsInWord.subList(i, iSegmentsInWord))) {
							if (tracer.isTracing()) {
								tracer.setSegment1(seg1);
								SHNaturalClass shClass = oncApproach.getNaturalClassContainingSegment(seg1);
								tracer.getTracingStep().setNaturalClass1(shClass);
								tracer.setOncState(currentState);
								tracer.setStatus(ONCSyllabificationStatus.ONSET_TEMPLATE_APPLIED);
								tracer.setTemplateFilterUsed(t);
								tracer.setSuccessful(true);
								tracer.recordStep();
								tracer.setSegment1(seg1);
								tracer.getTracingStep().setNaturalClass1(shClass);
								tracer.setSegment2(seg2);
								tracer.getTracingStep().setComparisonResult(result);
								tracer.getTracingStep().setNaturalClass2(oncApproach.getNaturalClassContainingSegment(seg2));
								tracer.setOncState(currentState);
							}
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean applyAnyWordFinalTemplates(List<ONCSegmentInSyllable> segmentsInWord,
			int i) {
		if (wordFinalTemplates.size() > 0) {
			int iSegmentsInWord = segmentsInWord.size();
			for (Template t: wordFinalTemplates) {
				int iItemsInTemplate = t.getSlots().size();
				int iStart = i + 1;
				if (iSegmentsInWord - iStart <= iItemsInTemplate) {
					int iEnd = Math.min(iStart + iItemsInTemplate, iSegmentsInWord);
					if (matcher.matches(t, segmentsInWord.subList(iStart, iEnd))) {
						for (int index = iStart;  index < iEnd; index++) {
							ONCSegmentInSyllable segInSyl = segmentsInWord.get(index);
							segInSyl.setUsage(ONCSegmentUsageType.WORD_FINAL);
							segmentsInWordFinalAppendx.add(segInSyl);
							if (tracer.isTracing()) {
								Segment seg = segmentsInWord.get(index).getSegment();
								tracer.setSegment1(seg);
								tracer.getTracingStep().setNaturalClass1(oncApproach.getNaturalClassContainingSegment(seg));
								tracer.setOncState(ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED);
								tracer.setStatus(ONCSyllabificationStatus.ADDED_AS_WORD_FINAL_APPENDIX);
								tracer.setTemplateFilterUsed(t);
								tracer.recordStep();
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getSyllabificationOfCurrentWord() {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (ONCSyllable syl : syllablesInCurrentWord) {
			for (CVSegmentInSyllable seg : syl.getSegmentsInSyllable()) {
				sb.append(seg.getGrapheme());
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		// Append any segments in the word final appendix
		for (CVSegmentInSyllable seg : segmentsInWordFinalAppendx) {
			sb.append(seg.getGrapheme());
		}
		return sb.toString();
	}

	public String getONCPatternOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (ONCSyllable syl : syllablesInCurrentWord) {
			Onset onset = syl.getOnset();
			onset.getONCPattern(sb);
			Rime rime = syl.getRime();
			rime.getONCPattern(sb);
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		// Append any segments in the word final appendix
		for (CVSegmentInSyllable seg : segmentsInWordFinalAppendx) {
			sb.append("a");
		}
		return sb.toString();
	}

	@Override
	public String getLingTreeDescriptionOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		sb.append("(W");
		for (ONCSyllable syl : syllablesInCurrentWord) {
			sb.append("(σ");
			Onset onset = syl.getOnset();
			onset.createLingTreeDescription(sb);
			Rime rime = syl.getRime();
			rime.createLingTreeDescription(sb);
			sb.append(")");
		}
		if (segmentsInWordFinalAppendx.size() > 0) {
			sb.append("(A");
			for (CVSegmentInSyllable seg : segmentsInWordFinalAppendx) {
				sb.append("(\\L ");
				sb.append(seg.getSegmentName());
				sb.append("(\\G ");
				sb.append(seg.getGrapheme());
				sb.append("))");
			}
			sb.append(")");
		}
		sb.append(")");
		return sb.toString();
	}
}
