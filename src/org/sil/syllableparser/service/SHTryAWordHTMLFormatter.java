// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import java.util.List;
import java.util.Locale;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceInfo;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceSyllabifierInfo;

/**
 * @author Andy Black
 *
 */
public class SHTryAWordHTMLFormatter extends TryAWordHTMLFormatter {

	SHTraceInfo traceInfo;

	public SHTryAWordHTMLFormatter(SHTraceInfo traceInfo, LanguageProject language, Locale locale) {
		super(language, locale);
		this.traceInfo = traceInfo;
		sWord = traceInfo.getWord();
		segmenter = traceInfo.getSegmenter();
		segmenterResult = traceInfo.getSegmenterResult();
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb);
		formatOverview(sb);
		boolean fSuccess = formatSegmentParsing(sb);
		if (fSuccess) {
			formatSyllablification(sb);
		}
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatSyllablification(StringBuilder sb) {
		SHSyllabifier syllabifier = traceInfo.getSyllabifier();
		List<SHTraceSyllabifierInfo> syllabifierInfo = syllabifier.getSyllabifierTraceInfo();
		SHSyllabifierResult sylResult = traceInfo.getSyllabifierResult();
		if (sylResult != null) {
			if (sylResult.success) {
				appendSuccessMessage(sb);
				sb.append("<p class='" + SUCCESS + "'>");
				sb.append(traceInfo.getSyllabifier().getSyllabificationOfCurrentWord());
			} else {
				sb.append("<p class='" + FAILURE + "'>");
				sb.append(bundle.getString("label.shsyllabificationfailure"));
				sb.append("</p>\n");
			}
		}

		sb.append("<p>" + formatDetailsStringWithColorWords("report.tawshdetails") + "</p>\n");
		sb.append("<div>");
		List<SHTraceSyllabifierInfo> traceList = syllabifier.getSyllabifierTraceInfo();
		formatSonoritySyllabificationDetails(sb, traceList);
		sb.append("</div>");
	}

	protected void formatSonoritySyllabificationDetails(StringBuilder sb,
			List<SHTraceSyllabifierInfo> traceList) {
		if (traceList.size() == 0) {
			return;
		}
		sb.append("<table border='1'>\n");
		sb.append("<tr valign='top'>");
		sb.append("<th>Segment</th>\n");
		sb.append("<th>Natural Class</th>\n");
		sb.append("<th>Sonority Comparison</th>\n");
		sb.append("</tr>\n");
		int i = 0;
		for (SHTraceSyllabifierInfo sylInfo : traceList) {
			sb.append("<tr valign='top'>");
			sb.append("<td>");
			sb.append("<span class='");
			if (sylInfo.segment1 != null) {
				sb.append(SUCCESS);
			} else {
				sb.append(FAILURE);
			}
			sb.append("'>");
			sb.append(sylInfo.getSegment1Result());
			sb.append("</span></a>\n");
			sb.append("</td>");

			sb.append("<td>");
			sb.append("<span class='");
			if (sylInfo.naturalClass1 != null) {
				sb.append(SUCCESS);
			} else {
				sb.append(FAILURE);
			}
			sb.append("'>");
			sb.append(sylInfo.getNaturalClass1Result());
			sb.append("</span></a>\n");
			sb.append("</td>");

			sb.append("<td rowspan=\"2\" valign=\"middle\">");
			sb.append("<span class='");
			if (sylInfo.comparisonResult != null) {
				sb.append(SUCCESS);
			} else {
				sb.append(FAILURE);
			}
			sb.append("'>");
			sb.append(sylInfo.getComparisonResult());
			sb.append("</span></a>\n");
			sb.append("</td>");		
			sb.append("</tr>\n");
			sb.append("<tr valign='top'>");
			sb.append("<td>");
			sb.append("<span class='");
			if (sylInfo.segment2 != null) {
				sb.append(SUCCESS);
			} else {
				sb.append(FAILURE);
			}
			sb.append("'>");
			sb.append(sylInfo.getSegment2Result());
			sb.append("</span></a>\n");
			sb.append("</td>");

			sb.append("<td>");
			sb.append("<span class='");
			if (sylInfo.naturalClass2 != null) {
				sb.append(SUCCESS);
			} else {
				sb.append(FAILURE);
			}
			sb.append("'>");
			sb.append(sylInfo.getNaturalClass2Result());
			sb.append("</span></a>\n");
			sb.append("</td>");
			sb.append("</tr>\n");
			sb.append("<tr><td colspan=\"3\"/></td>\n");
			i++;
		}
		sb.append("</table>\n");
	}
}
