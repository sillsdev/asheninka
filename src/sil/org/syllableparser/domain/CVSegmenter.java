/**
 * 
 */
package sil.org.syllableparser.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sil.org.syllableparser.model.CVSegment;

/**
 * @author Andy Black
 *
 */
public class CVSegmenter {

	List<CVSegment> segmentInventory;
	List<CVSegment> segmentsInCurrentWord = new LinkedList<CVSegment>(
			Arrays.asList(new CVSegment()));
	HashMap<String, CVSegment> graphemes = new HashMap<>();
	int iLongestGrapheme = 0;

	public CVSegmenter(List<CVSegment> segmentInventory) {
		super();
		this.segmentInventory = segmentInventory;
		for (CVSegment seg : segmentInventory) {
			String[] orthographemes = seg.getGraphemes().split(" +");
			for (String orthoform : orthographemes) {
				if (orthoform.length() > iLongestGrapheme) {
					iLongestGrapheme = orthoform.length();
				}
				graphemes.put(orthoform, seg);
			}
		}
	}

	public List<CVSegment> getSegmentInventory() {
		return segmentInventory;
	}

	public void setSegmentInventory(List<CVSegment> segmentInventory) {
		this.segmentInventory = segmentInventory;
	}

	public List<CVSegment> getSegmentsInWord() {
		return segmentsInCurrentWord;
	}

	public void setSegments(List<CVSegment> segments) {
		this.segmentsInCurrentWord = segments;
	}

	public HashMap<String, CVSegment> getGraphemes() {
		return graphemes;
	}

	public void setGraphemes(HashMap<String, CVSegment> graphemes) {
		this.graphemes = graphemes;
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
				String sTemp = word.substring(iStart, iStart + iSegLength);
				CVSegment seg = graphemes.get(sTemp);
				if (seg != null) {
					segmentsInCurrentWord.add(seg);
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
