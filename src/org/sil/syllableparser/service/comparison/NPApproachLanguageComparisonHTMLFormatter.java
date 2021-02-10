// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import java.time.LocalDateTime;
import java.util.Locale;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class NPApproachLanguageComparisonHTMLFormatter extends
		ONCApproachLanguageComparisonHTMLFormatter {

	NPApproachLanguageComparer npComparer;

	public NPApproachLanguageComparisonHTMLFormatter(NPApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.npComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public NPApproachLanguageComparisonHTMLFormatter(NPApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, locale);
		initialize(comparer, locale, dateTime);
		this.npComparer = comparer;
	}

	@Override
	public String format() {
		return format(bundle.getString("report.moraictitle"), bundle.getString("report.moraiccomparisonof"));
	}

	@Override
	protected void formatApproachSpecificSegmentHeader(StringBuilder sb, Segment seg) {
		sb.append("<th>");
		sb.append(bundle.getString("report.moras"));
		sb.append("</th>\n");
	}

	@Override
	protected void formatApproachSpecificSegmentInfo(StringBuilder sb, Segment seg, int iNumGraphemes) {
		String tdContent = "<td rowspan=\"" + iNumGraphemes + "\" align=\"center\" valign=\"top\">";
		sb.append(tdContent);
			sb.append(seg.getMoras());
		sb.append("</td>\n");
	}

	@Override
	protected void formatApproachSpecificSyllabificationParameters(StringBuilder sb) {
		String s1 = "";
		String s2 = "";
		if (comparer.useWeightByPositionDiffers) {
			if (comparer.langProj1UseWeightByPosition) {
				s1 = bundle.getString("label.yes");
				s2 = bundle.getString("label.no");
			} else {
				s1 = bundle.getString("label.no");
				s2 = bundle.getString("label.yes");
			}
			formatSyllabificationParameterInfo(sb, bundle.getString("report.useweightbypositiondiffers"), s1, s2);
		} else {
			sb.append("<p>" + bundle.getString("report.sameweightbyposition") + "</p>\n");
		}
		if (comparer.maxMorasDiffers) {
			s1 = Integer.toString(comparer.langProj1MaxMoras);
			s2 = Integer.toString(comparer.langProj2MaxMoras);
			formatSyllabificationParameterInfo(sb, bundle.getString("report.maxmorasdiffers"), s1, s2);
		} else {
			sb.append("<p>" + bundle.getString("report.samemaxmoras") + "</p>\n");
		}
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getMoraicPredictedSyllabification().length() == 0) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getMoraicPredictedSyllabification());
		}
	}
}
