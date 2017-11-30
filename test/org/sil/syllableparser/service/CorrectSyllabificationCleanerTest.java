package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.CorrectSyllabificationCleaner;

public class CorrectSyllabificationCleanerTest {

	LanguageProject languageProject;

	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void clearAllCorrectSyllabificationFromWordsTest() {
		ObservableList<Word> words = languageProject.getWords();
		CorrectSyllabificationCleaner cleaner = new CorrectSyllabificationCleaner(words);
		assertEquals(10025, words.size());
		// get non-empty correct syls
		List<Word> wordsWithNonEmptyCorrectSyllabification = words.stream()
				.filter(w -> w.getCorrectSyllabification().length() > 0)
				.collect(Collectors.toList());
		assertEquals(3, wordsWithNonEmptyCorrectSyllabification.size());
		cleaner.ClearAllCorrectSyllabificationFromWords();
		assertEquals(10025, words.size());
		wordsWithNonEmptyCorrectSyllabification = words.stream()
				.filter(w -> w.getCorrectSyllabification().length() > 0)
				.collect(Collectors.toList());
		assertEquals(0, wordsWithNonEmptyCorrectSyllabification.size());
	}

}
