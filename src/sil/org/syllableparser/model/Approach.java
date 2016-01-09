/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.ArrayList;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public abstract class Approach {

	public abstract ArrayList<String> getHyphenatedWords(ObservableList<Word> words);
}
