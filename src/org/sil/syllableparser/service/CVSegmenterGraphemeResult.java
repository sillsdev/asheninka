/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Environment;

/**
 * @author Andy Black
 *
 *         This is essentially a struct so we use public class fields
 *         (http://www
 *         .oracle.com/technetwork/java/javase/documentation/codeconventions
 *         -137265.html#177)
 */
public class CVSegmenterGraphemeResult extends ParseResult {
	public String sGrapheme = "";
	public List<CVSegmenterGraphemeEnvironmentResult> environmentsTried = new ArrayList<>();

	public CVSegmenterGraphemeResult() {
		super();
	}

	String getFailureMessage(ResourceBundle bundle) {
		String sSegmentFailure = bundle.getString("label.cvsegmentgraphemefailure");
		StringBuilder sb = new StringBuilder();
		sb.append(sSegmentFailure.replace("{0}", sGrapheme));
		for (CVSegmenterGraphemeEnvironmentResult envResult : environmentsTried) {
			sb.append(envResult.getFailureMessage(bundle));
		}
		return sb.toString();
	}
}
