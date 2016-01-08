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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import javafx.scene.Cursor;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class ParaTExtExportedWordListImporter extends WordImporter {

	public ParaTExtExportedWordListImporter() {
		super();
	}

	public ParaTExtExportedWordListImporter(LanguageProject languageProject) {
		super(languageProject);
	}

	public void importWords(File file, String sUntested) {
		
		 SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		    try {
		        SAXParser saxParser = saxParserFactory.newSAXParser();
		        ParaTExtExportedWordListXmlParserHandler handler = new ParaTExtExportedWordListXmlParserHandler();
		        saxParser.parse(file, handler);
		        List<Word> wordList = handler.getWordList();
		        //print employee information
		        for(Word word : wordList) {
		        	languageProject.createNewWordFromParaTExt(word, sUntested);
		        }
		    } catch (ParserConfigurationException | SAXException | IOException e) {
		        e.printStackTrace();
		    }
	}
}
