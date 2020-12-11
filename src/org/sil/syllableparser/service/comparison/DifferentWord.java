// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class DifferentWord extends DifferentSylParserObject {

	public DifferentWord(Word wordFrom1, Word wordFrom2) {
		super((SylParserObject)wordFrom1, (SylParserObject)wordFrom2);
	}
}
