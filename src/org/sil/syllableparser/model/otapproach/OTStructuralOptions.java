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
	
	public static final int INITIALIZED = 0;
	public static final int ALL_SET = 63;
	public static final int FOUR_CORE_OPTIONS_SET = 15;

	public static final int REMOVE_ONSET = 62;
	public static final int REMOVE_NUCLEUS = 61;
	public static final int REMOVE_CODA = 59;
	public static final int REMOVE_UNPARSED = 55;
	public static final int REMOVE_WORD_INITIAL = 47;
	public static final int REMOVE_WORD_FINAL = 31;

	public static String getStructuralOptions(int options) {
		StringBuilder sb = new StringBuilder();
		int numberOfCoreOptions = getNumberOfCoreOptionsSet(options);
		if (numberOfCoreOptions > 1) {
			sb.append("{");
		}
		int iCount = 0;
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.ONSET, iCount, "o");
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.NUCLEUS, iCount, "n");
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.CODA, iCount, "c");
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.UNPARSED, iCount, "u");
		if (numberOfCoreOptions > 1) {
			sb.append("}");
		}
		return sb.toString();
	}

	protected static int getStructuralOption(int options, StringBuilder sb,
			int numberOfCoreOptions, int option, int iCount, String sOption) {
		if ((options | option) == options) {
			if (iCount > 0 && numberOfCoreOptions > 1 && iCount < numberOfCoreOptions)
				sb.append(", ");
			sb.append(sOption);
			iCount++;
		}
		return iCount;
	}

	public static int getNumberOfCoreOptionsSet(int options) {
		options &= OTStructuralOptions.REMOVE_WORD_INITIAL;
		options &= OTStructuralOptions.REMOVE_WORD_FINAL;
		int iCount = 0;
		if ((options | OTStructuralOptions.ONSET) == options)
			iCount++;
		if ((options | OTStructuralOptions.NUCLEUS) == options)
			iCount++;
		if ((options | OTStructuralOptions.CODA) == options)
			iCount++;
		if ((options | OTStructuralOptions.UNPARSED) == options)
			iCount++;
		return iCount;
	}

}
