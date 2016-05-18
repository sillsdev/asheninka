/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class ControllerUtilities {
	public static void createToolbarButtonWithImage(String sUrl, Button buttonToolbar,
			Tooltip buttonTooltip, String sTooltip) {
		ImageView imageView = new ImageView();
		Image icon = new Image("file:resources/images/" + sUrl);
		imageView.setImage(icon);
		buttonToolbar.setGraphic(imageView);
		buttonTooltip = new Tooltip(sTooltip);
		buttonToolbar.setTooltip(buttonTooltip);
	}

	public static void setDateInStatusBar(StatusBar statusBar, ResourceBundle bundle) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withLocale(bundle.getLocale());
		statusBar.setText(LocalDate.now().format(formatter));
	}

	public static File getFileToOpen(MainApp mainApp, String sInitialFileName,
			String sFileChooserFilterDescription, String sFileExtensions) {
		FileChooser fileChooser = initFileChooser(mainApp, sInitialFileName,
				sFileChooserFilterDescription, sFileExtensions);

		// Show open file dialog
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		return file;
	}

	public static File getFileToOpen(MainApp mainApp, String sFileChooserFilterDescription,
			String sFileExtensions) {
		return getFileToOpen(mainApp, "", sFileChooserFilterDescription, sFileExtensions);
	}

	protected static FileChooser initFileChooser(MainApp mainApp,
			String sFileChooserFilterDescription, String sFileExtensions) {
		return initFileChooser(mainApp, "", sFileChooserFilterDescription, sFileExtensions);
	}

	protected static FileChooser initFileChooser(MainApp mainApp,
			String sInitialFileName, String sFileChooserFilterDescription, String sFileExtensions) {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				sFileChooserFilterDescription, sFileExtensions);
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialFileName(sInitialFileName);

		ApplicationPreferences applicationPreferences = mainApp.getApplicationPreferences();
		String sDirectoryPath = applicationPreferences.getLastOpenedDirectoryPath();
		if (sDirectoryPath != null && !sDirectoryPath.isEmpty()) {
			File initialDirectory = new File(sDirectoryPath);
			if (initialDirectory.exists() && initialDirectory.isDirectory()) {
				fileChooser.setInitialDirectory(initialDirectory);
			}
		}
		return fileChooser;
	}

	public static void doFileSaveAs(MainApp mainApp, Boolean fForceSave,
			String syllableParserFilterDescription) {
		FileChooser fileChooser = ControllerUtilities.initFileChooser(
				mainApp, syllableParserFilterDescription, Constants.ASHENINKA_DATA_FILE_EXTENSIONS);

		File file = null;
		if (fForceSave) {
			while (file == null) {
				file = askUserToSaveFile(mainApp, fileChooser);				
			}
		} else {
			file = askUserToSaveFile(mainApp, fileChooser);
		}
	}

	public static File askUserToSaveFile(MainApp mainApp, FileChooser fileChooser) {
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(Constants.ASHENINKA_DATA_FILE_EXTENSION)) {
				file = new File(file.getPath() + Constants.ASHENINKA_DATA_FILE_EXTENSION);
			}
			mainApp.saveLanguageData(file);
			String sDirectoryPath = file.getParent();
			ApplicationPreferences applicationPreferences = mainApp.getApplicationPreferences();
			applicationPreferences.setLastOpenedDirectoryPath(sDirectoryPath);
			mainApp.updateStageTitle(file);
		}
		return file;
	}

	public static TextInputDialog getTextInputDialog(MainApp mainApp, String title,
			String contentText) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(title);
		dialog.setHeaderText("");
		dialog.setGraphic(null);
		dialog.setContentText(contentText);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());
		return dialog;
	}

	public static void formatTimePassed(long timeStart, String sProcessName) {
		long timePassed = System.currentTimeMillis() - timeStart;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timePassed);
		long timeRemaining = timePassed - (minutes * 60000);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining);
		long millis = timeRemaining - (seconds * 1000);
		String sResult = sProcessName + " took " + minutes + ":" + seconds + "." + millis;
		System.out.println(sResult);
	}

	public static FXMLLoader getLoader(MainApp mainApp, Locale locale, Stage dialogStage,
			String resource, String title) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ApproachViewNavigator.class.getResource(resource));
		loader.setResources(ResourceBundle.getBundle(
				"sil.org.syllableparser.resources.SyllableParser", locale));

		AnchorPane page = loader.load();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);
		// set the icon
		dialogStage.getIcons().add(mainApp.getNewMainIconImage());
		dialogStage.setTitle(title);
		return loader;
	}

}
