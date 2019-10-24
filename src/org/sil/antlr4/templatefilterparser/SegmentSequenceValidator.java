// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import java.util.List;

import org.sil.antlr4.StringSequenceValidator;

public class SegmentSequenceValidator extends StringSequenceValidator{
	List<String> segmentsMasterList;

	public SegmentSequenceValidator(List<String> segmentsMasterList) {
		super(segmentsMasterList);
	}

	public List<String> getSegmentsMasterList() {
		return super.getStringsMasterList();
	}

	public void setSegmentsMasterList(List<String> segmentsMasterList) {
		super.setStringsMasterList(segmentsMasterList);
	}

	public boolean findSequenceOfSegments(String sInput) {
		return super.findSequenceOfStrings(sInput);
	}
}
