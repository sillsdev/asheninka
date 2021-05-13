// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.otapproach;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;

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
}
