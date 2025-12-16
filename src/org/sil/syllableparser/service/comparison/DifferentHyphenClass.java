// Copyright (c) 2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

/**
 * @author Andy Black
 *
 */
public class DifferentHyphenClass extends DifferentSylParserObject {

	public DifferentHyphenClass(HyphenClass hyphenClassFrom1, HyphenClass hyphenClassFrom2) {
		super((SylParserObject)hyphenClassFrom1, (SylParserObject)hyphenClassFrom2);
	}
}
