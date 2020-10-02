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
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */

public class ONCWordsPredictedVsCorrectController extends WordsPredictedVsCorrectCommonController {

	@FXML
	protected TableView<Word> oncWordsPredictedVsCorrectTable;

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

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getONCPredictedSyllabification().isEmpty() && !word
				.getONCPredictedSyllabification().equals(word.getCorrectSyllabification())));

		// Add observable list data to the table
		wordsPredictedVsCorrectTable.setItems(wordsToShow.sorted());
		setFocusOnWord(0);
	}
}
