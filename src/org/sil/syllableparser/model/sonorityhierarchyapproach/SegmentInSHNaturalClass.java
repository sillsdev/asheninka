/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

import org.sil.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 */
public class SegmentInSHNaturalClass {

	private Segment segment;
	private SHNaturalClass naturalClass;

	public SegmentInSHNaturalClass(Segment segment, SHNaturalClass naturalClass) {
		super();
		this.segment = segment;
		this.naturalClass = naturalClass;
	}

	public Segment getSegment() {
		return segment;
	}

	public SHNaturalClass getNaturalClass() {
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
		Segment seg = ((SegmentInSHNaturalClass) obj).getSegment();
		SHNaturalClass nc = ((SegmentInSHNaturalClass) obj).getNaturalClass();
		if (!segment.equals(seg)) {
			result = false;
		} else if (!naturalClass.equals(nc)) {
			result = false;
		}
		return result;
	}

}
