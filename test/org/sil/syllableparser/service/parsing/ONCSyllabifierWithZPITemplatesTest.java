/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class ONCSyllabifierWithZPITemplatesTest extends ONCSyllabifierTestBase {

	ArrayList<ONCSegmentInSyllable> segsInSyllable = new ArrayList<ONCSegmentInSyllable>(
			Arrays.asList());
	ONCSyllable syl = new ONCSyllable(null);
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.service.parsing.ONCSyllabifierTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		syl = new ONCSyllable(segsInSyllable);
		this.projectFile = "test/org/sil/syllableparser/testData/ZPI.ashedata";
		super.setUp();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.service.parsing.ONCSyllabifierTest#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void codaTemplateTest() {
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("gaxta", false, 1, "gaxt", "oncc",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L a(\\G a)))(C(\\L x(\\G x))(\\L t(\\G t))))))");
		disableCodaTemplateEnableWordFinalTemplate();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("gaxt", true, 1, "gaxt", "onca",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L a(\\G a)))(C(\\L x(\\G x)))))(A(\\L t(\\G t))))");
		}

	protected void disableCodaTemplateEnableWordFinalTemplate() {
		Optional<Template> doubleInCoda = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("27b9cbb3-11e6-4ac9-af23-55e0d46fe9bb")).findFirst();
		assertTrue(doubleInCoda.isPresent());
		Template codaTemplate = doubleInCoda.get();
		codaTemplate.setActive(false);
		assertEquals(TemplateType.CODA, codaTemplate.getTemplateFilterType());
		Optional<Template> wordFinal = languageProject.getTemplates().stream()
				.filter(t -> t.getID().equals("b80eac31-f1e8-452a-8170-b555d4a705b4")).findFirst();
		assertTrue(wordFinal.isPresent());
		Template wfTemplate = wordFinal.get();
		wfTemplate.setActive(true);
		assertEquals(TemplateType.WORDFINAL, wfTemplate.getTemplateFilterType());
	}

	@Test
	public void traceCodaTemplateTest() {
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabification("gaxta", false, 1, "gaxt", "oncc",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L a(\\G a)))(C(\\L x(\\G x))(\\L t(\\G t))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "g", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "x", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "x", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "x", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS_OR_CODA,
				ONCSyllabificationStatus.CODA_TEMPLATE_MATCHED, true);
		assertEquals("27b9cbb3-11e6-4ac9-af23-55e0d46fe9bb", tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "x", null, null, null, null, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", null, null, null, null, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.UNKNOWN,
				ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET, false);
		
		disableCodaTemplateEnableWordFinalTemplate();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("gaxt", true, 1, "gaxt", "onca",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L a(\\G a)))(C(\\L x(\\G x)))))(A(\\L t(\\G t))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "g", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "x", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "x", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "x", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, null, ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED,
				ONCSyllabificationStatus.ADDED_AS_WORD_FINAL_APPENDIX, true);
		assertEquals("b80eac31-f1e8-452a-8170-b555d4a705b4", tracingStep.getTemplateFilterUsed().getID());
	}
}
