// Copyright (c) 2018-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceSyllabifierInfo;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class SHSyllabifier {

	private LanguageProject languageProject;
	private SHApproach sonHierApproach;
	private CVSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	private boolean fDoTrace = false;
	private List<SHTraceSyllabifierInfo> syllabifierTraceInfoList = new ArrayList<SHTraceSyllabifierInfo>();

	LinkedList<SHSyllable> syllablesInCurrentWord = new LinkedList<SHSyllable>(
			Arrays.asList(new SHSyllable(null)));
	String sSyllabifiedWord;

	public SHSyllabifier(SHApproach sonHierApproach) {
		super();
		this.sonHierApproach = sonHierApproach;
		languageProject = sonHierApproach.getLanguageProject();
		segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		sonorityComparer = new SHSonorityComparer(sonHierApproach);
		sSyllabifiedWord = "";
	}

	public List<SHSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<SHSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<SHTraceSyllabifierInfo> getSyllabifierTraceInfo() {
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
			List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			fSuccess = parseIntoSyllables(segmentsInWord);
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<CVSegmentInSyllable> segmentsInWord) {
		if (segmentsInWord.size() == 0) {
			return false;
		}
		boolean fResult = syllabify(segmentsInWord);
		return fResult;
	}

	public boolean syllabify(List<CVSegmentInSyllable> segmentsInWord) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfoList.clear();
		SHTraceSyllabifierInfo traceInfo;
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		SHSyllable syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
		syl.add(segmentsInWord.get(0));
		int i = 1;
		while (i < segmentCount) {
			Segment seg1 = segmentsInWord.get(i - 1).getSegment();
			Segment seg2 = segmentsInWord.get(i).getSegment();
			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
			if (fDoTrace) {
				traceInfo = new SHTraceSyllabifierInfo(seg1,
						sonHierApproach.getNaturalClassContainingSegment(seg1), seg2,
						sonHierApproach.getNaturalClassContainingSegment(seg2), result);
				syllabifierTraceInfoList.add(traceInfo);
			}
			if (result == SHComparisonResult.MORE) {
				int j = i + 1;
				if (j < segmentCount) {
					Segment seg3 = segmentsInWord.get(j).getSegment();
					result = sonorityComparer.compare(seg2, seg3);
					if (result == SHComparisonResult.EQUAL || result == SHComparisonResult.MORE) {
						syl.add(segmentsInWord.get(i));
						i++;
						if (fDoTrace) {
							traceInfo = new SHTraceSyllabifierInfo(seg2,
									sonHierApproach.getNaturalClassContainingSegment(seg2), seg3,
									sonHierApproach.getNaturalClassContainingSegment(seg3), result);
							syllabifierTraceInfoList.add(traceInfo);
						}
					}
				} else {
					syl.add(segmentsInWord.get(i));
				}
				if (j < segmentCount) {
					syllablesInCurrentWord.add(syl);
					syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
					syl.add(segmentsInWord.get(i));
				}
			} else if (result == SHComparisonResult.LESS) {
				syl.add(segmentsInWord.get(i));
			} else if (result == SHComparisonResult.EQUAL) {
				syl.add(segmentsInWord.get(i));
			} else {
				System.out.println("syllabify: result=" + result);
				return false;
			}
			i++;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
		}
		return true;
	}

	public String getSyllabificationOfCurrentWord() {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (SHSyllable syl : syllablesInCurrentWord) {
			for (CVSegmentInSyllable seg : syl.getSegmentsInSyllable()) {
				sb.append(seg.getGrapheme());
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
			SHTraceSyllabifierInfo info = syllabifierTraceInfoList.get(i);
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
		return syllabifierTraceInfoList.stream().map(SHTraceSyllabifierInfo::getComparisonResult)
				.collect(Collectors.joining(", "));
	}
}
