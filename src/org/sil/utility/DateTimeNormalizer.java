// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * @author Andy Black
 *
 */
public class DateTimeNormalizer {

	public static String normalizeDateTimeWithDigits(LocalDateTime dateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		return dateTimeFormatter.format(dateTime);
	}

	public static String normalizeDateTimeWithWords(LocalDateTime dateTime, Locale locale) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(
				FormatStyle.LONG, FormatStyle.SHORT).withLocale(locale);
		return dateTimeFormatter.format(dateTime);
	}
}
