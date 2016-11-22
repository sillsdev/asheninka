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

import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of segments and parses them into a
 *         sequence of natural classes
 */
public class CVNaturalClasser {

	private final List<CVNaturalClass> activeNaturalClasses;
	List<CVNaturalClassInSyllable> naturalClassesInCurrentWord = new LinkedList<CVNaturalClassInSyllable>(
			Arrays.asList(new CVNaturalClassInSyllable(null, null)));
	HashMap<String, CVNaturalClass> segmentToNaturalClassMapping = new HashMap<>();

	public CVNaturalClasser(List<CVNaturalClass> activeNaturalClasses) {
		super();
		this.activeNaturalClasses = activeNaturalClasses;
		buildSegmentToNaturalClassMapping();
	}

	protected void buildSegmentToNaturalClassMapping() {
		for (CVNaturalClass nc : activeNaturalClasses) {
			setSegmentToNaturalClassMapping(nc);
		}
	}

	protected void setSegmentToNaturalClassMapping(CVNaturalClass nc) {
		for (Object snc : nc.getSegmentsOrNaturalClasses()) {
			if (snc instanceof Segment) {
				segmentToNaturalClassMapping.put(((Segment) snc).getSegment(), nc);
			} else if (snc instanceof CVNaturalClass) {
				setSegmentToNaturalClassMapping(((CVNaturalClass) snc));
			}
		}
	}

	public List<CVNaturalClass> getActiveNaturalClasses() {
		return activeNaturalClasses;
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

	public CVNaturalClasserResult convertSegmentsToNaturalClasses(List<CVSegmentInSyllable> segmentsInCurrentWord) {
		naturalClassesInCurrentWord.clear();
		CVNaturalClasserResult result = new CVNaturalClasserResult();
		for (CVSegmentInSyllable segInSyllable : segmentsInCurrentWord) {
			String sSegmentName = segInSyllable.getSegmentName();
			CVNaturalClass nc = segmentToNaturalClassMapping.get(sSegmentName);
			if (nc == null) {
				result.success = false;
				String joined = naturalClassesInCurrentWord.stream()
						.map(CVNaturalClassInSyllable::getNaturalClassName)
						.collect(Collectors.joining(", "));
				result.sClassesSoFar = joined;
				joined = naturalClassesInCurrentWord.stream()
						.map(CVNaturalClassInSyllable::getSegmentInSyllable)
						.map(CVSegmentInSyllable::getGrapheme)
						.collect(Collectors.joining(""));
				result.sGraphemesSoFar = joined;
				return result;
			}
			CVNaturalClassInSyllable natClassInSyllable = new CVNaturalClassInSyllable(nc,
					segInSyllable);
			naturalClassesInCurrentWord.add(natClassInSyllable);
		}
		return result;
	}
}
