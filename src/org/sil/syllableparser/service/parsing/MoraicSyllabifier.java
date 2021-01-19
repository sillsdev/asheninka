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
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SyllabificationParameters;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.Mora;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentUsageType;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicTracingStep;
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
	private boolean useWeightByPosition;
	private MoraicSyllabifierState currentState;
	private int iSegmentMatchesInTemplate = 0;
	
	LinkedList<MoraicSyllable> syllablesInCurrentWord = new LinkedList<MoraicSyllable>(
			Arrays.asList(new MoraicSyllable(null)));
	List<MoraicSegmentInSyllable> segmentsInWordInitialAppendix = new LinkedList<MoraicSegmentInSyllable>();
	List<MoraicSegmentInSyllable> segmentsInWordFinalAppendix = new LinkedList<MoraicSegmentInSyllable>();
	String sSyllabifiedWord;
	private List<Filter> onsetFailFilters = new ArrayList<Filter>();
	private List<Filter> onsetRepairFilters = new ArrayList<Filter>();
	private List<Filter> syllableFailFilters = new ArrayList<Filter>();
	private List<Filter> syllableRepairFilters = new ArrayList<Filter>();
	private List<Template> nucleusTemplates = new ArrayList<Template>();
	private List<Template> onsetTemplates = new ArrayList<Template>();
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
		nucleusTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.NUCLEUS)
				.collect(Collectors.toList());
		onsetTemplates = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getTemplateFilterType() == TemplateType.ONSET)
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
		onsetFailFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.ONSET && !f.getAction().isDoRepair())
				.collect(Collectors.toList());
		onsetRepairFilters = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.ONSET && f.getAction().isDoRepair())
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

	public List<Filter> getOnsetFilters() {
		return onsetFailFilters;
	}

	public List<Filter> getSyllableFilters() {
		return syllableFailFilters;
	}

	public List<Template> getNucleusTemplates() {
		return nucleusTemplates;
	}

	public List<Template> getOnsetTemplates() {
		return onsetTemplates;
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
		useWeightByPosition = languageProject.getSyllabificationParameters().isUseWeightByPosition();

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
			i = applyAnyWordInitialTemplates(segmentsInWord);
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
				if (applyAnyOnsetTemplates(seg1, seg2, result, segmentsInWord, syl, i,
						sonorityComparer, tracer, oncApproach)) {
					i = addMatchedSegmentsToOnset(segmentsInWord, syl, i);
					currentState = MoraicSyllabifierState.MORA;
				} else if (seg1.getMoras() == 0) {
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
				int morasInSyllable = getCountOfMorasInSyllable(syl);
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
					boolean haveCodaAlready = false;
					for (Mora mora : syl.getMoras()) {
						for (CVSegmentInSyllable item : mora.getGraphemes()) {
							if (item.getSegment().getMoras() == 0) {
								haveCodaAlready = true;
								break;
							}
						}
					}
					if (haveCodaAlready && applyAnyOnsetTemplates(seg1, seg2, result, segmentsInWord, syl, i,
							sonorityComparer, tracer, oncApproach)) {
						currentState = MoraicSyllabifierState.ONSET;
						syllablesInCurrentWord.add(syl);
						syl = createNewSyllable();
						tracer.setStatus(MoraicSyllabificationStatus.NON_INITIAL_ONSET_TEMPLATE_MATCHED_START_NEW_SYLLABLE);
						tracer.setSuccessful(true);
						tracer.recordStep();
						i = addMatchedSegmentsToOnset(segmentsInWord, syl, i);
						currentState = MoraicSyllabifierState.MORA;
					} else 
					if (useWeightByPosition && morasInSyllable < maxMorasInSyllable) {
						syl = addSegmentToSyllableAsMora(segmentsInWord, syl, seg1, i);
					} else {
//						if (applyAnyOnsetTemplates(seg1, seg2, result, segmentsInWord, syl, i,
//								sonorityComparer, tracer, oncApproach)) {
//							currentState = MoraicSyllabifierState.ONSET;
//							syllablesInCurrentWord.add(syl);
//							syl = createNewSyllable();
//							tracer.setStatus(MoraicSyllabificationStatus.NON_INITIAL_ONSET_TEMPLATE_MATCHED_START_NEW_SYLLABLE);
//							tracer.setSuccessful(true);
//							tracer.recordStep();
//							i = addMatchedSegmentsToOnset(segmentsInWord, syl, i);
//							currentState = MoraicSyllabifierState.MORA;
//						} else 
						{
						syl.getMoras().get(syl.getMoras().size() - 1).add(segmentsInWord.get(i));
						syl.getGraphemes().add(segmentsInWord.get(i));
						tracer.setStatus(MoraicSyllabificationStatus.APPENDED_TO_MORA);
						tracer.setSuccessful(true);
						tracer.recordStep();
						}
					}
				} else {
					if (morasInSyllable > 0) {
							//&& (result == SHComparisonResult.MISSING1 || result == SHComparisonResult.MISSING2)) {
						addSyllableToWord(seg1, syl, seg2, result);
						if (result == SHComparisonResult.MISSING1 || result == SHComparisonResult.MISSING2) {
							tracer.setStatus(MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
						} else {
							tracer.setStatus(MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
							if (applyAnyWordFinalTemplates(segmentsInWord, i - 1)) {
								return true;
							}
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
		}
		if (currentState == MoraicSyllabifierState.TEMPLATE_FAILED ||
				currentState == MoraicSyllabifierState.FILTER_FAILED)
			return false;
		if (currentState == MoraicSyllabifierState.MORA && syl.getMoras().size() == 0) {
			tracer.setStatus(MoraicSyllabificationStatus.EXPECTED_MORA_NOT_FOUND);
			tracer.recordStep();
			return false;
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

	protected int getCountOfMorasInSyllable(MoraicSyllable syl) {
		int morasInSyllable = 0;
		for (Mora mora : syl.getMoras()) {
			for (CVSegmentInSyllable segInSyl : mora.getGraphemes()) {
				int moras = segInSyl.getSegment().getMoras();
				if (moras > 0) {
					morasInSyllable += moras;
				} else {
					morasInSyllable++;
				}
			}
		}
		return morasInSyllable;
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
			MoraicSyllable syl, int i) {
		for (int index=0; index < iSegmentMatchesInTemplate; index++) {
			addSegmentToSyllableAsOnset(segmentsInWord, syl, i + index);
		}
		i = i + iSegmentMatchesInTemplate - 1;
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
		// TODO: are we actually using the mora class?
		Mora mora = new Mora();
		mora.add(segmentsInWord.get(i));
		syl.getMoras().add(mora);
		currentState = MoraicSyllabifierState.MORA;
		if (seg1.getMoras() > 1) {
			if (seg1.getMoras() == 2) {
				tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_TWO_MORAS);
			} else { // assuming this for now
				tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_THREE_MORAS);
			}
		} else {
			tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_MORA);
		}
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

	protected void addSegmentToSyllableAsOnset(
			List<MoraicSegmentInSyllable> segmentsInWord, MoraicSyllable syl, int i) {
		MoraicSegmentInSyllable segInSyl = segmentsInWord.get(i);
		segInSyl.setUsage(MoraicSegmentUsageType.ONSET);
		syl.add(segInSyl);
		syl.getOnset().add(segInSyl);
		if (tracer.isTracing()) {
			tracer.setSegment1(segInSyl.getSegment());
			tracer.getTracingStep().setNaturalClass1(
					oncApproach.getNaturalClassContainingSegment(segInSyl.getSegment()));
			tracer.setStatus(MoraicSyllabificationStatus.ADDED_AS_ONSET);
			tracer.recordStep();
		}
		applyAnyOnsetRepairFilters(segmentsInWord, i, syl, syllablesInCurrentWord,
				sonorityComparer, SHComparisonResult.LESS);

		applyAnyOnsetFailFilters(segmentsInWord, i, syl,
				syllablesInCurrentWord,
				sonorityComparer, SHComparisonResult.LESS);
	}

	private void applyAnyOnsetFailFilters(List<MoraicSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, MoraicSyllable syl,
			LinkedList<MoraicSyllable> syllablesInCurrentWord,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		MoraicTracer tracer = MoraicTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : onsetFailFilters) {
			int iItemsInFilter = f.getSlots().size();
			int iSegmentsInConstituent = syl.getGraphemes().size();
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
					tracer.setStatus(MoraicSyllabificationStatus.ONSET_FILTER_FAILED);
					tracer.setTemplateFilterUsed(f);
					tracer.setSuccessful(false);
					tracer.recordStep();
				}
			}
		}
	}

	// TODO: May need to refactor this which is also (mostly) in Onset.java
	public void applyAnyOnsetRepairFilters(List<MoraicSegmentInSyllable> segmentsInWord, int iSegmentInWord,
			MoraicSyllable syl, LinkedList<MoraicSyllable> syllablesInCurrentWord, SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		MoraicTracer tracer = MoraicTracer.getInstance();
		TemplateFilterMatcher matcher = TemplateFilterMatcher.getInstance();
		for (Filter f : onsetRepairFilters) {
			int iItemsInFilter = f.getSlots().size();
			// find repair slot position
			int iConstituentBeginPosition = findConstituentBeginPosition(f);
			int iSegmentsInConstituent = syl.getGraphemes().size();
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
								MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE,
								f);
						tracer.recordStep();
						break;
					}
					MoraicSyllable previousSyl = syllablesInCurrentWord.getLast();
					Optional<TemplateFilterSlotSegmentOrNaturalClass> oSlot = f.getSlots().stream()
							.filter(s -> s.isRepairLeftwardFromHere()).findFirst();
					if (!oSlot.isPresent())
						continue;
					TemplateFilterSlotSegmentOrNaturalClass slot = oSlot.get();
					int iSlotPos = f.getSlots().indexOf(slot);
					MoraicSegmentInSyllable segment = segmentsInWord.get(iStart + iSlotPos);
					if (codasAllowed && segment.getSegment().getMoras() == 0) {
						if (previousSyl.getGraphemes().size() > 1) {
							applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segmentsInWord, iStart + iSlotPos, iConstituentBeginPosition);
						} else if (nextMatchingSegmentCanBeOnset(f.getSlots(), iSlotPos, segmentsInWord, iStart)) {
							applyRepairToCoda(syl, syllablesInCurrentWord, tracer, f, segmentsInWord, iStart + iSlotPos, -1);
						} else {
							switch (opType) {
							case ALL_BUT_FIRST_HAS_ONSET:  // fall through
							case EVERY_SYLLABLE_HAS_ONSET:
								tracer.initStep(
										MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
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
										MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
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
								MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE,
								f);
						tracer.recordStep();
						break;
					}
				}
			}
		}
	}

	// TODO: need to refactor this which is also in Onset.java
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
			List<MoraicSegmentInSyllable> segmentsInWord, int iStart) {
		iSlotPos++;
		if (iSlotPos >= slots.size()) {
			return false;
		}
		MoraicSegmentInSyllable segment = segmentsInWord.get(iStart + iSlotPos);
		if (segment.getSegment().getMoras() > 0) {
			return false;
		}
		return true;
	}

	public void applyRepairToNucleus(MoraicSyllable syl, MoraicTracer tracer, Filter f,
			MoraicSyllable previousSyl, MoraicSegmentInSyllable segment) {
		Mora mora = previousSyl.getMoras().get(previousSyl.getMoras().size()-1);
		mora.add(segment);
		syl.getGraphemes().remove(0);
		syl.getSegmentsInSyllable().remove(0);
		tracer.initStep(MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
	}

	public void applyRepairToCoda(MoraicSyllable syl,
			LinkedList<MoraicSyllable> syllablesInCurrentWord, MoraicTracer tracer, Filter f,
			List<MoraicSegmentInSyllable> segmentsInWord, int iRepairPosition,
			int iConstituentBeginPosition) {
		int iSlotPosition = iRepairPosition;
		if (iConstituentBeginPosition >= 0) {
			iSlotPosition = iRepairPosition - iConstituentBeginPosition;
		}
		while (iSlotPosition <= iRepairPosition && syl.getGraphemes().size() > 0) {
			MoraicSegmentInSyllable segInSyl = segmentsInWord.get(iSlotPosition);
			int morasInSyllable = syllablesInCurrentWord.getLast().getMoras().size();
			if (useWeightByPosition && morasInSyllable < maxMorasInSyllable) {
				addSegmentToSyllableAsMora(segmentsInWord, syllablesInCurrentWord.getLast(), segInSyl.getSegment(), iSlotPosition);
			} else {
				syllablesInCurrentWord.getLast().getMoras().get(morasInSyllable - 1).add(segInSyl);
				syllablesInCurrentWord.getLast().getSegmentsInSyllable().add(segInSyl);
			}
			syl.getOnset().remove(0);
			syl.getSegmentsInSyllable().remove(0);
			iSlotPosition++;
		}
		tracer.initStep(MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, f);
		tracer.setSuccessful(true);
		tracer.recordStep();
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
	
	public boolean applyAnyOnsetTemplates(Segment seg1, Segment seg2, SHComparisonResult result,
			List<MoraicSegmentInSyllable> segmentsInWord, MoraicSyllable syl, int i,
			SHSonorityComparer sonorityComparer, MoraicTracer tracer,
			MoraicApproach oncApproach) {
		iSegmentMatchesInTemplate = 0;
		if (seg1 == null || seg2 == null || !seg1.isOnset() || !seg2.isOnset())
			return false;
		if (onsetTemplates.size() > 0) {
			int iSegmentsInWord = segmentsInWord.size();
			for (Template t : onsetTemplates) {
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
							tracer.setStatus(MoraicSyllabificationStatus.ONSET_TEMPLATE_MATCHED);
							tracer.setTemplateFilterUsed(t);
							tracer.setSuccessful(true);
							tracer.recordStep();
							tracer.setSegment1(seg1);
							tracer.getTracingStep().setNaturalClass1(shClass);
							tracer.setSegment2(seg2);
							tracer.getTracingStep().setComparisonResult(result);
							tracer.getTracingStep().setNaturalClass2(
									oncApproach.getNaturalClassContainingSegment(seg2));
						}
						return true;
					}
				}
			}
		}
		return false;
	}

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
								//tracer.setStatus(MoraicSyllabificationStatus.WORD_FINAL_TEMPLATE_APPLIED);
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
				for (CVSegmentInSyllable segInSyl : mora.getGraphemes()) {
					int morasBorn = segInSyl.getSegment().getMoras(); 
					for (int j = 1; j < morasBorn; j++) {
						sb.append(Constants.MORA_SYMBOL);
					}
				}
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
				int morasBorn = cvSegmentInSyllable.getSegment().getMoras(); 
				for (int i = 1; i < morasBorn; i++) {
					sb.append(" ");
					sb.append(Constants.MORA_SYMBOL);
				}
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
