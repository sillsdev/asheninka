// Copyright (c) 2020-2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.utility.StringUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class MoraicWordsController extends WordsControllerCommon {

	@FXML
	protected TableView<Word> moraicWordsTable;

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
				if (word != null && word.getMoraicParserResult().length() > 0
						&& word.getMoraicPredictedSyllabification().length() == 0) {
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
		super.setApproach(ApplicationPreferences.MORAIC_WORDS);
		super.setWordsTable(moraicWordsTable);
		super.initialize(location, resources);

		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.moraicPredictedSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue()
				.moraicParserResultProperty());
		predictedSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setMoraicPredictedSyllabification(predictedSyllabificationField
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
					currentWord.setMoraicParserResult(parserResultField.getText());
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
		showMoraicWordDetails(null);
		// Listen for selection changes and show the details when changed.
		wordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showMoraicWordDetails(newValue));
	}
	
	public void setData(MoraicApproach moraicApproachData, ObservableList<Word> words) {
		moraicApproach = moraicApproachData;
		languageProject = moraicApproach.getLanguageProject();
		setDataCommon(words);
		int max = wordsTable.getItems().size();
		// mainApp null test is to make some tests work
		if (max > 0 && mainApp != null) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastMoraicWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex, false);
		}
	}

	public ObservableList<Word> getPredictedWords() {
		return wordsTable.getItems().filtered(
				w -> !StringUtilities.isNullOrEmpty(w.getMoraicPredictedSyllabification()));
	}

	public ObservableList<Word> getPredictedEqualsCorrectWords() {
		return wordsTable.getItems()
				.filtered(
						w -> !StringUtilities.isNullOrEmpty(w.getMoraicPredictedSyllabification())
								&& w.getMoraicPredictedSyllabification().equals(
										w.getCorrectSyllabification()));
	}

	/**
	 * Fills all text fields to show details about the ONC word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param moraicWord
	 *            the segment or null
	 */
	private void showMoraicWordDetails(Word moraicWord) {
		currentWord = moraicWord;
		if (moraicWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(moraicWord.getWord());
			commentField.setText(moraicWord.getComment());
			predictedSyllabificationField.setText(moraicWord.getMoraicPredictedSyllabification());
			correctSyllabificationField.setText(moraicWord.getCorrectSyllabification());
			parserResultField.setText(moraicWord.getMoraicParserResult());
			setParserResultFieldColor(moraicWord.getMoraicParserResult());
			showParserResultAndLingTree(moraicWord.getMoraicPredictedSyllabification(), moraicWord.getMoraicParserResult(),
					moraicWord.getMoraicLingTreeDescription());
			setNodeOrientationOnFields();
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			commentField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (moraicWord != null) {
			int currentItem = updateStatusBarWords(getPredictedWords(), getPredictedEqualsCorrectWords());
			mainApp.getApplicationPreferences().setLastMoraicWordsViewItemUsed(currentItem);
		} else {
			updateStatusBarWords(FXCollections.observableArrayList(), FXCollections.observableArrayList());
		}
	}

	public TableView<Word> getMoraicWordsTable() {
		return wordsTable;
	}

	public void handleRemoveAllFilters() {
		super.handleRemoveAllFilters();
		setData(moraicApproach, languageProject.getWords());
	}
}
