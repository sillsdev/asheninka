// Copyright (c) 2018 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class SHWordsController extends WordsControllerCommon {

	@FXML
	protected TableView<Word> shWordsTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(shWordsTable);
		super.initialize(location, resources);
		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.shPredictedSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue()
				.shParserResultProperty());
		predictedSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setSHPredictedSyllabification(predictedSyllabificationField
								.getText());
					}
					if (languageProject != null) {
						predictedSyllabificationField.setFont(languageProject
								.getVernacularLanguage().getFont());
					}
				});
		parserResultField.textProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> {
				if (currentWord != null) {
					currentWord.setSHParserResult(parserResultField.getText());
				}
				if (languageProject != null) {
					parserResultField.setFont(languageProject.getAnalysisLanguage().getFont());
				}
				parserResultField.setEditable(true);
				parserResultField.positionCaret(newValue.length());
				parserResultField.setEditable(false);
			});
		});
		// Clear cv word details.
		showSHWordDetails(null);
		// Listen for selection changes and show the details when changed.
		wordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showSHWordDetails(newValue));
	}
	
	public void setData(SHApproach shApproachData, ObservableList<Word> words) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();
		this.words = words;

		// Add observable list data to the table
		wordsTable.setItems(words);
		int max = wordsTable.getItems().size();
		if (max > 0) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastSHWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		}
	}
	
	/**
	 * Fills all text fields to show details about the CV word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param shWord
	 *            the segment or null
	 */
	private void showSHWordDetails(Word shWord) {
		currentWord = shWord;
		if (shWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(shWord.getWord());
			predictedSyllabificationField.setText(shWord.getSHPredictedSyllabification());
			correctSyllabificationField.setText(shWord.getCorrectSyllabification());
			parserResultField.setText(shWord.getSHParserResult());
			parserResultField.getStyleClass().clear();
			if (shWord.getSHPredictedSyllabification().length() == 0
					&& shWord.getSHParserResult().length() > 0) {
				parserResultField.getStyleClass().add("failedsyllabification");
			} else {
				parserResultField.getStyleClass().add("successfullsyllabification");
			}
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (shWord != null) {
			int currentItem = wordsTable.getItems().indexOf(currentWord);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ wordsTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastSHWordsViewItemUsed(currentItem);
		}
	}

	public TableView<Word> getSHWordsTable() {
		return wordsTable;
	}
}
