// Copyright (c) 2021-2022 SIL International
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
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;

/**
 * @author Andy Black
 *
 */
public class MoraicApproachLanguageComparisonHTMLFormatterTest {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	MoraicApproach mua1;
	MoraicApproach mua2;
	MoraicApproachLanguageComparer comparer;
	MoraicApproachLanguageComparer comparerSame;
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
		mua1 = languageProject1.getMoraicApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file2 = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file2);
		mua2 = languageProject2.getMoraicApproach();
		comparer = new MoraicApproachLanguageComparer(mua1, mua2);
		invokeComparison(comparer, file1, file2);
		comparerSame = new MoraicApproachLanguageComparer(mua1, mua1);
		invokeComparison(comparerSame, file1, file1);
		dateTime = LocalDateTime.of(2016, Month.APRIL, 9, 8, 7, 3);
	}

	protected void invokeComparison(MoraicApproachLanguageComparer comparer, File file1, File file2) {
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
		comparer.compareTemplates();
		comparer.compareTemplateOrder();
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
			MoraicApproachLanguageComparisonHTMLFormatter formatter = new MoraicApproachLanguageComparisonHTMLFormatter(
					comparer, locale, dateTime);
			String result = formatter.format();
			File file = new File(
					"test/org/sil/syllableparser/testData/MoraicApproachLanguageComparisonHTMLEnglish.html");
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
		MoraicApproachLanguageComparisonHTMLFormatter formatter = new MoraicApproachLanguageComparisonHTMLFormatter(
				comparerSame, locale, dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/MoraicApproachLanguageComparisonSameHTMLEnglish.html");
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
		MoraicApproachLanguageComparisonHTMLFormatter formatter = new MoraicApproachLanguageComparisonHTMLFormatter(
				comparer, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/MoraicApproachLanguageComparisonHTMLSpanish.html");
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
		MoraicApproachLanguageComparisonHTMLFormatter formatter = new MoraicApproachLanguageComparisonHTMLFormatter(
				comparerSame, new Locale("es"), dateTime);
		String result = formatter.format();
		File file = new File("test/org/sil/syllableparser/testData/MoraicApproachLanguageComparisonSameHTMLSpanish.html");
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
