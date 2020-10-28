// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.oncapproach.ONCApproach;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class ONCWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> oncWordsPredictedVsCorrectTable;

	public TableView<Word> getONCWordsPredictedVsCorrectTable() {
		return oncWordsPredictedVsCorrectTable;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(oncWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.oncPredictedVsCorrectSyllabificationProperty());
	}
	
	public void setData(ONCApproach oncApproachData, ObservableList<Word> words) {
		oncApproach = oncApproachData;
		this.words = words;
		languageProject = oncApproachData.getLanguageProject();
		setColumnICURules(wordPredictedVsCorrectColumn, languageProject.getVernacularLanguage().getAnyIcuRules());

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getONCPredictedSyllabification().isEmpty() && !word
				.getONCPredictedSyllabification().equals(word.getCorrectSyllabification())));
		SortedList<Word> wordsSorted = wordsToShow.sorted();
		wordsSorted.comparatorProperty().bind(oncWordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(wordsSorted);
		updateStatusBar();
		setFocusOnWord(0);
	}
}
