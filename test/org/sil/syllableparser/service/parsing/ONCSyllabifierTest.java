// Copyright (c) 2019 SIL International 
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
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class ONCSyllabifierTest {

	ONCApproach oncApproach;
	ObservableList<SHNaturalClass> naturalClasses;
	ONCSegmenter segmenter;
	ObservableList<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	List<SHNaturalClass> shNaturalClasses;
	ONCSyllabifier oncSyllabifier;
	LanguageProject languageProject;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new ONCSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		oncApproach = languageProject.getONCApproach();
		shNaturalClasses = oncApproach.getActiveSHNaturalClasses();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncSyllabifier = new ONCSyllabifier(oncApproach);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", true, 1, "A", "n");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc");
		checkSyllabification("dankgem", true, 2, "dan.kgem", "onc.oonc");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fun", "onc");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", true, 1, "iae", "nnn");
		checkSyllabification("ibabe", false, 1, "i", "n"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", false, 0, "", "");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc");
		checkSyllabification("dankgem", true, 2, "dan.kgem", "onc.oonc");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fun", "onc");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", false, 0, "", "");
		checkSyllabification("ibabe", false, 0, "", ""); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", true, 1, "A", "n");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc");
		checkSyllabification("dankgem", true, 2, "dan.kgem", "onc.oonc");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fun", "onc");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", true, 1, "iae", "nnn");
		checkSyllabification("ibabe", false, 1, "i", "n"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", true, 1, "A", "n");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc");
		checkSyllabification("dankgem", true, 2, "dan.kgem", "onc.oonc");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fun", "onc");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", true, 1, "iae", "nnn");
		checkSyllabification("ibabe", false, 1, "i", "n"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "onc.on");
		checkSyllabification("donlyi", true, 2, "don.lyi", "onc.oon");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", false, 0, "", "");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc");
		checkSyllabification("dankgem", true, 2, "dan.kgem", "onc.oonc");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fun", "onc");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", false, 0, "", "");
		checkSyllabification("ibabe", false, 0, "", ""); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "onc.on");
		checkSyllabification("donlyi", true, 2, "don.lyi", "onc.oon");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", true, 1, "A", "n");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc");
		checkSyllabification("dankgem", true, 2, "dan.kgem", "onc.oonc");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fun", "onc");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon");
		checkSyllabification("funi", true, 2, "fun.i", "onc.n");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", true, 1, "iae", "nnn");
		checkSyllabification("ibabe", false, 1, "i", "n"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "onc.on");
		checkSyllabification("donlyi", true, 2, "don.lyi", "onc.oon");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", true, 1, "A", "n");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", false, 1, "da", "on");
		checkSyllabification("dankgem", false, 1, "da", "on");
		checkSyllabification("dampidon", false, 1, "da", "on");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fu", "on");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", false, 1, "fu", "on");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", true, 1, "iae", "nnn");
		checkSyllabification("ibabe", false, 1, "i", "n"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon");
		checkSyllabification("donni", true, 2, "do.nni", "on.oon");
		checkSyllabification("donnli", true, 2, "do.nnli", "on.ooon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", false, 0, "", "");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", false, 1, "da", "on");
		checkSyllabification("dankgem", false, 1, "da", "on");
		checkSyllabification("dampidon", false, 1, "da", "on");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fu", "on");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", false, 1, "fu", "on");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", false, 0, "", "");
		checkSyllabification("ibabe", false, 0, "", ""); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon");
		checkSyllabification("donni", true, 2, "do.nni", "on.oon");
		checkSyllabification("donnli", true, 2, "do.nnli", "on.ooon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "");
		checkSyllabification("b", false, 0, "", "");
		checkSyllabification("A", true, 1, "A", "n");
		checkSyllabification("ta", true, 1, "ta", "on");
		checkSyllabification("tad", false, 1, "ta", "on");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on");
		checkSyllabification("dangem", false, 1, "da", "on");
		checkSyllabification("dankgem", false, 1, "da", "on");
		checkSyllabification("dampidon", false, 1, "da", "on");
		checkSyllabification("dovdek", false, 2, "do.vde", "on.oon");
		checkSyllabification("fuhgt", false, 1, "fu", "on");
		checkSyllabification("fungt", false, 1, "fu", "on");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "oon.on");
		checkSyllabification("do", true, 1, "do", "on");
		checkSyllabification("funglo", false, 1, "fu", "on");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on");
		checkSyllabification("flu", true, 1, "flu", "oon");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on");
		checkSyllabification("iae", true, 1, "iae", "nnn");
		checkSyllabification("ibabe", false, 1, "i", "n"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon");
		checkSyllabification("donni", true, 2, "do.nni", "on.oon");
		checkSyllabification("donnli", true, 2, "do.nnli", "on.ooon");
		checkSyllabification("dolnti", false, 1, "do", "on");
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedSyllabification, String expectedONCPattern) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<ONCSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		fSuccess = oncSyllabifier.syllabify(segmentsInWord);
		assertEquals("word syllabified", success, fSuccess);
		List<ONCSyllable> syllablesInWord = oncSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				oncSyllabifier.getSyllabificationOfCurrentWord());
		assertEquals("ONC Pattern of word", expectedONCPattern,
				oncSyllabifier.getONCPatternOfCurrentWord());
	}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables,
			String expectedSyllabification, String expectedONCPattern) {
		boolean fSuccess = oncSyllabifier.convertStringToSyllables(word);
		assertEquals("word syllabified", success, fSuccess);
		List<ONCSyllable> syllablesInWord = oncSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				oncSyllabifier.getSyllabificationOfCurrentWord());
		assertEquals("Expected ONC pattern", expectedONCPattern,
				oncSyllabifier.getONCPatternOfCurrentWord());
	}

	@Test
	public void traceSyllabifyWordTest() {
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("", false, 0, "", "");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(0, tracingSteps.size());

		checkSyllabifyWord("A", true, 1, "A", "n");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("ta", true, 1, "ta", "on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("tan", true, 1, "tan", "onc");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);

		checkSyllabifyWord("Chiko", true, 2, "Chi.ko", "on.on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "ch", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "onc.on.onc");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "m", "Nasals", "p", "Obstruents", SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);

		checkSyllabifyWord("dapgek", false, 2, "da.pge", "on.oon");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		checkSyllabifyWord("doldek", false, 1, "do", "on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		checkSyllabifyWord("dlofun", true, 2, "dlo.fun", "oon.onc");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(7, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "f", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);

		checkSyllabifyWord("funmo", true, 2, "fun.mo", "onc.on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(7, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmlo", true, 2, "fun.mlo", "onc.oon");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmdo", true, 2, "funm.do", "oncc.on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("flu", true, 1, "flu", "oon");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "u", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("flum", true, 1, "flum", "oonc");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);

		checkSyllabifyWord("iae", true, 1, "iae", "nnn");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "e", "Vowels", SHComparisonResult.EQUAL,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "e", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("babe", false, 0, "", ""); // b not in hierarchy
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(1, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "b", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);

		checkSyllabifyWord("ibabe", false, 1, "i", "n"); // b not in hierarchy
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				ONCType.UNKNOWN, ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);
		
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tan", false, 1, "ta", "on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("a", false, 0, "", "");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(1, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET, false);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tanlo", true, 2, "ta.nlo", "on.oon");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(7, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tano", true, 2, "ta.no", "on.on");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
	}

	protected void checkTracingStep(ONCTracingStep sylInfo, String seg1, String nc1,
			String seg2, String nc2, SHComparisonResult result, ONCType oncType,
			ONCSyllabificationStatus status, boolean success) {
		if (seg1 == null) {
			assertNull(sylInfo.getSegment1());
		} else {
			assertEquals(seg1, sylInfo.getSegment1().getSegment());
		}
		if (nc1 == null) {
			assertNull(sylInfo.getNaturalClass1());
		} else {
			assertEquals(nc1, sylInfo.getNaturalClass1().getNCName());
		}
		if (seg2 == null) {
			assertNull(sylInfo.getSegment2());
		} else {
			assertEquals(seg2, sylInfo.getSegment2().getSegment());
		}
		if (nc2 == null) {
			assertNull(sylInfo.getNaturalClass2());
		} else {
			assertEquals(nc2, sylInfo.getNaturalClass2().getNCName());
		}
		assertEquals(result, sylInfo.comparisonResult);
		assertEquals(oncType, sylInfo.getOncType());
		assertEquals(status, sylInfo.getStatus());
		assertEquals(success, sylInfo.isSuccessful());
	}
}
