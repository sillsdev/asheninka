/**
 * Copyright (c) 2016-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Andy Black
 *
 * This is essentially a struct so we use public class fields
 * (http://www.oracle.com/technetwork/java/javase/documentation/codeconventions-137265.html#177)
 */
public class CVSegmenterResult extends ParseResult {
	public int iPositionOfFailure = -1;
	public List<CVSegmenterGraphemeResult> graphemesTried = new ArrayList<>();

	public CVSegmenterResult() {
		super();
	}

	String getFailureMessage(String sWord, ResourceBundle bundle) {
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		StringBuilder sb = new StringBuilder();
		sb.append(sSegmentFailure.replace("{0}",
				sWord.substring(iPositionOfFailure)));
		for (CVSegmenterGraphemeResult graphemeResult : graphemesTried) {
			sb.append(graphemeResult.getFailureMessage(bundle));
		}
		return sb.toString();
	}
}
