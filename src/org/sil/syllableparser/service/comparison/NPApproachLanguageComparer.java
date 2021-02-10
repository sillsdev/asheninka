// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.List;

import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.service.parsing.NPSyllabifier;

/**
 * @author Andy Black
 *
 */
public class NPApproachLanguageComparer extends ONCApproachLanguageComparer {
	// TODO: is ONC right?  What about CV?

	NPApproach npa1;
	NPApproach npa2;

	public NPApproachLanguageComparer(NPApproach npa1, NPApproach npa2) {
		super(npa1.getLanguageProject().getONCApproach(), npa2.getLanguageProject().getONCApproach());
		this.npa1 = npa1;
		this.npa2 = npa2;
	}

	public NPApproach getNpa1() {
		return npa1;
	}

	public void setNpa1(NPApproach npa1) {
		this.npa1 = npa1;
	}

	public NPApproach getNpa2() {
		return npa2;
	}

	public void setNpa2(NPApproach npa2) {
		this.npa2 = npa2;
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(npa1, words1);
		syllabifyWords(npa2, words2);
	}

	protected void syllabifyWords(NPApproach mua, List<Word> words) {
		NPSyllabifier npSyllabifier = new NPSyllabifier(mua);
		for (Word word : words) {
			boolean fSuccess = npSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setNPPredictedSyllabification(npSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getNPPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getNPPredictedSyllabification());
	}

	// TODO: is this right?
//	@Override
//	protected boolean isReallySameSegment(Segment segment1, Segment segment2) {
//		boolean result = super.isReallySameSegment(segment1, segment2);
//		if (result) {
//			if (segment1.getMoras() != segment2.getMoras()) {
//				return false;
//			}
//		}
//		return result;
//	}
}
