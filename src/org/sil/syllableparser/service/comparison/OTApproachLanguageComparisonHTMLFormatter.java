// Copyright (c) 2021 SIL International 
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
import java.util.Set;
import java.util.SortedSet;

import javafx.scene.paint.Color;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.lingtree.model.FontInfo;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.npapproach.NPFilter;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.service.LingTreeInteractor;

/**
 * @author Andy Black
 *
 */
public class OTApproachLanguageComparisonHTMLFormatter extends
		ONCApproachLanguageComparisonHTMLFormatter {

	NPApproachLanguageComparer npComparer;
	protected LingTreeInteractor ltInteractor;

	public OTApproachLanguageComparisonHTMLFormatter(NPApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.npComparer = comparer;
		ltInteractor = LingTreeInteractor.getInstance();
	}

	// Used for testing so the date time can be constant
	public OTApproachLanguageComparisonHTMLFormatter(NPApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, locale);
		initialize(comparer, locale, dateTime);
		this.npComparer = comparer;
		ltInteractor = LingTreeInteractor.getInstance();
	}

	@Override
	public String format() {
		return format(bundle.getString("report.nptitle"), bundle.getString("report.npcomparisonof"));
	}

	public String format(String sTitle, String sComparisonOf) {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, sTitle);
		formatOverview(sb, sComparisonOf);
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatSonorityHierarchy(sb);
		formatSonorityHierarchyOrder(sb);
		formatSyllabifcationParameters(sb);
		formatNaturalClasses(sb, npComparer.getCVNaturalClassesWhichDiffer());
		formatNPFilters(sb);
		formatNPFilterOrder(sb);
		formatNPRules(sb);
		formatNPRuleOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatNPFilters(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.filters") + "</h3>\n");
		SortedSet<DifferentFilter> diffFilters = npComparer
				.getFiltersWhichDiffer();
		if (diffFilters.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samefilters") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.filterswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentFilter differentFilters : diffFilters) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				Filter filter = (Filter) differentFilters.objectFrom1;
				formatFilterInfo(sb, filter);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				filter = (Filter) differentFilters.objectFrom2;
				formatFilterInfo(sb, filter);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatFilterInfo(StringBuilder sb, Filter filter) {
		if (filter == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(filter.getTemplateFilterName());
			sb.append(" {");
			boolean isRepair = filter.getAction().isDoRepair();
			sb.append(isRepair ? bundle.getString("radio.repairaction") : bundle.getString("radio.failaction"));
			sb.append("} [");
			sb.append(filter.getType());
			sb.append("] (");
			sb.append(filter.getTemplateFilterRepresentation());
			sb.append(")");
		}
	}

	protected void formatNPFilterOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = npComparer.getFilterOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.filterinorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<NPFilter> npFilters1 = npComparer.getNpa1().getValidActiveNPFilters();
			List<NPFilter> npFilters2 = npComparer.getNpa2().getValidActiveNPFilters();
			int size1 = npFilters1.size();
			int size2 = npFilters2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				Filter naturalClass = (Filter) formatSylParserObjectInOrder(npFilters1,
						size1, i);
				formatFilterInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (Filter) formatSylParserObjectInOrder(npFilters2, size2, i);
				formatFilterInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatSyllabifcationParameters(StringBuilder sb) {
		String s1 = "";
		String s2 = "";
		sb.append("<h3>" + bundle.getString("report.syllabificationparameters") + "</h3>\n");
		formatOnsetPrinciple(sb, s1, s2);
		formatApproachSpecificSyllabificationParameters(sb);
	}

	protected void formatNPRules(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.rules") + "</h3>\n");
		SortedSet<DifferentNPRule> diffRules = npComparer.getRulesWhichDiffer();
		if (diffRules.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samerules") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.ruleswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentNPRule differentFilters : diffRules) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\" valign=\"top\">");
				NPRule filter = (NPRule) differentFilters.objectFrom1;
				formatNPRuleInfo(sb, filter);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\" valign=\"top\">");
				filter = (NPRule) differentFilters.objectFrom2;
				formatNPRuleInfo(sb, filter);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatNPRuleInfo(StringBuilder sb, NPRule rule) {
		if (rule == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(rule.getRuleName());
			sb.append(" {");
			String localizedName = bundle.getString("nprule.action." + rule.getRuleAction().toString().toLowerCase());
			sb.append(localizedName);
			sb.append("}<br/><br/>");
			ltInteractor.initializeParameters(langProj1);
			FontInfo fiAnalysis = new FontInfo(langProj1.getAnalysisLanguage().getFont());
			fiAnalysis.setColor(Color.BLACK);
			ltInteractor.setLexicalFontInfo(fiAnalysis);
			ltInteractor.setVerticalGap(30.0);
			String ltSVG = ltInteractor.createSVG(rule.getRuleRepresentation(), true);
			ltSVG = rule.adjustForAffectedSVG(ltSVG);
			sb.append(ltSVG);
		}
	}

	protected void formatNPRuleOrder(StringBuilder sb) {
		Set<DifferentNPRule> diffs = npComparer.getRulesWhichDiffer();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.rulesinorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<NPRule> npRules1 = npComparer.getNpa1().getValidActiveNPRules();
			List<NPRule> npRules2 = npComparer.getNpa2().getValidActiveNPRules();
			int size1 = npRules1.size();
			int size2 = npRules2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\" valign=\"top\">");
				NPRule rule = (NPRule) formatSylParserObjectInOrder(npRules1,
						size1, i);
				formatNPRuleInfo(sb, rule);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\" valign=\"top\">");
				rule = (NPRule) formatSylParserObjectInOrder(npRules2, size2, i);
				formatNPRuleInfo(sb, rule);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getNPPredictedSyllabification().length() == 0) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getNPPredictedSyllabification());
		}
	}
}
