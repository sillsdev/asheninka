// Copyright (c) 2021 SIL International
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
import java.util.Comparator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class NPApproachLanguageComparisonHTMLFormatterTest {

	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	NPApproach npa1;
	NPApproach npa2;
	NPApproachLanguageComparer comparer;
	NPApproachLanguageComparer comparerSame;
	private Locale locale;
	private LocalDateTime dateTime;
	SortedSet<DifferentCVNaturalClass> cvNaturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVNaturalClass::getSortingValue));
	private File file1;
	private File file2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject1 = new LanguageProject();
		locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject1, locale);
		file1 = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file1);
		npa1 = languageProject1.getNPApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file2 = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file2);
		npa2 = languageProject2.getNPApproach();
		comparer = new NPApproachLanguageComparer(npa1, npa2);
		invokeComparison(comparer, file1, file2);
		comparerSame = new NPApproachLanguageComparer(npa1, npa1);
		invokeComparison(comparerSame, file1, file1);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 8, 7, 3);
	}

	protected void invokeComparison(NPApproachLanguageComparer comparer, File file1, File file2) {
		comparer.setDataSet1Info(file1.getPath());
		comparer.setDataSet2Info(file2.getPath());
		comparer.compareSegmentInventory();
		comparer.compareGraphemes();
		comparer.compareGraphemeNaturalClasses();
		comparer.compareEnvironments();
		comparer.compareSonorityHierarchy();
		comparer.compareSonorityHierarchyOrder();
		comparer.compareSyllabificationParameters();
		comparer.compareCVNaturalClasses(comparer.getOnca1().getActiveCVNaturalClasses(), comparer
				.getOnca2().getActiveCVNaturalClasses(), cvNaturalClassesWhichDiffer);
		comparer.compareNPRules();
		comparer.compareNPRuleOrder();
		comparer.compareFilters();
		comparer.compareFilterOrder();
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
		try {
			NPApproachLanguageComparisonHTMLFormatter formatter = new NPApproachLanguageComparisonHTMLFormatter(
					comparer, locale, dateTime);
			String result = formatter.format();
			File file = new File(
					"test/org/sil/syllableparser/testData/NPApproachLanguageComparisonHTMLEnglish.html");
			Stream<String> contents = Files.lines(file.toPath());
			String scontents = contents.collect(Collectors.joining("\n"));
			contents.close();
			assertEquals(scontents, result);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void formattingSameEnglishTest() {
		NPApproachLanguageComparisonHTMLFormatter formatter = new NPApproachLanguageComparisonHTMLFormatter(
				comparerSame, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/NPApproachLanguageComparisonSameHTMLEnglish.html");
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
	public void formattingSpanishTest() {
		NPApproachLanguageComparisonHTMLFormatter formatter = new NPApproachLanguageComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/NPApproachLanguageComparisonHTMLSpanish.html");
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
	public void formattingSameSpanishTest() {
		NPApproachLanguageComparisonHTMLFormatter formatter = new NPApproachLanguageComparisonHTMLFormatter(
				comparerSame, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/NPApproachLanguageComparisonSameHTMLSpanish.html");
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
