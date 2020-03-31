// Copyright (c) 2019-2020 SIL International 
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

import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class ONCApproachLanguageComparisonHTMLFormatter extends
		ApproachLanguageComparisonHTMLFormatter {

	ONCApproachLanguageComparer oncComparer;

	public ONCApproachLanguageComparisonHTMLFormatter(ONCApproachLanguageComparer comparer,
			Locale locale) {
		super(comparer, comparer.getOnca1().getLanguageProject(), comparer.getOnca2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, LocalDateTime.now());
		this.oncComparer = comparer;
	}

	// Used for testing so the date time can be constant
	public ONCApproachLanguageComparisonHTMLFormatter(ONCApproachLanguageComparer comparer,
			Locale locale, LocalDateTime dateTime) {
		super(comparer, comparer.getOnca1().getLanguageProject(), comparer.getOnca2()
				.getLanguageProject(), locale);
		initialize(comparer, locale, dateTime);
		this.oncComparer = comparer;
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		formatHTMLBeginning(sb, bundle.getString("report.onctitle"));
		formatOverview(sb, bundle.getString("report.onccomparisonof"));
		formatSegmentInventory(sb);
		formatGraphemeNaturalClasses(sb);
		formatEnvironments(sb);
		formatSonorityHierarchy(sb);
		formatSonorityHierarchyOrder(sb);
		formatSyllabifcationParameters(sb);
		formatNaturalClasses(sb, oncComparer.getCVNaturalClassesWhichDiffer());
		formatTemplates(sb);
		formatTemplateOrder(sb);
		formatFilters(sb);
		formatFilterOrder(sb);
		formatWords(sb);
		formatHTMLEnding(sb);
		return sb.toString();
	}

	protected void formatSonorityHierarchy(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.shsonorityhierarchy") + "</h3>\n");
		SortedSet<DifferentSHNaturalClass> diffNaturalCLasses = oncComparer
				.getNaturalClassesWhichDiffer();
		if (diffNaturalCLasses.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sameshsonorityhierarchy") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.shnaturalclasseswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentSHNaturalClass differentNaturalCLass : diffNaturalCLasses) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				SHNaturalClass naturalClass = (SHNaturalClass) differentNaturalCLass.objectFrom1;
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (SHNaturalClass) differentNaturalCLass.objectFrom2;
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatNaturalClassInfo(StringBuilder sb, SHNaturalClass naturalClass) {
		if (naturalClass == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(naturalClass.getNCName());
			sb.append(" (");
			sb.append(naturalClass.getSegmentsRepresentation());
			sb.append(")");
		}
	}

	protected void formatSonorityHierarchyOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = oncComparer.getSonorityHierarchyOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.shsonorityhierarchyorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<SHNaturalClass> sonorityHierarchy1 = oncComparer.getOnca1().getONCSonorityHierarchy();
			List<SHNaturalClass> sonorityHierarchy2 = oncComparer.getOnca2().getONCSonorityHierarchy();
			int size1 = sonorityHierarchy1.size();
			int size2 = sonorityHierarchy2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				SHNaturalClass naturalClass = (SHNaturalClass) formatSylParserObjectInOrder(sonorityHierarchy1,
						size1, i);
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (SHNaturalClass) formatSylParserObjectInOrder(sonorityHierarchy2, size2, i);
				formatNaturalClassInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatTemplates(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.templates") + "</h3>\n");
		SortedSet<DifferentTemplate> diffTemplates = oncComparer
				.getTemplatesWhichDiffer();
		if (diffTemplates.size() == 0) {
			sb.append("<p>" + bundle.getString("report.sametemplates") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.templateswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentTemplate differentTemplate : diffTemplates) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				Template template = (Template) differentTemplate.objectFrom1;
				formatTemplateInfo(sb, template);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				template = (Template) differentTemplate.objectFrom2;
				formatTemplateInfo(sb, template);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatTemplateInfo(StringBuilder sb, Template template) {
		if (template == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(template.getTemplateFilterName());
			sb.append(" [");
			sb.append(template.getType());
			sb.append("] (");
			sb.append(template.getTemplateFilterRepresentation());
			sb.append(")");
		}
	}

	protected void formatTemplateOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = oncComparer.getTemplateOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.templateinorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<Template> templates1 = oncComparer.getOnca1().getLanguageProject().getActiveAndValidTemplates();
			List<Template> templates2 = oncComparer.getOnca2().getLanguageProject().getActiveAndValidTemplates();
			int size1 = templates1.size();
			int size2 = templates2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				Template template = (Template) formatSylParserObjectInOrder(templates1,
						size1, i);
				formatTemplateInfo(sb, template);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				template = (Template) formatSylParserObjectInOrder(templates2, size2, i);
				formatTemplateInfo(sb, template);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected SylParserObject formatSylParserObjectInOrder(List<? extends SylParserObject> items,
			int size1, int i) {
		SylParserObject item;
		if (i < size1) {
			item = items.get(i);
		} else {
			item = null;
		}
		return item;
	}

	protected void formatFilters(StringBuilder sb) {
		sb.append("<h3>" + bundle.getString("report.filters") + "</h3>\n");
		SortedSet<DifferentFilter> diffFilters = oncComparer
				.getFiltersWhichDiffer();
		if (diffFilters.size() == 0) {
			sb.append("<p>" + bundle.getString("report.samefilters") + "</p>\n");
		} else {
			sb.append("<p>" + bundle.getString("report.filterswhichdiffer") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			for (DifferentFilter differentFilters : diffFilters) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				Filter filter = (Filter) differentFilters.objectFrom1;
				formatFilterInfo(sb, filter);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				filter = (Filter) differentFilters.objectFrom2;
				formatFilterInfo(sb, filter);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	protected void formatFilterInfo(StringBuilder sb, Filter filter) {
		if (filter == null) {
			sb.append("&#xa0;");
		} else {
			sb.append(filter.getTemplateFilterName());
			sb.append(" {");
			boolean isRepair = filter.getAction().isDoRepair();
			sb.append(isRepair ? bundle.getString("radio.repairaction") : bundle.getString("radio.failaction"));
			sb.append("} [");
			sb.append(filter.getType());
			sb.append("] (");
			sb.append(filter.getTemplateFilterRepresentation());
			sb.append(")");
		}
	}

	protected void formatFilterOrder(StringBuilder sb) {
		LinkedList<Diff> diffs = oncComparer.getFilterOrderDifferences();
		if (diffs.size() > 1) {
			sb.append("<p>" + bundle.getString("report.filterinorder") + "</p>\n");
			sb.append("<table border=\"1\">\n<thead>\n<tr>\n<th>");
			sb.append(getAdjectivalForm("report.first", "report.adjectivalendingm"));
			sb.append("</th>\n<th>");
			sb.append(getAdjectivalForm("report.second", "report.adjectivalendingm"));
			sb.append("</th>\n</tr>\n</thead>\n<tbody>\n");
			List<Filter> sonorityHierarchy1 = oncComparer.getOnca1().getLanguageProject().getActiveAndValidFilters();
			List<Filter> sonorityHierarchy2 = oncComparer.getOnca2().getLanguageProject().getActiveAndValidFilters();
			int size1 = sonorityHierarchy1.size();
			int size2 = sonorityHierarchy2.size();
			int maxSize = Math.max(size1, size2);
			for (int i = 0; i < maxSize; i++) {
				sb.append("<tr>\n<td class=\"");
				sb.append(ANALYSIS_1);
				sb.append("\">");
				Filter naturalClass = (Filter) formatSylParserObjectInOrder(sonorityHierarchy1,
						size1, i);
				formatFilterInfo(sb, naturalClass);
				sb.append("</td>\n<td class=\"");
				sb.append(ANALYSIS_2);
				sb.append("\">");
				naturalClass = (Filter) formatSylParserObjectInOrder(sonorityHierarchy2, size2, i);
				formatFilterInfo(sb, naturalClass);
				sb.append("</td>\n</tr>\n");
			}
			sb.append("</tbody>\n</table>\n");
		}
	}

	@Override
	protected void formatPredictedSyllabification(StringBuilder sb, Word word) {
		if (word == null || word.getONCPredictedSyllabification().length() == 0) {
			sb.append("&#xa0;");
		} else {
			sb.append(word.getONCPredictedSyllabification());
		}
	}
}
