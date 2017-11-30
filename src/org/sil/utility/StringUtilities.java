// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package org.sil.utility;

import java.io.File;
import java.text.BreakIterator;

/**
 * @author Andy Black
 *
 */
public class StringUtilities {

	public static boolean isNullOrEmpty(final String s) {
		return (s == null) ? true : s.length() == 0;
	}

	/**
	 * remove any sMatch string that is less than or equal to positionFromStart
	 * characters from the beginning of s
	 * 
	 * @param s
	 *            = the string
	 * @param sMatch
	 *            = the match which is to be removed
	 * @param positionFromStart
	 *            = the position from the start of s
	 * @return modified s
	 */
	public static String removeFromStart(String s, String sMatch, int positionFromStart) {
		String sResult = s;
		// TODO: need locale added to following
		BreakIterator characterIterator = BreakIterator.getCharacterInstance();
		characterIterator.setText(s);
		int startOfACharacter = characterIterator.first();
		int characterCount = 0;
		while (startOfACharacter < s.length() && characterCount < positionFromStart) {
			if (s.charAt(startOfACharacter) != sMatch.charAt(0)) {
				characterCount++;
			}
			startOfACharacter = characterIterator.next();
		}
		sResult = s.substring(0, startOfACharacter).replace(sMatch, "")
				+ s.substring(startOfACharacter);
		return sResult;
	}

	/**
	 * remove any sMatch string that is less than or equal to positionFromEnd
	 * characters from the end of s
	 * 
	 * @param s
	 *            = the string
	 * @param sMatch
	 *            = the match which is to be removed
	 * @param positionFromEnd
	 *            = the position from the end of s
	 * @return modified s
	 */
	public static String removeFromEnd(String s, String sMatch, int positionFromEnd) {
		String sResult = s;
		// TODO: need locale added to following
		BreakIterator characterIterator = BreakIterator.getCharacterInstance();
		characterIterator.setText(s);
		int startOfACharacter = characterIterator.last();
		startOfACharacter = characterIterator.previous();
		int characterCount = 0;
		while (startOfACharacter > 0 && characterCount < positionFromEnd) {
			if (s.charAt(startOfACharacter) != sMatch.charAt(0)) {
				characterCount++;
			}
			startOfACharacter = characterIterator.previous();
		}
		if (startOfACharacter != BreakIterator.DONE) {
			startOfACharacter = characterIterator.next();
			sResult = s.substring(0, startOfACharacter)
					+ s.substring(startOfACharacter).replace(sMatch, "");
		}
		return sResult;

	}

	public static String adjustForWindowsFileSeparator(String sPath) {
		if (File.separator.equals("\\")) {
			sPath = sPath.replaceAll("\\\\", "/");
		}
		return sPath;
	}


}
