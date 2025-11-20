// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVApproach;

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
	SortedSet<DifferentCVNaturalClass> diffs = new TreeSet<>(
			Comparator.comparing(DifferentCVNaturalClass::getSortingValue));

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
		comparer.compareGraphemes();
		comparer.compareGraphemeNaturalClasses();
		comparer.compareEnvironments();
		comparer.compareCVNaturalClasses(comparer.getCva1().getActiveCVNaturalClasses(), comparer.getCva2().getActiveCVNaturalClasses(), comparer.naturalClassesWhichDiffer);
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
		ApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/CVApproachLanguageComparisonHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath(), StandardCharsets.UTF_8);
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException | UncheckedIOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSameEnglishTest() {
		ApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparerSame, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/CVApproachLanguageComparisonSameHTMLEnglish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath(), StandardCharsets.UTF_8);
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException | UncheckedIOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSpanishTest() {
		ApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/CVApproachLanguageComparisonHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath(), StandardCharsets.UTF_8);
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException | UncheckedIOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void formattingSameSpanishTest() {
		ApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
				comparerSame, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/CVApproachLanguageComparisonSameHTMLSpanish.html");
		try {
			Stream<String> contents = Files.lines(file.toPath(), StandardCharsets.UTF_8);
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
			} catch (IOException | UncheckedIOException e) {
			e.printStackTrace();
		}
	}
}
