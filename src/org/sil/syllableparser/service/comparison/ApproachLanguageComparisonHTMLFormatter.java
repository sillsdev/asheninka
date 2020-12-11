// Copyright (c) 2019-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedSet;

import javafx.geometry.NodeOrientation;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.utility.DateTimeNormalizer;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public abstract class ApproachLanguageComparisonHTMLFormatter {

	protected ApproachLanguageComparer comparer;
	Locale locale;
	ResourceBundle bundle;
	LocalDateTime dateTime;
	LanguageProject langProj1;
	LanguageProject langProj2;
	Language analysis1;
	Language analysis2;
	Language vernacular1;
	Language vernacular2;
	final String ANALYSIS_1 = "analysis1";
	final String ANALYSIS_2 = "analysis2";
	final String VERNACULAR_1 = "vernacular1";
	final String VERNACULAR_2 = "vernacular2";

	public ApproachLanguageComparisonHTMLFormatter(ApproachLanguageComparer comparer,
			LanguageProject lang1, LanguageProject lang2, Locale locale) {
		super();
		langProj1 = lang1;
		langProj2 = lang2;
		initialize(comparer, locale, LocalDateTime.now());
	}

	// Used for testing so the date time can be constant
	public ApproachLanguageComparisonHTMLFormatter(ApproachLanguageComparer comparer,
			LanguageProject lang1, LanguageProject lang2, Locale locale, LocalDateTime dateTime) {
		super();
		langProj1 = lang1;
		langProj2 = lang2;
		initialize(comparer, locale, dateTime);
	}

	protected void initialize(ApproachLanguageComparer comparer, Locale locale,
			LocalDateTime dateTime) {
		this.comparer = comparer;
		this.locale = locale;
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		getAnalysisAndVernacularLanguages();
		this.dateTime = dateTime;
	}

	protected void getAnalysisAndVernacularLanguages() {
		analysis1 = langProj1.getAnalysisLanguage();
		vernacular1 = langProj1.getVernacularLanguage();
		analysis2 = langProj2.getAnalysisLanguage();
		vernacular2 = langProj2.getVernacularLanguage();
	}

	public abstract String format();

	protected void formatHTMLBeginning(StringBuilder sb, String reportTitle) {
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n<title>");
		sb.append(reportTitle);
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
		sb.append(";\n\tcolor:");
		sb.append(StringUtilities.toRGBCode(language.getColor()));
		if (language.getOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
			sb.append(";\n\tdirection:rtl");
		}
		sb.append(";\n}\n");
	}

	protected void formatOverview(StringBuilder sb, String comparisonOf) {
		sb.append("<h2>" + comparisonOf + "</h2>\n");
		if (comparer.getDataSet1Info() != null || comparer.getDataSet2Info() != null) {
			sb.append("<ol><li>");
			sb.append(comparer.getDataSet1Info());
			sb.append("</li>\n<li>");
			sb.append(comparer.getDataSet2Info());
			sb.append("</li>\n</ol>\n");
		}
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
				formatPredictedSyllabification(sb, word1);
				sb.append("</td>\n</tr>\n");
				sb.append("<tr>\n<td class=\"");
				sb.append(VERNACULAR_2);
				sb.append("\">");
				formatPredictedSyllabification(sb, word2);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatSyllabifcationParameters(StringBuilder sb) {
		String s1 = "";
		String s2 = "";
		sb.append("<h3>" + bundle.getString("report.syllabificationparameters") + "</h3>\n");
		if (comparer.codasAllowedDiffer) {
			if (comparer.langProj1CodasAllowed) {
				s1 = bundle.getString("label.yes");
				s2 = bundle.getString("label.no");
			} else {
				s1 = bundle.getString("label.no");
				s2 = bundle.getString("label.yes");
			}
			formatSyllabificationParameterInfo(sb, bundle.getString("report.codasalloweddiffers"), s1, s2);
		} else {
			sb.append("<p>" + bundle.getString("report.samecodasallowed") + "</p>\n");
		}
		if (comparer.onsetMaximizationDiffers) {
			if (comparer.langProj1OnsetMaximization) {
					s1 = bundle.getString("label.yes");
					s2 = bundle.getString("label.no");
				} else {
					s1 = bundle.getString("label.no");
					s2 = bundle.getString("label.yes");
				}
				formatSyllabificationParameterInfo(sb, bundle.getString("report.onsetmaximizationdiffers"), s1, s2);
		} else {
			sb.append("<p>" + bundle.getString("report.sameonsetmaximization") + "</p>\n");
		}
		if (comparer.onsetPrincipleDiffers) {
			switch (comparer.langProj1OnsetPrinciple)
			{
			case ALL_BUT_FIRST_HAS_ONSET:
				s1 = bundle.getString("radio.allbutfirst");
				break;
			case EVERY_SYLLABLE_HAS_ONSET:
				s1 = bundle.getString("radio.everysyllable");
				break;
			case ONSETS_NOT_REQUIRED:
				s1 = bundle.getString("radio.onsetsnotrequired");
				break;
			}
			switch (comparer.langProj2OnsetPrinciple)
			{
			case ALL_BUT_FIRST_HAS_ONSET:
				s2 = bundle.getString("radio.allbutfirst");
				break;
			case EVERY_SYLLABLE_HAS_ONSET:
				s2 = bundle.getString("radio.everysyllable");
				break;
			case ONSETS_NOT_REQUIRED:
				s2 = bundle.getString("radio.onsetsnotrequired");
				break;
			}
			formatSyllabificationParameterInfo(sb, bundle.getString("report.onsetprinciplediffers"), s1, s2);
		} else {
			sb.append("<p>" + bundle.getString("report.sameonsetprinciple") + "</p>\n");
		}
	}

	protected void formatSyllabificationParameterInfo(StringBuilder sb, String sTitle, String s1,
			String s2) {
		sb.append("<p>" + sTitle + "</p>\n");
		sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
		sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
		sb.append("</th>\n<th>");
		sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
		sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			sb.append("<tr>\n<td class=\"");
			sb.append(ANALYSIS_1);
			sb.append("\">");
			sb.append(s1);
			sb.append("</td>\n<td class=\"");
			sb.append(ANALYSIS_2);
			sb.append("\">");
			sb.append(s2);
			sb.append("</td>\n</tr>\n");
		sb.append("</tbody>\n</table>\n");
	}

	protected void formatWordInfo(StringBuilder sb, Word word) {
		if (word == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getWord());
		}
	}

	protected abstract void formatPredictedSyllabification(StringBuilder sb, Word word);

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

	protected void formatNaturalClasses(StringBuilder sb, SortedSet<? extends DifferentCVNaturalClass> diffNaturalClasses) {
		sb.append("<h3>" + bundle.getString("report.cvnaturalclasses") + "</h3>\n");
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

	protected void formatNaturalClassInfo(StringBuilder sb, CVNaturalClass naturalClass, String vernacularCSS) {
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
}
