// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPFilter;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.service.parsing.NPSyllabifier;

/**
 * @author Andy Black
 *
 */
public class NPApproachLanguageComparer extends ONCApproachLanguageComparer {

	NPApproach npa1;
	NPApproach npa2;
	protected SortedSet<DifferentNPRule> rulesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentNPRule::getSortingValue));
	protected LinkedList<Diff> ruleOrderDifferences = new LinkedList<>();

	public NPApproachLanguageComparer(NPApproach npa1, NPApproach npa2) {
		super(npa1.getLanguageProject().getONCApproach(), npa2.getLanguageProject().getONCApproach());
		this.npa1 = npa1;
		this.npa2 = npa2;
	}

	public NPApproach getNpa1() {
		return npa1;
	}

	public void setNpa1(NPApproach npa1) {
		this.npa1 = npa1;
	}

	public NPApproach getNpa2() {
		return npa2;
	}

	public void setNpa2(NPApproach npa2) {
		this.npa2 = npa2;
	}

	public SortedSet<DifferentNPRule> getRulesWhichDiffer() {
		return rulesWhichDiffer;
	}

	public LinkedList<Diff> getRuleOrderDifferences() {
		return ruleOrderDifferences;
	}

	@Override
	public void compare() {
		try {
			compareSegmentInventory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareSonorityHierarchy();
		compareSonorityHierarchyOrder();
		compareSyllabificationParameters();
		compareCVNaturalClasses(onca1.getActiveCVNaturalClasses(), onca2.getActiveCVNaturalClasses(),
				cvNaturalClassesWhichDiffer);
		compareNPRules();
		compareNPRuleOrder();
		compareNPFilters();
		compareNPFilterOrder();
		compareWords();
	}

	public void compareNPFilters() {
		List<NPFilter> filters1 = npa1.getValidActiveNPFilters();
		List<NPFilter> filters2 = npa2.getValidActiveNPFilters();

		Set<NPFilter> difference1from2 = new HashSet<NPFilter>(filters1);
		// use set difference (removeAll)
		difference1from2.removeAll(filters2);
		difference1from2.stream().forEach(
				filter -> filtersWhichDiffer.add(new DifferentFilter(
						filter, null)));

		Set<NPFilter> difference2from1 = new HashSet<NPFilter>(filters2);
		difference2from1.removeAll(filters1);
		difference2from1.stream().forEach(
				filter -> mergeSimilarFilters(filter));
	}

	public void compareNPFilterOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String filters1 = createTextFromFilters(npa1);
		String filters2 = createTextFromFilters(npa2);
		filterOrderDifferences = dmp.diff_main(filters1, filters2);
	}

	protected String createTextFromFilters(NPApproach npa) {
		return createTextFromTemplateFilter(npa.getNPFilters());
	}

	public void compareNPRules() {
		rulesWhichDiffer.clear();
		List<NPRule> rules1 = npa1.getValidActiveNPRules();
		List<NPRule> rules2 = npa2.getValidActiveNPRules();

		List<NPRule> diff1from2 = new ArrayList<NPRule>(rules1);
		diff1from2.removeAll(rules2);
		diff1from2.stream().forEach(
				rule -> rulesWhichDiffer.add(new DifferentNPRule(
						rule, null)));
		List<NPRule> diff2from1 = new ArrayList<NPRule>(rules2);
		diff2from1.removeAll(rules1);
		diff2from1.stream().forEach(
				rule -> mergeSimilarRules(rule));
	}

	protected void mergeSimilarRules(NPRule rule) {
		List<DifferentNPRule> sameRuleRepresentation = rulesWhichDiffer
				.stream()
				.filter(r -> r.getObjectFrom1() != null
						&& ((NPRule) r.getObjectFrom1()).getID().equals(
								rule.getID())).collect(Collectors.toList());
		if (sameRuleRepresentation.size() > 0) {
			DifferentNPRule diffRule = sameRuleRepresentation.get(0);
			diffRule.setObjectFrom2(rule);
		} else {
			DifferentNPRule diffRule = new DifferentNPRule(null,
					rule);
			rulesWhichDiffer.add(diffRule);
		}
	}

	public void compareNPRuleOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String rules1 = createTextFromRules(npa1.getValidActiveNPRules());
		String rules2 = createTextFromRules(npa2.getValidActiveNPRules());
		ruleOrderDifferences = dmp.diff_main(rules1, rules2);
	}

	protected String createTextFromRules(List<NPRule> list) {
		StringBuilder sb = new StringBuilder();
		list.stream().forEach(rule -> {
			sb.append(rule.getRuleName());
			sb.append("\t");
			sb.append(rule.getRuleAction());
			sb.append("\t");
			sb.append(rule.getRuleLevel());
			sb.append("\t");
			CVSegmentOrNaturalClass.createTextFromCVSegOrNC(rule.getAffectedSegOrNC(), sb);
			sb.append("\t");
			CVSegmentOrNaturalClass.createTextFromCVSegOrNC(rule.getContextSegOrNC(), sb);
			sb.append("\n");
		});
		return sb.toString();
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(npa1, words1);
		syllabifyWords(npa2, words2);
	}

	protected void syllabifyWords(NPApproach npa, List<Word> words) {
		NPSyllabifier npSyllabifier = new NPSyllabifier(npa);
		for (Word word : words) {
			boolean fSuccess = npSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setNPPredictedSyllabification(npSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getNPPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getNPPredictedSyllabification());
	}

}
