// Copyright (c) 2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import org.sil.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 * an Object Value used in templates and filters
 */
public class TemplateFilterSlotSegment {
	private TemplateFilterSlotSegmentOrNaturalClass slot;
	private Segment segment;

	public TemplateFilterSlotSegment() {
	}

	public TemplateFilterSlotSegment(TemplateFilterSlotSegmentOrNaturalClass slot, Segment segment) {
		this.slot = slot;
		this.segment = segment;
	}

	public TemplateFilterSlotSegmentOrNaturalClass getSlot() {
		return slot;
	}

	public void setSlot(TemplateFilterSlotSegmentOrNaturalClass slot) {
		this.slot = slot;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}
}
