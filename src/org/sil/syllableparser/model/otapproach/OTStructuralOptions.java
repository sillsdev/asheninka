/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

import org.sil.syllableparser.Constants;

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
	public static final int COMBO_O_N_C_U = 15;
	public static final int COMBO_O_N_C = 7;
	public static final int COMBO_O_N_U = 11;
	public static final int COMBO_O_C_U = 13;
	public static final int COMBO_N_C_U = 14;
	public static final int COMBO_O_N = 3;
	public static final int COMBO_O_C = 5;
	public static final int COMBO_O_U = 9;
	public static final int COMBO_N_C = 6;
	public static final int COMBO_N_U = 10;
	public static final int COMBO_C_U = 12;

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
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.ONSET,
				iCount, Constants.OT_STRUCTURAL_OPTION_ONSET);
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.NUCLEUS,
				iCount, Constants.OT_STRUCTURAL_OPTION_NUCLEUS);
		iCount = getStructuralOption(options, sb, numberOfCoreOptions, OTStructuralOptions.CODA,
				iCount, Constants.OT_STRUCTURAL_OPTION_CODA);
		iCount = getStructuralOption(options, sb, numberOfCoreOptions,
				OTStructuralOptions.UNPARSED, iCount, Constants.OT_STRUCTURAL_OPTION_UNPARSED);
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
