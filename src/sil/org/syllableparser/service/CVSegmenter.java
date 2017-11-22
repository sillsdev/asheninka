// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.GraphemeNaturalClass;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         a Service Takes a word string and parses it into a sequence of
 *         segments
 */
public class CVSegmenter {

	// private final List<Segment> activeSegmentInventory;
	private final List<Grapheme> activeGraphemes;
	private final List<GraphemeNaturalClass> activeClasses;
	List<CVSegmentInSyllable> segmentsInCurrentWord = new LinkedList<CVSegmentInSyllable>(
			Arrays.asList(new CVSegmentInSyllable(null, null)));
	HashMap<String, List<Grapheme>> graphemeToSegmentMapping = new HashMap<>();
	int iLongestGrapheme = 0;

	// public CVSegmenter(List<Segment> activeSegmentInventory) {
	// super();
	// this.activeSegmentInventory = activeSegmentInventory;
	// this.activeGraphemes = null;
	// buildGraphemeToCVSegmentMapping();
	// }

	public CVSegmenter(List<Grapheme> activeGraphemes, List<GraphemeNaturalClass> activeClasses) {
		super();
		this.activeGraphemes = activeGraphemes;
		this.activeClasses = activeClasses;
		buildGraphemeToCVSegmentMapping();
	}

	protected void buildGraphemeToCVSegmentMapping() {
		for (Grapheme graph : activeGraphemes) {
			String sForm = graph.getForm();
			if (sForm.length() > iLongestGrapheme) {
				iLongestGrapheme = sForm.length();
			}
			List<Grapheme> graphs;
			if (graphemeToSegmentMapping.containsKey(sForm)) {
				graphs = graphemeToSegmentMapping.get(sForm);
				graphs.add(graph);
			} else {
				graphs = new ArrayList<Grapheme>();
				graphs.add(graph);
			}
			graphemeToSegmentMapping.put(sForm, graphs);
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

	public HashMap<String, List<Grapheme>> getGraphemeToSegmentMapping() {
		return graphemeToSegmentMapping;
	}

	public void setGraphemeToSegmentMapping(HashMap<String, List<Grapheme>> graphemes) {
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
				List<Grapheme> graphsForThisForm = graphemeToSegmentMapping.get(sPotentialGrapheme);
				if (graphsForThisForm != null && graphsForThisForm.size() > 0) {
					boolean fIsMatch = false;
					for (Grapheme grapheme : graphsForThisForm) {
						CVSegmenterGraphemeResult graphemeResult = new CVSegmenterGraphemeResult();
						graphemeResult.sGrapheme = grapheme.getForm();
						result.graphemesTried.add(graphemeResult);
						if (!grapheme.matchesAnEnvironment(word.substring(0, iStart),
								word.substring(iStart + iSegLength), activeClasses, graphemeResult)) {
							continue;
						}
						Segment seg = grapheme.getOwningSegment();
						if (seg != null) {
							CVSegmentInSyllable segmentInSyllable = new CVSegmentInSyllable(seg,
									sPotentialGrapheme);
							segmentsInCurrentWord.add(segmentInSyllable);
							fIsMatch = true;
							break;
						}
					}
					if (fIsMatch) {
						break;
					}
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
