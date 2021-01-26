// Copyright (c) 2019-2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.service.comparison.SyllabificationsComparer;
import org.sil.syllableparser.service.comparison.SyllabificationsComparer.ApproachesToCompare;

/**
 * @author Andy Black
 *
 */
public class SyllabificationsComparerTest {

	LanguageProject languageProject;
	CVApproach cva;
	SHApproach sha;
	ONCApproach onca;
	SyllabificationsComparer comparer;

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
		cva = languageProject.getCVApproach();
		sha = languageProject.getSHApproach();
		onca = languageProject.getONCApproach();
		comparer = new SyllabificationsComparer(languageProject);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void languageContentsTest() {
		// words
		ObservableList<Word> words;
		words = cva.getWords();
		assertEquals("CV words size", 10025, words.size());
		words = sha.getWords();
		assertEquals("SH words size", 10025, words.size());
		words = onca.getWords();
		assertEquals("ONC words size", 10025, words.size());
	}

	@Test
	public void calculateApproachesToCompareTest() {
		// two set
		checkApproachesToCompare(true, true, false, false, ApproachesToCompare.CV_SH);
		checkApproachesToCompare(true, false, true, false, ApproachesToCompare.CV_ONC);
		checkApproachesToCompare(true, false, false, true, ApproachesToCompare.CV_MORAIC);
		checkApproachesToCompare(false, true, true, false, ApproachesToCompare.SH_ONC);
		checkApproachesToCompare(false, true, false, true, ApproachesToCompare.SH_MORAIC);
		checkApproachesToCompare(false, false, true, true, ApproachesToCompare.ONC_MORAIC);
		// three set
		checkApproachesToCompare(true, true, true, false, ApproachesToCompare.CV_SH_ONC);
		checkApproachesToCompare(true, true, false, true, ApproachesToCompare.CV_SH_MORAIC);
		checkApproachesToCompare(true, false, true, true, ApproachesToCompare.CV_ONC_MORAIC);
		checkApproachesToCompare(false, true, true, true, ApproachesToCompare.SH_ONC_MORAIC);
		// four set
		checkApproachesToCompare(true, true, true, true, ApproachesToCompare.CV_SH_ONC_MORAIC);
		// zero or one set
		checkApproachesToCompare(false, false, false, false, ApproachesToCompare.CV_SH_ONC_MORAIC);
		checkApproachesToCompare(true, false, false, false, ApproachesToCompare.CV_SH_ONC_MORAIC);
		checkApproachesToCompare(false, true, false, false, ApproachesToCompare.CV_SH_ONC_MORAIC);
		checkApproachesToCompare(false, false, true, false, ApproachesToCompare.CV_SH_ONC_MORAIC);
		checkApproachesToCompare(false, false, false, true, ApproachesToCompare.CV_SH_ONC_MORAIC);
	}

	protected void checkApproachesToCompare(boolean useCV, boolean useSH, boolean useONC,
			boolean useMoraic, ApproachesToCompare approachesExpected) {
		comparer.setUseCVApproach(useCV);
		comparer.setUseSHApproach(useSH);
		comparer.setUseONCApproach(useONC);
		comparer.setUseMoraicApproach(useMoraic);
		comparer.calculateApproachesToCompare();
		assertEquals(approachesExpected, comparer.getApproachesToCompare());
	}

