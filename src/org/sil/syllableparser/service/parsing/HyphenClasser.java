/**
 * Copyright (c) 2025-2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSyllable;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;

/**
 * 
 */
public class HyphenClasser {

	private List<HyphenClassInWord> classesInWord = new ArrayList<HyphenClassInWord>();
	private HyphenClassInWord wordBoundary;
	private HyphenApproach hyphenApproach;
	HashMap<Segment, HyphenClass> segmentToHyphenClassMapping = new HashMap<>();

	public HyphenClasser(HyphenApproach hyphenApproach) {
		super();
		this.hyphenApproach = hyphenApproach;
		wordBoundary = new HyphenClassInWord(hyphenApproach.getWordBoundaryHC(), null);
	}

	public List<HyphenClassInWord> getClassesInWord() {
		return classesInWord;
	}

	public void setClassesInWord(List<HyphenClassInWord> classesInWord) {
		this.classesInWord = classesInWord;
	}

	public HyphenClasserResult parseIntoHyphenClasses(List<? extends CVSegmentInSyllable> segmentsInWord) {
		HyphenClasserResult hcResult = new HyphenClasserResult();
		segmentToHyphenClassMapping.clear();
		classesInWord.clear();
		if (segmentsInWord.size() == 0) {
			hcResult.success = false;
			return hcResult;
		}
		buildSegmentToHyphenClassMapping();
		classesInWord.clear();
		classesInWord.add(wordBoundary);
		for (CVSegmentInSyllable segInWord : segmentsInWord) {
			HyphenClass hc = segmentToHyphenClassMapping.get(segInWord.getSegment());
			HyphenClassInWord hciw = new HyphenClassInWord(hc, segInWord);
			classesInWord.add(hciw);
		}
		classesInWord.add(wordBoundary);
		hcResult.sClasses = getClassesRepresentation(classesInWord);
		return hcResult;
	}

	protected void buildSegmentToHyphenClassMapping() {
		for (Segment seg : hyphenApproach.getLanguageProject().getActiveSegmentsInInventory()) {
			for (HyphenClass hc : hyphenApproach.getActiveHyphenClasses()) {
				if (hc.getSegments().contains(seg) && !segmentToHyphenClassMapping.containsKey(seg)) {
					segmentToHyphenClassMapping.put(seg, hc);
				}
			}
		}
	}

	public static String getClassesRepresentation(List<HyphenClassInWord> classesInWord) {
		return classesInWord.stream().map(HyphenClassInWord::getClassName).collect(Collectors.joining(", "));
	}
}
