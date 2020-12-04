// Copyright (c) 2018-2020 SIL International 
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
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
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

public class SHWordsController extends WordsControllerCommon {

	@FXML
	protected TableView<Word> shWordsTable;

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
				if (word != null && word.getSHParserResult().length() > 0
						&& word.getSHPredictedSyllabification().length() == 0) {
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
		super.setApproach(ApplicationPreferences.SH_WORDS);
		super.setWordsTable(shWordsTable);
		super.initialize(location, resources);
		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

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
		setDataCommon(words);
		int max = wordsTable.getItems().size();
		if (max > 0) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastSHWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		}
	}

	public ObservableList<Word> getPredictedWords() {
		return wordsTable.getItems().filtered(
				w -> !StringUtilities.isNullOrEmpty(w.getSHPredictedSyllabification()));
	}

	public ObservableList<Word> getPredictedEqualsCorrectWords() {
		return wordsTable.getItems()
				.filtered(
						w -> !StringUtilities.isNullOrEmpty(w.getSHPredictedSyllabification())
								&& w.getSHPredictedSyllabification().equals(
										w.getCorrectSyllabification()));
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
			commentField.setText(shWord.getComment());
			predictedSyllabificationField.setText(shWord.getSHPredictedSyllabification());
			correctSyllabificationField.setText(shWord.getCorrectSyllabification());
			parserResultField.setText(shWord.getSHParserResult());
			setParserResultFieldColor(shWord.getSHParserResult());
			showParserResultAndLingTree(shWord.getSHPredictedSyllabification(), shWord.getSHParserResult(),
					shWord.getSHLingTreeDescription());
			setNodeOrientationOnFields();
		} else {
			// Word is null, remove all the text.
			wordField.setText("");
			commentField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (shWord != null) {
			int currentItem = updateStatusBarWords(getPredictedWords(), getPredictedEqualsCorrectWords());
			mainApp.getApplicationPreferences().setLastSHWordsViewItemUsed(currentItem);
		} else {
			updateStatusBarWords(FXCollections.observableArrayList(), FXCollections.observableArrayList());
		}
	}

	public TableView<Word> getSHWordsTable() {
		return wordsTable;
	}

	public void handleRemoveAllFilters() {
		super.handleRemoveAllFilters();
		setData(shApproach, languageProject.getWords());
	}
}
