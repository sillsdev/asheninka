// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         a Service Takes a word string and parses it into a sequence of
 *         segments
 */
public class NPSegmenter extends CVSegmenter {

	List<NPSegmentInSyllable> npSegmentsInCurrentWord = new LinkedList<NPSegmentInSyllable>(
			Arrays.asList(new NPSegmentInSyllable(null, null)));

	public NPSegmenter(List<Grapheme> activeGraphemes, List<GraphemeNaturalClass> activeClasses) {
		super(activeGraphemes, activeClasses);
	}

	public List<NPSegmentInSyllable> getSegmentsInWord() {
		return npSegmentsInCurrentWord;
	}

	public void setONCSegments(List<NPSegmentInSyllable> segments) {
		this.npSegmentsInCurrentWord = segments;
	}

	@Override
	public CVSegmenterResult segmentWord(String word) {
		npSegmentsInCurrentWord.clear();
		return super.segmentWord(word);
	}
	protected void addSegmentInSyllable(String sPotentialGrapheme, Segment seg) {
		NPSegmentInSyllable segmentInSyllable = new NPSegmentInSyllable(seg,
				sPotentialGrapheme);
		npSegmentsInCurrentWord.add(segmentInSyllable);
	}
}
