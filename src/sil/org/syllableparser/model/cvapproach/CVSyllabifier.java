/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * a Service
 *         Takes a sequence of natural classes and parses them into a sequence
 *         of syllables
 */
public class CVSyllabifier {

	private final List<CVSyllablePattern> cvPatterns;
	private final List<CVNaturalClassInSyllable> naturalClassesInCurrentWord;

	LinkedList<CVSyllable> syllablesInCurrentWord = new LinkedList<CVSyllable>(
			Arrays.asList(new CVSyllable(null)));
	String sSyllabifiedWord;

	public CVSyllabifier(List<CVSyllablePattern> cvPatterns,
			List<CVNaturalClassInSyllable> naturalClassesInCurrentWord) {
		super();
		this.cvPatterns = cvPatterns;
		this.naturalClassesInCurrentWord = naturalClassesInCurrentWord;
		sSyllabifiedWord = "";
	}

	public List<CVSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<CVSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<CVSyllablePattern> getCvPatterns() {
		return cvPatterns;
	}

	public boolean convertNaturalClassesToSyllables() {
		syllablesInCurrentWord.clear();

		// recursively parse into syllables
		boolean result = parseIntoSyllables(naturalClassesInCurrentWord, cvPatterns);

		if (result) {
			// the list of syllables found is in reverse order; flip them
			Collections.reverse(syllablesInCurrentWord);
		}
		return result;
	}

	private boolean parseIntoSyllables(List<CVNaturalClassInSyllable> naturalClassesInWord,
			List<CVSyllablePattern> patterns) {
		if (naturalClassesInWord.size() == 0) {
			return true;
		}
		for (CVSyllablePattern pattern : patterns) {
			if (naturalClassesMatchSyllablePattern(naturalClassesInWord, pattern)) {
				List<CVNaturalClassInSyllable> remainingNaturalClassesInWord = naturalClassesInWord
						.subList(pattern.getNCs().size(), naturalClassesInWord.size());
				if (parseIntoSyllables(remainingNaturalClassesInWord, patterns)) {
					List<CVNaturalClassInSyllable> naturalClassesInSyllable = naturalClassesInWord
							.subList(0, pattern.getNCs().size());
					CVSyllable syl = new CVSyllable(naturalClassesInSyllable);
					syllablesInCurrentWord.add(syl);
					return true;
				}
			}
		}
		return false;
	}

	private boolean naturalClassesMatchSyllablePattern(
			List<CVNaturalClassInSyllable> naturalClassesInWord, CVSyllablePattern currentCVPattern) {
		// TODO: is there another, better way to compare the contents of two
		// lists?
		ObservableList<CVNaturalClass> naturalClassesInCVPattern = currentCVPattern.getNCs();
		if (naturalClassesInCVPattern.size() > naturalClassesInWord.size()) {
			return false;
		}
		Iterator<CVNaturalClassInSyllable> currentNaturalClass = naturalClassesInWord.iterator();
		CVNaturalClassInSyllable ncInSyllable = currentNaturalClass.next();
		for (CVNaturalClass ncInPattern : naturalClassesInCVPattern) {
			if (!ncInSyllable.getNaturalClass().equals(ncInPattern)) {
				return false;
			}
			if (currentNaturalClass.hasNext()) {
				ncInSyllable = currentNaturalClass.next();
			} else {
				break;
			}
		}
		return true;
	}

	public String getSyllabificationOfCurrentWord() {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (CVSyllable syl : syllablesInCurrentWord) {
			for (CVNaturalClassInSyllable nc : syl.getNaturalClassesInSyllable()) {
				sb.append(nc.getSegmentInSyllable().getGrapheme());
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

}
