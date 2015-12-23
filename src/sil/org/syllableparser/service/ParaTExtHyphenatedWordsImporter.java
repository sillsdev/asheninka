/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javafx.scene.Cursor;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class ParaTExtHyphenatedWordsImporter extends WordImporter {

	public ParaTExtHyphenatedWordsImporter() {
		super();
	}

	public ParaTExtHyphenatedWordsImporter(LanguageProject languageProject) {
		super(languageProject);
	}

	public void importWords(File file, String sUntested) {
		StringBuilder sb = new StringBuilder();
		extractParaTExtPreamble(file, sb);
		languageProject.setParaTExtHyphenatedWordsPreamble(sb.toString());

		// now add all words
		try (Stream<String> stream = Files.lines(file.toPath()).skip(7)) {
			stream.forEach(s -> languageProject.createNewWordFromParaTExt(s, sUntested));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extractParaTExtPreamble(File file, StringBuilder sb) {
		// TODO: is there a way to use a stream for this? I did not find
		// anything online
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);

			int count = 1;
			// not sure why, but are getting something in the first character position
			String line = bufr.readLine().substring(1);
			while (line != null && count < 8) {
				sb.append(line);
				sb.append("\n");
				line = bufr.readLine();
				count++;
			}
			bufr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
