/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javafx.scene.Cursor;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class ListWordImporter extends WordImporter {

	public ListWordImporter() {
		super();
	}

	public ListWordImporter(MainApp mainApp, LanguageProject languageProject,
			String sFileChooserFilterDescription, String sFileExtensions) {
		super(mainApp, languageProject, sFileChooserFilterDescription, sFileExtensions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.service.WordImporter#importWords()
	 */
	@Override
	public void importWords(ResourceBundle bundle) {
		File file = null;

		file = getFileToOpen();

		if (file != null) {
			//Scene scene = statusBar.getScene();
			importWordsFromFile(file, bundle);
		}

	}

	public void importWordsFromFile(File file, ResourceBundle bundle) {
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(s -> languageProject.createNewWord(s, bundle));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
