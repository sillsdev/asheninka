// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;

/**
 * @author Andy Black
 *
 */
public class DifferentCVSyllablePattern extends DifferentSylParserObject {

	public DifferentCVSyllablePattern(CVSyllablePattern naturalClassFrom1, CVSyllablePattern naturalClassFrom2) {
		super((SylParserObject)naturalClassFrom1, (SylParserObject)naturalClassFrom2);
	}
}
