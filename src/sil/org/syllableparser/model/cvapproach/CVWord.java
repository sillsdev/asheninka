/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.Word;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVWord extends Word {
	private final StringProperty predictedSyllabification;

	// TODO: decide if we need some kind of a comment field to say what kind of
	// case this word represents

	// TODO: add an indication of whether or not the predicted and correct
	// values are the same

	public CVWord() {
		super();
		this.predictedSyllabification = new SimpleStringProperty("");
	}

	public CVWord(String word, String predictedSyllabification,
			String correctSyllabification, String parserResult) {
		super(word, correctSyllabification, parserResult);
		this.predictedSyllabification = new SimpleStringProperty(
				predictedSyllabification);
	}

	public String getPredictedSyllabification() {
		return predictedSyllabification.get();
	}

	public StringProperty predictedSyllabificationProperty() {
		return predictedSyllabification;
	}

	public void setPredictedSyllabification(String predictedSyllabification) {
		this.predictedSyllabification.set(predictedSyllabification);
	}

	public static int findIndexInCVWordListByUuid(ObservableList<CVWord> list, String uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
				int index = -1;
				for (SylParserObject sylParserObject : list) {
					index++;
					if (sylParserObject.getID() == uuid) {
						return index;
					}
				}		
				return -1;
			}
}
