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
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.utility.StringUtilities;

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
			boolean fSuccess = sylResult.success;
			if (fSuccess) {
				appendSuccessMessage(sb);
				sb.append("<p class='" + SUCCESS + " vernacular'>");
				sb.append(traceInfo.getSyllabifier().getSyllabificationOfCurrentWord());
				sb.append("</p>");
			} else {
				sb.append("<p class='" + FAILURE + "'>");
				sb.append(bundle.getString("label.shsyllabificationfailure"));
				sb.append("</p>\n");
			}
			createSVGOfTree(sb, fSuccess);
		}

		sb.append("<p>" + formatDetailsStringWithColorWords("report.tawoncdetails") + "</p>\n");
		sb.append("<div>");
		List<ONCTracingStep> tracingSteps = syllabifier.getTracingSteps();
		formatONCSyllabificationDetails(sb, tracingSteps);
		sb.append("</div>");
	}

	protected void formatONCSyllabificationDetails(StringBuilder sb,
			List<ONCTracingStep> tracingSteps) {
		if (tracingSteps.size() == 0) {
			return;
		}
		String rowStatus;

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
		sb.append(bundle.getString("report.tawonctype"));
		sb.append("</th>\n");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawoncstatus"));
		sb.append("</th>\n");
		sb.append("</tr>\n");
		int i = 0;
		for (ONCTracingStep tracingStep : tracingSteps) {
			tracingStep.setBundle(bundle);
			if (tracingStep.comparisonResult == SHComparisonResult.MISSING1) {
				tracingStep.sMissingNaturalClass = bundle.getString("report.tawshmissingnc");
			}
			if (tracingStep.comparisonResult == SHComparisonResult.MISSING2) {
				tracingStep.sMissingNaturalClass = bundle.getString("report.tawshmissingnc");
			}
			if (tracingStep.isSuccessful()) {
				rowStatus = SUCCESS;
			} else {
				rowStatus = FAILURE;
			}
			sb.append("<tr valign='top'>");
			sb.append("<td>");
			sb.append("<span class='");
			sb.append(rowStatus);
			sb.append("'>&#xa0;<span class='vernacular'>");
			sb.append(tracingStep.getSegment1Result());
			sb.append("</span> (<span class='analysis'>");
			sb.append(tracingStep.getNaturalClass1Result());
			sb.append("</span>)&#xa0;");
			sb.append("</span>\n");
			sb.append("</td>");

			sb.append("<td align=\"center\">");
			sb.append("<span class='");
			if (tracingStep.comparisonResult == SHComparisonResult.MISSING1
					|| tracingStep.comparisonResult == SHComparisonResult.MISSING2) {
				sb.append(FAILURE);
			} else {
				sb.append(SUCCESS);
			}
			sb.append("'>");
			sb.append(tracingStep.getComparisonResultAsString());
			sb.append("</span>\n");
			sb.append("</td>");

			sb.append("<td>");
			sb.append("<span class='");
			sb.append(rowStatus);
			sb.append("'>&#xa0;<span class='vernacular'>");
			sb.append(tracingStep.getSegment2Result());
			sb.append("</span> (<span class='analysis'>");
			sb.append(tracingStep.getNaturalClass2Result());
			sb.append("</span>)&#xa0;");
			sb.append("</span>\n");
			sb.append("<td class='");
			sb.append(rowStatus);
			sb.append("'>&#xa0;");
			sb.append(tracingStep.getOncStateLocalized());
			sb.append("</td>");
			sb.append("<td class='");
			sb.append(rowStatus);
			sb.append("'>&#xa0;");
			sb.append(tracingStep.getStatusLocalized());
			sb.append("</td>");
			sb.append("</tr>\n");
			i++;
			if (i < tracingSteps.size()) {
				sb.append("<tr><td colspan=\"5\"/></td>\n");
			}
		}
		sb.append("</table>\n");
	}
}
