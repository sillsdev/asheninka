// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;

/**
 * @author Andy Black
 *
 */
public class DifferentCVNaturalClass extends DifferentSylParserObject {

	public DifferentCVNaturalClass(CVNaturalClass naturalClassFrom1, CVNaturalClass naturalClassFrom2) {
		super((SylParserObject)naturalClassFrom1, (SylParserObject)naturalClassFrom2);
	}
}
