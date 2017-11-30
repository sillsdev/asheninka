// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.utility;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.utility.DateTimeNormalizer;

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
		String normalizedDateTime = DateTimeNormalizer.normalizeDateTimeWithDigits(dateTime);
		assertEquals("20160409-080703", normalizedDateTime);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 14, 17, 13);
		normalizedDateTime = DateTimeNormalizer.normalizeDateTimeWithDigits(dateTime);
		assertEquals("20160409-141713", normalizedDateTime);
		Locale locale = new Locale("en");
		String asWords = DateTimeNormalizer.normalizeDateTimeWithWords(dateTime, locale);
		assertEquals("April 9, 2016 2:17 PM", asWords);
		locale = new Locale("es");
		asWords = DateTimeNormalizer.normalizeDateTimeWithWords(dateTime, locale);
		assertEquals("9 de abril de 2016 14:17", asWords);
	}

}
