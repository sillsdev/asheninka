/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparer {

	// Embedded class used in this particular implementation

	CVApproach cva1;
	CVApproach cva2;

	Set<DifferentSegment> segmentsWhichDiffer = new HashSet<DifferentSegment>();
	Set<DifferentCVNaturalClass> naturalClassesWhichDiffer = new HashSet<DifferentCVNaturalClass>();
	Set<DifferentWord> wordsWhichDiffer = new HashSet<DifferentWord>();

	public CVApproachLanguageComparer(CVApproach cva1, CVApproach cva2) {
		super();
		this.cva1 = cva1;
		this.cva2 = cva2;
	}

	public CVApproach getCVAa1() {
		return cva1;
	}

	public void setCVA1(CVApproach cva1) {
		this.cva1 = cva1;
	}

	public CVApproach getCVA2() {
		return cva2;
	}

	public void setCVA2(CVApproach cva2) {
		this.cva2 = cva2;
	}

	public Set<DifferentSegment> getSegmentsWhichDiffer() {
		return segmentsWhichDiffer;
	}

	public Set<DifferentCVNaturalClass> getNaturalClassesWhichDiffer() {
		return naturalClassesWhichDiffer;
	}

	public Set<DifferentWord> getWordsWhichDiffer() {
		return wordsWhichDiffer;
	}

	public void compareSegmentInventory() {
		ObservableList<Segment> segments1 = cva1.getLanguageProject().getSegmentInventory();
		ObservableList<Segment> segments2 = cva2.getLanguageProject().getSegmentInventory();

		Set<Segment> difference1from2 = new HashSet<Segment>(segments1);
		// use set difference (removeAll)
		difference1from2.removeAll(segments2);
		difference1from2.stream().forEach(
				segment -> segmentsWhichDiffer.add(new DifferentSegment(segment, null)));

		Set<Segment> difference2from1 = new HashSet<Segment>(segments2);
		difference2from1.removeAll(segments1);
		difference2from1.stream().forEach(segment -> mergeSimilarSegments(segment));
	}

	protected void mergeSimilarSegments(Segment segment) {
		List<DifferentSegment> sameSegmentsName = segmentsWhichDiffer
				.stream()
				.filter(ds -> ds.getObjectFrom1() != null
						&& ((Segment) ds.getObjectFrom1()).getSegment()
								.equals(segment.getSegment())).collect(Collectors.toList());
		if (sameSegmentsName.size() > 0) {
			DifferentSegment diffSeg = sameSegmentsName.get(0);
			diffSeg.setObjectFrom2(segment);
		} else {
			DifferentSegment diffSegment = new DifferentSegment(null, segment);
			segmentsWhichDiffer.add(diffSegment);
		}
	}

	public void compareNaturalClasses() {
		ObservableList<CVNaturalClass> naturalClasses1 = cva1.getCVNaturalClasses();
		ObservableList<CVNaturalClass> naturalClasses2 = cva2.getCVNaturalClasses();

		Set<CVNaturalClass> difference1from2 = new HashSet<CVNaturalClass>(naturalClasses1);
		// use set difference (removeAll)
		difference1from2.removeAll(naturalClasses2);
		difference1from2.stream().forEach(
				naturalClass -> naturalClassesWhichDiffer.add(new DifferentCVNaturalClass(naturalClass, null)));

		Set<CVNaturalClass> difference2from1 = new HashSet<CVNaturalClass>(naturalClasses2);
		difference2from1.removeAll(naturalClasses1);
		difference2from1.stream().forEach(naturalClass -> mergeSimilarCVNaturalClasses(naturalClass));
	}

	protected void mergeSimilarCVNaturalClasses(CVNaturalClass naturalClass) {
		List<DifferentCVNaturalClass> sameNaturalClassesName = naturalClassesWhichDiffer
				.stream()
				.filter(dnc -> dnc.getObjectFrom1() != null
						&& ((CVNaturalClass) dnc.getObjectFrom1()).getNCName()
								.equals(naturalClass.getNCName())).collect(Collectors.toList());
		if (sameNaturalClassesName.size() > 0) {
			DifferentCVNaturalClass diffNaturalClass = sameNaturalClassesName.get(0);
			diffNaturalClass.setObjectFrom2(naturalClass);
		} else {
			DifferentCVNaturalClass diffNaturalClass = new DifferentCVNaturalClass(null, naturalClass);
			naturalClassesWhichDiffer.add(diffNaturalClass);
		}
	}

	public void compareWords() {
		// make sure both sets have been syllabified
		List<Word> words1 = cva1.getLanguageProject().getWords();
		List<Word> words2 = cva2.getLanguageProject().getWords();
		// TODO: are there side-effects from this?  If so, do we want them to be there?
		syllabifyWords(cva1, words1);
		syllabifyWords(cva2, words2);

		Set<Word> difference1from2 = new HashSet<Word>(words1);
		// use set difference (removeAll)
		difference1from2.removeAll(words2);
		difference1from2.stream().forEach(
				word -> wordsWhichDiffer.add(new DifferentWord(word, null)));

		Set<Word> difference2from1 = new HashSet<Word>(words2);
		difference2from1.removeAll(words1);
		difference2from1.stream().forEach(word -> mergeSimilarWords(word));
	}

	protected void syllabifyWords(CVApproach cva, List<Word> words) {
		CVSyllabifier stringSyllabifier = new CVSyllabifier(cva);
		for (Word word : words) {
			boolean fSuccess = stringSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setCVPredictedSyllabification(word.getCVPredictedSyllabification());
			}
		}

	}

	protected void mergeSimilarWords(Word word) {
		List<DifferentWord> sameWordsName = wordsWhichDiffer
				.stream()
				.filter(dw -> dw.getObjectFrom1() != null
						&& ((Word) dw.getObjectFrom1()).getWord().equals(word.getWord()))
				.collect(Collectors.toList());
		if (sameWordsName.size() > 0) {
			DifferentWord diffWord = sameWordsName.get(0);
			diffWord.setObjectFrom2(word);
		} else {
			DifferentWord diffWord = new DifferentWord(null, word);
			wordsWhichDiffer.add(diffWord);
		}
	}

}
