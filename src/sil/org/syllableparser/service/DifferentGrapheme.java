// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentGrapheme extends DifferentSylParserObject {

	public DifferentGrapheme(Grapheme graphemeFrom1, Grapheme graphemeFrom2) {
		super((SylParserObject)graphemeFrom1, (SylParserObject)graphemeFrom2);
	}
	
	public String getSortingValue() {
		if (objectFrom1 != null) {
			return ((Grapheme) objectFrom1).getForm();
		} else if (objectFrom2 != null) {
			return ((Grapheme) objectFrom2).getForm();
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
		DifferentGrapheme other = (DifferentGrapheme) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((Grapheme) objectFrom1).getForm().equals(((Grapheme) other.objectFrom1).getForm()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((Grapheme) objectFrom2).getForm().equals(((Grapheme) other.objectFrom2).getForm()))
			return false;
		return true;
	}
}