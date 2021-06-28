// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.service.LingTreeInteractor;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public abstract class TryAWordHTMLFormatter {

	protected LanguageProject language;
	protected String sSegmenterResult = "";
	protected String sSyllabifierResult = "";
	protected Locale locale;
	protected ResourceBundle bundle;
	protected LocalDateTime dateTime;
	protected Language analysis;
	protected Language vernacular;
	protected String imagesURI;
	protected String sSuccess;
	protected final String ANALYSIS = "analysis";
	protected final String VERNACULAR = "vernacular";
	protected final String SUCCESS = "success";
	protected final String SUCCESS_COLOR = "green";
	protected final String FAILURE = "failure";
	protected final String FAILURE_COLOR = "red";
	protected final String MATCHED = "matched";
	protected final String MATCHED_COLOR = "blue";
	protected final String COLOR_WORD_BEGINNING = "<span class='";
	protected final String COLOR_WORD_MIDDLE = "'>";
	protected final String COLOR_WORD_ENDING = "</span>";
	protected TraceInfo baseTraceInfo;
	protected String sWord;
	protected CVSegmenter segmenter;
	protected CVSegmenterResult segmenterResult;
	protected String lingTreeDescription;
	protected LingTreeInteractor ltInteractor;

	public TryAWordHTMLFormatter(LanguageProject language, Locale locale) {
		super();
		this.language = language;
		this.locale = locale;
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		getAnalysisAndVernacularLanguages();
		sSuccess = bundle.getString("report.tawsuccess");
		ltInteractor = LingTreeInteractor.getInstance();
		ltInteractor.initializeParameters(language);
		try {
			setJAR_URI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
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
				+ "/resources/images/";
	}

	protected void getAnalysisAndVernacularLanguages() {
		analysis = language.getAnalysisLanguage();
		vernacular = language.getVernacularLanguage();
	}

	public abstract String format();

	protected void formatHTMLBeginning(StringBuilder sb) {
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n<title>");
		sb.append(bundle.getString("report.tawtitle"));
		sb.append("</title>\n<style type=\"text/css\">\n");
		createLanguageFontCSS(sb, analysis, ANALYSIS);
		createLanguageFontCSS(sb, vernacular, VERNACULAR);
		createColorCSS(sb, SUCCESS, SUCCESS_COLOR);
		createColorCSS(sb, FAILURE, FAILURE_COLOR);
		createColorCSS(sb, MATCHED, MATCHED_COLOR);
		sb.append(Constants.TRY_A_WORD_INTERBLOCK_CSS);
		sb.append("</style>\n");
		sb.append(Constants.TRY_A_WORD_JAVASCRIPT);
		sb.append("</head>\n<body>\n");
	}

	protected void createLanguageFontCSS(StringBuilder sb, Language language, String sLangTypeNumber) {
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
		String sFormattedWord = "<span style=\"font-family:" + vernacular.getFontFamily()
				+ ";\">" + sWord + "</span>";
		sb.append("<h2>" + getAddedArgument("report.tawtitle", sFormattedWord) + "</h2>\n");
	}

	protected boolean formatSegmentParsing(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.tawsegments") + "</h3>\n");
		CVSegmenterResult segResult = segmenterResult;
		boolean fSuccess = segResult.success;
		if (fSuccess) {
			appendSuccessMessage(sb);
			sb.append("<table class='" + SUCCESS + "' border='1' cellpadding='4pt'><tbody><tr>");
			sb.append("<th align='left'>" + bundle.getString("report.tawgrapheme") + "</th>");
			for (CVSegmentInSyllable seg : segmenter.getSegmentsInWord()) {
				formatSegmentInfo(sb, seg, true);
			}
			sb.append("</tr><tr>\n");
			sb.append("<th align='left'>" + bundle.getString("report.tawsegment") + "</th>");
			for (CVSegmentInSyllable seg : segmenter.getSegmentsInWord()) {
				formatSegmentInfo(sb, seg, false);
			}
			sb.append("</tr></tbody></table>\n");
		} else {
			sb.append("<p class='" + FAILURE + "'>");
			sb.append(segResult.getFailureMessage(sWord, bundle));
			sb.append("</p>\n");
		}
		return fSuccess;
	}

	protected void formatSegmentInfo(StringBuilder sb, CVSegmentInSyllable seg,
			boolean fShowGrapheme) {
		sb.append("<td class='" + VERNACULAR + "'>");
		if (seg == null) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			if (fShowGrapheme) {
				sb.append(seg.getGrapheme());
			} else {
				sb.append(seg.getSegment().getSegment());
			}
		}
		sb.append("</td>");
	}

	protected void appendSuccessMessage(StringBuilder sb) {
		sb.append("<p class='" + SUCCESS + "'>" + sSuccess + "</p>\n");
	}

	protected String formatDetailsStringWithColorWords(String messageID) {
		String sFailureColorWord = createColorWord(FAILURE, bundle.getString("report.failurecolorword"));
		String sMatchedColorWord = createColorWord(MATCHED, bundle.getString("report.matchedcolorword"));
		String sSuccessColorWord = createColorWord(SUCCESS, bundle.getString("report.successcolorword"));
		Object[] args = { sFailureColorWord, sMatchedColorWord, sSuccessColorWord};
		MessageFormat msgFormatter = new MessageFormat("");
		msgFormatter.setLocale(locale);
		msgFormatter.applyPattern(bundle.getString(messageID));
		return msgFormatter.format(args);
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

	public String getLingTreeDescription() {
		return lingTreeDescription;
	}

	public void setLingTreeDescription(String lingTreeDescription) {
		this.lingTreeDescription = lingTreeDescription;
	}

	protected void createSVGOfTree(StringBuilder sb, boolean fSuccess) {
		if (!StringUtilities.isNullOrEmpty(lingTreeDescription) && !lingTreeDescription.equals("(W)")) {
			sb.append("<div style=\"text-align:left\">");
			sb.append(ltInteractor.createSVG(lingTreeDescription, fSuccess));
			sb.append("</div>");
		}
	}
}
