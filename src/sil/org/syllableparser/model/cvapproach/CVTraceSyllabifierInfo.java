/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package sil.org.syllableparser.model.cvapproach;

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
public class CVTraceSyllabifierInfo {

	public String sCVSyllablePattern = "";
	// default is false because most fail to match
	public boolean syllablePatternMatched = false;
	// default is false because most fail to match
	public boolean parseWasSuccessful = false;

	public List<CVTraceSyllabifierInfo> daughterInfo = new ArrayList<CVTraceSyllabifierInfo>();

	public CVTraceSyllabifierInfo() {
		super();
	}

	public CVTraceSyllabifierInfo(String sCVSyllablePattern) {
		super();
		this.sCVSyllablePattern = sCVSyllablePattern;
	}
}
