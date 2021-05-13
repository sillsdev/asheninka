/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

/**
 * @author Andy Black
 *
 */
public class OTStructuralOptions {

	public static final int ONSET = 1;
	public static final int NUCLEUS = 2;
	public static final int CODA = 4;
	public static final int UNPARSED = 8;
	public static final int WORD_INITIAL = 16;
	public static final int WORD_FINAL = 32;
	
	public static final int INITIALIZED = 63;

	public static final int REMOVE_ONSET = 62;
	public static final int REMOVE_NUCLEUS = 61;
	public static final int REMOVE_CODA = 59;
	public static final int REMOVE_UNPARSED = 55;
}
