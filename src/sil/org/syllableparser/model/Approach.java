// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
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

	public abstract ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words);
	public abstract ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words);
	public abstract ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words);
}
