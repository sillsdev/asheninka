// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class SHApproachLanguageComparisonHTMLFormatter extends
		ApproachLanguageComparisonHTMLFormatter {

	SHApproachLanguageComparer shComparer;

	public SHApproachLanguageComparisonHTMLFormatter(SHApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, comparer.getSha1().getLanguageProject(), comparer.getSha2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.shComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public SHApproachLanguageComparisonHTMLFormatter(SHApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, comparer.getSha1().getLanguageProject(), comparer.getSha2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, dateTime);
		this.shComparer = comparer;
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, bundle.getString("report.shtitle"));
		formatOverview(sb, bundle.getString("report.shcomparisonof"));
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatSonorityHierarchy(sb);
		formatSonorityHierarchyOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatSonorityHierarchy(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.shsonorityhierarchy") + "</h3>\n");
		SortedSet<DifferentSHNaturalClass> diffNaturalCLasses = shComparer
				.getNaturalClassesWhichDiffer();
		if (diffNaturalCLasses.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sameshsonorityhierarchy") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.shnaturalclasseswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentSHNaturalClass differentNaturalCLass : diffNaturalCLasses) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				SHNaturalClass naturalClass = (SHNaturalClass) differentNaturalCLass.objectFrom1;
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (SHNaturalClass) differentNaturalCLass.objectFrom2;
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatNaturalClassInfo(StringBuilder sb, SHNaturalClass naturalClass) {
		if (naturalClass == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(naturalClass.getNCName());
			sb.append(" (");
			sb.append(naturalClass.getSegmentsRepresentation());
			sb.append(")");
		}
	}

	protected void formatSonorityHierarchyOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = shComparer.getSonorityHierarchyOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.shsonorityhierarchyorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<SHNaturalClass> sonorityHierarchy1 = shComparer.getSha1().getSHSonorityHierarchy();
			List<SHNaturalClass> sonorityHierarchy2 = shComparer.getSha2().getSHSonorityHierarchy();
			int size1 = sonorityHierarchy1.size();
			int size2 = sonorityHierarchy2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				SHNaturalClass naturalClass = formatSonorityHierarchyInOrder(sonorityHierarchy1,
						size1, i);
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = formatSonorityHierarchyInOrder(sonorityHierarchy2, size2, i);
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected SHNaturalClass formatSonorityHierarchyInOrder(List<SHNaturalClass> naturalClasses,
			int size1, int i) {
		SHNaturalClass naturalClass;
		if (i < size1) {
			naturalClass = (SHNaturalClass) naturalClasses.get(i);
		} else {
			naturalClass = null;
		}
		return naturalClass;
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getSHPredictedSyllabification().length() == 0) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getSHPredictedSyllabification());
		}
	}
}
