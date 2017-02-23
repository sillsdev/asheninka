package sil.org.syllableparser.service;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sil.org.syllableparser.model.Word;

public class CorrectSyllabificationCleaner {
	
	ObservableList<Word> words = FXCollections.observableArrayList();

	public CorrectSyllabificationCleaner(ObservableList<Word> words) {
		super();
		this.words = words;
	}
	
	public void ClearAllCorrectSyllabificationFromWords() {
		List<Word> wordsWithNonEmptyCorrectSyllabification = words.stream()
				.filter(w -> w.getCorrectSyllabification().length() > 0)
				.collect(Collectors.toList());
		wordsWithNonEmptyCorrectSyllabification.forEach(w -> w.setCorrectSyllabification(""));
	}

}
