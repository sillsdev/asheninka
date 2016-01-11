/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Approach;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.view.ApproachController;

/**
 * @author Andy Black
 *
 */
public class ParaTExtHyphenatedWordsExporter extends WordExporter {

	public ParaTExtHyphenatedWordsExporter() {
		super();
	}

	public ParaTExtHyphenatedWordsExporter(LanguageProject languageProject) {
		super(languageProject);
	}

	@Override
	public void exportWords(File file, Approach approach) {
		ArrayList<String> hyphenatedWords = approach.getParaTExtHyphenatedWords(languageProject
				.getWords());
		exportWordsToFile(file, hyphenatedWords);
	}
	
	@Override
	public void exportWords(File file, ApproachController controller) {
		ArrayList<String> hyphenatedWords = controller.getParaTExtHyphenatedWords(languageProject
				.getWords());
		exportWordsToFile(file, hyphenatedWords);
	}

	protected void exportWordsToFile(File file, ArrayList<String> hyphenatedWords) {
		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					file.getPath()), Constants.UTF8_ENCODING));
	
			fileWriter.write(Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE);
			for (String hyphenatedWord : hyphenatedWords) {
				fileWriter.write(hyphenatedWord);
				fileWriter.write("\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
