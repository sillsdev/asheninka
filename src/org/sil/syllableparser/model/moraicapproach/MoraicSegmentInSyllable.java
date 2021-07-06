/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.moraicapproach;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentUsageType;

/**
 * @author Andy Black
 *
 */
public class MoraicSegmentInSyllable extends CVSegmentInSyllable {
	MoraicSegmentUsageType usage;

	public MoraicSegmentInSyllable(Segment segment, String grapheme) {
		super(segment, grapheme);
		usage = MoraicSegmentUsageType.UNKNOWN;
	}

	public MoraicSegmentUsageType getUsage() {
		return usage;
	}

	public void setUsage(MoraicSegmentUsageType usage) {
		this.usage = usage;
	}
}
