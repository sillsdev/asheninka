// Copyright (c) 2021-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.otapproach;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;

/**
 * @author Andy Black
 *
 */

public class OTApproach extends Approach {

	LanguageProject langProj;
	private ObservableList<OTConstraint> constraints = FXCollections.observableArrayList();
	private ObservableList<OTConstraintRanking> constraintRankings = FXCollections.observableArrayList();

	@XmlElementWrapper(name = "constraints")
	@XmlElement(name = "constraint")
	public ObservableList<OTConstraint> getOTConstraints() {
		return constraints;
	}

	public void setOTConstraints(ObservableList<OTConstraint> constraints) {
		this.constraints = constraints;
	}

	public List<OTConstraint> getActiveOTConstraints() {
		return constraints.stream().filter(c -> c.isActive())
				.collect(Collectors.toList());
	}

	public List<OTConstraint> getValidActiveOTConstraints() {
		return constraints.stream().filter(c -> c.isActive() & c.isValid())
				.collect(Collectors.toList());
	}

	@XmlElementWrapper(name = "rankings")
	@XmlElement(name = "ranking")
	public ObservableList<OTConstraintRanking> getOTConstraintRankings() {
		return constraintRankings;
	}
	public void setOTConstraintRankings(ObservableList<OTConstraintRanking> rankings) {
		this.constraintRankings = rankings;
	}

	public List<OTConstraintRanking> getActiveOTConstraintRankings() {
		return constraintRankings.stream().filter(r -> r.isActive())
				.collect(Collectors.toList());
	}

	/**
	 * Clear out all data in this Sonority Hierarchy approach
	 */
	public void clear() {
		constraints.clear();
		constraintRankings.clear();
	}

	public void load(OTApproach otApproachLoaded) {
		langProj = this.getLanguageProject();
		for (OTConstraint constraint : otApproachLoaded.getOTConstraints()) {
			constraints.add(constraint);
		}
		for (OTConstraintRanking ranking : otApproachLoaded.getOTConstraintRankings()) {
			constraintRankings.add(ranking);
		}
	}

	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getOTPredictedSyllabification();
	}

	public List<CVNaturalClass> getActiveCVNaturalClasses() {
		return getLanguageProject().getCVApproach().getActiveCVNaturalClasses();
	}

	public void createDefaultSetOfConstraints(ResourceBundle bundle) {
		CVSegmentOrNaturalClass emptySegOrNC = null;
		Optional<CVNaturalClass> ncVowelsOpt = getActiveCVNaturalClasses().stream()
				.filter(nc -> nc.getNCName().equals("V")).findFirst();
		CVSegmentOrNaturalClass vowelsSegOrNC = new CVSegmentOrNaturalClass();
		CVNaturalClass ncVowels = null;
		if (ncVowelsOpt.isPresent()) {
			ncVowels = ncVowelsOpt.get();
			vowelsSegOrNC = new CVSegmentOrNaturalClass(ncVowels.getNCName(),
					ncVowels.getDescription(), false, ncVowels.getID(), true);
			OTConstraint marginV = new OTConstraint("*Margin/V",
					"Vowels are neither onsets nor codas", vowelsSegOrNC, emptySegOrNC,
					OTStructuralOptions.COMBO_O_C, OTStructuralOptions.INITIALIZED, "", "", false,
					true);
			marginV.setAffectedElement1(ncVowels.getNCName());
			marginV.setAffectedElement2("");
			marginV.setLingTreeDescription("(\\O(\\O[V]({o, c})))");
			getOTConstraints().add(marginV);
		}
		Optional<CVNaturalClass> ncCOpt = getActiveCVNaturalClasses().stream()
				.filter(nc -> nc.getNCName().equals("C")).findFirst();
		CVSegmentOrNaturalClass cSegOrNC = new CVSegmentOrNaturalClass();
		CVNaturalClass ncC = null;
		if (ncCOpt.isPresent()) {
			ncC = ncCOpt.get();
			cSegOrNC = new CVSegmentOrNaturalClass(ncC.getNCName(), ncC.getDescription(), false,
					ncC.getID(), true);
			OTConstraint peakC = new OTConstraint("*Peak/C", "Syllable peaks are not consonants",
					cSegOrNC, emptySegOrNC, OTStructuralOptions.NUCLEUS,
					OTStructuralOptions.INITIALIZED, "", "", false, true);
			peakC.setAffectedElement1(ncC.getNCName());
			peakC.setAffectedElement2("");
			peakC.setLingTreeDescription("(\\O(\\O[C](n)))");
			getOTConstraints().add(peakC);

			OTConstraint complexOnset = new OTConstraint("*Complex/Onset", "Avoid complex onsets",
					cSegOrNC, cSegOrNC, OTStructuralOptions.ONSET, OTStructuralOptions.ONSET, "",
					"", false, true);
			complexOnset.setAffectedElement1(ncC.getNCName());
			complexOnset.setAffectedElement2(ncC.getNCName());
			complexOnset.setLingTreeDescription("(\\O(\\O[C](o))(\\O[C](o)))");
			getOTConstraints().add(complexOnset);

			OTConstraint complexCoda = new OTConstraint("*Complex/Coda", "Avoid complex codas",
					cSegOrNC, cSegOrNC, OTStructuralOptions.CODA, OTStructuralOptions.CODA, "", "",
					true, true);
			complexCoda.setAffectedElement1(ncC.getNCName());
			complexCoda.setAffectedElement2(ncC.getNCName());
			complexCoda.setLingTreeDescription("(\\O(\\O[C](c))(\\O[C](c)))");
			getOTConstraints().add(complexCoda);
		}
		OTConstraint noCoda = new OTConstraint("NoCoda", "Codas not allowed", emptySegOrNC,
				emptySegOrNC, OTStructuralOptions.CODA, OTStructuralOptions.INITIALIZED, "", "",
				false, true);
		noCoda.setAffectedElement1(bundle.getString("label.any"));
		noCoda.setAffectedElement2("");
		noCoda.setLingTreeDescription("(\\O(\\O<Any>(c)))");
		getOTConstraints().add(noCoda);

		OTConstraint parse = new OTConstraint("Parse", "Every segment should be parsed",
				emptySegOrNC, emptySegOrNC, OTStructuralOptions.UNPARSED,
				OTStructuralOptions.INITIALIZED, "", "", false, true);
		parse.setAffectedElement1(bundle.getString("label.any"));
		parse.setAffectedElement2("");
		parse.setLingTreeDescription("(\\O(\\O<Any>(u)))");
		getOTConstraints().add(parse);

		OTConstraint onset1 = new OTConstraint("Onset1",
				"Avoid anything other than an onset before a nucleus", emptySegOrNC, emptySegOrNC,
				OTStructuralOptions.COMBO_N_C_U, OTStructuralOptions.NUCLEUS, "", "", false, true);
		onset1.setAffectedElement1(bundle.getString("label.any"));
		onset1.setAffectedElement2(bundle.getString("label.any"));
		onset1.setLingTreeDescription("(\\O(\\O<Any>(u)))");
		getOTConstraints().add(onset1);

		OTConstraint onset2 = new OTConstraint("Onset2", "Avoid a nucleus word initially",
				emptySegOrNC, emptySegOrNC, OTStructuralOptions.WORD_INITIAL
						+ OTStructuralOptions.NUCLEUS, OTStructuralOptions.INITIALIZED, "", "",
				false, true);
		onset2.setAffectedElement1(bundle.getString("label.any"));
		onset2.setAffectedElement2("");
		onset2.setLingTreeDescription("(\\O(\\O# <Any>(n)))");
		getOTConstraints().add(onset2);
	}

}
