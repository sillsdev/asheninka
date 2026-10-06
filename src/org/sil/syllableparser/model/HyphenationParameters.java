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
public abstract class HyphenationParameters {
	
	private String discretionaryHyphen;
	private int startAfterCharactersFromBeginning;
	private int stopBeforeCharactersFromEnd;
	private boolean fCountSegments = false;
	private int startAfterSegmentsFromBeginning;
	private int stopBeforeSegmentsFromEnd;
	
	public HyphenationParameters() {
		discretionaryHyphen = "=";
		startAfterCharactersFromBeginning = 0;
		stopBeforeCharactersFromEnd = 0;
		fCountSegments = false;
		startAfterSegmentsFromBeginning = 0;
		stopBeforeSegmentsFromEnd = 0;
	}
	public HyphenationParameters(String discretionaryHyphen, int startAfterCharactersFromBeginning,
			int stopBeforeCharactersFromEnd, boolean fCountSegments, int startAfterSegmentsFromBeginning, int stopBeforeSegmentsFromEnd) {
		this.discretionaryHyphen = discretionaryHyphen;
		this.startAfterCharactersFromBeginning = startAfterCharactersFromBeginning;
		this.stopBeforeCharactersFromEnd = stopBeforeCharactersFromEnd;
		this.fCountSegments = fCountSegments;
		this.startAfterSegmentsFromBeginning = startAfterSegmentsFromBeginning;
		this.stopBeforeSegmentsFromEnd = stopBeforeSegmentsFromEnd;
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
	public boolean isfCountSegments() {
		return fCountSegments;
	}
	public void setfCountSegments(boolean fCountSegments) {
		this.fCountSegments = fCountSegments;
	}
	public int getStartAfterSegmentsFromBeginning() {
		return startAfterSegmentsFromBeginning;
	}
	public void setStartAfterSegmentsFromBeginning(int startAfterSegmentsFromBeginning) {
		this.startAfterSegmentsFromBeginning = startAfterSegmentsFromBeginning;
	}
	public int getStopBeforeSegmentsFromEnd() {
		return stopBeforeSegmentsFromEnd;
	}
	public void setStopBeforeSegmentsFromEnd(int stopBeforeSegmentsFromEnd) {
		this.stopBeforeSegmentsFromEnd = stopBeforeSegmentsFromEnd;
	}



}
