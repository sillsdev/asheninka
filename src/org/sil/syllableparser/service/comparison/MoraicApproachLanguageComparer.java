// Copyright (c) 2020-2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.List;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.service.parsing.MoraicSyllabifier;

/**
 * @author Andy Black
 *
 */
public class MoraicApproachLanguageComparer extends ONCApproachLanguageComparer {

	MoraicApproach mua1;
	MoraicApproach mua2;

	public MoraicApproachLanguageComparer(MoraicApproach mua1, MoraicApproach mua2) {
		super(mua1.getLanguageProject().getONCApproach(), mua2.getLanguageProject().getONCApproach());
		this.mua1 = mua1;
		this.mua2 = mua2;
	}

	public MoraicApproach getMua1() {
		return mua1;
	}

	public void setMua1(MoraicApproach mua1) {
		this.mua1 = mua1;
	}

	public MoraicApproach getMua2() {
		return mua2;
	}

	public void setMua2(MoraicApproach mua2) {
		this.mua2 = mua2;
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		syllabifyWords(mua1, words1);
		syllabifyWords(mua2, words2);
	}

	protected void syllabifyWords(MoraicApproach mua, List<Word> words) {
		MoraicSyllabifier moraicSyllabifier = new MoraicSyllabifier(mua);
		for (Word word : words) {
			boolean fSuccess = moraicSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setMoraicPredictedSyllabification(moraicSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		return word.getMoraicPredictedSyllabification().equals(
				((Word) diffWord.getObjectFrom1()).getMoraicPredictedSyllabification());
	}

	@Override
	protected boolean isReallySameSegment(Segment segment1, Segment segment2) {
		boolean result = super.isReallySameSegment(segment1, segment2);
		if (result) {
			if (segment1.getMoras() != segment2.getMoras()) {
				return false;
			}
		}
		return result;
	}
}
