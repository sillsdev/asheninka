// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

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
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

/**
 * @author Andy Black
 *
 */
public class SyllabificationsComparerTest {

	LanguageProject languageProject;
	CVApproach cva;
	SHApproach sha;
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
	}

	@Test
	public void compareWordsTest() {
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
}
