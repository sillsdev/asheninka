/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Black
 *
 * Object value
 */
public class NPSyllable {

	private List<NPSegmentInSyllable> segmentsInSyllable = new ArrayList<NPSegmentInSyllable>();
	private NPNodeInSyllable node;

	public NPSyllable(List<NPSegmentInSyllable> segmentsInSyllable, NPNodeInSyllable node) {
		super();
		this.segmentsInSyllable = segmentsInSyllable;
		this.node = node;
	}
	public List<NPSegmentInSyllable> getSegmentsInSyllable() {
		return segmentsInSyllable;
	}
	public void setSegmentsInSyllable(List<NPSegmentInSyllable> segmentsInSyllable) {
		this.segmentsInSyllable = segmentsInSyllable;
	}
	public NPNodeInSyllable getNode() {
		return node;
	}
	public void setNode(NPNodeInSyllable node) {
		this.node = node;
	}
}
