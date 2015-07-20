/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVSegmentNaturalClassChooserController {
	
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
	//public void initialize() {
	}
	
	 /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
//        if (isInputValid()) {
//            person.setFirstName(firstNameField.getText());
//            person.setLastName(lastNameField.getText());
//            person.setStreet(streetField.getText());
//            person.setPostalCode(Integer.parseInt(postalCodeField.getText()));
//            person.setCity(cityField.getText());
//            person.setBirthday(DateUtil.parse(birthdayField.getText()));

            okClicked = true;
            dialogStage.close();
//        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}


    /**
     * Called when the user clicks help.
     */
    @FXML
    private void handleHelp() {
        mainApp.showNotImplementedYet();
    }
}
