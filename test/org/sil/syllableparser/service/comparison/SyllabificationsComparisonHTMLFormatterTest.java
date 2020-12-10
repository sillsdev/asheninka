// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.service.comparison.SyllabificationComparisonHTMLFormatter;
import org.sil.syllableparser.service.comparison.SyllabificationsComparer;

/**
 * @author Andy Black
 *
 */
public class SyllabificationsComparisonHTMLFormatterTest {

	LanguageProject languageProject;
	CVApproach cva;
	SHApproach sha;
	SyllabificationsComparer comparer;
	private Locale locale;
	private LocalDateTime dateTime;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		sha = languageProject.getSHApproach();
		comparer = new SyllabificationsComparer(languageProject);
		dateTime = LocalDateTime.of(2019, Month.JANUARY, 24, 8, 7, 3);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void formattingCVSHEnglishTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonCVSHHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingCVSHSpanishTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonCVSHHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingCVONCEnglishTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonCVONCHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingCVONCSpanishTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonCVONCHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingCVSHONCEnglishTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonCVSHONCHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingCVSHONCSpanishTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonCVSHONCHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSHONCEnglishTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonSHONCHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSHONCSpanishTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SyllabificationComparisonSHONCHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
