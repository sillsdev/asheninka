// Copyright (c) 2025 SIL International 
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

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

/**
 * @author Andy Black
 *
 */
public class HyphenApproachLanguageComparisonHTMLFormatter extends
		ApproachLanguageComparisonHTMLFormatter {

	HyphenApproachLanguageComparer hyphenComparer;

	public HyphenApproachLanguageComparisonHTMLFormatter(HyphenApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, comparer.getCva1().getLanguageProject(), comparer.getCva2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.hyphenComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public HyphenApproachLanguageComparisonHTMLFormatter(HyphenApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, comparer.getCva1().getLanguageProject(), comparer.getCva2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, dateTime);
		this.hyphenComparer = comparer;
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, bundle.getString("report.shtitle"));
		formatOverview(sb, bundle.getString("report.shcomparisonof"));
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatHyphenClasses(sb);
		formatHyphenClasssOrder(sb);
		//TODO: change rules
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatHyphenClasses(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.shsonorityhierarchy") + "</h3>\n");
		SortedSet<DifferentHyphenClass> diffHyphenClasses = hyphenComparer
				.getHyphenClassesWhichDiffer();
		if (diffHyphenClasses.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sameshsonorityhierarchy") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.shnaturalclasseswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentHyphenClass differentHyphenClass : diffHyphenClasses) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				HyphenClass naturalClass = (HyphenClass) differentHyphenClass.objectFrom1;
				formatHyphenClassInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (HyphenClass) differentHyphenClass.objectFrom2;
				formatHyphenClassInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatHyphenClassInfo(StringBuilder sb, HyphenClass hyphenClass) {
		if (hyphenClass == null) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(hyphenClass.getClassName());
			sb.append(" (");
			sb.append(hyphenClass.getSegmentsRepresentation());
			sb.append(")");
		}
	}

	protected void formatHyphenClasssOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = hyphenComparer.getHyphenChangeRuleOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.shsonorityhierarchyorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<HyphenClass> sonorityHierarchy1 = hyphenComparer.getCva1().getHyphenClasses();
			List<HyphenClass> sonorityHierarchy2 = hyphenComparer.getCva2().getHyphenClasses();
			int size1 = sonorityHierarchy1.size();
			int size2 = sonorityHierarchy2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				HyphenClass naturalClass = formatSonorityHierarchyInOrder(sonorityHierarchy1,
						size1, i);
				formatHyphenClassInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = formatSonorityHierarchyInOrder(sonorityHierarchy2, size2, i);
				formatHyphenClassInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected HyphenClass formatSonorityHierarchyInOrder(List<HyphenClass> naturalClasses,
			int size1, int i) {
		HyphenClass naturalClass;
		if (i < size1) {
			naturalClass = (HyphenClass) naturalClasses.get(i);
		} else {
			naturalClass = null;
		}
		return naturalClass;
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getSHPredictedSyllabification().length() == 0) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(word.getSHPredictedSyllabification());
		}
	}
}
