// Copyright (c) 2020-2021 SIL International
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

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SyllabificationParameters;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.Mora;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentUsageType;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicTracingStep;
import org.sil.syllableparser.model.oncapproach.Onset;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.TemplateFilterMatcher;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class MoraicSyllabifier implements Syllabifiable {

	private LanguageProject languageProject;
	private MoraicApproach oncApproach;
	private MoraicSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	MoraicTracer tracer = null;
	private SyllabificationParameters sylParams;
	private boolean codasAllowed;
	private boolean onsetMaximization;
	private OnsetPrincipleType opType;
	private int maxMorasInSyllable;
	private MoraicSyllabifierState currentState;

	LinkedList<MoraicSyllable> syllablesInCurrentWord = new LinkedList<MoraicSyllable>(
			Arrays.asList(new MoraicSyllable(null)));
	List<MoraicSegmentInSyllable> segmentsInWordInitialAppendix = new LinkedList<MoraicSegmentInSyllable>();
	List<MoraicSegmentInSyllable> segmentsInWordFinalAppendix = new LinkedList<MoraicSegmentInSyllable>();
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

	public MoraicSyllabifier(MoraicApproach oncApproach) {
		super();
		tracer = MoraicTracer.getInstance();
		tracer.resetSteps();
		this.oncApproach = oncApproach;
		languageProject = oncApproach.getLanguageProject();
		sylParams = languageProject.getSyllabificationParameters();
		codasAllowed = sylParams.isCodasAllowed();
		onsetMaximization = sylParams.isOnsetMaximization();
		maxMorasInSyllable = sylParams.getMaxMorasPerSyllable();
		opType = sylParams.getOnsetPrincipleEnum();
		segmenter = new MoraicSegmenter(languageProject.getActiveGraphemes(),
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

	public List<MoraicSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<MoraicSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<MoraicTracingStep> getTracingSteps() {
		return tracer.getTracingSteps();
	}

	// following used for Unit Testing
	public void setTracingStep(MoraicTracingStep tracingStep) {
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
			List<MoraicSegmentInSyllable> segmentsInWord = (List<MoraicSegmentInSyllable>) segmenter
					.getSegmentsInWord();
			fSuccess = parseIntoSyllables(segmentsInWord);
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<MoraicSegmentInSyllable> segmentsInWord) {
		if (segmentsInWord.size() == 0) {
			return false;
		}
		boolean fResult = syllabify(segmentsInWord);
		return fResult;
	}

	public boolean syllabify(List<MoraicSegmentInSyllable> segmentsInWord) {
		boolean result = performSyllabification(segmentsInWord, false);
		if (wordInitialTemplates.size() > 0 && !result && syllablesInCurrentWord.size() == 0) {
			tracer.setStatus(MoraicSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES);
			tracer.recordStep();
			result = performSyllabification(segmentsInWord, true);
		}
		return result;
	}

	private boolean performSyllabification(List<MoraicSegmentInSyllable> segmentsInWord, boolean tryWordInitialTemplates) {
		currentState = MoraicSyllabifierState.UNKNOWN;
		syllablesInCurrentWord.clear();
		segmentsInWordInitialAppendix.clear();
		segmentsInWordFinalAppendix.clear();
		boolean useWeightByPosition = languageProject.getSyllabificationParameters().isUseWeightByPosition();
//		int maxMorasInSyllable = languageProject.getSyllabificationParameters().getMaxMorasPerSyllable();
		if (!tryWordInitialTemplates) {
			tracer.resetSteps();
		}
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		Segment seg1 = segmentsInWord.get(0).getSegment();
		if (seg1 == null) {
			return false;
		}
		MoraicSyllable syl = createNewSyllable();
		int i = 0;
		if (tryWordInitialTemplates) {
//			i = applyAnyWordInitialTemplates(segmentsInWord);
			if (i == 0) {
				tracer.setStatus(MoraicSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED);
				tracer.recordStep();
				return false;
			}
		}
		if (opType == OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET && seg1.getMoras() > 0) {
			tracer.setSegment1(seg1);
			tracer.setStatus(MoraicSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
			tracer.recordStep();
			return false;
		}
		SHNaturalClass natClass = oncApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			tracer.setSegment1(seg1);
			tracer.setStatus(MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
			tracer.recordStep();
			return false;
		}
		while (i < segmentCount) {
			seg1 = segmentsInWord.get(i).getSegment();
			Segment seg2 = null;
			if (i < segmentCount - 1) {
				seg2 = segmentsInWord.get(i + 1).getSegment();
			}
			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
			tracer.initStep(seg1, oncApproach.getNaturalClassContainingSegment(seg1), seg2,
					oncApproach.getNaturalClassContainingSegment(seg2), result,
					MoraicSyllabificationStatus.UNKNOWN);
			if (currentState == MoraicSyllabifierState.MORA && result == SHComparisonResult.LESS) {
				if (seg1.getMoras() == 0) {
					if (onsetMaximization || (seg2 != null && seg2.getMoras() == 1)
							|| !isCoda(i, syl.getMoras().size(), result, segmentsInWord)) {
						syl = addSyllableToWord(seg1, syl, seg2, result);
						currentState = MoraicSyllabifierState.ONSET;
					}
				}
			}
			switch (currentState) {
			case UNKNOWN:
			case ONSET:
				//List<MoraicSegmentInSyllable> onset = syl.getOnset();
//				if (onset.applyAnyTemplates(seg1, seg2, result, segmentsInWord, syl, i,
//						sonorityComparer, tracer, currentState, oncApproach)) {
//					i = addMatchedSegmentsToOnset(segmentsInWord, syl, i, onset);
//					currentState = MoraicSyllabificationStatus.NUCLEUS;
//				} else 
				if (seg1.getMoras() == 0) {
					if (result == SHComparisonResult.LESS) {
						addSegmentToSyllableAsOnset(segmentsInWord, syl, i);
					} else {
						tracer.setStatus(MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
						tracer.recordStep();
						return false;
					}
				} else {
					if (syl.getGraphemes().size() == 0 && opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
						if ((syllablesInCurrentWord.size() > 0 && opType == OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET) 
								|| opType == OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET) {
							tracer.setStatus(MoraicSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
							tracer.recordStep();
							return false;
						}
					}
					i--;
					currentState = MoraicSyllabifierState.MORA;
					if (seg1.getMoras() == 0) {
						if (result == SHComparisonResult.MISSING1) {
							tracer.setStatus(MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
							tracer.recordStep();
							return false;
						} else {
							tracer.setStatus(MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
							tracer.recordStep();
						}
					} else {
						tracer.setStatus(MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
						tracer.recordStep();
//						if (i > 0 && opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED
//								&& seg1.getMoras() > 0) {
//							tracer.setStatus(MoraicSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
//							tracer.recordStep();
//							return false;
//						}
					}
				}
				break;
			case MORA:
				int morasInSyllable = syl.getMoras().size();
				int morasInSegment = seg1.getMoras();
				if (morasInSegment > 0) {
					if (morasInSyllable < maxMorasInSyllable) {
						syl = addSegmentToSyllableAsMora(segmentsInWord, syl, seg1, i);
					} else {
						i--;
						currentState = MoraicSyllabifierState.ONSET;
						syllablesInCurrentWord.add(syl);
						syl = createNewSyllable();
						tracer.setStatus(MoraicSyllabificationStatus.MAXIMUM_MORAS_IN_SYLLABLE_FOUND_START_NEW_SYLLABLE);
						tracer.setSuccessful(true);
						tracer.recordStep();
					}
				} else if (isCoda(i, morasInSyllable, result, segmentsInWord)) {
					if (useWeightByPosition && morasInSyllable < maxMorasInSyllable) {
						syl = addSegmentToSyllableAsMora(segmentsInWord, syl, seg1, i);
					} else {
						syl.getMoras().get(morasInSyllable - 1).add(segmentsInWord.get(i));
						syl.getGraphemes().add(segmentsInWord.get(i));
						tracer.setStatus(MoraicSyllabificationStatus.APPENDED_TO_MORA);
						tracer.setSuccessful(true);
						tracer.recordStep();
					}
				} else {
					if (morasInSyllable > 0) {
							//&& (result == SHComparisonResult.MISSING1 || result == SHComparisonResult.MISSING2)) {
						addSyllableToWord(seg1, syl, seg2, result);
						if (result == SHComparisonResult.MISSING1 || result == SHComparisonResult.MISSING2) {
							tracer.setStatus(MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
						} else {
							tracer.setStatus(MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
						}
					} else {
						tracer.setStatus(MoraicSyllabificationStatus.EXPECTED_MORA_NOT_FOUND);
					}
					tracer.recordStep();
					return false;
				}
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
	//	}
		if (currentState == MoraicSyllabifierState.TEMPLATE_FAILED ||
				currentState == MoraicSyllabifierState.FILTER_FAILED)
			return false;
//		if (currentState == MoraicSyllabificationStatus.NUCLEUS && syl.getRime().getNucleus().getGraphemes().size() == 0) {
//			return false;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
			Segment seg = segmentsInWord.get(segmentCount - 1).getSegment();
			tracer.setSegment1(seg);
			tracer.setStatus(MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);
			tracer.recordStep();
		}
		return true;
	}

	protected MoraicSyllable addSyllableToWord(Segment seg1, MoraicSyllable syl, Segment seg2,
			SHComparisonResult result) {
		syllablesInCurrentWord.add(syl);
		tracer.setSegment1(seg1);
		tracer.setSegment2(seg2);
		tracer.setStatus(MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
		tracer.recordStep();
		syl = createNewSyllable();
		tracer.initStep(seg1, oncApproach.getNaturalClassContainingSegment(seg1), seg2,
				oncApproach.getNaturalClassContainingSegment(seg2), result,
				MoraicSyllabificationStatus.UNKNOWN);
		return syl;
	}

	protected boolean isCoda(int segIndex, int morasInSyllable, SHComparisonResult result, List<MoraicSegmentInSyllable> segmentsInWord) {
		if (!codasAllowed) {
			return false;
		}
		if (morasInSyllable > 0 && segIndex > 0) {
			Segment seg1 = segmentsInWord.get(segIndex - 1).getSegment();
			Segment seg2 = segmentsInWord.get(segIndex).getSegment();
			SHComparisonResult resultBefore = sonorityComparer.compare(seg1, seg2);
			if (resultBefore != SHComparisonResult.MORE) {
				return false;
			}
			if (result == SHComparisonResult.MORE || result == SHComparisonResult.EQUAL) {
				return true;
			} else if (!onsetMaximization & result == SHComparisonResult.LESS) {
				return true;
			}
		}
		return false;
	}
	protected int addMatchedSegmentsToOnset(List<MoraicSegmentInSyllable> segmentsInWord,
			MoraicSyllable syl, int i, Onset onset) {
		int iMatched = onset.getSegmentMatchesInTemplate();
		for (int index=0; index < iMatched; index++) {
//			currentState = addSegmentToSyllableAsOnset(segmentsInWord, syl, i + index);
		}
		i = i + iMatched - 1;
		return i;
	}

	public MoraicSyllable createNewSyllable() {
		MoraicSyllable syl = new MoraicSyllable(new ArrayList<MoraicSegmentInSyllable>());
		syl.setFailFilters(syllableFailFilters);
		syl.setRepairFilters(syllableRepairFilters);
//		syl.getOnset().setFailFilters(onsetFailFilters);
//		syl.getOnset().setRepairFilters(onsetRepairFilters);
//		syl.getOnset().setCodasAllowed(codasAllowed);
//		syl.getOnset().setOpType(opType);
//		syl.getOnset().setTemplates(onsetTemplates);
		return syl;
	}

	protected MoraicSyllabificationStatus addSegmentToSyllableAsCoda(List<MoraicSegmentInSyllable> segmentsInWord,
			MoraicSyllable syl, int i) {
		MoraicSyllabificationStatus currentState = null;
//		segmentsInWord.get(i).setUsage(MoraicSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
//		Coda coda = syl.getRime().getCoda();
//		coda.add(segmentsInWord.get(i));
//		currentState = MoraicSyllabificationStatus.CODA_OR_ONSET;
		tracer.setSegment1(segmentsInWord.get(i).getSegment());
//		tracer.setMoraicState(MoraicSyllabificationStatus.CODA);
//		tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_CODA);
		tracer.recordStep();
//		coda.applyAnyRepairFilters(segmentsInWord, i, syl, syllablesInCurrentWord,
//				sonorityComparer, SHComparisonResult.MORE);
//		currentState = coda.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED, syllablesInCurrentWord, sonorityComparer, SHComparisonResult.MORE);
		return currentState;
	}

	protected MoraicSyllable addSegmentToSyllableAsMora(List<MoraicSegmentInSyllable> segmentsInWord,
			MoraicSyllable syl, Segment seg1, int i) {
//		if (nucleusTemplates.size() > 0) {
//			Template templateMatched = null;
//			boolean startNewSyllable = false;
//			int iSegmentsInWord = segmentsInWord.size();
//			int iSegmentsInConstituent = syl.getRime().getNucleus().getGraphemes().size();
//			for (Template t: nucleusTemplates) {
//				int iItemsInTemplate = t.getSlots().size();
//				if (iSegmentsInConstituent < iItemsInTemplate) {
//					int iStart = i - iSegmentsInConstituent;
//					int iEnd = Math.min(iStart + iItemsInTemplate, iSegmentsInWord);
//					if (matcher.matches(t, segmentsInWord.subList(iStart, iEnd), sonorityComparer, null)) {
//						templateMatched = t;
//						break;
//					}
//				} else {
//					startNewSyllable = true;
//				}
//			}
//			if (templateMatched == null) {
//				if (startNewSyllable) {
//					syllablesInCurrentWord.add(syl);
//					if (opType == OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
//						syl = createNewSyllable();
//						if (tracer.isTracing())
//							createTemplateTracerStep(
//									seg1,
//									MoraicSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_ANOTHER_NUCLEUS_CREATE_NEW_SYLLABLE);
//					} else {
//						currentState = MoraicSyllabificationStatus.TEMPLATE_FAILED;
//						if (tracer.isTracing()) {
//							tracer.setStatus(MoraicSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_NUCLEUS_ONSET_REQUIRED_BUT_WONT_BE_ONE);
//							tracer.recordStep();
//						}
//						return syl;
//					}
//				} else {
//					currentState = MoraicSyllabificationStatus.TEMPLATE_FAILED;
//					if (tracer.isTracing()) {
//						tracer.setStatus(MoraicSyllabificationStatus.NUCLEUS_TEMPLATES_ALL_FAIL);
//						tracer.recordStep();
//					}
//					return syl;
//				}
//			} else {
//				if (tracer.isTracing())
//					tracer.setTemplateFilterUsed(templateMatched);
//					createTemplateTracerStep(
//							seg1,
//							MoraicSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED);
//			}
//		}
		segmentsInWord.get(i).setUsage(MoraicSegmentUsageType.MORA);
		syl.add(segmentsInWord.get(i));
		Mora mora = new Mora();
		mora.add(segmentsInWord.get(i));
		syl.getMoras().add(mora);
		currentState = MoraicSyllabifierState.MORA;
		tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_MORA);
		tracer.recordStep();
//		currentState = nucleus.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, syllablesInCurrentWord, sonorityComparer, SHComparisonResult.EQUAL);
		return syl;
	}

	public void createTemplateTracerStep(Segment seg1, MoraicSyllabificationStatus newStatus) {
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

	protected MoraicSyllabificationStatus addSegmentToSyllableAsOnset(
			List<MoraicSegmentInSyllable> segmentsInWord, MoraicSyllable syl, int i) {
		MoraicSyllabificationStatus currentState = null;
		MoraicSegmentInSyllable segInSyl = segmentsInWord.get(i);
		segInSyl.setUsage(MoraicSegmentUsageType.ONSET);
		syl.add(segInSyl);
		syl.getOnset().add(segInSyl);
//		Onset onset = syl.getOnset();
//		onset.add(segInSyl);
//		currentState = MoraicSyllabificationStatus.ONSET_OR_NUCLEUS;
		if (tracer.isTracing()) {
			tracer.setSegment1(segInSyl.getSegment());
			tracer.getTracingStep().setNaturalClass1(
					oncApproach.getNaturalClassContainingSegment(segInSyl.getSegment()));
			tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_ONSET);
			tracer.recordStep();
		}
//		onset.applyAnyRepairFilters(segmentsInWord, i, syl, syllablesInCurrentWord,
//				sonorityComparer, SHComparisonResult.LESS);
//
//		currentState = onset.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
//				MoraicSyllabificationStatus.ONSET_FILTER_FAILED, syllablesInCurrentWord,
//				sonorityComparer, SHComparisonResult.LESS);
		return currentState;
	}

	protected MoraicSyllabificationStatus updateTypeForNewSyllable() {
		MoraicSyllabificationStatus currentState = null;
		if (opType == OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
//			currentState = MoraicSyllabificationStatus.ONSET_OR_NUCLEUS;
		} else {
//			currentState = MoraicSyllabificationStatus.ONSET;
		}
		return currentState;
	}

	protected MoraicSyllabificationStatus addSegmentToSyllableAsCodaStartNewSyllable(
			List<MoraicSegmentInSyllable> segmentsInWord, MoraicSyllable syl, int i, MoraicSyllabificationStatus currentState) {
//		segmentsInWord.get(i).setUsage(MoraicSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
//		Coda coda = syl.getRime().getCoda();
//		coda.add(segmentsInWord.get(i));
//		coda.applyAnyRepairFilters(segmentsInWord, i, syl, syllablesInCurrentWord,
//				sonorityComparer, SHComparisonResult.LESS);
		syllablesInCurrentWord.add(syl);
		tracer.setSegment1(segmentsInWord.get(i).getSegment());
//		tracer.setMoraicState(MoraicSyllabificationStatus.CODA);
//		tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);
		tracer.recordStep();
//		currentState = coda.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED, syllablesInCurrentWord, sonorityComparer, SHComparisonResult.MORE);
//		currentState = syl.getRime().applyAnyFailFilters(segmentsInWord, i, currentState, syl,
//				MoraicSyllabificationStatus.RIME_FILTER_FAILED, syllablesInCurrentWord, sonorityComparer, SHComparisonResult.EQUAL);
//		currentState = syl.applyAnyFailFilters(segmentsInWord, i, currentState, syl,
//				MoraicSyllabificationStatus.SYLLABLE_FILTER_FAILED, syllablesInCurrentWord, sonorityComparer, null);
//		if (applyAnyWordFinalTemplates(segmentsInWord, i)) {
//			currentState = MoraicSyllabificationStatus.WORD_FINAL_TEMPLATE_APPLIED;
//		}
		return currentState;
	}

//	public int applyAnyCodaTemplates(Segment seg1, Segment seg2, SHComparisonResult result,
//			List<MoraicSegmentInSyllable> segmentsInWord, int i, MoraicSyllable syl) {
//		if (!seg1.isCoda() || seg2 == null || !seg2.isCoda())
//			return 0;
//		if (codaTemplates.size() > 0) {
//			int iSegmentsInWord = segmentsInWord.size();
//			for (Template t : codaTemplates) {
//				int iItemsInTemplate = t.getSlots().size();
//				if (iItemsInTemplate >= 2) {
//					if (matcher.matches(t, segmentsInWord.subList(i, iSegmentsInWord),
//							sonorityComparer, null)) {
//						if (tracer.isTracing()) {
//							tracer.setSegment1(seg1);
//							SHNaturalClass shClass = oncApproach
//									.getNaturalClassContainingSegment(seg1);
//							tracer.getTracingStep().setNaturalClass1(shClass);
//							tracer.setMoraicState(currentState);
//							tracer.setStatus(MoraicSyllabificationStatus.CODA_TEMPLATE_MATCHED);
//							tracer.setTemplateFilterUsed(t);
//							tracer.setSuccessful(true);
//							tracer.recordStep();
//						}
//						int iMatchCount = matcher.getMatchCount();
//						int iEnd = Math.min(i + iMatchCount, iSegmentsInWord);
//						for (int index = i; index < iEnd; index++) {
//							segmentsInWord.get(index).setUsage(MoraicSegmentUsageType.CODA);
//							syl.add(segmentsInWord.get(index));
//							Coda coda = syl.getRime().getCoda();
//							coda.add(segmentsInWord.get(index));
//							if (tracer.isTracing()) {
//								tracer.setSegment1(segmentsInWord.get(index).getSegment());
//								if (index == (iEnd-1)) {
//									tracer.setMoraicState(MoraicSyllabificationStatus.ONSET_OR_NUCLEUS);
//									tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);
//								} else {
//									tracer.setMoraicState(MoraicSyllabificationStatus.CODA);
//									tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_CODA);
//								}
//								tracer.setSuccessful(true);
//								tracer.recordStep();
//							}
//						}
//						return iEnd - 1;
//					}
//				}
//			}
//		}
//		return 0;
//	}
	public boolean applyAnyWordFinalTemplates(List<MoraicSegmentInSyllable> segmentsInWord,
			int i) {
		if (wordFinalTemplates.size() > 0) {
			int iSegmentsInWord = segmentsInWord.size();
			for (Template t: wordFinalTemplates) {
				int iItemsInTemplate = t.getSlots().size();
				int iStart = i + 1;
				if (iSegmentsInWord - iStart <= iItemsInTemplate) {
					int iEnd = Math.min(iStart + iItemsInTemplate, iSegmentsInWord);
					if (matcher.matches(t, segmentsInWord.subList(iStart, iEnd), sonorityComparer, null)) {
						int iMatchCount = matcher.getMatchCount();
						if ((iStart + iMatchCount) < iSegmentsInWord) {
							// word final template must match entire rest of the word
							continue;
						}
						iEnd = Math.min(iStart + iMatchCount, iSegmentsInWord);
						for (int index = iStart;  index < iEnd; index++) {
							MoraicSegmentInSyllable segInSyl = segmentsInWord.get(index);
							segInSyl.setUsage(MoraicSegmentUsageType.WORD_FINAL);
							segmentsInWordFinalAppendix.add(segInSyl);
							if (tracer.isTracing()) {
								Segment seg = segmentsInWord.get(index).getSegment();
								tracer.setSegment1(seg);
								tracer.getTracingStep().setNaturalClass1(oncApproach.getNaturalClassContainingSegment(seg));
								tracer.setStatus(MoraicSyllabificationStatus.WORD_FINAL_TEMPLATE_APPLIED);
								tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_WORD_FINAL_APPENDIX);
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

	public int applyAnyWordInitialTemplates(List<MoraicSegmentInSyllable> segmentsInWord) {
		if (wordInitialTemplates.size() > 0) {
			int iSegmentsInWord = segmentsInWord.size();
			for (Template t: wordInitialTemplates) {
				int iItemsInTemplate = t.getSlots().size();
				int iStart = 0;
				if (iSegmentsInWord > iItemsInTemplate) {
					int iEnd = Math.min(iStart + iItemsInTemplate, iSegmentsInWord);
					if (matcher.matches(t, segmentsInWord.subList(iStart, iEnd), sonorityComparer, null)) {
						for (int index = iStart;  index < iEnd; index++) {
							MoraicSegmentInSyllable segInSyl = segmentsInWord.get(index);
							segInSyl.setUsage(MoraicSegmentUsageType.WORD_INITIAL);
							segmentsInWordInitialAppendix.add(segInSyl);
							if (tracer.isTracing()) {
								Segment seg = segmentsInWord.get(index).getSegment();
								tracer.setSegment1(seg);
								tracer.getTracingStep().setNaturalClass1(oncApproach.getNaturalClassContainingSegment(seg));
								tracer.setStatus(MoraicSyllabificationStatus.WORD_INITIAL_TEMPLATE_APPLIED);
								tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_WORD_INITIAL_APPENDIX);
								tracer.setTemplateFilterUsed(t);
								tracer.recordStep();
							}
						}
						return iEnd;
					}
				}
			}
		}
		return 0;
	}
	public String getSyllabificationOfCurrentWord() {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		// Begin with any segments in the word initial appendix
		for (CVSegmentInSyllable seg : segmentsInWordInitialAppendix) {
			sb.append(seg.getGrapheme());
		}
		for (MoraicSyllable syl : syllablesInCurrentWord) {
			for (CVSegmentInSyllable seg : syl.getSegmentsInSyllable()) {
				sb.append(seg.getGrapheme());
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		// Append any segments in the word final appendix
		for (CVSegmentInSyllable seg : segmentsInWordFinalAppendix) {
			sb.append(seg.getGrapheme());
		}
		return sb.toString();
	}

	public String getMoraicPatternOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		// Begin with any segments in the word initial appendix
		for (int j = 0; j < segmentsInWordInitialAppendix.size(); j++) {
			sb.append("a");
		}
		for (MoraicSyllable syl : syllablesInCurrentWord) {
			for (int j = 0; j < syl.getOnset().size(); j++) {
				sb.append(Constants.SYLLABLE_SYMBOL);
			}
			for (Mora mora : syl.getMoras()) {
				sb.append(Constants.MORA_SYMBOL);
				for (int j = 1; j < mora.getGraphemes().size(); j++) {
					sb.append(Constants.CODA_IN_MORA_SYMBOL);
				}
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		// Append any segments in the word final appendix
		for (int j = 0; j < segmentsInWordFinalAppendix.size(); j++) {
			sb.append("a");
		}
		return sb.toString();
	}

	@Override
	public String getLingTreeDescriptionOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		sb.append("(W");
		if (segmentsInWordInitialAppendix.size() > 0) {
			addAppendixSegmentsToLingTreeDescription(sb, segmentsInWordInitialAppendix);
		}
		for (MoraicSyllable syl : syllablesInCurrentWord) {
			sb.append("(");
			sb.append(Constants.SYLLABLE_SYMBOL);
			createLingTreeDescription(sb, syl.getOnset());
			for (Mora mora : syl.getMoras()) {
				sb.append("(");
				sb.append(Constants.MORA_SYMBOL);
				createLingTreeDescription(sb, mora.getGraphemes());
				sb.append(")");
			}
			sb.append(")");
		}
		if (segmentsInWordFinalAppendix.size() > 0) {
			addAppendixSegmentsToLingTreeDescription(sb, segmentsInWordFinalAppendix);
		}
		sb.append(")");
		return sb.toString();
	}

	protected void createLingTreeDescription(StringBuilder sb, List<? extends CVSegmentInSyllable> list) {
		if (list.size() > 0) {
			for (CVSegmentInSyllable cvSegmentInSyllable : list) {
				sb.append("(\\L ");
				sb.append(cvSegmentInSyllable.getSegment().getSegment());
				sb.append("(\\G ");
				sb.append(cvSegmentInSyllable.getGrapheme());
				sb.append("))");
			}
		}
	}

	private void addAppendixSegmentsToLingTreeDescription(StringBuilder sb,
			List<MoraicSegmentInSyllable> segmentsInWordAppendix) {
		sb.append("(A");
		for (CVSegmentInSyllable seg : segmentsInWordAppendix) {
			sb.append("(\\L ");
			sb.append(seg.getSegmentName());
			sb.append("(\\G ");
			sb.append(seg.getGrapheme());
			sb.append("))");
		}
		sb.append(")");
	}
}
