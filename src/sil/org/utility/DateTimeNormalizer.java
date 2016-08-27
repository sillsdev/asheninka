/**
 * 
 */
package sil.org.utility;

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
