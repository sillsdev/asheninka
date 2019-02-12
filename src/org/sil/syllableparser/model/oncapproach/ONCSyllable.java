// Copyright (c) 2018-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;


/**
 * @author Andy Black
 *
 * A syllable in a word using the Sonority Hierarchy approach 
 *
 * A value object
 */
public class ONCSyllable extends Object {

	private final List<ONCSegmentInSyllable> segmentsInSyllable;

	public ONCSyllable(ArrayList<ONCSegmentInSyllable> arrayList) {
		super();
		this.segmentsInSyllable = arrayList;
	}

	public List<ONCSegmentInSyllable> getSegmentsInSyllable() {
		return segmentsInSyllable;
	}
	
	public void add(ONCSegmentInSyllable seg) {
		segmentsInSyllable.add(seg);
	}
	
	public String getSegmentNamesInSyllable() {
		StringBuilder sb = new StringBuilder();
		
		for (ONCSegmentInSyllable seg : segmentsInSyllable) {
			sb.append(seg.getSegmentName());
		}	
		return sb.toString();
	}
}
