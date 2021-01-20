// Copyright (c) 2020-2021 SIL International
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

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.parsing.MoraicSyllabifier;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * @author Andy Black
 *
 */
public class MoraicApproachLanguageComparer extends ApproachLanguageComparer {

	MoraicApproach mua1;
	MoraicApproach mua2;

	SortedSet<DifferentSHNaturalClass> naturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentSHNaturalClass::getSortingValue));
	LinkedList<Diff> sonorityHierarchyOrderDifferences = new LinkedList<>();
	SortedSet<DifferentCVNaturalClass> cvNaturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVNaturalClass::getSortingValue));
	SortedSet<DifferentFilter> filtersWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentFilter::getSortingValue));
	LinkedList<Diff> filterOrderDifferences = new LinkedList<>();
	SortedSet<DifferentTemplate> templatesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentTemplate::getSortingValue));
	LinkedList<Diff> templateOrderDifferences = new LinkedList<>();

	public MoraicApproachLanguageComparer(MoraicApproach mua1, MoraicApproach mua2) {
		super(mua1.getLanguageProject(), mua2.getLanguageProject());
		this.mua1 = mua1;
		this.mua2 = mua2;
	}

	public MoraicApproach getMua1() {
		return mua1;
	}

	public void setMua1(MoraicApproach mua1) {
		this.mua1 = mua1;
	}

	public MoraicApproach getMua2() {
		return mua2;
	}

	public void setMua2(MoraicApproach mua2) {
		this.mua2 = mua2;
	}

	public SortedSet<DifferentSHNaturalClass> getNaturalClassesWhichDiffer() {
		return naturalClassesWhichDiffer;
	}

	public SortedSet<DifferentCVNaturalClass> getCVNaturalClassesWhichDiffer() {
		return cvNaturalClassesWhichDiffer;
	}

	public LinkedList<Diff> getSonorityHierarchyOrderDifferences() {
		return sonorityHierarchyOrderDifferences;
	}

	public SortedSet<DifferentFilter> getFiltersWhichDiffer() {
		return filtersWhichDiffer;
	}

	public LinkedList<Diff> getFilterOrderDifferences() {
		return filterOrderDifferences;
	}

	public SortedSet<DifferentTemplate> getTemplatesWhichDiffer() {
		return templatesWhichDiffer;
	}

	public LinkedList<Diff> getTemplateOrderDifferences() {
		return templateOrderDifferences;
	}

	@Override
	public void compare() {
		compareSegmentInventory();
		compareSonorityHierarchy();
		compareGraphemeNaturalClasses();
		compareEnvironments();
		compareSonorityHierarchy();
		compareSonorityHierarchyOrder();
		compareCVNaturalClasses(mua1.getActiveCVNaturalClasses(), mua2.getActiveCVNaturalClasses(),
				cvNaturalClassesWhichDiffer);
		compareFilters();
		compareFilterOrder();
		compareTemplates();
		compareTemplateOrder();
		compareSyllabificationParameters();
		compareWords();
	}

	public void compareSonorityHierarchy() {
		List<SHNaturalClass> naturalClasses1 = mua1.getMoraicSonorityHierarchy();
		List<SHNaturalClass> naturalClasses2 = mua2.getMoraicSonorityHierarchy();

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

	public void compareFilters() {
		List<Filter> filters1 = mua1.getLanguageProject().getFilters().filtered(f -> f.isActive());
		List<Filter> filters2 = mua2.getLanguageProject().getFilters().filtered(f -> f.isActive());

		Set<Filter> difference1from2 = new HashSet<Filter>(filters1);
		// use set difference (removeAll)
		difference1from2.removeAll(filters2);
		difference1from2.stream().forEach(
				filter -> filtersWhichDiffer.add(new DifferentFilter(
						filter, null)));

		Set<Filter> difference2from1 = new HashSet<Filter>(filters2);
		difference2from1.removeAll(filters1);
		difference2from1.stream().forEach(
				filter -> mergeSimilarFilters(filter));
	}

	protected void mergeSimilarFilters(Filter filter) {
		List<DifferentFilter> sameFilterRepresentation = filtersWhichDiffer
				.stream()
				.filter(f -> f.getObjectFrom1() != null
						&& ((Filter) f.getObjectFrom1()).getTemplateFilterName().equals(
								filter.getTemplateFilterName())).collect(Collectors.toList());
		if (sameFilterRepresentation.size() > 0) {
			DifferentFilter diffFilter = sameFilterRepresentation.get(0);
			diffFilter.setObjectFrom2(filter);
		} else {
			DifferentFilter diffFilter = new DifferentFilter(null,
					filter);
			filtersWhichDiffer.add(diffFilter);
		}
	}

	public void compareFilterOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String filters1 = createTextFromFilters(mua1);
		String filters2 = createTextFromFilters(mua2);
		filterOrderDifferences = dmp.diff_main(filters1, filters2);
	}

	protected String createTextFromFilters(MoraicApproach mua) {
		return createTextFromTemplateFilter(mua.getLanguageProject().getActiveAndValidFilters());
	}

	public void compareTemplates() {
		List<Template> templates1 = mua1.getLanguageProject().getTemplates().filtered(t -> t.isActive());
		List<Template> templates2 = mua2.getLanguageProject().getTemplates().filtered(t -> t.isActive());

		Set<Template> difference1from2 = new HashSet<Template>(templates1);
		// use set difference (removeAll)
		difference1from2.removeAll(templates2);
		difference1from2.stream().forEach(
				template -> templatesWhichDiffer.add(new DifferentTemplate(
						template, null)));

		Set<Template> difference2from1 = new HashSet<Template>(templates2);
		difference2from1.removeAll(templates1);
		difference2from1.stream().forEach(
				template -> mergeSimilarTemplates(template));
	}

	protected void mergeSimilarTemplates(Template template) {
		List<DifferentTemplate> sameTemplateRepresentation = templatesWhichDiffer
				.stream()
				.filter(t -> t.getObjectFrom1() != null
						&& ((Template) t.getObjectFrom1()).getTemplateFilterName().equals(
								template.getTemplateFilterName())).collect(Collectors.toList());
		if (sameTemplateRepresentation.size() > 0) {
			DifferentTemplate diffTemplate = sameTemplateRepresentation.get(0);
			diffTemplate.setObjectFrom2(template);
		} else {
			DifferentTemplate diffTemplate = new DifferentTemplate(null,
					template);
			templatesWhichDiffer.add(diffTemplate);
		}
	}

	public void compareTemplateOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String templates1 = createTextFromTemplates(mua1);
		String templates2 = createTextFromTemplates(mua2);
		templateOrderDifferences = dmp.diff_main(templates1, templates2);
	}

	protected String createTextFromTemplates(MoraicApproach mua) {
		return createTextFromTemplateFilter(mua.getLanguageProject().getActiveAndValidTemplates());
	}

	protected String createTextFromTemplateFilter(List<? extends TemplateFilter> list) {
		StringBuilder sb = new StringBuilder();
		list.stream().forEach(tf -> {
			sb.append(tf.getTemplateFilterName());
			sb.append("\t");
			sb.append(tf.getTemplateFilterRepresentation());
			sb.append("\t");
			sb.append(tf.getType());
			sb.append("\n");
		});
		return sb.toString();

	}
	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(mua1, words1);
		syllabifyWords(mua2, words2);
	}

	protected void syllabifyWords(MoraicApproach mua, List<Word> words) {
		MoraicSyllabifier moraicSyllabifier = new MoraicSyllabifier(mua);
		for (Word word : words) {
			boolean fSuccess = moraicSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setMoraicPredictedSyllabification(moraicSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	public void compareSonorityHierarchyOrder() {
		diff_match_patch dmp = new diff_match_patch();
		String sonorityHierarchy1 = createTextFromSonorityHierarchy(mua1);
		String sonorityHierarchy2 = createTextFromSonorityHierarchy(mua2);
		sonorityHierarchyOrderDifferences = dmp.diff_main(sonorityHierarchy1, sonorityHierarchy2);
	}

	protected String createTextFromSonorityHierarchy(MoraicApproach mua) {
		StringBuilder sb = new StringBuilder();
		mua.getMoraicSonorityHierarchy().stream().forEach(nc -> {
			sb.append(nc.getNCName());
			sb.append("\t");
			sb.append(nc.getSegmentsRepresentation());
			sb.append("\n");
		});
		return sb.toString();
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getMoraicPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getMoraicPredictedSyllabification());
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
