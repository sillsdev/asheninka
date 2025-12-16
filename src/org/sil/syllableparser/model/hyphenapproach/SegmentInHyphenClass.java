/**
 * Copyright (c) 2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.hyphenapproach;

import org.sil.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 */
public class SegmentInHyphenClass {

	private Segment segment;
	private HyphenClass naturalClass;

	public SegmentInHyphenClass(Segment segment, HyphenClass hyphenClass) {
		super();
		this.segment = segment;
		this.naturalClass = hyphenClass;
	}

	public Segment getSegment() {
		return segment;
	}

	public HyphenClass getNaturalClass() {
		return naturalClass;
	}

	@Override
	public int hashCode() {
		return segment.hashCode() * 1000 + naturalClass.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		boolean result = true;
		Segment seg = ((SegmentInHyphenClass) obj).getSegment();
		HyphenClass nc = ((SegmentInHyphenClass) obj).getNaturalClass();
		if (!segment.equals(seg)) {
			result = false;
		} else if (!naturalClass.equals(nc)) {
			result = false;
		}
		return result;
	}

}
