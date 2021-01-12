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
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         a Service Takes a word string and parses it into a sequence of
 *         segments
 */
public class MoraicSegmenter extends CVSegmenter {

	List<MoraicSegmentInSyllable> moraicSegmentsInCurrentWord = new LinkedList<MoraicSegmentInSyllable>(
			Arrays.asList(new MoraicSegmentInSyllable(null, null)));

	public MoraicSegmenter(List<Grapheme> activeGraphemes, List<GraphemeNaturalClass> activeClasses) {
		super(activeGraphemes, activeClasses);
	}

	public List<MoraicSegmentInSyllable> getSegmentsInWord() {
		return moraicSegmentsInCurrentWord;
	}

	public void setONCSegments(List<MoraicSegmentInSyllable> segments) {
		this.moraicSegmentsInCurrentWord = segments;
	}

	@Override
	public CVSegmenterResult segmentWord(String word) {
		moraicSegmentsInCurrentWord.clear();
		return super.segmentWord(word);
	}
	protected void addSegmentInSyllable(String sPotentialGrapheme, Segment seg) {
		MoraicSegmentInSyllable segmentInSyllable = new MoraicSegmentInSyllable(seg,
				sPotentialGrapheme);
		moraicSegmentsInCurrentWord.add(segmentInSyllable);
	}
}