	@Test
	public void compareCVandSHWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2134, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(312);
		assertEquals("10's cv is dos.yen.tos", "dos.yen.tos",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'do.syen.tos'", "do.syen.tos",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(258);
		assertEquals("258's cv is ba.bel", "ba.bel",
				diffWord.getCVPredictedSyllabification());
		assertEquals("258's sh is ''", "",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(1062);
		assertEquals("1062's cv is mo.ti.yah.wan", "mo.ti.yah.wan",
				diffWord.getCVPredictedSyllabification());
		assertEquals("1062's sh is mo.ti.ya.hwan", "mo.ti.ya.hwan",
				diffWord.getSHPredictedSyllabification());
	}

	@Test
	public void compareCVandONCWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3040, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(290);
		assertEquals("290's cv is 'kik.wa.la.ni.li.ti.yas.ke'", "kik.wa.la.ni.li.ti.yas.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("290's onc is 'ki.kwa.la.ni.li.ti.ya.ske'", "",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(240);
		assertEquals("240's cv is 'kih.ne.kis.ke'", "kih.ne.kis.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("240's onc is 'ki.hne.ki.ske'", "",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(946);
		assertEquals("946's cv is 'mi.yak'", "mi.yak",
				diffWord.getCVPredictedSyllabification());
		assertEquals("946's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
	}
	
	@Test
	public void compareONCandSHWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3723, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("263's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("263's sh is 'chih.chi.pa.wak'", "chih.chi.pa.wak",
				diffWord.getSHPredictedSyllabification());
	}

	@Test
	public void compareCVandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 779, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "ba.so",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's moraic is ''", "",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(40);
		assertEquals("40's cv is ''", "",
				diffWord.getCVPredictedSyllabification());
		assertEquals("40's moraic is 'de.mo.ni.o'", "de.mo.ni.o",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareSHandMoraicWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1721, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(100);
		assertEquals("100's sh is 'an.me.hwah.te'", "an.me.hwah.te",
				diffWord.getSHPredictedSyllabification());
		assertEquals("100's moraic is ''", "",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(259);
		assertEquals("259's sh is 'chi.kwa.se'", "chi.kwa.se",
				diffWord.getSHPredictedSyllabification());
		assertEquals("259's moraic is 'chik.wa.se'", "chik.wa.se",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareONCandMoraicWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2997, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'chi.kna.wi'", "chi.kna.wi",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is 'chik.na.wi'", "chik.na.wi",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(11);
		assertEquals("11's sh is 'chi.kwa.se'", "chi.kwa.se",
				diffWord.getONCPredictedSyllabification());
		assertEquals("11's moraic is 'chik.wa.se'", "chik.wa.se",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandSHWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4233, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(315);
		assertEquals("315's cv is ''", "",
				diffWord.getCVPredictedSyllabification());
		assertEquals("315's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("315's sh is 'cho.kas.kya'", "cho.kas.kya",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(258);
		assertEquals("258's cv is ba.bel", "ba.bel",
				diffWord.getCVPredictedSyllabification());
		assertEquals("258's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("258's sh is ''", "",
				diffWord.getSHPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2226, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's moraic is ''", "",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(296);
		assertEquals("296's cv is 'chik.na.wi'", "chik.na.wi",
				diffWord.getCVPredictedSyllabification());
		assertEquals("296's sh is 'chi.kna.wi'", "chi.kna.wi",
				diffWord.getSHPredictedSyllabification());
		assertEquals("296's moraic is 'chik.na.wi'", "chik.na.wi",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3376, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(296);
		assertEquals("296's cv is 'ki.ka.was.ke'", "ki.ka.was.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("296's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("296's moraic is 'ki.ka.was.ke'", "ki.ka.was.ke",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandMoraicWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4005, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's sh is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'dyes.yes.ye.te'", "dyes.yes.ye.te",
				diffWord.getMoraicPredictedSyllabification());
	}


	@Test
	public void compareCVandSHandONCandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4275, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's cv is 'chi.chik'", "chi.chik",
				diffWord.getCVPredictedSyllabification());
		assertEquals("298's sh is 'chi.chik'", "chi.chik",
				diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'chi.chik'", "chi.chik",
				diffWord.getMoraicPredictedSyllabification());
	}
	@Test
		public void numberOfApproachesBeingComparedTest() {
			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(false);
			comparer.setUseMoraicApproach(false);
			assertEquals(2, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(false);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(false);
			assertEquals(2, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(false);
			comparer.setUseONCApproach(false);
			comparer.setUseMoraicApproach(true);
			assertEquals(2, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(false);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(false);
			assertEquals(2, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(false);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(false);
			comparer.setUseMoraicApproach(true);
			assertEquals(2, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(false);
			comparer.setUseSHApproach(false);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(true);
			assertEquals(2, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(false);
			assertEquals(3, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(false);
			comparer.setUseMoraicApproach(true);
			assertEquals(3, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(false);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(true);
			assertEquals(3, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(false);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(true);
			assertEquals(3, comparer.numberOfApproachesBeingCompared());

			comparer.setUseCVApproach(true);
			comparer.setUseSHApproach(true);
			comparer.setUseONCApproach(true);
			comparer.setUseMoraicApproach(true);
			assertEquals(4, comparer.numberOfApproachesBeingCompared());
		}
}
