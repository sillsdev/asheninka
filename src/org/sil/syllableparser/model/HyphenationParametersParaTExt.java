/**
 * Copyright (c) 2016-2026 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package org.sil.syllableparser.model;

/**
 * @author Andy Black
 *
 */
public class HyphenationParametersParaTExt extends HyphenationParameters {

	public HyphenationParametersParaTExt() {
		super();
	}

	public HyphenationParametersParaTExt(String discretionaryHyphen, int startAfterCharactersFromBeginning,
			int stopBeforeCharactersFromEnd, boolean fCountSegments, int startAfterSegmentsFromBeginning,
			int stopBeforeSegmentsFromEnd) {
		super(discretionaryHyphen, startAfterCharactersFromBeginning, stopBeforeCharactersFromEnd, fCountSegments,
				startAfterSegmentsFromBeginning, stopBeforeSegmentsFromEnd);
	}
}
