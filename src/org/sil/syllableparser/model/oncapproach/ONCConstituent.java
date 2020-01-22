/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Black
 *
 * A constituent in a syllable using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public abstract class ONCConstituent {
	protected List<ONCSegmentInSyllable> graphemes = new ArrayList<ONCSegmentInSyllable>();

	public List<ONCSegmentInSyllable> getGraphemes() {
		return graphemes;
	}

	public void setGraphemes(List<ONCSegmentInSyllable> graphemesInConstituent) {
		this.graphemes = graphemesInConstituent;
	}
	
	public boolean exists() {
		if (graphemes.size() > 0)
			return true;
		return false;
	}
	
	public void add(ONCSegmentInSyllable oncSegmentInSyllable) {
		graphemes.add(oncSegmentInSyllable);
	}
	
	protected void createLingTreeDescription(StringBuilder sb, String sType) {
		if (graphemes.size() > 0) {
			sb.append("(");
			sb.append(sType);
			for (ONCSegmentInSyllable oncSegmentInSyllable : graphemes) {
				sb.append("(\\L ");
				sb.append(oncSegmentInSyllable.getSegment().getSegment());
				sb.append("(\\G ");
				sb.append(oncSegmentInSyllable.getGrapheme());
				sb.append("))");
			}
			sb.append(")");
		}
	}
	
	protected void getONCPattern(StringBuilder sb, String sType) {
		for (int i=0; i < graphemes.size(); i++) {
			sb.append(sType);
		}
	}
}
