// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentSegment extends DifferentSylParserObject {

	public DifferentSegment(Segment segmentFrom1, Segment segmentFrom2) {
		super((SylParserObject)segmentFrom1, (SylParserObject)segmentFrom2);
	}
}
