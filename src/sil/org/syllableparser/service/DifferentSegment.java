/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 */
public class DifferentSegment {
	Segment segmentFrom1;
	Segment segmentFrom2;

	public DifferentSegment(Segment segmentFrom1, Segment segmentFrom2) {
		super();
		this.segmentFrom1 = segmentFrom1;
		this.segmentFrom2 = segmentFrom2;
	}

	public Segment getSegmentFrom1() {
		return segmentFrom1;
	}

	public void setSegmentFrom1(Segment segmentFrom1) {
		this.segmentFrom1 = segmentFrom1;
	}

	public Segment getSegmentFrom2() {
		return segmentFrom2;
	}

	public void setSegmentFrom2(Segment segmentFrom2) {
		this.segmentFrom2 = segmentFrom2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((segmentFrom1 == null) ? 0 : segmentFrom1.hashCode());
		result = prime * result + ((segmentFrom2 == null) ? 0 : segmentFrom2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DifferentSegment other = (DifferentSegment) obj;
		if (segmentFrom1 == null) {
			if (other.segmentFrom1 != null)
				return false;
		} else if (!segmentFrom1.getSegment().equals(other.segmentFrom1.getSegment()))
			return false;
		if (segmentFrom2 == null) {
			if (other.segmentFrom2 != null)
				return false;
		} else if (!segmentFrom2.getSegment().equals(other.segmentFrom2.getSegment()))
			return false;
		return true;
	}
}
