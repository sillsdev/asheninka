/**
 * Copyright (c) 2021 SIL International
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
import org.sil.syllableparser.model.moraicapproach.MoraicConstituent;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
import org.sil.syllableparser.model.moraicapproach.MoraicTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class MoraicSyllabifierWithFilterTest extends MoraicSyllabifierTestBase {

	ArrayList<MoraicSegmentInSyllable> segsInSyllable = new ArrayList<MoraicSegmentInSyllable>(
			Arrays.asList());
	MoraicSyllable syl = new MoraicSyllable(null);
	SHSonorityComparer sonorityComparer;
	@Before
	public void setUp() throws Exception {
		syl = new MoraicSyllable(segsInSyllable);
		this.projectFile = Constants.UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS;
		super.setUp();
		// turn off the nucleus template that allows only one vowel in a nucleus
		Optional<Template> nucSingleVowel = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("748b9c38-b42c-4f18-acdc-aed453d1c5f4")).findFirst();
		if (nucSingleVowel.isPresent()) {
			nucSingleVowel.get().setActive(false);
		}
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		sonorityComparer = new SHSonorityComparer(moraicApproach.getLanguageProject());
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
//		Nucleus nuc = new Nucleus();
//		doCheckFilterFail("", 0, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("a", 0, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("t", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("ta", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		MoraicSegmentInSyllable segInSyl = createMoraicSegmentInSyllable("a");
//		nuc.add(segInSyl);
//		doCheckFilterFail("a", 0, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("ta", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		segInSyl = createMoraicSegmentInSyllable("e");
//		nuc.add(segInSyl);
//		doCheckFilterFail("ae", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("tae", 2, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		segInSyl = createMoraicSegmentInSyllable("i");
//		nuc.add(segInSyl);
//		doCheckFilterFail("aei", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("aei", 2, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("taei", 2, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//		doCheckFilterFail("taei", 3, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED);
//
//		for (Filter f : failFilters) {
//			f.setTemplateFilterType(FilterType.CODA);
//		}
//		Coda coda = new Coda();
//		doCheckFilterFail("", 0, failFilters, coda, MoraicSyllabifierState.CODA, MoraicSyllabifierState.CODA,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED);
//		doCheckFilterFail("a", 0, failFilters, coda, MoraicSyllabifierState.CODA, MoraicSyllabifierState.CODA,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED);
//		segInSyl = createMoraicSegmentInSyllable("a");
//		coda.add(segInSyl);
//		doCheckFilterFail("a", 0, failFilters, coda, MoraicSyllabifierState.CODA, MoraicSyllabifierState.CODA,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED);
//		segInSyl = createMoraicSegmentInSyllable("e");
//		coda.add(segInSyl);
//		doCheckFilterFail("ae", 1, failFilters, coda, MoraicSyllabifierState.CODA, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED);
//		segInSyl = createMoraicSegmentInSyllable("i");
//		coda.add(segInSyl);
//		doCheckFilterFail("aei", 1, failFilters, coda, MoraicSyllabifierState.CODA, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED);
//		doCheckFilterFail("aei", 2, failFilters, coda, MoraicSyllabifierState.CODA, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.CODA_FILTER_FAILED);

	}

	public MoraicSegmentInSyllable createMoraicSegmentInSyllable(String segString) {
		Optional<Segment> optSegment = languageProject.getActiveSegmentsInInventory().stream()
				.filter(s -> s.getSegment().equals(segString)).findFirst();
		assertNotNull(optSegment);
		Segment seg = optSegment.get();
		assertNotNull(seg);
		MoraicSegmentInSyllable segInSyl = new MoraicSegmentInSyllable(seg, segString);
		return segInSyl;
	}

	public void doCheckFilterFail(String word, int iSegmentInWord, List<Filter> failFilters,
			MoraicConstituent constituent, MoraicSyllabifierState currentState, MoraicSyllabifierState expectedState,
			MoraicSyllabificationStatus tracingStatus) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		assertEquals(true, fSuccess);
		List<MoraicSegmentInSyllable> segmentsInWord = (List<MoraicSegmentInSyllable>) segmenter
				.getSegmentsInWord();
		constituent.setFailFilters(failFilters);
		MoraicSyllabifierState returnType = constituent.applyAnyFailFilters(segmentsInWord, iSegmentInWord,
				currentState, syl, tracingStatus, (LinkedList<MoraicSyllable>) muSyllabifier.getSyllablesInCurrentWord(), sonorityComparer, null);
		assertEquals(expectedState, returnType);
	}

	public void doCheckFilterFailWithTrace(String word, int iSegmentInWord,
			List<Filter> failFilters, MoraicConstituent constituent, MoraicSyllabifierState currentState,
			MoraicSyllabifierState expectedState, MoraicSyllabificationStatus status, int iTracingSteps) {
		muSyllabifier.getTracingSteps().clear();
		doCheckFilterFail(word, iSegmentInWord, failFilters, constituent, currentState,
				expectedState, status);
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(iTracingSteps, tracingSteps.size());
		if (iTracingSteps > 0) {
			MoraicTracingStep tracingStep = tracingSteps.get(0);
			assertEquals(status, tracingStep.getStatus());
		}
	}

	@Test
	public void checkFailFilterTraceTest() {
		muSyllabifier.setDoTrace(true);
		muSyllabifier.setTracingStep(new MoraicTracingStep());
		List<Filter> failFilters = (List<Filter>) languageProject
				.getActiveAndValidFilters()
				.stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS
						&& !f.getAction().isDoRepair()).collect(Collectors.toList());
//		Nucleus nuc = new Nucleus();
//		doCheckFilterFailWithTrace("", 0, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
//		doCheckFilterFailWithTrace("a", 0, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
//		doCheckFilterFailWithTrace("t", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
//		doCheckFilterFailWithTrace("ta", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
//		MoraicSegmentInSyllable segInSyl = createMoraicSegmentInSyllable("a");
//		nuc.add(segInSyl);
//		doCheckFilterFailWithTrace("a", 0, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
//		doCheckFilterFailWithTrace("ta", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.NUCLEUS,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
//		segInSyl = createMoraicSegmentInSyllable("e");
//		nuc.add(segInSyl);
//		doCheckFilterFailWithTrace("ae", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
//		doCheckFilterFailWithTrace("tae", 2, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
//		segInSyl = createMoraicSegmentInSyllable("i");
//		nuc.add(segInSyl);
//		doCheckFilterFailWithTrace("aei", 1, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
//		doCheckFilterFailWithTrace("aei", 2, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
//		doCheckFilterFailWithTrace("taei", 2, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
//		doCheckFilterFailWithTrace("taei", 3, failFilters, nuc, MoraicSyllabifierState.NUCLEUS, MoraicSyllabifierState.FILTER_FAILED,
//				MoraicSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
	}

	@Test
	public void filterFailTest() {
		assertEquals(4, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(3);
		assertEquals(false, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		checkSyllabification("sjat", false, 1, "sj", "σσ", "(W(σ(O(\\L s(\\G s))(\\L j(\\G j)))))");
		checkSyllabification("tatsjat", false, 2, "tat.sj", "σμc.σσ",
				"(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a))(\\L t(\\G t))))(σ(O(\\L s(\\G s))(\\L j(\\G j)))))");
	}

	@Test
	public void traceFilterFailTest() {
		muSyllabifier.setDoTrace(true);
		Filter f = languageProject.getActiveAndValidFilters().get(3);
		assertEquals(false, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		checkSyllabifyWord("sjat", false, 1, "sj", "σσ", "(W(σ(O(\\L s(\\G s))(\\L j(\\G j)))))");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "j", "Glides", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "j", "Glides", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_FAILED, false);
		assertEquals(f, tracingStep.getTemplateFilterUsed());
	}

	@Test
	public void onsetFilterRepairTest() {
		assertEquals(4, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(0);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());

		// codas allowed, onsets not required, no weight by position, /t/ is onset or coda, tl splits to t.l
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "μc.σμc.σμc",
				"(W(σ(μ(\\L æ(\\G æ))(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		checkSyllabification("tlæntɪk", true, 2, "tlæn.tɪk", "σσμc.σμc",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		checkSyllabification("æitlæintɪk", true, 3, "æit.læin.tɪk", "μμc.σμμc.σμc",
				"(W(σ(μ(\\L æ(\\G æ)))(μ(\\L i(\\G i))(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L i(\\G i))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		checkSyllabification("tlæintɪk", true, 2, "tlæin.tɪk", "σσμμc.σμc",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L i(\\G i))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");

		// codas allowed, onsets not required, use weight by position, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "μμ.σμμ.σμμ",
				"(W(σ(μ(\\L æ(\\G æ)))(μ(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ)))(μ(\\L k(\\G k)))))");
		checkSyllabification("tlæntɪk", true, 2, "tlæn.tɪk", "σσμμ.σμμ",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ)))(μ(\\L k(\\G k)))))");
		checkSyllabification("æitlæintɪk", true, 3, "æit.læin.tɪk", "μμc.σμμc.σμμ",
				"(W(σ(μ(\\L æ(\\G æ)))(μ(\\L i(\\G i))(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L i(\\G i))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ)))(μ(\\L k(\\G k)))))");
		checkSyllabification("tlæintɪk", true, 2, "tlæin.tɪk", "σσμμc.σμμ",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L i(\\G i))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ)))(μ(\\L k(\\G k)))))");

		// codas not allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("ætlæntɪk", false, 2, "æ.tlæ", "μ.σσμ",
				"(W(σ(μ(\\L æ(\\G æ))))(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ)))))");

		// codas allowed, all but first has onset, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "μμ.σμμ.σμμ",
				"(W(σ(μ(\\L æ(\\G æ)))(μ(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ)))(μ(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ)))(μ(\\L k(\\G k)))))");

		// codas allowed, all but first has onset, /t/ is onset or coda, t splits to t.
		f.getSlots().remove(1);
		checkSyllabification("ætæntɪk", false, 2, "æ.tænt", "μ.σμμc",
				"(W(σ(μ(\\L æ(\\G æ))))(σ(O(\\L t(\\G t)))(μ(\\L æ(\\G æ)))(μ(\\L n(\\G n))(\\L t(\\G t)))))");

		// codas allowed, onsets not required, /t/ is onset or coda, t splits to t.
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("ætæntɪk", true, 3, "æt.ænt.ɪk", "μμ.μμc.μμ",
				"(W(σ(μ(\\L æ(\\G æ)))(μ(\\L t(\\G t))))(σ(μ(\\L æ(\\G æ)))(μ(\\L n(\\G n))(\\L t(\\G t))))(σ(μ(\\L ɪ(\\G ɪ)))(μ(\\L k(\\G k)))))");
	}

	@Test
	public void traceOnsetFilterRepairTest() {
		assertEquals(4, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(0);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		muSyllabifier.setDoTrace(true);

		checkSyllabifyWord("tlæntɪk", true, 2, "tlæn.tɪk", "σσμc.σμc",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE,
				false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);

		// codas allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		checkSyllabifyWord("ætlæntɪk", true, 3, "æt.læn.tɪk", "μc.σμc.σμc",
				"(W(σ(μ(\\L æ(\\G æ))(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);

		// codas not allowed, onsets not required, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabifyWord("ætlæntɪk", false, 2, "æ.tlæ", "μ.σσμ",
				"(W(σ(μ(\\L æ(\\G æ))))(σ(O(\\L t(\\G t))(\\L l(\\G l)))(μ(\\L æ(\\G æ)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);

		// codas allowed, all but first has onset, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabifyWord("ætlæntɪk", true, 3, "æt.læn.tɪk", "μc.σμc.σμc",
				"(W(σ(μ(\\L æ(\\G æ))(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);

		// codas allowed, every syllable has onset, /t/ is onset or coda, tl splits to t.l
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabifyWord("tætlæntɪk", true, 3, "tæt.læn.tɪk", "σμc.σμc.σμc",
				"(W(σ(O(\\L t(\\G t)))(μ(\\L æ(\\G æ))(\\L t(\\G t))))(σ(O(\\L l(\\G l)))(μ(\\L æ(\\G æ))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(16, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET,
				true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);

		// codas allowed, all but first has onset, /t/ is onset or coda, t splits to t.
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		f.getSlots().remove(1);
		checkSyllabifyWord("ætæntɪk", false, 2, "æ.tænt", "μ.σμcc",
				"(W(σ(μ(\\L æ(\\G æ))))(σ(O(\\L t(\\G t)))(μ(\\L æ(\\G æ))(\\L n(\\G n))(\\L t(\\G t)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);

		// codas allowed, onsets not required, /t/ is onset or coda, t splits to t.
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabifyWord("ætæntɪk", true, 3, "æt.ænt.ɪk", "μc.μcc.μc",
				"(W(σ(μ(\\L æ(\\G æ))(\\L t(\\G t))))(σ(μ(\\L æ(\\G æ))(\\L n(\\G n))(\\L t(\\G t))))(σ(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruent"
				+ "s", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.ONSET_FILTER_REPAIR_APPLIED, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "æ", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
	}
}
