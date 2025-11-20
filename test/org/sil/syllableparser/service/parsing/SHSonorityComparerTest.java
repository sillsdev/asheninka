// Copyright (c) 2018-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter class is functioning
 *         correctly
 */
public class SHSonorityComparerTest {

	SHApproach sonHierApproach;
	ObservableList<SHNaturalClass> naturalClasses;
	CVSegmenter segmenter;
	List<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	SHSonorityComparer sonorityComparer;

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
		sonHierApproach = languageProject.getSHApproach();
		sonorityComparer = new SHSonorityComparer(sonHierApproach);
		segmentInventory = sonHierApproach.getLanguageProject().getActiveSegmentsInInventory();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes, languageProject.getActiveGraphemeNaturalClasses());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void sonorityComparerTest() {
		assertEquals(26, segmentInventory.size());
		Segment segmentA = segmentInventory.get(0);
		assertEquals("a", segmentA.getSegment());
		SHComparisonResult result = sonorityComparer.compare(null, segmentA);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentA, null);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentA, segmentA);
		assertEquals(SHComparisonResult.EQUAL, result);

		Segment segmentB = segmentInventory.get(1);
		assertEquals("b", segmentB.getSegment());
		// N.B. b is not in any natural class
		result = sonorityComparer.compare(segmentA, segmentB);
		assertEquals(SHComparisonResult.MISSING2, result);
		result = sonorityComparer.compare(segmentB, segmentA);
		assertEquals(SHComparisonResult.MISSING1, result);
		result = sonorityComparer.compare(segmentB, segmentB);
		assertEquals(SHComparisonResult.MISSING1, result);
		
		Segment segmentD = segmentInventory.get(3);
		assertEquals("d", segmentD.getSegment());
		Segment segmentCh = segmentInventory.get(2);
		assertEquals("ch", segmentCh.getSegment());
		result = sonorityComparer.compare(segmentD, segmentCh);
		assertEquals(SHComparisonResult.EQUAL, result);
		result = sonorityComparer.compare(segmentA, segmentD);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentD, segmentA);
		assertEquals(SHComparisonResult.LESS, result);

		Segment segmentE = segmentInventory.get(4);
		assertEquals("e", segmentE.getSegment());
		result = sonorityComparer.compare(segmentE, segmentA);
		assertEquals(SHComparisonResult.EQUAL, result);
		result = sonorityComparer.compare(segmentE, segmentD);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentD, segmentE);
		assertEquals(SHComparisonResult.LESS, result);
		
		Segment segmentL = segmentInventory.get(10);
		assertEquals("l", segmentL.getSegment());
		result = sonorityComparer.compare(segmentL, segmentA);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentA, segmentL);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentL, segmentD);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentD, segmentL);
		assertEquals(SHComparisonResult.LESS, result);

		Segment segmentR = segmentInventory.get(16);
		assertEquals("r", segmentR.getSegment());
		result = sonorityComparer.compare(segmentR, segmentL);
		assertEquals(SHComparisonResult.EQUAL, result);
		result = sonorityComparer.compare(segmentR, segmentL);
		assertEquals(SHComparisonResult.EQUAL, result);

		Segment segmentM = segmentInventory.get(11);
		assertEquals("m", segmentM.getSegment());
		result = sonorityComparer.compare(segmentM, segmentA);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentA, segmentM);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentM, segmentL);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentL, segmentM);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentM, segmentD);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentD, segmentM);
		assertEquals(SHComparisonResult.LESS, result);

		Segment segmentN = segmentInventory.get(12);
		assertEquals("n", segmentN.getSegment());
		result = sonorityComparer.compare(segmentN, segmentM);
		assertEquals(SHComparisonResult.EQUAL, result);
		result = sonorityComparer.compare(segmentM, segmentN);
		assertEquals(SHComparisonResult.EQUAL, result);

		Segment segmentW = segmentInventory.get(22);
		assertEquals("w", segmentW.getSegment());
		result = sonorityComparer.compare(segmentW, segmentA);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentA, segmentW);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentW, segmentL);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentL, segmentW);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentW, segmentM);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentM, segmentW);
		assertEquals(SHComparisonResult.LESS, result);
		result = sonorityComparer.compare(segmentW, segmentD);
		assertEquals(SHComparisonResult.MORE, result);
		result = sonorityComparer.compare(segmentD, segmentW);
		assertEquals(SHComparisonResult.LESS, result);

		Segment segmentY = segmentInventory.get(24);
		assertEquals("y", segmentY.getSegment());
		result = sonorityComparer.compare(segmentY, segmentW);
		assertEquals(SHComparisonResult.EQUAL, result);
		result = sonorityComparer.compare(segmentW, segmentY);
		assertEquals(SHComparisonResult.EQUAL, result);
	}

	@Test
	public void sonorityLevelTest() {
		assertEquals(26, segmentInventory.size());

		checkLevel(segmentInventory.get(0), "a", 0);
		// N.B. b is not in any natural class
		checkLevel(segmentInventory.get(1), "b", -1);

		checkLevel(segmentInventory.get(3), "d", 4);
		checkLevel(segmentInventory.get(2), "ch", 4);
		checkLevel(segmentInventory.get(4), "e", 0);
		checkLevel(segmentInventory.get(10), "l", 2);
		checkLevel(segmentInventory.get(16), "r", 2);
		checkLevel(segmentInventory.get(11), "m", 3);
		checkLevel(segmentInventory.get(12), "n", 3);
		checkLevel(segmentInventory.get(22), "w", 1);
		checkLevel(segmentInventory.get(24), "y", 1);
	}

	private void checkLevel(Segment segment, String form, int expectedLevel) {
		assertEquals(form, segment.getSegment());
		int level = sonorityComparer.getLevel(segment);
		assertEquals(expectedLevel, level);
	}
}
