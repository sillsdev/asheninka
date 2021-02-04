/**
 * Copyright (c) 2021 SIL International
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
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class MoraicSyllabifierWithTemplateTest extends MoraicSyllabifierTestBase {

	ArrayList<MoraicSegmentInSyllable> segsInSyllable = new ArrayList<MoraicSegmentInSyllable>(
			Arrays.asList());
	MoraicSyllable syl = new MoraicSyllable(null);
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.service.parsing.ONCSyllabifierTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		syl = new MoraicSyllable(segsInSyllable);
		this.projectFile = Constants.UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS;
		super.setUp();
		// turn off the nucleus template that allows only one vowel in a nucleus
		Optional<Filter> nucTwoVowel = languageProject.getActiveAndValidFilters().stream()
				.filter(f -> f.getID().equals("4102eee1-30b1-4770-8e6f-ffb6e55e4b50")).findFirst();
		if (nucTwoVowel.isPresent()) {
			nucTwoVowel.get().setActive(false);
		}
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
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
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("sp", false, 0, "", "", "(W)");
		checkSyllabification("spat", true, 1, "spat", "σσμc",
				"(W(σ(\\L s(\\G s))(\\L p(\\G p))(μ(\\L a(\\G a))(\\L t(\\G t)))))");
		checkSyllabification("stap", true, 1, "stap", "σσμc",
				"(W(σ(\\L s(\\G s))(\\L t(\\G t))(μ(\\L a(\\G a))(\\L p(\\G p)))))");
		checkSyllabification("spɹeɪ", true, 1, "spɹeɪ", "σσσμμ",
				"(W(σ(\\L s(\\G s))(\\L p(\\G p))(\\L ɹ(\\G ɹ))(μ μ(\\L eɪ(\\G eɪ)))))");
		checkSyllabification("stɹap", true, 1, "stɹap", "σσσμc",
				"(W(σ(\\L s(\\G s))(\\L t(\\G t))(\\L ɹ(\\G ɹ))(μ(\\L a(\\G a))(\\L p(\\G p)))))");
		checkSyllabification("manstəɹ", true, 2, "man.stəɹ", "σμc.σσμc",
				"(W(σ(\\L m(\\G m))(μ(\\L a(\\G a))(\\L n(\\G n))))(σ(\\L s(\\G s))(\\L t(\\G t))(μ(\\L ə(\\G ə))(\\L ɹ(\\G ɹ)))))");
		checkSyllabification("smanstil", true, 2, "sman.stil", "σσμc.σσμc",
				"(W(σ(\\L s(\\G s))(\\L m(\\G m))(μ(\\L a(\\G a))(\\L n(\\G n))))(σ(\\L s(\\G s))(\\L t(\\G t))(μ(\\L i(\\G i))(\\L l(\\G l)))))");
		checkSyllabification("tɛkstjuɹ", true, 2, "tɛk.stjuɹ", "σμc.σσσμc",
				"(W(σ(\\L t(\\G t))(μ(\\L ɛ(\\G ɛ))(\\L k(\\G k))))(σ(\\L s(\\G s))(\\L t(\\G t))(\\L j(\\G j))(μ(\\L u(\\G u))(\\L ɹ(\\G ɹ)))))");
		}

	@Test
	public void syllableTemplateTest() {
		initSyllableTemplate();

		checkSyllabification("səm", true, 1, "səm", "σμc", "(W(σ(\\L s(\\G s))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		checkSyllabification("asəm", true, 2, "a.səm", "μ.σμc",
				"(W(σ(μ(\\L a(\\G a))))(σ(\\L s(\\G s))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		checkSyllabification("sləm", true, 1, "sləm", "σσμc", "(W(σ(\\L s(\\G s))(\\L l(\\G l))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		checkSyllabification("asləm", true, 2, "a.sləm", "μ.σσμc",
				"(W(σ(μ(\\L a(\\G a))))(σ(\\L s(\\G s))(\\L l(\\G l))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		checkSyllabification("slwəm", true, 1, "slwəm", "σσσμc",
				"(W(σ(\\L s(\\G s))(\\L l(\\G l))(\\L w(\\G w))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		checkSyllabification("aslwəm", true, 2, "a.slwəm", "μ.σσσμc",
				"(W(σ(μ(\\L a(\\G a))))(σ(\\L s(\\G s))(\\L l(\\G l))(\\L w(\\G w))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		checkSyllabification("moʊndlo", true, 2, "moʊn.dlo", "σμμc.σσμ",
				"(W(σ(\\L m(\\G m))(μ μ(\\L oʊ(\\G oʊ))(\\L n(\\G n))))(σ(\\L d(\\G d))(\\L l(\\G l))(μ(\\L o(\\G o)))))");
		checkSyllabification("dlomoʊn", true, 2, "dlo.moʊn", "σσμ.σμμc",
				"(W(σ(\\L d(\\G d))(\\L l(\\G l))(μ(\\L o(\\G o))))(σ(\\L m(\\G m))(μ μ(\\L oʊ(\\G oʊ))(\\L n(\\G n)))))");
		checkSyllabification("snlwəm", false, 0, "", "", "(W)");
		checkSyllabification("asnlwəm", false, 1, "a", "μ", "(W(σ(μ(\\L a(\\G a)))))");
		checkSyllabification("moʊst", false, 0, "", "", "(W)");
		checkSyllabification("ətmoʊst", false, 1, "ət", "μc", "(W(σ(μ(\\L ə(\\G ə))(\\L t(\\G t)))))");
	}

	protected void initSyllableTemplate() {
		// onsets not required, codas allowed, syllable pattern is ([C]) ([C]) ([C]) [V] ([N])
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		Optional<Template> syllableTemplate = languageProject.getTemplates().stream()
				.filter(t -> t.getID().equals("603e7989-7664-4bb6-840e-3ef2c2899805")).findFirst();
		assertTrue(syllableTemplate.isPresent());
		Template sylTemplate = syllableTemplate.get();
		assertEquals(TemplateType.SYLLABLE, sylTemplate.getTemplateFilterType());
		syllableTemplate.get().setActive(true);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
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
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("psoʊt", true, 1, "psoʊt", "aaμμc",
				"(W(A(\\L p(\\G p))(\\L s(\\G s)))(σ(μ μ(\\L oʊ(\\G oʊ))(\\L t(\\G t)))))");
		checkSyllabification("poʊt", true, 1, "poʊt", "σμμc",
				"(W(σ(\\L p(\\G p))(μ μ(\\L oʊ(\\G oʊ))(\\L t(\\G t)))))");
		checkSyllabification("snoʊt", true, 1, "snoʊt", "σσμμc",
				"(W(σ(\\L s(\\G s))(\\L n(\\G n))(μ μ(\\L oʊ(\\G oʊ))(\\L t(\\G t)))))");

		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("psoʊt", true, 1, "psoʊt", "aaμμc",
				"(W(A(\\L p(\\G p))(\\L s(\\G s)))(σ(μ μ(\\L oʊ(\\G oʊ))(\\L t(\\G t)))))");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "p", "Obstruents", "s", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "p", "Obstruents", null, null, null,
				MoraicSyllabificationStatus.ADDED_AS_WORD_INITIAL_APPENDIX, true);
		assertEquals(templateGuid, tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "s", "Obstruents", null, null, null,
				MoraicSyllabificationStatus.ADDED_AS_WORD_INITIAL_APPENDIX, true);
		assertEquals(templateGuid, tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_TWO_MORAS, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "t", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
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
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("moʊst", true, 1, "moʊst", "σμμca",
				"(W(σ(\\L m(\\G m))(μ μ(\\L oʊ(\\G oʊ))(\\L s(\\G s))))(A(\\L t(\\G t))))");
		checkSyllabification("ətmoʊst", true, 2, "ət.moʊst", "μc.σμμca",
				"(W(σ(μ(\\L ə(\\G ə))(\\L t(\\G t))))(σ(\\L m(\\G m))(μ μ(\\L oʊ(\\G oʊ))(\\L s(\\G s))))(A(\\L t(\\G t))))");
		checkSyllabification("sɪks", true, 1, "sɪks", "σμca",
				"(W(σ(\\L s(\\G s))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k))))(A(\\L s(\\G s))))");
		checkSyllabification("sɪksθ", true, 1, "sɪksθ", "σμcaa",
				"(W(σ(\\L s(\\G s))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k))))(A(\\L s(\\G s))(\\L θ(\\G θ))))");
		checkSyllabification("sɪksθs", true, 1, "sɪksθs", "σμcaaa",
				"(W(σ(\\L s(\\G s))(μ(\\L ɪ(\\G ɪ))(\\L k(\\G k))))(A(\\L s(\\G s))(\\L θ(\\G θ))(\\L s(\\G s))))");
		checkSyllabification("sɪlk", true, 1, "sɪlk", "σμcc", "(W(σ(\\L s(\\G s))(μ(\\L ɪ(\\G ɪ))(\\L l(\\G l))(\\L k(\\G k)))))");
		checkSyllabification("sɪlks", true, 1, "sɪlks", "σμcca",
				"(W(σ(\\L s(\\G s))(μ(\\L ɪ(\\G ɪ))(\\L l(\\G l))(\\L k(\\G k))))(A(\\L s(\\G s))))");
		checkSyllabification("gæsg", false, 1, "gæs", "σμc", "(W(σ(\\L g(\\G g))(μ(\\L æ(\\G æ))(\\L s(\\G s)))))");
		checkSyllabification("gæsp", true, 1, "gæsp", "σμca", "(W(σ(\\L g(\\G g))(μ(\\L æ(\\G æ))(\\L s(\\G s))))(A(\\L p(\\G p))))");
		checkSyllabification("moʊtdto", false, 1, "moʊt", "σμμc",
				"(W(σ(\\L m(\\G m))(μ μ(\\L oʊ(\\G oʊ))(\\L t(\\G t)))))");
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
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("moʊst", true, 1, "moʊst", "σμμca",
				"(W(σ(\\L m(\\G m))(μ μ(\\L oʊ(\\G oʊ))(\\L s(\\G s))))(A(\\L t(\\G t))))");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "m", "Nasals", "oʊ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "s", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "s", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_TWO_MORAS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "s", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_WORD_FINAL_APPENDIX, true);
		assertEquals("957dfbc1-511c-448a-b874-6c21abcb53d0", tracingStep.getTemplateFilterUsed().getID());
	}

	@Test
	public void traceOnsetTemplateTest() {
		// get the onset template which allows /s/ in an onset which violates the SSP
		Optional<Template> sInOnset = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("b364b517-5938-466e-b707-db5191821765")).findFirst();
		assertTrue(sInOnset.isPresent());
		Template onsetTemplate = sInOnset.get();
		assertEquals(TemplateType.ONSET, onsetTemplate.getTemplateFilterType());

		// onsets required, codas OK
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("spat", true, 1, "spat", "σσμc",
				"(W(σ(\\L s(\\G s))(\\L p(\\G p))(μ(\\L a(\\G a))(\\L t(\\G t)))))");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "p", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ONSET_TEMPLATE_MATCHED, true);
		assertEquals("b364b517-5938-466e-b707-db5191821765", tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "s", "Obstruents", "p", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "p", "Obstruents", null, null, null,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("tɛkstjuɹ", true, 2, "tɛk.stjuɹ", "σμc.σσσμc",
				"(W(σ(\\L t(\\G t))(μ(\\L ɛ(\\G ɛ))(\\L k(\\G k))))(σ(\\L s(\\G s))(\\L t(\\G t))(\\L j(\\G j))(μ(\\L u(\\G u))(\\L ɹ(\\G ɹ)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "ɛ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "ɛ", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "ɛ", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "k", "Obstruents", "s", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "s", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "s", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ONSET_TEMPLATE_MATCHED, true);
		assertEquals("b364b517-5938-466e-b707-db5191821765", tracingStep.getTemplateFilterUsed().getID());
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "s", "Obstruents", "t", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, null,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "j", "Glides", null, null, null,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "u", "Vowels", "ɹ", "Liquids", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "ɹ", "Liquids", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "ɹ", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

	}
	@Test
	public void traceSyllableTemplateTest() {
		initSyllableTemplate();
		muSyllabifier.setDoTrace(true);

		checkSyllabifyWord("səm", true, 1, "səm", "σμc", "(W(σ(\\L s(\\G s))(μ(\\L ə(\\G ə))(\\L m(\\G m)))))");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("səm", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "s", "Obstruents", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "ə", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "ə", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("səm", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "ə", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("səm", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep,  "m", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "m", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("sət", false, 0, "", "", "(W)");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("sə", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "s", "Obstruents", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "ə", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "ə", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("sə", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "ə", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATES_FAILED, false);

		checkSyllabifyWord("koʊmə", true, 2, "koʊ.mə", "σμμ.σμ",
				"(W(σ(\\L k(\\G k))(μ μ(\\L oʊ(\\G oʊ))))(σ(\\L m(\\G m))(μ(\\L ə(\\G ə)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "k", "Obstruents", "oʊ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("koʊm", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "k", "Obstruents", "oʊ", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("koʊm", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "oʊ", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_TWO_MORAS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", "Nasals", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "m", "Nasals", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("mə", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "m", "Nasals", "ə", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "ə", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "ə", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("mə", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "ə", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "ə", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("sni", true, 1, "sni", "σσμ",
				"(W(σ(\\L s(\\G s))(\\L n(\\G n))(μ(\\L i(\\G i)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("sni", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "s", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("sni", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("sni", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "i", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("snwi", true, 1, "snwi", "σσσμ",
				"(W(σ(\\L s(\\G s))(\\L n(\\G n))(\\L w(\\G w))(μ(\\L i(\\G i)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("snwi", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "s", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "w", "Glides", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("snwi", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "w", "Glides", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep,  "w", "Glides", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("snwi", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep,  "w", "Glides", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("snwi", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "i", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("snlwi", false, 0, "", "", "(W)");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(1, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "s", "Obstruents", "n", "Nasals", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATES_FAILED, false);

		checkSyllabifyWord("uɪti", true, 3, "u.ɪ.ti", "μ.μ.σμ",
				"(W(σ(μ(\\L u(\\G u))))(σ(μ(\\L ɪ(\\G ɪ))))(σ(\\L t(\\G t))(μ(\\L i(\\G i)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "u", "Vowels", "ɪ", "Vowels", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "ɪ", "Vowels", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("u", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "ɪ", "Vowels", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "ɪ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATES_FAILED, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "ɪ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "ɪ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "ɪ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
		MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("ɪ", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "ɪ", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "t", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "t", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("ti", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "t", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(12);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SYLLABLE_TEMPLATE_MATCHED, true);
		assertEquals("603e7989-7664-4bb6-840e-3ef2c2899805", tracingStep.getTemplateFilterUsed().getID());
		assertEquals("ti", tracingStep.getGraphemesInMatchedSyllableTemplate());
		tracingStep = tracingSteps.get(13);
		checkTracingStep(tracingStep, "i", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(14);
		checkTracingStep(tracingStep, "i", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
	}
}
