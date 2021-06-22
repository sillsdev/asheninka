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
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.syllableparser.service.comparison.DifferentSegment;
import org.sil.syllableparser.service.comparison.DifferentWord;

/**
 * @author Andy Black
 *
 */
public class OTApproachLanguageComparerTest {
	
	// N.B., since the moraic approach uses the same items as the ONC for most views
	// we only test here for those that differ: segments, syllabification parameters, and words.

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	OTApproach ota1;
	OTApproach ota2;

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
		ota1 = languageProject1.getOTApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		ota2 = languageProject2.getOTApproach();
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
		words = ota1.getWords();
		assertEquals("OT words size", 10025, words.size());
		words = ota2.getWords();
		assertEquals("OT words size", 10025, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		OTApproachLanguageComparer comparer = new OTApproachLanguageComparer(ota1, ota2);
		compareSegments(comparer);
		compareOTConstraints(comparer);
		compareOTConstraintRankings(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		OTApproachLanguageComparer comparer = new OTApproachLanguageComparer(ota1, ota1);
		compareSameSegments(comparer);
		compareSameOTConstraints(comparer);
		compareSameWords(comparer);
	}

	protected void compareOTConstraints(OTApproachLanguageComparer comparer) {
		comparer.compareOTConstraints();
		SortedSet<DifferentOTConstraint> diffs = comparer.getConstraintsWhichDiffer();
		assertEquals("number of different constraints", 2, diffs.size());
		List<DifferentOTConstraint> listOfDiffs = new ArrayList<DifferentOTConstraint>();
		listOfDiffs.addAll(diffs);
		DifferentOTConstraint diffFilter = listOfDiffs.get(0);
		OTConstraint c1 = (OTConstraint) diffFilter.getObjectFrom1();
		OTConstraint c2 = (OTConstraint) diffFilter.getObjectFrom2();
		assertEquals("Onset1", c1.getConstraintName());
		assertEquals("Onset1", c2.getConstraintName());
		assertEquals("(\\O(\\O<Any>({n, c, u}))(\\O<Any>(n)))", c1.getLingTreeDescription());
		assertEquals("(\\O(\\O<Any>({n, c, u}))(\\O<Any>(c)))", c2.getLingTreeDescription());
		diffFilter = listOfDiffs.get(1);
		c1 = (OTConstraint) diffFilter.getObjectFrom1();
		c2 = (OTConstraint) diffFilter.getObjectFrom2();
		assertEquals("Onset2", c1.getConstraintName());
		assertNull(c2);
		assertEquals("(\\O(\\O# <Any>(n)))", c1.getLingTreeDescription());
	}

	protected void compareOTConstraintRankings(OTApproachLanguageComparer comparer) {
		comparer.compareOTConstraintRankings();;
		SortedSet<DifferentOTConstraintRanking> diffs = comparer.getConstraintRankingsWhichDiffer();
		assertEquals("number of different constraint rankings", 1, diffs.size());
		List<DifferentOTConstraintRanking> listOfDiffs = new ArrayList<DifferentOTConstraintRanking>();
		listOfDiffs.addAll(diffs);
		DifferentOTConstraintRanking diffFilter = listOfDiffs.get(0);
		OTConstraintRanking r1 = (OTConstraintRanking) diffFilter.getObjectFrom1();
		OTConstraintRanking r2 = (OTConstraintRanking) diffFilter.getObjectFrom2();
		assertEquals("Pass one", r1.getName());
		assertEquals("Pass one", r2.getName());
		assertEquals("*Peak/C < *Margin/V < Parse < *Complex/Onset < *Complex/Coda < NoCoda < Onset1 < Onset2", r1.getRankingRepresentation());
		assertEquals("*Peak/C < *Complex/Onset < *Margin/V < Parse < *Complex/Coda < NoCoda < Onset1 < Onset2", r2.getRankingRepresentation());
	}

	@Test
	public void compareOTConstraintRankingsOrderTest() {
		OTApproachLanguageComparer comparer = new OTApproachLanguageComparer(ota1, ota2);
		comparer.compareOTConstraintRankingsOrder();
		LinkedList<Diff> differences = comparer.getRankingOrderDifferences();
		assertEquals(1, differences.size());
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

	protected void compareWords(OTApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 5844, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(0);
		assertEquals(null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom2()).getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals("ababrastro", ((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("a.ba.bras.tro", ((Word) diffWord.getObjectFrom1()).getOTPredictedSyllabification());
		assertEquals("", ((Word) diffWord.getObjectFrom1()).getOTParserResult());
		assertEquals(null, ((Word) diffWord.getObjectFrom2()));
		diffWord = listOfDiffs.get(2);
		assertEquals("abima'el", ((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("abima'el", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom1()).getOTPredictedSyllabification());
		assertEquals("a.bi.ma.'el", ((Word) diffWord.getObjectFrom2()).getOTPredictedSyllabification());
	}

	protected void compareSameSegments(ApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 0, diffs.size());
	}

	protected void compareSameOTConstraints(OTApproachLanguageComparer comparer) {
		comparer.compareOTConstraints();
		SortedSet<DifferentOTConstraint> diffs = comparer.getConstraintsWhichDiffer();
		assertEquals("number of different constraints", 0, diffs.size());
	}

	protected void compareSameWords(OTApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 0, diffs.size());
	}
}
