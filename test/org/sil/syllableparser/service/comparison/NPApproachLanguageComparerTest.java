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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import javafx.collections.ObservableList;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPFilter;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.service.comparison.DifferentSegment;
import org.sil.syllableparser.service.comparison.DifferentWord;

/**
 * @author Andy Black
 *
 */
public class NPApproachLanguageComparerTest {
	
	// N.B., since the moraic approach uses the same items as the ONC for most views
	// we only test here for those that differ: segments, syllabification parameters, and words.

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	NPApproach npa1;
	NPApproach npa2;

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
		npa1 = languageProject1.getNPApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		npa2 = languageProject2.getNPApproach();
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
		words = npa1.getWords();
		assertEquals("Moraic words size", 10025, words.size());
		words = npa2.getWords();
		assertEquals("Moraic words size", 10025, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		NPApproachLanguageComparer comparer = new NPApproachLanguageComparer(npa1, npa2);
		compareSyllabificationParameters(comparer);
		compareSegments(comparer);
		compareNPFilters(comparer);
		compareNPRules(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		NPApproachLanguageComparer comparer = new NPApproachLanguageComparer(npa1, npa1);
		compareSameSegments(comparer);
		comparer.compareSyllabificationParameters();
		assertEquals(false, comparer.useWeightByPositionDiffers);
		assertEquals(false, comparer.maxMorasDiffers);
		compareSameWords(comparer);
	}

	protected void compareNPFilters(NPApproachLanguageComparer comparer) {
		npa1.getNPFilters().get(0).setActive(true);
		comparer.compareNPFilters();
		SortedSet<DifferentFilter> diffs = comparer.getFiltersWhichDiffer();
		assertEquals("number of different filters", 2, diffs.size());
		List<DifferentFilter> listOfDiffs = new ArrayList<DifferentFilter>();
		listOfDiffs.addAll(diffs);
		DifferentFilter diffFilter = listOfDiffs.get(0);
		NPFilter f1 = (NPFilter) diffFilter.getObjectFrom1();
		NPFilter f2 = (NPFilter) diffFilter.getObjectFrom2();
		assertNull(f1);
		assertEquals("Coda failure filter", f2.getTemplateFilterName());
		assertEquals(FilterType.CODA, f2.getTemplateFilterType());
		assertEquals("m k", f2.getTemplateFilterRepresentation());
		diffFilter = listOfDiffs.get(1);
		f1 = (NPFilter) diffFilter.getObjectFrom1();
		f2 = (NPFilter) diffFilter.getObjectFrom2();
		assertEquals("Onset failure filter", f1.getTemplateFilterName());
		assertEquals("Onset failure filter", f2.getTemplateFilterName());
		assertEquals(FilterType.ONSET, f1.getTemplateFilterType());
		assertEquals(FilterType.ONSET, f2.getTemplateFilterType());
		assertEquals("t l", f1.getTemplateFilterRepresentation());
		assertEquals("t n", f2.getTemplateFilterRepresentation());
	}

	@Test
	public void compareFiltersOrderTest() {
		NPApproachLanguageComparer comparer = new NPApproachLanguageComparer(npa1, npa2);
		comparer.compareFilterOrder();
		LinkedList<Diff> differences = comparer.getFilterOrderDifferences();
		assertEquals(48, differences.size());
	}

	protected void compareNPRules(NPApproachLanguageComparer comparer) {
		comparer.compareNPRules();
		SortedSet<DifferentNPRule> diffs = comparer.getRulesWhichDiffer();
		assertEquals("number of different rules", 3, diffs.size());
		List<DifferentNPRule> listOfDiffs = new ArrayList<DifferentNPRule>();
		listOfDiffs.addAll(diffs);
		DifferentNPRule diffFilter = listOfDiffs.get(0);
		NPRule r1 = (NPRule) diffFilter.getObjectFrom1();
		NPRule r2 = (NPRule) diffFilter.getObjectFrom2();
		assertEquals("Augment coda with another coda", r1.getRuleName());
		assertEquals("Augment coda with another coda", r2.getRuleName());
		assertEquals(NPRuleAction.AUGMENT, r1.getRuleAction());
		assertEquals(NPRuleAction.AUGMENT, r2.getRuleAction());
		assertEquals(NPRuleLevel.N_BAR, r1.getRuleLevel());
		assertEquals(NPRuleLevel.N_BAR, r2.getRuleLevel());
		assertEquals("6fbdfa10-f79d-44c6-a1bf-b474ac386c12", r1.getAffectedSegOrNC().getUuid());
		assertEquals("b500749a-9617-444c-ae28-7552cb3236ec", r2.getAffectedSegOrNC().getUuid());
		assertEquals("6fbdfa10-f79d-44c6-a1bf-b474ac386c12", r1.getContextSegOrNC().getUuid());
		assertEquals("b500749a-9617-444c-ae28-7552cb3236ec", r2.getContextSegOrNC().getUuid());
		diffFilter = listOfDiffs.get(1);
		r1 = (NPRule) diffFilter.getObjectFrom1();
		r2 = (NPRule) diffFilter.getObjectFrom2();
		assertEquals("Coda", r1.getRuleName());
		assertEquals("Coda", r2.getRuleName());
		assertEquals(NPRuleAction.ATTACH, r1.getRuleAction());
		assertEquals(NPRuleAction.ATTACH, r2.getRuleAction());
		assertEquals(NPRuleLevel.N_BAR, r1.getRuleLevel());
		assertEquals(NPRuleLevel.N_BAR, r2.getRuleLevel());
		assertEquals("6fbdfa10-f79d-44c6-a1bf-b474ac386c12", r1.getAffectedSegOrNC().getUuid());
		assertEquals("b500749a-9617-444c-ae28-7552cb3236ec", r2.getAffectedSegOrNC().getUuid());
		assertEquals("9821d4ba-8c34-432d-9afe-7c428dcd9afa", r1.getContextSegOrNC().getUuid());
		assertEquals("9821d4ba-8c34-432d-9afe-7c428dcd9afa", r2.getContextSegOrNC().getUuid());
	}

	@Test
	public void compareNPRulesOrderTest() {
		NPApproachLanguageComparer comparer = new NPApproachLanguageComparer(npa1, npa2);
		comparer.compareNPRuleOrder();
		LinkedList<Diff> differences = comparer.getRuleOrderDifferences();
		assertEquals(94, differences.size());
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
		assertEquals("number of different words", 1287, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(0);
		assertEquals("a", ((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("a", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom1()).getNPPredictedSyllabification());
		assertEquals("a", ((Word) diffWord.getObjectFrom2()).getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals(null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom2()).getNPPredictedSyllabification());
		assertEquals("", ((Word) diffWord.getObjectFrom2()).getNPParserResult());
		diffWord = listOfDiffs.get(2);
		assertEquals("third's 1 is ababrastro", "ababrastro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("third's 2 is null", null, ((Word) diffWord.getObjectFrom2()));
		assertEquals("third's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getNPPredictedSyllabification());
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
