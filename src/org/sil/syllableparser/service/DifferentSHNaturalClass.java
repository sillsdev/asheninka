// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class DifferentSHNaturalClass extends DifferentSylParserObject {

	public DifferentSHNaturalClass(SHNaturalClass naturalClassFrom1, SHNaturalClass naturalClassFrom2) {
		super((SylParserObject)naturalClassFrom1, (SylParserObject)naturalClassFrom2);
	}

	public String getSortingValue() {
		if (objectFrom1 != null) {
			return ((SHNaturalClass) objectFrom1).getNCName();
		} else if (objectFrom2 != null) {
			return ((SHNaturalClass) objectFrom2).getNCName();
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
		} else if (!((SHNaturalClass) objectFrom1).getNCName().equals(((SHNaturalClass) other.objectFrom1).getNCName()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((SHNaturalClass) objectFrom2).getNCName().equals(((SHNaturalClass) other.objectFrom2).getNCName()))
			return false;
		return true;
	}
}
