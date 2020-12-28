// Copyright (c) 2020 SIL International
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
	protected TableView<Word> muWordsPredictedVsCorrectTable;

	public TableView<Word> getMuWordsPredictedVsCorrectTable() {
		return muWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(muWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.muPredictedVsCorrectSyllabificationProperty());
	}
	
	public void setData(MoraicApproach muApproachData, ObservableList<Word> words) {
		muApproach = muApproachData;
		this.words = words;
		languageProject = muApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getMuPredictedSyllabification().isEmpty() && !word
				.getMuPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(muWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		int iLastIndex = mainApp.getApplicationPreferences().getLastMoraicWordsPredictedVsCorrectViewItemUsed();
		focusOnLastItemUsed(iLastIndex);
		updateStatusBar();
	}

	public void handleFilterWords() {
		wordsPredictedVsCorrectTable.refresh();
		setData(muApproach, languageProject.getWords());
		super.handleFilterWords();
	}

	public void handleRemoveFiltersWord() {
		super.handleRemoveFiltersWord();
		setData(muApproach, languageProject.getWords());
	}

}
