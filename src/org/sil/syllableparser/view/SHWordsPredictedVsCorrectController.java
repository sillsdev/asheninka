// Copyright (c) 2018-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;


import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class SHWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> shWordsPredictedVsCorrectTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(shWordsPredictedVsCorrectTable);
		super.initialize(location, resources);
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.shPredictedVsCorrectSyllabificationProperty());
	}
	
	public void setData(SHApproach shApproachData, ObservableList<Word> words) {
		shApproach = shApproachData;
		this.words = words;
		languageProject = shApproachData.getLanguageProject();

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getSHPredictedSyllabification().isEmpty() && !word
				.getSHPredictedSyllabification().equals(word.getCorrectSyllabification())));

		// Add observable list data to the table
		wordsPredictedVsCorrectTable.setItems(wordsToShow.sorted());
		updateStatusBar();
		setFocusOnWord(0);
	}
}
