/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 *         A rime in a syllable using the Onset-Nucleus-Coda approach
 *
 *         A value object
 *
 */
public class Rime extends ONCConstituent {
	private Nucleus nucleus = new Nucleus();
	private Coda coda = new Coda();

	public List<ONCSegmentInSyllable> getGraphemes() {
		graphemes.clear();
		graphemes.addAll(nucleus.getGraphemes());
		graphemes.addAll(coda.getGraphemes());
		return graphemes;
	}

	public Nucleus getNucleus() {
		return nucleus;
	}

	public void setNucleus(Nucleus nucleus) {
		this.nucleus = nucleus;
	}

	public Coda getCoda() {
		return coda;
	}

	public void setCoda(Coda coda) {
		this.coda = coda;
	}

	public boolean exists() {
		return nucleus.exists();
	}

	public void createLingTreeDescription(StringBuilder sb) {
		if (nucleus.exists()) {
			sb.append("(R");
			nucleus.createLingTreeDescription(sb);
			coda.createLingTreeDescription(sb);
			sb.append(")");
		}
	}

	public void getONCPattern(StringBuilder sb) {
		nucleus.getONCPattern(sb);
		coda.getONCPattern(sb);
	}

	@Override
	public void applyAnyRepairFilters(List<ONCSegmentInSyllable> segmentsInWord, int iSegmentInWord,
			ONCSyllable syl, LinkedList<ONCSyllable> syllablesInCurrentWord, SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		// TODO: flesh out
	}
}
