// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentEnvironment extends DifferentSylParserObject {

	public DifferentEnvironment(Environment environmentFrom1, Environment environmentFrom2) {
		super((SylParserObject) environmentFrom1, (SylParserObject) environmentFrom2);
	}
}
