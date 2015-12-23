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
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class ListWordImporter extends WordImporter {

	public ListWordImporter() {
		super();
	}

	public ListWordImporter(LanguageProject languageProject) {
		super(languageProject);
	}


	public void importWords(File file, String sUntested) {
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(s -> languageProject.createNewWord(s, sUntested));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
