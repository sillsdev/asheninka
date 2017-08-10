// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 * a Service
 * Takes a word string and parses it into a sequence of segments
 */
public class CVSegmenter {

	private final List<Segment> activeSegmentInventory;
	List<CVSegmentInSyllable> segmentsInCurrentWord = new LinkedList<CVSegmentInSyllable>(
			Arrays.asList(new CVSegmentInSyllable(null, null)));
	HashMap<String, Segment> graphemeToSegmentMapping = new HashMap<>();
	int iLongestGrapheme = 0;

	public CVSegmenter(List<Segment> activeSegmentInventory) {
		super();
		this.activeSegmentInventory = activeSegmentInventory;
		buildGraphemeToCVSegmentMapping();
	}

	protected void buildGraphemeToCVSegmentMapping() {
		// TODO: use lambda expressions for this
		for (Segment seg : activeSegmentInventory) {
			List<String> orthographemes = seg.getGraphemes().stream().map(Grapheme::getForm).collect(Collectors.toList());
			for (String orthoform : orthographemes) {
				if (orthoform.length() > iLongestGrapheme) {
					iLongestGrapheme = orthoform.length();
				}
				graphemeToSegmentMapping.put(orthoform, seg);
			}
		}
	}

	public List<Segment> getActiveSegmentInventory() {
		return activeSegmentInventory;
	}

	public List<CVSegmentInSyllable> getSegmentsInWord() {
		return segmentsInCurrentWord;
	}

	public void setSegments(List<CVSegmentInSyllable> segments) {
		this.segmentsInCurrentWord = segments;
	}

	public HashMap<String, Segment> getGraphemeToSegmentMapping() {
		return graphemeToSegmentMapping;
	}

	public void setGraphemeToSegmentMapping(HashMap<String, Segment> graphemes) {
		this.graphemeToSegmentMapping = graphemes;
	}

	public CVSegmenterResult segmentWord(String word) {
		segmentsInCurrentWord.clear();
		
		CVSegmenterResult result = new CVSegmenterResult();

		if (word == null || word.isEmpty()) {
			return result;
		}

		int iSegLength;
		// work way through word left to right
		for (int iStart = 0; iStart < word.length(); iStart += iSegLength) {
			// Look for longest match
			for (iSegLength = iLongestGrapheme; iSegLength > 0; iSegLength--) {
				if ((iStart + iSegLength) > word.length()) {
					continue;
				}
				String sPotentialGrapheme = word.substring(iStart, iStart + iSegLength);
				Segment seg = graphemeToSegmentMapping.get(sPotentialGrapheme);
				if (seg != null) {
					CVSegmentInSyllable segmentInSyllable = new CVSegmentInSyllable(seg, sPotentialGrapheme);
					segmentsInCurrentWord.add(segmentInSyllable);
					break;
				}
			}
			if (iSegLength == 0) {
				result.iPositionOfFailure = iStart;
				result.success = false;
				return result;
			}
		}
		return result;
	}
}
