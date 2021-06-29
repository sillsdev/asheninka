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
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.syllableparser.service.parsing.OTSyllabifier;

/**
 * @author Andy Black
 *
 */
public class OTApproachLanguageComparer extends ONCApproachLanguageComparer {

	OTApproach ota1;
	OTApproach ota2;
	protected SortedSet<DifferentOTConstraint> constraintsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentOTConstraint::getSortingValue));
	protected SortedSet<DifferentOTConstraintRanking> constraintRankingsWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentOTConstraintRanking::getSortingValue));
	protected LinkedList<Diff> rankingOrderDifferences = new LinkedList<>();

	public OTApproachLanguageComparer(OTApproach ota1, OTApproach ota2) {
		super(ota1.getLanguageProject().getONCApproach(), ota2.getLanguageProject().getONCApproach());
		this.ota1 = ota1;
		this.ota2 = ota2;
	}

	public OTApproach getOta1() {
		return ota1;
	}

	public void setOta1(OTApproach ota1) {
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

	public SortedSet<DifferentOTConstraintRanking> getConstraintRankingsWhichDiffer() {
		return constraintRankingsWhichDiffer;
	}

	public LinkedList<Diff> getRankingOrderDifferences() {
		return rankingOrderDifferences;
	}

	@Override
	public void compare() {
		try {
			compareSegmentInventory();
		} catch (Exception e) {
			e.printStackTrace();
			MainApp.reportException(e, null);
		}
		compareCVNaturalClasses(ota1.getActiveCVNaturalClasses(), ota2.getActiveCVNaturalClasses(),
				cvNaturalClassesWhichDiffer);
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareOTConstraints();
		compareOTConstraintRankings();
		compareOTConstraintRankingsOrder();
		compareWords();
	}

	public void compareOTConstraints() {
		constraintsWhichDiffer.clear();
		List<OTConstraint> constraints1 = ota1.getValidActiveOTConstraints();
		List<OTConstraint> constraints2 = ota2.getValidActiveOTConstraints();

		List<OTConstraint> diff1from2 = new ArrayList<OTConstraint>(constraints1);
		diff1from2.removeAll(constraints2);
		diff1from2.stream().forEach(
				rule -> constraintsWhichDiffer.add(new DifferentOTConstraint(
						rule, null)));
		List<OTConstraint> diff2from1 = new ArrayList<OTConstraint>(constraints2);
		diff2from1.removeAll(constraints1);
		diff2from1.stream().forEach(
				rule -> mergeSimilarConstraints(rule));
	}

	protected void mergeSimilarConstraints(OTConstraint constraint) {
		List<DifferentOTConstraint> sameConstraintRepresentation = constraintsWhichDiffer
				.stream()
				.filter(c -> c.getObjectFrom1() != null
						&& ((OTConstraint) c.getObjectFrom1()).getID().equals(
								constraint.getID())).collect(Collectors.toList());
		if (sameConstraintRepresentation.size() > 0) {
			DifferentOTConstraint diffConstraint = sameConstraintRepresentation.get(0);
			diffConstraint.setObjectFrom2(constraint);
		} else {
			DifferentOTConstraint diffConstraint = new DifferentOTConstraint(null,
					constraint);
			constraintsWhichDiffer.add(diffConstraint);
		}
	}

	public void compareOTConstraintRankings() {
		constraintRankingsWhichDiffer.clear();
		List<OTConstraintRanking> rankings1 = ota1.getActiveOTConstraintRankings();
		List<OTConstraintRanking> rankings2 = ota2.getActiveOTConstraintRankings();

		List<OTConstraintRanking> diff1from2 = new ArrayList<OTConstraintRanking>(rankings1);
		diff1from2.removeAll(rankings2);
		diff1from2.stream().forEach(
				ranking -> constraintRankingsWhichDiffer.add(new DifferentOTConstraintRanking(
						ranking, null)));
		List<OTConstraintRanking> diff2from1 = new ArrayList<OTConstraintRanking>(rankings2);
		diff2from1.removeAll(rankings1);
		diff2from1.stream().forEach(
				ranking -> mergeSimilarConstraintRankings(ranking));
	}

	protected void mergeSimilarConstraintRankings(OTConstraintRanking ranking) {
		List<DifferentOTConstraintRanking> sameRankingRepresentation = constraintRankingsWhichDiffer
				.stream()
				.filter(r -> r.getObjectFrom1() != null
						&& ((OTConstraintRanking) r.getObjectFrom1()).getID().equals(
								ranking.getID())).collect(Collectors.toList());
		if (sameRankingRepresentation.size() > 0) {
			DifferentOTConstraintRanking diffRanking = sameRankingRepresentation.get(0);
			diffRanking.setObjectFrom2(ranking);
		} else {
			DifferentOTConstraintRanking diffRanking = new DifferentOTConstraintRanking(null,
					ranking);
			constraintRankingsWhichDiffer.add(diffRanking);
		}
	}

	public void compareOTConstraintRankingsOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String rankings1 = createTextFromRankings(ota1.getActiveOTConstraintRankings());
		String rankings2 = createTextFromRankings(ota2.getActiveOTConstraintRankings());
		rankingOrderDifferences = dmp.diff_main(rankings1, rankings2);
	}

	protected String createTextFromRankings(List<OTConstraintRanking> list) {
		StringBuilder sb = new StringBuilder();
		list.stream().forEach(ranking -> {
			sb.append(ranking.getName());
			sb.append("\t");
			sb.append(ranking.getDescription());
//			sb.append("\t");
//			sb.append(ranking.getRankingRepresentation());
			sb.append("\n");
		});
		return sb.toString();
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(ota1, words1);
		syllabifyWords(ota2, words2);
	}

	protected void syllabifyWords(OTApproach ota, List<Word> words) {
		OTSyllabifier otSyllabifier = new OTSyllabifier(ota);
		otSyllabifier.setBundle(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION));
		for (Word word : words) {
			boolean fSuccess = otSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setOTPredictedSyllabification(otSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getOTPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getOTPredictedSyllabification());
	}

}
