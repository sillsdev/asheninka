// Copyright (c) 2026 SIL International
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

import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.service.parsing.HyphenTestBase;

/**
 * @author Andy Black
 *
 */
public class HyphenApproachLanguageComparisonHTMLFormatterTest extends HyphenTestBase {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	HyphenApproach cva1;
	HyphenApproach cva2;
	HyphenApproachLanguageComparer comparer;
	HyphenApproachLanguageComparer comparerSame;
	private Locale locale;
	private LocalDateTime dateTime;
	SortedSet<DifferentHyphenClass> diffs = new TreeSet<>(
			Comparator.comparing(DifferentHyphenClass::getSortingValue));

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject1 = new LanguageProject();
		locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject1, locale);
		File file1 = new File(Constants.UNIT_TEST_HYPHEN_DATA_FILE_NAME);
		xmlBackEndProvider.loadLanguageDataFromFile(file1);
		cva1 = languageProject1.getHyphenApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		File file2 = new File(Constants.UNIT_TEST_HYPHEN_DATA_FILE_2_NAME);
		xmlBackEndProvider.loadLanguageDataFromFile(file2);
		cva2 = languageProject2.getHyphenApproach();
		comparer = new HyphenApproachLanguageComparer(cva1, cva2);
		invokeComparison(comparer, file1, file2);
		comparerSame = new HyphenApproachLanguageComparer(cva1, cva1);
		invokeComparison(comparerSame, file1, file1);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 8, 7, 3);
	}

	protected void invokeComparison(HyphenApproachLanguageComparer comparer, File file1, File file2) {
		comparer.setDataSet1Info(file1.getPath());
		comparer.setDataSet2Info(file2.getPath());
		comparer.compareSegmentInventory();
		comparer.compareGraphemes();
		comparer.compareGraphemeNaturalClasses();
		comparer.compareEnvironments();
		comparer.compareHyphenClasses(comparer.getHa1().getHyphenClasses(), comparer.getHa2().getHyphenClasses(), comparer.getHyphenClassesWhichDiffer());
		comparer.compareHyphenChangeRules();
		comparer.compareHyphenChangeRuleOrder();
		comparer.compareWords();
	}

	@Test
	public void formattingEnglishTest() {
		ApproachLanguageComparisonHTMLFormatter formatter = new HyphenApproachLanguageComparisonHTMLFormatter(
				comparer, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/HyphenApproachLanguageComparisonHTMLEnglish.html");
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
		ApproachLanguageComparisonHTMLFormatter formatter = new HyphenApproachLanguageComparisonHTMLFormatter(
				comparerSame, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/HyphenApproachLanguageComparisonSameHTMLEnglish.html");
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
		ApproachLanguageComparisonHTMLFormatter formatter = new HyphenApproachLanguageComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/HyphenApproachLanguageComparisonHTMLSpanish.html");
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
		ApproachLanguageComparisonHTMLFormatter formatter = new HyphenApproachLanguageComparisonHTMLFormatter(
				comparerSame, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/HyphenApproachLanguageComparisonSameHTMLSpanish.html");
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
