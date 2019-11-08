// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import java.util.List;

import org.sil.antlr4.StringSequenceValidator;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;

public class SegmentSequenceValidator extends StringSequenceValidator {
	List<String> segmentsAsStringsMasterList;
	List<Segment> segmentsMasterList;
	int iMaxDepth;
	int initialSlotsSize;

	public SegmentSequenceValidator(List<String> segmentsMasterList) {
		super(segmentsMasterList);
	}

	public List<Segment> getSegmentsMasterList() {
		return segmentsMasterList;
	}

	public void setSegmentsMasterList(List<Segment> segmentsMasterList) {
		this.segmentsMasterList = segmentsMasterList;
	}

	public boolean findSequenceOfSegments(String sInput) {
		return super.findSequenceOfStrings(sInput);
	}

	public boolean findSequenceOfSegments(String sInput, List<TemplateFilterSlotSegmentOrNaturalClass> slots) {
		iMaxDepth = 0;
		initialSlotsSize = slots.size();
		return findSequenceOfSegmentsGetMaxDepth(sInput, 0, slots);
	}

	private boolean findSequenceOfSegmentsGetMaxDepth(String sInput, int iDepth, List<TemplateFilterSlotSegmentOrNaturalClass> slots) {
		if (sInput.length() == 0) {
			iMaxDepth = iDepth;
			return true;
		}
		for (Segment seg : segmentsMasterList) {
			String str = seg.getSegment();
			if (sInput.startsWith(str)) {
				int iLen = str.length();
				boolean fIsFound = findSequenceOfSegmentsGetMaxDepth(sInput.substring(iLen), iDepth + iLen, slots);
				if (fIsFound) {
					TemplateFilterSlotSegmentOrNaturalClass slot = new TemplateFilterSlotSegmentOrNaturalClass(seg);
					slots.add(initialSlotsSize, slot);
					return true;
				}
			}
		}
		iMaxDepth = Math.max(iMaxDepth, iDepth);
		return false;
	}

}
