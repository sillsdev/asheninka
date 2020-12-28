// Copyright (c) 2020 SIL International 
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
	protected TableView<Word> muWordsTable;

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
				if (word != null && word.getMuParserResult().length() > 0
						&& word.getMuPredictedSyllabification().length() == 0) {
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
		super.setWordsTable(muWordsTable);
		super.initialize(location, resources);

		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.muPredictedSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue()
				.muParserResultProperty());
		predictedSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setMuPredictedSyllabification(predictedSyllabificationField
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
					currentWord.setMuParserResult(parserResultField.getText());
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
		showMuWordDetails(null);
		// Listen for selection changes and show the details when changed.
		wordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showMuWordDetails(newValue));
	}
	
	public void setData(MoraicApproach muApproachData, ObservableList<Word> words) {
		muApproach = muApproachData;
		languageProject = muApproach.getLanguageProject();
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
				w -> !StringUtilities.isNullOrEmpty(w.getMuPredictedSyllabification()));
	}

	public ObservableList<Word> getPredictedEqualsCorrectWords() {
		return wordsTable.getItems()
				.filtered(
						w -> !StringUtilities.isNullOrEmpty(w.getMuPredictedSyllabification())
								&& w.getMuPredictedSyllabification().equals(
										w.getCorrectSyllabification()));
	}

	/**
	 * Fills all text fields to show details about the ONC word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param muWord
	 *            the segment or null
	 */
	private void showMuWordDetails(Word muWord) {
		currentWord = muWord;
		if (muWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(muWord.getWord());
			commentField.setText(muWord.getComment());
			predictedSyllabificationField.setText(muWord.getMuPredictedSyllabification());
			correctSyllabificationField.setText(muWord.getCorrectSyllabification());
			parserResultField.setText(muWord.getMuParserResult());
			setParserResultFieldColor(muWord.getMuParserResult());
			showParserResultAndLingTree(muWord.getMuPredictedSyllabification(), muWord.getMuParserResult(),
					muWord.getMuLingTreeDescription());
			setNodeOrientationOnFields();
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			commentField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (muWord != null) {
			int currentItem = updateStatusBarWords(getPredictedWords(), getPredictedEqualsCorrectWords());
			mainApp.getApplicationPreferences().setLastMoraicWordsViewItemUsed(currentItem);
		} else {
			updateStatusBarWords(FXCollections.observableArrayList(), FXCollections.observableArrayList());
		}
	}

	public TableView<Word> getMuWordsTable() {
		return wordsTable;
	}

	public void handleRemoveAllFilters() {
		super.handleRemoveAllFilters();
		setData(muApproach, languageProject.getWords());
	}
}
