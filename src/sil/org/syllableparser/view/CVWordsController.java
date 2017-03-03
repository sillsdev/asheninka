// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
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

public class CVWordsController extends SylParserBaseController implements Initializable {

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
	private TableView<Word> cvWordsTable;
	@FXML
	private TableColumn<Word, String> wordColumn;
	@FXML
	private TableColumn<Word, String> predictedSyllabificationColumn;
	@FXML
	private TableColumn<Word, String> correctSyllabificationColumn;
	@FXML
	private TableColumn<Word, String> parserResultColumn;

	@FXML
	private TextField wordField;
	@FXML
	private TextField predictedSyllabificationField;
	@FXML
	private TextField correctSyllabificationField;
	@FXML
	private TextField parserResultField;

	private ObservableList<Word> words = FXCollections.observableArrayList();

	private Word currentWord;

	public CVWordsController() {

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
		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvPredictedSyllabificationProperty());
		correctSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.correctSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvParserResultProperty());

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

		// Clear cv word details.
		showCVWordDetails(null);

		// Listen for selection changes and show the details when changed.
		cvWordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showCVWordDetails(newValue));

		// Handle TextField text changes.
		wordField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentWord != null) {
				currentWord.setWord(wordField.getText());
			}
			if (languageProject != null) {
				wordField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
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
			predictedSyllabificationField.setText(cvWord.getCVPredictedSyllabification());
			correctSyllabificationField.setText(cvWord.getCorrectSyllabification());
			parserResultField.setText(cvWord.getCVParserResult());
			parserResultField.getStyleClass().clear();
			if (cvWord.getCVPredictedSyllabification().length() == 0
					&& cvWord.getCVParserResult().length() > 0) {
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

		if (cvWord != null) {
			int currentItem = cvWordsTable.getItems().indexOf(currentWord);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ cvWordsTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastCVWordsViewItemUsed(currentItem);
		}
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = cvWordsTable.getItems().size();
		value = adjustIndexValue(value, max);
		cvWordsTable.getSelectionModel().clearAndSelect(value);
	}

	public void setWord(Word cvWord) {
		wordField.setText(cvWord.getWord());
		predictedSyllabificationField.setText(cvWord.getCVPredictedSyllabification());
		correctSyllabificationField.setText(cvWord.getCorrectSyllabification());
		parserResultField.setText(cvWord.getCVParserResult());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param words
	 *            TODO
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData, ObservableList<Word> words) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		this.words = words;

		// Add observable list data to the table
		cvWordsTable.setItems(words);
		int max = cvWordsTable.getItems().size();
		if (max > 0) {
			int iLastIndex = mainApp.getApplicationPreferences().getLastCVWordsViewItemUsed();
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		}
	}

	public void setFocusOnWord(int index) {
		if (cvWordsTable.getItems().size() > 0 && index > -1
				&& index < cvWordsTable.getItems().size()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					cvWordsTable.requestFocus();
					cvWordsTable.getSelectionModel().select(index);
					cvWordsTable.getFocusModel().focus(index);
					cvWordsTable.scrollTo(index);
				}
			});
		}
	}

	public TableView<Word> getCVWordsTable() {
		return cvWordsTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		Word newWord = new Word();
		newWord.setCVParserResult(bundle.getString("label.untested"));
		words.add(newWord);
		int i = words.size() - 1;
		cvWordsTable.requestFocus();
		cvWordsTable.getSelectionModel().select(i);
		cvWordsTable.getFocusModel().focus(i);
		cvWordsTable.scrollTo(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = words.indexOf(currentWord);
		currentWord = null;
		if (i >= 0) {
			words.remove(i);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { wordField, predictedSyllabificationField,
				correctSyllabificationField, parserResultField };
	}
}
