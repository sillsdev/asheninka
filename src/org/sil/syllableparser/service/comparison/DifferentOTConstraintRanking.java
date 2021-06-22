// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;

/**
 * @author Andy Black
 *
 */
public class DifferentOTConstraintRanking extends DifferentSylParserObject {

	public DifferentOTConstraintRanking(OTConstraintRanking rankingFrom1, OTConstraintRanking rankingFrom2) {
		super((SylParserObject)rankingFrom1, (SylParserObject)rankingFrom2);
	}
}
