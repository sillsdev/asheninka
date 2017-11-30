/**
 * Copyright (c) 2016-2017 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package org.sil.syllableparser.model;

/**
 * @author Andy Black
 *
 */
public abstract class HyphenationParameters {
	
	private String discretionaryHyphen;
	private int startAfterCharactersFromBeginning;
	private int stopBeforeCharactersFromEnd;
	
	public HyphenationParameters() {
		discretionaryHyphen = "=";
		startAfterCharactersFromBeginning = 0;
		stopBeforeCharactersFromEnd = 0;
	}
	public HyphenationParameters(String discretionaryHyphen, int startAfterCharactersFromBeginning,
			int stopBeforeCharactersFromEnd) {
		this.discretionaryHyphen = discretionaryHyphen;
		this.startAfterCharactersFromBeginning = startAfterCharactersFromBeginning;
		this.stopBeforeCharactersFromEnd = stopBeforeCharactersFromEnd;
	}

	public String getDiscretionaryHyphen() {
		return discretionaryHyphen;
	}

	public void setDiscretionaryHyphen(String discretionaryHyphen) {
		this.discretionaryHyphen = discretionaryHyphen;
	}

	public int getStartAfterCharactersFromBeginning() {
		return startAfterCharactersFromBeginning;
	}

	public void setStartAfterCharactersFromBeginning(int startAfterCharactersFromBeginning) {
		this.startAfterCharactersFromBeginning = startAfterCharactersFromBeginning;
	}

	public int getStopBeforeCharactersFromEnd() {
		return stopBeforeCharactersFromEnd;
	}

	public void setStopBeforeCharactersFromEnd(int stopBeforeCharactersFromEnd) {
		this.stopBeforeCharactersFromEnd = stopBeforeCharactersFromEnd;
	}



}
