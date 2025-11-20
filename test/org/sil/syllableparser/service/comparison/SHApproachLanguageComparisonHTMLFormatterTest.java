// Copyright (c) 2019-2025 SIL International
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
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

/**
 * @author Andy Black
 *
 */
public class SHApproachLanguageComparisonHTMLFormatterTest {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	SHApproach sha1;
	SHApproach sha2;
	SHApproachLanguageComparer comparer;
	SHApproachLanguageComparer comparerSame;
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
		sha1 = languageProject1.getSHApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		File file2 = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file2);
		sha2 = languageProject2.getSHApproach();
		comparer = new SHApproachLanguageComparer(sha1, sha2);
		invokeComparison(comparer, file1, file2);
		comparerSame = new SHApproachLanguageComparer(sha1, sha1);
		invokeComparison(comparerSame, file1, file1);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 8, 7, 3);
	}

	protected void invokeComparison(SHApproachLanguageComparer comparer, File file1, File file2) {
		comparer.setDataSet1Info(file1.getPath());
		comparer.setDataSet2Info(file2.getPath());
		comparer.compareSegmentInventory();
		comparer.compareGraphemes();
		comparer.compareGraphemeNaturalClasses();
		comparer.compareEnvironments();
		comparer.compareSonorityHierarchy();
		comparer.compareSonorityHierarchyOrder();
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
		SHApproachLanguageComparisonHTMLFormatter formatter = new SHApproachLanguageComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SHApproachLanguageComparisonHTMLEnglish.html");
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
		SHApproachLanguageComparisonHTMLFormatter formatter = new SHApproachLanguageComparisonHTMLFormatter(
				comparerSame, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SHApproachLanguageComparisonSameHTMLEnglish.html");
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
		SHApproachLanguageComparisonHTMLFormatter formatter = new SHApproachLanguageComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SHApproachLanguageComparisonHTMLSpanish.html");
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
		SHApproachLanguageComparisonHTMLFormatter formatter = new SHApproachLanguageComparisonHTMLFormatter(
				comparerSame, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/SHApproachLanguageComparisonSameHTMLSpanish.html");
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
