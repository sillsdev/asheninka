// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.SortedSet;

import org.sil.syllableparser.model.Word;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class SyllabificationComparisonHTMLFormatter extends ApproachLanguageComparisonHTMLFormatter {

	SyllabificationsComparer sylComparer;

	public SyllabificationComparisonHTMLFormatter(SyllabificationsComparer comparer, Locale locale) {
		super(comparer, comparer.getLanguageProject(), comparer.getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.sylComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public SyllabificationComparisonHTMLFormatter(SyllabificationsComparer comparer, Locale locale,
			LocalDateTime dateTime) {
		super(comparer, comparer.getLanguageProject(), comparer.getLanguageProject(), locale);
		initialize(comparer, locale, dateTime);
		this.sylComparer = comparer;
	}

	public String format() {
		sylComparer.compareSyllabifications();
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, bundle.getString("report.syllabificationstitle"));
		formatOverview(sb, bundle.getString("report.syllabificationscomparisonof"));
		formatSyllabifications(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatRowLabelDescriptions(StringBuilder sb) {
		sb.append("<p/>\n<table>\n<tbody>\n");
		if (sylComparer.isUseCVApproach()) {
			formatApproachLabelDescription(sb, bundle.getString("report.cvapproachabbreviation"),
					bundle.getString("approach.cv"));
		}
		if (sylComparer.isUseSHApproach()) {
			formatApproachLabelDescription(sb, bundle.getString("report.shapproachabbreviation"),
					bundle.getString("approach.sonorityhierarchy"));
		}
		if (sylComparer.isUseONCApproach()) {
			formatApproachLabelDescription(sb, bundle.getString("report.oncapproachabbreviation"),
					bundle.getString("approach.onc"));
		}
		sb.append("</tbody>\n</table>\n<p/>\n");
	}

	protected void formatSyllabifications(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.syllabifications") + "</h3>\n");
		SortedSet<Word> diffWords = sylComparer.getSyllabificationsWhichDiffer();
		if (diffWords.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samesyllabifications") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.syllabificationswhichdiffer") + "</p>\n");
			formatRowLabelDescriptions(sb);
			sb.append("<table border=\"1\">\n");
			sb.append("<tbody>\n");
			for (Word differentWord : diffWords) {
				// extra gap between words:
				sb.append("<tr><td colspan=\"3\"/></tr>\n");
				sb.append("<tr><td rowspan=\"3\">");
				sb.append(differentWord.getWord());
				sb.append("</td>");
				boolean isFirstRow = true;
				if (sylComparer.isUseCVApproach()) {
					formatApproachRow(sb, bundle.getString("report.cvapproachabbreviation"),
							differentWord.getCVPredictedSyllabification(), isFirstRow);
					isFirstRow = false;
				}
				if (sylComparer.isUseSHApproach()) {
					formatApproachRow(sb, bundle.getString("report.shapproachabbreviation"),
							differentWord.getSHPredictedSyllabification(), isFirstRow);
					isFirstRow = false;
				}
				if (sylComparer.isUseONCApproach()) {
					formatApproachRow(sb, bundle.getString("report.oncapproachabbreviation"),
							differentWord.getONCPredictedSyllabification(), isFirstRow);
					isFirstRow = false;
				}
//				sb.append("</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatApproachLabelDescription(StringBuilder sb, String approachAbbreviation,
			String approachName) {
		sb.append("<tr>\n<td>");
		sb.append(approachAbbreviation);
		sb.append("&#xa0;");
		sb.append("</td>\n");
		sb.append("<td>&#xa0;=&#xa0;</td>\n");
		sb.append("<td>&#xa0;");
		formatWordInfo(sb, approachName);
		sb.append("</td>\n</tr>\n");
	}

	protected void formatApproachRow(StringBuilder sb, String approachAbbreviation,
			String approachSyllabification, boolean isFirstRow) {
		if (!isFirstRow) {
			sb.append("<tr>\n");
		}
		sb.append("<td>");
		sb.append(approachAbbreviation);
		sb.append("&#xa0;");
		sb.append("</td>\n");
		sb.append("<td class=\"");
		sb.append(VERNACULAR_1);
		sb.append("\">&#xa0;");
		formatWordInfo(sb, approachSyllabification);
		sb.append("</td>\n</tr>\n");
	}

	protected void formatWordInfo(StringBuilder sb, String syllabification) {
		if (StringUtilities.isNullOrEmpty(syllabification)) {
			sb.append("<span style='color:");
			sb.append(bundle.getString("report.failurecolorword"));
			sb.append(";'>");
			sb.append(bundle.getString("report.syllabificationfailed"));
			sb.append("</span>");
		} else {
			sb.append(syllabification);
		}
	}

	protected void formatSyllabificationInfo(StringBuilder sb, String syllabification) {
		if (StringUtilities.isNullOrEmpty(syllabification)) {
			sb.append("&#xa0;");
		} else {
			sb.append(syllabification);
		}
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		// we don't use this for this formatter
	}
}
