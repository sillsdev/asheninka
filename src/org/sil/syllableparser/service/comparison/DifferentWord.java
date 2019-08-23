// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class DifferentWord extends DifferentSylParserObject {

	public DifferentWord(Word wordFrom1, Word wordFrom2) {
		super((SylParserObject)wordFrom1, (SylParserObject)wordFrom2);
	}

	public String getSortingValue() {
		if (objectFrom1 != null) {
			return ((Word) objectFrom1).getWord();
		} else if (objectFrom2 != null) {
			return ((Word) objectFrom2).getWord();
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
		DifferentWord other = (DifferentWord) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((Word) objectFrom1).getWord().equals(((Word) other.objectFrom1).getWord()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((Word) objectFrom2).getWord().equals(((Word) other.objectFrom2).getWord()))
			return false;
		return true;
	}
}