// Copyright (c) 2019-2020 SIL International 
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
import org.sil.syllableparser.model.oncapproach.ONCApproach;
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

public class ONCWordsController extends WordsControllerCommon {

	@FXML
	protected TableView<Word> oncWordsTable;

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
				if (word != null && word.getONCParserResult().length() > 0
						&& word.getONCPredictedSyllabification().length() == 0) {
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
		super.setWordsTable(oncWordsTable);
		super.initialize(location, resources);

		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.oncPredictedSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue()
				.oncParserResultProperty());
		predictedSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setONCPredictedSyllabification(predictedSyllabificationField
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
					currentWord.setONCParserResult(parserResultField.getText());
				}
				if (languageProject != null) {
					parserResultField.setFont(languageProject.getAnalysisLanguage().getFont());
				}
				parserResultField.setEditable(true);
				parserResultField.positionCaret(newValue.length());
				parserResultField.setEditable(false);
			});
		});
		// Clear onc word details.
		showONCWordDetails(null);
		// Listen for selection changes and show the details when changed.
		wordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showONCWordDetails(newValue));
	}
	
	public void setData(ONCApproach oncApproachData, ObservableList<Word> words) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		setColumnICURules(wordColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(commentColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		setColumnICURules(predictedSyllabificationColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(correctSyllabificationColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(parserResultColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		this.words = words;

		// Add observable list data to the table
		wordsTable.setItems(words);
		int max = wordsTable.getItems().size();
		// mainApp null test is to make some tests work
		if (max > 0 && mainApp != null) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastONCWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		}
	}
	
	/**
	 * Fills all text fields to show details about the ONC word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param oncWord
	 *            the segment or null
	 */
	private void showONCWordDetails(Word oncWord) {
		currentWord = oncWord;
		if (oncWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(oncWord.getWord());
			commentField.setText(oncWord.getComment());
			predictedSyllabificationField.setText(oncWord.getONCPredictedSyllabification());
			correctSyllabificationField.setText(oncWord.getCorrectSyllabification());
			parserResultField.setText(oncWord.getONCParserResult());
			setParserResultFieldColor(oncWord.getONCParserResult());
			showParserResultAndLingTree(oncWord.getONCPredictedSyllabification(), oncWord.getONCParserResult(),
					oncWord.getONCLingTreeDescription());
			setNodeOrientationOnFields();
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			commentField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (oncWord != null) {
			ObservableList<Word> predictedWords = wordsTable.getItems().filtered(
					w -> !StringUtilities.isNullOrEmpty(w.getONCPredictedSyllabification()));
			ObservableList<Word> predictedEqualsCorrectWords = wordsTable.getItems().filtered(
					w -> !StringUtilities.isNullOrEmpty(w.getONCPredictedSyllabification())
							&& w.getONCPredictedSyllabification().equals(
									w.getCorrectSyllabification()));
			int currentItem = updateStatusBarWords(predictedWords, predictedEqualsCorrectWords);
			mainApp.getApplicationPreferences().setLastONCWordsViewItemUsed(currentItem);
		}
	}

	public TableView<Word> getONCWordsTable() {
		return wordsTable;
	}
}
