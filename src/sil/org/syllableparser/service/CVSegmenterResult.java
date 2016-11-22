/**
 * Copyright (c) 2016 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package sil.org.syllableparser.service;

/**
 * @author Andy Black
 *
 * This is essentially a struct so we use public class fields
 * (http://www.oracle.com/technetwork/java/javase/documentation/codeconventions-137265.html#177)
 */
public class CVSegmenterResult extends ParseResult {
	public int iPositionOfFailure = -1;

	public CVSegmenterResult() {
		super();
	}
}
