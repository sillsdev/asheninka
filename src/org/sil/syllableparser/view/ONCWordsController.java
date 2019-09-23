// Copyright (c) 2019 SIL International 
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
		this.words = words;

		// Add observable list data to the table
		wordsTable.setItems(words);
		int max = wordsTable.getItems().size();
		if (max > 0) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastONCWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		}
	}
	
	/**
	 * Fills all text fields to show details about the CV word. If the specified
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
			predictedSyllabificationField.setText(oncWord.getONCPredictedSyllabification());
			correctSyllabificationField.setText(oncWord.getCorrectSyllabification());
			parserResultField.setText(oncWord.getONCParserResult());
			parserResultField.getStyleClass().clear();
			boolean fSuccess;
			if (oncWord.getONCPredictedSyllabification().length() == 0
					&& oncWord.getONCParserResult().length() > 0) {
				parserResultField.getStyleClass().add("failedsyllabification");
				fSuccess = false;
			} else {
				parserResultField.getStyleClass().add("successfullsyllabification");
				fSuccess = true;
			}
			if (oncWord.getONCLingTreeDescription().length() == 0
					|| oncWord.getONCLingTreeDescription().equals("(W)")) {
				ltSVG = "";
			} else {
				ltInteractor.initializeParameters(languageProject);
				ltSVG = ltInteractor.createSVG(oncWord.getONCLingTreeDescription(), fSuccess);
			}
			showLingTreeSVG();
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (oncWord != null) {
			int currentItem = wordsTable.getItems().indexOf(currentWord);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ wordsTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastONCWordsViewItemUsed(currentItem);
		}
	}

	public TableView<Word> getONCWordsTable() {
		return wordsTable;
	}
}
