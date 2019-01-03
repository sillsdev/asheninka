// Copyright (c) 2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceSyllabifierInfo;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class SHSyllabifier {

	private LanguageProject languageProject;
	private SHApproach sonHierApproach;
	private List<Segment> activeSegmentInventory;
	private List<SHNaturalClass> activeNaturalClasses;
	private CVSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	private boolean fDoTrace = false;
	private List<SHTraceSyllabifierInfo> syllabifierTraceInfo = new ArrayList<SHTraceSyllabifierInfo>();

	LinkedList<SHSyllable> syllablesInCurrentWord = new LinkedList<SHSyllable>(
			Arrays.asList(new SHSyllable(null)));
	String sSyllabifiedWord;

	public SHSyllabifier(SHApproach sonHierApproach) {
		super();
		this.sonHierApproach = sonHierApproach;
		languageProject = sonHierApproach.getLanguageProject();
		activeSegmentInventory = languageProject.getActiveSegmentsInInventory();
		activeNaturalClasses = sonHierApproach.getActiveSHNaturalClasses();
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
		return syllabifierTraceInfo;
	}

	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public boolean convertSegmentstoSyllables() {
//		syllablesInCurrentWord.clear();
//		syllabifierTraceInfo.clear();
//
//		// recursively parse into syllables
//		boolean result = parseIntoSyllables(naturalClassListInCurrentWord,
//				syllabifierTraceInfo, true);
//
//		if (result) {
//			// the list of syllables found is in reverse order; flip them
//			Collections.reverse(syllablesInCurrentWord);
//		}
		return false;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
				fSuccess = parseIntoSyllables(segmentsInWord,
						syllabifierTraceInfo, true);

				if (fSuccess) {
					// the list of syllables found is in reverse order; flip
					// them
					Collections.reverse(syllablesInCurrentWord);
				}
			}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<CVSegmentInSyllable> segmentsInWord,
			List<SHTraceSyllabifierInfo> sylTraceInfo,
			Boolean isWordInitial) {
		if (segmentsInWord.size() == 0) {
			return true;
		}
		boolean fResult = syllabify(segmentsInWord);
		SHTraceSyllabifierInfo sylInfo = new SHTraceSyllabifierInfo("");
		return fResult;
	}

	public boolean syllabify(List<CVSegmentInSyllable> segmentsInWord) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();
		int segmentCount = segmentsInWord.size();  
		if (segmentCount == 0) {
			return false;
		}
		SHSyllable syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
		syl.add(segmentsInWord.get(0));
//		syllablesInCurrentWord.add(syl);
		int i = 1;
		while (i < segmentCount) {
			Segment seg1 = segmentsInWord.get(i-1).getSegment();
			Segment seg2 = segmentsInWord.get(i).getSegment(); 
			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
			if (result == SHComparisonResult.MORE) {
				int j = i+1;
				if (j < segmentCount) {
					Segment seg3 = segmentsInWord.get(j).getSegment();
					result = sonorityComparer.compare(seg2, seg3);
					if (result == SHComparisonResult.EQUAL ||
							result == SHComparisonResult.MORE) {
						syl.add(segmentsInWord.get(i));
//						syl.add(segmentsInWord.get(j));
						i++;
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
			}
			i++;
		}
		if (syl.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syl);
		}		
		return true;
	}
	
	public boolean syllabifyOld(List<CVSegmentInSyllable> segmentsInWord) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();
		int segmentCount = segmentsInWord.size();  
		if (segmentCount == 0) {
			return false;
		}
		segmentCount--;
		SHSyllable syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
		Segment seg1 = null;
		Segment seg2 = null; 
		Segment seg3 = null;
		int i = -1;
		while (i < segmentCount) {
			if (i == -1) {
				seg1 = null;
			} else {
				seg1 = segmentsInWord.get(i).getSegment();
			}
			seg2 = segmentsInWord.get(i+1).getSegment();
			if ((i+1) < segmentCount) {
				seg3 = segmentsInWord.get(i+2).getSegment();
			} else {
				seg3 = null;
			}
			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
			if (result == SHComparisonResult.MORE) {
				result = sonorityComparer.compare(seg2, seg3);
				if (result == SHComparisonResult.EQUAL) {
					syl.add(segmentsInWord.get(++i));					
				}
				syllablesInCurrentWord.add(syl);
				syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
				syl.add(segmentsInWord.get(++i));
			} else if (result == SHComparisonResult.LESS) {
				syl.add(segmentsInWord.get(++i));
			} else if (result == SHComparisonResult.EQUAL) {
				syl.add(segmentsInWord.get(i));
				result = sonorityComparer.compare(seg2, seg3);
				if (result == SHComparisonResult.LESS) {
					i++;
				}
//				syllablesInCurrentWord.add(syl);
//				syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
			} else {
				System.out.println("syllabify: result=" + result);
			}
			//i++;
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

}
