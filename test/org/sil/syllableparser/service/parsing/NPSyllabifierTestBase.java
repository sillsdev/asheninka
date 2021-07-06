/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPFilter;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.model.npapproach.NPSyllabificationStatus;
import org.sil.syllableparser.model.npapproach.NPSyllable;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class NPSyllabifierTestBase {

	protected NPApproach npApproach;
	protected ObservableList<SHNaturalClass> naturalClasses;
	protected NPSegmenter segmenter;
	protected ObservableList<Segment> segmentInventory;
	protected List<Grapheme> activeGraphemes;
	protected List<SHNaturalClass> shNaturalClasses;
	protected NPSyllabifier npSyllabifier;
	protected LanguageProject languageProject;
	protected String projectFile = Constants.UNIT_TEST_DATA_FILE;
	protected NPRule rule;
	protected List<NPRule> rules;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(projectFile);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new NPSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		npApproach = languageProject.getNPApproach();
		shNaturalClasses = npApproach.getActiveSHNaturalClasses();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		npSyllabifier = new NPSyllabifier(npApproach);
		rules = npApproach.getValidActiveNPRules();
		}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables, String expectedSyllabification,
			String expectedLTDescription) {
				CVSegmenterResult segResult = segmenter.segmentWord(word);
				boolean fSuccess = segResult.success;
				List<NPSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
				fSuccess = npSyllabifier.syllabify(segmentsInWord);
				assertEquals("word syllabified", success, fSuccess);
				List<NPSyllable> syllablesInWord = npSyllabifier.getSyllablesInCurrentWord();
				assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
						syllablesInWord.size());
				assertEquals("Expected Syllabification of word", expectedSyllabification,
						npSyllabifier.getSyllabificationOfCurrentWord());
				assertEquals("LingTree Description of word", expectedLTDescription,
						npSyllabifier.getLingTreeDescriptionOfCurrentWord());
			}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables, String expectedSyllabification,
			String expectedLTDescription) {
				boolean fSuccess = npSyllabifier.convertStringToSyllables(word);
				assertEquals("word syllabified", success, fSuccess);
				List<NPSyllable> syllablesInWord = npSyllabifier.getSyllablesInCurrentWord();
				assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
						syllablesInWord.size());
				assertEquals("Expected Syllabification of word", expectedSyllabification,
						npSyllabifier.getSyllabificationOfCurrentWord());
				assertEquals("LingTree Description of word", expectedLTDescription,
						npSyllabifier.getLingTreeDescriptionOfCurrentWord());
			}

	protected void checkSSPTracingStep(NPTracingStep tracingStep, String seg1, String nc1, String seg2,
			String nc2, SHComparisonResult result) {
				if (seg1 == null) {
					assertNull(tracingStep.getSegment1());
				} else {
					assertEquals(seg1, tracingStep.getSegment1().getSegment());
				}
				if (nc1 == null) {
					assertNull(tracingStep.getNaturalClass1());
				} else {
					assertEquals(nc1, tracingStep.getNaturalClass1().getNCName());
				}
				if (seg2 == null) {
					assertNull(tracingStep.getSegment2());
				} else {
					assertEquals(seg2, tracingStep.getSegment2().getSegment());
				}
				if (nc2 == null) {
					assertNull(tracingStep.getNaturalClass2());
				} else {
					assertEquals(nc2, tracingStep.getNaturalClass2().getNCName());
				}
				assertEquals(result, tracingStep.comparisonResult);
			}

	protected void turnOnseMaximizationOff() {
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		// onset maximization is handled by ordering the rules
		rule = rules.get(3);
		rules.add(2, rule);
		rules.remove(4);
		ObservableList<NPRule> orules = FXCollections.observableArrayList(rules);
		npApproach.setNPRules(orules);
	}

	protected void turnOnsetMaximizationOn() {
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		// onset maximization is handled by ordering the rules
		rule = rules.get(3);
		rules.add(2, rule);
		rules.remove(4);
		ObservableList<NPRule> orules = FXCollections.observableArrayList(rules);
		npApproach.setNPRules(orules);
	}

	protected void turnCodasAllowedOff() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		// coda off is handled by deactivating the coda-oriented rules
		Optional<NPRule> codaRule = rules.stream()
				.filter(r -> r.getID().equals("7d8c3b88-7d72-40ac-a8f2-0f1df56aeef1")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(false);
		codaRule = rules.stream()
				.filter(r -> r.getID().equals("b70cc8c6-13e8-4dc9-87ef-3a2f9cdcd7eb")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(false);
	}

	protected void turnCodasAllowedOn() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		// coda on is handled by activating the coda-oriented rules
		Optional<NPRule> codaRule = rules.stream()
				.filter(r -> r.getID().equals("7d8c3b88-7d72-40ac-a8f2-0f1df56aeef1")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(true);
		codaRule = rules.stream()
				.filter(r -> r.getID().equals("b70cc8c6-13e8-4dc9-87ef-3a2f9cdcd7eb")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(true);
	}

	protected void turnOffOnsetAndCodaRules() {
		// turn off onset and coda rules, both attach and augment ones
		npApproach.getNPRules().get(1).setActive(false);
		npApproach.getNPRules().get(2).setActive(false);
		npApproach.getNPRules().get(3).setActive(false);
		npApproach.getNPRules().get(4).setActive(false);
		npApproach.getNPRules().get(5).setActive(false);
	}

	protected void turnOnTestingFilters() {
		List<NPFilter> filters = npApproach.getNPFilters().stream().filter(filter -> filter.isValid())
				.collect(Collectors.toList());
		// turn on the three inactive fail filters we're testing with
		Optional<NPFilter> filter = filters.stream()
				.filter(r -> r.getID().equals("0b187b16-3dc2-414a-9c8a-76bbfccd8a47")).findFirst();
		assertTrue(filter.isPresent());
		filter.get().setActive(true);
		filter = filters.stream()
				.filter(r -> r.getID().equals("c366bc4d-cb94-44f9-830d-49ae4404596c")).findFirst();
		assertTrue(filter.isPresent());
		filter.get().setActive(true);
		filter = filters.stream()
				.filter(r -> r.getID().equals("710653a7-e536-4815-ae69-5ecec206b19e")).findFirst();
		assertTrue(filter.isPresent());
		filter.get().setActive(true);
	}

	protected void checkNPTracingStep(NPTracingStep step, NPSyllabificationStatus status, String segment, boolean success) {
		assertEquals(status, step.getStatus());
		if (step.getSegment1() != null)
			assertEquals(segment, step.getSegment1().getSegment());
		assertEquals(success, step.isSuccessful());
	}

}
