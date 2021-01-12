/**
 * Copyright (c) 2020-2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * @author Andy Black
 *
 * A constituent in a syllable 
 *
 * A value object
 *
 */
public abstract class SyllableConstituent {
	protected List<CVSegmentInSyllable> graphemes = new ArrayList<CVSegmentInSyllable>();
	protected List<Filter> failFilters = new ArrayList<Filter>();
	protected List<Filter> repairFilters = new ArrayList<Filter>();
	protected List<Template> templates = new ArrayList<Template>();

	public List<?extends CVSegmentInSyllable> getGraphemes() {
		return graphemes;
	}

	public void setGraphemes(List<CVSegmentInSyllable> graphemesInConstituent) {
		this.graphemes = graphemesInConstituent;
	}
	
	public boolean exists() {
		if (graphemes.size() > 0)
			return true;
		return false;
	}
	
	public List<Filter> getFailFilters() {
		return failFilters;
	}

	public void setFailFilters(List<Filter> failFilters) {
		this.failFilters = failFilters;
	}

	public List<Filter> getRepairFilters() {
		return repairFilters;
	}

	public void setRepairFilters(List<Filter> repairFilters) {
		this.repairFilters = repairFilters;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	protected void createLingTreeDescription(StringBuilder sb, String sType) {
		if (graphemes.size() > 0) {
			sb.append("(");
			sb.append(sType);
			for (CVSegmentInSyllable cvSegmentInSyllable : graphemes) {
				sb.append("(\\L ");
				sb.append(cvSegmentInSyllable.getSegment().getSegment());
				sb.append("(\\G ");
				sb.append(cvSegmentInSyllable.getGrapheme());
				sb.append("))");
			}
			sb.append(")");
		}
	}
}
