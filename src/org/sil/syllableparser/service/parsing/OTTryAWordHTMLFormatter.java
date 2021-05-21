// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.util.List;
import java.util.Locale;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.npapproach.NPSyllabificationStatus;
import org.sil.syllableparser.model.npapproach.NPTraceInfo;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.otapproach.OTTraceInfo;
import org.sil.syllableparser.model.otapproach.OTTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTracingStep;

/**
 * @author Andy Black
 *
 */
public class OTTryAWordHTMLFormatter extends TryAWordHTMLFormatter {

	OTTraceInfo traceInfo;
	public OTTryAWordHTMLFormatter(OTTraceInfo traceInfo2, LanguageProject language, Locale locale) {
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
		OTSyllabifier syllabifier = traceInfo.getSyllabifier();
		OTSyllabifierResult sylResult = traceInfo.getSyllabifierResult();
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
		createSVGOfTree(sb, true);

		sb.append("<p>" + formatDetailsStringWithColorWords("report.tawnpdetails") + "</p>\n");
		sb.append("<div>");
		List<OTTracingStep> tracingSteps = syllabifier.getTracingSteps();
		formatOTSyllabificationDetails(sb, tracingSteps);
		sb.append("</div>");
	}

	protected void formatOTSyllabificationDetails(StringBuilder sb,
			List<OTTracingStep> tracingSteps) {
		if (tracingSteps.size() == 0) {
			return;
		}
		String rowStatus;

		sb.append("<table border='1'>\n");
		sb.append("<tr valign='top'>");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawoncstatus"));
		sb.append("</th>\n");
		sb.append("<th>");
		sb.append(bundle.getString("report.tawnpinfo"));
		sb.append("</th>\n");
		sb.append("</tr>\n");
		for (OTTracingStep tracingStep : tracingSteps) {
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
			sb.append("<td class='");
			sb.append(rowStatus);
			if (tracingStep.getStatus() == NPSyllabificationStatus.APPLYING_RULE) {
				sb.append("' colspan='2'");
			}
			sb.append("'>&#xa0;");
			sb.append(tracingStep.getStatusLocalized());
			sb.append("</td>");
			if (tracingStep.getStatus() != NPSyllabificationStatus.APPLYING_RULE) {
				sb.append("<td class='");
				sb.append(rowStatus);
				sb.append("'>");
//				if (tracingStep.getStatus() == NPSyllabificationStatus.SSP_FAILED
//						|| tracingStep.getStatus() == NPSyllabificationStatus.SSP_PASSED) {
//					formatSSPInfo(sb, rowStatus, tracingStep);
//				} else {
					formatWordTreeInfo(sb, tracingStep);
//				}
				sb.append("</td>");
			}
		}
		sb.append("</table>\n");
	}

	protected void formatWordTreeInfo(StringBuilder sb, OTTracingStep tracingStep) {
		OTSyllabifier syllabifier = traceInfo.getSyllabifier();
		if (tracingStep.getSegmentsInWord() == null || tracingStep.getSegmentsInWord().size() == 0) {
			sb.append(SHTracingStep.NULL_REPRESENTATION);
		} else {
//			syllabifier.setSegmentsInWord(tracingStep.getSegmentsInWord());
			setLingTreeDescription(syllabifier.getLingTreeDescriptionOfCurrentWord());
			createSVGOfTree(sb, tracingStep.isSuccessful());
		}
	}

	protected void formatSSPInfo(StringBuilder sb, String rowStatus, NPTracingStep tracingStep) {
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
		sb.append("</tr>\n");
		sb.append("<tr>\n");
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
		sb.append("</td>");
		sb.append("</tr>\n");
		sb.append("</table>\n");
	}
}
