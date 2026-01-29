// Copyright (c) 2025-202 SIL International 
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
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
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
		super(comparer, comparer.getHa1().getLanguageProject(), comparer.getHa2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.hyphenComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public HyphenApproachLanguageComparisonHTMLFormatter(HyphenApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, comparer.getHa1().getLanguageProject(), comparer.getHa2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, dateTime);
		this.hyphenComparer = comparer;
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, bundle.getString("report.hyphentitle"));
		formatOverview(sb, bundle.getString("report.hyphencomparisonof"));
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatHyphenClasses(sb);
		formatHyphenChangeRules(sb);
		formatHyphenChangeRuleOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatHyphenClasses(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.hyphenclasses") + "</h3>\n");
		SortedSet<DifferentHyphenClass> diffHyphenClasses = hyphenComparer
				.getHyphenClassesWhichDiffer();
		if (diffHyphenClasses.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samehyphenclasses") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.hyphenclasseswhichdiffer") + "</p>\n");
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

	protected void formatHyphenChangeRules(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.hyphenchangerules") + "</h3>\n");
		SortedSet<DifferentHyphenChangeRule> diffHyphenChangeRules = hyphenComparer
				.getHyphenChangeRulesWhichDiffer();
		if (diffHyphenChangeRules.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samehyphenchangerules") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.hyphenchangeruleswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentHyphenChangeRule differentHyphenChangeRule : diffHyphenChangeRules) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				HyphenChangeRule hyphenRule = (HyphenChangeRule) differentHyphenChangeRule.objectFrom1;
				formatHyphenChangeRuleInfo(sb, hyphenRule);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				hyphenRule = (HyphenChangeRule) differentHyphenChangeRule.objectFrom2;
				formatHyphenChangeRuleInfo(sb, hyphenRule);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatHyphenChangeRuleInfo(StringBuilder sb, HyphenChangeRule hyphenRule) {
		if (hyphenRule == null) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(hyphenRule.getRuleName());
			sb.append(" (");
			sb.append(hyphenRule.getMatchRepresentation());
			sb.append(" " + Constants.RIGHTWARD_ARROW + " ");
			sb.append(hyphenRule.getChangeRepresentation());
			sb.append(" )");
		}
	}

	protected void formatHyphenChangeRuleOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = hyphenComparer.getHyphenChangeRuleOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.hyphenchangeruleorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<HyphenChangeRule> changeRules1 = hyphenComparer.getHa1().getActiveHyphenChangeRules();
			List<HyphenChangeRule> changeRules2 = hyphenComparer.getHa2().getActiveHyphenChangeRules();
			int size1 = changeRules1.size();
			int size2 = changeRules2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				HyphenChangeRule hyphenRule = formatHyphenChangeRulesInOrder(changeRules1,
						size1, i);
				formatHyphenChangeRuleInfo(sb, hyphenRule);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				hyphenRule = formatHyphenChangeRulesInOrder(changeRules2, size2, i);
				formatHyphenChangeRuleInfo(sb, hyphenRule);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected HyphenChangeRule formatHyphenChangeRulesInOrder(List<HyphenChangeRule> hyphenClasses,
			int size1, int i) {
		HyphenChangeRule hyphenClass;
		if (i < size1) {
			hyphenClass = (HyphenChangeRule) hyphenClasses.get(i);
		} else {
			hyphenClass = null;
		}
		return hyphenClass;
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getHyphenPredictedSyllabification().length() == 0) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(word.getHyphenPredictedSyllabification());
		}
	}
}
