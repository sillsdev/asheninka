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
import org.sil.syllableparser.model.otapproach.OTApproach;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class OTWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> otWordsPredictedVsCorrectTable;

	public TableView<Word> getOTWordsPredictedVsCorrectTable() {
		return otWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(otWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.otPredictedVsCorrectSyllabificationProperty());
	}

	public void setData(OTApproach otApproachData, ObservableList<Word> words) {
		otApproach = otApproachData;
		this.words = words;
		languageProject = otApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getOTPredictedSyllabification().isEmpty() && !word
				.getOTPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(otWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		for (Word w : wordsSorted) {
			System.out.println("otp:" + w.getOTPredictedSyllabification());
		}
		int iLastIndex = mainApp.getApplicationPreferences().getLastOTWordsPredictedVsCorrectViewItemUsed();
		focusOnLastItemUsed(iLastIndex);
		updateStatusBar();
	}

	public void handleFilterWords() {
		wordsPredictedVsCorrectTable.refresh();
		setData(otApproach, languageProject.getWords());
		super.handleFilterWords();
	}

	public void handleRemoveFiltersWord() {
		super.handleRemoveFiltersWord();
		setData(otApproach, languageProject.getWords());
	}
}
