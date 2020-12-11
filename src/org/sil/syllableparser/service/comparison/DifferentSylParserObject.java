// Copyright (c) 2016-2020 SIL International 
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
public class DifferentSylParserObject {
	protected SylParserObject objectFrom1;
	protected SylParserObject objectFrom2;

	public DifferentSylParserObject(SylParserObject objectFrom1, SylParserObject objectFrom2) {
		super();
		this.objectFrom1 = objectFrom1;
		this.objectFrom2 = objectFrom2;
	}

	public SylParserObject getObjectFrom1() {
		return objectFrom1;
	}

	public void setObjectFrom1(SylParserObject objectFrom1) {
		this.objectFrom1 = objectFrom1;
	}

	public SylParserObject getObjectFrom2() {
		return objectFrom2;
	}

	public void setObjectFrom2(SylParserObject objectFrom2) {
		this.objectFrom2 = objectFrom2;
	}
	
	public String getSortingValue() {
		if (objectFrom1 != null) {
			return objectFrom1.getSortingValue();
		} else if (objectFrom2 != null) {
			return objectFrom2.getSortingValue();
		}
		return Constants.NULL_AS_STRING;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectFrom1 == null) ? 0 : objectFrom1.hashCode());
		result = prime * result + ((objectFrom2 == null) ? 0 : objectFrom2.hashCode());
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
		DifferentSylParserObject other = (DifferentSylParserObject) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((Word) objectFrom1).equals((Word) other.objectFrom1))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((Word) objectFrom2).equals((Word) other.objectFrom2))
			return false;
		return true;
	}

}
