/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.cedarsoftware.util.StringUtilities;

import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class ParaTExtExportedWordListXmlParserHandler extends DefaultHandler {
	
	private List<Word> wordList = null;
	private Word myWord = null;
	
	public List<Word> getWordList() {
		return wordList;
	}
	public void setWordList(List<Word> wordList) {
		this.wordList = wordList;
	}
	public Word getWord() {
		return myWord;
	}
	public void setWord(Word word) {
		this.myWord = word;
	}
	
	  @Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes)
	            throws SAXException {
	 
	        if (qName.equalsIgnoreCase("item")) {
	            String wordAttribute = attributes.getValue("word");
	            myWord = new Word();
	            myWord.setWord(wordAttribute);
	            String hyphenationApproved = attributes.getValue("hyphenationApproved");
	            if (!StringUtilities.isEmpty(hyphenationApproved) && "True".equalsIgnoreCase(hyphenationApproved)) {
	            	String correctHyphenation = attributes.getValue("hyphenation");
	            	if (!StringUtilities.isEmpty(correctHyphenation)) {
	            		myWord.setCorrectSyllabification(correctHyphenation.replaceAll("=", "."));
	            	}
	            }
	            //initialize list
	            if (wordList == null)
	                wordList = new ArrayList<>();
	        } 
	    }
	 
	    @Override
	    public void endElement(String uri, String localName, String qName) throws SAXException {
	        if (qName.equalsIgnoreCase("item")) {
	            //add new word object to list
	            wordList.add(myWord);
	        }
	    }

}
