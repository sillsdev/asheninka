// Copyright (c) 2019 SIL International
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

import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SyllabificationParameters;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.Coda;
import org.sil.syllableparser.model.oncapproach.Nucleus;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSegmentUsageType;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.oncapproach.Onset;
import org.sil.syllableparser.model.oncapproach.Rime;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

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
	private boolean fDoTrace = false;
	ONCTracingStep tracingStep = null;
	private List<ONCTracingStep> tracingSteps = new ArrayList<ONCTracingStep>();
	private SyllabificationParameters sylParams;
	private boolean codasAllowed;
	private boolean onsetMaximization;
	private OnsetPrincipleType opType;

	LinkedList<ONCSyllable> syllablesInCurrentWord = new LinkedList<ONCSyllable>(
			Arrays.asList(new ONCSyllable(null)));
	String sSyllabifiedWord;

	public ONCSyllabifier(ONCApproach oncApproach) {
		super();
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
	}

	public List<ONCSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<ONCSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<ONCTracingStep> getTracingSteps() {
		return tracingSteps;
	}

	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		tracingSteps.clear();
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
		ONCType currentType = ONCType.UNKNOWN;
		syllablesInCurrentWord.clear();
		tracingSteps.clear();
		tracingStep = new ONCTracingStep();
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		Segment seg1 = segmentsInWord.get(0).getSegment();
		if (seg1 == null) {
			return false;
		}
		ONCSyllable syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
		if (opType == OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET && !seg1.isOnset()) {
			if (fDoTrace) {
				tracingStep.setSegment1(seg1);
				tracingStep
						.setStatus(ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
				tracingSteps.add(tracingStep);
			}
			return false;
		}
		SHNaturalClass natClass = oncApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			if (fDoTrace) {
				tracingStep.setSegment1(seg1);
				tracingStep.setStatus(ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
				tracingSteps.add(tracingStep);
			}
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
			if (fDoTrace) {
				tracingStep.setSegment1(seg1);
				tracingStep.setNaturalClass1(oncApproach.getNaturalClassContainingSegment(seg1));
				tracingStep.setSegment2(seg2);
				tracingStep.setNaturalClass2(oncApproach.getNaturalClassContainingSegment(seg2));
				tracingStep.setComparisonResult(result);
				tracingStep.setStatus(ONCSyllabificationStatus.UNKNOWN);
				tracingStep.setOncType(currentType);
			}

			switch (currentType) {
			case UNKNOWN:
			case ONSET:
				if (seg1.isOnset()
						&& (result == SHComparisonResult.LESS)) {
					currentType = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else {
					i--;
					currentType = ONCType.NUCLEUS;
					if (fDoTrace) {
						if (seg1.isOnset()) {
							if (result == SHComparisonResult.MISSING1) {
								tracingStep
								.setStatus(ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
								tracingSteps.add(tracingStep);
								return false;
							} else {
								tracingStep
										.setStatus(ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
							}
						} else {
							tracingStep
									.setStatus(ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
						}
						tracingSteps.add(tracingStep);
						tracingStep = new ONCTracingStep();
					}
				}
				break;
			case ONSET_OR_NUCLEUS:
				if (seg1.isOnset()
						&& (result == SHComparisonResult.LESS)) {
					currentType = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else if (seg1.isNucleus()) {
					currentType = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else {
					// Probably never get here, but just in case...
					if (fDoTrace) {
						tracingStep
								.setStatus(ONCSyllabificationStatus.EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND);
						tracingSteps.add(tracingStep);
					}
					return false;
				}
				break;
			case NUCLEUS:
				if (seg1.isNucleus()) {
					currentType = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else {
					if (fDoTrace) {
						tracingStep.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);
						tracingSteps.add(tracingStep);
					}
					return false;
				}
				break;
			case NUCLEUS_OR_CODA:
				if (seg1.isNucleus()) {
					currentType = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else if (seg1.isCoda() && codasAllowed) {
					if (seg1.isOnset() && result == SHComparisonResult.LESS) {
						if (onsetMaximization) {
							i--;
							syllablesInCurrentWord.add(syl);
							syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
							currentType = updateTypeForNewSyllable();
							if (fDoTrace) {
								tracingStep
										.setStatus(ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE);
								tracingSteps.add(tracingStep);
								tracingStep = new ONCTracingStep();
							}
						} else if (!seg2.isOnset()
								&& opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
							i--;
							syllablesInCurrentWord.add(syl);
							syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
							currentType = ONCType.ONSET_OR_NUCLEUS;
							if (fDoTrace) {
								tracingStep
										.setStatus(ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE);
								tracingSteps.add(tracingStep);
								tracingStep = new ONCTracingStep();
							}
						} else {
							syl = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i);
							currentType = updateTypeForNewSyllable();
						}
					} else {
						if (result == SHComparisonResult.MISSING2) {
							if (fDoTrace) {
								tracingStep
										.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);
								tracingSteps.add(tracingStep);
							}
							return false;
						}
						else {
							syl = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i);
							currentType = updateTypeForNewSyllable();
						}
					}
				} else {
					i--;
					syllablesInCurrentWord.add(syl);
					syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
					currentType = ONCType.ONSET;
					if (fDoTrace) {
						if (codasAllowed) {
							tracingStep
									.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
						} else {
							tracingStep
									.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE);
						}
						tracingSteps.add(tracingStep);
						tracingStep = new ONCTracingStep();
					}
				}
				break;
			case CODA_OR_ONSET:
				if (seg1.isCoda() && codasAllowed && result == SHComparisonResult.MORE) {
					currentType = addSegmentToSyllableAsCoda(segmentsInWord, syl, i);
				} else {
					i--;
					syllablesInCurrentWord.add(syl);
					syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
					currentType = ONCType.ONSET_OR_NUCLEUS;
					if (fDoTrace) {
						tracingStep.setStatus(ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
						tracingSteps.add(tracingStep);
						tracingStep = new ONCTracingStep();
					}
				}
				break;
			case CODA: // never actually used...
//				if (codasAllowed) {
//					if (seg1.isCoda() && result == SHComparisonResult.LESS) {
//						syl = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i);
//						currentType = ONCType.ONSET_OR_NUCLEUS;
//					} else {
//						if (fDoTrace) {
//							traceInfo.setStatus(ONCSyllabificationStatus.EXPECTED_CODA_NOT_FOUND);
//							syllabifierTraceInfoList.add(traceInfo);
//						}
//						return false;
//					}
//				} else {
//					i--;
//					currentType = ONCType.ONSET_OR_NUCLEUS;
//					if (fDoTrace) {
//						traceInfo
//								.setStatus(ONCSyllabificationStatus.EXPECTED_CODA_BUT_CODAS_NOT_ALLOWED);
//						syllabifierTraceInfoList.add(traceInfo);
//						traceInfo = new ONCTracingStep();
//					}
//				}
				break;
			}
			i++;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
			if (fDoTrace) {
				Segment seg = segmentsInWord.get(segmentCount - 1).getSegment();
				tracingStep.setSegment1(seg);
				tracingStep.setStatus(ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);
				tracingSteps.add(tracingStep);
			}
		}
		return true;
	}

	protected ONCType addSegmentToSyllableAsCoda(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, int i) {
		ONCType currentType;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
		syl.getRime().getCoda().add(segmentsInWord.get(i));
		currentType = ONCType.CODA_OR_ONSET;
		if (fDoTrace) {
			tracingStep.setSegment1(segmentsInWord.get(i).getSegment());
			tracingStep.setOncType(ONCType.CODA);
			tracingStep.setStatus(ONCSyllabificationStatus.ADDED_AS_CODA);
			tracingSteps.add(tracingStep);
			tracingStep = new ONCTracingStep();
		}
		return currentType;
	}

	protected ONCType addSegmentToSyllableAsNucleus(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, Segment seg1, int i) {
		ONCType currentType;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.NUCLEUS);
		syl.add(segmentsInWord.get(i));
		syl.getRime().getNucleus().add(segmentsInWord.get(i));
		currentType = ONCType.NUCLEUS_OR_CODA;
		if (fDoTrace) {
			tracingStep.setOncType(ONCType.NUCLEUS);
			tracingStep.setStatus(ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
			tracingSteps.add(tracingStep);
			tracingStep = new ONCTracingStep();
		}
		return currentType;
	}

	protected ONCType addSegmentToSyllableAsOnset(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, Segment seg1, int i) {
		ONCType currentType;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.ONSET);
		syl.add(segmentsInWord.get(i));
		syl.getOnset().add(segmentsInWord.get(i));
		currentType = ONCType.ONSET_OR_NUCLEUS;
		if (fDoTrace) {
			tracingStep.setOncType(ONCType.ONSET);
			tracingStep.setStatus(ONCSyllabificationStatus.ADDED_AS_ONSET);
			tracingSteps.add(tracingStep);
			tracingStep = new ONCTracingStep();
		}
		return currentType;
	}

	protected ONCType updateTypeForNewSyllable() {
		ONCType currentType;
		if (opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
			currentType = ONCType.ONSET_OR_NUCLEUS;
		} else {
			currentType = ONCType.ONSET;
		}
		return currentType;
	}

	protected ONCSyllable addSegmentToSyllableAsCodaStartNewSyllable(
			List<ONCSegmentInSyllable> segmentsInWord, ONCSyllable syl, int i) {
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
		syl.getRime().getCoda().add(segmentsInWord.get(i));
		syllablesInCurrentWord.add(syl);
		syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
		if (fDoTrace) {
			tracingStep.setSegment1(segmentsInWord.get(i).getSegment());
			tracingStep.setOncType(ONCType.CODA);
			tracingStep.setStatus(ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);
			tracingSteps.add(tracingStep);
			tracingStep = new ONCTracingStep();
		}
		return syl;
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
		sb.append(")");
		return sb.toString();
	}

	public String getNaturalClassesInCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = tracingSteps.size();
		for (int i = 0; i < iSize; i++) {
			ONCTracingStep info = tracingSteps.get(i);
			if (info.getStatus() == null) {
				continue;
			}
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(getNCName(info.naturalClass1));
			if (i == iSize - 1) {
				sb.append(", ");
				sb.append(getNCName(info.naturalClass2));
			}
		}
		return sb.toString();
	}

	private String getNCName(SHNaturalClass natClass) {
		if (natClass == null) {
			return "null";
		} else {
			return natClass.getNCName();
		}
	}

	public String getSonorityValuesInCurrentWord() {
		return tracingSteps.stream().map(ONCTracingStep::getComparisonResult)
				.collect(Collectors.joining(", "));
	}
}
