// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.service.comparison.MoraicApproachLanguageComparer;
import org.sil.syllableparser.service.comparison.DifferentSegment;
import org.sil.syllableparser.service.comparison.DifferentWord;

/**
 * @author Andy Black
 *
 */
public class MoraicApproachLanguageComparerTest {
	
	// N.B., since the moraic approach uses the same items as the ONC for most views
	// we only test here for those that differ: segments, syllabification parameters, and words.

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	MoraicApproach mua1;
	MoraicApproach mua2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject1 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject1, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		mua1 = languageProject1.getMoraicApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		mua2 = languageProject2.getMoraicApproach();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void languagesContentsTest() {
		// Segments
		ObservableList<Segment> segmentInventory;
		segmentInventory = languageProject1.getSegmentInventory();
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		segmentInventory = languageProject2.getSegmentInventory();
		assertEquals("Segment inventory size", 33, segmentInventory.size());
		// words
		ObservableList<Word> words;
		words = mua1.getWords();
		assertEquals("Moraic words size", 10025, words.size());
		words = mua2.getWords();
		assertEquals("Moraic words size", 10025, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		MoraicApproachLanguageComparer comparer = new MoraicApproachLanguageComparer(mua1, mua2);
		compareSyllabificationParameters(comparer);
		compareSegments(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		MoraicApproachLanguageComparer comparer = new MoraicApproachLanguageComparer(mua1, mua1);
		compareSameSegments(comparer);
		comparer.compareSyllabificationParameters();
		assertEquals(false, comparer.useWeightByPositionDiffers);
		assertEquals(false, comparer.maxMorasDiffers);
		compareSameWords(comparer);
	}

	protected void compareSegments(ApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 25, diffs.size());
		List<DifferentSegment> listOfDiffs = new ArrayList<DifferentSegment>();
		listOfDiffs.addAll(diffs);
		DifferentSegment diffSeg = listOfDiffs.get(1);
		Segment seg1 = ((Segment) diffSeg.getObjectFrom1());
		Segment seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("second's 1 is an /a/", "a", seg1.getSegment());
		assertEquals("second's 2 is an /a/", "a", seg2.getSegment());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("number of active graphemes in segment1", 2, seg1.getActiveGraphs().size());
		assertEquals("number of graphemes in segment2", 5, seg2.getGraphs().size());
		assertEquals("number of graphemes in segment2", 4, seg2.getActiveGraphs().size());
		assertEquals("second's 1's graphemes are 'a A'", "a A",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
		assertEquals("second's 2's graphemes are 'a A á Á'", "a A á Á",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		assertEquals(1, seg1.getMoras());
		assertEquals(1, seg2.getMoras());
		diffSeg = listOfDiffs.get(0);
		assertNull("first's 1 is null", diffSeg.getObjectFrom1());
		seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("first's 2 is a /\'/", "'", seg2.getSegment());
		assertEquals("number of graphemes in segment2", 1, seg2.getGraphs().size());
		assertEquals("first's 2's graphemes are '\''", "'",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(2);
		seg1 = ((Segment) diffSeg.getObjectFrom1());
		seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("second's 1 is a /b/", "b", seg1.getSegment());
		assertEquals("second's 2 is a /b/", "b", seg2.getSegment());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("number of active graphemes in segment1", 2, seg1.getActiveGraphs().size());
		assertEquals("number of graphemes in segment2", 2, seg2.getGraphs().size());
		assertEquals("number of graphemes in segment2", 2, seg2.getActiveGraphs().size());
		assertEquals("second's 1's graphemes are 'b B'", "b B",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
		assertEquals("second's 2's graphemes are 'b B'", "b B",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		assertEquals(0, seg1.getMoras());
		assertEquals(0, seg2.getMoras());
		diffSeg = listOfDiffs.get(16);
		seg1 = ((Segment) diffSeg.getObjectFrom1());
		assertEquals("eleventh's 1 is a /ɲ/", "ɲ", seg1.getSegment());
		assertNull("eleventh's 2 is null", diffSeg.getObjectFrom2());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("tenth's 1's graphemes are 'ñ Ñ'", "ñ Ñ",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
	}

	protected void compareWords(ApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 3901, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(0);
		assertEquals("a", ((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("a", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom1()).getMoraicPredictedSyllabification());
		assertEquals("a", ((Word) diffWord.getObjectFrom2()).getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals(null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom2()).getMoraicPredictedSyllabification());
		assertEquals("", ((Word) diffWord.getObjectFrom2()).getMoraicParserResult());
		diffWord = listOfDiffs.get(2);
		assertEquals("third's 1 is ababrastro", "ababrastro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("third's 2 is null", null, ((Word) diffWord.getObjectFrom2()));
		assertEquals("third's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getMoraicPredictedSyllabification());
	}

	protected void compareSameSegments(ApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 0, diffs.size());
	}

	protected void compareSyllabificationParameters(ApproachLanguageComparer comparer) {
		comparer.compareSyllabificationParameters();
		assertEquals(true, comparer.isCodasAllowedDifferent());
		assertTrue(comparer.isLangProj1CodasAllowed());
		assertFalse(comparer.isLangProj2CodasAllowed());
		assertEquals(true, comparer.isOnsetMaximizationDifferent());
		assertFalse(comparer.isLangProj1OnsetMaximization());
		assertTrue(comparer.isLangProj2OnsetMaximization());
		assertEquals(true, comparer.isOnsetPrincipleDifferent());
		assertEquals(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET, comparer.getLangProj1OnsetPrinciple());
		assertEquals(OnsetPrincipleType.ONSETS_NOT_REQUIRED, comparer.getLangProj2OnsetPrinciple());
	}

	protected void compareSameWords(ApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 0, diffs.size());
	}
}
