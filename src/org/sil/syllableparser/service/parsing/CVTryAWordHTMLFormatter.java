// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.util.List;
import java.util.Locale;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVTraceInfo;
import org.sil.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;

/**
 * @author Andy Black
 *
 */
public class CVTryAWordHTMLFormatter extends TryAWordHTMLFormatter {

	CVTraceInfo traceInfo;
	String sNaturalClasserResult = "";
	String sSyllabifierResult = "";
	String sNaturalClassFailure = "";

	public CVTryAWordHTMLFormatter(CVTraceInfo traceInfo, LanguageProject language, Locale locale) {
		super(language, locale);
		this.traceInfo = traceInfo;
		sWord = traceInfo.getWord();
		segmenter = traceInfo.getSegmenter();
		segmenterResult = traceInfo.getSegmenterResult();
		sNaturalClassFailure = bundle.getString("label.cvnaturalclassfailure");
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
		
		sb.append("<p>" + formatDetailsStringWithColorWords("report.tawcvdetails") + "</p>\n");
		sb.append("<div>");
		List<CVTraceSyllabifierInfo> traceList = syllabifier.getSyllabifierTraceInfo();
		formatSyllablePatternDetails(sb, traceList);
		sb.append("</div>");
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
}
