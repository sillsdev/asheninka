// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Word;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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

	protected ObservableList<Word> words = FXCollections.observableArrayList();

	private Word currentWord;

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
			int currentItem = wordsPredictedVsCorrectTable.getItems().indexOf(currentWord) + 1;
			this.mainApp.updateStatusBarNumberOfItems(currentItem + "/"
					+ wordsPredictedVsCorrectTable.getItems().size() + " ");
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
}
