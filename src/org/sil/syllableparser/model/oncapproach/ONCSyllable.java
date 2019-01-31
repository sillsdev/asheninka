// Copyright (c) 2018 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.List;

import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;


/**
 * @author Andy Black
 *
 * A syllable in a word using the Sonority Hierarchy approach 
 *
 * A value object
 */
public class ONCSyllable extends Object {

	private final List<CVSegmentInSyllable> segmentsInSyllable;

	public ONCSyllable(List<CVSegmentInSyllable> segmentsInSyllable) {
		super();
		this.segmentsInSyllable = segmentsInSyllable;
	}

	public List<CVSegmentInSyllable> getSegmentsInSyllable() {
		return segmentsInSyllable;
	}
	
	public void add(CVSegmentInSyllable seg) {
		segmentsInSyllable.add(seg);
	}
	
	public String getSegmentNamesInSyllable() {
		StringBuilder sb = new StringBuilder();
		
		for (CVSegmentInSyllable seg : segmentsInSyllable) {
			sb.append(seg.getSegmentName());
		}	
		return sb.toString();
	}
}
