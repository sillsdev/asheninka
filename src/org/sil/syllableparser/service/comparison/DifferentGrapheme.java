// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentGrapheme extends DifferentSylParserObject {

	public DifferentGrapheme(Grapheme graphemeFrom1, Grapheme graphemeFrom2) {
		super((SylParserObject)graphemeFrom1, (SylParserObject)graphemeFrom2);
	}
}
