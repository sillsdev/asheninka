/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sil.org.syllableparser.model.Segment;
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
			String[] orthographemes = seg.getGraphemes().split(" +");
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

	public boolean segmentWord(String word) {
		segmentsInCurrentWord.clear();

		if (word == null || word.isEmpty()) {
			return true;
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
				// TODO: give report of how far we got or some such so user knows
				return false;
			}
		}
		return true;
	}
}
