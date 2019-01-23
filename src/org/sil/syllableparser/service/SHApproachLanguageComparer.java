// Copyright (c) 2019 SIL International
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
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * @author Andy Black
 *
 */
public class SHApproachLanguageComparer extends ApproachLanguageComparer {

	SHApproach sha1;
	SHApproach sha2;

	SortedSet<DifferentSHNaturalClass> naturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentSHNaturalClass::getSortingValue));
	LinkedList<Diff> sonorityHierarchyOrderDifferences = new LinkedList<>();

	public SHApproachLanguageComparer(SHApproach sha1, SHApproach sha2) {
		super(sha1.getLanguageProject(), sha2.getLanguageProject());
		this.sha1 = sha1;
		this.sha2 = sha2;
	}

	public SHApproach getSha1() {
		return sha1;
	}

	public void setSha1(SHApproach sha1) {
		this.sha1 = sha1;
	}

	public SHApproach getSha2() {
		return sha2;
	}

	public void setSha2(SHApproach sha2) {
		this.sha2 = sha2;
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
		compareWords();
	}

	public void compareSonorityHierarchy() {
		List<SHNaturalClass> naturalClasses1 = sha1.getSHSonorityHierarchy();
		List<SHNaturalClass> naturalClasses2 = sha2.getSHSonorityHierarchy();

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
		syllabifyWords(sha1, words1);
		syllabifyWords(sha2, words2);
	}

	protected void syllabifyWords(SHApproach sha, List<Word> words) {
		SHSyllabifier shSyllabifier = new SHSyllabifier(sha);
		for (Word word : words) {
			boolean fSuccess = shSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setSHPredictedSyllabification(shSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	public void compareSonorityHierarchyOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String sonorityHierarchy1 = createTextFromSonorityHierarchy(sha1);
		String sonorityHierarchy2 = createTextFromSonorityHierarchy(sha2);
		sonorityHierarchyOrderDifferences = dmp.diff_main(sonorityHierarchy1, sonorityHierarchy2);
	}

	protected String createTextFromSonorityHierarchy(SHApproach sha) {
		StringBuilder sb = new StringBuilder();
		sha.getSHSonorityHierarchy().stream().forEach(nc -> {
			sb.append(nc.getNCName());
			sb.append("\t");
			sb.append(nc.getSegmentsRepresentation());
			sb.append("\n");
		});
		return sb.toString();
	}
}
