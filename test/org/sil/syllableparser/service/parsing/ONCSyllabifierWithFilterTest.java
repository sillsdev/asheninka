/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.oncapproach.Coda;
import org.sil.syllableparser.model.oncapproach.Nucleus;
import org.sil.syllableparser.model.oncapproach.ONCConstituent;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class ONCSyllabifierWithFilterTest extends ONCSyllabifierTestBase {

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
		this.projectFile = Constants.UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS;
		super.setUp();
		// turn off the nucleus template that allows only one vowel in a nucleus
		Optional<Template> nucSingleVowel = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("748b9c38-b42c-4f18-acdc-aed453d1c5f4")).findFirst();
		if (nucSingleVowel.isPresent()) {
			nucSingleVowel.get().setActive(false);
		}
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
	public void checkFailFilterTest() {
		List<Filter> failFilters = (List<Filter>) languageProject
				.getActiveAndValidFilters()
				.stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS
						&& !f.getAction().isDoRepair()).collect(Collectors.toList());
		Nucleus nuc = new Nucleus();
		doCheckFilterFail("", 0, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("a", 0, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("t", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("ta", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		ONCSegmentInSyllable segInSyl = createONCSegmentInSyllable("a");
		nuc.add(segInSyl);
		doCheckFilterFail("a", 0, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("ta", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("e");
		nuc.add(segInSyl);
		doCheckFilterFail("ae", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("tae", 2, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("i");
		nuc.add(segInSyl);
		doCheckFilterFail("aei", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("aei", 2, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("taei", 2, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("taei", 3, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);

		for (Filter f : failFilters) {
			f.setTemplateFilterType(FilterType.CODA);
		}
		Coda coda = new Coda();
		doCheckFilterFail("", 0, failFilters, coda, ONCSyllabifierState.CODA, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		doCheckFilterFail("a", 0, failFilters, coda, ONCSyllabifierState.CODA, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("a");
		coda.add(segInSyl);
		doCheckFilterFail("a", 0, failFilters, coda, ONCSyllabifierState.CODA, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("e");
		coda.add(segInSyl);
		doCheckFilterFail("ae", 1, failFilters, coda, ONCSyllabifierState.CODA, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("i");
		coda.add(segInSyl);
		doCheckFilterFail("aei", 1, failFilters, coda, ONCSyllabifierState.CODA, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		doCheckFilterFail("aei", 2, failFilters, coda, ONCSyllabifierState.CODA, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);

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

	public void doCheckFilterFail(String word, int iSegmentInWord, List<Filter> failFilters,
			ONCConstituent constituent, ONCSyllabifierState currentState, ONCSyllabifierState expectedState,
			ONCSyllabificationStatus tracingStatus) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		assertEquals(true, fSuccess);
		List<ONCSegmentInSyllable> segmentsInWord = (List<ONCSegmentInSyllable>) segmenter
				.getSegmentsInWord();
		constituent.setFailFilters(failFilters);
		ONCSyllabifierState returnType = constituent.applyAnyFailFilters(segmentsInWord, iSegmentInWord,
				currentState, syl, tracingStatus, (LinkedList<ONCSyllable>) oncSyllabifier.getSyllablesInCurrentWord());
		assertEquals(expectedState, returnType);
	}

	public void doCheckFilterFailWithTrace(String word, int iSegmentInWord,
			List<Filter> failFilters, ONCConstituent constituent, ONCSyllabifierState currentState,
			ONCSyllabifierState expectedState, ONCSyllabificationStatus status, int iTracingSteps) {
		oncSyllabifier.getTracingSteps().clear();
		doCheckFilterFail(word, iSegmentInWord, failFilters, constituent, currentState,
				expectedState, status);
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(iTracingSteps, tracingSteps.size());
		if (iTracingSteps > 0) {
			ONCTracingStep tracingStep = tracingSteps.get(0);
			assertEquals(status, tracingStep.getStatus());
		}
	}

	@Test
	public void checkFailFilterTraceTest() {
		oncSyllabifier.setDoTrace(true);
		oncSyllabifier.setTracingStep(new ONCTracingStep());
		List<Filter> failFilters = (List<Filter>) languageProject
				.getActiveAndValidFilters()
				.stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS
						&& !f.getAction().isDoRepair()).collect(Collectors.toList());
		Nucleus nuc = new Nucleus();
		doCheckFilterFailWithTrace("", 0, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("a", 0, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("t", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("ta", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		ONCSegmentInSyllable segInSyl = createONCSegmentInSyllable("a");
		nuc.add(segInSyl);
		doCheckFilterFailWithTrace("a", 0, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("ta", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		segInSyl = createONCSegmentInSyllable("e");
		nuc.add(segInSyl);
		doCheckFilterFailWithTrace("ae", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("tae", 2, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		segInSyl = createONCSegmentInSyllable("i");
		nuc.add(segInSyl);
		doCheckFilterFailWithTrace("aei", 1, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("aei", 2, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("taei", 2, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("taei", 3, failFilters, nuc, ONCSyllabifierState.NUCLEUS, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
	}

	@Test
	public void filterFailTest() {
		assertEquals(3, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(1);
		assertEquals(false, f.getAction().isDoRepair());
		assertEquals(FilterType.NUCLEUS, f.getTemplateFilterType());
		checkSyllabification("eɪoɹtə", false, 1, "eɪo", "nn",
				"(W(σ(R(N(\\L eɪ(\\G eɪ))(\\L o(\\G o))))))");
	}

	@Test
	public void traceFilterFailTest() {
		oncSyllabifier.setDoTrace(true);
		Filter f = languageProject.getActiveAndValidFilters().get(1);
		assertEquals(false, f.getAction().isDoRepair());
		assertEquals(FilterType.NUCLEUS, f.getTemplateFilterType());
		checkSyllabifyWord("eɪoɹtə", false, 1, "eɪo", "nn",
				"(W(σ(R(N(\\L eɪ(\\G eɪ))(\\L o(\\G o))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "eɪ", "Vowels", "o", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "eɪ", "Vowels", "o", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "ɹ", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, false);
		assertEquals(f, tracingStep.getTemplateFilterUsed());
	}

	@Test
	public void onsetFilterRepairTest() {
		assertEquals(3, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(0);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());

		// codas allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("tlæntɪk", true, 2, "tlæn.tɪk", "oonc.onc",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");

		// codas not allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætlæntɪk", false, 2, "æ.tlæ", "n.oon",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L t(\\G t))(\\L l(\\G l)))(R(N(\\L æ(\\G æ))))))");

		// codas allowed, all but first has onset, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");

		// codas allowed, all but first has onset, /t/ is onset or coda, t splits to t.
		f.getSlots().remove(1);
		checkSyllabification("ætæntɪk", true, 3, "æ.tæn.tɪk", "n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");

		// codas allowed, onsets not required, /t/ is onset or coda, t splits to t.
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætæntɪk", true, 3, "æt.ænt.ɪk", "nc.ncc.nc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n))(\\L t(\\G t)))))(σ(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
	}

	@Test
	public void traceOnsetFilterRepairTest() {
		assertEquals(3, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(0);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		oncSyllabifier.setDoTrace(true);

		checkSyllabifyWord("tlæntɪk", true, 2, "tlæn.tɪk", "oonc.onc",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET,
				true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.FILTER_FAILED, ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);

		// codas allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		checkSyllabifyWord("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);

		// codas not allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabifyWord("ætlæntɪk", false, 2, "æ.tlæ", "n.oon",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L t(\\G t))(\\L l(\\G l)))(R(N(\\L æ(\\G æ))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);

		// codas allowed, all but first has onset, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabifyWord("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);

		// codas allowed, every syllable has onset, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabifyWord("tætlæntɪk", true, 3, "tæt.læn.tɪk", "onc.onc.onc",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET,
				true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);

		// codas allowed, all but first has onset, /t/ is onset or coda, t splits to t.
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		f.getSlots().remove(1);
		checkSyllabifyWord("ætæntɪk", true, 3, "æ.tæn.tɪk", "n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_FAILED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);

		// codas allowed, onsets not required, /t/ is onset or coda, t splits to t.
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabifyWord("ætæntɪk", true, 3, "æt.ænt.ɪk", "nc.ncc.nc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n))(\\L t(\\G t)))))(σ(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruent"
				+ "s", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCSyllabifierState.FILTER_REPAIR_APPLIED,
				ONCSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
	}
}
