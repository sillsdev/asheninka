/**
 * 
 */
package sil.org.syllableparser.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.valueobject.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.valueobject.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 * Takes a sequence of segments and parses them into a sequence of natural classes
 */
public class CVNaturalClasser {

	private final List<CVNaturalClass> naturalClasses;
	List<CVNaturalClassInSyllable> naturalClassesInCurrentWord = new LinkedList<CVNaturalClassInSyllable>(
			Arrays.asList(new CVNaturalClassInSyllable(null, null)));
	HashMap<String, CVNaturalClass> segmentToNaturalClassMapping = new HashMap<>();

	public CVNaturalClasser(List<CVNaturalClass> naturalClasses) {
		super();
		this.naturalClasses = naturalClasses;
		buildSegmentToNaturalClassMapping();
	}

	protected void buildSegmentToNaturalClassMapping() {
		for (CVNaturalClass nc : naturalClasses) {
			setSegmentToNaturalClassMapping(nc);
		}
	}

	protected void setSegmentToNaturalClassMapping(CVNaturalClass nc) {
		for (Object snc : nc.getSegmentsOrNaturalClasses()) {
			if (snc instanceof CVSegment) {
				segmentToNaturalClassMapping.put(((CVSegment) snc).getSegment(), nc);
			} else if (snc instanceof CVNaturalClass) {
				setSegmentToNaturalClassMapping(((CVNaturalClass) snc));
			}
		}
	}

	public List<CVNaturalClass> getNaturalClasses() {
		return naturalClasses;
	}

	public List<CVNaturalClassInSyllable> getNaturalClassesInCurrentWord() {
		return naturalClassesInCurrentWord;
	}

	public void setNaturalClassesInCurrentWord(
			List<CVNaturalClassInSyllable> naturalClassesInCurrentWord) {
		this.naturalClassesInCurrentWord = naturalClassesInCurrentWord;
	}

	public HashMap<String, CVNaturalClass> getSegmentToNaturalClass() {
		return segmentToNaturalClassMapping;
	}

	public void setSegmentToNaturalClass(HashMap<String, CVNaturalClass> segmentToNaturalClass) {
		this.segmentToNaturalClassMapping = segmentToNaturalClass;
	}

	public boolean convertSegmentsToNaturalClasses(List<CVSegmentInSyllable> segmentsInCurrentWord) {
		naturalClassesInCurrentWord.clear();

		for (CVSegmentInSyllable segInSyllable : segmentsInCurrentWord) {
			String sSegmentName = segInSyllable.getSegmentName();
			CVNaturalClass nc = segmentToNaturalClassMapping.get(sSegmentName);
			if (nc == null) {
				return false;
			}
			CVNaturalClassInSyllable natClassInSyllable = new CVNaturalClassInSyllable(nc,
					segInSyllable);
			naturalClassesInCurrentWord.add(natClassInSyllable);
		}
		return true;
	}
}
