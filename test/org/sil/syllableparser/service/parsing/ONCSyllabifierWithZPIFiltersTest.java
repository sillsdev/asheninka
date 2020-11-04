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
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class ONCSyllabifierWithZPIFiltersTest extends ONCSyllabifierTestBase {

	ArrayList<ONCSegmentInSyllable> segsInSyllable = new ArrayList<ONCSegmentInSyllable>(
			Arrays.asList());
	ONCSyllable syl = new ONCSyllable(null);
	SHSonorityComparer sonorityComparer;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.service.parsing.ONCSyllabifierTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		syl = new ONCSyllable(segsInSyllable);
		this.projectFile = "test/org/sil/syllableparser/testData/ZPIFilters.ashedata";
		super.setUp();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		sonorityComparer = new SHSonorityComparer(oncApproach.getLanguageProject());
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

	public ONCSegmentInSyllable createONCSegmentInSyllable(String segString) {
		Optional<Segment> optSegment = languageProject.getActiveSegmentsInInventory().stream()
				.filter(s -> s.getSegment().equals(segString)).findFirst();
		assertNotNull(optSegment);
		Segment seg = optSegment.get();
		assertNotNull(seg);
		ONCSegmentInSyllable segInSyl = new ONCSegmentInSyllable(seg, segString);
		return segInSyl;
	}

	@Test
	public void onsetFilterRepairTest() {
		assertEquals(6, languageProject.getFilters().size());
		Filter f = languageProject.getFilters().get(5);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		assertTrue(f.isActive());
		assertEquals("[Sonorant] _ *[Obs] | ([Sonorant]) *[Obs]", f.getTemplateFilterRepresentation());
		assertEquals("477f6740-f65c-4ee8-86d2-a09af0487794", f.getID());
		checkSyllabification("gyonnske", true, 2, "gyonns.ke", "onccc.on",
				"(W(σ(O(\\L gy(\\G gy)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))(\\L n(\\G n))(\\L s(\\G s)))))(σ(O(\\L k(\\G k)))(R(N(\\L e(\\G e))))))");
		checkSyllabification("xkwerpnzoon", true, 2, "xkwerp.nzoon", "ooncc.oonc",
				"(W(σ(O(\\L x(\\G x))(\\L kw(\\G kw)))(R(N(\\L e(\\G e)))(C(\\L r(\\G r))(\\L p(\\G p)))))(σ(O(\\L n(\\G n))(\\L z(\\G z)))(R(N(\\L oo(\\G oo)))(C(\\L n(\\G n))))))");
		checkSyllabification("dizfwerttee", true, 3, "diz.fwert.tee", "onc.ooncc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L i(\\G i)))(C(\\L z(\\G z)))))(σ(O(\\L f(\\G f))(\\L w(\\G w)))(R(N(\\L e(\\G e)))(C(\\L r(\\G r))(\\L t(\\G t)))))(σ(O(\\L t(\\G t)))(R(N(\\L ee(\\G ee))))))");
		checkSyllabification("gyelgwzeedet", true, 3, "gyelgw.zee.det", "oncc.on.onc",
				"(W(σ(O(\\L gy(\\G gy)))(R(N(\\L e(\\G e)))(C(\\L l(\\G l))(\\L gw(\\G gw)))))(σ(O(\\L z(\\G z)))(R(N(\\L ee(\\G ee)))))(σ(O(\\L d(\\G d)))(R(N(\\L e(\\G e)))(C(\\L t(\\G t))))))");
		checkSyllabification("xlërmanknaal", true, 3, "xlër.man.knaal", "oonc.onc.oonc",
				"(W(σ(O(\\L x(\\G x))(\\L l(\\G l)))(R(N(\\L ë(\\G ë)))(C(\\L r(\\G r)))))(σ(O(\\L m(\\G m)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L k(\\G k))(\\L n(\\G n)))(R(N(\\L aa(\\G aa)))(C(\\L l(\\G l))))))");

		disableNoVFilterEnableVFilter();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		
		f = languageProject.getFilters().get(1);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		assertEquals("[V] [Sonorant] _ *[Obs] | ([Sonorant]) *[Obs]", f.getTemplateFilterRepresentation());
		assertEquals("b6af8d7c-8d41-4d30-896b-6edd5ff078e3", f.getID());
		checkSyllabification("xkwerpnzoon", true, 2, "xkwerp.nzoon", "ooncc.oonc",
				"(W(σ(O(\\L x(\\G x))(\\L kw(\\G kw)))(R(N(\\L e(\\G e)))(C(\\L r(\\G r))(\\L p(\\G p)))))(σ(O(\\L n(\\G n))(\\L z(\\G z)))(R(N(\\L oo(\\G oo)))(C(\\L n(\\G n))))))");
		checkSyllabification("dizfwerttee", true, 3, "diz.fwert.tee", "onc.ooncc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L i(\\G i)))(C(\\L z(\\G z)))))(σ(O(\\L f(\\G f))(\\L w(\\G w)))(R(N(\\L e(\\G e)))(C(\\L r(\\G r))(\\L t(\\G t)))))(σ(O(\\L t(\\G t)))(R(N(\\L ee(\\G ee))))))");
		checkSyllabification("gyelgwzeedet", true, 3, "gyelgw.zee.det", "oncc.on.onc",
				"(W(σ(O(\\L gy(\\G gy)))(R(N(\\L e(\\G e)))(C(\\L l(\\G l))(\\L gw(\\G gw)))))(σ(O(\\L z(\\G z)))(R(N(\\L ee(\\G ee)))))(σ(O(\\L d(\\G d)))(R(N(\\L e(\\G e)))(C(\\L t(\\G t))))))");
		checkSyllabification("xlërmanknaal", true, 3, "xlër.man.knaal", "oonc.onc.oonc",
				"(W(σ(O(\\L x(\\G x))(\\L l(\\G l)))(R(N(\\L ë(\\G ë)))(C(\\L r(\\G r)))))(σ(O(\\L m(\\G m)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L k(\\G k))(\\L n(\\G n)))(R(N(\\L aa(\\G aa)))(C(\\L l(\\G l))))))");
	}
	
	protected void disableNoVFilterEnableVFilter() {
		Optional<Filter> noV = languageProject.getFilters().stream()
				.filter(f -> f.getID().equals("477f6740-f65c-4ee8-86d2-a09af0487794")).findFirst();
		assertTrue(noV.isPresent());
		Filter noVFilter = noV.get();
		noVFilter.setActive(false);
		assertEquals(FilterType.ONSET, noVFilter.getTemplateFilterType());
		Optional<Filter> v = languageProject.getFilters().stream()
				.filter(f -> f.getID().equals("b6af8d7c-8d41-4d30-896b-6edd5ff078e3")).findFirst();
		assertTrue(v.isPresent());
		Filter vFilter = v.get();
		vFilter.setActive(true);
		assertEquals(FilterType.ONSET, vFilter.getTemplateFilterType());
		assertEquals(1, languageProject.getActiveAndValidFilters().size());
		Optional<Filter> vActive = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getID().equals("b6af8d7c-8d41-4d30-896b-6edd5ff078e3")).findFirst();
		assertTrue(vActive.isPresent());
	}


	@Test
	public void traceOnsetFilterRepairTest() {
		assertEquals(6, languageProject.getFilters().size());
		Filter f = languageProject.getFilters().get(5);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		assertTrue(f.isActive());
		assertEquals("[Sonorant] _ *[Obs] | ([Sonorant]) *[Obs]", f.getTemplateFilterRepresentation());
		assertEquals("477f6740-f65c-4ee8-86d2-a09af0487794", f.getID());
		oncSyllabifier.setDoTrace(true);
		
		checkSyllabifyWord("gyonnske", true, 2, "gyonns.ke", "onccc.on",
				"(W(σ(O(\\L gy(\\G gy)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))(\\L n(\\G n))(\\L s(\\G s)))))(σ(O(\\L k(\\G k)))(R(N(\\L e(\\G e))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "gy", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED,
				true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS,
				true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "n", "Nasals", SHComparisonResult.EQUAL,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", "s", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ONSET_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "n", "Nasals", "s", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "s", "Obstruents", null, null, null,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.FILTER_REPAIR_APPLIED, ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		Filter fRepair = (Filter) tracingStep.getTemplateFilterUsed();
		assertEquals("477f6740-f65c-4ee8-86d2-a09af0487794", fRepair.getID());
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, null,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "e", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		disableNoVFilterEnableVFilter();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		
		f = languageProject.getActiveAndValidFilters().get(0);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		assertEquals("[V] [Sonorant] _ *[Obs] | ([Sonorant]) *[Obs]", f.getTemplateFilterRepresentation());
		assertEquals("b6af8d7c-8d41-4d30-896b-6edd5ff078e3", f.getID());
		assertTrue(f.isActive());

		checkSyllabifyWord("xkwerpnzoon", true, 2, "xkwerp.nzoon", "ooncc.oonc",
				"(W(σ(O(\\L x(\\G x))(\\L kw(\\G kw)))(R(N(\\L e(\\G e)))(C(\\L r(\\G r))(\\L p(\\G p)))))(σ(O(\\L n(\\G n))(\\L z(\\G z)))(R(N(\\L oo(\\G oo)))(C(\\L n(\\G n))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(16, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "x", "Obstruents", "kw", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ONSET_TEMPLATE_MATCHED,
				true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "x", "Obstruents", "kw", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET,
				true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "kw", "Obstruents", null, null, null, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "e", "Vowels", "r", "Liquid", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "e", "Vowels", "r", "Liquid", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "r", "Liquid", "p", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "p", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				ONCSyllabifierState.CODA_OR_ONSET, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "p", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.ONSET_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "p", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.FILTER_REPAIR_APPLIED, ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		fRepair = (Filter) tracingStep.getTemplateFilterUsed();
		assertEquals("b6af8d7c-8d41-4d30-896b-6edd5ff078e3", fRepair.getID());
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, null,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "z", "Obstruents", null, null, null,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(12);
		checkTracingStep(tracingStep, "oo", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(13);
		checkTracingStep(tracingStep, "oo", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(14);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(15);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

	}

}
