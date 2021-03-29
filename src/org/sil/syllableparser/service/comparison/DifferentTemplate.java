// Copyright (c) 2020-2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.TemplateFilter;

/**
 * @author Andy Black
 *
 */
public class DifferentTemplate extends DifferentSylParserObject {

	public DifferentTemplate(TemplateFilter templateFilterFrom1, TemplateFilter templateFilterFrom2) {
		super((SylParserObject) templateFilterFrom1, (SylParserObject) templateFilterFrom2);
	}
}
