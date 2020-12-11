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
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
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
public class ONCSyllabifierWithTemplateTest extends ONCSyllabifierTestBase {

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
		Optional<Filter> nucTwoVowel = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getID().equals("4102eee1-30b1-4770-8e6f-ffb6e55e4b50")).findFirst();
		if (nucTwoVowel.isPresent()) {
			nucTwoVowel.get().setActive(false);
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
	public void onsetTemplateTest() {
		// get the nucleus template that allows only one vowel in a nucleus
		Optional<Template> sInOnset = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("b364b517-5938-466e-b707-db5191821765")).findFirst();
		assertTrue(sInOnset.isPresent());
		Template onsetTemplate = sInOnset.get();
		assertEquals(TemplateType.ONSET, onsetTemplate.getTemplateFilterType());

		// onsets required, codas OK
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("sp", false, 0, "", "", "(W)");
		checkSyllabification("spat", true, 1, "spat", "oonc",
				"(W(σ(O(\\L s(\\G s))(\\L p(\\G p)))(R(N(\\L a(\\G a)))(C(\\L t(\\G t))))))");
		checkSyllabification("stap", true, 1, "stap", "oonc",
				"(W(σ(O(\\L s(\\G s))(\\L t(\\G t)))(R(N(\\L a(\\G a)))(C(\\L p(\\G p))))))");
		checkSyllabification("spɹeɪ", true, 1, "spɹeɪ", "ooon",
				"(W(σ(O(\\L s(\\G s))(\\L p(\\G p))(\\L ɹ(\\G ɹ)))(R(N(\\L eɪ(\\G eɪ))))))");
		checkSyllabification("stɹap", true, 1, "stɹap", "ooonc",
				"(W(σ(O(\\L s(\\G s))(\\L t(\\G t))(\\L ɹ(\\G ɹ)))(R(N(\\L a(\\G a)))(C(\\L p(\\G p))))))");
		checkSyllabification("manstəɹ", true, 2, "man.stəɹ", "onc.oonc",
				"(W(σ(O(\\L m(\\G m)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L s(\\G s))(\\L t(\\G t)))(R(N(\\L ə(\\G ə)))(C(\\L ɹ(\\G ɹ))))))");
		checkSyllabification("smanstil", true, 2, "sman.stil", "oonc.oonc",
				"(W(σ(O(\\L s(\\G s))(\\L m(\\G m)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L s(\\G s))(\\L t(\\G t)))(R(N(\\L i(\\G i)))(C(\\L l(\\G l))))))");
		}

	@Test
	public void nucleusTemplateTest() {
		// get the nucleus template that allows only one vowel in a nucleus
		Optional<Template> nucSingleVowel = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("748b9c38-b42c-4f18-acdc-aed453d1c5f4")).findFirst();
		assertTrue(nucSingleVowel.isPresent());
		Template nucTemplate = nucSingleVowel.get(); 
		assertEquals(TemplateType.NUCLEUS, nucTemplate.getTemplateFilterType());

		// onsets not required, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ælæntɪk", true, 3, "æ.læn.tɪk", "n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("æalæntɪk", true, 4, "æ.a.læn.tɪk", "n.n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(R(N(\\L a(\\G a)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("ælæantɪk", true, 4, "æ.læ.an.tɪk", "n.on.nc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))))(σ(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("ælæntɪak", true, 4, "æ.læn.tɪ.ak", "n.onc.on.nc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))))(σ(R(N(\\L a(\\G a)))(C(\\L k(\\G k))))))");
		checkSyllabification("bitl̩", false, 1, "bi", "on",
				"(W(σ(O(\\L b(\\G b)))(R(N(\\L i(\\G i))))))");

		// all but first has onset, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("æatlæntɪk", false, 1, "æ", "n",
				"(W(σ(R(N(\\L æ(\\G æ))))))");
		checkSyllabification("ælæantɪk", false, 2, "æ.læ", "n.on",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ))))))");
		checkSyllabification("ælæntɪak", false, 3, "æ.læn.tɪ", "n.onc.on",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ))))))");

		// all but first has onset, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætlæntɪk", false, 0, "", "",
				"(W)");
		checkSyllabification("tæatlæntɪk", false, 1, "tæ", "on",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ))))))");
		checkSyllabification("tælæantɪk", false, 2, "tæ.læ", "on.on",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ))))))");
		checkSyllabification("tælæntɪak", false, 3, "tæ.læn.tɪ", "on.onc.on",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ))))))");

		// change template to allow for one to two vowels in the nucleus.
		TemplateFilterSlotSegmentOrNaturalClass slot1 = nucTemplate.getSlots().get(0);
		TemplateFilterSlotSegmentOrNaturalClass slot2 = new TemplateFilterSlotSegmentOrNaturalClass(slot1.getCVNaturalClass());	
		slot2.setOptional(true);
		nucTemplate.getSlots().add(slot2);

		// onsets not required, nucleus can be one or two vowels
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ælæntɪk", true, 3, "æ.læn.tɪk", "n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("æalæntɪk", true, 3, "æa.læn.tɪk", "nn.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ))(\\L a(\\G a)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("ælæantɪk", true, 3, "æ.læan.tɪk", "n.onnc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ))(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("ælæntɪak", true, 3, "æ.læn.tɪak", "n.onc.onnc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ))(\\L a(\\G a)))(C(\\L k(\\G k))))))");

		// all but first has onset, nucleus can be one or two vowels
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("æatlæntɪk", true, 3, "æat.læn.tɪk", "nnc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ))(\\L a(\\G a)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("ælæntɪk", true, 3, "æ.læn.tɪk", "n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("ælæntɪak", true, 3, "æ.læn.tɪak", "n.onc.onnc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ))(\\L a(\\G a)))(C(\\L k(\\G k))))))");

		// all but first has onset, nucleus can be one or two vowels
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("ætlæntɪk", false, 0, "", "",
				"(W)");
		checkSyllabification("tæatlæntɪk", true, 3, "tæat.læn.tɪk", "onnc.onc.onc",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ))(\\L a(\\G a)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("tælæantɪk", true, 3, "tæ.læan.tɪk", "on.onnc.onc",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ))(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("tælæntɪak", true, 3, "tæ.læn.tɪak", "on.onc.onnc",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ))(\\L a(\\G a)))(C(\\L k(\\G k))))))");
	}

	@Test
	public void traceNucleusTemplateTest() {
		// get the nucleus template that allows only one vowel in a nucleus
		Optional<Template> nucSingleVowel = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("748b9c38-b42c-4f18-acdc-aed453d1c5f4")).findFirst();
		assertTrue(nucSingleVowel.isPresent());
		Template nucTemplate = nucSingleVowel.get(); 
		assertEquals(TemplateType.NUCLEUS, nucTemplate.getTemplateFilterType());

		// onsets not required, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ælæntɪk", true, 3, "æ.læn.tɪk", "n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "l", "Liquids", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "æ", "Vowels", "l", "Liquids", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		// rest is tested elsewhere
		
		checkSyllabifyWord("æalæntɪk", true, 4, "æ.a.læn.tɪk", "n.n.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))))(σ(R(N(\\L a(\\G a)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(16, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_ANOTHER_NUCLEUS_CREATE_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "a", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "æ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);

		// all but first has onset, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("æalæntɪk", false, 1, "æ", "n",
				"(W(σ(R(N(\\L æ(\\G æ))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_NUCLEUS_ONSET_REQUIRED_BUT_WONT_BE_ONE, false);

		// every syllable has onset, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tæalæntɪk", false, 1, "tæ", "on",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L æ(\\G æ))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "æ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET,
				true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "æ", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_BLOCKS_ADDING_NUCLEUS_ONSET_REQUIRED_BUT_WONT_BE_ONE, false);
		
		// onsets not required, nucleus must have two vowels
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		// change template to require two vowels in the nucleus.
		TemplateFilterSlotSegmentOrNaturalClass slot1 = nucTemplate.getSlots().get(0);
		TemplateFilterSlotSegmentOrNaturalClass slot2 = new TemplateFilterSlotSegmentOrNaturalClass(slot1.getCVNaturalClass());	
		nucTemplate.getSlots().add(slot2);		
		checkSyllabifyWord("ælæntɪk", false, 0, "", "",
				"(W)");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(2, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "æ", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "æ", "Vowels", "l", "Liquids", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATES_ALL_FAIL, false);
	}

	@Test
	public void codaTemplateTest() {
		initializeCodaTest();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("moʊst", true, 1, "moʊst", "oncc",
				"(W(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L s(\\G s))(\\L t(\\G t))))))");
		checkSyllabification("ətmoʊst", true, 2, "ət.moʊst", "nc.oncc",
				"(W(σ(R(N(\\L ə(\\G ə)))(C(\\L t(\\G t)))))(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L s(\\G s))(\\L t(\\G t))))))");
		checkSyllabification("sɪks", false, 1, "sɪk", "onc",
				"(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("sɪlk", true, 1, "sɪlk", "oncc", "(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L l(\\G l))(\\L k(\\G k))))))");
		checkSyllabification("sɪlks", false, 1, "sɪlk", "oncc",
				"(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L l(\\G l))(\\L k(\\G k))))))");
		checkSyllabification("gæsp", false, 1, "gæs", "onc", "(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s))))))");
		checkSyllabification("gæspo", true, 2, "gæs.po", "onc.on",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s)))))(σ(O(\\L p(\\G p)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("gæsg", false, 1, "gæs", "onc", "(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s))))))");
		checkSyllabification("gæstmog", true, 2, "gæst.mog", "oncc.onc",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s))(\\L t(\\G t)))))(σ(O(\\L m(\\G m)))(R(N(\\L o(\\G o)))(C(\\L g(\\G g))))))");
		checkSyllabification("gætstmog", true, 2, "gætst.mog", "onccc.onc",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t))(\\L s(\\G s))(\\L t(\\G t)))))(σ(O(\\L m(\\G m)))(R(N(\\L o(\\G o)))(C(\\L g(\\G g))))))");
		checkSyllabification("gæstog", true, 2, "gæst.og", "oncc.nc",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s))(\\L t(\\G t)))))(σ(R(N(\\L o(\\G o)))(C(\\L g(\\G g))))))");
		checkSyllabification("gætstog", true, 2, "gætst.og", "onccc.nc",
				"(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t))(\\L s(\\G s))(\\L t(\\G t)))))(σ(R(N(\\L o(\\G o)))(C(\\L g(\\G g))))))");
		}

	public void initializeCodaTest() {
		// get the nucleus template that allows only one vowel in a nucleus
		Optional<Template> sInCoda = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("957dfbc1-511c-448a-b874-6c21abcb53d0")).findFirst();
		assertTrue(sInCoda.isPresent());
		Template codaTemplate = sInCoda.get();
		assertEquals(TemplateType.WORDFINAL, codaTemplate.getTemplateFilterType());
		codaTemplate.setTemplateFilterType(TemplateType.CODA);
		assertEquals(TemplateType.CODA, codaTemplate.getTemplateFilterType());

		Optional<Template> wordFinalTemplate = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("f49276fd-27fc-49be-86a7-7dc93a96e813")).findFirst();
		assertTrue(wordFinalTemplate.isPresent());
		Template wfTemplate = wordFinalTemplate.get();
		assertEquals(TemplateType.WORDFINAL, wfTemplate.getTemplateFilterType());
		wfTemplate.setActive(false);

		// onsets not required, codas OK
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
	}

	@Test
	public void wordInitialTemplateTest() {
		String templateGuid = "f49276fd-27fc-49be-86a7-7dc93a96e813";
		// get the word final template
		Optional<Template> wordInitialTemplate = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals(templateGuid)).findFirst();
		assertTrue(wordInitialTemplate.isPresent());
		Template wiTemplate = wordInitialTemplate.get();
		assertEquals(TemplateType.WORDFINAL, wiTemplate.getTemplateFilterType());
		wiTemplate.setTemplateFilterType(TemplateType.WORDINITIAL);

		// onsets not required, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("psoʊt", true, 1, "psoʊt", "aanc",
				"(W(A(\\L p(\\G p))(\\L s(\\G s)))(σ(R(N(\\L oʊ(\\G oʊ)))(C(\\L t(\\G t))))))");
		checkSyllabification("poʊt", true, 1, "poʊt", "onc",
				"(W(σ(O(\\L p(\\G p)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L t(\\G t))))))");
		checkSyllabification("snoʊt", true, 1, "snoʊt", "oonc",
				"(W(σ(O(\\L s(\\G s))(\\L n(\\G n)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L t(\\G t))))))");

		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("psoʊt", true, 1, "psoʊt", "aanc",
				"(W(A(\\L p(\\G p))(\\L s(\\G s)))(σ(R(N(\\L oʊ(\\G oʊ)))(C(\\L t(\\G t))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "p", "Obstruents", "s", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "p", "Obstruents", "s", "Obstruents", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "p", "Obstruents", null, null, null,
				ONCSyllabifierState.WORD_INITIAL_TEMPLATE_APPLIED, ONCSyllabificationStatus.ADDED_AS_WORD_INITIAL_APPENDIX, true);
		assertEquals(templateGuid, tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "s", "Obstruents", null, null, null, ONCSyllabifierState.WORD_INITIAL_TEMPLATE_APPLIED,
				ONCSyllabificationStatus.ADDED_AS_WORD_INITIAL_APPENDIX, true);
		assertEquals(templateGuid, tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.UNKNOWN,
				ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "t", null, null, null, null, ONCSyllabifierState.UNKNOWN,
				ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
		// restore template to be word final
		wiTemplate.setTemplateFilterType(TemplateType.WORDFINAL);
	}

	@Test
	public void wordFinalTemplateTest() {
		// get the word final template
		Optional<Template> wordFinalTemplate = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("957dfbc1-511c-448a-b874-6c21abcb53d0")).findFirst();
		assertTrue(wordFinalTemplate.isPresent());
		Template wfTemplate = wordFinalTemplate.get();
		assertEquals(TemplateType.WORDFINAL, wfTemplate.getTemplateFilterType());

		// onsets not required, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("moʊst", true, 1, "moʊst", "onca",
				"(W(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L s(\\G s)))))(A(\\L t(\\G t))))");
		checkSyllabification("ətmoʊst", true, 2, "ət.moʊst", "nc.onca",
				"(W(σ(R(N(\\L ə(\\G ə)))(C(\\L t(\\G t)))))(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L s(\\G s)))))(A(\\L t(\\G t))))");
		checkSyllabification("sɪks", true, 1, "sɪks", "onca",
				"(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k)))))(A(\\L s(\\G s))))");
		checkSyllabification("sɪksθ", true, 1, "sɪksθ", "oncaa",
				"(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k)))))(A(\\L s(\\G s))(\\L θ(\\G θ))))");
		checkSyllabification("sɪksθs", true, 1, "sɪksθs", "oncaaa",
				"(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k)))))(A(\\L s(\\G s))(\\L θ(\\G θ))(\\L s(\\G s))))");
		checkSyllabification("sɪlk", true, 1, "sɪlk", "oncc", "(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L l(\\G l))(\\L k(\\G k))))))");
		checkSyllabification("sɪlks", true, 1, "sɪlks", "oncca",
				"(W(σ(O(\\L s(\\G s)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L l(\\G l))(\\L k(\\G k)))))(A(\\L s(\\G s))))");
		checkSyllabification("gæsg", false, 1, "gæs", "onc", "(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s))))))");
		checkSyllabification("gæsp", true, 1, "gæsp", "onca", "(W(σ(O(\\L g(\\G g)))(R(N(\\L æ(\\G æ)))(C(\\L s(\\G s)))))(A(\\L p(\\G p))))");
		checkSyllabification("moʊtdto", false, 1, "moʊt", "onc",
				"(W(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L t(\\G t))))))");
	}

	@Test
	public void traceWordFinalTemplateTest() {
		// get the word final template
		Optional<Template> wordFinalTemplate = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("957dfbc1-511c-448a-b874-6c21abcb53d0")).findFirst();
		assertTrue(wordFinalTemplate.isPresent());
		Template wfTemplate = wordFinalTemplate.get();
		assertEquals(TemplateType.WORDFINAL, wfTemplate.getTemplateFilterType());

		// onsets not required, nucleus can be only one vowel
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("moʊst", true, 1, "moʊst", "onca",
				"(W(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L s(\\G s)))))(A(\\L t(\\G t))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "m", "Nasals", "oʊ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "s", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "s", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "s", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, null, ONCSyllabifierState.WORD_FINAL_TEMPLATE_APPLIED,
				ONCSyllabificationStatus.ADDED_AS_WORD_FINAL_APPENDIX, true);
		assertEquals("957dfbc1-511c-448a-b874-6c21abcb53d0", tracingStep.getTemplateFilterUsed().getID());
	}

	@Test
	public void traceOnsetTemplateTest() {
		// get the nucleus template that allows only one vowel in a nucleus
		Optional<Template> sInOnset = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("b364b517-5938-466e-b707-db5191821765")).findFirst();
		assertTrue(sInOnset.isPresent());
		Template onsetTemplate = sInOnset.get();
		assertEquals(TemplateType.ONSET, onsetTemplate.getTemplateFilterType());

		// onsets required, codas OK
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("spat", true, 1, "spat", "oonc",
				"(W(σ(O(\\L s(\\G s))(\\L p(\\G p)))(R(N(\\L a(\\G a)))(C(\\L t(\\G t))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(7, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "p", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ONSET_TEMPLATE_MATCHED, true);
		assertEquals("b364b517-5938-466e-b707-db5191821765", tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "s", "Obstruents", "p", "Obstruents", SHComparisonResult.EQUAL, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "p", "Obstruents", null, null, null, ONCSyllabifierState.ONSET,
				ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", "Vowels", "t", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "a", "Vowels", "t", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "t", null, null, null, null, ONCSyllabifierState.UNKNOWN,
				ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
	}

	@Test
	public void traceCodaTemplateTest() {
		initializeCodaTest();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("moʊst", true, 1, "moʊst", "oncc",
				"(W(σ(O(\\L m(\\G m)))(R(N(\\L oʊ(\\G oʊ)))(C(\\L s(\\G s))(\\L t(\\G t))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "m", "Nasals", "oʊ", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "s", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		assertEquals("748b9c38-b42c-4f18-acdc-aed453d1c5f4", tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "s", "Obstruents", SHComparisonResult.MORE, ONCSyllabifierState.NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "s", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL, ONCSyllabifierState.NUCLEUS_OR_CODA,
				ONCSyllabificationStatus.CODA_TEMPLATE_MATCHED, true);
		assertEquals("957dfbc1-511c-448a-b874-6c21abcb53d0", tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "s", null, null, null, null, ONCSyllabifierState.CODA,
				ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", null, null, null, null, ONCSyllabifierState.ONSET_OR_NUCLEUS,
				ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
	}
}
