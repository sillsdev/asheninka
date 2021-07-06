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
	public void calculateApproachesToUseTest() {
		// zero set
		checkApproachesToUse(false, false, false, false, false, false, 0);

		// one set
		checkApproachesToUse(true, false, false, false, false, false, 1);
		checkApproachesToUse(false, true, false, false, false, false, 2);
		checkApproachesToUse(false, false, true, false, false, false, 4);
		checkApproachesToUse(false, false, false, true, false, false, 8);
		checkApproachesToUse(false, false, false, false, true, false, 16);
		checkApproachesToUse(false, false, false, false, false, true, 32);

		// two set
		checkApproachesToUse(true, true, false, false, false, false, 3);
		checkApproachesToUse(true, false, true, false, false, false, 5);
		checkApproachesToUse(true, false, false, true, false, false, 9);
		checkApproachesToUse(true, false, false, false, true, false, 17);
		checkApproachesToUse(true, false, false, false, false, true, 33);

		checkApproachesToUse(false, true, true, false, false, false, 6);
		checkApproachesToUse(false, true, false, true, false, false, 10);
		checkApproachesToUse(false, true, false, false, true, false, 18);
		checkApproachesToUse(false, true, false, false, false, true, 34);

		checkApproachesToUse(false, false, true, true, false, false, 12);
		checkApproachesToUse(false, false, true, false, true, false, 20);
		checkApproachesToUse(false, false, true, false, false, true, 36);

		checkApproachesToUse(false, false, false, true, true, false, 24);
		checkApproachesToUse(false, false, false, true, false, true, 40);

		checkApproachesToUse(false, false, false, false, true, true, 48);

		// three set
		checkApproachesToUse(true, true, true, false, false, false, 7);
		checkApproachesToUse(true, true, false, true, false, false, 11);
		checkApproachesToUse(true, true, false, false, true, false, 19);
		checkApproachesToUse(true, true, false, false, false, true, 35);

		checkApproachesToUse(true, false, true, true, false, false, 13);
		checkApproachesToUse(true, false, true, false, true, false, 21);
		checkApproachesToUse(true, false, true, false, false, true, 37);

		checkApproachesToUse(true, false, false, true, true, false, 25);
		checkApproachesToUse(true, false, false, true, false, true, 41);

		checkApproachesToUse(true, false, false, false, true, true, 49);

		checkApproachesToUse(false, true, true, true, false, false, 14);
		checkApproachesToUse(false, true, true, false, true, false, 22);
		checkApproachesToUse(false, true, true, false, false, true, 38);

		checkApproachesToUse(false, true, false, true, true, false, 26);
		checkApproachesToUse(false, true, false, true, false, true, 42);

		checkApproachesToUse(false, true, false, false, true, true, 50);

		checkApproachesToUse(false, false, true, true, true, false, 28);
		checkApproachesToUse(false, false, true, true, false, true, 44);
		checkApproachesToUse(false, false, true, false, true, true, 52);

		checkApproachesToUse(false, false, false, true, true, true, 56);

		// four set
		checkApproachesToUse(true, true, true, true, false, false, 15);
		checkApproachesToUse(true, true, true, false, true, false, 23);
		checkApproachesToUse(true, true, true, false, false, true, 39);

		checkApproachesToUse(true, true, false, true, true, false, 27);
		checkApproachesToUse(true, true, false, true, false, true, 43);
		checkApproachesToUse(true, true, false, false, true, true, 51);

		checkApproachesToUse(true, false, true, true, true, false, 29);
		checkApproachesToUse(true, false, true, true, false, true, 45);
		checkApproachesToUse(true, false, true, false, true, true, 53);

		checkApproachesToUse(true, false, false, true, true, true, 57);

		checkApproachesToUse(false, true, true, true, true, false, 30);
		checkApproachesToUse(false, true, true, true, false, true, 46);
		checkApproachesToUse(false, true, true, false, true, true, 54);
		checkApproachesToUse(false, true, false, true, true, true, 58);

		checkApproachesToUse(false, false, true, true, true, true, 60);

		// five set
		checkApproachesToUse(true, true, true, true, true, false, 31);
		checkApproachesToUse(true, true, true, true, false, true, 47);
		checkApproachesToUse(true, true, true, false, true, true, 55);
		checkApproachesToUse(true, true, false, true, true, true, 59);
		checkApproachesToUse(true, false, true, true, true, true, 61);
		checkApproachesToUse(false, true, true, true, true, true, 62);

		// six set
		checkApproachesToUse(true, true, true, true, true, true, 63);
	}

	protected void checkApproachesToUse(boolean useCV, boolean useSH, boolean useONC,
			boolean useMoraic, boolean useNP, boolean useOT, int approachesExpected) {
		comparer.setUseCVApproach(useCV);
		comparer.setUseSHApproach(useSH);
		comparer.setUseONCApproach(useONC);
		comparer.setUseMoraicApproach(useMoraic);
		comparer.setUseNPApproach(useNP);
		comparer.setUseOTApproach(useOT);
		comparer.calculateApproachesToUse();
		assertEquals(approachesExpected, comparer.getApproachesToUse());
	}

	protected void checkNumberOfApproachesToCompare(boolean useCV, boolean useSH, boolean useONC,
			boolean useMoraic, boolean useNP, boolean useOT, int numberOfApproachesExpected) {
		comparer.setUseCVApproach(useCV);
		comparer.setUseSHApproach(useSH);
		comparer.setUseONCApproach(useONC);
		comparer.setUseMoraicApproach(useMoraic);
		comparer.setUseNPApproach(useNP);
		comparer.setUseOTApproach(useOT);
		comparer.calculateApproachesToUse();
		assertEquals(numberOfApproachesExpected, comparer.numberOfApproachesBeingCompared());
	}

	@Test
	public void compareCVandSHWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2134, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(312);
		assertEquals("10's cv is dos.yen.tos", "dos.yen.tos",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'do.syen.tos'", "do.syen.tos",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(258);
		assertEquals("258's cv is ba.bel", "ba.bel", diffWord.getCVPredictedSyllabification());
		assertEquals("258's sh is ''", "", diffWord.getSHPredictedSyllabification());
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
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3040, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(290);
		assertEquals("290's cv is 'kik.wa.la.ni.li.ti.yas.ke'", "kik.wa.la.ni.li.ti.yas.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("290's onc is 'ki.kwa.la.ni.li.ti.ya.ske'", "",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(240);
		assertEquals("240's cv is 'kih.ne.kis.ke'", "kih.ne.kis.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("240's onc is 'ki.hne.ki.ske'", "", diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(946);
		assertEquals("946's cv is 'mi.yak'", "mi.yak", diffWord.getCVPredictedSyllabification());
		assertEquals("946's onc is ''", "", diffWord.getONCPredictedSyllabification());
	}

	@Test
	public void compareCVandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1585, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(47);
		assertEquals("47's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("47's moraic is 'dyes.yes.ye.te'", "dyes.yes.ye.te",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareCVandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3002, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(47);
		assertEquals("47's cv is 'chi.ki.wit'", "chi.ki.wit",
				diffWord.getCVPredictedSyllabification());
		assertEquals("47's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1599, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(0);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("a", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(47);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("a.min.na.dab", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandONCWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3723, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("263's sh is 'chih.chi.pa.wak'", "chih.chi.pa.wak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("263's onc is ''", "", diffWord.getONCPredictedSyllabification());
	}

	@Test
	public void compareSHandMoraicWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2947, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(100);
		assertEquals("100's sh is 'an.me.hwah.te'", "an.me.hwah.te",
				diffWord.getSHPredictedSyllabification());
		assertEquals("100's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("263's sh is 'chi.kwa.se'", "chi.kwa.se",
				diffWord.getSHPredictedSyllabification());
		assertEquals("263's moraic is 'chik.wa.se'", "chik.wa.se",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareSHandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3801, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("263's sh is 'chih.chi.pa.wak'", "chih.chi.pa.wak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("263's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareSHandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1332, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("ab.we.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("ke.chi.was.kya", diffWord.getSHPredictedSyllabification());
		assertEquals("ke.chi.wask.ya", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareONCandMoraicWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1706, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(6);
		assertEquals("10's onc is 'chi.kna.wi'", "chi.kna.wi",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is 'chik.na.wi'", "chik.na.wi",
				diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(7);
		assertEquals("11's onc is 'chi.kwa.se'", "chi.kwa.se",
				diffWord.getONCPredictedSyllabification());
		assertEquals("11's moraic is 'chik.wa.se'", "chik.wa.se",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareONCandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 135, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's onc is 'ga.da.ra'", "ga.da.ra",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(113);
		assertEquals("113's onc is 'si.ko.mo.ro'", "si.ko.mo.ro",
				diffWord.getONCPredictedSyllabification());
		assertEquals("113's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareONCandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4070, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1263);
		assertEquals("ki.te.na.mi.kya", diffWord.getONCPredictedSyllabification());
		assertEquals("ki.te.na.mik.ya", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareMoraicandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1571, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's moraic is 'chi.pak'", "chi.pak",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("263's moraic is 'kint.sak.wi.li'", "kint.sak.wi.li",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("263's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareMoraicandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2605, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(263);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.tad", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		 comparer.compareSyllabifications();
		 SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		 assertEquals("number of different words", 4145, diffs.size());
		 List<Word> listOfDiffs = new ArrayList<Word>();
		 listOfDiffs.addAll(diffs);
		 Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(457);
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ge.re.ka", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4233, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(315);
		assertEquals("315's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("315's sh is 'cho.kas.kya'", "cho.kas.kya",
				diffWord.getSHPredictedSyllabification());
		assertEquals("315's onc is ''", "", diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(258);
		assertEquals("258's cv is ba.bel", "ba.bel", diffWord.getCVPredictedSyllabification());
		assertEquals("258's sh is ''", "", diffWord.getSHPredictedSyllabification());
		assertEquals("258's onc is ''", "", diffWord.getONCPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3222, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(300);
		assertEquals("300's cv is 'chik.na.wi'", "chik.na.wi",
				diffWord.getCVPredictedSyllabification());
		assertEquals("300's sh is 'chi.kna.wi'", "chi.kna.wi",
				diffWord.getSHPredictedSyllabification());
		assertEquals("300's moraic is 'chik.na.wi'", "chik.na.wi",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4275, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(315);
		assertEquals("315's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("315's sh is 'cho.kas.kya'", "cho.kas.kya",
				diffWord.getSHPredictedSyllabification());
		assertEquals("315's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(258);
		assertEquals("258's cv is ba.bel", "ba.bel", diffWord.getCVPredictedSyllabification());
		assertEquals("258's sh is ''", "", diffWord.getSHPredictedSyllabification());
		assertEquals("258's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2237, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(315);
		assertEquals("bi.wat", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("bi.wat", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(258);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("as.ti.ya.lo.ya", diffWord.getSHPredictedSyllabification());
		assertEquals("as.ti.ya.lo.ya", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3150, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(274);
		assertEquals("274's cv is 'ki.ka.was.ke'", "ki.ka.was.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("274's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("274's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3082, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(274);
		assertEquals("274's cv is 'ki.ket.sa.ya'", "ki.ket.sa.ya",
				diffWord.getCVPredictedSyllabification());
		assertEquals("274's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("274's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4287, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(274);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.yah.mo", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3070, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(274);
		assertEquals("274's cv is 'ki.ko.wak'", "ki.ko.wak",
				diffWord.getCVPredictedSyllabification());
		assertEquals("274's moraic is 'ki.ko.wak'", "ki.ko.wak",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("274's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2858, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1274);
		assertEquals("mo.chi.ka.wa.lis.te", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("mo.chi.ka.wa.lis.te", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4329, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(2274);
		assertEquals("nin.kal", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("nin.kal", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandMoraicWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4027, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's sh is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'dyes.yes.ye.te'", "dyes.yes.ye.te",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3801, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's sh is 'dyos.to.tat.s'", "dyos.to.tat.s",
				diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("298's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		 comparer.compareSyllabifications();
		 SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		 assertEquals("number of different words", 4093, diffs.size());
		 List<Word> listOfDiffs = new ArrayList<Word>();
		 listOfDiffs.addAll(diffs);
		 Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(3298);
		assertEquals("ta.nas.te.kal", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("ta.nas.te.kal", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4027, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's sh is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getSHPredictedSyllabification());
		assertEquals("298's moraic is 'dyes.yes.ye.te'", "dyes.yes.ye.te",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("298's np is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareSHandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		 comparer.compareSyllabifications();
		 SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		 assertEquals("number of different words", 3102, diffs.size());
		 List<Word> listOfDiffs = new ArrayList<Word>();
		 listOfDiffs.addAll(diffs);
		 Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(2298);
		assertEquals("shik.chi.wi.li.ti", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("shik.chi.wi.li.ti", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4168, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(3298);
		assertEquals("ta.kwih.kwi.lo.li", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("tak.wihk.wi.lo.li", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareONCandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 1706, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is 'chi.pak'", "chi.pak",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'kint.sak.wa'", "kint.sak.wa",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("298's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareONCandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4159, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(2298);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("nis.te.mo.ti.kat.ka.lo.ya", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareONCandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		 comparer.compareSyllabifications();
		 SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		 assertEquals("number of different words", 4145, diffs.size());
		 List<Word> listOfDiffs = new ArrayList<Word>();
		 listOfDiffs.addAll(diffs);
		 Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(2298);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ni.tah.to.lils.te", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		 comparer.compareSyllabifications();
		 SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		 assertEquals("number of different words", 4159, diffs.size());
		 List<Word> listOfDiffs = new ArrayList<Word>();
		 listOfDiffs.addAll(diffs);
		 Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(3296);
		assertEquals("tak.wi.tap", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("tak.wi.tap", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandMoraicWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4292, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's cv is 'chi.chik'", "chi.chik", diffWord.getCVPredictedSyllabification());
		assertEquals("298's sh is 'chi.chik'", "chi.chik", diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'chi.chik'", "chi.chik",
				diffWord.getMoraicPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4275, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's cv is 'chi.chik'", "chi.chik", diffWord.getCVPredictedSyllabification());
		assertEquals("298's sh is 'chi.chik'", "chi.chik", diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("298's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4310, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("be.ker", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("be.ke.r", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4292, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's cv is 'chi.chik'", "chi.chik", diffWord.getCVPredictedSyllabification());
		assertEquals("298's sh is 'chi.chik'", "chi.chik", diffWord.getSHPredictedSyllabification());
		assertEquals("298's moraic is 'chi.chik'", "chi.chik",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("298's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3296, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(2398);
		assertEquals("shi.kin.kish.ti", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4352, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1298);
		assertEquals("ki.ta.pa.chol", diffWord.getCVPredictedSyllabification());
		assertEquals("ki.ta.pa.chol", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ki.ta.pa.chol", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3150, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so", diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's cv is 'ki.ko.wak'", "ki.ko.wak",
				diffWord.getCVPredictedSyllabification());
		assertEquals("298's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'ki.ko.wak'", "ki.ko.wak",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("298's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4343, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(3298);
		assertEquals("tah.tol.mes", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("tah.tol.mes", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4329, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(3298);
		assertEquals("ta.kah.ka.wal", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ta.kah.ka.wal", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4343, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1398);
		assertEquals("kit.sa.wi.lis.ke", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("kit.sa.wi.lis.ke", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4027, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's sh is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'dyes.yes.ye.te'", "dyes.yes.ye.te",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("298's np is 'dye.sye.sye.te'", "dye.sye.sye.te",
				diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4182, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1298);
		assertEquals("ki.te.ta.nik", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("ki.te.ta.nik", diffWord.getMoraicPredictedSyllabification());
		assertEquals("ki.te.ta.nik", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4168, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1298);
		assertEquals("ki.ti.pa.no.lis.te", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ki.ti.pa.no.lis.te", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4182, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1298);
		assertEquals("ki.te.ta.nik", diffWord.getSHPredictedSyllabification());
		assertEquals("ki.te.ta.nik", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ki.te.ta.nik", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareONCandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4159, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(3245);
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ta.kis.ti.kat.ka.lo.ya", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandMoraicandNPWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4292, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is ''", "", diffWord.getCVPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		assertEquals("10's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("10's moraic is ''", "", diffWord.getMoraicPredictedSyllabification());
		assertEquals("10's np is ''", "", diffWord.getNPPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("298's cv is 'chi.chik'", "chi.chik", diffWord.getCVPredictedSyllabification());
		assertEquals("298's sh is 'chi.chik'", "chi.chik", diffWord.getSHPredictedSyllabification());
		assertEquals("298's onc is ''", "", diffWord.getONCPredictedSyllabification());
		assertEquals("298's moraic is 'chi.chik'", "chi.chik",
				diffWord.getMoraicPredictedSyllabification());
		assertEquals("298's np is ''", "", diffWord.getNPPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandMoraicandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(false);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4366, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("be.ker", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("be.ke.r", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(false);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4352, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("be.ker", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("be.ke.r", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4366, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("be.ker", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("be.ke.r", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4343, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("be.ker", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("be.ke.r", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareSHandONCandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4182, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(1298);
		assertEquals("ki.te.ta.nik", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("ki.te.ta.nik", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("ki.te.ta.nik", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void compareCVandSHandONCandMoraicandNPandOTWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.setUseMoraicApproach(true);
		comparer.setUseNPApproach(true);
		comparer.setUseOTApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4366, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("a.bu.e.lol.me", diffWord.getOTPredictedSyllabification());
		diffWord = listOfDiffs.get(298);
		assertEquals("be.ker", diffWord.getCVPredictedSyllabification());
		assertEquals("", diffWord.getSHPredictedSyllabification());
		assertEquals("", diffWord.getONCPredictedSyllabification());
		assertEquals("", diffWord.getMoraicPredictedSyllabification());
		assertEquals("", diffWord.getNPPredictedSyllabification());
		assertEquals("be.ke.r", diffWord.getOTPredictedSyllabification());
	}

	@Test
	public void numberOfApproachesBeingComparedTest() {
		// zero set
		checkNumberOfApproachesToCompare(false, false, false, false, false, false, 0);

		// one set
		checkNumberOfApproachesToCompare(true, false, false, false, false, false, 1);
		checkNumberOfApproachesToCompare(false, true, false, false, false, false, 1);
		checkNumberOfApproachesToCompare(false, false, true, false, false, false, 1);
		checkNumberOfApproachesToCompare(false, false, false, true, false, false, 1);
		checkNumberOfApproachesToCompare(false, false, false, false, true, false, 1);
		checkNumberOfApproachesToCompare(false, false, false, false, false, true, 1);

		// two set
		checkNumberOfApproachesToCompare(true, true, false, false, false, false, 2);
		checkNumberOfApproachesToCompare(true, false, true, false, false, false, 2);
		checkNumberOfApproachesToCompare(true, false, false, true, false, false, 2);
		checkNumberOfApproachesToCompare(true, false, false, false, true, false, 2);
		checkNumberOfApproachesToCompare(true, false, false, false, false, true, 2);

		checkNumberOfApproachesToCompare(false, true, true, false, false, false, 2);
		checkNumberOfApproachesToCompare(false, true, false, true, false, false, 2);
		checkNumberOfApproachesToCompare(false, true, false, false, true, false, 2);
		checkNumberOfApproachesToCompare(false, true, false, false, false, true, 2);

		checkNumberOfApproachesToCompare(false, false, true, true, false, false, 2);
		checkNumberOfApproachesToCompare(false, false, true, false, true, false, 2);
		checkNumberOfApproachesToCompare(false, false, true, false, false, true, 2);

		checkNumberOfApproachesToCompare(false, false, false, true, true, false, 2);
		checkNumberOfApproachesToCompare(false, false, false, true, false, true, 2);

		checkNumberOfApproachesToCompare(false, false, false, false, true, true, 2);

		// three set
		checkNumberOfApproachesToCompare(true, true, true, false, false, false, 3);
		checkNumberOfApproachesToCompare(true, true, false, true, false, false, 3);
		checkNumberOfApproachesToCompare(true, true, false, false, true, false, 3);
		checkNumberOfApproachesToCompare(true, true, false, false, false, true, 3);

		checkNumberOfApproachesToCompare(true, false, true, true, false, false, 3);
		checkNumberOfApproachesToCompare(true, false, true, false, true, false, 3);
		checkNumberOfApproachesToCompare(true, false, true, false, false, true, 3);

		checkNumberOfApproachesToCompare(true, false, false, true, true, false, 3);
		checkNumberOfApproachesToCompare(true, false, false, true, false, true, 3);

		checkNumberOfApproachesToCompare(true, false, false, false, true, true, 3);

		checkNumberOfApproachesToCompare(false, true, true, true, false, false, 3);
		checkNumberOfApproachesToCompare(false, true, true, false, true, false, 3);
		checkNumberOfApproachesToCompare(false, true, true, false, false, true, 3);

		checkNumberOfApproachesToCompare(false, true, false, true, true, false, 3);
		checkNumberOfApproachesToCompare(false, true, false, true, false, true, 3);

		checkNumberOfApproachesToCompare(false, true, false, false, true, true, 3);

		checkNumberOfApproachesToCompare(false, false, true, true, true, false, 3);
		checkNumberOfApproachesToCompare(false, false, true, true, false, true, 3);
		checkNumberOfApproachesToCompare(false, false, true, false, true, true, 3);

		checkNumberOfApproachesToCompare(false, false, false, true, true, true, 3);

		// four set
		checkNumberOfApproachesToCompare(true, true, true, true, false, false, 4);
		checkNumberOfApproachesToCompare(true, true, true, false, true, false, 4);
		checkNumberOfApproachesToCompare(true, true, true, false, false, true, 4);

		checkNumberOfApproachesToCompare(true, true, false, true, true, false, 4);
		checkNumberOfApproachesToCompare(true, true, false, true, false, true, 4);
		checkNumberOfApproachesToCompare(true, true, false, false, true, true, 4);

		checkNumberOfApproachesToCompare(true, false, true, true, true, false, 4);
		checkNumberOfApproachesToCompare(true, false, true, true, false, true, 4);
		checkNumberOfApproachesToCompare(true, false, true, false, true, true, 4);

		checkNumberOfApproachesToCompare(true, false, false, true, true, true, 4);

		checkNumberOfApproachesToCompare(false, true, true, true, true, false, 4);
		checkNumberOfApproachesToCompare(false, true, true, true, false, true, 4);
		checkNumberOfApproachesToCompare(false, true, true, false, true, true, 4);
		checkNumberOfApproachesToCompare(false, true, false, true, true, true, 4);

		checkNumberOfApproachesToCompare(false, false, true, true, true, true, 4);

		// five set
		checkNumberOfApproachesToCompare(true, true, true, true, true, false, 5);
		checkNumberOfApproachesToCompare(true, true, true, true, false, true, 5);
		checkNumberOfApproachesToCompare(true, true, true, false, true, true, 5);
		checkNumberOfApproachesToCompare(true, true, false, true, true, true, 5);
		checkNumberOfApproachesToCompare(true, false, true, true, true, true, 5);
		checkNumberOfApproachesToCompare(false, true, true, true, true, true, 5);

		// six set
		checkNumberOfApproachesToCompare(true, true, true, true, true, true, 6);
	}
}
