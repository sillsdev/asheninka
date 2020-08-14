// Copyright (c) 2019-2020 SIL International
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
	SortedSet<DifferentCVNaturalClass> cvNaturalClassesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentCVNaturalClass::getSortingValue));
	SortedSet<DifferentFilter> filtersWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentFilter::getSortingValue));
	LinkedList<Diff> filterOrderDifferences = new LinkedList<>();
	SortedSet<DifferentTemplate> templatesWhichDiffer = new TreeSet<>(
			Comparator.comparing(DifferentTemplate::getSortingValue));
	LinkedList<Diff> templateOrderDifferences = new LinkedList<>();

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
		compareCVNaturalClasses(onca1.getActiveCVNaturalClasses(), onca2.getActiveCVNaturalClasses(),
				cvNaturalClassesWhichDiffer);
		compareFilters();
		compareFilterOrder();
		compareTemplates();
		compareTemplateOrder();
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

	public void compareFilters() {
		List<Filter> filters1 = onca1.getLanguageProject().getFilters();
		List<Filter> filters2 = onca2.getLanguageProject().getFilters();

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
		String filters1 = createTextFromFilters(onca1);
		String filters2 = createTextFromFilters(onca2);
		filterOrderDifferences = dmp.diff_main(filters1, filters2);
	}

	protected String createTextFromFilters(ONCApproach onca) {
		return createTextFromTemplateFilter(onca.getLanguageProject().getActiveAndValidFilters());
	}

	public void compareTemplates() {
		List<Template> templates1 = onca1.getLanguageProject().getTemplates();
		List<Template> templates2 = onca2.getLanguageProject().getTemplates();

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
		String templates1 = createTextFromTemplates(onca1);
		String templates2 = createTextFromTemplates(onca2);
		templateOrderDifferences = dmp.diff_main(templates1, templates2);
	}

	protected String createTextFromTemplates(ONCApproach onca) {
		return createTextFromTemplateFilter(onca.getLanguageProject().getActiveAndValidTemplates());
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
