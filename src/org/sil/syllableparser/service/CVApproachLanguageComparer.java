// Copyright (c) 2016-2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.utility.StringUtilities;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparer {

	// Embedded class used in this particular implementation

	CVApproach cva1;
	CVApproach cva2;

	String dataSet1Info;
	String dataSet2Info;

	SortedSet<DifferentSegment> segmentsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentSegment::getSortingValue));
	SortedSet<DifferentGrapheme> graphemesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentGrapheme::getSortingValue));
	SortedSet<DifferentEnvironment> environmentsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentEnvironment::getSortingValue));
	SortedSet<DifferentCVNaturalClass> naturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVNaturalClass::getSortingValue));
	SortedSet<DifferentGraphemeNaturalClass> graphemeNaturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentGraphemeNaturalClass::getSortingValue));
	SortedSet<DifferentCVSyllablePattern> syllablePatternsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVSyllablePattern::getSortingValue));
	SortedSet<DifferentWord> wordsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentWord::getSortingValue));
	LinkedList<Diff> syllablePatternOrderDifferences = new LinkedList<>();

	public CVApproachLanguageComparer(CVApproach cva1, CVApproach cva2) {
		super();
		this.cva1 = cva1;
		this.cva2 = cva2;
	}

	public CVApproach getCva1() {
		return cva1;
	}

	public void setCva1(CVApproach cva1) {
		this.cva1 = cva1;
	}

	public CVApproach getCva2() {
		return cva2;
	}

	public void setCva2(CVApproach cva2) {
		this.cva2 = cva2;
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

	public SortedSet<DifferentCVNaturalClass> getNaturalClassesWhichDiffer() {
		return naturalClassesWhichDiffer;
	}

	public SortedSet<DifferentGraphemeNaturalClass> getGraphemeNaturalClassesWhichDiffer() {
		return graphemeNaturalClassesWhichDiffer;
	}

	public SortedSet<DifferentCVSyllablePattern> getSyllablePatternsWhichDiffer() {
		return syllablePatternsWhichDiffer;
	}

	public LinkedList<Diff> getSyllablePatternOrderDifferences() {
		return syllablePatternOrderDifferences;
	}

	public SortedSet<DifferentWord> getWordsWhichDiffer() {
		return wordsWhichDiffer;
	}

	public void compare() {
		compareSegmentInventory();
		compareNaturalClasses();
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareSyllablePatterns();
		compareSyllablePatternOrder();
		compareWords();
	}

	public void compareSegmentInventory() {
		List<Segment> segments1 = cva1.getLanguageProject().getActiveSegmentsInInventory();
		List<Segment> segments2 = cva2.getLanguageProject().getActiveSegmentsInInventory();

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
		List<Grapheme> graphemes1 = cva1.getLanguageProject().getActiveGraphemes();
		List<Grapheme> graphemes2 = cva2.getLanguageProject().getActiveGraphemes();

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
						&& ((Grapheme) ds.getObjectFrom1()).getForm()
								.equals(grapheme.getForm())
						&& ((Grapheme) ds.getObjectFrom1()).getEnvsRepresentation()
								.equals(grapheme.getEnvsRepresentation())
								).collect(Collectors.toList());
		if (sameGraphemesForm.size() > 0) {
			DifferentGrapheme diffGrapheme = sameGraphemesForm.get(0);
			diffGrapheme.setObjectFrom2(grapheme);
		} else {
			DifferentGrapheme diffGrapheme = new DifferentGrapheme(null, grapheme);
			graphemesWhichDiffer.add(diffGrapheme);
		}
	}

	public void compareEnvironments() {
		List<Environment> environment1 = cva1.getLanguageProject().getActiveAndValidEnvironments();
		List<Environment> environment2 = cva2.getLanguageProject().getActiveAndValidEnvironments();

		Set<Environment> difference1from2 = new HashSet<Environment>(environment1);
		// use set difference (removeAll)
		difference1from2.removeAll(environment2);
		difference1from2.stream().forEach(
				environment -> environmentsWhichDiffer.add(new DifferentEnvironment(environment, null)));

		Set<Environment> difference2from1 = new HashSet<Environment>(environment2);
		difference2from1.removeAll(environment1);
		difference2from1.stream().forEach(environment -> mergeSimilarEnvironments(environment));
	}

	protected void mergeSimilarEnvironments(Environment environment) {
		List<DifferentEnvironment> sameEnvironments = environmentsWhichDiffer
				.stream()
				.filter(ds -> ds.getObjectFrom1() != null
						&& ((Environment) ds.getObjectFrom1()).getEnvironmentRepresentation()
								.equals(environment.getEnvironmentRepresentation())).collect(Collectors.toList());
		if (sameEnvironments.size() > 0) {
			DifferentEnvironment diffEnv = sameEnvironments.get(0);
			diffEnv.setObjectFrom2(environment);
		} else {
			DifferentEnvironment diffEnv = new DifferentEnvironment(null, environment);
			environmentsWhichDiffer.add(diffEnv);
		}
	}

	public void compareNaturalClasses() {
		List<CVNaturalClass> naturalClasses1 = cva1.getActiveCVNaturalClasses();
		List<CVNaturalClass> naturalClasses2 = cva2.getActiveCVNaturalClasses();

		Set<CVNaturalClass> difference1from2 = new HashSet<CVNaturalClass>(naturalClasses1);
		// use set difference (removeAll)
		difference1from2.removeAll(naturalClasses2);
		difference1from2.stream().forEach(
				naturalClass -> naturalClassesWhichDiffer.add(new DifferentCVNaturalClass(
						naturalClass, null)));

		Set<CVNaturalClass> difference2from1 = new HashSet<CVNaturalClass>(naturalClasses2);
		difference2from1.removeAll(naturalClasses1);
		difference2from1.stream().forEach(
				naturalClass -> mergeSimilarCVNaturalClasses(naturalClass));
	}

	protected void mergeSimilarCVNaturalClasses(CVNaturalClass naturalClass) {
		List<DifferentCVNaturalClass> sameNaturalClassesName = naturalClassesWhichDiffer
				.stream()
				.filter(dnc -> dnc.getObjectFrom1() != null
						&& ((CVNaturalClass) dnc.getObjectFrom1()).getNCName().equals(
								naturalClass.getNCName())).collect(Collectors.toList());
		if (sameNaturalClassesName.size() > 0) {
			DifferentCVNaturalClass diffNaturalClass = sameNaturalClassesName.get(0);
			diffNaturalClass.setObjectFrom2(naturalClass);
		} else {
			DifferentCVNaturalClass diffNaturalClass = new DifferentCVNaturalClass(null,
					naturalClass);
			naturalClassesWhichDiffer.add(diffNaturalClass);
		}
	}

	public void compareGraphemeNaturalClasses() {
		List<GraphemeNaturalClass> gncs1 = cva1.getLanguageProject().getActiveGraphemeNaturalClasses();
		List<GraphemeNaturalClass> gncs2 = cva2.getLanguageProject().getActiveGraphemeNaturalClasses();

		Set<GraphemeNaturalClass> difference1from2 = new HashSet<GraphemeNaturalClass>(gncs1);
		// use set difference (removeAll)
		difference1from2.removeAll(gncs2);
		difference1from2.stream().forEach(
				gnc -> graphemeNaturalClassesWhichDiffer.add(new DifferentGraphemeNaturalClass(gnc, null)));

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

	public void compareSyllablePatterns() {
		List<CVSyllablePattern> syllablePatterns1 = cva1.getActiveCVSyllablePatterns();
		List<CVSyllablePattern> syllablePatterns2 = cva2.getActiveCVSyllablePatterns();

		Set<CVSyllablePattern> difference1from2 = new HashSet<CVSyllablePattern>(syllablePatterns1);
		// use set difference (removeAll)
		difference1from2.removeAll(syllablePatterns2);
		difference1from2.stream().forEach(
				syllablePattern -> syllablePatternsWhichDiffer.add(new DifferentCVSyllablePattern(
						syllablePattern, null)));

		Set<CVSyllablePattern> difference2from1 = new HashSet<CVSyllablePattern>(syllablePatterns2);
		difference2from1.removeAll(syllablePatterns1);
		difference2from1.stream().forEach(
				syllablePattern -> mergeSimilarCVSyllablePatterns(syllablePattern));
	}

	protected void mergeSimilarCVSyllablePatterns(CVSyllablePattern syllablePattern) {
		List<DifferentCVSyllablePattern> sameSyllablePatternName = syllablePatternsWhichDiffer
				.stream()
				.filter(dsp -> dsp.getObjectFrom1() != null
						&& ((CVSyllablePattern) dsp.getObjectFrom1()).getSPName().equals(
								syllablePattern.getSPName())).collect(Collectors.toList());
		if (sameSyllablePatternName.size() > 0) {
			DifferentCVSyllablePattern diffSyllablePattern = sameSyllablePatternName.get(0);
			diffSyllablePattern.setObjectFrom2(syllablePattern);
		} else {
			DifferentCVSyllablePattern diffSyllablePattern = new DifferentCVSyllablePattern(null,
					syllablePattern);
			syllablePatternsWhichDiffer.add(diffSyllablePattern);
		}
	}

	public void compareWords() {
		// make sure both sets have been syllabified
		List<Word> words1 = cva1.getLanguageProject().getWords();
		List<Word> words2 = cva2.getLanguageProject().getWords();
		// TODO: are there side-effects from this? If so, do we want them to be
		// there?
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

	public void compareSyllablePatternOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String syllablePatterns1 = createTextFromSyllablePattern(cva1);
		String syllablePatterns2 = createTextFromSyllablePattern(cva2);
		syllablePatternOrderDifferences = dmp.diff_main(syllablePatterns1, syllablePatterns2);
	}

	protected String createTextFromSyllablePattern(CVApproach cva) {
		StringBuilder sb = new StringBuilder();
		cva.getActiveCVSyllablePatterns().stream().forEach(sp -> {
			sb.append(sp.getSPName());
			sb.append("\t");
			sb.append(sp.getNCSRepresentation());
			sb.append("\n");
		});
		return sb.toString();
	}
}
