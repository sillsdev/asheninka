// Copyright (c) 2018-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.LingTreeInteractor;
import org.sil.syllableparser.service.filter.ColumnFilterType;
import org.sil.syllableparser.service.filter.WordsFilter;
import org.sil.syllableparser.service.filter.WordsFilterType;
import org.sil.utility.service.keyboards.KeyboardChanger;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

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
	@FXML
	MenuItem miFilterWords;
	@FXML
	MenuItem miRemoveAllFiltersWords;
	@FXML
	MenuItem miRemoveFiltersWords;
	@FXML
	MenuItem miRemoveAllFiltersPredicted;
	@FXML
	MenuItem miRemoveFiltersPredicted;
	@FXML
	MenuItem miRemoveAllFiltersCorrect;
	@FXML
	MenuItem miRemoveFiltersCorrect;
	@FXML
	MenuItem miFilterPredictedSyllabifications;
	@FXML
	MenuItem miFilterCorrectSyllabifications;
	@FXML
	protected ContextMenu filterCorrectSyllableBreaksContextMenu = new ContextMenu();
	@FXML
	protected ContextMenu filterPredictedSyllableBreaksContextMenu = new ContextMenu();
	@FXML
	protected ContextMenu filterWordsContextMenu = new ContextMenu();


	protected ObservableList<Word> words = FXCollections.observableArrayList();
	protected FilteredList<Word> filteredWords;
	protected FilteredList<Word> filteredPredictedWords;
	protected FilteredList<Word> filteredCorrectWords;

	protected Word currentWord;
	protected LingTreeInteractor ltInteractor;
	protected String ltSVG = "";
	protected WordsFilter filterCorrectSyllableBreaks = new WordsFilter();
	protected WordsFilter filterPredictedSyllableBreaks = new WordsFilter();
	protected WordsFilter filterWords = new WordsFilter();
	ObservableList<Word> wordsToFilter;

	public WordsControllerCommon() {
		ltInteractor = LingTreeInteractor.getInstance();
		filterWords.setWordsFilterType(getWordsFilterType());
		filterPredictedSyllableBreaks.setWordsFilterType(getPredictedWordsFilterType());
		filterCorrectSyllableBreaks.setWordsFilterType(getCorrectWordsFilterType());
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

		// Without the following, the columns get set to equal values, more or
		// less
		// With it, the widths remain as stored in the preferences
		// wordsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		makeColumnHeaderWrappable(wordColumn);
		makeColumnHeaderWrappable(predictedSyllabificationColumn);
		makeColumnHeaderWrappable(correctSyllabificationColumn);
		makeColumnHeaderWrappable(commentColumn);
		// for some reason, the following makes the header very high
		// when we also use cvWords.Table.scrollTo(index) in
		// setFocusOnWord(index)
		// makeColumnHeaderWrappable(parserResultColumn);
		initializeContextMenus();

		keyboardChanger = KeyboardChanger.getInstance();
		// Handle TextField text changes.
		wordField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setWord(wordField.getText());
					}
					if (languageProject != null) {
						wordField.setFont(languageProject.getVernacularLanguage().getFont());
						String sVernacular = mainApp.getStyleFromColor(languageProject
								.getVernacularLanguage().getColor());
						wordField.setStyle(sVernacular);
						predictedSyllabificationField.setStyle(sVernacular);
						correctSyllabificationField.setStyle(sVernacular);
						String sAnalysis = mainApp.getStyleFromColor(languageProject
								.getAnalysisLanguage().getColor());
						commentField.setStyle(sAnalysis);
					}
				});
		wordField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				keyboardChanger.tryToChangeKeyboardTo(languageProject.getVernacularLanguage().getKeyboard(), MainApp.class);
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
		correctSyllabificationField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				keyboardChanger.tryToChangeKeyboardTo(languageProject.getVernacularLanguage().getKeyboard(), MainApp.class);
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
		commentField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				keyboardChanger.tryToChangeKeyboardTo(languageProject.getAnalysisLanguage().getKeyboard(), MainApp.class);
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

	protected void initializeContextMenus() {
		miFilterWords = new MenuItem(bundle.getString("menu.filterwords"));
		miFilterWords.setOnAction((event) -> {
			handleFilterWords();
		});
		miRemoveFiltersWords = new MenuItem(bundle.getString("menu.filterremovefilterwords"));
		miRemoveFiltersWords.setDisable(true);
		miRemoveFiltersWords.setOnAction((event) -> {
			handleRemoveFiltersWord();
		});
		miRemoveAllFiltersWords = new MenuItem(bundle.getString("menu.filterremoveallfilters"));
		miRemoveAllFiltersWords.setOnAction((event) -> {
			handleRemoveAllFilters();
		});

		miFilterPredictedSyllabifications = new MenuItem(bundle.getString("menu.filterpredictedwords"));
		miFilterPredictedSyllabifications.setOnAction((event) -> {
			handleFilterPredictedSyllabifications();
		});
		miRemoveFiltersPredicted = new MenuItem(bundle.getString("menu.filterremovefilterpredictedwords"));
		miRemoveFiltersPredicted.setDisable(true);
		miRemoveFiltersPredicted.setOnAction((event) -> {
			handleRemoveFiltersPredicted();
		});
		miRemoveAllFiltersPredicted = new MenuItem(bundle.getString("menu.filterremoveallfilters"));
		miRemoveAllFiltersPredicted.setOnAction((event) -> {
			handleRemoveAllFilters();;
		});

		miFilterCorrectSyllabifications = new MenuItem(bundle.getString("menu.filtercorrectwords"));
		miFilterCorrectSyllabifications.setOnAction((event) -> {
			handleFilterCorrectSyllabifications();
		});
		miRemoveFiltersCorrect = new MenuItem(bundle.getString("menu.filterremovefiltercorrectwords"));
		miRemoveFiltersCorrect.setDisable(true);
		miRemoveFiltersCorrect.setOnAction((event) -> {
			handleRemoveFiltersCorrect();
		});
		miRemoveAllFiltersCorrect = new MenuItem(bundle.getString("menu.filterremoveallfilters"));
		miRemoveAllFiltersCorrect.setOnAction((event) -> {
			handleRemoveAllFilters();;
		});

		filterWordsContextMenu.getItems().addAll(miFilterWords, miRemoveFiltersWords,
				new SeparatorMenuItem(), miRemoveAllFiltersWords);
		wordColumn.setContextMenu(filterWordsContextMenu);

		filterPredictedSyllableBreaksContextMenu.getItems().addAll(
				miFilterPredictedSyllabifications, miRemoveFiltersPredicted,
				new SeparatorMenuItem(), miRemoveAllFiltersPredicted);
		predictedSyllabificationColumn.setContextMenu(filterPredictedSyllableBreaksContextMenu);

		filterCorrectSyllableBreaksContextMenu.getItems().addAll(miFilterCorrectSyllabifications,
				miRemoveFiltersCorrect, new SeparatorMenuItem(), miRemoveAllFiltersCorrect);
		correctSyllabificationColumn.setContextMenu(filterCorrectSyllableBreaksContextMenu);
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

	public void setFocusOnWord(int index, boolean fCheckFilters) {
		if (fCheckFilters
				&& (filterWords.isActive() || filterPredictedSyllableBreaks.isActive() || filterCorrectSyllableBreaks
						.isActive())) {
			if (index > -1 && index < words.size()) {
				Word w = words.get(index);
				if (wordsTable.getItems().contains(w)) {
					index = wordsTable.getItems().indexOf(w);
				} else {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(MainApp.kApplicationTitle);
					alert.setHeaderText(bundle.getString("label.wordhiddenbyfilter").replace("{0}", w.getWord()));
					alert.setContentText(bundle
							.getString("label.wordhiddenbyfiltersoremoveallfilters"));
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(mainApp.getNewMainIconImage());
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						handleRemoveAllFilters();
					} else {
						// The user chose not to remove filters so quit
						// Otherwise we may change position in the table
						return;
					}
				}
			}
		}
		final int i = index;
		if (wordsTable.getItems().size() > 0 && i > -1 && i < wordsTable.getItems().size()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					wordsTable.requestFocus();
					wordsTable.getSelectionModel().select(i);
					wordsTable.getFocusModel().focus(i);
					wordsTable.scrollTo(i);
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

	public void handleFilterWords() {
		filterWords = processFilter("label.filterwords", getWordsFilterType(), wordColumn, filterWords);
		applyWordFilters();
	}

	public void handleFilterPredictedSyllabifications() {
		WordsFilterType wordsFilterType = getPredictedWordsFilterType();
		filterPredictedSyllableBreaks = processFilter("label.filterpredictedsyllablebreaks",
				wordsFilterType, predictedSyllabificationColumn, filterPredictedSyllableBreaks);
		applyWordFilters();
	}

	protected WordsFilterType getCorrectWordsFilterType() {
		WordsFilterType wordsFilterType = WordsFilterType.CV_CORRECT;
		switch (this.getClass().getSimpleName()) {
		case "ONCWordsController":
			wordsFilterType = WordsFilterType.ONC_CORRECT;
			break;
		case "SHWordsController":
			wordsFilterType = WordsFilterType.SH_CORRECT;
		}
		return wordsFilterType;
	}

	protected WordsFilterType getPredictedWordsFilterType() {
		WordsFilterType wordsFilterType = WordsFilterType.CV_PREDICTED;
		switch (this.getClass().getSimpleName()) {
		case "ONCWordsController":
			wordsFilterType = WordsFilterType.ONC_PREDICTED;
			break;
		case "SHWordsController":
			wordsFilterType = WordsFilterType.SH_PREDICTED;
		}
		return wordsFilterType;
	}

	protected WordsFilterType getWordsFilterType() {
		WordsFilterType wordsFilterType = WordsFilterType.CV_WORDS;
		switch (this.getClass().getSimpleName()) {
		case "ONCWordsController":
			wordsFilterType = WordsFilterType.ONC_WORDS;
			break;
		case "SHWordsController":
			wordsFilterType = WordsFilterType.SH_WORDS;
		}
		return wordsFilterType;
	}

	public void handleFilterCorrectSyllabifications() {
		filterCorrectSyllableBreaks = processFilter("label.filtercorrectsyllablebreaks",
				getCorrectWordsFilterType(), correctSyllabificationColumn, filterCorrectSyllableBreaks);
		applyWordFilters();
	}

	protected WordsFilter processFilter(String sTitle, WordsFilterType wordsFilterType,
			TableColumn<Word, String> column, WordsFilter filter) {
		try {
			Stage filterWordDialogStage = new Stage();
			String resource = "fxml/ColumnFilterDialog.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale,
					filterWordDialogStage, bundle.getString(sTitle),
					ApproachViewNavigator.class.getResource(resource), bundle);

			ColumnFilterController controller = loader.getController();
			controller.setDialogStage(filterWordDialogStage);
			controller.setWordsFilterType(wordsFilterType);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setFilter(filter);
			controller.setWords(wordsTable.getItems());
			filterWordDialogStage.showAndWait();
			if (controller.isResultIsOK()) {
				controller.getFilter().setActive(true);
				return controller.getFilter();
			}
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
		return filter;
	}

	public void applyWordFilters() {
		wordsToFilter = languageProject.getWords();
		boolean someColumnIsFiltered = false;
		someColumnIsFiltered = applyWordFilter(someColumnIsFiltered, filterWords, wordColumn, miRemoveFiltersWords);
		someColumnIsFiltered = applyWordFilter(someColumnIsFiltered, filterPredictedSyllableBreaks, predictedSyllabificationColumn, miRemoveFiltersPredicted);
		someColumnIsFiltered = applyWordFilter(someColumnIsFiltered, filterCorrectSyllableBreaks, correctSyllabificationColumn, miRemoveFiltersCorrect);
		if (someColumnIsFiltered) {
			filteredWords = new FilteredList<>(wordsToFilter, p -> true);
			SortedList<Word> sortedWords = new SortedList<>(filteredWords);
			sortedWords.comparatorProperty().bind(wordsTable.comparatorProperty());
			wordsTable.setItems(sortedWords);
			int max = wordsTable.getItems().size();
			if (max > 0) {
				int indexToStartWith = adjustIndexValue(0, max);
				setFocusOnWord(indexToStartWith, false);
			}
		} else {
			wordsTable.setItems(wordsToFilter);
		}
		wordsTable.refresh();
	}

	protected boolean applyWordFilter(boolean someColumnIsFiltered, WordsFilter wordsFilter,
			TableColumn<Word, String> column, MenuItem miRemove) {
		if (wordsFilter.isActive()) {
			wordsToFilter = wordsFilter.applyFilter(wordsToFilter);
			column.setStyle("-fx-background-color:yellow");
			someColumnIsFiltered = true;
			miRemove.setDisable(false);
		} else {
			miRemove.setDisable(true);
		}
		return someColumnIsFiltered;
	}

	protected void handleRemoveAllFilters() {
		handleRemoveFiltersWord();
		handleRemoveFiltersPredicted();
		handleRemoveFiltersCorrect();
	}

	protected void handleRemoveFiltersCorrect() {
		correctSyllabificationColumn.setStyle("");
		filterCorrectSyllableBreaks.setActive(false);
		WordsFilterType wft = getCorrectWordsFilterType();
		prefs.setPreferencesKey(ColumnFilterController.getAppPreferencesBaseToUse(wft)
				+ ApplicationPreferences.FILTER_ACTIVE, false);
		applyWordFilters();
	}

	protected void handleRemoveFiltersPredicted() {
		predictedSyllabificationColumn.setStyle("");
		filterPredictedSyllableBreaks.setActive(false);
		WordsFilterType wft = getPredictedWordsFilterType();
		prefs.setPreferencesKey(ColumnFilterController.getAppPreferencesBaseToUse(wft)
				+ ApplicationPreferences.FILTER_ACTIVE, false);
		applyWordFilters();
	}

	protected void handleRemoveFiltersWord() {
		wordColumn.setStyle("");
		filterWords.setActive(false);
		WordsFilterType wft = getWordsFilterType();
		prefs.setPreferencesKey(ColumnFilterController.getAppPreferencesBaseToUse(wft)
				+ ApplicationPreferences.FILTER_ACTIVE, false);
		applyWordFilters();
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

	protected void showParserResultAndLingTree(String sPredictedSyllabification,
			String sParserResult, String sLingTreeDescription) {
		boolean fSuccess;
		if (sPredictedSyllabification.length() == 0 && sParserResult.length() > 0) {
			parserResultField.getStyleClass().add("failedsyllabification");
			fSuccess = false;
		} else {
			parserResultField.getStyleClass().add("successfullsyllabification");
			fSuccess = true;
		}
		if (sLingTreeDescription.length() == 0 || sLingTreeDescription.equals("(W)")) {
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

	public int updateStatusBarWords(ObservableList<Word> predictedWords,
			ObservableList<Word> predictedEqualsCorrectWords) {
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
			sPredictedToTotal = iPredicted + "/" + iTotalWords + " (" + nf.format(predictedPercent)
					+ ") ";
			double predictedEqualsCorrectPercent = ((double) iPredictedEqualsCorrect / (double) iTotalWords);
			sPredictedEqualsCorrectToTotal = " " + iPredictedEqualsCorrect + "/" + iTotalWords
					+ " (" + nf.format(predictedEqualsCorrectPercent) + ") ";
		}
		if (mainApp != null) {
		this.mainApp.updateStatusBarWordItems(sPredictedToTotal, sPredictedEqualsCorrectToTotal,
				sNumberOfItems);
		}
		return currentItem;
	}

	protected void initWordFilters() {
		initWordsFilter(ColumnFilterController.getAppPreferencesBaseToUse(getWordsFilterType()), filterWords);
		initWordsFilter(ColumnFilterController.getAppPreferencesBaseToUse(getPredictedWordsFilterType()), filterPredictedSyllableBreaks);
		initWordsFilter(ColumnFilterController.getAppPreferencesBaseToUse(getCorrectWordsFilterType()), filterCorrectSyllableBreaks);
	}

	@SuppressWarnings("static-access")
	protected void initWordsFilter(String sAppPreferencesBase, WordsFilter filter) {
		filter.setActive(prefs.getBooleanValue(sAppPreferencesBase + prefs.FILTER_ACTIVE, false));
		ColumnFilterType columnFilterType = ColumnFilterController.getColumnFilterType(prefs, sAppPreferencesBase + prefs.FILTER_SEARCH_TYPE);
		filter.setFilterType(columnFilterType);
		filter.setMatchCase(prefs.getBooleanValue(sAppPreferencesBase + prefs.FILTER_MATCH_CASE, false));
		filter.setMatchDiacritics(prefs.getBooleanValue(sAppPreferencesBase + prefs.FILTER_MATCH_DIACRITICS, false));
		filter.setTextToMatch(prefs.getStringValue(sAppPreferencesBase + prefs.FILTER_SEARCH_TEXT, ""));
	}

	protected void setDataCommon(ObservableList<Word> words) {
		setColumnICURules(wordColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(commentColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		setColumnICURules(predictedSyllabificationColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(correctSyllabificationColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(parserResultColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		this.words = words;
		if (mainApp != null) {
			prefs = mainApp.getApplicationPreferences();
			initWordFilters();
			applyWordFilters();
		}

		// Add observable list data to the table
		wordsTable.setItems(words);
	}
}
