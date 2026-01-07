// Copyright (c) 2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleProcessor;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * @author Andy Black
 *
 */
public class HyphenApproachLanguageComparer extends ApproachLanguageComparer {

	HyphenApproach cva1;
	HyphenApproach cva2;

	SortedSet<DifferentHyphenClass> hyphenClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentHyphenClass::getSortingValue));
	SortedSet<DifferentHyphenChangeRule> hyphenChangeRulesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentHyphenChangeRule::getSortingValue));
	LinkedList<Diff> hyphenChangeRuleOrderDifferences = new LinkedList<>();

	public HyphenApproachLanguageComparer(HyphenApproach cva1, HyphenApproach cva2) {
		super(cva1.getLanguageProject(), cva2.getLanguageProject());
		this.cva1 = cva1;
		this.cva2 = cva2;
	}

	public HyphenApproach getCva1() {
		return cva1;
	}

	public void setCva1(HyphenApproach cva1) {
		this.cva1 = cva1;
	}

	public HyphenApproach getCva2() {
		return cva2;
	}

	public void setCva2(HyphenApproach cva2) {
		this.cva2 = cva2;
	}

	public SortedSet<DifferentHyphenClass> getHyphenClassesWhichDiffer() {
		return hyphenClassesWhichDiffer;
	}

	public SortedSet<DifferentHyphenChangeRule> getHyphenChangeRulesWhichDiffer() {
		return hyphenChangeRulesWhichDiffer;
	}

	public LinkedList<Diff> getHyphenChangeRuleOrderDifferences() {
		return hyphenChangeRuleOrderDifferences;
	}

	@Override
	public void compare() {
		compareSegmentInventory();
		compareHyphenClasses(cva1.getActiveHyphenClasses(), cva2.getActiveHyphenClasses(),
				hyphenClassesWhichDiffer);
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareHyphenChangeRules();
		compareHyphenChangeRuleOrder();
		compareWords();
	}

	public void compareHyphenClasses(List<HyphenClass> hyphenClasses1,
			List<HyphenClass> hyphenClasses2,
			SortedSet<DifferentHyphenClass> hyphenClassesWhichDiffer) {
		Set<HyphenClass> difference1from2 = new HashSet<HyphenClass>(hyphenClasses1);
		// use set difference (removeAll)
		difference1from2.removeAll(hyphenClasses2);
		difference1from2.stream().forEach(
				hyphenClass -> hyphenClassesWhichDiffer.add(new DifferentHyphenClass(
						hyphenClass, null)));

		Set<HyphenClass> difference2from1 = new HashSet<HyphenClass>(hyphenClasses2);
		difference2from1.removeAll(hyphenClasses1);
		difference2from1.stream().forEach(
				hyphenClass -> mergeSimilarHyphenClasses(hyphenClass,
						hyphenClassesWhichDiffer));
	}

	protected void mergeSimilarHyphenClasses(HyphenClass hyphenClass,
			SortedSet<DifferentHyphenClass> hyphenClassesWhichDiffer) {
		List<DifferentHyphenClass> sameHyphenClassesName = hyphenClassesWhichDiffer
				.stream()
				.filter(dhc -> dhc.getObjectFrom1() != null
						&& ((HyphenClass) dhc.getObjectFrom1()).getClassName().equals(
								hyphenClass.getClassName())).collect(Collectors.toList());
		if (sameHyphenClassesName.size() > 0) {
			DifferentHyphenClass diffHyphenClass = sameHyphenClassesName.get(0);
			diffHyphenClass.setObjectFrom2(hyphenClass);
		} else {
			DifferentHyphenClass diffNaturalClass = new DifferentHyphenClass(null,
					hyphenClass);
			hyphenClassesWhichDiffer.add(diffNaturalClass);
		}
	}

	public void compareHyphenChangeRules() {
		List<HyphenChangeRule> syllablePatterns1 = cva1.getActiveHyphenChangeRules();
		List<HyphenChangeRule> syllablePatterns2 = cva2.getActiveHyphenChangeRules();

		Set<HyphenChangeRule> difference1from2 = new HashSet<HyphenChangeRule>(syllablePatterns1);
		// use set difference (removeAll)
		difference1from2.removeAll(syllablePatterns2);
		difference1from2.stream().forEach(
				syllablePattern -> hyphenChangeRulesWhichDiffer.add(new DifferentHyphenChangeRule(
						syllablePattern, null)));

		Set<HyphenChangeRule> difference2from1 = new HashSet<HyphenChangeRule>(syllablePatterns2);
		difference2from1.removeAll(syllablePatterns1);
		difference2from1.stream().forEach(
				syllablePattern -> mergeSimilarCVSyllablePatterns(syllablePattern));
	}

	protected void mergeSimilarCVSyllablePatterns(HyphenChangeRule syllablePattern) {
		List<DifferentHyphenChangeRule> sameSyllablePatternName = hyphenChangeRulesWhichDiffer
				.stream()
				.filter(dsp -> dsp.getObjectFrom1() != null
						&& ((HyphenChangeRule) dsp.getObjectFrom1()).getRuleName().equals(
								syllablePattern.getRuleName())).collect(Collectors.toList());
		if (sameSyllablePatternName.size() > 0) {
			DifferentHyphenChangeRule diffSyllablePattern = sameSyllablePatternName.get(0);
			diffSyllablePattern.setObjectFrom2(syllablePattern);
		} else {
			DifferentHyphenChangeRule diffSyllablePattern = new DifferentHyphenChangeRule(null,
					syllablePattern);
			hyphenChangeRulesWhichDiffer.add(diffSyllablePattern);
		}
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(cva1, words1);
		syllabifyWords(cva2, words2);
	}

	protected void syllabifyWords(HyphenApproach cva, List<Word> words) {
//		HyphenChangeRuleProcessor stringSyllabifier = new HyphenChangeRuleProcessor(cva);
//		for (Word word : words) {
//			boolean fSuccess = stringSyllabifier.convertStringToSyllables(word.getWord());
//			if (fSuccess) {
//				word.setHyphenPredictedSyllabification(stringSyllabifier
//						.getSyllabificationOfCurrentWord());
//			}
//		}

	}

	public void compareHyphenChangeRuleOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String syllablePatterns1 = createTextFromSyllablePattern(cva1);
		String syllablePatterns2 = createTextFromSyllablePattern(cva2);
		hyphenChangeRuleOrderDifferences = dmp.diff_main(syllablePatterns1, syllablePatterns2);
	}

	protected String createTextFromSyllablePattern(HyphenApproach cva) {
		StringBuilder sb = new StringBuilder();
		cva.getActiveHyphenChangeRules().stream().forEach(sp -> {
			sb.append(sp.getRuleName());
			sb.append("\t");
			sb.append(sp.getMatchRepresentation());
			sb.append("\t");
			sb.append(sp.getChangeRepresentation());
			sb.append("\n");
		});
		return sb.toString();
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getHyphenPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getHyphenPredictedSyllabification());
	}
}
