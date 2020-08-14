// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StringSequenceValidator {
	List<String> stringsMasterList;
	int iMaxDepth;

	public StringSequenceValidator(List<String> stringsMasterList) {
		super();
		this.stringsMasterList = stringsMasterList;
		Comparator<String> sortByLength = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.length() > o2.length())
					return -1;

				if (o2.length() > o1.length())
					return 1;

				return 0;
			}
		};
		Collections.sort(stringsMasterList, sortByLength);
	}

	public List<String> getStringsMasterList() {
		return stringsMasterList;
	}

	public void setStringsMasterList(List<String> stringsMasterList) {
		this.stringsMasterList = stringsMasterList;
	}

	public int getMaxDepth() {
		return iMaxDepth;
	}

	public void setMaxDepth(int iMaxDepth) {
		this.iMaxDepth = iMaxDepth;
	}

	public boolean findSequenceOfStrings(String sInput) {
		iMaxDepth = 0;
		return findSequenceOfStringsGetMaxDepth(sInput, 0);
	}

	private boolean findSequenceOfStringsGetMaxDepth(String sInput, int iDepth) {
		if (sInput.length() == 0) {
			iMaxDepth = iDepth;
			return true;
		}
		for (String str : stringsMasterList) {
			if (sInput.startsWith(str)) {
				int iLen = str.length();
				boolean fIsFound = findSequenceOfStringsGetMaxDepth(sInput.substring(iLen), iDepth + iLen);
				if (fIsFound) {
					return true;
				}
			}
		}
		iMaxDepth = Math.max(iMaxDepth, iDepth);
		return false;
	}
}
