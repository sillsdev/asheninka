/**
 * Copyright (c) 2016-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ResourceBundle;

/**
 * @author Andy Black
 *
 *         This is essentially a struct so we use public class fields
 *         (http://www
 *         .oracle.com/technetwork/java/javase/documentation/codeconventions
 *         -137265.html#177)
 */
public class CVSegmenterGraphemeEnvironmentResult extends ParseResult {
	public String sEnvironmentRepresentation = "";
	public boolean fEnvironmentPassed = false;

	public CVSegmenterGraphemeEnvironmentResult() {
		super();
	}

	String getFailureMessage(ResourceBundle bundle) {
		String sSegmentFailure = bundle.getString("label.cvsegmentgraphemeenvironmentfailure");
		String sSegmenterResult = sSegmentFailure.replace("{0}", sEnvironmentRepresentation);
		String sResult = fEnvironmentPassed ? bundle
				.getString("label.cvsegmentgraphemeenvironmentpassed") : bundle
				.getString("label.cvsegmentgraphemeenvironmentfailed");
		sSegmenterResult = sSegmenterResult.replace("{1}", sResult);
		return sSegmenterResult;
	}
}
