// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.npapproach.NPRule;

/**
 * @author Andy Black
 *
 */
public class DifferentNPRule extends DifferentSylParserObject {

	public DifferentNPRule(NPRule ruleFrom1, NPRule ruleFrom2) {
		super((SylParserObject)ruleFrom1, (SylParserObject)ruleFrom2);
	}
}
