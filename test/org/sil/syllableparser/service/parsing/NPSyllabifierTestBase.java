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
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
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

}