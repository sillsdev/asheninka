// Copyright (c) 2019 SIL International 
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
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         a Service Takes a word string and parses it into a sequence of
 *         segments
 */
public class ONCSegmenter extends CVSegmenter {

	List<ONCSegmentInSyllable> oncSegmentsInCurrentWord = new LinkedList<ONCSegmentInSyllable>(
			Arrays.asList(new ONCSegmentInSyllable(null, null)));

	public ONCSegmenter(List<Grapheme> activeGraphemes, List<GraphemeNaturalClass> activeClasses) {
		super(activeGraphemes, activeClasses);
	}

	public List<ONCSegmentInSyllable> getONCSegmentsInWord() {
		return oncSegmentsInCurrentWord;
	}

	public void setONCSegments(List<ONCSegmentInSyllable> segments) {
		this.oncSegmentsInCurrentWord = segments;
	}

	@Override
	public CVSegmenterResult segmentWord(String word) {
		oncSegmentsInCurrentWord.clear();
		return super.segmentWord(word);
	}
	protected void addSegmentInSyllable(String sPotentialGrapheme, Segment seg) {
		ONCSegmentInSyllable segmentInSyllable = new ONCSegmentInSyllable(seg,
				sPotentialGrapheme);
		oncSegmentsInCurrentWord.add(segmentInSyllable);
	}
}
