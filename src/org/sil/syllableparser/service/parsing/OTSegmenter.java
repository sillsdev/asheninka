// Copyright (c) 2021-2025 SIL International 
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
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         a Service Takes a word string and parses it into a sequence of
 *         segments
 */
public class OTSegmenter extends CVSegmenter {

	List<OTSegmentInSyllable> otSegmentsInCurrentWord = new LinkedList<OTSegmentInSyllable>(
			Arrays.asList(new OTSegmentInSyllable(null, null)));

	public OTSegmenter(List<Grapheme> activeGraphemes, List<GraphemeNaturalClass> activeClasses) {
		super(activeGraphemes, activeClasses);
	}

	public List<OTSegmentInSyllable> getSegmentsInWord() {
		return otSegmentsInCurrentWord;
	}

	public void setOTSegments(List<OTSegmentInSyllable> segments) {
		this.otSegmentsInCurrentWord = segments;
	}

	@Override
	public CVSegmenterResult segmentWord(String word) {
		otSegmentsInCurrentWord.clear();
		return super.segmentWord(word);
	}

	protected void addSegmentInSyllable(String sPotentialGrapheme, Segment seg) {
		OTSegmentInSyllable segmentInSyllable = new OTSegmentInSyllable(seg,
				sPotentialGrapheme);
		otSegmentsInCurrentWord.add(segmentInSyllable);
	}
}
