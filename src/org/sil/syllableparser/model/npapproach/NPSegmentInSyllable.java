/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 */
public class NPSegmentInSyllable extends CVSegmentInSyllable {
	NPNodeInSyllable node = null;
	NPSyllable syllable = null;

	public NPSegmentInSyllable(Segment segment, String grapheme) {
		super(segment, grapheme);
	}

	public NPNodeInSyllable getNode() {
		return node;
	}

	public void setNode(NPNodeInSyllable value) {
		this.node = value;
	}

	public NPSyllable getSyllable() {
		return syllable;
	}

	public void setSyllable(NPSyllable syllable) {
		this.syllable = syllable;
	}
}
