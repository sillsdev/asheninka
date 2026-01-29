// Copyright (c) 2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;

/**
 * @author Andy Black
 *
 */
public class DifferentHyphenChangeRule extends DifferentSylParserObject {

	public DifferentHyphenChangeRule(HyphenChangeRule hyphenChangeRuleFrom1, HyphenChangeRule hyphenChangeRuleFrom2) {
		super((SylParserObject)hyphenChangeRuleFrom1, (SylParserObject)hyphenChangeRuleFrom2);
	}
}
