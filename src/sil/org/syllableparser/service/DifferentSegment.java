/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentSegment extends DifferentSylParserObject {

	public DifferentSegment(Segment segmentFrom1, Segment segmentFrom2) {
		super((SylParserObject)segmentFrom1, (SylParserObject)segmentFrom2);
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
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((Segment) objectFrom1).getSegment().equals(((Segment) other.objectFrom1).getSegment()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((Segment) objectFrom2).getSegment().equals(((Segment) other.objectFrom2).getSegment()))
			return false;
		return true;
	}
}
