/**
 * 
 */
package sil.org.syllableparser.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.valueobject.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 * Takes a word string and parses it into a sequence of segments
 */
public class CVSegmenter {

	private final List<CVSegment> segmentInventory;
	List<CVSegmentInSyllable> segmentsInCurrentWord = new LinkedList<CVSegmentInSyllable>(
			Arrays.asList(new CVSegmentInSyllable(null, null)));
	HashMap<String, CVSegment> graphemeToSegmentMapping = new HashMap<>();
	int iLongestGrapheme = 0;

	public CVSegmenter(List<CVSegment> segmentInventory) {
		super();
		this.segmentInventory = segmentInventory;
		buildGraphemeToCVSegmentMapping();
	}

	protected void buildGraphemeToCVSegmentMapping() {
		for (CVSegment seg : segmentInventory) {
			String[] orthographemes = seg.getGraphemes().split(" +");
			for (String orthoform : orthographemes) {
				if (orthoform.length() > iLongestGrapheme) {
					iLongestGrapheme = orthoform.length();
				}
				graphemeToSegmentMapping.put(orthoform, seg);
			}
		}
	}

	public List<CVSegment> getSegmentInventory() {
		return segmentInventory;
	}

	public List<CVSegmentInSyllable> getSegmentsInWord() {
		return segmentsInCurrentWord;
	}

	public void setSegments(List<CVSegmentInSyllable> segments) {
		this.segmentsInCurrentWord = segments;
	}

	public HashMap<String, CVSegment> getGraphemeToSegmentMapping() {
		return graphemeToSegmentMapping;
	}

	public void setGraphemeToSegmentMapping(HashMap<String, CVSegment> graphemes) {
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
				CVSegment seg = graphemeToSegmentMapping.get(sPotentialGrapheme);
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
