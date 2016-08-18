/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;

/**
 * @author Andy Black
 *
 */
public class DifferentCVNaturalClass extends DifferentSylParserObject {

	public DifferentCVNaturalClass(CVNaturalClass naturalClassFrom1, CVNaturalClass naturalClassFrom2) {
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
		} else if (!((CVNaturalClass) objectFrom1).getNCName().equals(((CVNaturalClass) other.objectFrom1).getNCName()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((CVNaturalClass) objectFrom2).getNCName().equals(((CVNaturalClass) other.objectFrom2).getNCName()))
			return false;
		return true;
	}
}
