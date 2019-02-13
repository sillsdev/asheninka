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
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTraceSyllabifierInfo;
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
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
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
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

	protected void checkSyllabifyWord(String word, boolean success, String expectedNaturalClasses,
			String expectedSonorityValues, int numberOfSyllables, String expectedSyllabification) {
		boolean fSuccess = oncSyllabifier.convertStringToSyllables(word);
		assertEquals("word syllabified", success, fSuccess);
		String naturalClassesInWord = oncSyllabifier.getNaturalClassesInCurrentWord();
		assertEquals(expectedNaturalClasses, naturalClassesInWord);
		String sonorityValues = oncSyllabifier.getSonorityValuesInCurrentWord();
		assertEquals(expectedSonorityValues, sonorityValues);
		List<ONCSyllable> syllablesInWord = oncSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				oncSyllabifier.getSyllabificationOfCurrentWord());
	}

//	@Test
	public void traceSyllabifyWordTest() {
		oncSyllabifier.setDoTrace(true);

		checkSyllabifyWord("", false, "", "", 0, "");
		List<ONCTraceSyllabifierInfo> traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(0, traceInfo.size());

		checkSyllabifyWord("A", true, "Vowels, null",  ONCTraceSyllabifierInfo.NULL_REPRESENTATION, 1, "A");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		ONCTraceSyllabifierInfo sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("ta", true, "Obstruents, Vowels", "<", 1, "ta");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("tad", true, "Obstruents, Vowels, Obstruents", "<, >", 1, "tad");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("Chiko", true, "Obstruents, Vowels, Obstruents, Vowels",
				"<, >, <", 2, "Chi.ko");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("tampidon", true,
				"Obstruents, Vowels, Nasals, Obstruents, Vowels, Obstruents, Vowels, Nasals",
				"<, >, >, <, >, <, >", 3, "tam.pi.don");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(5);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(6);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("dapgek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Vowels, Obstruents",
				"<, >, =, <, >", 2, "dap.gek");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("dapkgek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Obstruents, Vowels, Obstruents",
				"<, >, =, =, <, >", 2, "dap.kgek");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(6, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(5);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("dovdek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Vowels, Obstruents",
				"<, >, =, <, >", 2, "dov.dek");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("fuhgt", true, "Obstruents, Vowels, Obstruents, Obstruents, Obstruents",
				"<, >, =, =", 2, "fuh.gt");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("dlofugh", true,
				"Obstruents, Liquids, Vowels, Obstruents, Vowels, Obstruents, Obstruents, null",
				"<, <, >, <, >, =, " + ONCTraceSyllabifierInfo.NULL_REPRESENTATION, 3, "dlo.fug.h");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(5);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(6);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("do", true, "Obstruents, Vowels", "<", 1, "do");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("funglo", true,
				"Obstruents, Vowels, Nasals, Obstruents, Liquids, Vowels",
				"<, >, >, <, <", 2, "fun.glo");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("fugh", true, "Obstruents, Vowels, Obstruents, Obstruents, null",
				"<, >, =, " + ONCTraceSyllabifierInfo.NULL_REPRESENTATION, 2, "fug.h");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("flu", true, "Obstruents, Liquids, Vowels", "<, <", 1, "flu");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("fluk", true, "Obstruents, Liquids, Vowels, Obstruents",
				"<, <, >", 1, "fluk");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("iae", true, "Vowels, Vowels, Vowels", "=, =", 1, "iae");
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);

		checkSyllabifyWord("babe", false, "null, null", "!!!", 0, ""); // b not in hierarchy
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("ibabe", false, "Vowels, null", "!!!", 0, ""); // b not in hierarchy
		traceInfo = oncSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
	}
}
