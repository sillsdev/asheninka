// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public abstract class ApproachLanguageComparer {

	LanguageProject langProj1;
	LanguageProject langProj2;

	String dataSet1Info;
	String dataSet2Info;

	SortedSet<DifferentSegment> segmentsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentSegment::getSortingValue));
	SortedSet<DifferentGrapheme> graphemesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentGrapheme::getSortingValue));
	SortedSet<DifferentEnvironment> environmentsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentEnvironment::getSortingValue));
	SortedSet<DifferentGraphemeNaturalClass> graphemeNaturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentGraphemeNaturalClass::getSortingValue));
	SortedSet<DifferentWord> wordsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentWord::getSortingValue));

	public ApproachLanguageComparer(LanguageProject lang1, LanguageProject lang2) {
		langProj1 = lang1;
		langProj2 = lang2;
	}

	public String getDataSet1Info() {
		return dataSet1Info;
	}

	public void setDataSet1Info(String dataSet1Info) {
		this.dataSet1Info = StringUtilities.adjustForWindowsFileSeparator(dataSet1Info);
	}

	public String getDataSet2Info() {
		return dataSet2Info;
	}

	public void setDataSet2Info(String dataSet2Info) {
		this.dataSet2Info = StringUtilities.adjustForWindowsFileSeparator(dataSet2Info);
	}

	public SortedSet<DifferentSegment> getSegmentsWhichDiffer() {
		return segmentsWhichDiffer;
	}

	public SortedSet<DifferentGrapheme> getGraphemesWhichDiffer() {
		return graphemesWhichDiffer;
	}

	public SortedSet<DifferentEnvironment> getEnvironmentsWhichDiffer() {
		return environmentsWhichDiffer;
	}

	public SortedSet<DifferentGraphemeNaturalClass> getGraphemeNaturalClassesWhichDiffer() {
		return graphemeNaturalClassesWhichDiffer;
	}

	public SortedSet<DifferentWord> getWordsWhichDiffer() {
		return wordsWhichDiffer;
	}

	public abstract void compare();

	public void compareSegmentInventory() {
		List<Segment> segments1 = langProj1.getActiveSegmentsInInventory();
		List<Segment> segments2 = langProj2.getActiveSegmentsInInventory();

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

	public void compareGraphemes() {
		List<Grapheme> graphemes1 = langProj1.getActiveGraphemes();
		List<Grapheme> graphemes2 = langProj2.getActiveGraphemes();

		Set<Grapheme> difference1from2 = new HashSet<Grapheme>(graphemes1);
		// use set difference (removeAll)
		difference1from2.removeAll(graphemes2);
		difference1from2.stream().forEach(
				grapheme -> graphemesWhichDiffer.add(new DifferentGrapheme(grapheme, null)));

		Set<Grapheme> difference2from1 = new HashSet<Grapheme>(graphemes2);
		difference2from1.removeAll(graphemes1);
		difference2from1.stream().forEach(grapheme -> mergeSimilarGraphemes(grapheme));
	}

	protected void mergeSimilarGraphemes(Grapheme grapheme) {
		List<DifferentGrapheme> sameGraphemesForm = graphemesWhichDiffer
				.stream()
				.filter(ds -> ds.getObjectFrom1() != null
						&& ((Grapheme) ds.getObjectFrom1()).getForm().equals(grapheme.getForm())
						&& ((Grapheme) ds.getObjectFrom1()).getEnvsRepresentation().equals(
								grapheme.getEnvsRepresentation())).collect(Collectors.toList());
		if (sameGraphemesForm.size() > 0) {
			DifferentGrapheme diffGrapheme = sameGraphemesForm.get(0);
			diffGrapheme.setObjectFrom2(grapheme);
		} else {
			DifferentGrapheme diffGrapheme = new DifferentGrapheme(null, grapheme);
			graphemesWhichDiffer.add(diffGrapheme);
		}
	}

	public void compareEnvironments() {
		List<Environment> environment1 = langProj1.getActiveAndValidEnvironments();
		List<Environment> environment2 = langProj2.getActiveAndValidEnvironments();

		Set<Environment> difference1from2 = new HashSet<Environment>(environment1);
		// use set difference (removeAll)
		difference1from2.removeAll(environment2);
		difference1from2.stream().forEach(
				environment -> environmentsWhichDiffer.add(new DifferentEnvironment(environment,
						null)));

		Set<Environment> difference2from1 = new HashSet<Environment>(environment2);
		difference2from1.removeAll(environment1);
		difference2from1.stream().forEach(environment -> mergeSimilarEnvironments(environment));
	}

	protected void mergeSimilarEnvironments(Environment environment) {
		List<DifferentEnvironment> sameEnvironments = environmentsWhichDiffer
				.stream()
				.filter(ds -> ds.getObjectFrom1() != null
						&& ((Environment) ds.getObjectFrom1()).getEnvironmentRepresentation()
								.equals(environment.getEnvironmentRepresentation()))
				.collect(Collectors.toList());
		if (sameEnvironments.size() > 0) {
			DifferentEnvironment diffEnv = sameEnvironments.get(0);
			diffEnv.setObjectFrom2(environment);
		} else {
			DifferentEnvironment diffEnv = new DifferentEnvironment(null, environment);
			environmentsWhichDiffer.add(diffEnv);
		}
	}

	public void compareGraphemeNaturalClasses() {
		List<GraphemeNaturalClass> gncs1 = langProj1.getActiveGraphemeNaturalClasses();
		List<GraphemeNaturalClass> gncs2 = langProj2.getActiveGraphemeNaturalClasses();

		Set<GraphemeNaturalClass> difference1from2 = new HashSet<GraphemeNaturalClass>(gncs1);
		// use set difference (removeAll)
		difference1from2.removeAll(gncs2);
		difference1from2.stream().forEach(
				gnc -> graphemeNaturalClassesWhichDiffer.add(new DifferentGraphemeNaturalClass(gnc,
						null)));

		Set<GraphemeNaturalClass> difference2from1 = new HashSet<GraphemeNaturalClass>(gncs2);
		difference2from1.removeAll(gncs1);
		difference2from1.stream().forEach(gnc -> mergeSimilarGraphemeNaturalClasses(gnc));
	}

	protected void mergeSimilarGraphemeNaturalClasses(GraphemeNaturalClass gnc) {
		List<DifferentGraphemeNaturalClass> sameGNCRepresentation = graphemeNaturalClassesWhichDiffer
				.stream()
				.filter(ds -> ds.getObjectFrom1() != null
						&& ((GraphemeNaturalClass) ds.getObjectFrom1()).getGNCRepresentation()
								.equals(gnc.getGNCRepresentation())).collect(Collectors.toList());
		if (sameGNCRepresentation.size() > 0) {
			DifferentGraphemeNaturalClass diffGnc = sameGNCRepresentation.get(0);
			diffGnc.setObjectFrom2(gnc);
		} else {
			DifferentGraphemeNaturalClass diffGnc = new DifferentGraphemeNaturalClass(null, gnc);
			graphemeNaturalClassesWhichDiffer.add(diffGnc);
		}
	}

	public void compareWords() {
		// make sure both sets have been syllabified
		List<Word> words1 = langProj1.getWords();
		List<Word> words2 = langProj2.getWords();
		// TODO: are there side-effects from this? If so, do we want them to be
		// there?
		syllabifyWords(words1, words2);

		Set<Word> difference1from2 = new HashSet<Word>(words1);
		// use set difference (removeAll)
		difference1from2.removeAll(words2);
		difference1from2.stream().forEach(
				word -> wordsWhichDiffer.add(new DifferentWord(word, null)));

		Set<Word> difference2from1 = new HashSet<Word>(words2);
		difference2from1.removeAll(words1);
		difference2from1.stream().forEach(word -> mergeSimilarWords(word));
	}

	protected abstract void syllabifyWords(List<Word> words1, List<Word> words2);

	protected abstract boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word);

	protected void mergeSimilarWords(Word word) {
		List<DifferentWord> sameWordsName = wordsWhichDiffer
				.stream()
				.filter(dw -> dw.getObjectFrom1() != null
						&& ((Word) dw.getObjectFrom1()).getWord().equals(word.getWord()))
				.collect(Collectors.toList());
		if (sameWordsName.size() > 0) {
			DifferentWord diffWord = sameWordsName.get(0);
			diffWord.setObjectFrom2(word);
			if (predictedSyllabificationAreSame(diffWord, word)) {
				wordsWhichDiffer.remove(diffWord);
			}
		} else {
			DifferentWord diffWord = new DifferentWord(null, word);
			wordsWhichDiffer.add(diffWord);
		}
	}
}
