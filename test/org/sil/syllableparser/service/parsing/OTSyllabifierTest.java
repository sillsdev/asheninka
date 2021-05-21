// Copyright (c) 2021 SIL International 
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
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.*;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.syllableparser.model.otapproach.OTSyllable;
import org.sil.syllableparser.service.parsing.CVNaturalClasser;
import org.sil.syllableparser.service.parsing.CVNaturalClasserResult;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.CVSyllabifier;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class OTSyllabifierTest {

	OTApproach ota;
	OTSegmenter segmenter;
	ObservableList<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	List<OTConstraint> constraints;
	List<OTConstraintRanking> rankings;
	OTSyllabifier otSyllabifier;

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
		ota = languageProject.getOTApproach();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new OTSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		constraints = ota.getValidActiveOTConstraints();
		rankings = ota.getActiveOTConstraintRankings();
		otSyllabifier = new OTSyllabifier(ota);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void syllabifierTest() {
		assertEquals(8, constraints.size());
		String name = constraints.get(0).getConstraintName().trim();
		assertEquals("NoCoda", name);
		name = constraints.get(2).getConstraintName().trim();
		assertEquals("Onset1", name);
		assertEquals(2, rankings.size());
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedStructuralOptions, String expectedSyllabification) {
		otSyllabifier = new OTSyllabifier(ota);
		boolean fSuccess = otSyllabifier.convertStringToSyllables(word);
		assertEquals(success, fSuccess);
		List<OTSyllable> syllablesInWord = otSyllabifier.getSyllablesInCurrentWord();
		assertEquals(numberOfSyllables, syllablesInWord.size());
		assertEquals(expectedStructuralOptions, otSyllabifier.getStructuralOptionsInParse());
		assertEquals(expectedSyllabification,
				otSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void syllabifyWordTest() {
		checkSyllabifyWord("Chiko", true, 2, "on.on", "Chi.ko");
		checkSyllabifyWord("dapbek", true, 2, "onc.onc", "dap.bek");
		checkSyllabifyWord("bampidon", true, 3, "onc.on.onc", "bam.pi.don");
		checkSyllabifyWord("bovdek", true, 2, "onc.onc", "bov.dek");
		checkSyllabifyWord("fuhgt", true, 1, "onccc", "fuhgt");
		checkSyllabifyWord("blofugh", true, 2, "oon.oncc", "blo.fugh");
		checkSyllabifyWord("bo", true, 1, "on", "bo");
		checkSyllabifyWord("funglo", true, 2, "oncc.on", "fung.lo");
		checkSyllabifyWord("fugh", true, 1, "oncc", "fugh");
		checkSyllabifyWord("flu", true, 1, "oon", "flu");
		checkSyllabifyWord("cat", false, 0, "", ""); // no c segment
	}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables,
			String expectedstructuralOptions, String expectedSyllabification) {
		boolean fSuccess = otSyllabifier.convertStringToSyllables(word);
		assertEquals(success, fSuccess);
		List<OTSyllable> syllablesInWord = otSyllabifier.getSyllablesInCurrentWord();
		assertEquals(numberOfSyllables, syllablesInWord.size());
		String joined = syllablesInWord.stream().map(OTSyllable::getStructuralOptionsInSyllable)
				.collect(Collectors.joining("."));
		assertEquals(expectedstructuralOptions, joined);
		assertEquals(expectedSyllabification, otSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void traceSyllabifyWordTest() {
		otSyllabifier.setDoTrace(true);
		checkSyllabifyWord("Chiko", true, 2, "CV, CV", "Chi.ko");
		List<CVTraceSyllabifierInfo> traceInfo = otSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		CVTraceSyllabifierInfo sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());

		checkSyllabifyWord("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		traceInfo = otSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());

		traceInfo = otSyllabifier.getSyllabifierTraceInfo();
		sylInfo = traceInfo.get(1);
		assertNotNull(sylInfo);
		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
		assertEquals(true, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(true, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(1);
		assertNotNull(sylInfo);
		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
		assertEquals(true, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());

		checkSyllabifyWord("funglo", false, 0, "", ""); // CVCC only word
														// finally; CCV not
														// possible word
														// medially
		traceInfo = otSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());

		traceInfo = otSyllabifier.getSyllabifierTraceInfo();
		sylInfo = traceInfo.get(1);
		assertNotNull(sylInfo);
		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
		assertEquals(false, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false, sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());

	}
}
