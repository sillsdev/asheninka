// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparisonHTMLFormatter extends ApproachLanguageComparisonHTMLFormatter {

	CVApproachLanguageComparer cvComparer;

	public CVApproachLanguageComparisonHTMLFormatter(CVApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, comparer.getCva1().getLanguageProject(), comparer.getCva2().getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.cvComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public CVApproachLanguageComparisonHTMLFormatter(CVApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, comparer.getCva1().getLanguageProject(), comparer.getCva2().getLanguageProject(), locale, dateTime);
		initialize(comparer, locale, dateTime);
		this.cvComparer = comparer;
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, bundle.getString("report.cvtitle"));
		formatOverview(sb, bundle.getString("report.cvcomparisonof"));
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatNaturalClasses(sb, cvComparer.getNaturalClassesWhichDiffer());
		formatSyllablePatterns(sb);
		formatSyllablePatternOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatSyllablePatterns(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.cvsyllablepatterns") + "</h3>\n");
		SortedSet<DifferentCVSyllablePattern> diffSyllablePatterns = cvComparer
				.getSyllablePatternsWhichDiffer();
		if (diffSyllablePatterns.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samecvsyllablepatterns") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.cvsyllablepatternswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentCVSyllablePattern differentSyllablePattern : diffSyllablePatterns) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				CVSyllablePattern syllablePattern = (CVSyllablePattern) differentSyllablePattern.objectFrom1;
				formatSyllablePatternInfo(sb, syllablePattern);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				syllablePattern = (CVSyllablePattern) differentSyllablePattern.objectFrom2;
				formatSyllablePatternInfo(sb, syllablePattern);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatSyllablePatternInfo(StringBuilder sb, CVSyllablePattern syllablePattern) {
		if (syllablePattern == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(syllablePattern.getSPName());
			sb.append(" (");
			sb.append(syllablePattern.getNCSRepresentation());
			sb.append(")");
		}
	}

	protected void formatSyllablePatternOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = cvComparer.getSyllablePatternOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.cvsyllablepatternsorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<CVSyllablePattern> sylPatterns1 = cvComparer.getCva1().getCVSyllablePatterns();
			List<CVSyllablePattern> sylPatterns2 = cvComparer.getCva2().getCVSyllablePatterns();
			int size1 = sylPatterns1.size();
			int size2 = sylPatterns2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				CVSyllablePattern syllablePattern = formatSyllablePatternInOrder(sylPatterns1,
						size1, i);
				formatSyllablePatternInfo(sb, syllablePattern);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				syllablePattern = formatSyllablePatternInOrder(sylPatterns2, size2, i);
				formatSyllablePatternInfo(sb, syllablePattern);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected CVSyllablePattern formatSyllablePatternInOrder(List<CVSyllablePattern> sylPatterns1,
			int size1, int i) {
		CVSyllablePattern syllablePattern1;
		if (i < size1) {
			syllablePattern1 = (CVSyllablePattern) sylPatterns1.get(i);
		} else {
			syllablePattern1 = null;
		}
		return syllablePattern1;
	}

	protected void formatHTMLEnding(StringBuilder sb) {
		sb.append("</body>\n</html>\n");
	}

	protected String getAdjectivalForm(String mainProperty, String adjectivalEnding) {
		Object[] args = { bundle.getString(adjectivalEnding) };
		MessageFormat msgFormatter = new MessageFormat("");
		msgFormatter.setLocale(locale);
		msgFormatter.applyPattern(bundle.getString(mainProperty));
		return msgFormatter.format(args);
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getCVPredictedSyllabification().length() == 0) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getCVPredictedSyllabification());
		}
	}
}
