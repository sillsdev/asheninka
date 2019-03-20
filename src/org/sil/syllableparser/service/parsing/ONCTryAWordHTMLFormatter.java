// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.util.List;
import java.util.Locale;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.oncapproach.ONCTraceInfo;
import org.sil.syllableparser.model.oncapproach.ONCTraceSyllabifierInfo;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class ONCTryAWordHTMLFormatter extends TryAWordHTMLFormatter {

	ONCTraceInfo traceInfo;

	// TODO: make sure this is right and refactor any common code with SH one
	public ONCTryAWordHTMLFormatter(ONCTraceInfo traceInfo2, LanguageProject language, Locale locale) {
		super(language, locale);
		this.traceInfo = traceInfo2;
		sWord = traceInfo2.getWord();
		segmenter = traceInfo2.getSegmenter();
		segmenterResult = traceInfo2.getSegmenterResult();
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
		ONCSyllabifier syllabifier = traceInfo.getSyllabifier();
		ONCSyllabifierResult sylResult = traceInfo.getSyllabifierResult();
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
		List<ONCTraceSyllabifierInfo> traceList = syllabifier.getSyllabifierTraceInfo();
		formatSonoritySyllabificationDetails(sb, traceList);
		sb.append("</div>");
	}

	protected void formatSonoritySyllabificationDetails(StringBuilder sb,
			List<ONCTraceSyllabifierInfo> traceList) {
		if (traceList.size() == 0) {
			return;
		}
		String row1Status;
		String row2Status;

		sb.append("<table border='1'>\n");
		sb.append("<tr valign='top'>");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawshsegment1"));
		sb.append("</th>\n");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawshrelation"));
		sb.append("</th>\n");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawshsegment2"));
		sb.append("</th>\n");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawshstartssyllable"));
		sb.append("</th>\n");
		sb.append("</tr>\n");
		int i = 0;
		for (ONCTraceSyllabifierInfo sylInfo : traceList) {
			if (sylInfo.comparisonResult == SHComparisonResult.MISSING1) {
				sylInfo.sMissingNaturalClass = bundle.getString("report.tawshmissingnc");
				row1Status = FAILURE;
			} else {
				row1Status = SUCCESS;
			}
			if (sylInfo.comparisonResult == SHComparisonResult.MISSING2) {
				sylInfo.sMissingNaturalClass = bundle.getString("report.tawshmissingnc");
				row2Status = FAILURE;
			} else {
				row2Status = SUCCESS;
			}
			sb.append("<tr valign='top'>");
			sb.append("<td>");
			sb.append("<span class='");
			sb.append(row1Status);
			sb.append("'>&#xa0;");
			sb.append(sylInfo.getSegment1Result());
			sb.append(" (");
			sb.append(sylInfo.getNaturalClass1Result());
			sb.append(")&#xa0;");
			sb.append("</span>\n");
			sb.append("</td>");

			sb.append("<td align=\"center\">");
			sb.append("<span class='");
			if (sylInfo.comparisonResult == SHComparisonResult.MISSING1
					|| sylInfo.comparisonResult == SHComparisonResult.MISSING2) {
				sb.append(FAILURE);
			} else {
				sb.append(SUCCESS);
			}
			sb.append("'>");
			sb.append(sylInfo.getComparisonResult());
			sb.append("</span>\n");
			sb.append("</td>");

			sb.append("<td>");
			sb.append("<span class='");
			sb.append(row2Status);
			sb.append("'>&#xa0;");
			sb.append(sylInfo.getSegment2Result());
			sb.append(" (");
			sb.append(sylInfo.getNaturalClass2Result());
			sb.append(")&#xa0;");
			sb.append("</span>\n");
			sb.append("<td align='center'>");
//			if (sylInfo.startsSyllable) {
//				sb.append("&sigma;");
//			} else {
				sb.append("&#xa0;");
//			}
			sb.append("</td>");
			sb.append("</tr>\n");
			i++;
			if (i < traceList.size()) {
				sb.append("<tr><td colspan=\"4\"/></td>\n");
			}
		}
		sb.append("</table>\n");
	}
}
