// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;

/**
 * @author Andy Black
 *
 */
public class OTApproachLanguageComparer extends ONCApproachLanguageComparer {

	OTApproach ota1;
	OTApproach ota2;
	protected SortedSet<DifferentOTConstraint> constraintsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentOTConstraint::getSortingValue));
	protected LinkedList<Diff> rankingOrderDifferences = new LinkedList<>();

	public OTApproachLanguageComparer(OTApproach ota1, OTApproach ota2) {
		super(ota1.getLanguageProject().getONCApproach(), ota2.getLanguageProject().getONCApproach());
		this.ota1 = ota1;
		this.ota2 = ota2;
	}

	public OTApproach getOta1() {
		return ota1;
	}

	public void setNpa1(OTApproach ota1) {
		this.ota1 = ota1;
	}

	public OTApproach getOta2() {
		return ota2;
	}

	public void setOta2(OTApproach ota2) {
		this.ota2 = ota2;
	}

	public SortedSet<DifferentOTConstraint> getConstraintsWhichDiffer() {
		return constraintsWhichDiffer;
	}

	public LinkedList<Diff> getRankingOrderDifferences() {
		return rankingOrderDifferences;
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
		compareOTConstraints();
		compareOTConstraintRankingsOrder();
		compareWords();
	}

	public void compareOTConstraints() {
		constraintsWhichDiffer.clear();
		List<OTConstraint> rules1 = ota1.getValidActiveOTConstraints();
		List<OTConstraint> rules2 = ota2.getValidActiveOTConstraints();

		List<OTConstraint> diff1from2 = new ArrayList<OTConstraint>(rules1);
		diff1from2.removeAll(rules2);
		diff1from2.stream().forEach(
				rule -> constraintsWhichDiffer.add(new DifferentOTConstraint(
						rule, null)));
		List<OTConstraint> diff2from1 = new ArrayList<OTConstraint>(rules2);
		diff2from1.removeAll(rules1);
		diff2from1.stream().forEach(
				rule -> mergeSimilarConstraints(rule));
	}

	protected void mergeSimilarConstraints(OTConstraint constraint) {
		List<DifferentOTConstraint> sameConstraintRepresentation = constraintsWhichDiffer
				.stream()
				.filter(r -> r.getObjectFrom1() != null
						&& ((OTConstraint) r.getObjectFrom1()).getID().equals(
								constraint.getID())).collect(Collectors.toList());
		if (sameConstraintRepresentation.size() > 0) {
			DifferentOTConstraint diffFilter = sameConstraintRepresentation.get(0);
			diffFilter.setObjectFrom2(constraint);
		} else {
			DifferentOTConstraint diffFilter = new DifferentOTConstraint(null,
					constraint);
			constraintsWhichDiffer.add(diffFilter);
		}
	}

	public void compareOTConstraintRankingsOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String rules1 = createTextFromRules(ota1.getActiveOTConstraintRankings());
		String rules2 = createTextFromRules(ota2.getActiveOTConstraintRankings());
		rankingOrderDifferences = dmp.diff_main(rules1, rules2);
	}

	protected String createTextFromRules(List<OTConstraintRanking> list) {
		StringBuilder sb = new StringBuilder();
		list.stream().forEach(rule -> {
			sb.append(rule.getName());
			sb.append("\t");
			// TODO: need list of constraints
//			CVSegmentOrNaturalClass.createTextFromCVSegOrNC(rule.getAffectedSegOrNC(), sb);
//			sb.append("\t");
//			CVSegmentOrNaturalClass.createTextFromCVSegOrNC(rule.getContextSegOrNC(), sb);
//			sb.append("\n");
		});
		return sb.toString();
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(ota1, words1);
		syllabifyWords(ota2, words2);
	}

	protected void syllabifyWords(OTApproach npa, List<Word> words) {
//		NPSyllabifier npSyllabifier = new NPSyllabifier(npa);
//		for (Word word : words) {
//			boolean fSuccess = npSyllabifier.convertStringToSyllables(word.getWord());
//			if (fSuccess) {
//				word.setNPPredictedSyllabification(npSyllabifier.getSyllabificationOfCurrentWord());
//			}
//		}
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getOTPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getOTPredictedSyllabification());
	}

}
