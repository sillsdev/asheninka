/**
 * Copyright (c) 2016-2017 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package sil.org.syllableparser.model;

/**
 * @author Andy Black
 *
 */
public class HyphenationParametersParaTExt extends HyphenationParameters {

	public HyphenationParametersParaTExt() {
		super();
	}

	public HyphenationParametersParaTExt(String discretionaryHyphen, int startAfterCharactersFromBeginning,
			int stopBeforeCharactersFromEnd) {
		super(discretionaryHyphen, startAfterCharactersFromBeginning, stopBeforeCharactersFromEnd);
	}
}
