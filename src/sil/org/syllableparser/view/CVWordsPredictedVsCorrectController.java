/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
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

public class CVWordsPredictedVsCorrectController extends SylParserBaseController implements
		Initializable {

	@FXML
	private TableView<Word> cvWordsPredictedVsCorrectTable;
	@FXML
	private Label whenTableIsEmptyMessage;
	@FXML
	private TableColumn<Word, String> wordPredictedVsCorrectColumn;

	private ObservableList<Word> words = FXCollections.observableArrayList();

	private Word currentWord;

	public CVWordsPredictedVsCorrectController() {

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
		cvWordsPredictedVsCorrectTable.setPlaceholder(whenTableIsEmptyMessage);

		// Initialize the table with the three columns.
		wordPredictedVsCorrectColumn.setCellValueFactory(cellData -> cellData.getValue()
				.cvPredictedVsCorrectSyllabificationProperty());

		makeColumnHeaderWrappable(wordPredictedVsCorrectColumn);

		// Listen for selection change for status bar info
		cvWordsPredictedVsCorrectTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showStatusBarNumberOfItems(newValue));

	}

	private void showStatusBarNumberOfItems(Word cvWord) {
		currentWord = cvWord;
		if (cvWord != null) {
			int currentItem = cvWordsPredictedVsCorrectTable.getItems().indexOf(currentWord) + 1;
			this.mainApp.updateStatusBarNumberOfItems(currentItem + "/"
					+ cvWordsPredictedVsCorrectTable.getItems().size() + " ");
		}
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
		this.words = words;

		ObservableList<Word> wordsToShow = words.filtered(word -> (!word
				.getCorrectSyllabification().isEmpty()
				&& !word.getCVPredictedSyllabification().isEmpty() && !word
				.getCVPredictedSyllabification().equals(word.getCorrectSyllabification())));

		// Add observable list data to the table
		cvWordsPredictedVsCorrectTable.setItems(wordsToShow.sorted());
		setFocusOnWord(0);
	}

	public void setFocusOnWord(int index) {
		if (cvWordsPredictedVsCorrectTable.getItems().size() > 0 && index > -1
				&& index < cvWordsPredictedVsCorrectTable.getItems().size()) {
			cvWordsPredictedVsCorrectTable.requestFocus();
			cvWordsPredictedVsCorrectTable.getSelectionModel().select(index);
			cvWordsPredictedVsCorrectTable.getFocusModel().focus(index);
			cvWordsPredictedVsCorrectTable.scrollTo(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sil.org.syllableparser.view.ApproachEditorController#handleInsertNewItem
	 * ()
	 */
	@Override
	void handleInsertNewItem() {
		// nothing to do in this case
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sil.org.syllableparser.view.ApproachEditorController#handleRemoveItem()
	 */
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
}
