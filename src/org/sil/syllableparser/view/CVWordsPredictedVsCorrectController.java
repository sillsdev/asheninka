// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class CVWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> cvWordsPredictedVsCorrectTable;

	public TableView<Word> getCVWordsPredictedVsCorrectTable() {
		return cvWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(cvWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvPredictedVsCorrectSyllabificationProperty());
	}

	public void setData(CVApproach cvApproachData, ObservableList<Word> words) {
		cvApproach = cvApproachData;
		this.words = words;
		languageProject = cvApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getCVPredictedSyllabification().isEmpty() && !word
				.getCVPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(cvWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		updateStatusBar();
		setFocusOnWord(0);
	}
}
