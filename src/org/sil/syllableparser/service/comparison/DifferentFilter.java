// Copyright (c) 2020-2021 SIL International 
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

	public DifferentFilter(Filter filterFrom1, Filter filterFrom2) {
		super((SylParserObject)filterFrom1, (SylParserObject)filterFrom2);
	}
}
