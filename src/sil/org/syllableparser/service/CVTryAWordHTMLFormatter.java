// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;

import com.sun.xml.internal.ws.encoding.SwACodec;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Language;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import sil.org.syllableparser.model.cvapproach.CVTraceInfo;
import sil.org.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;
import sil.org.utility.DateTimeNormalizer;
import sil.org.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class CVTryAWordHTMLFormatter {

	CVTraceInfo traceInfo;
	LanguageProject language;
	String sSegmenterResult = "";
	String sNaturalClasserResult = "";
	String sSyllabifierResult = "";
	Locale locale;
	String sNaturalClassFailure = "";
	ResourceBundle bundle;
	LocalDateTime dateTime;
	Language analysis;
	Language vernacular;
	String imagesURI;
	String sSuccess;
	final String ANALYSIS = "analysis";
	final String VERNACULAR = "vernacular";
	final String SUCCESS = "success";
	final String SUCCESS_COLOR = "green";
	final String FAILURE = "failure";
	final String FAILURE_COLOR = "red";
	final String MATCHED = "matched";
	final String MATCHED_COLOR = "blue";
	final String COLOR_WORD_BEGINNING = "<span class='";
	final String COLOR_WORD_MIDDLE = "'>";
	final String COLOR_WORD_ENDING = "</span>";

	public CVTryAWordHTMLFormatter(CVTraceInfo traceInfo, LanguageProject language, Locale locale) {
		super();
		this.traceInfo = traceInfo;
		this.language = language;
		this.locale = locale;
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		getAnalysisAndVernacularLanguages();
		sNaturalClassFailure = bundle.getString("label.cvnaturalclassfailure");
		sSuccess = bundle.getString("report.tawsuccess");
		try {
			setJAR_URI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String createColorWord(String sClassName, String sColorWord) {
		return COLOR_WORD_BEGINNING + sClassName + COLOR_WORD_MIDDLE + sColorWord
				+ COLOR_WORD_ENDING;
	}

	protected void setJAR_URI() throws URISyntaxException {
		CodeSource codeSource = MainApp.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		String jarDir = jarFile.getParentFile().getPath();
		imagesURI = StringUtilities.adjustForWindowsFileSeparator(jarDir)
				+ "/src/sil/org/syllableparser/resources/images/";
	}

	protected void getAnalysisAndVernacularLanguages() {
		analysis = language.getAnalysisLanguage();
		vernacular = language.getVernacularLanguage();
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb);
		formatOverview(sb);
		boolean fSuccess = formatSegmentParsing(sb);
		if (fSuccess) {
			fSuccess = formatNaturalClasses(sb);
			if (fSuccess) {
				formatSyllablification(sb);
			}
		}
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatHTMLBeginning(StringBuilder sb) {
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n<title>");
		sb.append(bundle.getString("report.tawtitle"));
		sb.append("</title>\n<style type=\"text/css\">\n");
		createLanguagFontCSS(sb, analysis, ANALYSIS);
		createLanguagFontCSS(sb, vernacular, VERNACULAR);
		createColorCSS(sb, SUCCESS, SUCCESS_COLOR);
		createColorCSS(sb, FAILURE, FAILURE_COLOR);
		createColorCSS(sb, MATCHED, MATCHED_COLOR);
		sb.append(Constants.TRY_A_WORD_INTERBLOCK_CSS);
		sb.append("</style>\n");
		sb.append(Constants.TRY_A_WORD_JAVASCRIPT);
		sb.append("</head>\n<body>\n");
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

	protected void createColorCSS(StringBuilder sb, String sType, String sColor) {
		sb.append(".");
		sb.append(sType);
		sb.append(" {\n\tcolor:");
		sb.append(sColor);
		sb.append(";\n\n}\n");
	}

	protected void formatOverview(StringBuilder sb) {
		String sFormattedWord = "<span style=\"font-family='" + vernacular.getFontFamily()
				+ ";'\">" + traceInfo.getWord() + "</span>";
		sb.append("<h2>" + getAddedArgument("report.tawtitle", sFormattedWord) + "</h2>\n");
	}

	protected boolean formatSegmentParsing(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.tawsegments") + "</h3>\n");
		CVSegmenterResult segResult = traceInfo.getSegmenterResult();
		boolean fSuccess = segResult.success;
		if (fSuccess) {
			appendSuccessMessage(sb);
			sb.append("<table class='" + SUCCESS + "' border='1' cellpadding='4pt'><tbody><tr>");
			sb.append("<th align='left'>" + bundle.getString("report.tawgrapheme") + "</th>");
			for (CVSegmentInSyllable seg : traceInfo.getSegmenter().segmentsInCurrentWord) {
				formatSegmentInfo(sb, seg, true);
			}
			sb.append("</tr><tr>\n");
			sb.append("<th align='left'>" + bundle.getString("report.tawsegment") + "</th>");
			for (CVSegmentInSyllable seg : traceInfo.getSegmenter().segmentsInCurrentWord) {
				formatSegmentInfo(sb, seg, false);
			}
			sb.append("</tr></tbody></table>\n");
		} else {
			sb.append("<p class='" + FAILURE + "'>");
			sb.append(segResult.getFailureMessage(traceInfo.getWord(), bundle));
			sb.append("</p>\n");
		}
		return fSuccess;
	}

	protected void formatSegmentInfo(StringBuilder sb, CVSegmentInSyllable seg,
			boolean fShowGrapheme) {
		sb.append("<td class='" + VERNACULAR + "'>");
		if (seg == null) {
			sb.append("&#xa0;");
		} else {
			if (fShowGrapheme) {
				sb.append(seg.getGrapheme());
			} else {
				sb.append(seg.getSegment().getSegment());
			}
		}
		sb.append("</td>");
	}

	protected boolean formatNaturalClasses(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.tawnaturalclasses") + "</h3>\n");
		CVNaturalClasserResult ncResult = traceInfo.getNaturalClasserResult();
		if (ncResult.success) {
			appendSuccessMessage(sb);
			sb.append("<p class='" + SUCCESS + "'>"
					+ traceInfo.getNaturalClasser().getNaturalClassListsInCurrentWordAsString()
					+ "</p>\n");
		} else {
			String sFailureMessage0 = sNaturalClassFailure.replace("{0}", ncResult.sClassesSoFar);
			String sFailureMessage1 = sFailureMessage0.replace("{1}", ncResult.sGraphemesSoFar);
			sb.append("<p class='" + FAILURE + "'>" + sFailureMessage1 + "</p>\n");
		}
		return ncResult.success;
	}

	protected void appendSuccessMessage(StringBuilder sb) {
		sb.append("<p class='" + SUCCESS + "'>" + sSuccess + "</p>\n");
	}

	protected void formatSyllablification(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.tawsyllablepatterns") + "</h3>\n");

		CVSyllabifier syllabifier = traceInfo.getSyllabifier();
		List<CVTraceSyllabifierInfo> syllabifierInfo = syllabifier.getSyllabifierTraceInfo();
		CVSyllabifierResult sylResult = traceInfo.getSyllabifierResult();
		if (sylResult != null) {
			if (sylResult.success) {
				appendSuccessMessage(sb);
				sb.append("<p class='" + SUCCESS + "'>");
				sb.append(traceInfo.getSyllabifier().getSyllabificationOfCurrentWord());
			} else {
				sb.append("<p class='" + FAILURE + "'>");
				sb.append(bundle.getString("label.cvsyllabificationfailure"));
				sb.append("</p>\n");
			}
		}
		
		sb.append("<p>" + formatDetailsStringWithColorWords() + "</p>\n");
		sb.append("<div>");
		List<CVTraceSyllabifierInfo> traceList = syllabifier.getSyllabifierTraceInfo();
		formatSyllablePatternDetails(sb, traceList);
		sb.append("</div>");
	}

	protected String formatDetailsStringWithColorWords() {
		String sFailureColorWord = createColorWord(FAILURE, bundle.getString("report.failurecolorword"));
		String sMatchedColorWord = createColorWord(MATCHED, bundle.getString("report.matchedcolorword"));
		String sSuccessColorWord = createColorWord(SUCCESS, bundle.getString("report.successcolorword"));
		Object[] args = { sFailureColorWord, sMatchedColorWord, sSuccessColorWord};
		MessageFormat msgFormatter = new MessageFormat("");
		msgFormatter.setLocale(locale);
		msgFormatter.applyPattern(bundle.getString("report.tawdetails"));
		return msgFormatter.format(args);
	}
	
	protected void formatSyllablePatternDetails(StringBuilder sb,
			List<CVTraceSyllabifierInfo> traceList) {
		if (traceList.size() == 0) {
			return;
		}
		sb.append("<table border='0'>\n");
		int index = 0;
		int indexMax = traceList.size() - 1;
		for (CVTraceSyllabifierInfo sylInfo : traceList) {
			sb.append("<tr valign='top'>");
			sb.append("<td width='10'/>\n");
			sb.append("<td style=\"border:solid; border-width:thin;\">");
			sb.append("<a><img src=\"file:///" + imagesURI);
			if (sylInfo.daughterInfo.size() == 0) {
				if (index == 0) {
					sb.append("beginminus.gif\"");
				} else if (index < indexMax) {
					sb.append("minus.gif\"");
				} else {
					sb.append("lastminus.gif\"");
				}
			} else {
				if (index == 0) {
					sb.append("beginplus.gif\"");
				} else if (index < indexMax) {
					sb.append("plus.gif\"");
				} else {
					sb.append("lastplus.gif\"");
				}
			}
			sb.append(" onclick=\"Toggle(this.parentNode, &quot;file:///" + imagesURI
					+ "&quot;, 0)\"/>");
			sb.append("<span class='");
			if (sylInfo.syllablePatternMatched) {
				if (sylInfo.parseWasSuccessful) {
					sb.append(SUCCESS);
				} else {
					sb.append(MATCHED);
				}
			} else {
				sb.append(FAILURE);
			}
			sb.append("'>");
			sb.append(sylInfo.sCVSyllablePattern);
			if (sylInfo.parseWasSuccessful && sylInfo.daughterInfo.size() == 0) {
				sb.append(" " + sSuccess);
			}
			sb.append("</span></a>\n");
			sb.append("<div style=\"display:none;\">");
			formatSyllablePatternDetails(sb, sylInfo.daughterInfo);
			sb.append("</div>");
			sb.append("</td>");
			sb.append("</tr>\n");
			index++;
		}
		sb.append("</table>\n");
	}

	protected void formatHTMLEnding(StringBuilder sb) {
		sb.append("</body>\n</html>\n");
	}

	protected String getAddedArgument(String mainProperty, String addedArgument) {
		Object[] args = { addedArgument };
		MessageFormat msgFormatter = new MessageFormat("");
		msgFormatter.setLocale(locale);
		msgFormatter.applyPattern(bundle.getString(mainProperty));
		return msgFormatter.format(args);
	}

	protected String getAdjectivalForm(String mainProperty, String adjectivalEnding) {
		Object[] args = { bundle.getString(adjectivalEnding) };
		MessageFormat msgFormatter = new MessageFormat("");
		msgFormatter.setLocale(locale);
		msgFormatter.applyPattern(bundle.getString(mainProperty));
		return msgFormatter.format(args);
	}
}
