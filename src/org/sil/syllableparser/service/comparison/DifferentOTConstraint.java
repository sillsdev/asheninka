// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.otapproach.OTConstraint;

/**
 * @author Andy Black
 *
 */
public class DifferentOTConstraint extends DifferentSylParserObject {

	public DifferentOTConstraint(OTConstraint contraintFrom1, OTConstraint constraintFrom2) {
		super((SylParserObject)contraintFrom1, (SylParserObject)constraintFrom2);
	}
}
