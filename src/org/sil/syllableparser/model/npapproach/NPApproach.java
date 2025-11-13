// Copyright (c) 2021-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.npapproach;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
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
	private ObservableList<NPFilter> filters = FXCollections.observableArrayList();

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

	public List<NPRule> getValidActiveNPRules() {
		return rules.stream().filter(rule -> rule.isValid() && rule.isActive())
				.collect(Collectors.toList());
	}

	@XmlElementWrapper(name = "filters")
	@XmlElement(name = "filter")
	public ObservableList<NPFilter> getNPFilters() {
		return filters;
	}
	public void setNPFilters(ObservableList<NPFilter> filters) {
		this.filters = filters;
	}

	public List<NPFilter> getActiveNPFilters() {
		return filters.stream().filter(filter -> filter.isActive())
				.collect(Collectors.toList());
	}

	public List<NPFilter> getValidActiveNPFilters() {
		return filters.stream().filter(filter -> filter.isValid() && filter.isActive())
				.collect(Collectors.toList());
	}

	/**
	 * Clear out all data in this Sonority Hierarchy approach
	 */
	public void clear() {
		rules.clear();
		filters.clear();
	}

	public void load(NPApproach npApproachLoaded) {
		langProj = this.getLanguageProject();
		shApproach = langProj.getSHApproach();
		for (NPRule rule : npApproachLoaded.getNPRules()) {
			rules.add(rule);
		}
		for (NPFilter filter : npApproachLoaded.getNPFilters()) {
			filters.add(filter);
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

	public void createDefaultSetOfRules() {
		CVSegmentOrNaturalClass emptySegOrNC = null;
		Optional<CVNaturalClass> ncVowelsOpt = getActiveCVNaturalClasses().stream()
				.filter(nc -> nc.getNCName().equals("V")).findFirst();
		CVSegmentOrNaturalClass vowelsSegOrNC = new CVSegmentOrNaturalClass();
		CVNaturalClass ncVowels = null;
		if (ncVowelsOpt.isPresent()) {
			ncVowels = ncVowelsOpt.get();
			vowelsSegOrNC = new CVSegmentOrNaturalClass(ncVowels.getNCName(),
					ncVowels.getDescription(), false, ncVowels.getID(), true);
			NPRule buildNucleus = new NPRule("Nucleus", "Build nucleus", vowelsSegOrNC,
					emptySegOrNC, NPRuleAction.BUILD, NPRuleLevel.ALL, true, true,
					"(N''(N'(N(\\L [V]))))");
			buildNucleus.setAffectedSegmentOrNaturalClass(ncVowels.getNCName());
			getNPRules().add(buildNucleus);
		}
		Optional<CVNaturalClass> ncCOpt = getActiveCVNaturalClasses().stream()
				.filter(nc -> nc.getNCName().equals("C")).findFirst();
		CVSegmentOrNaturalClass cSegOrNC = new CVSegmentOrNaturalClass();
		CVNaturalClass ncC = null;
		if (ncCOpt.isPresent()) {
			ncC = ncCOpt.get();
			cSegOrNC = new CVSegmentOrNaturalClass(ncC.getNCName(), ncC.getDescription(), false,
					ncC.getID(), true);
			NPRule attachOnset = new NPRule("Onset", "Attach onset to N''", cSegOrNC,
					vowelsSegOrNC, NPRuleAction.ATTACH, NPRuleLevel.N_DOUBLE_BAR, true, true,
					"(N''(\\L [C])(N'(N(\\L [V]))))");
			attachOnset.setAffectedSegmentOrNaturalClass(ncC.getNCName());
			attachOnset.setContextSegmentOrNaturalClass(ncVowels.getNCName());
			getNPRules().add(attachOnset);
		}
		if (ncVowelsOpt.isPresent() && ncCOpt.isPresent()) {
			NPRule attachCoda = new NPRule("Coda", "Attach coda to N'", cSegOrNC,
					vowelsSegOrNC, NPRuleAction.ATTACH, NPRuleLevel.N_BAR, true, true,
					"(N'(N(\\L [V]))(\\L [C]))");
			attachCoda.setAffectedSegmentOrNaturalClass(ncC.getNCName());
			attachCoda.setContextSegmentOrNaturalClass(ncVowels.getNCName());
			getNPRules().add(attachCoda);
		}
		if (ncCOpt.isPresent()) {
			NPRule augmentOnset = new NPRule("Additional onset", "Augment onset with another onset", cSegOrNC,
					cSegOrNC, NPRuleAction.AUGMENT, NPRuleLevel.N_DOUBLE_BAR, true, true,
					"(N''(\\L [C])(\\L [C]))");
			augmentOnset.setAffectedSegmentOrNaturalClass(ncC.getNCName());
			augmentOnset.setContextSegmentOrNaturalClass(ncC.getNCName());
			getNPRules().add(augmentOnset);
			NPRule augmentCoda = new NPRule("Additional coda", "Augment coda with another coda", cSegOrNC,
					cSegOrNC, NPRuleAction.AUGMENT, NPRuleLevel.N_BAR, true, true,
					"(N'(\\L [C])(\\L [C]))");
			augmentCoda.setAffectedSegmentOrNaturalClass(ncC.getNCName());
			augmentCoda.setContextSegmentOrNaturalClass(ncC.getNCName());
			getNPRules().add(augmentCoda);
		}
	}


}
