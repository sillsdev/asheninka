// Copyright (c) 2016-2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model.cvapproach;

import org.sil.syllableparser.model.Segment;


/**
 * @author Andy Black
 *
 * A segment which occurs in a syllable using the CV Patterns approach 
 *
 * A value object
 */

public class CVSegmentInSyllable {
	protected final Segment segment;
	protected final String grapheme;

	public CVSegmentInSyllable(Segment segment, String grapheme) {
		super();
		this.segment = segment;
		this.grapheme = grapheme;
	}

	public Segment getSegment() {
		return segment;
	}

	public String getGrapheme() {
		return grapheme;
	}

	public String getSegmentId() {
		return segment.getID();
	}

	public String getSegmentName() {
		return segment.getSegment();
	}

}
