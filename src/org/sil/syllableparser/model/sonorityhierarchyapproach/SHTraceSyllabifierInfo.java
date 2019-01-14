/**
 * Copyright (c) 2018-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

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
	public boolean startsSyllable = false;
	public String sMissingNaturalClass = "No Natural Class";
	public static final String NULL_REPRESENTATION = "&#xa0;&#x2014";

	public String getSegment1Result() {
		if (segment1 == null) {
			return NULL_REPRESENTATION;
		} else {
			return segment1.getSegment();
		}
	}

	public String getNaturalClass1Result() {
		if (naturalClass1 == null) {
			if (comparisonResult == SHComparisonResult.MISSING1) {
				return sMissingNaturalClass;
			} else {
				return NULL_REPRESENTATION;
			}
		} else {
			return naturalClass1.getNCName();
		}
	}

	public String getSegment2Result() {
		if (segment2 == null) {
			return NULL_REPRESENTATION;
		} else {
			return segment2.getSegment();
		}
	}

	public String getNaturalClass2Result() {
		if (naturalClass2 == null) {
			if (comparisonResult == SHComparisonResult.MISSING2) {
				return sMissingNaturalClass;
			} else {
				return NULL_REPRESENTATION;
			}
		} else {
			return naturalClass2.getNCName();
		}
	}

	public String getComparisonResult() {
		String result = "";
		if (comparisonResult == null) {
			return NULL_REPRESENTATION;
		} else {
			switch (comparisonResult) {
			case LESS:
				result = "<";
				break;
			case EQUAL:
				result = "=";
				break;
			case MORE:
				result = ">";
				break;
			case MISSING1:
				result = "!!!";
				break;
			case MISSING2:
				result = "!!!";
				break;
			default:
				break;
			}
			return result;
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
