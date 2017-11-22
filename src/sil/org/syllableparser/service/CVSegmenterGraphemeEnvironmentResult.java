/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package sil.org.syllableparser.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.Environment;

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
