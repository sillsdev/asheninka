// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package sil.org.environmentparser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GraphemeSequenceValidator {
	List<String> graphemesMasterList;
	int iMaxDepth;

	public GraphemeSequenceValidator(List<String> graphemesMasterList) {
		super();
		this.graphemesMasterList = graphemesMasterList;
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
		Collections.sort(graphemesMasterList, sortByLength);
	}

	public List<String> getGraphemesMasterList() {
		return graphemesMasterList;
	}

	public void setGraphemesMasterList(List<String> graphemesMasterList) {
		this.graphemesMasterList = graphemesMasterList;
	}

	public int getMaxDepth() {
		return iMaxDepth;
	}

	public void setMaxDepth(int iMaxDepth) {
		this.iMaxDepth = iMaxDepth;
	}

	public boolean findSequenceOfGraphemes(String sInput) {
		iMaxDepth = 0;
		return findSequenceOfGraphemesGetMaxDepth(sInput, 0);
	}

	private boolean findSequenceOfGraphemesGetMaxDepth(String sInput, int iDepth) {
		if (sInput.length() == 0) {
			iMaxDepth = iDepth;
			return true;
		}
		for (String grapheme : graphemesMasterList) {
			if (sInput.startsWith(grapheme)) {
				int iLen = grapheme.length();
				boolean fIsFound = findSequenceOfGraphemesGetMaxDepth(sInput.substring(iLen), iDepth + iLen);
				if (fIsFound) {
					return true;
				}
			}
		}
		iMaxDepth = Math.max(iMaxDepth, iDepth);
		return false;
	}
}
