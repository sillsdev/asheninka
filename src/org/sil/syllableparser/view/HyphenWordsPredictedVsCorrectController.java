// Copyright (c) 2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class HyphenWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> hyphenWordsPredictedVsCorrectTable;

	public TableView<Word> getHyphenWordsPredictedVsCorrectTable() {
		return hyphenWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(hyphenWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvPredictedVsCorrectSyllabificationProperty());
	}

	public void setData(HyphenApproach hyphenApproachData, ObservableList<Word> words) {
		hyphenApproach = hyphenApproachData;
		this.words = words;
		languageProject = hyphenApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getHyphenPredictedSyllabification().isEmpty() && !word
				.getHyphenPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(hyphenWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		int iLastIndex = mainApp.getApplicationPreferences().getLastHyphenWordsPredictedVsCorrectViewItemUsed();
		focusOnLastItemUsed(iLastIndex);
		updateStatusBar();
	}

	public void handleFilterWords() {
		wordsPredictedVsCorrectTable.refresh();
		setData(hyphenApproach, languageProject.getWords());
		super.handleFilterWords();
	}

	public void handleRemoveFiltersWord() {
		super.handleRemoveFiltersWord();
		setData(hyphenApproach, languageProject.getWords());
	}
}
