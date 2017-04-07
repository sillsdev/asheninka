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
	List<List<CVNaturalClassInSyllable>> naturalClassListsInCurrentWord = new LinkedList<List<CVNaturalClassInSyllable>>(
			Arrays.asList(new LinkedList<CVNaturalClassInSyllable>(Arrays
					.asList(new CVNaturalClassInSyllable(null, null)))));
	HashMap<String, List<CVNaturalClass>> segmentToNaturalClassesMapping = new HashMap<>();

	public CVNaturalClasser(List<CVNaturalClass> activeNaturalClasses) {
		super();
		this.activeNaturalClasses = activeNaturalClasses;
		buildSegmentToNaturalClassMapping();
	}

	protected void buildSegmentToNaturalClassMapping() {
		for (CVNaturalClass nc : activeNaturalClasses) {
			setSegmentToNaturalClassesMapping(nc);
		}
	}

	protected void setSegmentToNaturalClassesMapping(CVNaturalClass nc) {
		for (Object snc : nc.getSegmentsOrNaturalClasses()) {
			if (snc instanceof Segment) {
				String sId = ((Segment) snc).getID();
				addNaturalClassToMap(sId, nc);
			} else {
				setSegmentToNestedNaturalClassesMapping((CVNaturalClass)snc, nc);
			}
		}
	}

	protected void setSegmentToNestedNaturalClassesMapping(CVNaturalClass nestedNC, CVNaturalClass nc) {
		for (Object snc : nestedNC.getSegmentsOrNaturalClasses()) {
			if (snc instanceof Segment) {
				String sId = ((Segment) snc).getID();
				addNaturalClassToMap(sId, nc);
			} else {
				setSegmentToNestedNaturalClassesMapping((CVNaturalClass)snc, nc);
			}
		}
	}

	protected void addNaturalClassToMap(String sId, CVNaturalClass nc) {
		List<CVNaturalClass> naturalClasses = segmentToNaturalClassesMapping.get(sId);
		if (naturalClasses == null) {
			naturalClasses = new LinkedList<CVNaturalClass>(Arrays.asList(nc));
			segmentToNaturalClassesMapping.put(sId, naturalClasses);
		} else {
			naturalClasses.add(nc);
		}
	}

	public List<CVNaturalClass> getActiveNaturalClasses() {
		return activeNaturalClasses;
	}

	public List<List<CVNaturalClassInSyllable>> getNaturalClassListsInCurrentWord() {
		return naturalClassListsInCurrentWord;
	}

	public void setNaturalClassListsInCurrentWord(
			List<List<CVNaturalClassInSyllable>> naturalClassListsInCurrentWord) {
		this.naturalClassListsInCurrentWord = naturalClassListsInCurrentWord;
	}

	public String getNaturalClassListsInCurrentWordAsString() {
		// TODO: is there a way to do this with lambdas?
		StringBuilder sb = new StringBuilder();
		int iSize = naturalClassListsInCurrentWord.size();
		int i = 0;
		for (List<CVNaturalClassInSyllable> listOfNCS : naturalClassListsInCurrentWord) {
			i++;
			if (listOfNCS.size() > 1) {
				sb.append("{");
			}
			String s = listOfNCS.stream().map(CVNaturalClassInSyllable::getNaturalClassName)
					.collect(Collectors.joining(","));
			sb.append(s);
			if (listOfNCS.size() > 1) {
				sb.append("}");
			}
			if (i < iSize) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public String getSegmentsInCurrentWordAsString() {
		// TODO: is there a way to do this with lambdas?
		StringBuilder sb = new StringBuilder();
		int iSize = naturalClassListsInCurrentWord.size();
		int i = 0;
		for (List<CVNaturalClassInSyllable> listOfNCS : naturalClassListsInCurrentWord) {
			i++;
			if (listOfNCS.size() > 1) {
				sb.append("{");
			}
			String s = listOfNCS.stream().map(CVNaturalClassInSyllable::getNaturalClassName)
					.collect(Collectors.joining(","));
			sb.append(s);
			if (listOfNCS.size() > 1) {
				sb.append("}");
			}
			if (i < iSize) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public HashMap<String, List<CVNaturalClass>> getSegmentToNaturalClasses() {
		return segmentToNaturalClassesMapping;
	}

	public void setSegmentToNaturalClasses(
			HashMap<String, List<CVNaturalClass>> segmentToNaturalClasses) {
		this.segmentToNaturalClassesMapping = segmentToNaturalClasses;
	}

	public CVNaturalClasserResult convertSegmentsToNaturalClasses(
			List<CVSegmentInSyllable> segmentsInCurrentWord) {
		naturalClassListsInCurrentWord.clear();
		CVNaturalClasserResult result = new CVNaturalClasserResult();
		for (CVSegmentInSyllable segInSyllable : segmentsInCurrentWord) {
			String sId = segInSyllable.getSegmentId();
			List<CVNaturalClassInSyllable> naturalClassesList = null;
			List<CVNaturalClass> listOfNatClasses = segmentToNaturalClassesMapping.get(sId);
			if (listOfNatClasses == null) {
				result.success = false;
				result.sClassesSoFar = getNaturalClassListsInCurrentWordAsString();
				int iCurrentSegment = segmentsInCurrentWord.indexOf(segInSyllable);
				String joined = segmentsInCurrentWord.subList(0, iCurrentSegment).stream()
						.map(CVSegmentInSyllable::getGrapheme).collect(Collectors.joining(""));
				result.sGraphemesSoFar = joined;
				return result;
			}
			for (CVNaturalClass nc : segmentToNaturalClassesMapping.get(sId)) {
				CVNaturalClassInSyllable natClassInSyllable = new CVNaturalClassInSyllable(nc,
						segInSyllable);
				if (naturalClassesList == null) {
					naturalClassesList = new LinkedList<CVNaturalClassInSyllable>(
							Arrays.asList(natClassInSyllable));
					naturalClassListsInCurrentWord.add(naturalClassesList);
				} else {
					naturalClassesList.add(natClassInSyllable);
				}
			}
		}
		return result;
	}
}
