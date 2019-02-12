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
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationErrorType;
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
	private List<ONCTraceSyllabifierInfo> syllabifierTraceInfoList = new ArrayList<ONCTraceSyllabifierInfo>();
	private SyllabificationParameters sylParams;
	private boolean codasAllowed;
	private boolean onsetMaximization;
	private OnsetPrincipleType opType;

	enum ONCType {
		CODA, NUCLEUS, NUCLEUS_OR_CODA, ONSET, ONSET_OR_NUCLEUS, UNKNOWN
	}

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
		ONCTraceSyllabifierInfo traceInfo = null;
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		ONCSyllable syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
		// syl.add(segmentsInWord.get(0));
		Segment seg1 = segmentsInWord.get(0).getSegment();
		if (seg1 == null) {
			return false;
		}
		if (opType == OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET && !seg1.isOnset()) {
			if (fDoTrace) {
				traceInfo = new ONCTraceSyllabifierInfo(seg1,
						ONCSyllabificationErrorType.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);
				syllabifierTraceInfoList.add(traceInfo);
			}
			return false;
		}
		SHNaturalClass natClass = oncApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			if (fDoTrace) {
				traceInfo = new ONCTraceSyllabifierInfo(seg1,
						ONCSyllabificationErrorType.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
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
				traceInfo = new ONCTraceSyllabifierInfo(seg1,
						oncApproach.getNaturalClassContainingSegment(seg1), seg2,
						oncApproach.getNaturalClassContainingSegment(seg2), result);
				syllabifierTraceInfoList.add(traceInfo);
			}

			switch (currentType) {
			case UNKNOWN:
			case ONSET:
				if (seg1.isOnset()
						&& (result == SHComparisonResult.LESS || result == SHComparisonResult.EQUAL)) {
					segmentsInWord.get(i).setUsage(ONCSegmentUsageType.ONSET);
					syl.add(segmentsInWord.get(i));
					currentType = ONCType.ONSET_OR_NUCLEUS;
				} else {
					i--;
					currentType = ONCType.NUCLEUS;
				}
				break;
			case ONSET_OR_NUCLEUS:
				if (seg1.isOnset()
						&& (result == SHComparisonResult.LESS || result == SHComparisonResult.EQUAL)) {
					segmentsInWord.get(i).setUsage(ONCSegmentUsageType.ONSET);
					syl.add(segmentsInWord.get(i));
					currentType = ONCType.ONSET_OR_NUCLEUS;
				} else if (seg1.isNucleus()) {
					segmentsInWord.get(i).setUsage(ONCSegmentUsageType.NUCLEUS);
					syl.add(segmentsInWord.get(i));
					currentType = ONCType.NUCLEUS_OR_CODA;
				} else {
					System.out.println("Failure:"
							+ ONCSyllabificationErrorType.EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND);
					return false;
				}
				break;
			case NUCLEUS:
				if (seg1.isNucleus()) {
					segmentsInWord.get(i).setUsage(ONCSegmentUsageType.NUCLEUS);
					syl.add(segmentsInWord.get(i));
					currentType = ONCType.NUCLEUS_OR_CODA;
				} else {
					System.out.println("Failure:"
							+ ONCSyllabificationErrorType.EXPECTED_NUCLEUS_NOT_FOUND);
					return false;
				}
				break;
			case NUCLEUS_OR_CODA:
				if (seg1.isNucleus()) {
					segmentsInWord.get(i).setUsage(ONCSegmentUsageType.NUCLEUS);
					syl.add(segmentsInWord.get(i));
					currentType = ONCType.NUCLEUS_OR_CODA;
				} else if (seg1.isCoda() && codasAllowed) {
					if (seg1.isOnset() && result == SHComparisonResult.LESS) {
						if (onsetMaximization) {
							i--;
							syllablesInCurrentWord.add(syl);
							syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
							currentType = updateTypeForNewSyllable();
						} else if (!seg2.isOnset()
								&& opType != OnsetPrincipleType.ONSETS_NOT_REQUIRED) {
							i--;
							syllablesInCurrentWord.add(syl);
							syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
							currentType = ONCType.ONSET_OR_NUCLEUS;
						} else {
							syl = markAsCoda(segmentsInWord, syl, i);
							currentType = updateTypeForNewSyllable();
						}
					} else {
						syl = markAsCoda(segmentsInWord, syl, i);
						currentType = updateTypeForNewSyllable();
					}
				} else {
					i--;
					syllablesInCurrentWord.add(syl);
					syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
					currentType = ONCType.ONSET_OR_NUCLEUS;
					// System.out.println("Failure:" +
					// ONCSyllabificationErrorType.EXPECTED_NUCLEUS_OR_CODA_NOT_FOUND);
					// return false;
				}
				break;
			case CODA:
				if (codasAllowed) {
					if (seg1.isCoda() && result == SHComparisonResult.LESS) {
						syl = markAsCoda(segmentsInWord, syl, i);
						currentType = ONCType.ONSET_OR_NUCLEUS;
					} else {
						System.out.println("Failure:"
								+ ONCSyllabificationErrorType.EXPECTED_CODA_NOT_FOUND);
						return false;
					}
				} else {
					i--;
					currentType = ONCType.ONSET_OR_NUCLEUS;
				}
				break;
			}
			i++;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
			if (fDoTrace) {
				Segment seg = segmentsInWord.get(segmentCount - 1).getSegment();
				traceInfo = new ONCTraceSyllabifierInfo(seg,
						oncApproach.getNaturalClassContainingSegment(seg), null, null, null);
				traceInfo.startsSyllable = true;
				syllabifierTraceInfoList.add(traceInfo);
			}
		}
		return true;
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

	protected ONCSyllable markAsCoda(List<ONCSegmentInSyllable> segmentsInWord, ONCSyllable syl,
			int i) {
		segmentsInWord.get(i).setUsage(ONCSegmentUsageType.CODA);
		syl.add(segmentsInWord.get(i));
		syllablesInCurrentWord.add(syl);
		syl = new ONCSyllable(new ArrayList<ONCSegmentInSyllable>());
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
