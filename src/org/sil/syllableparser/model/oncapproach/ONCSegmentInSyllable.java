/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 */
public class ONCSegmentInSyllable extends CVSegmentInSyllable {
	ONCSegmentUsageType usage;

	public ONCSegmentInSyllable(Segment segment, String grapheme) {
		super(segment, grapheme);
		usage = ONCSegmentUsageType.UNKNOWN;
	}

	public ONCSegmentUsageType getUsage() {
		return usage;
	}

	public void setUsage(ONCSegmentUsageType usage) {
		this.usage = usage;
	}
}
