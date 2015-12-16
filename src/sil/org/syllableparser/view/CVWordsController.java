/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVWord;
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

	protected final class WrappingTableCell extends TableCell<CVWord, String> {
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
				setGraphic(text);
			}
		}
	}

	@FXML
	private TableView<CVWord> cvWordsTable;
	@FXML
	private TableColumn<CVWord, String> wordColumn;
	@FXML
	private TableColumn<CVWord, String> predictedSyllabificationColumn;
	@FXML
	private TableColumn<CVWord, String> correctSyllabificationColumn;
	@FXML
	private TableColumn<CVWord, String> parserResultColumn;

	@FXML
	private TextField wordField;
	@FXML
	private TextField predictedSyllabificationField;
	@FXML
	private TextField correctSyllabificationField;
	@FXML
	private TextField parserResultField;

	private CVApproach cvApproach;
	private CVWord currentWord;

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
		wordColumn.setCellValueFactory(cellData -> cellData.getValue().cvWordProperty());
		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.predictedSyllabificationProperty());
		correctSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.correctSyllabificationProperty());
		parserResultColumn.setCellValueFactory(cellData -> cellData.getValue().cvParserResultProperty());

		// Custom rendering of the table cell.
		wordColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		predictedSyllabificationColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		correctSyllabificationColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		parserResultColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		
		makeColumnHeaderWrappable(wordColumn);
		makeColumnHeaderWrappable(predictedSyllabificationColumn);
		makeColumnHeaderWrappable(correctSyllabificationColumn);
		makeColumnHeaderWrappable(parserResultColumn);
		
		// Clear cv word details.
		showCVWordDetails(null);

		// Listen for selection changes and show the details when changed.
		cvWordsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showCVWordDetails(newValue));

		// Handle TextField text changes.
		wordField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentWord != null) {
				currentWord.setCVWord(wordField.getText());
			}
		});
		predictedSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (currentWord != null) {
						currentWord.setPredictedSyllabification(predictedSyllabificationField
								.getText());
					}
				});
		correctSyllabificationField.textProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							if (currentWord != null) {
								currentWord.setCorrectSyllabification(correctSyllabificationField
										.getText());
							}
						});
		parserResultField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentWord != null) {
				currentWord.setCVParserResult(parserResultField.getText());
			}
		});
		parserResultColumn.getStyleClass().add("syllabification-result");
		parserResultField.getStyleClass().add("syllabification-result");
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
	private void showCVWordDetails(CVWord cvWord) {
		currentWord = cvWord;
		if (cvWord != null) {
			// Fill the text fields with info from the segment object.
			wordField.setText(cvWord.getCVWord());
			predictedSyllabificationField.setText(cvWord.getPredictedSyllabification());
			correctSyllabificationField.setText(cvWord.getCorrectSyllabification());
			parserResultField.setText(cvWord.getCVParserResult());
		} else {
			// Segment is null, remove all the text.
			wordField.setText("");
			predictedSyllabificationField.setText("");
			correctSyllabificationField.setText("");
			parserResultField.setText("");
		}

		if (cvWord != null) {
			int currentItem = cvWordsTable.getItems().indexOf(currentWord) + 1;
			this.mainApp.updateStatusBarNumberOfItems(currentItem + "/"
					+ cvWordsTable.getItems().size() + " ");
		}

	}

	public void setWord(CVWord cvWord) {
		wordField.setText(cvWord.getCVWord());
		predictedSyllabificationField.setText(cvWord.getPredictedSyllabification());
		correctSyllabificationField.setText(cvWord.getCorrectSyllabification());
		parserResultField.setText(cvWord.getCVParserResult());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;

		// Add observable list data to the table
		cvWordsTable.setItems(cvApproachData.getCVWords());
		if (cvWordsTable.getItems().size() > 0) {
			// select one
			cvWordsTable.requestFocus();
			cvWordsTable.getSelectionModel().select(0);
			cvWordsTable.getFocusModel().focus(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		CVWord newCVWord = new CVWord();
		newCVWord.setCVParserResult(bundle.getString("label.untested"));
		cvApproach.getCVWords().add(newCVWord);
		int i = cvApproach.getCVWords().size() - 1;
		cvWordsTable.requestFocus();
		cvWordsTable.getSelectionModel().select(i);
		cvWordsTable.getFocusModel().focus(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = cvApproach.getCVWords().indexOf(currentWord);
		currentWord = null;
		if (i >= 0) {
			cvApproach.getCVWords().remove(i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCut()
	 */
	@Override
	void handleCut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCopy()
	 */
	@Override
	void handleCopy() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handlePaste()
	 */
	@Override
	void handlePaste() {
		// TODO Auto-generated method stub

	}
}
