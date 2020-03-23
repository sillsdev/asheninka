// Copyright (c) 2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentFilter extends DifferentSylParserObject {

	public DifferentFilter(Filter FilterFrom1, Filter FilterFrom2) {
		super((SylParserObject)FilterFrom1, (SylParserObject)FilterFrom2);
	}
}
