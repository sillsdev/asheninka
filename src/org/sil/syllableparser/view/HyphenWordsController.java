// Copyright (c) 2025-2026 SIL International 
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
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
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

public class HyphenWordsController extends WordsControllerCommon {

	@FXML
	protected TableView<Word> hyphenWordsTable;

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
				if (word != null && word.getHyphenParserResult().length() > 0
						&& word.getHyphenPredictedSyllabification().length() == 0) {
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
		super.setApproach(ApplicationPreferences.HYPHEN_WORDS);
		super.setWordsTable(hyphenWordsTable);
		super.initialize(location, resources);
		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

		predictedSyllabificationColumn
				.setCellValueFactory(cellData -> cellData.getValue().hyphenPredictedSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue().hyphenParserResultProperty());
		predictedSyllabificationField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentWord != null) {
				currentWord.setHyphenPredictedSyllabification(predictedSyllabificationField.getText());
			}
			if (languageProject != null) {
				predictedSyllabificationField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
		parserResultField.textProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> {
				if (currentWord != null) {
					currentWord.setHyphenParserResult(parserResultField.getText());
				}
				if (languageProject != null) {
					parserResultField.setFont(languageProject.getAnalysisLanguage().getFont());
				}
				parserResultField.setEditable(true);
				parserResultField.positionCaret(newValue.length());
				parserResultField.setEditable(false);
			});
		});

		parserLingTreeSVG.setVisible(false);
		// Clear hyphen word details.
		showHyphenWordDetails(null);
		// Listen for selection changes and show the details when changed.
		wordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showHyphenWordDetails(newValue));
	}

	public void setData(HyphenApproach hyphenApproachData, ObservableList<Word> words) {
		hyphenApproach = hyphenApproachData;
		languageProject = hyphenApproach.getLanguageProject();
		setDataCommon(words);
		int max = wordsTable.getItems().size();
		if (max > 0) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastHyphenWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex, false);
		}
	}

	public ObservableList<Word> getPredictedWords() {
		return wordsTable.getItems().filtered(w -> !StringUtilities.isNullOrEmpty(w.getHyphenPredictedSyllabification()));
	}

	public ObservableList<Word> getPredictedEqualsCorrectWords() {
		return wordsTable.getItems().filtered(w -> !StringUtilities.isNullOrEmpty(w.getHyphenPredictedSyllabification())
				&& w.getHyphenPredictedSyllabification().equals(w.getCorrectSyllabification()));
	}

	/**
	 * Fills all text fields to show details about the "Hyphen" word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param hyphenWord the segment or null
	 */
	private void showHyphenWordDetails(Word hyphenWord) {
		currentWord = hyphenWord;
		if (hyphenWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(hyphenWord.getWord());
			commentField.setText(hyphenWord.getComment());
			predictedSyllabificationField.setText(hyphenWord.getHyphenPredictedSyllabification());
			correctSyllabificationField.setText(hyphenWord.getCorrectSyllabification());
			parserResultField.setText(hyphenWord.getHyphenParserResult());
			setParserResultFieldColor(hyphenWord.getHyphenParserResult());
			// no tree diagram for "Hyphen" approach
//			showParserResultAndLingTree(hyphenWord.getHyphenPredictedSyllabification(),
//					hyphenWord.getHyphenParserResult(), hyphenWord.getHyphenLingTreeDescription());
			setNodeOrientationOnFields();
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			commentField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (hyphenWord != null) {
			int currentItem = updateStatusBarWords(getPredictedWords(), getPredictedEqualsCorrectWords());
			mainApp.getApplicationPreferences().setLastHyphenWordsViewItemUsed(currentItem);
		} else {
			updateStatusBarWords(FXCollections.observableArrayList(), FXCollections.observableArrayList());
		}
	}

	public TableView<Word> getHyphenWordsTable() {
		return wordsTable;
	}

	public void handleRemoveAllFilters() {
		super.handleRemoveAllFilters();
		setData(hyphenApproach, languageProject.getWords());
	}
}
