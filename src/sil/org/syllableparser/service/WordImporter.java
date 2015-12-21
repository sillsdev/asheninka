/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;
import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public abstract class WordImporter {

	protected MainApp mainApp;
	protected LanguageProject languageProject;
	protected String sFileChooserFilterDescription;
	protected String sFileExtensions;

	abstract public void importWords(ResourceBundle bundle);

	public WordImporter() {
		super();
		mainApp = null;
		languageProject = null;
		sFileExtensions = "";
		sFileChooserFilterDescription = "";
	}

	public WordImporter(MainApp mainApp, LanguageProject languageProject,
			String sFileFilterDescription, String sFileExtensions) {
		super();
		this.mainApp = mainApp;
		this.languageProject = languageProject;
		this.sFileChooserFilterDescription = sFileFilterDescription;
		this.sFileExtensions = sFileExtensions;
	}

	public File getFileToOpen() {
		String sDirectoryPath;
		File file;
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				sFileChooserFilterDescription, sFileExtensions);
		fileChooser.getExtensionFilters().add(extFilter);
		sDirectoryPath = ApplicationPreferences.getLastOpenedDirectoryPath();
		if (sDirectoryPath != null && !sDirectoryPath.isEmpty()) {
			File initialDirectory = new File(sDirectoryPath);
			if (initialDirectory.exists() && initialDirectory.isDirectory()) {
				fileChooser.setInitialDirectory(initialDirectory);
			}
		}

		// Show open file dialog
		file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		return file;
	}

}
