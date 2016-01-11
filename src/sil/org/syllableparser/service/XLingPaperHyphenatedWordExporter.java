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
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Approach;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.view.ApproachController;

/**
 * @author Andy Black
 *
 */
public class XLingPaperHyphenatedWordExporter extends WordExporter {

	public XLingPaperHyphenatedWordExporter() {
		super();
	}

	public XLingPaperHyphenatedWordExporter(LanguageProject languageProject) {
		super(languageProject);
	}

	@Override
	public void exportWords(File file, Approach approach) {
		ArrayList<String> hyphenatedWords = approach.getXLingPaperHyphenatedWords(languageProject
				.getWords());
		exportWordsToFile(file, hyphenatedWords);
	}

	@Override
	public void exportWords(File file, ApproachController controller) {
		ArrayList<String> hyphenatedWords = controller.getXLingPaperHyphenatedWords(languageProject
				.getWords());
		exportWordsToFile(file, hyphenatedWords);
	}

	protected void exportWordsToFile(File file, ArrayList<String> hyphenatedWords) {
		try {
			SortedSet<Character> charsUsed = new TreeSet<Character>();
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					file.getPath()), Constants.UTF8_ENCODING));

			fileWriter.write("<exceptions>\n");
			for (String hyphenatedWord : hyphenatedWords) {
				fileWriter.write("<word>");
				fileWriter.write(hyphenatedWord);
				fileWriter.write("</word>\n");
				char[] chars = hyphenatedWord.toCharArray();
				for (char c : chars) {
					charsUsed.add(c);
				}
			}
			Iterator<Character> cIt = charsUsed.iterator();
			while (cIt.hasNext()) {
				char c = cIt.next();
				if (!Character.isLetterOrDigit(c) && c != '-') {
					fileWriter.write("<wordformingcharacter>");
					fileWriter.write(c);
					fileWriter.write("</wordformingcharacter>\n");					
				}
			}
			fileWriter.write("</exceptions>\n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
