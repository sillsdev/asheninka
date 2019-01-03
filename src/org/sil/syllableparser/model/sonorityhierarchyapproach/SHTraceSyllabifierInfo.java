/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Black
 *
 *         This is essentially a struct so we use public class fields
 *         (http://www
 *         .oracle.com/technetwork/java/javase/documentation/codeconventions
 *         -137265.html#177)
 */
public class SHTraceSyllabifierInfo {

	public String sCVSyllablePattern = "";
	// default is false because most fail to match
	public boolean syllablePatternMatched = false;
	// default is false because most fail to match
	public boolean parseWasSuccessful = false;

	public List<SHTraceSyllabifierInfo> daughterInfo = new ArrayList<SHTraceSyllabifierInfo>();

	public SHTraceSyllabifierInfo() {
		super();
	}

	public SHTraceSyllabifierInfo(String sCVSyllablePattern) {
		super();
		this.sCVSyllablePattern = sCVSyllablePattern;
	}
}
