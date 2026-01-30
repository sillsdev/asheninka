// Copyright (c) 2025-2026 SIL International
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

	HyphenApproach ha1;
	HyphenApproach ha2;

	SortedSet<DifferentHyphenClass> hyphenClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentHyphenClass::getSortingValue));
	SortedSet<DifferentHyphenChangeRule> hyphenChangeRulesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentHyphenChangeRule::getSortingValue));
	LinkedList<Diff> hyphenChangeRuleOrderDifferences = new LinkedList<>();

	public HyphenApproachLanguageComparer(HyphenApproach ha1, HyphenApproach ha2) {
		super(ha1.getLanguageProject(), ha2.getLanguageProject());
		this.ha1 = ha1;
		this.ha2 = ha2;
	}

	public HyphenApproach getHa1() {
		return ha1;
	}

	public void setHa1(HyphenApproach ha1) {
		this.ha1 = ha1;
	}

	public HyphenApproach getHa2() {
		return ha2;
	}

	public void setHa2(HyphenApproach ha2) {
		this.ha2 = ha2;
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
		compareHyphenClasses(ha1.getActiveHyphenClasses(), ha2.getActiveHyphenClasses(),
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
		List<HyphenChangeRule> changeRules1 = ha1.getActiveHyphenChangeRules();
		List<HyphenChangeRule> changeRules2 = ha2.getActiveHyphenChangeRules();

		Set<HyphenChangeRule> difference1from2 = new HashSet<HyphenChangeRule>(changeRules1);
		// use set difference (removeAll)
		difference1from2.removeAll(changeRules2);
		difference1from2.stream().forEach(
				changeRule -> hyphenChangeRulesWhichDiffer.add(new DifferentHyphenChangeRule(
						changeRule, null)));

		Set<HyphenChangeRule> difference2from1 = new HashSet<HyphenChangeRule>(changeRules2);
		difference2from1.removeAll(changeRules1);
		difference2from1.stream().forEach(
				changeRule -> mergeSimilarChangeRules(changeRule));
	}

	protected void mergeSimilarChangeRules(HyphenChangeRule changeRule) {
		List<DifferentHyphenChangeRule> sameChangeRuleName = hyphenChangeRulesWhichDiffer
				.stream()
				.filter(dsp -> dsp.getObjectFrom1() != null
						&& ((HyphenChangeRule) dsp.getObjectFrom1()).getRuleName().equals(
								changeRule.getRuleName())).collect(Collectors.toList());
		if (sameChangeRuleName.size() > 0) {
			DifferentHyphenChangeRule diffChangeRule = sameChangeRuleName.get(0);
			diffChangeRule.setObjectFrom2(changeRule);
		} else {
			DifferentHyphenChangeRule diffChangeRule = new DifferentHyphenChangeRule(null,
					changeRule);
			hyphenChangeRulesWhichDiffer.add(diffChangeRule);
		}
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(ha1, words1);
		syllabifyWords(ha2, words2);
	}

	protected void syllabifyWords(HyphenApproach ha, List<Word> words) {
		HyphenChangeRuleProcessor ruleProcessor = new HyphenChangeRuleProcessor(ha);
		for (Word word : words) {
			boolean fSuccess = ruleProcessor.convertStringToHyphenatedForm(word.getWord());
			if (fSuccess) {
				word.setHyphenPredictedSyllabification(ruleProcessor.getSyllabifiedWord());
			}
		}
	}

	public void compareHyphenChangeRuleOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String syllablePatterns1 = createTextFromChangeRules(ha1);
		String syllablePatterns2 = createTextFromChangeRules(ha2);
		hyphenChangeRuleOrderDifferences = dmp.diff_main(syllablePatterns1, syllablePatterns2);
	}

	protected String createTextFromChangeRules(HyphenApproach ha) {
		StringBuilder sb = new StringBuilder();
		ha.getActiveHyphenChangeRules().stream().forEach(sp -> {
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
