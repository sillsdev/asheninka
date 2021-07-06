// Copyright (c) 2020-2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class MoraicWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> moraicWordsPredictedVsCorrectTable;

	public TableView<Word> getMoraicWordsPredictedVsCorrectTable() {
		return moraicWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(moraicWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.moraicPredictedVsCorrectSyllabificationProperty());
	}
	
	public void setData(MoraicApproach moraicApproachData, ObservableList<Word> words) {
		moraicApproach = moraicApproachData;
		this.words = words;
		languageProject = moraicApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getMoraicPredictedSyllabification().isEmpty() && !word
				.getMoraicPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(moraicWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		int iLastIndex = mainApp.getApplicationPreferences().getLastMoraicWordsPredictedVsCorrectViewItemUsed();
		focusOnLastItemUsed(iLastIndex);
		updateStatusBar();
	}

	public void handleFilterWords() {
		wordsPredictedVsCorrectTable.refresh();
		setData(moraicApproach, languageProject.getWords());
		super.handleFilterWords();
	}

	public void handleRemoveFiltersWord() {
		super.handleRemoveFiltersWord();
		setData(moraicApproach, languageProject.getWords());
	}

}
