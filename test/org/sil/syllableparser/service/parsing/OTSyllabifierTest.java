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
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.model.otapproach.OTStructuralOptions;
import org.sil.syllableparser.model.otapproach.OTSyllable;
import org.sil.syllableparser.model.otapproach.OTTracingStep;

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
	ResourceBundle bundle;

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
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		otSyllabifier.setBundle(bundle);
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
		checkSyllabifyWord("Chiko", true, 2, "on.on", "Chi.ko", "(W(σ( o(\\L ch(\\G Ch)))( n(\\L i(\\G i))))(σ( o(\\L k(\\G k)))( n(\\L o(\\G o)))))");
		checkSyllabifyWord("dapbek", true, 2, "onc.onc", "dap.bek", "(W(σ( o(\\L d(\\G d)))( n(\\L a(\\G a)))( c(\\L p(\\G p))))(σ( o(\\L b(\\G b)))( n(\\L e(\\G e)))( c(\\L k(\\G k)))))");
		checkSyllabifyWord("bampidon", true, 3, "onc.on.onc", "bam.pi.don", "(W(σ( o(\\L b(\\G b)))( n(\\L a(\\G a)))( c(\\L m(\\G m))))(σ( o(\\L p(\\G p)))( n(\\L i(\\G i))))(σ( o(\\L d(\\G d)))( n(\\L o(\\G o)))( c(\\L n(\\G n)))))");
		checkSyllabifyWord("bovdek", true, 2, "onc.onc", "bov.dek", "(W(σ( o(\\L b(\\G b)))( n(\\L o(\\G o)))( c(\\L v(\\G v))))(σ( o(\\L d(\\G d)))( n(\\L e(\\G e)))( c(\\L k(\\G k)))))");
		checkSyllabifyWord("fuhgt", true, 1, "onccc", "fuhgt", "(W(σ( o(\\L f(\\G f)))( n(\\L u(\\G u)))( c(\\L h(\\G h)))( c(\\L g(\\G g)))( c(\\L t(\\G t)))))");
		checkSyllabifyWord("blofugh", true, 2, "oon.oncc", "blo.fugh", "(W(σ( o(\\L b(\\G b)))( o(\\L l(\\G l)))( n(\\L o(\\G o))))(σ( o(\\L f(\\G f)))( n(\\L u(\\G u)))( c(\\L g(\\G g)))( c(\\L h(\\G h)))))");
		checkSyllabifyWord("bo", true, 1, "on", "bo", "(W(σ( o(\\L b(\\G b)))( n(\\L o(\\G o)))))");
		checkSyllabifyWord("funglo", true, 2, "oncc.on", "fung.lo", "(W(σ( o(\\L f(\\G f)))( n(\\L u(\\G u)))( c(\\L n(\\G n)))( c(\\L g(\\G g))))(σ( o(\\L l(\\G l)))( n(\\L o(\\G o)))))");
		checkSyllabifyWord("fugh", true, 1, "oncc", "fugh", "(W(σ( o(\\L f(\\G f)))( n(\\L u(\\G u)))( c(\\L g(\\G g)))( c(\\L h(\\G h)))))");
		checkSyllabifyWord("flu", true, 1, "oon", "flu", "(W(σ( o(\\L f(\\G f)))( o(\\L l(\\G l)))( n(\\L u(\\G u)))))");
		checkSyllabifyWord("cat", false, 0, "", "", "(W)"); // no c segment

		useSecondRanking();
		checkSyllabifyWord("dapbek", false, 1, "ooooo", "dapbek", "(W(σ( o(\\L d(\\G d)))( o(\\L a(\\G a)))( o(\\L p(\\G p)))( o(\\L b(\\G b)))( o(\\L e(\\G e)))))");
	}

	protected void useSecondRanking() {
		OTConstraintRanking ranking2 = rankings.get(1);
		rankings.set(0, ranking2);
		ota.setOTConstraintRankings(FXCollections.observableArrayList(rankings));
		otSyllabifier = new OTSyllabifier(ota);
		otSyllabifier.setBundle(bundle);
	}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables,
			String expectedstructuralOptions, String expectedSyllabification, String expectedLTDescription) {
		boolean fSuccess = otSyllabifier.convertStringToSyllables(word);
		assertEquals(success, fSuccess);
		List<OTSyllable> syllablesInWord = otSyllabifier.getSyllablesInCurrentWord();
		assertEquals(numberOfSyllables, syllablesInWord.size());
		String joined = syllablesInWord.stream().map(OTSyllable::getStructuralOptionsInSyllable)
				.collect(Collectors.joining("."));
		assertEquals(expectedstructuralOptions, joined);
		assertEquals(expectedSyllabification, otSyllabifier.getSyllabificationOfCurrentWord());
		assertEquals(expectedLTDescription, otSyllabifier.getLingTreeDescriptionOfCurrentWord());

	}

	@Test
	public void traceSyllabifyWordTest() {
		String sInitialization = bundle.getString("report.tawotinitialization");
		String sHouseKeeping = bundle.getString("report.tawothousekeeping");
		String unparsedFailure = bundle.getString("report.tawotunparsedsegments");
		String ambiguousFailure = bundle.getString("report.tawotsomesegmentsareambiguous");
		String noConstraints = bundle.getString("report.tawotnoconstraints");
		otSyllabifier.setDoTrace(true);
		checkSyllabifyWord("Chiko", true, 2, "on.on", "Chi.ko", "(W(σ( o(\\L ch(\\G Ch)))( n(\\L i(\\G i))))(σ( o(\\L k(\\G k)))( n(\\L o(\\G o)))))");
		List<OTTracingStep> tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		OTTracingStep step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(sHouseKeeping, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(2);
		assertEquals("*Peak/C", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(3);
		assertEquals("*Margin/V", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_N_U);
		step = tracingSteps.get(4);
		assertEquals("Parse", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 3, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(5);
		assertEquals("*Complex/Onset", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 3, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(6);
		assertEquals("*Complex/Coda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 3, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(7);
		assertEquals("NoCoda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 3, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(8);
		assertTrue(step.isAddedAsSyllable());
		step = tracingSteps.get(9);
		assertTrue(step.isAddedAsSyllable());

		checkSyllabifyWord("dapbek", true, 2, "onc.onc", "dap.bek", "(W(σ( o(\\L d(\\G d)))( n(\\L a(\\G a)))( c(\\L p(\\G p))))(σ( o(\\L b(\\G b)))( n(\\L e(\\G e)))( c(\\L k(\\G k)))))");
		tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(sHouseKeeping, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(2);
		assertEquals("*Peak/C", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_C_U);
		step = tracingSteps.get(3);
		assertEquals("*Margin/V", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_C_U);
		step = tracingSteps.get(4);
		assertEquals("Parse", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.CODA);
		step = tracingSteps.get(5);
		assertEquals("*Complex/Onset", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.CODA);
		step = tracingSteps.get(6);
		assertEquals("*Complex/Coda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.CODA);
		step = tracingSteps.get(7);
		assertTrue(step.isAddedAsSyllable());
		step = tracingSteps.get(8);
		assertTrue(step.isAddedAsSyllable());

		checkSyllabifyWord("bampidon", true, 3, "onc.on.onc", "bam.pi.don", "(W(σ( o(\\L b(\\G b)))( n(\\L a(\\G a)))( c(\\L m(\\G m))))(σ( o(\\L p(\\G p)))( n(\\L i(\\G i))))(σ( o(\\L d(\\G d)))( n(\\L o(\\G o)))( c(\\L n(\\G n)))))");
		tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(11, tracingSteps.size());
		step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 6, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 7, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(sHouseKeeping, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 6, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 7, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(2);
		assertEquals("*Peak/C", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 6, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 7, OTStructuralOptions.COMBO_C_U);
		step = tracingSteps.get(3);
		assertEquals("*Margin/V", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 6, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 7, OTStructuralOptions.COMBO_C_U);
		step = tracingSteps.get(4);
		assertEquals("Parse", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 6, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 7, OTStructuralOptions.CODA);
		step = tracingSteps.get(5);
		assertEquals("*Complex/Onset", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 6, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 7, OTStructuralOptions.CODA);
		step = tracingSteps.get(6);
		assertEquals("*Complex/Coda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 6, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 7, OTStructuralOptions.CODA);
		step = tracingSteps.get(7);
		assertEquals("NoCoda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 4, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 5, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 6, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 7, OTStructuralOptions.CODA);
		step = tracingSteps.get(8);
		assertTrue(step.isAddedAsSyllable());
		step = tracingSteps.get(9);
		assertTrue(step.isAddedAsSyllable());
		step = tracingSteps.get(10);
		assertTrue(step.isAddedAsSyllable());

		checkSyllabifyWord("funglo", true, 2, "oncc.on", "fung.lo", "(W(σ( o(\\L f(\\G f)))( n(\\L u(\\G u)))( c(\\L n(\\G n)))( c(\\L g(\\G g))))(σ( o(\\L l(\\G l)))( n(\\L o(\\G o)))))");
		tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(sHouseKeeping, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(2);
		assertEquals("*Peak/C", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(3);
		assertEquals("*Margin/V", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_U);
		step = tracingSteps.get(4);
		assertEquals("Parse", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 5, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(5);
		assertEquals("*Complex/Onset", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_C);
		checkStructuralOptions(step, 5, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(6);
		assertEquals("*Complex/Coda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.NUCLEUS);
		checkStructuralOptions(step, 2, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 3, OTStructuralOptions.CODA);
		checkStructuralOptions(step, 4, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 5, OTStructuralOptions.NUCLEUS);
		step = tracingSteps.get(7);
		assertTrue(step.isAddedAsSyllable());
		step = tracingSteps.get(8);
		assertTrue(step.isAddedAsSyllable());

		useSecondRanking();
		otSyllabifier.setDoTrace(true);
		checkSyllabifyWord("dapbek", false, 1, "ooooo", "dapbek", "(W(σ( o(\\L d(\\G d)))( o(\\L a(\\G a)))( o(\\L p(\\G p)))( o(\\L b(\\G b)))( o(\\L e(\\G e)))))");
		tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(sHouseKeeping, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(2);
		assertEquals("NoCoda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_U);
		step = tracingSteps.get(3);
		assertEquals("*Peak/C", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.UNPARSED);
		step = tracingSteps.get(4);
		assertEquals("Onset1", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 1, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 2, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 3, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 4, OTStructuralOptions.ONSET);
		checkStructuralOptions(step, 5, OTStructuralOptions.UNPARSED);
		step = tracingSteps.get(5);
		assertEquals(unparsedFailure, step.getFailureMessage());

		// assuming we're using the second rankings
		while (rankings.get(0).getRanking().size() > 2) {
			rankings.get(0).getRanking().remove(2);
		}
		ota.setOTConstraintRankings(FXCollections.observableArrayList(rankings));
		otSyllabifier = new OTSyllabifier(ota);
		otSyllabifier.setBundle(bundle);
		otSyllabifier.setDoTrace(true);
		checkSyllabifyWord("dapbek", false, 0, "", "", "(W)");
		tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(sHouseKeeping, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_C_U);
		step = tracingSteps.get(2);
		assertEquals("NoCoda", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_N_U);
		step = tracingSteps.get(3);
		assertEquals("*Peak/C", step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.UNPARSED);
		step = tracingSteps.get(4);
		assertEquals(ambiguousFailure, step.getFailureMessage());

		// assuming we're using the second rankings
		rankings.clear();
		otSyllabifier = new OTSyllabifier(ota);
		ota.setOTConstraintRankings(FXCollections.observableArrayList(rankings));
		otSyllabifier.setBundle(bundle);
		otSyllabifier.setDoTrace(true);
		checkSyllabifyWord("dapbek", false, 0, "", "", "(W)");
		tracingSteps = otSyllabifier.getTracingSteps();
		assertEquals(2, tracingSteps.size());
		step = tracingSteps.get(0);
		assertEquals(sInitialization, step.getConstraintName());
		checkStructuralOptions(step, 0, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 1, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 2, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 3, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 4, OTStructuralOptions.COMBO_O_N_C_U);
		checkStructuralOptions(step, 5, OTStructuralOptions.COMBO_O_N_C_U);
		step = tracingSteps.get(1);
		assertEquals(noConstraints, step.getFailureMessage());
	}

	protected void checkStructuralOptions(OTTracingStep step, int iSegment, int iExpectedOptions) {
		OTSegmentInSyllable sis = step.getSegmentsInWord().get(iSegment);
		assertEquals(iExpectedOptions, sis.getStructuralOptions());
	}
}
