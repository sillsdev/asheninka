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

public class WordsControllerCommon extends SylParserBaseController implements Initializable {

	protected final class VernacularWrappingTableCell extends TableCell<Word, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				text.setFont(languageProject.getVernacularLanguage().getFont());
				setGraphic(text);
			}
		}
	}

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

	@FXML
	protected TableView<Word> wordsTable;
	@FXML
	private TableColumn<Word, String> wordColumn;
	@FXML
	protected TableColumn<Word, String> predictedSyllabificationColumn;
	@FXML
	private TableColumn<Word, String> correctSyllabificationColumn;
	@FXML
	protected TableColumn<Word, String> parserResultColumn;

	@FXML
	protected TextField wordField;
	@FXML
	protected TextField predictedSyllabificationField;
	@FXML
	protected TextField correctSyllabificationField;
	@FXML
	protected TextField parserResultField;

	protected ObservableList<Word> words = FXCollections.observableArrayList();

	protected Word currentWord;

	public WordsControllerCommon() {

	}

	public void setWordsTable(TableView<Word> tableView) {
		wordsTable = tableView;
	}
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// public void initialize() {
		this.bundle = resources;

		// Initialize the table with the three columns.
		wordColumn.setCellValueFactory(cellData -> cellData.getValue().wordProperty());
		correctSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.correctSyllabificationProperty());

		// Custom rendering of the table cell.
		wordColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		predictedSyllabificationColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		correctSyllabificationColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		parserResultColumn.setCellFactory(column -> {
			return new ParserResultWrappingTableCell();
		});

		makeColumnHeaderWrappable(wordColumn);
		makeColumnHeaderWrappable(predictedSyllabificationColumn);
		makeColumnHeaderWrappable(correctSyllabificationColumn);
		// for some reason, the following makes the header very high
		// when we also use cvWords.Table.scrollTo(index) in
		// setFocusOnWord(index)
		// makeColumnHeaderWrappable(parserResultColumn);

		// Handle TextField text changes.
		wordField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentWord != null) {
				currentWord.setWord(wordField.getText());
			}
			if (languageProject != null) {
				wordField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
		correctSyllabificationField.textProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							if (currentWord != null) {
								currentWord.setCorrectSyllabification(correctSyllabificationField
										.getText());
							}
							if (languageProject != null) {
								correctSyllabificationField.setFont(languageProject
										.getVernacularLanguage().getFont());
							}
						});

		// not so happy with making it smaller
		// parserResultColumn.getStyleClass().add("syllabification-result");
		// parserResultField.getStyleClass().add("syllabification-result");

		// Use of Enter move focus to next item.
		wordField.setOnAction((event) -> {
			predictedSyllabificationField.requestFocus();
		});
		predictedSyllabificationField.setOnAction((event) -> {
			correctSyllabificationField.requestFocus();
		});

		wordField.requestFocus();

	}

	@Override
	public void setViewItemUsed(int value) {
		int max = wordsTable.getItems().size();
		value = adjustIndexValue(value, max);
		wordsTable.getSelectionModel().clearAndSelect(value);
	}

	public void setWord(Word cvWord) {
		wordField.setText(cvWord.getWord());
		predictedSyllabificationField.setText(cvWord.getCVPredictedSyllabification());
		correctSyllabificationField.setText(cvWord.getCorrectSyllabification());
		parserResultField.setText(cvWord.getCVParserResult());
	}

	public void setFocusOnWord(int index) {
		if (wordsTable.getItems().size() > 0 && index > -1
				&& index < wordsTable.getItems().size()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					wordsTable.requestFocus();
					wordsTable.getSelectionModel().select(index);
					wordsTable.getFocusModel().focus(index);
					wordsTable.scrollTo(index);
				}
			});
		}
	}

	public TableView<Word> getCVWordsTable() {
		return wordsTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		Word newWord = new Word();
		newWord.setCVParserResult(bundle.getString("label.untested"));
		words.add(newWord);
		int i = words.size() - 1;
		wordsTable.requestFocus();
		wordsTable.getSelectionModel().select(i);
		wordsTable.getFocusModel().focus(i);
		wordsTable.scrollTo(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = words.indexOf(currentWord);
		currentWord = null;
		if (i >= 0) {
			words.remove(i);
			int max = wordsTable.getItems().size();
			i = adjustIndexValue(i, max);
			// select the last one used
			wordsTable.requestFocus();
			wordsTable.getSelectionModel().select(i);
			wordsTable.getFocusModel().focus(i);
			wordsTable.scrollTo(i);
		}
		wordsTable.refresh();
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { wordField, predictedSyllabificationField,
				correctSyllabificationField, parserResultField };
	}
}
