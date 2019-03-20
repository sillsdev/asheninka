/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.parsing.ONCType;

/**
 * @author Andy Black
 *
 */
public class ONCTraceSyllabifierInfo {

	public Segment segment1 = null;
	public SHNaturalClass naturalClass1 = null;
	public Segment segment2 = null;
	public SHNaturalClass naturalClass2 = null;
	public SHComparisonResult comparisonResult = null;
	private ONCSyllabificationStatus status = ONCSyllabificationStatus.UNKNOWN;
	private ONCType oncType = ONCType.UNKNOWN;
	public String sMissingNaturalClass = "No Natural Class";
	public static final String NULL_REPRESENTATION = "&#xa0;&#x2014";

	public ONCTraceSyllabifierInfo() {
		super();
	}

	public ONCTraceSyllabifierInfo(Segment segment, ONCSyllabificationStatus status) {
		super();
		this.segment1 = segment;
		this.status = status;
	}

	public ONCTraceSyllabifierInfo(Segment segment, ONCType oncType) {
		super();
		this.segment1 = segment;
		this.oncType = oncType;
	}

	public ONCTraceSyllabifierInfo(Segment segment1, SHNaturalClass naturalClass1, Segment segment2,
			SHNaturalClass naturalClass2, SHComparisonResult result) {
		super();
		this.segment1 = segment1;
		this.naturalClass1 = naturalClass1;
		this.segment2 = segment2;
		this.naturalClass2 = naturalClass2;
		this.comparisonResult = result;
	}

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

	public ONCSyllabificationStatus getStatus() {
		return status;
	}

	public void setStatus(ONCSyllabificationStatus status) {
		this.status = status;
	}

	public Segment getSegment1() {
		return segment1;
	}

	public void setSegment1(Segment segment1) {
		this.segment1 = segment1;
	}

	public SHNaturalClass getNaturalClass1() {
		return naturalClass1;
	}

	public void setNaturalClass1(SHNaturalClass naturalClass1) {
		this.naturalClass1 = naturalClass1;
	}

	public Segment getSegment2() {
		return segment2;
	}

	public void setSegment2(Segment segment2) {
		this.segment2 = segment2;
	}

	public SHNaturalClass getNaturalClass2() {
		return naturalClass2;
	}

	public void setNaturalClass2(SHNaturalClass naturalClass2) {
		this.naturalClass2 = naturalClass2;
	}

	public ONCType getOncType() {
		return oncType;
	}

	public void setOncType(ONCType oncType) {
		this.oncType = oncType;
	}

	public void setComparisonResult(SHComparisonResult comparisonResult) {
		this.comparisonResult = comparisonResult;
	}
}
