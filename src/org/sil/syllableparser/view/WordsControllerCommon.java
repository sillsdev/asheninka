// Copyright (c) 2018-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.LingTreeInteractor;
import org.sil.utility.StringUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author Andy Black
 *
 */

public class WordsControllerCommon extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<Word, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class VernacularWrappingTableCell extends TableCell<Word, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processVernacularTableCell(this, text, item, empty);
		}
	}

	@FXML
	protected TableView<Word> wordsTable;
	@FXML
	protected TableColumn<Word, String> wordColumn;
	@FXML
	protected TableColumn<Word, String> predictedSyllabificationColumn;
	@FXML
	protected TableColumn<Word, String> correctSyllabificationColumn;
	@FXML
	protected TableColumn<Word, String> parserResultColumn;
	@FXML
	protected TableColumn<Word, String> commentColumn;

	@FXML
	protected TextField wordField;
	@FXML
	protected TextField commentField;
	@FXML
	protected TextField predictedSyllabificationField;
	@FXML
	protected TextField correctSyllabificationField;
	@FXML
	protected TextField parserResultField;
	@FXML
	protected WebView parserLingTreeSVG;
	@FXML
	protected WebEngine webEngine;

	protected ObservableList<Word> words = FXCollections.observableArrayList();

	protected Word currentWord;
	protected LingTreeInteractor ltInteractor;
	protected String ltSVG = "";
	public WordsControllerCommon() {
		ltInteractor = LingTreeInteractor.getInstance();
	}

	public void setWordsTable(TableView<Word> tableView) {
		wordsTable = tableView;
		this.tableView = tableView;
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		this.bundle = resources;
		webEngine = parserLingTreeSVG.getEngine();

		// Initialize the table with the columns.
		wordColumn.setCellValueFactory(cellData -> cellData.getValue().wordProperty());
		correctSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.correctSyllabificationProperty());
		commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());

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
		commentColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		// Without the following, the columns get set to equal values, more or less
		// With it, the widths remain as stored in the preferences
		//wordsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		makeColumnHeaderWrappable(wordColumn);
		makeColumnHeaderWrappable(predictedSyllabificationColumn);
		makeColumnHeaderWrappable(correctSyllabificationColumn);
		makeColumnHeaderWrappable(commentColumn);
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
				String sVernacular = mainApp.getStyleFromColor(languageProject.getVernacularLanguage().getColor());
				wordField.setStyle(sVernacular);
				predictedSyllabificationField.setStyle(sVernacular);
				correctSyllabificationField.setStyle(sVernacular);
				String sAnalysis = mainApp.getStyleFromColor(languageProject.getAnalysisLanguage().getColor());
				commentField.setStyle(sAnalysis);
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
		commentField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentWord != null) {
				currentWord.setComment(commentField.getText());
			}
			if (languageProject != null) {
				commentField.setFont(languageProject.getAnalysisLanguage().getFont());
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
		commentField.setText(cvWord.getComment());
		parserResultField.setText(cvWord.getCVParserResult());
	}

	protected void setNodeOrientationOnFields() {
		NodeOrientation vernacularOrientation = languageProject.getVernacularLanguage()
				.getOrientation();
		NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
				.getOrientation();
		commentField.setNodeOrientation(analysisOrientation);
		correctSyllabificationField.setNodeOrientation(vernacularOrientation);
		predictedSyllabificationField.setNodeOrientation(vernacularOrientation);
		wordField.setNodeOrientation(vernacularOrientation);
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

	protected void showLingTreeSVG() {
		ltInteractor.initializeParameters(languageProject);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>");
		sb.append("<body><div style=\"text-align:left\">");
		sb.append(ltSVG);
		sb.append("</div></body></html>");
		webEngine.loadContent(sb.toString());
	}

	@Override
	void handleInsertNewItem() {
		Word newWord = new Word();
		newWord.setCVParserResult(bundle.getString("label.untested"));
		newWord.setSHParserResult(bundle.getString("label.untested"));
		newWord.setONCParserResult(bundle.getString("label.untested"));
		words.add(newWord);
		handleInsertNewItem(words, wordsTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(words, currentWord, wordsTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(words, currentWord, wordsTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(words, currentWord, wordsTable);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { wordField, predictedSyllabificationField,
				correctSyllabificationField, parserResultField, commentField };
	}

	protected void showParserResultAndLingTree(String sPredictedSyllabification, String sParserResult, String sLingTreeDescription) {
		boolean fSuccess;
		if (sPredictedSyllabification.length() == 0
				&& sParserResult.length() > 0) {
			parserResultField.getStyleClass().add("failedsyllabification");
			fSuccess = false;
		} else {
			parserResultField.getStyleClass().add("successfullsyllabification");
			fSuccess = true;
		}
		if (sLingTreeDescription.length() == 0
				|| sLingTreeDescription.equals("(W)")) {
			ltSVG = "";
		} else {
			ltInteractor.initializeParameters(languageProject);
			ltSVG = ltInteractor.createSVG(sLingTreeDescription, fSuccess);
		}
		showLingTreeSVG();
	}

	protected void setParserResultFieldColor(String sParserResult) {
		String sSuccess = bundle.getString("label.success");
		if (sParserResult.equals(sSuccess)) {
			parserResultField.setStyle(Constants.TEXT_COLOR_CSS_BEGIN
					+ Constants.PARSER_SUCCESS_COLOR_STRING + Constants.TEXT_COLOR_CSS_END);
		} else {
			parserResultField.setStyle(Constants.TEXT_COLOR_CSS_BEGIN
					+ Constants.PARSER_FAILURE_COLOR_STRING + Constants.TEXT_COLOR_CSS_END);
		}
	}

	public int updateStatusBarWords(ObservableList<Word> predictedWords, ObservableList<Word> predictedEqualsCorrectWords) {
		int iTotalWords = wordsTable.getItems().size();
		int iPredicted = predictedWords.size();
		int iPredictedEqualsCorrect = predictedEqualsCorrectWords.size();
		int currentItem = wordsTable.getItems().indexOf(currentWord);
		String sPredictedToTotal = "";
		String sPredictedEqualsCorrectToTotal = "";
		String sNumberOfItems = (currentItem + 1) + "/" + iTotalWords + " ";
		if (iTotalWords > 0) {
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMaximumFractionDigits(2);
			nf.setRoundingMode(RoundingMode.HALF_UP);
			double predictedPercent = ((double) iPredicted / (double) iTotalWords);
			sPredictedToTotal = iPredicted + "/" + iTotalWords + " ("
					+ nf.format(predictedPercent) + ") ";
			double predictedEqualsCorrectPercent = ((double) iPredictedEqualsCorrect / (double) iTotalWords);
			sPredictedEqualsCorrectToTotal = " " + iPredictedEqualsCorrect + "/" + iTotalWords + " ("
					+ nf.format(predictedEqualsCorrectPercent) + ") ";
		}
		this.mainApp.updateStatusBarWordItems(sPredictedToTotal, sPredictedEqualsCorrectToTotal, sNumberOfItems);
		return currentItem;
	}
}
