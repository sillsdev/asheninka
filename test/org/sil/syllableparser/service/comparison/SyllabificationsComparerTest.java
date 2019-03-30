// Copyright (c) 2019 SIL International
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
		checkApproachesToCompare(true, false, true, ApproachesToCompare.CV_ONC);
		checkApproachesToCompare(true, true, false, ApproachesToCompare.CV_SH);
		checkApproachesToCompare(false, true, true, ApproachesToCompare.SH_ONC);
		checkApproachesToCompare(true, true, true, ApproachesToCompare.CV_SH_ONC);
		checkApproachesToCompare(false, false, false, ApproachesToCompare.CV_SH_ONC);
		checkApproachesToCompare(false, false, true, ApproachesToCompare.CV_SH_ONC);
		checkApproachesToCompare(false, true, false, ApproachesToCompare.CV_SH_ONC);
		checkApproachesToCompare(true, false, false, ApproachesToCompare.CV_SH_ONC);
	}

	protected void checkApproachesToCompare(boolean useCV, boolean useSH, boolean useONC,
			ApproachesToCompare approachesExpected) {
		comparer.setUseCVApproach(useCV);
		comparer.setUseSHApproach(useSH);
		comparer.setUseONCApproach(useONC);
		comparer.calculateApproachesToCompare();
		assertEquals(approachesExpected, comparer.getApproachesToCompare());
	}

	@Test
	public void compareCVandSHWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(false);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 2122, diffs.size());
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
		diffWord = listOfDiffs.get(1060);
		assertEquals("1060's cv is mo.ti.yah.wan", "mo.ti.yah.wan",
				diffWord.getCVPredictedSyllabification());
		assertEquals("1060's sh is mo.ti.ya.hwan", "mo.ti.ya.hwan",
				diffWord.getSHPredictedSyllabification());
	}

	@Test
	public void compareCVandONCWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(false);
		comparer.setUseONCApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3285, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's cv is 'ba.so'", "ba.so",
				diffWord.getCVPredictedSyllabification());
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(312);
		assertEquals("312's cv is 'kik.wa.la.ni.li.ti.yas.ke'", "kik.wa.la.ni.li.ti.yas.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("312's onc is 'ki.kwa.la.ni.li.ti.ya.ske'", "ki.kwa.la.ni.li.ti.ya.ske",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(254);
		assertEquals("254's cv is 'kih.ne.kis.ke'", "kih.ne.kis.ke",
				diffWord.getCVPredictedSyllabification());
		assertEquals("254's onc is 'ki.hne.ki.ske'", "ki.hne.ki.ske",
				diffWord.getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(1060);
		assertEquals("1060's cv is 'mi.yak'", "mi.yak",
				diffWord.getCVPredictedSyllabification());
		assertEquals("1060's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
	}
	
	@Test
	public void compareONCandSHWordsTest() {
		comparer.setUseCVApproach(false);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 3645, diffs.size());
		List<Word> listOfDiffs = new ArrayList<Word>();
		listOfDiffs.addAll(diffs);
		Word diffWord = listOfDiffs.get(10);
		assertEquals("10's onc is ''", "",
				diffWord.getONCPredictedSyllabification());
		assertEquals("10's sh is 'a.hwi.yak'", "a.hwi.yak",
				diffWord.getSHPredictedSyllabification());
		diffWord = listOfDiffs.get(262);
		assertEquals("262's onc is 'chi.chi.wa.li.ste'", "chi.chi.wa.li.ste",
				diffWord.getONCPredictedSyllabification());
		assertEquals("262's sh is 'chi.chi.wa.lis.te'", "chi.chi.wa.lis.te",
				diffWord.getSHPredictedSyllabification());
	}

	@Test
	public void compareCVandONCandSHWordsTest() {
		comparer.setUseCVApproach(true);
		comparer.setUseSHApproach(true);
		comparer.setUseONCApproach(true);
		comparer.compareSyllabifications();
		SortedSet<Word> diffs = comparer.getSyllabificationsWhichDiffer();
		assertEquals("number of different words", 4225, diffs.size());
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
		assertEquals("315's onc is 'cho.ka.skya'", "cho.ka.skya",
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
}
