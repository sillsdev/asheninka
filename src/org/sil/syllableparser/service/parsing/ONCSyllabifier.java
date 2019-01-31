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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
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
	private ONCApproach sonHierApproach;
	private CVSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	private boolean fDoTrace = false;
	private List<ONCTraceSyllabifierInfo> syllabifierTraceInfoList = new ArrayList<ONCTraceSyllabifierInfo>();

	LinkedList<ONCSyllable> syllablesInCurrentWord = new LinkedList<ONCSyllable>(
			Arrays.asList(new ONCSyllable(null)));
	String sSyllabifiedWord;

	public ONCSyllabifier(ONCApproach oncApproach) {
		super();
		this.sonHierApproach = oncApproach;
		languageProject = oncApproach.getLanguageProject();
		segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
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
		ONCTraceSyllabifierInfo traceInfo = null;
		boolean fLastStartedSyllable = true;
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		ONCSyllable syl = new ONCSyllable(new ArrayList<CVSegmentInSyllable>());
		syl.add(segmentsInWord.get(0));
		Segment seg1 = segmentsInWord.get(0).getSegment();
		SHNaturalClass natClass = sonHierApproach.getNaturalClassContainingSegment(seg1);
		if (natClass == null) {
			if (fDoTrace) {
				traceInfo = new ONCTraceSyllabifierInfo(seg1, null, null, null, SHComparisonResult.MISSING1);
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
				traceInfo = new ONCTraceSyllabifierInfo(seg1,
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
							traceInfo = new ONCTraceSyllabifierInfo(seg2,
									sonHierApproach.getNaturalClassContainingSegment(seg2), seg3,
									sonHierApproach.getNaturalClassContainingSegment(seg3), result);
							syllabifierTraceInfoList.add(traceInfo);
						}
					}
					syllablesInCurrentWord.add(syl);
					syl = new ONCSyllable(new ArrayList<CVSegmentInSyllable>());
					syl.add(segmentsInWord.get(i));
					fLastStartedSyllable = true;
				} else {
					syl.add(segmentsInWord.get(i));
				}
			} else if (result == SHComparisonResult.LESS) {
				syl.add(segmentsInWord.get(i));
			} else if (result == SHComparisonResult.EQUAL) {
				syl.add(segmentsInWord.get(i));
			} else {
				return false;
			}
			i++;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
			if (fDoTrace && fLastStartedSyllable) {
				Segment seg = segmentsInWord.get(segmentCount -1).getSegment();
				traceInfo = new ONCTraceSyllabifierInfo(seg,
						sonHierApproach.getNaturalClassContainingSegment(seg), null,
						null, null);
				traceInfo.startsSyllable = true;
				syllabifierTraceInfoList.add(traceInfo);
			}
		}
		return true;
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
