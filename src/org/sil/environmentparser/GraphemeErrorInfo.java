// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.environmentparser;

public class GraphemeErrorInfo {

	String sGrapheme;
	int iMaxDepth;
	
	public GraphemeErrorInfo(String sGrapheme, int iMaxDepth) {
		super();
		this.sGrapheme = sGrapheme;
		this.iMaxDepth = iMaxDepth;
	}

	public String getGrapheme() {
		return sGrapheme;
	}

	public void setGrapheme(String sGrapheme) {
		this.sGrapheme = sGrapheme;
	}

	public int getMaxDepth() {
		return iMaxDepth;
	}

	public void setMaxDepth(int iMaxDepth) {
		this.iMaxDepth = iMaxDepth;
	}
}
