// Copyright (c) 2016-2020 SIL International 
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
import org.sil.utility.StringUtilities;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class CVWordsController extends WordsControllerCommon {

	@FXML
	protected TableView<Word> cvWordsTable;

	protected final class ParserResultWrappingTableCell extends TableCell<Word, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				setStyle("");
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				Word word = (Word) this.getTableRow().getItem();
				if (word != null && word.getCVParserResult().length() > 0
						&& word.getCVPredictedSyllabification().length() == 0) {
					text.setFill(Constants.PARSER_FAILURE);
				} else {
					text.setFill(Constants.PARSER_SUCCESS);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setWordsTable(cvWordsTable);
		super.initialize(location, resources);
		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvPredictedSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvParserResultProperty());
		predictedSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setCVPredictedSyllabification(predictedSyllabificationField
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
					currentWord.setCVParserResult(parserResultField.getText());
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
		showCVWordDetails(null);
		// Listen for selection changes and show the details when changed.
		wordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showCVWordDetails(newValue));
	}
	
	public void setData(CVApproach cvApproachData, ObservableList<Word> words) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		setColumnICURules(wordColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(commentColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		setColumnICURules(predictedSyllabificationColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(correctSyllabificationColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(parserResultColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		this.words = words;

		// Add observable list data to the table
		wordsTable.setItems(words);
		int max = wordsTable.getItems().size();
		if (max > 0) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastCVWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		}
	}
	
	/**
	 * Fills all text fields to show details about the CV word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param cvWord
	 *            the segment or null
	 */
	private void showCVWordDetails(Word cvWord) {
		currentWord = cvWord;
		if (cvWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(cvWord.getWord());
			commentField.setText(cvWord.getComment());
			predictedSyllabificationField.setText(cvWord.getCVPredictedSyllabification());
			correctSyllabificationField.setText(cvWord.getCorrectSyllabification());
			parserResultField.setText(cvWord.getCVParserResult());
			setParserResultFieldColor(cvWord.getCVParserResult());
			showParserResultAndLingTree(cvWord.getCVPredictedSyllabification(), cvWord.getCVParserResult(),
					cvWord.getCVLingTreeDescription());
			setNodeOrientationOnFields();
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			commentField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (cvWord != null) {
			ObservableList<Word> predictedWords = wordsTable.getItems().filtered(
					w -> !StringUtilities.isNullOrEmpty(w.getCVPredictedSyllabification()));
			ObservableList<Word> predictedEqualsCorrectWords = wordsTable.getItems().filtered(
					w -> !StringUtilities.isNullOrEmpty(w.getCVPredictedSyllabification())
							&& w.getCVPredictedSyllabification().equals(
									w.getCorrectSyllabification()));
			int currentItem = updateStatusBarWords(predictedWords, predictedEqualsCorrectWords);
			mainApp.getApplicationPreferences().setLastCVWordsViewItemUsed(currentItem);
		}
	}

	public TableView<Word> getCVWordsTable() {
		return wordsTable;
	}
}
