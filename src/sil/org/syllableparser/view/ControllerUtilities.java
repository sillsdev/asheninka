/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.MainApp;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * @author Andy Black
 *
 */
public class ControllerUtilities {
	public static void createToolbarButtonWithImage(String sUrl, Button buttonToolbar,
			Tooltip buttonTooltip, String sTooltip) {
		ImageView imageView = new ImageView();
		Image icon = new Image("file:src/sil/org/syllableparser/resources/images/" + sUrl);
		imageView.setImage(icon);
		buttonToolbar.setGraphic(imageView);
		buttonTooltip = new Tooltip(sTooltip);
		buttonToolbar.setTooltip(buttonTooltip);
	}

	public static void setDateInStatusBar(StatusBar statusBar, ResourceBundle bundle) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withLocale(bundle.getLocale());
		statusBar.setText(LocalDate.now().format(formatter));
	}

	public static File getFileToOpen(MainApp mainApp, 
			String sFileChooserFilterDescription,
			String sFileExtensions) {
		FileChooser fileChooser = initFileChooser(sFileChooserFilterDescription, sFileExtensions);

		// Show open file dialog
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		return file;
	}

	protected static FileChooser initFileChooser(String sFileChooserFilterDescription,
			String sFileExtensions) {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				sFileChooserFilterDescription, sFileExtensions);
		fileChooser.getExtensionFilters().add(extFilter);

		String sDirectoryPath = ApplicationPreferences.getLastOpenedDirectoryPath();
		if (sDirectoryPath != null && !sDirectoryPath.isEmpty()) {
			File initialDirectory = new File(sDirectoryPath);
			if (initialDirectory.exists() && initialDirectory.isDirectory()) {
				fileChooser.setInitialDirectory(initialDirectory);
			}
		}
		return fileChooser;
	}

}
