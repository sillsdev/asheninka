// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import java.util.List;
import java.util.Locale;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.model.otapproach.OTStructuralOptions;
import org.sil.syllableparser.model.otapproach.OTTraceInfo;
import org.sil.syllableparser.model.otapproach.OTTracingStep;
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

		sb.append("<p>" + formatDetailsStringWithColorWords("report.tawotdetails") + "</p>\n");
		sb.append("<div>");
		List<OTTracingStep> tracingSteps = syllabifier.getTracingSteps();
		formatOTSyllabificationDetails(sb, tracingSteps);
		sb.append("</div>");
	}

	protected void formatOTSyllabificationDetails(StringBuilder sb, List<OTTracingStep> tracingSteps) {
		if (tracingSteps.size() == 0) {
			return;
		}

		OTTracingStep previousTracingStep = null;
		for (OTTracingStep tracingStep : tracingSteps) {
			tracingStep.setBundle(bundle);
			if (tracingSteps.indexOf(tracingStep) > 0) {
				if (tracingStep.isAddedAsSyllable()) {
					formatAddingSyllable(sb, tracingStep);
				} else {
					formatEvaluatingConstraint(sb, previousTracingStep, tracingStep);
				}
			}
			previousTracingStep = tracingStep;
		}
	}

	protected void formatEvaluatingConstraint(StringBuilder sb, OTTracingStep previousTracingStep,
			OTTracingStep tracingStep) {
		sb.append("<div style='font-variant:small-caps'>\n");
		sb.append(tracingStep.getConstraintName());
		sb.append("</div>\n");

		sb.append("<table border='0'>\n");
		sb.append("<tr valign='top'>\n");
		sb.append("<td>");
		formatCandidateGrid(sb, previousTracingStep, tracingStep);
		sb.append("</td>\n");
		sb.append("<td>&#xa0;&#xa0;==>&#xa0;&#xa0;</td>\n");
		sb.append("<td>");
		formatCandidateGrid(sb, null, tracingStep);
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");
		sb.append("<p/>\n");
	}

	protected void formatAddingSyllable(StringBuilder sb, OTTracingStep tracingStep) {
		sb.append("<p>");
		StringBuilder sbSyl = new StringBuilder();
		for (OTSegmentInSyllable sis : tracingStep.getSyllable().getSegmentsInSyllable()) {
			sbSyl.append(sis.getGrapheme());
		}
		sb.append(getAddedArgument("report.tawotaddsyllable", sbSyl.toString()));
		sb.append("</p>\n");
	}

	protected void formatCandidateGrid(StringBuilder sb, OTTracingStep previousTracingStep,
			OTTracingStep tracingStep) {
		sb.append("<table border='1'>\n");
		sb.append("<tr valign='top'>\n");
		for (OTSegmentInSyllable segInSyl : tracingStep.getSegmentsInWord()) {
			sb.append("<th>");
			sb.append(segInSyl.getSegmentName());
			sb.append("</th>\n");
		}
		sb.append("</tr>\n");
		showStructuralOption(sb, tracingStep, previousTracingStep, OTStructuralOptions.ONSET, Constants.OT_STRUCTURAL_OPTION_ONSET);
		showStructuralOption(sb, tracingStep, previousTracingStep, OTStructuralOptions.NUCLEUS, Constants.OT_STRUCTURAL_OPTION_NUCLEUS);
		showStructuralOption(sb, tracingStep, previousTracingStep, OTStructuralOptions.CODA, Constants.OT_STRUCTURAL_OPTION_CODA);
		showStructuralOption(sb, tracingStep, previousTracingStep, OTStructuralOptions.UNPARSED, Constants.OT_STRUCTURAL_OPTION_UNPARSED);
		sb.append("</table>\n");
	}

	protected void showStructuralOption(StringBuilder sb, OTTracingStep tracingStep,
			OTTracingStep previousTracingStep, int iOption, String sOption) {
		sb.append("<tr valign='top'>\n");
		for (OTSegmentInSyllable segInSyl : tracingStep.getSegmentsInWord()) {
			sb.append("<td");
			if ((segInSyl.getStructuralOptions() & iOption) > 0) {
				sb.append(">");
				sb.append(sOption);
			} else {
				if (previousTracingStep != null) {
					int i = tracingStep.getSegmentsInWord().indexOf(segInSyl);
					OTSegmentInSyllable previousSegInSyl = previousTracingStep.getSegmentsInWord()
							.get(i);
					if ((previousSegInSyl.getStructuralOptions() & iOption) > 0) {
						sb.append(" style='background-color:");
						sb.append(bundle.getString("report.successcolorword"));
						sb.append("'>");
						sb.append(sOption);
					} else {
						sb.append(">");
					}
				} else {
					sb.append(">");
				}
				sb.append("&#xa0;");
			}
			sb.append("</td>\n");
		}
		sb.append("</tr>\n");
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

}
