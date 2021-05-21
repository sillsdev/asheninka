/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.OTSegmenter;

/**
 * @author Andy Black
 *
 */
public class OTConstraintMatcherTest {

	OTSegmenter segmenter;
	OTConstraintMatcher matcher;
	OTConstraint constraint;
	List<OTConstraint> constraints;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		List<Grapheme> activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new OTSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		OTApproach otApproach = languageProject.getOTApproach();
		constraints = otApproach.getValidActiveOTConstraints();
		matcher = OTConstraintMatcher.getInstance();
		matcher.setLanguageProject(languageProject);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void marginVMatcherTest() {
		Optional<OTConstraint> marginV = constraints.stream()
				.filter(r -> r.getID().equals("886ca941-5cb8-4b4e-a07b-c7721059d9ca")).findFirst();
		assertTrue(marginV.isPresent());
		constraint = marginV.get();
		checkMatch("", false, 0);
		checkMatch("t", false, 1);
		checkMatch("tl", false, 2);
		checkMatch("a", true, 1);
	}

	@Test
	public void onset1MatcherTest() {
		Optional<OTConstraint> onset1 = constraints.stream()
				.filter(r -> r.getID().equals("5c1397cb-13d3-4430-9785-06c273aa17a7")).findFirst();
		assertTrue(onset1.isPresent());
		constraint = onset1.get();
		checkMatch("", false, 0);
		checkMatch("t", false, 1);
		checkMatch("tl", true, 2);
		checkMatch("ta", true, 2);
	}

	@Test
	public void noCodaMatcherTest() {
		Optional<OTConstraint> noCoda = constraints.stream()
				.filter(r -> r.getID().equals("50863688-ec78-4f93-9ba0-1f5f236e35bb")).findFirst();
		assertTrue(noCoda.isPresent());
		constraint = noCoda.get();
		checkMatch("", false, 0);
		checkMatch("t", true, 1);
		checkMatch("tl", true, 2);
		checkMatch("a", true, 1);
		checkMatch("n", true, 1);
		}

	@Test
	public void onset2MatcherTest() {
		Optional<OTConstraint> onset2 = constraints.stream()
				.filter(r -> r.getID().equals("377e55fb-ab7f-4f45-803c-137af536c0f9")).findFirst();
		assertTrue(onset2.isPresent());
		constraint = onset2.get();
		checkMatch("", false, 0);
		checkMatch("t", true, 1);
		checkMatch("tl", true, 2);
		checkMatch("a", true, 1);
		checkMatch("at", true, 2);
	}

	@Test
	public void peakCMatcherTest() {
		Optional<OTConstraint> peakC = constraints.stream()
				.filter(r -> r.getID().equals("8634013f-2c6c-477c-b928-1ca9b922feb8")).findFirst();
		assertTrue(peakC.isPresent());
		constraint = peakC.get();
		checkMatch("", false, 0);
		checkMatch("t", true, 1);
		checkMatch("tl", true, 2);
		checkMatch("a", false, 1);
		checkMatch("at", false, 2);
		}

	@Test
	public void parseMatcherTest() {
		Optional<OTConstraint> parse = constraints.stream()
				.filter(r -> r.getID().equals("5274463f-e44c-4474-a306-312fd5c8fd91")).findFirst();
		assertTrue(parse.isPresent());
		constraint = parse.get();
		checkMatch("", false, 0);
		checkMatch("t", true, 1);
		checkMatch("tl", true, 2);
		checkMatch("a", true, 1);
		checkMatch("at", true, 2);
	}

	@Test
	public void complexOnsetMatcherTest() {
		Optional<OTConstraint> complexOnset = constraints.stream()
				.filter(r -> r.getID().equals("fe57515a-d4a3-427c-a739-b7ebb720108a")).findFirst();
		assertTrue(complexOnset.isPresent());
		constraint = complexOnset.get();
		checkMatch("", false, 0);
		checkMatch("t", false, 1);
		checkMatch("tl", true, 2);
		checkMatch("a", false, 1);
		checkMatch("at", false, 2);
	}

	private void checkMatch(String wordPortion, boolean expectedMatch, int expectedSegments) {
		CVSegmenterResult segResult = segmenter.segmentWord(wordPortion);
		boolean fSuccess = segResult.success;
		assertTrue(fSuccess);
		List<OTSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		assertEquals(expectedSegments, segmentsInWord.size());
		OTSegmentInSyllable segInSyl1 = null;
		OTSegmentInSyllable segInSyl2 = null;
		if (expectedSegments > 0) {
			segInSyl1 = segmentsInWord.get(0);
		}
		if (expectedSegments == 2) {
			segInSyl2 = segmentsInWord.get(1);
		}
		assertEquals(expectedMatch, matcher.match(constraint, segInSyl1, segInSyl2, expectedSegments));
	}
}
