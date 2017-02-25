// Copyright (c) 2016 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.cvapproach.CVApproach;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparisonHTMLFormatterTest {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	CVApproach cva1;
	CVApproach cva2;
	CVApproachLanguageComparer comparer;
	CVApproachLanguageComparer comparerSame;
	private Locale locale;
	private LocalDateTime dateTime;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject1 = new LanguageProject();
		locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject1, locale);
		File file1 = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file1);
		cva1 = languageProject1.getCVApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		File file2 = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file2);
		cva2 = languageProject2.getCVApproach();
		comparer = new CVApproachLanguageComparer(cva1, cva2);
		invokeComparison(comparer, file1, file2);
		comparerSame = new CVApproachLanguageComparer(cva1, cva1);
		invokeComparison(comparerSame, file1, file1);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 8, 7, 3);
	}

	protected void invokeComparison(CVApproachLanguageComparer comparer, File file1, File file2) {
		comparer.setDataSet1Info(file1.getPath());
		comparer.setDataSet2Info(file2.getPath());
		comparer.compareSegmentInventory();
		comparer.compareNaturalClasses();
		comparer.compareSyllablePatterns();
		comparer.compareSyllablePatternOrder();
		comparer.compareWords();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void formattingEnglishTest() {
		CVApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/sil/org/syllableparser/testData/CVApproachLanguageComparisonHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSameEnglishTest() {
		CVApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparerSame, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/sil/org/syllableparser/testData/CVApproachLanguageComparisonSameHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSpanishTest() {
		CVApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/sil/org/syllableparser/testData/CVApproachLanguageComparisonHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSameSpanishTest() {
		CVApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparerSame, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/sil/org/syllableparser/testData/CVApproachLanguageComparisonSameHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
