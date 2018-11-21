// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.utility.DateTimeNormalizer;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparisonHTMLFormatter {

	CVApproachLanguageComparer comparer;
	Locale locale;
	ResourceBundle bundle;
	LocalDateTime dateTime;
	Language analysis1;
	Language analysis2;
	Language vernacular1;
	Language vernacular2;
	final String ANALYSIS_1 = "analysis1";
	final String ANALYSIS_2 = "analysis2";
	final String VERNACULAR_1 = "vernacular1";
	final String VERNACULAR_2 = "vernacular2";

	public CVApproachLanguageComparisonHTMLFormatter(CVApproachLanguageComparer comparer,
			Locale locale) {
		super();
		initialize(comparer, locale, LocalDateTime.now());
	}

	// Used for testing so the date time can be constant
	public CVApproachLanguageComparisonHTMLFormatter(CVApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super();
		initialize(comparer, locale, dateTime);
	}

	protected void initialize(CVApproachLanguageComparer comparer, Locale locale,
			LocalDateTime dateTime) {
		this.comparer = comparer;
		this.locale = locale;
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		getAnalysisAndVernacularLanguages(comparer);
		this.dateTime = dateTime;
	}

	protected void getAnalysisAndVernacularLanguages(CVApproachLanguageComparer comparer) {
		LanguageProject language = comparer.getCva1().getLanguageProject();
		analysis1 = language.getAnalysisLanguage();
		vernacular1 = language.getVernacularLanguage();
		language = comparer.getCva2().getLanguageProject();
		analysis2 = language.getAnalysisLanguage();
		vernacular2 = language.getVernacularLanguage();
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb);
		formatOverview(sb);
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatNaturalClasses(sb);
		formatSyllablePatterns(sb);
		formatSyllablePatternOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatHTMLBeginning(StringBuilder sb) {
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n<title>");
		sb.append(bundle.getString("report.cvtitle"));
		sb.append("</title>\n<style type=\"text/css\">\n");
		createLanguagFontCSS(sb, analysis1, ANALYSIS_1);
		createLanguagFontCSS(sb, analysis2, ANALYSIS_2);
		createLanguagFontCSS(sb, vernacular1, VERNACULAR_1);
		createLanguagFontCSS(sb, vernacular2, VERNACULAR_2);
		sb.append("</style>\n</head>\n<body>\n");
	}

	protected void createLanguagFontCSS(StringBuilder sb, Language language, String sLangTypeNumber) {
		sb.append(".");
		sb.append(sLangTypeNumber);
		sb.append(" {\n\tfont-family:");
		sb.append(language.getFontFamily());
		sb.append(";\n\tfont-size:");
		sb.append(language.getFontSize());
		sb.append(";\n\tfont-style:");
		sb.append(language.getFontType());
		sb.append(";\n}\n");
	}

	protected void formatOverview(StringBuilder sb) {
		sb.append("<h2>" + bundle.getString("report.comparisonof") + "</h2>\n");
		sb.append("<ol><li>");
		sb.append(comparer.getDataSet1Info());
		sb.append("</li>\n<li>");
		sb.append(comparer.getDataSet2Info());
		sb.append("</li>\n</ol>\n");
		sb.append("<div>" + bundle.getString("report.performedon"));
		sb.append(DateTimeNormalizer.normalizeDateTimeWithWords(dateTime, locale));
		sb.append("</div>\n");
	}

	protected void formatSegmentInventory(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.segmentinventory") + "</h3>\n");
		SortedSet<DifferentSegment> diffSegments = comparer.getSegmentsWhichDiffer();
		if (diffSegments.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samesegmentinventory") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.segmentswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentSegment differentSegment : diffSegments) {
				sb.append("<tr>\n<td class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\" valign=\"top\">");
				Segment seg = (Segment) differentSegment.objectFrom1;
				formatSegmentInfo(sb, seg);
				sb.append("</td>\n<td class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\" valign=\"top\">");
				seg = (Segment) differentSegment.objectFrom2;
				formatSegmentInfo(sb, seg);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatSegmentInfo(StringBuilder sb, Segment seg) {
		if (seg == null) {
			sb.append("&#xa0;");
		} else {
			List<Grapheme> graphemes = seg.getActiveGraphs();
			int iNumGraphemes = graphemes.size();
			if (iNumGraphemes > 0) {
				sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
				sb.append(bundle.getString("label.segment"));
				sb.append("</th>\n<th>");
				sb.append(bundle.getString("report.graphemes"));
				sb.append("</th>\n<th>");
				sb.append(bundle.getString("report.environments"));
				sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
				sb.append("<tr>\n");
				sb.append("<td rowspan=\"" + iNumGraphemes + "\" valign=\"top\">");
				sb.append(seg.getSegment());
				int i = 0;
				for (Grapheme grapheme : graphemes) {
					if (i > 0) {
						sb.append("<tr>\n");
					}
					sb.append("<td>");
					sb.append(grapheme.getForm());
					sb.append("</td>\n<td>");
					sb.append(grapheme.getEnvsRepresentation());
					sb.append("</td>\n</tr>\n");
					i++;
				}
				sb.append("</tbody>\n</table>\n");
			}
		}
	}

	protected void formatGraphemeNaturalClasses(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.graphemenaturalclasses") + "</h3>\n");
		SortedSet<DifferentGraphemeNaturalClass> diffGraphemeNaturalClasses = comparer
				.getGraphemeNaturalClassesWhichDiffer();
		if (diffGraphemeNaturalClasses.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samegraphemenaturalclasses") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.graphemenaturalclasseswhichdiffer")
					+ "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentGraphemeNaturalClass differentGraphemeNaturalClass : diffGraphemeNaturalClasses) {
				sb.append("<tr>\n<td class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\">");
				GraphemeNaturalClass gnc = (GraphemeNaturalClass) differentGraphemeNaturalClass.objectFrom1;
				formatGraphemeNaturalClassInfo(sb, gnc);
				sb.append("</td>\n<td class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				gnc = (GraphemeNaturalClass) differentGraphemeNaturalClass.objectFrom2;
				formatGraphemeNaturalClassInfo(sb, gnc);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatGraphemeNaturalClassInfo(StringBuilder sb, GraphemeNaturalClass gnc) {
		if (gnc == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(gnc.getNCName());
			sb.append(" (");
			sb.append(gnc.getGNCRepresentation());
			sb.append(")");
		}
	}

	protected void formatEnvironments(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.environments") + "</h3>\n");
		SortedSet<DifferentEnvironment> diffEnvironments = comparer.getEnvironmentsWhichDiffer();
		if (diffEnvironments.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sameenvironments") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.environmentswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentEnvironment differentEnvironment : diffEnvironments) {
				sb.append("<tr>\n<td class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\">");
				Environment gnc = (Environment) differentEnvironment.objectFrom1;
				formatEnvironmentInfo(sb, gnc);
				sb.append("</td>\n<td class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				gnc = (Environment) differentEnvironment.objectFrom2;
				formatEnvironmentInfo(sb, gnc);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatEnvironmentInfo(StringBuilder sb, Environment env) {
		if (env == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(env.getEnvironmentRepresentation());
		}
	}

	protected void formatNaturalClasses(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.cvnaturalclasses") + "</h3>\n");
		SortedSet<DifferentCVNaturalClass> diffNaturalClasses = comparer
				.getNaturalClassesWhichDiffer();
		if (diffNaturalClasses.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samecvnaturalclasses") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.cvnaturalclasseswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingf"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingf"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentCVNaturalClass differentNaturalClass : diffNaturalClasses) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				CVNaturalClass naturalClass = (CVNaturalClass) differentNaturalClass.objectFrom1;
				formatNaturalClassInfo(sb, naturalClass, VERNACULAR_1);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (CVNaturalClass) differentNaturalClass.objectFrom2;
				formatNaturalClassInfo(sb, naturalClass, VERNACULAR_2);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatNaturalClassInfo(StringBuilder sb, CVNaturalClass naturalClass,
			String vernacularCSS) {
		if (naturalClass == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(naturalClass.getNCName());
			sb.append(" (<span class=\"");
			sb.append(vernacularCSS);
			sb.append("\">");
			sb.append(naturalClass.getSNCRepresentation());
			sb.append("</span>)");
		}
	}

	protected void formatSyllablePatterns(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.cvsyllablepatterns") + "</h3>\n");
		SortedSet<DifferentCVSyllablePattern> diffSyllablePatterns = comparer
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
		SortedSet<DifferentCVSyllablePattern> diffSyllablePatterns = comparer
				.getSyllablePatternsWhichDiffer();
		if (diffSyllablePatterns.size() != 0) {
			sb.append("<p>" + bundle.getString("report.cvsyllablepatternsorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<CVSyllablePattern> sylPatterns1 = comparer.getCva1().getCVSyllablePatterns();
			List<CVSyllablePattern> sylPatterns2 = comparer.getCva2().getCVSyllablePatterns();
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

	protected void formatWords(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.words") + "</h3>\n");
		SortedSet<DifferentWord> diffWords = comparer.getWordsWhichDiffer();
		if (diffWords.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samewords") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.wordswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingf"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingf"));
			sb.append("</th>\n<th>");
			sb.append(bundle.getString("report.syllabification"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentWord differentWord : diffWords) {
				sb.append("<tr>\n<td rowspan=\"2\" valign=\"top\" class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\">");
				Word word1 = (Word) differentWord.objectFrom1;
				formatWordInfo(sb, word1);
				sb.append("</td>\n<td rowspan=\"2\" valign=\"bottom\" class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				Word word2 = (Word) differentWord.objectFrom2;
				formatWordInfo(sb, word2);
				sb.append("</td>\n<td class=\"");
				sb.append(VERNACULAR_1);
				sb.append("\">");
				formatCVPredictedSyllabification(sb, word1);
				sb.append("</td>\n</tr>\n");
				sb.append("<tr>\n<td class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				formatCVPredictedSyllabification(sb, word2);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatWordInfo(StringBuilder sb, Word word) {
		if (word == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getWord());
		}
	}

	protected void formatCVPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getCVPredictedSyllabification().length() == 0) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getCVPredictedSyllabification());
		}
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

}