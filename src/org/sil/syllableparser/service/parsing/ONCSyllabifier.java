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

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SyllabificationParameters;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSegmentUsageType;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTraceSyllabifierInfo;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class ONCSyllabifier {

	private LanguageProject languageProject;
	private ONCApproach oncApproach;
	private ONCSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	private boolean fDoTrace = false;
	ONCTraceSyllabifierInfo traceInfo = null;
	private List<ONCTraceSyllabifierInfo> syllabifierTraceInfoList = new ArrayList<ONCTraceSyllabifierInfo>();
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

	public List<ONCTraceSyllabifierInfo> getSyllabifierTraceInfo() {
		return syllabifierTraceInfoList;
	}

	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfoList.clear();
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<ONCSegmentInSyllable> segmentsInWord = (List<ONCSegmentInSyllable>) segmenter
					.getONCSegmentsInWord();
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
		syllabifierTraceInfoList.clear();
		traceInfo = new ONCTraceSyllabifierInfo();
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		ONCSyllable syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
		Segment seg1 = segmentsInWord.get(0).getSegment();
		if (seg1 == null) {
			return false;
		}
		if (opType == OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET && !seg1.isOnset()) {
			if (fDoTrace) {
				traceInfo.setSegment1(seg1);
				traceInfo
						.setStatus(ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
				syllabifierTraceInfoList.add(traceInfo);
			}
			return false;
		}
		SHNaturalClass natClass = oncApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			if (fDoTrace) {
				traceInfo.setSegment1(seg1);
				traceInfo.setStatus(ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
				syllabifierTraceInfoList.add(traceInfo);
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
				traceInfo.setSegment1(seg1);
				traceInfo.setNaturalClass1(oncApproach.getNaturalClassContainingSegment(seg1));
				traceInfo.setSegment2(seg2);
				traceInfo.setNaturalClass2(oncApproach.getNaturalClassContainingSegment(seg2));
				traceInfo.setComparisonResult(result);
				traceInfo.setStatus(ONCSyllabificationStatus.UNKNOWN);
			}

			switch (currentType) {
			case UNKNOWN:
			case ONSET:
				if (seg1.isOnset()
						&& (result == SHComparisonResult.LESS || result == SHComparisonResult.EQUAL)) {
					currentType = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else {
					i--;
					currentType = ONCType.NUCLEUS;
					if (fDoTrace) {
						if (seg1.isOnset()) {
							if (result == SHComparisonResult.MISSING1) {
								traceInfo
								.setStatus(ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
								syllabifierTraceInfoList.add(traceInfo);
								return false;
							} else {
								traceInfo
										.setStatus(ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
							}
						} else {
							traceInfo
									.setStatus(ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
						}
						syllabifierTraceInfoList.add(traceInfo);
						traceInfo = new ONCTraceSyllabifierInfo();
					}
				}
				break;
			case ONSET_OR_NUCLEUS:
				if (seg1.isOnset()
						&& (result == SHComparisonResult.LESS || result == SHComparisonResult.EQUAL)) {
					currentType = addSegmentToSyllableAsOnset(segmentsInWord, syl, seg1, i);
				} else if (seg1.isNucleus()) {
					currentType = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else {
					// Probably never get here, but just in case...
					if (fDoTrace) {
						traceInfo
								.setStatus(ONCSyllabificationStatus.EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND);
						syllabifierTraceInfoList.add(traceInfo);
					}
					return false;
				}
				break;
			case NUCLEUS:
				if (seg1.isNucleus()) {
					currentType = addSegmentToSyllableAsNucleus(segmentsInWord, syl, seg1, i);
				} else {
					if (fDoTrace) {
						traceInfo.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);
						syllabifierTraceInfoList.add(traceInfo);
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
								traceInfo
										.setStatus(ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE);
								syllabifierTraceInfoList.add(traceInfo);
								traceInfo = new ONCTraceSyllabifierInfo();
							}
						} else if (!seg2.isOnset()
								&& opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
							i--;
							syllablesInCurrentWord.add(syl);
							syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
							currentType = ONCType.ONSET_OR_NUCLEUS;
							if (fDoTrace) {
								traceInfo
										.setStatus(ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE);
								syllabifierTraceInfoList.add(traceInfo);
								traceInfo = new ONCTraceSyllabifierInfo();
							}
						} else {
							syl = addSegmentToSyllableAsCodaStartNewSyllable(segmentsInWord, syl, i);
							currentType = updateTypeForNewSyllable();
						}
					} else {
						if (result == SHComparisonResult.EQUAL) {
							currentType = addSegmentToSyllableAsCoda(segmentsInWord, syl, i);
						} else {
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
							traceInfo
									.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
						} else {
							traceInfo
									.setStatus(ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE);
						}
						syllabifierTraceInfoList.add(traceInfo);
						traceInfo = new ONCTraceSyllabifierInfo();
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
						traceInfo.setStatus(ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
						syllabifierTraceInfoList.add(traceInfo);
						traceInfo = new ONCTraceSyllabifierInfo();
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
//						traceInfo = new ONCTraceSyllabifierInfo();
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
				traceInfo.setSegment1(seg);
				traceInfo.setStatus(ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);
				syllabifierTraceInfoList.add(traceInfo);
			}
		}
		return true;
	}

	protected ONCType addSegmentToSyllableAsCoda(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, int i) {
		ONCType currentType;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
		currentType = ONCType.CODA_OR_ONSET;
		if (fDoTrace) {
			traceInfo.setSegment1(segmentsInWord.get(i).getSegment());
			traceInfo.setOncType(ONCType.CODA);
			traceInfo.setStatus(ONCSyllabificationStatus.ADDED_AS_CODA);
			syllabifierTraceInfoList.add(traceInfo);
			traceInfo = new ONCTraceSyllabifierInfo();
		}
		return currentType;
	}

	protected ONCType addSegmentToSyllableAsNucleus(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, Segment seg1, int i) {
		ONCType currentType;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.NUCLEUS);
		syl.add(segmentsInWord.get(i));
		currentType = ONCType.NUCLEUS_OR_CODA;
		if (fDoTrace) {
			traceInfo.setOncType(ONCType.NUCLEUS);
			traceInfo.setStatus(ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
			syllabifierTraceInfoList.add(traceInfo);
			traceInfo = new ONCTraceSyllabifierInfo();
		}
		return currentType;
	}

	protected ONCType addSegmentToSyllableAsOnset(List<ONCSegmentInSyllable> segmentsInWord,
			ONCSyllable syl, Segment seg1, int i) {
		ONCType currentType;
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.ONSET);
		syl.add(segmentsInWord.get(i));
		currentType = ONCType.ONSET_OR_NUCLEUS;
		if (fDoTrace) {
			traceInfo.setOncType(ONCType.ONSET);
			traceInfo.setStatus(ONCSyllabificationStatus.ADDED_AS_ONSET);
			syllabifierTraceInfoList.add(traceInfo);
			traceInfo = new ONCTraceSyllabifierInfo();
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
		syllablesInCurrentWord.add(syl);
		syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
		if (fDoTrace) {
			traceInfo.setSegment1(segmentsInWord.get(i).getSegment());
			traceInfo.setOncType(ONCType.CODA);
			traceInfo.setStatus(ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);
			syllabifierTraceInfoList.add(traceInfo);
			traceInfo = new ONCTraceSyllabifierInfo();
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
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (ONCSyllable syl : syllablesInCurrentWord) {
			for (ONCSegmentInSyllable seg : syl.getSegmentsInSyllable()) {
				sb.append(seg.getUsageAString());
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	public String getNaturalClassesInCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = syllabifierTraceInfoList.size();
		for (int i = 0; i < iSize; i++) {
			ONCTraceSyllabifierInfo info = syllabifierTraceInfoList.get(i);
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
		return syllabifierTraceInfoList.stream().map(ONCTraceSyllabifierInfo::getComparisonResult)
				.collect(Collectors.joining(", "));
	}
}
