/**
 * 
 */
package sil.org.utility;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andy Black
 *
 */
public class DateTimeNormalizerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		LocalDateTime dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 8, 7, 3);
		String normalizedDateTime = DateTimeNormalizer.normalizeDateTime(dateTime);
		assertEquals("20160409-080703", normalizedDateTime);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 14, 17, 13);
		normalizedDateTime = DateTimeNormalizer.normalizeDateTime(dateTime);
		assertEquals("20160409-141713", normalizedDateTime);
	}

}
