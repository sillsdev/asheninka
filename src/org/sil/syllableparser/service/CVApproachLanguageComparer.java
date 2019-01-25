// Copyright (c) 2016-2019 SIL International
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

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparer extends ApproachLanguageComparer {

	CVApproach cva1;
	CVApproach cva2;

	SortedSet<DifferentCVNaturalClass> naturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVNaturalClass::getSortingValue));
	SortedSet<DifferentCVSyllablePattern> syllablePatternsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVSyllablePattern::getSortingValue));
	LinkedList<Diff> syllablePatternOrderDifferences = new LinkedList<>();

	public CVApproachLanguageComparer(CVApproach cva1, CVApproach cva2) {
		super(cva1.getLanguageProject(), cva2.getLanguageProject());
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

	public SortedSet<DifferentCVNaturalClass> getNaturalClassesWhichDiffer() {
		return naturalClassesWhichDiffer;
	}

	public SortedSet<DifferentCVSyllablePattern> getSyllablePatternsWhichDiffer() {
		return syllablePatternsWhichDiffer;
	}

	public LinkedList<Diff> getSyllablePatternOrderDifferences() {
		return syllablePatternOrderDifferences;
	}

	@Override
	public void compare() {
		compareSegmentInventory();
		compareNaturalClasses();
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareSyllablePatterns();
		compareSyllablePatternOrder();
		compareWords();
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

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(cva1, words1);
		syllabifyWords(cva2, words2);
	}

	protected void syllabifyWords(CVApproach cva, List<Word> words) {
		CVSyllabifier stringSyllabifier = new CVSyllabifier(cva);
		for (Word word : words) {
			boolean fSuccess = stringSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setCVPredictedSyllabification(stringSyllabifier.getSyllabificationOfCurrentWord());
//				word.setCVPredictedSyllabification(word.getCVPredictedSyllabification());
			}
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
