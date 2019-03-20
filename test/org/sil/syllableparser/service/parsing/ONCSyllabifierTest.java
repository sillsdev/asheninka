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
import org.sil.syllableparser.model.oncapproach.ONCTraceSyllabifierInfo;
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
		List<ONCSegmentInSyllable> segmentsInWord = segmenter.getONCSegmentsInWord();
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
		List<ONCTraceSyllabifierInfo> tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(0, tracingSteps.size());

		checkSyllabifyWord("A", true, 1, "A", "n");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, tracingSteps.size());
		ONCTraceSyllabifierInfo sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "a", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("ta", true, 1, "ta", "on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "a", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("tan", true, 1, "tan", "onc");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);

		checkSyllabifyWord("Chiko", true, 2, "Chi.ko", "on.on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(6, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "ch", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "i", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "onc.on.onc");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(9, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "m", "Nasals", "p", "Obstruents", SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(7);
		checkTracingStep(sylInfo, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(8);
		checkTracingStep(sylInfo, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);

		checkSyllabifyWord("dapgek", false, 2, "da.pge", "on.oon");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(9, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "d", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(7);
		checkTracingStep(sylInfo, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
		sylInfo = tracingSteps.get(8);
		checkTracingStep(sylInfo, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);

		checkSyllabifyWord("doldek", false, 1, "do", "on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);

		checkSyllabifyWord("dlofun", true, 2, "dlo.fun", "oon.onc");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "d", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "o", "Vowels", "f", "Obstruents", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);

		checkSyllabifyWord("funmo", true, 2, "fun.mo", "onc.on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("funmlo", true, 2, "fun.mlo", "onc.oon");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(8, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(7);
		checkTracingStep(sylInfo, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("funmdo", true, 2, "funm.do", "oncc.on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(8, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "m", "Nasals", "d", "Obstruents", SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(7);
		checkTracingStep(sylInfo, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("flu", true, 1, "flu", "oon");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "u", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "u", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("flum", true, 1, "flum", "oonc");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "m", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE);

		checkSyllabifyWord("iae", true, 1, "iae", "nnn");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "a", "Vowels", "e", "Vowels", SHComparisonResult.EQUAL,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "e", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "e", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		checkSyllabifyWord("babe", false, 0, "", ""); // b not in hierarchy
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "b", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);

		checkSyllabifyWord("ibabe", false, 1, "i", "n"); // b not in hierarchy
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				ONCType.UNKNOWN, ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT);
		
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tan", false, 1, "ta", "on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCType.UNKNOWN, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND);

		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("a", false, 0, "", "");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "a", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tanlo", true, 2, "ta.nlo", "on.oon");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(6);
		checkTracingStep(sylInfo, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tano", true, 2, "ta.no", "on.on");
		tracingSteps = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(6, tracingSteps.size());
		sylInfo = tracingSteps.get(0);
		checkTracingStep(sylInfo, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(1);
		checkTracingStep(sylInfo, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(2);
		checkTracingStep(sylInfo, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE);
		sylInfo = tracingSteps.get(3);
		checkTracingStep(sylInfo, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCType.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET);
		sylInfo = tracingSteps.get(4);
		checkTracingStep(sylInfo, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS);
		sylInfo = tracingSteps.get(5);
		checkTracingStep(sylInfo, "o", null, null, null, null,
				ONCType.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD);
	}

	protected void checkTracingStep(ONCTraceSyllabifierInfo sylInfo, String seg1, String nc1,
			String seg2, String nc2, SHComparisonResult result, ONCType oncType,
			ONCSyllabificationStatus status) {
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
	}
}
