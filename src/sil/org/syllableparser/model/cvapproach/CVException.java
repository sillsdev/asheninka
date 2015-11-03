/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import sil.org.syllableparser.model.SylParserObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVException extends SylParserObject {
	private final StringProperty word;
	private final StringProperty correctSyllabification;
	private StringProperty discussion;

	public CVException() {
		super();
		this.word = new SimpleStringProperty("");
		this.correctSyllabification = new SimpleStringProperty("");
		this.discussion = new SimpleStringProperty("");
		createUUID();
	}

	public CVException(String word, String correctHyphenation, String discussion) {
		super();
		this.word = new SimpleStringProperty(word);
		this.correctSyllabification = new SimpleStringProperty(correctHyphenation);
		this.discussion = new SimpleStringProperty(discussion);
		createUUID();
	}

	public String getWord() {
		return word.get();
	}

	public StringProperty wordProperty() {
		return word;
	}

	public void setWord(String cvWord) {
		this.word.set(cvWord);
	}

	public String getCorrectSyllabification() {
		return correctSyllabification.get();
	}

	public StringProperty correctSyllabificationProperty() {
		return correctSyllabification;
	}

	public void setCorrectSyllabification(String correctSyllabification) {
		this.correctSyllabification.set(correctSyllabification);
	}

	/**
	 * @return the discussion
	 */
	public StringProperty discussionProperty() {
		return discussion;
	}

	public String getDiscussion() {
		return discussion.get();
	}

	public void setDiscussion(String discussion) {
		this.discussion.set(discussion);
	}
}
