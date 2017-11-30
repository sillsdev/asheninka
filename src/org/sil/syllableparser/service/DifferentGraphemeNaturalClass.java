// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentGraphemeNaturalClass extends DifferentSylParserObject {

	public DifferentGraphemeNaturalClass(GraphemeNaturalClass graphemeFrom1,
			GraphemeNaturalClass graphemeFrom2) {
		super((SylParserObject) graphemeFrom1, (SylParserObject) graphemeFrom2);
	}

	public String getSortingValue() {
		if (objectFrom1 != null) {
			return ((GraphemeNaturalClass) objectFrom1).getGNCRepresentation();
		} else if (objectFrom2 != null) {
			return ((GraphemeNaturalClass) objectFrom2).getGNCRepresentation();
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
		DifferentGraphemeNaturalClass other = (DifferentGraphemeNaturalClass) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((GraphemeNaturalClass) objectFrom1).getGNCRepresentation().equals(
				((GraphemeNaturalClass) other.objectFrom1).getGNCRepresentation()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((GraphemeNaturalClass) objectFrom2).getGNCRepresentation().equals(
				((GraphemeNaturalClass) other.objectFrom2).getGNCRepresentation()))
			return false;
		return true;
	}
}
