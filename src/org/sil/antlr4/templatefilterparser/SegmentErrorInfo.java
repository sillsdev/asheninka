// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

public class SegmentErrorInfo {

	String sSegment;
	int iMaxDepth;
	
	public SegmentErrorInfo(String sSegment, int iMaxDepth) {
		super();
		this.sSegment = sSegment;
		this.iMaxDepth = iMaxDepth;
	}

	public String getSegment() {
		return sSegment;
	}

	public void setSegment(String sGrapheme) {
		this.sSegment = sGrapheme;
	}

	public int getMaxDepth() {
		return iMaxDepth;
	}

	public void setMaxDepth(int iMaxDepth) {
		this.iMaxDepth = iMaxDepth;
	}
}
