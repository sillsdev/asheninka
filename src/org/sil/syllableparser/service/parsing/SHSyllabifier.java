// Copyright (c) 2018-2019 SIL International
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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTracingStep;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class SHSyllabifier implements Syllabifiable {

	private LanguageProject languageProject;
	private SHApproach sonHierApproach;
	private CVSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	private boolean fDoTrace = false;
	private List<SHTracingStep> syllabifierTraceInfoList = new ArrayList<SHTracingStep>();

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

	public List<SHTracingStep> getSyllabifierTraceInfo() {
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
			List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			fSuccess = parseIntoSyllables(segmentsInWord);
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<? extends CVSegmentInSyllable> segmentsInWord) {
		if (segmentsInWord.size() == 0) {
			return false;
		}
		boolean fResult = syllabify(segmentsInWord);
		return fResult;
	}

	public boolean syllabify(List<? extends CVSegmentInSyllable> segmentsInWord) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfoList.clear();
		SHTracingStep traceInfo = null;
		boolean fLastStartedSyllable = true;
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		SHSyllable syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
		syl.add(segmentsInWord.get(0));
		Segment seg1 = segmentsInWord.get(0).getSegment();
		SHNaturalClass natClass = sonHierApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			if (fDoTrace) {
				traceInfo = new SHTracingStep(seg1, null, null, null, SHComparisonResult.MISSING1);
				syllabifierTraceInfoList.add(traceInfo);
			}
			return false;
		}
		int i = 1;
		while (i < segmentCount) {
			seg1 = segmentsInWord.get(i - 1).getSegment();
			Segment seg2 = segmentsInWord.get(i).getSegment();
			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
			if (fDoTrace) {
				traceInfo = new SHTracingStep(seg1,
						sonHierApproach.getNaturalClassContainingSegment(seg1), seg2,
						sonHierApproach.getNaturalClassContainingSegment(seg2), result);
				if (fLastStartedSyllable) {
					traceInfo.startsSyllable = true;
					fLastStartedSyllable = false;
				}
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
							traceInfo = new SHTracingStep(seg2,
									sonHierApproach.getNaturalClassContainingSegment(seg2), seg3,
									sonHierApproach.getNaturalClassContainingSegment(seg3), result);
							syllabifierTraceInfoList.add(traceInfo);
						}
					}
					syl = endThisSyllableStartNew(segmentsInWord, syl, i);
					fLastStartedSyllable = true;
				} else {
					syl.add(segmentsInWord.get(i));
				}
			} else if (result == SHComparisonResult.LESS) {
				syl.add(segmentsInWord.get(i));
			} else if (result == SHComparisonResult.EQUAL) {
				syl = endThisSyllableStartNew(segmentsInWord, syl, i);
				fLastStartedSyllable = true;
			} else {
				return false;
			}
			i++;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
			if (fDoTrace && fLastStartedSyllable) {
				Segment seg = segmentsInWord.get(segmentCount -1).getSegment();
				traceInfo = new SHTracingStep(seg,
						sonHierApproach.getNaturalClassContainingSegment(seg), null,
						null, null);
				traceInfo.startsSyllable = true;
				syllabifierTraceInfoList.add(traceInfo);
			}
		}
		return true;
	}

	protected SHSyllable endThisSyllableStartNew(
			List<? extends CVSegmentInSyllable> segmentsInWord, SHSyllable syl, int i) {
		syllablesInCurrentWord.add(syl);
		syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
		syl.add(segmentsInWord.get(i));
		return syl;
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

	@Override
	public String getLingTreeDescriptionOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		sb.append("(W");
		for (SHSyllable syl : syllablesInCurrentWord) {
			sb.append("(σ");
			for (CVSegmentInSyllable seg : syl.getSegmentsInSyllable()) {
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

	public String getNaturalClassesInCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = syllabifierTraceInfoList.size();
		for (int i = 0; i < iSize; i++) {
			SHTracingStep info = syllabifierTraceInfoList.get(i);
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
		return syllabifierTraceInfoList.stream().map(SHTracingStep::getComparisonResult)
				.collect(Collectors.joining(", "));
	}
}
