/**
 * 
 */
package sil.org.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Andy Black
 *
 */
public class DateTimeNormalizer {
	
	public static String normalizeDateTime(LocalDateTime dateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		return dateTimeFormatter.format(dateTime);
	}

}
