/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVException;
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

public class CVExceptionsController extends SylParserBaseController
		implements Initializable {

	protected final class WrappingTableCell extends
			TableCell<CVException, String> {
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
				text.wrappingWidthProperty().bind(
						getTableColumn().widthProperty());
				setGraphic(text);
			}
		}
	}

	@FXML
	private TableView<CVException> cvExceptionsTable;
	@FXML
	private TableColumn<CVException, String> exceptionWordColumn;
	@FXML
	private TableColumn<CVException, String> correctSyllabificationColumn;
	@FXML
	private TableColumn<CVException, String> discussionColumn;

	@FXML
	private TextField exceptionWordField;
	@FXML
	private TextField correctSyllabificationField;
	@FXML
	private TextField discussionField;

	private CVApproach cvApproach;
	private CVException currentException;

	public CVExceptionsController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// public void initialize() {

		// Initialize the table with the three columns.
		exceptionWordColumn.setCellValueFactory(cellData -> cellData.getValue()
				.wordProperty());
		correctSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.correctSyllabificationProperty());
		discussionColumn.setCellValueFactory(cellData -> cellData.getValue()
				.discussionProperty());

		// Custom rendering of the table cell.
		exceptionWordColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		correctSyllabificationColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		discussionColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});

		makeColumnHeaderWrappable(exceptionWordColumn);
		makeColumnHeaderWrappable(correctSyllabificationColumn);
		makeColumnHeaderWrappable(discussionColumn);

		// Clear cv word details.
		showCVExceptionDetails(null);

		// Listen for selection changes and show the details when changed.
		cvExceptionsTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showCVExceptionDetails(newValue));

		// Handle TextField text changes.
		exceptionWordField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentException.setWord(exceptionWordField.getText());
				});
		correctSyllabificationField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentException.setCorrectSyllabification(correctSyllabificationField.getText());
				});
		discussionField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentException.setDiscussion(discussionField.getText());
				});

		// Use of Enter move focus to next item.
		exceptionWordField.setOnAction((event) -> {
			correctSyllabificationField.requestFocus();
		});
		correctSyllabificationField.setOnAction((event) -> {
			discussionField.requestFocus();
		});

		exceptionWordField.requestFocus();

	}

	/**
	 * Fills all text fields to show details about the CV word. If the
	 * specified word is null, all text fields are cleared.
	 * 
	 * @param cvException
	 *            the segment or null
	 */
	private void showCVExceptionDetails(CVException cvException) {
		currentException = cvException;
		if (cvException != null) {
			// Fill the text fields with info from the segment object.
			exceptionWordField.setText(cvException.getWord());
			correctSyllabificationField.setText(cvException.getCorrectSyllabification());
			discussionField.setText(cvException.getDiscussion());
		} else {
			// Segment is null, remove all the text.
			exceptionWordField.setText("");
			correctSyllabificationField.setText("");
			discussionField.setText("");
		}
	}

	public void setExceptionWord(CVException cvExceptionWord) {
		exceptionWordField.setText(cvExceptionWord.getWord());
		correctSyllabificationField.setText(cvExceptionWord.getCorrectSyllabification());
		discussionField.setText(cvExceptionWord.getDiscussion());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;

		// Add observable list data to the table
		cvExceptionsTable.setItems(cvApproachData.getCVExceptions());
		if (cvExceptionsTable.getItems().size() > 0) {
			// select one
			cvExceptionsTable.requestFocus();
			cvExceptionsTable.getSelectionModel().select(0);
			cvExceptionsTable.getFocusModel().focus(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		CVException newCVException = new CVException();
		cvApproach.getCVExceptions().add(newCVException);
		int i = cvApproach.getCVExceptions().size() - 1;
		cvExceptionsTable.requestFocus();
		cvExceptionsTable.getSelectionModel().select(i);
		cvExceptionsTable.getFocusModel().focus(i);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = cvApproach.getCVExceptions().indexOf(currentException);
		currentException = null;
		if (i >= 0) {
			cvApproach.getCVExceptions().remove(i);
		}
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCut()
	 */
	@Override
	void handleCut() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCopy()
	 */
	@Override
	void handleCopy() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handlePaste()
	 */
	@Override
	void handlePaste() {
		// TODO Auto-generated method stub
		
	}
}
