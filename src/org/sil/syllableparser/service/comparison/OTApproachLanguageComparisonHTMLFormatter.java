// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.comparison;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import javafx.scene.paint.Color;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.lingtree.model.FontInfo;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.syllableparser.service.LingTreeInteractor;

/**
 * @author Andy Black
 *
 */
public class OTApproachLanguageComparisonHTMLFormatter extends
		ONCApproachLanguageComparisonHTMLFormatter {

	OTApproachLanguageComparer otComparer;
	protected LingTreeInteractor ltInteractor;

	public OTApproachLanguageComparisonHTMLFormatter(OTApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.otComparer = comparer;
		ltInteractor = LingTreeInteractor.getInstance();
	}

	// Used for testing so the date time can be constant
	public OTApproachLanguageComparisonHTMLFormatter(OTApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, locale);
		initialize(comparer, locale, dateTime);
		this.otComparer = comparer;
		ltInteractor = LingTreeInteractor.getInstance();
	}

	@Override
	public String format() {
		return format(bundle.getString("report.ottitle"), bundle.getString("report.otcomparisonof"));
	}

	public String format(String sTitle, String sComparisonOf) {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, sTitle);
		formatOverview(sb, sComparisonOf);
		formatSegmentInventory(sb);
		formatNaturalClasses(sb, otComparer.getCVNaturalClassesWhichDiffer());
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatOTConstraints(sb);
		formatOTConstraintRankings(sb);
		formatOTRankingsOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatOTConstraints(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.constraints") + "</h3>\n");
		SortedSet<DifferentOTConstraint> diffConstraints = otComparer.getConstraintsWhichDiffer();
		if (diffConstraints.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sameconstraints") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.constraintswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentOTConstraint differentconstraints : diffConstraints) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\" valign=\"top\">");
				OTConstraint constraint = (OTConstraint) differentconstraints.objectFrom1;
				formatOTConstraintInfo(sb, constraint);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\" valign=\"top\">");
				constraint = (OTConstraint) differentconstraints.objectFrom2;
				formatOTConstraintInfo(sb, constraint);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatOTConstraintInfo(StringBuilder sb, OTConstraint constraint) {
		if (constraint == null) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(Constants.NON_BREAKING_SPACE);
			sb.append(constraint.getConstraintName());
			sb.append("<br/>");
			ltInteractor.initializeParameters(langProj1);
			FontInfo fiAnalysis = new FontInfo(langProj1.getAnalysisLanguage().getFont());
			fiAnalysis.setColor(Color.BLACK);
			ltInteractor.setLexicalFontInfo(fiAnalysis);
			ltInteractor.setVerticalGap(30.0);
			double yInit = ltInteractor.getInitialYCoordinate();
			ltInteractor.setInitialYCoordinate(yInit-(ltInteractor.getVerticalGap()));
			String ltSVG = ltInteractor.createSVG(constraint.getLingTreeDescription(), true);
			sb.append(ltSVG);
		}
	}

	protected void formatOTConstraintRankings(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.constraintrankings") + "</h3>\n");
		SortedSet<DifferentOTConstraintRanking> diffRankings = otComparer.getConstraintRankingsWhichDiffer();
		if (diffRankings.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sameconstraintrankings") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.constraintrankingswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentOTConstraintRanking differentconstraints : diffRankings) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\" valign=\"top\">");
				OTConstraintRanking ranking = (OTConstraintRanking) differentconstraints.objectFrom1;
				formatOTConstraintRankingInfo(sb, ranking);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\" valign=\"top\">");
				ranking = (OTConstraintRanking) differentconstraints.objectFrom2;
				formatOTConstraintRankingInfo(sb, ranking);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatOTConstraintRankingInfo(StringBuilder sb, OTConstraintRanking ranking) {
		if (ranking == null) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(ranking.getName());
			sb.append("<br/>");
			sb.append(ranking.getRankingRepresentation());
		}
	}

	protected void formatOTRankingsOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = otComparer.getRankingOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.constraintrankingsinorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<OTConstraintRanking> otRankings1 = otComparer.getOta1().getActiveOTConstraintRankings();
			List<OTConstraintRanking> otRankings2 = otComparer.getOta2().getActiveOTConstraintRankings();
			int size1 = otRankings1.size();
			int size2 = otRankings2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\" valign=\"top\">");
				OTConstraintRanking ranking = (OTConstraintRanking) formatSylParserObjectInOrder(otRankings1,
						size1, i);
				formatOTConstraintRankingInfo(sb, ranking);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\" valign=\"top\">");
				ranking = (OTConstraintRanking) formatSylParserObjectInOrder(otRankings2, size2, i);
				formatOTConstraintRankingInfo(sb, ranking);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getOTPredictedSyllabification().length() == 0) {
			sb.append(Constants.NON_BREAKING_SPACE);
		} else {
			sb.append(word.getOTPredictedSyllabification());
		}
	}
}
