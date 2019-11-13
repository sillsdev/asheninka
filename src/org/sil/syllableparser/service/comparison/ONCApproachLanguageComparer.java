// Copyright (c) 2019 SIL International
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

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.parsing.ONCSyllabifier;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * @author Andy Black
 *
 */
public class ONCApproachLanguageComparer extends ApproachLanguageComparer {

	ONCApproach onca1;
	ONCApproach onca2;

	SortedSet<DifferentSHNaturalClass> naturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentSHNaturalClass::getSortingValue));
	LinkedList<Diff> sonorityHierarchyOrderDifferences = new LinkedList<>();

	public ONCApproachLanguageComparer(ONCApproach sha1, ONCApproach sha2) {
		super(sha1.getLanguageProject(), sha2.getLanguageProject());
		this.onca1 = sha1;
		this.onca2 = sha2;
	}

	public ONCApproach getOnca1() {
		return onca1;
	}

	public void setOnca1(ONCApproach onca1) {
		this.onca1 = onca1;
	}

	public ONCApproach getOnca2() {
		return onca2;
	}

	public void setOnca2(ONCApproach onca2) {
		this.onca2 = onca2;
	}

	public SortedSet<DifferentSHNaturalClass> getNaturalClassesWhichDiffer() {
		return naturalClassesWhichDiffer;
	}

	public LinkedList<Diff> getSonorityHierarchyOrderDifferences() {
		return sonorityHierarchyOrderDifferences;
	}

	@Override
	public void compare() {
		compareSegmentInventory();
		compareSonorityHierarchy();
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareSonorityHierarchy();
		compareSonorityHierarchyOrder();
		compareSyllabificationParameters();
		compareWords();
	}

	public void compareSonorityHierarchy() {
		List<SHNaturalClass> naturalClasses1 = onca1.getONCSonorityHierarchy();
		List<SHNaturalClass> naturalClasses2 = onca2.getONCSonorityHierarchy();

		Set<SHNaturalClass> difference1from2 = new HashSet<SHNaturalClass>(naturalClasses1);
		// use set difference (removeAll)
		difference1from2.removeAll(naturalClasses2);
		difference1from2.stream().forEach(
				naturalClass -> naturalClassesWhichDiffer.add(new DifferentSHNaturalClass(
						naturalClass, null)));

		Set<SHNaturalClass> difference2from1 = new HashSet<SHNaturalClass>(naturalClasses2);
		difference2from1.removeAll(naturalClasses1);
		difference2from1.stream().forEach(
				naturalClass -> mergeSimilarSHNaturalClasses(naturalClass));
	}

	protected void mergeSimilarSHNaturalClasses(SHNaturalClass naturalClass) {
		List<DifferentSHNaturalClass> sameNaturalClassesName = naturalClassesWhichDiffer
				.stream()
				.filter(dnc -> dnc.getObjectFrom1() != null
						&& ((SHNaturalClass) dnc.getObjectFrom1()).getNCName().equals(
								naturalClass.getNCName())).collect(Collectors.toList());
		if (sameNaturalClassesName.size() > 0) {
			DifferentSHNaturalClass diffNaturalClass = sameNaturalClassesName.get(0);
			diffNaturalClass.setObjectFrom2(naturalClass);
		} else {
			DifferentSHNaturalClass diffNaturalClass = new DifferentSHNaturalClass(null,
					naturalClass);
			naturalClassesWhichDiffer.add(diffNaturalClass);
		}
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(onca1, words1);
		syllabifyWords(onca2, words2);
	}

	protected void syllabifyWords(ONCApproach onc, List<Word> words) {
		ONCSyllabifier oncSyllabifier = new ONCSyllabifier(onc);
		for (Word word : words) {
			boolean fSuccess = oncSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setONCPredictedSyllabification(oncSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	public void compareSonorityHierarchyOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String sonorityHierarchy1 = createTextFromSonorityHierarchy(onca1);
		String sonorityHierarchy2 = createTextFromSonorityHierarchy(onca2);
		sonorityHierarchyOrderDifferences = dmp.diff_main(sonorityHierarchy1, sonorityHierarchy2);
	}

	protected String createTextFromSonorityHierarchy(ONCApproach sha) {
		StringBuilder sb = new StringBuilder();
		sha.getONCSonorityHierarchy().stream().forEach(nc -> {
			sb.append(nc.getNCName());
			sb.append("\t");
			sb.append(nc.getSegmentsRepresentation());
			sb.append("\n");
		});
		return sb.toString();
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getONCPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getONCPredictedSyllabification());
	}

	@Override
	protected boolean isReallySameSegment(Segment segment1, Segment segment2) {
		boolean result = super.isReallySameSegment(segment1, segment2);
		if (result) {
			if ((segment1.isCoda() != segment2.isCoda()) ||
				(segment1.isNucleus() != segment2.isNucleus()) ||
				(segment1.isOnset() != segment2.isOnset())) {
				return false;
			}
		}
		return result;
	}
}
