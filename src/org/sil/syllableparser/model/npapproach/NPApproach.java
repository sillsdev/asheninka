// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.npapproach;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */

public class NPApproach extends Approach {

	LanguageProject langProj;
	SHApproach shApproach;
	private ObservableList<NPRule> rules = FXCollections.observableArrayList();

	@XmlElementWrapper(name = "rules")
	@XmlElement(name = "rule")
	public ObservableList<NPRule> getNPRules() {
		return rules;
	}
	public void setNPRules(ObservableList<NPRule> rules) {
		this.rules = rules;
	}

	public List<NPRule> getActiveNPRules() {
		return rules.stream().filter(rule -> rule.isActive())
				.collect(Collectors.toList());
	}


	/**
	 * Clear out all data in this Sonority Hierarchy approach
	 */
	public void clear() {
		rules.clear();
	}

	public void load(NPApproach npApproachLoaded) {
		langProj = this.getLanguageProject();
		shApproach = langProj.getSHApproach();
		for (NPRule rule : npApproachLoaded.getNPRules()) {
			rules.add(rule);
		}
	}

	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getNPPredictedSyllabification();
	}

	public ObservableList<SHNaturalClass> getNPSonorityHierarchy() {
		return getLanguageProject().getSHApproach().getSHSonorityHierarchy();
	}

	public List<SHNaturalClass> getActiveSHNaturalClasses() {
		return shApproach.getActiveSHNaturalClasses();
	}

	public SHNaturalClass getNaturalClassContainingSegment(Segment seg1) {
		return shApproach.getNaturalClassContainingSegment(seg1);
	}

	public List<CVNaturalClass> getActiveCVNaturalClasses() {
		return getLanguageProject().getCVApproach().getActiveCVNaturalClasses();
	}
}
