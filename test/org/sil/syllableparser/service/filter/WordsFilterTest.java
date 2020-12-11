/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.filter;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.parsing.ONCSyllabifierTestBase;

/**
 * @author Andy Black
 *
 */
public class WordsFilterTest extends ONCSyllabifierTestBase {

	WordsFilter wf;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.projectFile = "test/org/sil/syllableparser/testData/CJOWordFilters.ashedata";
		super.setUp();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void anywhereFilterTest() {
		// ignore diacritics in all
		// first set: case insensitive match of "ta"
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.ANYWHERE, false, false, "ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 737, "akemijantaperotero");
		
		// second set: case sensitive match of "ta"
		wf.setMatchCase(true);
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 735, "akemijantaperotero");

		// third set: case insensitive match of "Ta"
		wf.setMatchCase(false);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 737, "akemijantaperotero");

		// fourth set: case sensitive match of "Ta"
		wf.setMatchCase(true);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 4, "Tajorentsi");
	}

	@Test
	public void atEndFilterTest() {
		// ignore diacritics in all
		// first set: case insensitive match of "ta"
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.AT_END, false, false, "ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 67, "ijeekakinta");
		
		// second set: case sensitive match of "ta"
		wf.setMatchCase(true);
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 67, "ijeekakinta");

		// third set: case insensitive match of "Ta"
		wf.setMatchCase(false);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 67, "ijeekakinta");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 67, "ijeekakinta");

		// fourth set: case sensitive match of "Ta"
		wf.setMatchCase(true);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 0, null);
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 0, null);
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 0, null);
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 0, null);
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 0, null);
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 0, null);
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 0, null);
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 0, null);
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 0, null);
	}

	@Test
	public void atStartFilterTest() {
		// ignore diacritics in all
		// first set: case insensitive match of "ta"
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.AT_START, false, false, "ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 5, "Tajorentsi");
		
		// second set: case sensitive match of "ta"
		wf.setMatchCase(true);
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 1, "tampyaa");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 1, "tampyaa");

		// third set: case insensitive match of "Ta"
		wf.setMatchCase(false);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 5, "Tajorentsi");

		// fourth set: case sensitive match of "Ta"
		wf.setMatchCase(true);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 4, "Tajorentsi");
	}

	@Test
	public void blanksFilterTest() {
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.BLANKS, false, false, "");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 1, "anik");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 0, null);
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 0, null);
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 0, null);
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 1, "anik");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 0, null);
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 0, null);
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 0, null);
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 0, null);
	}

	@Test
	public void nonblanksFilterTest() {
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.NON_BLANKS, false, false, "");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 1716, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 1716, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 1716, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 1715, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 1716, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 1716, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 1716, "achariniiteni");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 1716, "achariniiteni");
	}

	@Test
	public void regularExpressionFilterTest() {
		// ignore diacritics in all
		// first set: case insensitive match of "ta" // "n\\.?[tk]"
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.REGULAR_EXPRESSION, false, false, "ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 737, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 737, "akemijantaperotero");
		
		// second set: case sensitive match of "ta"
		wf.setMatchCase(true);
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 735, "akemijantaperotero");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 735, "akemijantaperotero");

		// third set: case insensitive match of "Ta"
		wf.setMatchCase(false);
		wf.setTextToMatch("^Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 5, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 5, "Tajorentsi");

		// fourth set: case sensitive match of "Ta"
		wf.setMatchCase(true);
		wf.setTextToMatch("^Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 4, "Tajorentsi");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 4, "Tajorentsi");
	}

	@Test
	public void wholeItemFilterTest() {
		// ignore diacritics in all
		// first set: case insensitive match of "ta"
		wf = new WordsFilter(WordsFilterType.ONC_PREDICTED, ColumnFilterType.WHOLE_ITEM, false, false, "ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 2, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 2, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 2, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 2, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 2, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 2, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 2, "monkaataka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 2, "monkaataka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 2, "monkaataka", "monkaataka");
		
		// second set: case sensitive match of "ta"
		wf.setMatchCase(true);
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 1, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 1, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 1, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 1, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 1, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 1, "mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 1, "monkaataka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 1, "monkaataka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 1, "monkaataka", "monkaataka");

		// third set: case insensitive match of "Ta"
		wf.setMatchCase(false);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 2, "Mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 2, "Mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 2, "Mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 2, "Mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 2, "Mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 2, "Mon.kaa.ta.ka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 2, "Monkaataka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 2, "Monkaataka", "monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 2, "Monkaataka", "monkaataka");

		// fourth set: case sensitive match of "Ta"
		wf.setMatchCase(true);
		wf.setTextToMatch("Ta");
		checkApplyFilter(wf, WordsFilterType.ONC_PREDICTED, 1, "Mon.kaa.ta.ka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_CORRECT, 1, "Mon.kaa.ta.ka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_CORRECT, 1, "Mon.kaa.ta.ka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_CORRECT, 1, "Mon.kaa.ta.ka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_PREDICTED, 1, "Mon.kaa.ta.ka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_PREDICTED, 1, "Mon.kaa.ta.ka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.ONC_WORDS, 1, "Monkaataka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.CV_WORDS, 1, "Monkaataka", "Monkaataka");
		checkApplyFilter(wf, WordsFilterType.SH_WORDS, 1, "Monkaataka", "Monkaataka");
	}

	private void checkApplyFilter(WordsFilter wf, WordsFilterType wordsFilterType, int numMatched, String firstMatchedWord) {
		wf.setWordsFilterType(wordsFilterType);
		ObservableList<Word> filteredWords = wf.applyFilter(oncApproach.getWords());
		assertEquals(numMatched, filteredWords.size());
		if (numMatched > 0) {
			Word word = filteredWords.get(0);
			assertEquals(firstMatchedWord, word.getWord());
		}
	}

	private void checkApplyFilter(WordsFilter wf, WordsFilterType wordsFilterType, int numMatched, String textToMatch,
			String firstMatchedWord) {
		wf.setTextToMatch(textToMatch);
		checkApplyFilter(wf, wordsFilterType, numMatched, firstMatchedWord);
	}
}
