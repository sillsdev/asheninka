/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;

/**
 * @author Andy Black
 *
 */
public class DifferentCVSyllablePattern extends DifferentSylParserObject {

	public DifferentCVSyllablePattern(CVSyllablePattern naturalClassFrom1, CVSyllablePattern naturalClassFrom2) {
		super((SylParserObject)naturalClassFrom1, (SylParserObject)naturalClassFrom2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DifferentCVNaturalClass other = (DifferentCVNaturalClass) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((CVSyllablePattern) objectFrom1).getSPName().equals(((CVSyllablePattern) other.objectFrom1).getSPName()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((CVSyllablePattern) objectFrom2).getSPName().equals(((CVSyllablePattern) other.objectFrom2).getSPName()))
			return false;
		return true;
	}
}
