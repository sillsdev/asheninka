// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.filter.WordsFilter;
import org.sil.syllableparser.service.filter.WordsFilterType;
import org.sil.utility.view.ControllerUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */

public class WordsPredictedVsCorrectCommonController extends SylParserBaseController implements
		Initializable {

	@FXML
	protected TableView<Word> wordsPredictedVsCorrectTable;
	@FXML
	private Label whenTableIsEmptyMessage;
	@FXML
	protected TableColumn<Word, String> wordPredictedVsCorrectColumn;
	@FXML
	MenuItem miFilterWords;
	@FXML
	MenuItem miRemoveFiltersWords;
	@FXML
	protected ContextMenu filterWordsContextMenu = new ContextMenu();

	protected ObservableList<Word> words = FXCollections.observableArrayList();

	private Word currentWord;
	protected FilteredList<Word> filteredWords;
	protected SortedList<Word> sortedWords;
	protected WordsFilter filterWords = new WordsFilter();
	boolean haveAppliedFilter = false;

	public WordsPredictedVsCorrectCommonController() {

	}
	protected final class VernacularWrappingTableCell extends TableCell<Word, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			processVernacularTableCell(this, text, item, empty);
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// public void initialize() {
		this.bundle = resources;

		whenTableIsEmptyMessage = new Label(resources.getString("label.nopredictedvscorrect")
				+ resources.getString("menu.syllabifywords"));
		wordsPredictedVsCorrectTable.setPlaceholder(whenTableIsEmptyMessage);

		makeColumnHeaderWrappable(wordPredictedVsCorrectColumn);
		// Custom rendering of the table cell.
		wordPredictedVsCorrectColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		wordsPredictedVsCorrectTable.getSortOrder().add(wordPredictedVsCorrectColumn);
		miFilterWords = new MenuItem(bundle.getString("menu.filterwords"));
		miFilterWords.setOnAction((event) -> {
			handleFilterWords();
		});
		miRemoveFiltersWords = new MenuItem(bundle.getString("menu.filterremovefilterwords"));
		miRemoveFiltersWords.setDisable(true);
		miRemoveFiltersWords.setOnAction((event) -> {
			handleRemoveFiltersWord();
		});
		filterWordsContextMenu.getItems().addAll(miFilterWords, miRemoveFiltersWords);
		wordPredictedVsCorrectColumn.setContextMenu(filterWordsContextMenu);

		// Listen for selection change for status bar info
		wordsPredictedVsCorrectTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showStatusBarNumberOfItems(newValue));

	}

	protected void setWordsTable(TableView<Word> tableView) {
		wordsPredictedVsCorrectTable = tableView;
	}
	
	private void showStatusBarNumberOfItems(Word cvWord) {
		currentWord = cvWord;
		if (cvWord != null) {
			int currentItem = wordsPredictedVsCorrectTable.getItems().indexOf(currentWord);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ wordsPredictedVsCorrectTable.getItems().size() + " ");
			if (this instanceof CVWordsPredictedVsCorrectController) {
				mainApp.getApplicationPreferences().setLastCVWordsPredictedVsCorrectViewItemUsed(currentItem);
			}
			else if (this instanceof SHWordsPredictedVsCorrectController) {
				mainApp.getApplicationPreferences().setLastSHWordsPredictedVsCorrectViewItemUsed(currentItem);
			}
			else if (this instanceof ONCWordsPredictedVsCorrectController) {
				mainApp.getApplicationPreferences().setLastONCWordsPredictedVsCorrectViewItemUsed(currentItem);
			}
		}
	}

	public void setFocusOnWord(int index) {
		if (wordsPredictedVsCorrectTable.getItems().size() > 0 && index > -1
				&& index < wordsPredictedVsCorrectTable.getItems().size()) {
			wordsPredictedVsCorrectTable.requestFocus();
			wordsPredictedVsCorrectTable.getSelectionModel().select(index);
			wordsPredictedVsCorrectTable.getFocusModel().focus(index);
			wordsPredictedVsCorrectTable.scrollTo(index);
		}
	}

	public void handleFilterWords() {
		try {
			Stage filterWordDialogStage = new Stage();
			String resource = "fxml/ColumnFilterDialog.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale,
					filterWordDialogStage, bundle.getString("label.filterwords"),
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			ColumnFilterController controller = loader.getController();
			controller.setDialogStage(filterWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setWordsFilterType(WordsFilterType.WORDS);
			controller.setFilter(filterWords);
			controller.setWords(wordsPredictedVsCorrectTable.getItems());
			if (filterWords != null) {
				controller.setColumnFilterType(filterWords.getFilterType());
				controller.setTextToSearchFor(filterWords.getTextToMatch());
				controller.setMatchCase(filterWords.isMatchCase());
				controller.setMatchDiacritics(filterWords.isMatchDiacritics());
			}
			filterWordDialogStage.showAndWait();
			if (controller.isResultIsOK()) {
				controller.getFilter().setActive(true);
				filteredWords = new FilteredList<>(controller.getWords(), p -> true);
				sortedWords = new SortedList<>(filteredWords);
				haveAppliedFilter = true;
				putFilteredDataInTable();
				filterWords = controller.getFilter();
			} else if (haveAppliedFilter) {
				putFilteredDataInTable();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void putFilteredDataInTable() {
		sortedWords.comparatorProperty().bind(wordsPredictedVsCorrectTable.comparatorProperty());
		wordsPredictedVsCorrectTable.setItems(sortedWords);
		int max = wordsPredictedVsCorrectTable.getItems().size();
		if (max > 0) {
			int indexToStartWith = adjustIndexValue(0, max);
			setFocusOnWord(indexToStartWith);
		}
		miRemoveFiltersWords.setDisable(false);
		wordPredictedVsCorrectColumn.setStyle("-fx-background-color:yellow");
		wordsPredictedVsCorrectTable.refresh();
	}

	protected void handleRemoveFiltersWord() {
		wordPredictedVsCorrectColumn.setStyle("");
		filterWords.setActive(false);
		miRemoveFiltersWords.setDisable(true);
		wordsPredictedVsCorrectTable.setItems(words);
		wordsPredictedVsCorrectTable.refresh();
	}

	@Override
	void handleInsertNewItem() {
		// nothing to do in this case
	}

	@Override
	void handleRemoveItem() {
		// nothing to do in this case
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return null;
	}

	@Override
	void handlePreviousItem() {
		// nothing to do
	}

	@Override
	void handleNextItem() {
		// nothing to do
	}

	protected void updateStatusBar() {
		int iCount = wordsPredictedVsCorrectTable.getItems().size();
		int iFirst = (iCount >0) ? 1: 0;
		mainApp.updateStatusBarNumberOfItems(iFirst + "/" + iCount + " ");
	}

	protected void focusOnLastItemUsed(int iLastIndex) {
		int max = wordsPredictedVsCorrectTable.getItems().size();
		if (max > 0 && mainApp != null) {
			iLastIndex = adjustIndexValue(iLastIndex, max);
			setFocusOnWord(iLastIndex);
		} else {
			setFocusOnWord(0);
		}
	}
}
