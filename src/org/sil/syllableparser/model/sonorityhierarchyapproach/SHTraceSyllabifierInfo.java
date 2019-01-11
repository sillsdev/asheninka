/**
 * Copyright (c) 2018-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 */
public class SHTraceSyllabifierInfo {

	public Segment segment1 = null;
	public SHNaturalClass naturalClass1 = null;
	public Segment segment2 = null;
	public SHNaturalClass naturalClass2 = null;
	public SHComparisonResult comparisonResult;

	public String getSegment1Result() {
		if (segment1 == null) {
			return "null";
		} else {
			return segment1.getSegment();
		}
	}

	public String getNaturalClass1Result() {
		if (naturalClass1 == null) {
			return "null";
		} else {
			return naturalClass1.getNCName();
		}
	}

	public String getSegment2Result() {
		if (segment2 == null) {
			return "null";
		} else {
			return segment2.getSegment();
		}
	}

	public String getNaturalClass2Result() {
		if (naturalClass2 == null) {
			return "null";
		} else {
			return naturalClass2.getNCName();
		}
	}

	public String getComparisonResult() {
		if (comparisonResult == null) {
			return "null";
		} else {
			return comparisonResult.name();
		}
	}

	public SHTraceSyllabifierInfo() {
		super();
	}

	public SHTraceSyllabifierInfo(Segment segment1, SHNaturalClass naturalClass1, Segment segment2,
			SHNaturalClass naturalClass2, SHComparisonResult comparisonResult) {
		super();
		this.segment1 = segment1;
		this.naturalClass1 = naturalClass1;
		this.segment2 = segment2;
		this.naturalClass2 = naturalClass2;
		this.comparisonResult = comparisonResult;
	}
}
