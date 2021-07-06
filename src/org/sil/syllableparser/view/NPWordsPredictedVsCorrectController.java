// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.npapproach.NPApproach;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class NPWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> npWordsPredictedVsCorrectTable;

	public TableView<Word> getNPWordsPredictedVsCorrectTable() {
		return npWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(npWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.npPredictedVsCorrectSyllabificationProperty());
	}
	
	public void setData(NPApproach npApproachData, ObservableList<Word> words) {
		npApproach = npApproachData;
		this.words = words;
		languageProject = npApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getNPPredictedSyllabification().isEmpty() && !word
				.getNPPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(npWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		int iLastIndex = mainApp.getApplicationPreferences().getLastNPWordsPredictedVsCorrectViewItemUsed();
		focusOnLastItemUsed(iLastIndex);
		updateStatusBar();
	}

	public void handleFilterWords() {
		wordsPredictedVsCorrectTable.refresh();
		setData(npApproach, languageProject.getWords());
		super.handleFilterWords();
	}

	public void handleRemoveFiltersWord() {
		super.handleRemoveFiltersWord();
		setData(npApproach, languageProject.getWords());
	}
}
