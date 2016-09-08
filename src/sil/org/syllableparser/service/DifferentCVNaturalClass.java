// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;

/**
 * @author Andy Black
 *
 */
public class DifferentCVNaturalClass extends DifferentSylParserObject {

	public DifferentCVNaturalClass(CVNaturalClass naturalClassFrom1, CVNaturalClass naturalClassFrom2) {
		super((SylParserObject)naturalClassFrom1, (SylParserObject)naturalClassFrom2);
	}

	public String getSortingValue() {
		if (objectFrom1 != null) {
			return ((CVNaturalClass) objectFrom1).getNCName();
		} else if (objectFrom2 != null) {
			return ((CVNaturalClass) objectFrom2).getNCName();
		}
		return Constants.NULL_AS_STRING;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DifferentCVSyllablePattern other = (DifferentCVSyllablePattern) obj;
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
