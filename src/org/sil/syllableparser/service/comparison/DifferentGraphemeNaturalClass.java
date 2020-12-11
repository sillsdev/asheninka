// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentGraphemeNaturalClass extends DifferentSylParserObject {

	public DifferentGraphemeNaturalClass(GraphemeNaturalClass graphemeFrom1,
			GraphemeNaturalClass graphemeFrom2) {
		super((SylParserObject) graphemeFrom1, (SylParserObject) graphemeFrom2);
	}
}
