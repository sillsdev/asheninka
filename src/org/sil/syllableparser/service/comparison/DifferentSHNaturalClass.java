// Copyright (c) 2019-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class DifferentSHNaturalClass extends DifferentSylParserObject {

	public DifferentSHNaturalClass(SHNaturalClass naturalClassFrom1, SHNaturalClass naturalClassFrom2) {
		super((SylParserObject)naturalClassFrom1, (SylParserObject)naturalClassFrom2);
	}
}
