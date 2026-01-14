// Copyright (c) 2026 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.util.Locale;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.hyphenapproach.HyphenTraceInfo;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class HyphenTryAWordHTMLFormatter extends TryAWordHTMLFormatter {

	HyphenTraceInfo traceInfo;
	String sNaturalClasserResult = "";
	String sSyllabifierResult = "";
	String sHyphenClassFailure = "";
	final String kStartingStateCss = ".startingState {\n"
			+ "font-style:italic;\n"
			+ "}\n";
	final String kRuleChangeHighlightCss = ".ruleChangeHighlight {\n"
			+ "font-weight:bold;\n"
			+ "font-size:larger\n"
			+ "}\n";
	public HyphenTryAWordHTMLFormatter(HyphenTraceInfo traceInfo, LanguageProject language, Locale locale) {
		super(language, locale);
		this.traceInfo = traceInfo;
		sWord = traceInfo.getWord();
		segmenter = traceInfo.getSegmenter();
		segmenterResult = traceInfo.getSegmenterResult();
		sHyphenClassFailure = bundle.getString("label.hyphenclassfailure");
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, kStartingStateCss, kRuleChangeHighlightCss);
		formatOverview(sb);
		boolean fSuccess = formatSegmentParsing(sb);
		if (fSuccess) {
			fSuccess = formatHyphenClasses(sb);
			if (fSuccess) {
				formatHyphenation(sb);
			}
		}
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected boolean formatHyphenClasses(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.tawhyphenclasses") + "</h3>\n");
		HyphenClasserResult ncResult = traceInfo.getHyphenClasserResult();
		if (ncResult.success) {
			appendSuccessMessage(sb);
			sb.append("<table class='" + SUCCESS + "' border='1' cellpadding='4pt'><tbody><tr>");
			sb.append("<th align='left'>" + bundle.getString("report.tawhyphenclass") + "</th>");
			String classRepresentration = ncResult.sClassesSoFar; 
			showClassRepresentationInTableRow(sb, classRepresentration);
			sb.append("</tr><tr>\n");
			sb.append("<th align='left'>" + bundle.getString("report.tawsegment") + "</th>");
			sb.append("<td>" + Constants.NON_BREAKING_SPACE + "</td>");
			for (CVSegmentInSyllable seg : segmenter.getSegmentsInWord()) {
				formatSegmentInfo(sb, seg, false);
			}
			sb.append("<td>" + Constants.NON_BREAKING_SPACE + "</td>");
			sb.append("</tr></tbody></table>\n");
		} else {
			String sFailureMessage0 = sHyphenClassFailure.replace("{0}", ncResult.sClassesSoFar);
			String sFailureMessage1 = sFailureMessage0.replace("{1}", ncResult.sGraphemesSoFar);
			sb.append("<p class='" + FAILURE + "'>" + sFailureMessage1 + "</p>\n");
		}
		return ncResult.success;
	}

	protected void showClassRepresentationInTableRow(StringBuilder sb, String classRepresentration) {
		String[] classes = classRepresentration.split(", ");
		for (int i = 0; i < classes.length; i++) {
			String sClass = classes[i];
			sb.append("<td class='" + ANALYSIS + "'>");
			if (StringUtilities.isNullOrEmpty(sClass)) {
				sb.append(Constants.NON_BREAKING_SPACE);
			} else {
				sb.append(sClass);
			}
			sb.append("</td>");
		}
	}

	protected void formatHyphenation(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.tawhyphenchangerules") + "</h3>\n");
		appendSuccessMessage(sb);
		sb.append("<p class='" + SUCCESS + " vernacular'>");
		sb.append(traceInfo.getHyphenChangeRuleResult().sHyphenation);
		sb.append("</p>");
		sb.append("<p>" + bundle.getString("report.tawhyphendetails") + "</p>\n");
		sb.append("<div>");
		sb.append("<table class='" + SUCCESS + "' border='1' cellpadding='4pt'><thead><tr>");
		sb.append("<th align='left'>" + bundle.getString("report.tawhyphenrule") + "</th>");
		sb.append("<th align='left'>" + bundle.getString("report.tawhyphenstate") + "</th>");
		sb.append("</tr></thead><tbody><tr>");
		sb.append("<td class='startingState'>");
		sb.append(bundle.getString("report.tawhyphenstartstate"));
		sb.append("</td><td>");
		sb.append(traceInfo.getHyphenClasserResult().sClassesSoFar);
		sb.append("</td></tr>");
		for (HyphenChangeRuleState hcState : traceInfo.getHyphenProcessor().getStateHistory()) {
			sb.append("<tr><td>");
			sb.append(hcState.rule.getRuleName());
			sb.append("</td><td>");
			formatClassesInWord(sb, hcState);
			sb.append("</td></tr>");
		}
		sb.append("</tbody></table>");
		sb.append("</div>");
	}
	
	protected void formatClassesInWord(StringBuilder sb, HyphenChangeRuleState hcState) {
		int matchSize = hcState.getRule().getMatchHyphenClasses().size();
		int changeSize = hcState.getRule().getChangeHyphenClasses().size();
		int startInWord = hcState.getClassIndex() - matchSize + 1;
		int endOfSpan = (changeSize == matchSize) ? startInWord: startInWord + matchSize;
		int classesSize = hcState.getClassesInWord().size();
		for (int i = 0; i < classesSize; i++) {
			if (i == startInWord) {
				sb.append("<span class='ruleChangeHighlight'>");
			}
			sb.append(hcState.getClassesInWord().get(i).getClassName());
			if (i == endOfSpan) {
				sb.append("</span>");
			}
			if (i < classesSize - 1) {
				sb.append(", ");
			}
		}

	}
}
