/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package sil.org.syllableparser.service;

import java.util.ResourceBundle;

/**
 * @author Andy Black
 *
 */
public class CVSegmenterResult extends ParseResult {
	public int iPositionOfFailure = -1;

	public CVSegmenterResult() {
		super();
	}

	String getFailureMessage(String sWord, ResourceBundle bundle) {
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sSegmenterResult = sSegmentFailure.replace("{0}",
				sWord.substring(iPositionOfFailure));
		return sSegmenterResult;
	}
}
