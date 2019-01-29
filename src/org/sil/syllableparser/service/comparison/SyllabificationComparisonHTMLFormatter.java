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
public class SyllabificationComparisonHTMLFormatter extends
		ApproachLanguageComparisonHTMLFormatter
{

	SyllabificationsComparer sylComparer;

	public SyllabificationComparisonHTMLFormatter(SyllabificationsComparer comparer,
			Locale locale) {
		super(comparer, comparer.getLanguageProject(), comparer.getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.sylComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public SyllabificationComparisonHTMLFormatter(SyllabificationsComparer comparer,
			Locale locale, LocalDateTime dateTime) {
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
	
	protected void formatSyllabifications(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.syllabifications") + "</h3>\n");
		SortedSet<Word> diffWords = sylComparer.getSyllabificationsWhichDiffer();
		if (diffWords.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samesyllabifications") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.syllabificationswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(bundle.getString("approach.cv"));
			sb.append("</th>\n<th>");
			sb.append(bundle.getString("approach.sonorityhierarchy"));
			sb.append("</th>\n<th>");
			sb.append(bundle.getString("report.syllabification"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (Word differentWord : diffWords) {
				sb.append("<tr>\n<td rowspan=\"2\" valign=\"top\" class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\">");
				String cv = differentWord.getCVPredictedSyllabification();
				formatWordInfo(sb, cv);
				sb.append("</td>\n<td rowspan=\"2\" valign=\"bottom\" class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				String sh = differentWord.getSHPredictedSyllabification();
				formatWordInfo(sb, sh);
				sb.append("</td>\n<td class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\">");
				formatSyllabificationInfo(sb, cv);
				sb.append("</td>\n</tr>\n");
				sb.append("<tr>\n<td class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				formatSyllabificationInfo(sb, sh);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
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
