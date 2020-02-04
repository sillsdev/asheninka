/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;

/**
 * @author Andy Black
 *
 * Singleton (which needs to have its values initialized)
 */
public class TemplateFilterMatcher {

	private static TemplateFilterMatcher instance;
	List<Segment> activeSegments = new ArrayList<>();
	List<CVNaturalClass> activeClasses = new ArrayList<>();

	public static TemplateFilterMatcher getInstance() {
		if (instance == null) {
			instance = new TemplateFilterMatcher();
		}
		return instance;
	}

	public TemplateFilterMatcher() {
	}

	public List<Segment> getActiveSegments() {
		return activeSegments;
	}

	public void setActiveSegments(List<Segment> activeSegments) {
		this.activeSegments = activeSegments;
	}

	public List<CVNaturalClass> getActiveClasses() {
		return activeClasses;
	}

	public void setActiveClasses(List<CVNaturalClass> activeClasses) {
		this.activeClasses = activeClasses;
	}

	public boolean matches(TemplateFilter tf, List<ONCSegmentInSyllable> segmentsToMatch) {
		int iSlot = 0;
		List<TemplateFilterSlotSegmentOrNaturalClass> slots = tf.getSlots();
		int iSeg = 0;
		TemplateFilterSlotSegmentOrNaturalClass slot;
		while (iSlot < slots.size() && iSeg < segmentsToMatch.size()) {
			slot = slots.get(iSlot);
			ONCSegmentInSyllable segInSyl = segmentsToMatch.get(iSeg);
			Segment seg = segInSyl.getSegment();
			if (slot.isSegment()) {
				Segment referringSegment = slot.getReferringSegment();
				if (!activeSegments.contains(referringSegment)) {
					return false;
				}
				if (seg.equals(referringSegment)) {
					iSeg++;
				} else if (!slot.isOptional()) {
					return false;
				}
			} else {
				CVNaturalClass natClass = slot.getCVNaturalClass();
				if (!activeClasses.contains(natClass)) {
					return false;
				}
				int index = SylParserObject.findIndexInListByUuid(
						natClass.getSegmentsOrNaturalClasses(), seg.getID());
				if (index >= 0) {
					iSeg++;
				} else if (!slot.isOptional()) {
					return false;
				}
			}
			iSlot++;
		}
		while (iSlot < slots.size()) {
			// not all slots were tested; remaining slots must be optional
			slot = slots.get(iSlot);
			if (!slot.isOptional()) {
				return false;
			}
			iSlot++;
		}
		if (iSlot == slots.size()) {
			return true;
		}	
		return false;
	}
}
