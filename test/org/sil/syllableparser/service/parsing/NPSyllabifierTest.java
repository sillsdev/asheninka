// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Test;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.npapproach.NPFilter;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.model.npapproach.NPSyllabificationStatus;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class NPSyllabifierTest extends NPSyllabifierTestBase {

	@Test
	public void wordToSegmentToSyllableCodasOnMaxAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		turnOnseMaximizationOn();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fun", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", true, 2, "don.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	protected void turnOnseMaximizationOff() {
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		// onset maximization is handled by ordering the rules
		rule = rules.get(3);
		rules.add(2, rule);
		rules.remove(4);
		ObservableList<NPRule> orules = FXCollections.observableArrayList(rules);
		npApproach.setNPRules(orules);
	}

	protected void turnOnseMaximizationOn() {
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		// onset maximization is handled by ordering the rules
		rule = rules.get(3);
		rules.add(2, rule);
		rules.remove(4);
		ObservableList<NPRule> orules = FXCollections.observableArrayList(rules);
		npApproach.setNPRules(orules);
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		turnOnseMaximizationOn();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", false, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fun", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", true, 2, "don.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");

	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		turnOnseMaximizationOn();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fun", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", true, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", true, 2, "don.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fun", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", true, 2, "don.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", false, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fun", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", true, 2, "don.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 2, "dan.gem", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L n(\\G n)))))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e)))(\\L m(\\G m))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fun", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", true, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", true, 2, "don.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n)))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnAllButFirstTest() {
		turnCodasAllowedOff();
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n)))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L m(\\G m)))");
		checkSyllabification("dankgem", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L m(\\G m)))");
		checkSyllabification("dampidon", false, 3, "da.pi.do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L m(\\G m))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n)))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n)))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", false, 2, "fu.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", false, 2, "do.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", false, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	protected void turnCodasAllowedOff() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		// coda off is handled by deactivating the coda-oriented rules
		Optional<NPRule> codaRule = rules.stream()
				.filter(r -> r.getID().equals("7d8c3b88-7d72-40ac-a8f2-0f1df56aeef1")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(false);
		codaRule = rules.stream()
				.filter(r -> r.getID().equals("b70cc8c6-13e8-4dc9-87ef-3a2f9cdcd7eb")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(false);
	}

	protected void turnCodasAllowedOn() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		// coda on is handled by activating the coda-oriented rules
		Optional<NPRule> codaRule = rules.stream()
				.filter(r -> r.getID().equals("7d8c3b88-7d72-40ac-a8f2-0f1df56aeef1")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(true);
		codaRule = rules.stream()
				.filter(r -> r.getID().equals("b70cc8c6-13e8-4dc9-87ef-3a2f9cdcd7eb")).findFirst();
		assertTrue(codaRule.isPresent());
		codaRule.get().setActive(true);
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnEveryTest() {
		turnCodasAllowedOff();
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", false, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n)))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L m(\\G m)))");
		checkSyllabification("dankgem", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L m(\\G m)))");
		checkSyllabification("dampidon", false, 3, "da.pi.do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L m(\\G m))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n)))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n)))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", false, 2, "fu.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", false, 2, "do.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", false, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnNoOnsetsRequiredTest() {
		turnCodasAllowedOff();
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("b", false, 0, "", "(W(\\O \\L b(\\G b)))");
		checkSyllabification("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tad", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d)))");
		checkSyllabification("tan", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n)))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("dangem", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L m(\\G m)))");
		checkSyllabification("dankgem", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n))(\\O \\L k(\\G k))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L m(\\G m)))");
		checkSyllabification("dampidon", false, 3, "da.pi.do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L m(\\G m))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n)))");
		checkSyllabification("dovdek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L v(\\G v))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		checkSyllabification("fuhgt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L h(\\G h))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("fungt", false, 1, "fu", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n))(\\O \\L g(\\G g))(\\O \\L t(\\G t)))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L g(\\G g))(\\O \\L h(\\G h)))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n)))");
		checkSyllabification("do", true, 1, "do", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funglo", false, 2, "fu.glo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(\\O \\L n(\\G n))(σ(N''(\\L g(\\G g))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u))))))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("flu", true, 1, "flu", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u))))))(σ(N''(\\L k(\\G k))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("iae", true, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donni", false, 2, "do.ni", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n))(σ(N''(\\L n(\\G n))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("donnli", false, 2, "do.nli", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L n(\\G n))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L i(\\G i)))))))");
		checkSyllabification("dolnti", false, 2, "do.ti", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(\\O \\L n(\\G n))(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i)))))))");
	}

	@Test
	public void leftAdjoinRulesTest() {
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		npApproach = languageProject.getNPApproach();
		CVSegmentOrNaturalClass affectedSegOrNC = new CVSegmentOrNaturalClass("y", "", true, "5b00fb26-005d-4e73-a7c0-5ffac83a3a97", true);
		CVSegmentOrNaturalClass contextSegOrNC = new CVSegmentOrNaturalClass("V", "", false, "9821d4ba-8c34-432d-9afe-7c428dcd9afa", true);
		NPRule leftAdjoinRule = new NPRule("LeftAdjoinAtN", "", affectedSegOrNC, contextSegOrNC, NPRuleAction.LEFT_ADJOIN, NPRuleLevel.N, true, true, "");
		turnOffOnsetAndCodaRules();
		npApproach.getNPRules().add(leftAdjoinRule);
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("a", true, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("ya", true, 1, "ya", "(W(σ(N''(N'(N(N(\\L y(\\G y)))(N(\\L a(\\G a))))))))");
		checkSyllabification("ae", true, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aye", true, 2, "a.ye", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(N(\\L y(\\G y)))(N(\\L e(\\G e))))))))");
		checkSyllabification("yad", false, 1, "ya", "(W(σ(N''(N'(N(N(\\L y(\\G y)))(N(\\L a(\\G a)))))))(\\O \\L d(\\G d)))");
		checkSyllabification("ade", false, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ayed", false, 2, "a.ye", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(N(\\L y(\\G y)))(N(\\L e(\\G e)))))))(\\O \\L d(\\G d)))");
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ya", true, 1, "ya", "(W(σ(N''(N'(N(N(\\L y(\\G y)))(N(\\L a(\\G a))))))))");
		List<NPTracingStep> tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		NPTracingStep tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "y", true);
		checkSSPTracingStep(tracingStep, "y", "Glides", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.LEFT_ADJOINED_SEGMENT_TO_N_NODE, "y", true);

		npSyllabifier.setDoTrace(false);
		leftAdjoinRule.setRuleLevel(NPRuleLevel.N_BAR);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("a", true, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("ya", true, 1, "ya", "(W(σ(N''(N'(N'(\\L y(\\G y)))(N'(N(\\L a(\\G a))))))))");
		checkSyllabification("ae", true, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aye", true, 2, "a.ye", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N'(\\L y(\\G y)))(N'(N(\\L e(\\G e))))))))");
		checkSyllabification("yad", false, 1, "ya", "(W(σ(N''(N'(N'(\\L y(\\G y)))(N'(N(\\L a(\\G a)))))))(\\O \\L d(\\G d)))");
		checkSyllabification("ade", false, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ayed", false, 2, "a.ye", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N'(\\L y(\\G y)))(N'(N(\\L e(\\G e)))))))(\\O \\L d(\\G d)))");
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ya", true, 1, "ya", "(W(σ(N''(N'(N'(\\L y(\\G y)))(N'(N(\\L a(\\G a))))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "y", true);
		checkSSPTracingStep(tracingStep, "y", "Glides", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.LEFT_ADJOINED_SEGMENT_TO_N_BAR_NODE, "y", true);

		npSyllabifier.setDoTrace(false);
		leftAdjoinRule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("a", true, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("ya", true, 1, "ya", "(W(σ(N''(N''(\\L y(\\G y)))(N''(N'(N(\\L a(\\G a))))))))");
		checkSyllabification("ae", true, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aye", true, 2, "a.ye", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N''(\\L y(\\G y)))(N''(N'(N(\\L e(\\G e))))))))");
		checkSyllabification("yad", false, 1, "ya", "(W(σ(N''(N''(\\L y(\\G y)))(N''(N'(N(\\L a(\\G a)))))))(\\O \\L d(\\G d)))");
		checkSyllabification("ade", false, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("ayed", false, 2, "a.ye", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N''(\\L y(\\G y)))(N''(N'(N(\\L e(\\G e)))))))(\\O \\L d(\\G d)))");
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ya", true, 1, "ya", "(W(σ(N''(N''(\\L y(\\G y)))(N''(N'(N(\\L a(\\G a))))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "y", true);
		checkSSPTracingStep(tracingStep, "y", "Glides", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.LEFT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE, "y", true);
}

	protected void turnOffOnsetAndCodaRules() {
		// turn off onset and coda rules, both attach and augment ones
		npApproach.getNPRules().get(1).setActive(false);
		npApproach.getNPRules().get(2).setActive(false);
		npApproach.getNPRules().get(3).setActive(false);
		npApproach.getNPRules().get(4).setActive(false);
		npApproach.getNPRules().get(5).setActive(false);
	}

	@Test
	public void rightAdjoinRulesTest() {
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		npApproach = languageProject.getNPApproach();
		CVSegmentOrNaturalClass affectedSegOrNC = new CVSegmentOrNaturalClass("y", "", true, "5b00fb26-005d-4e73-a7c0-5ffac83a3a97", true);
		CVSegmentOrNaturalClass contextSegOrNC = new CVSegmentOrNaturalClass("V", "", false, "9821d4ba-8c34-432d-9afe-7c428dcd9afa", true);
		NPRule rightAdjoinRule = new NPRule("RightAdjoinAtN", "", affectedSegOrNC, contextSegOrNC, NPRuleAction.RIGHT_ADJOIN, NPRuleLevel.N, true, true, "");
		turnOffOnsetAndCodaRules();
		npApproach.getNPRules().add(rightAdjoinRule);
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("a", true, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("ay", true, 1, "ay", "(W(σ(N''(N'(N(N(\\L a(\\G a)))(N(\\L y(\\G y))))))))");
		checkSyllabification("ae", true, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aey", true, 2, "a.ey", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(N(\\L e(\\G e)))(N(\\L y(\\G y))))))))");
		checkSyllabification("ayd", false, 1, "ay", "(W(σ(N''(N'(N(N(\\L a(\\G a)))(N(\\L y(\\G y)))))))(\\O \\L d(\\G d)))");
		checkSyllabification("ade", false, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aeyd", false, 2, "a.ey", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(N(\\L e(\\G e)))(N(\\L y(\\G y)))))))(\\O \\L d(\\G d)))");
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ay", true, 1, "ay", "(W(σ(N''(N'(N(N(\\L a(\\G a)))(N(\\L y(\\G y))))))))");
		List<NPTracingStep> tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		NPTracingStep tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "y", "Glides", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.RIGHT_ADJOINED_SEGMENT_TO_N_NODE, "y", true);

		npSyllabifier.setDoTrace(false);
		rightAdjoinRule.setRuleLevel(NPRuleLevel.N_BAR);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("a", true, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("ay", true, 1, "ay", "(W(σ(N''(N'(N'(N(\\L a(\\G a))))(N'(\\L y(\\G y)))))))");
		checkSyllabification("ae", true, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aey", true, 2, "a.ey", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N'(N(\\L e(\\G e))))(N'(\\L y(\\G y)))))))");
		checkSyllabification("ayd", false, 1, "ay", "(W(σ(N''(N'(N'(N(\\L a(\\G a))))(N'(\\L y(\\G y))))))(\\O \\L d(\\G d)))");
		checkSyllabification("ade", false, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aeyd", false, 2, "a.ey", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N'(N(\\L e(\\G e))))(N'(\\L y(\\G y))))))(\\O \\L d(\\G d)))");
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ay", true, 1, "ay", "(W(σ(N''(N'(N'(N(\\L a(\\G a))))(N'(\\L y(\\G y)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "y", "Glides", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.RIGHT_ADJOINED_SEGMENT_TO_N_BAR_NODE, "y", true);

		npSyllabifier.setDoTrace(false);
		rightAdjoinRule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		checkSyllabification("", false, 0, "", "(W)");
		checkSyllabification("a", true, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("ay", true, 1, "ay", "(W(σ(N''(N''(N'(N(\\L a(\\G a)))))(N''(\\L y(\\G y))))))");
		checkSyllabification("ae", true, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aey", true, 2, "a.ey", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N''(N'(N(\\L e(\\G e)))))(N''(\\L y(\\G y))))))");
		checkSyllabification("ayd", false, 1, "ay", "(W(σ(N''(N''(N'(N(\\L a(\\G a)))))(N''(\\L y(\\G y)))))(\\O \\L d(\\G d)))");
		checkSyllabification("ade", false, 2, "a.e", "(W(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L d(\\G d))(σ(N''(N'(N(\\L e(\\G e)))))))");
		checkSyllabification("aeyd", false, 2, "a.ey", "(W(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N''(N'(N(\\L e(\\G e)))))(N''(\\L y(\\G y)))))(\\O \\L d(\\G d)))");
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("ay", true, 1, "ay", "(W(σ(N''(N''(N'(N(\\L a(\\G a)))))(N''(\\L y(\\G y))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "y", "Glides", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.RIGHT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE, "y", true);
	}

	@Test
	public void filterOnsetCodaRimeTest() {
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		turnOnTestingFilters();
		Optional<NPRule> codaAugmentRule = rules.stream()
				.filter(r -> r.getID().equals("b70cc8c6-13e8-4dc9-87ef-3a2f9cdcd7eb")).findFirst();
		assertTrue(codaAugmentRule.isPresent());
		CVSegmentOrNaturalClass soc = new CVSegmentOrNaturalClass("[C]", "", false, "b500749a-9617-444c-ae28-7552cb3236ec", true);
		codaAugmentRule.get().setAffectedSegOrNC(soc);
		npSyllabifier = new NPSyllabifier(npApproach);
		checkSyllabification("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tla", false, 1, "la", "(W(\\O \\L t(\\G t))(σ(N''(\\L l(\\G l))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("tam", true, 1, "tam", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L m(\\G m))))))");
		checkSyllabification("tamk", false, 1, "tam", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(\\O \\L k(\\G k)))");
		checkSyllabification("potla", false, 2, "po.la", "(W(σ(N''(\\L p(\\G p))(N'(N(\\L o(\\G o))))))(\\O \\L t(\\G t))(σ(N''(\\L l(\\G l))(N'(N(\\L a(\\G a)))))))");
		checkSyllabification("potam", true, 2, "po.tam", "(W(σ(N''(\\L p(\\G p))(N'(N(\\L o(\\G o))))))(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L m(\\G m))))))");
		checkSyllabification("potamk", false, 2, "po.tam", "(W(σ(N''(\\L p(\\G p))(N'(N(\\L o(\\G o))))))(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(\\O \\L k(\\G k)))");

		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tla", false, 1, "la", "(W(\\O \\L t(\\G t))(σ(N''(\\L l(\\G l))(N'(N(\\L a(\\G a)))))))");
		List<NPTracingStep> tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		NPTracingStep tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "l", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "l", true);
		checkSSPTracingStep(tracingStep, "l", "Liquids", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "l", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "l", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "l", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "l", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "l", "Liquids",  SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.FILTER_FAILED, "t", false);
		assertEquals("0b187b16-3dc2-414a-9c8a-76bbfccd8a47", tracingStep.getFilterUsed().getID());
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "l", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "l", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "l", false);

		checkSyllabifyWord("tamk", false, 1, "tam", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(\\O \\L k(\\G k)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "m", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "m", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "k", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "l", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "l", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "m", true);
		checkSSPTracingStep(tracingStep, "m", "Nasals", "k", "Obstruents", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.FILTER_FAILED, "k", false);
		assertEquals("c366bc4d-cb94-44f9-830d-49ae4404596c", tracingStep.getFilterUsed().getID());
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "k", false);

		Optional<NPRule> codaAttachRule = rules.stream()
				.filter(r -> r.getID().equals("7d8c3b88-7d72-40ac-a8f2-0f1df56aeef1")).findFirst();
		assertTrue(codaAttachRule.isPresent());
		codaAttachRule.get().setActive(false);
		Optional<NPRule> codaAttachFilterRule = npApproach.getNPRules().stream()
				.filter(r -> r.getID().equals("3714c6f9-26c7-4ffb-b127-ea34569b6e41")).findFirst();
		assertTrue(codaAttachFilterRule.isPresent());
		codaAttachFilterRule.get().setActive(true);
		npSyllabifier.setDoTrace(false);
		checkSyllabification("tañ", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L ɲ(\\G ñ)))");
		checkSyllabification("potañ", false, 2, "po.ta", "(W(σ(N''(\\L p(\\G p))(N'(N(\\L o(\\G o))))))(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L ɲ(\\G ñ)))");

		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tañ", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L ɲ(\\G ñ)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "t", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "a", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "ɲ", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.FILTER_FAILED, "", false);
		assertEquals("710653a7-e536-4815-ae69-5ecec206b19e", tracingStep.getFilterUsed().getID());
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "l", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "l", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "l", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "k", false);
		}

	protected void turnOnTestingFilters() {
		List<NPFilter> filters = npApproach.getNPFilters().stream().filter(filter -> filter.isValid())
				.collect(Collectors.toList());
		// turn on the three inactive fail filters we're testing with
		Optional<NPFilter> filter = filters.stream()
				.filter(r -> r.getID().equals("0b187b16-3dc2-414a-9c8a-76bbfccd8a47")).findFirst();
		assertTrue(filter.isPresent());
		filter.get().setActive(true);
		filter = filters.stream()
				.filter(r -> r.getID().equals("c366bc4d-cb94-44f9-830d-49ae4404596c")).findFirst();
		assertTrue(filter.isPresent());
		filter.get().setActive(true);
		filter = filters.stream()
				.filter(r -> r.getID().equals("710653a7-e536-4815-ae69-5ecec206b19e")).findFirst();
		assertTrue(filter.isPresent());
		filter.get().setActive(true);
	}

	@Test
	public void traceSyllabifyWordTest() {
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("", false, 0, "", "(W)");
		List<NPTracingStep> tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(0, tracingSteps.size());

		checkSyllabifyWord("A", true, 1, "A", "(W(σ(N''(N'(N(\\L a(\\G A)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		NPTracingStep tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("ta", true, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(11, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("tan", true, 1, "tan", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("Chiko", true, 2, "Chi.ko", "(W(σ(N''(\\L ch(\\G Ch))(N'(N(\\L i(\\G i))))))(σ(N''(\\L k(\\G k))(N'(N(\\L o(\\G o)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "i", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "ch", true);
		checkSSPTracingStep(tracingStep, "ch", "Obstruents", "i", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "ch", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "k", true);
		checkSSPTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "k", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a)))(\\L m(\\G m)))))(σ(N''(\\L p(\\G p))(N'(N(\\L i(\\G i))))))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(20, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "i", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "p", true);
		checkSSPTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "p", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "d", true);
		checkSSPTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "d", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "a", true);
		checkSSPTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "m", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "o", true);
		checkSSPTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(15);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(16);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(17);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(18);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(19);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("dapgek", false, 2, "da.ge", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L a(\\G a))))))(\\O \\L p(\\G p))(σ(N''(\\L g(\\G g))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "d", true);
		checkSSPTracingStep(tracingStep, "d", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "d", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "g", true);
		checkSSPTracingStep(tracingStep, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "g", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "p", false);
		checkSSPTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		checkSyllabifyWord("doldek", false, 2, "do.de", "(W(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o))))))(\\O \\L l(\\G l))(σ(N''(\\L d(\\G d))(N'(N(\\L e(\\G e))))))(\\O \\L k(\\G k)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "d", true);
		checkSSPTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "d", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "d", true);
		checkSSPTracingStep(tracingStep, "d", "Obstruents", "e", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "d", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "l", false);
		checkSSPTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		checkSyllabifyWord("dlofun", true, 2, "dlo.fun", "(W(σ(N''(\\L d(\\G d))(\\L l(\\G l))(N'(N(\\L o(\\G o))))))(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(16, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "u", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "l", true);
		checkSSPTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "l", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "f", true);
		checkSSPTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "f", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "u", true);
		checkSSPTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "d", true);
		checkSSPTracingStep(tracingStep, "d", "Obstruents", "l", "Liquids", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "d", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(15);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("funmo", true, 2, "fun.mo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L m(\\G m))(N'(N(\\L o(\\G o)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(15, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "u", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "f", true);
		checkSSPTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "f", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "m", true);
		checkSSPTracingStep(tracingStep, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "m", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "u", true);
		checkSSPTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("funmlo", true, 2, "fun.mlo", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(σ(N''(\\L m(\\G m))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(16, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "u", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "f", true);
		checkSSPTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "f", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "l", true);
		checkSSPTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "l", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "u", true);
		checkSSPTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "m", true);
		checkSSPTracingStep(tracingStep, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "m", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(15);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("funmdo", false, 2, "fun.do", "(W(σ(N''(\\L f(\\G f))(N'(N(\\L u(\\G u)))(\\L n(\\G n)))))(\\O \\L m(\\G m))(σ(N''(\\L d(\\G d))(N'(N(\\L o(\\G o)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(16, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "u", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "f", true);
		checkSSPTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "f", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "d", true);
		checkSSPTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "d", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "u", true);
		checkSSPTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "m", false);
		checkSSPTracingStep(tracingStep, "m", "Nasals", "d", "Obstruents", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(14);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "n", false);
		checkSSPTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL);
		tracingStep = tracingSteps.get(15);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		checkSyllabifyWord("flum", true, 1, "flum", "(W(σ(N''(\\L f(\\G f))(\\L l(\\G l))(N'(N(\\L u(\\G u)))(\\L m(\\G m))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "u", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "l", true);
		checkSSPTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "l", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "u", true);
		checkSSPTracingStep(tracingStep, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE, "m", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "f", true);
		checkSSPTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "f", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("iae", true, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(σ(N''(N'(N(\\L a(\\G a))))))(σ(N''(N'(N(\\L e(\\G e)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "i", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);

		checkSyllabifyWord("babe", false, 2, "a.e", "(W(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(13, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "b", false);
		checkSSPTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "b", false);
		checkSSPTracingStep(tracingStep, "b", null, "e", "Vowels", SHComparisonResult.MISSING1);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		checkSyllabifyWord("ibabe", false, 3, "i.a.e", "(W(σ(N''(N'(N(\\L i(\\G i))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L b(\\G b))(σ(N''(N'(N(\\L e(\\G e)))))))"); // b not in hierarchy
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "i", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "b", false);
		checkSSPTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "b", false);
		checkSSPTracingStep(tracingStep, "b", null, "e", "Vowels", SHComparisonResult.MISSING1);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);
		
		turnCodasAllowedOff();
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tan", false, 1, "ta", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("a", false, 1, "a", "(W(σ(N''(N'(N(\\L a(\\G a)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(7, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.ONSET_REQUIRED_IN_EVERY_SYLLABLE_BUT_SOME_SYLLABLE_DOES_NOT_HAVE_AN_ONSET, "", false);

		turnCodasAllowedOn();
//		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tano", true, 2, "ta.no", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L a(\\G a))))))(σ(N''(\\L n(\\G n))(N'(N(\\L o(\\G o)))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "n", true);
		checkSSPTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "n", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(12);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(13);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		
		// check for a segment that is not in any natural class
		// remove /a/ from vowels
		List<SHNaturalClass> natClasses = languageProject.getSHApproach().getActiveSHNaturalClasses();
		SHNaturalClass vowels = natClasses.get(0);
		vowels.getSegments().remove(0);
		checkSyllabifyWord("tan", false, 1, "a", "(W(\\O \\L t(\\G t))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(11, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "t", false);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "a", false);
		checkSSPTracingStep(tracingStep, "a", null, "n", "Nasals", SHComparisonResult.MISSING1);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		turnCodasAllowedOn();
		//turnOnseMaximizationOff();
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("etan", false, 2, "e.a", "(W(σ(N''(N'(N(\\L e(\\G e))))))(\\O \\L t(\\G t))(σ(N''(N'(N(\\L a(\\G a))))))(\\O \\L n(\\G n)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(12, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "a", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "t", false);
		checkSSPTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_FAILED, "a", false);
		checkSSPTracingStep(tracingStep, "a", null, "n", "Nasals", SHComparisonResult.MISSING1);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(11);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

		checkSyllabifyWord("eb", false, 1, "e", "(W(σ(N''(N'(N(\\L e(\\G e))))))(\\O \\L b(\\G b)))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(11, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(1);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "e", true);
		tracingStep = tracingSteps.get(2);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(3);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(4);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(5);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "b", true);
		tracingStep = tracingSteps.get(6);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(7);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(8);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
		tracingStep = tracingSteps.get(9);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
		tracingStep = tracingSteps.get(10);
		checkNPTracingStep(tracingStep, NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED, "", false);

	turnCodasAllowedOn();
	turnOnseMaximizationOn();
	languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
	npApproach = languageProject.getNPApproach();
	npSyllabifier = new NPSyllabifier(npApproach);
	npSyllabifier.setDoTrace(true);
	checkSyllabifyWord("tinlo", true, 2, "ti.nlo", "(W(σ(N''(\\L t(\\G t))(N'(N(\\L i(\\G i))))))(σ(N''(\\L n(\\G n))(\\L l(\\G l))(N'(N(\\L o(\\G o)))))))");
	tracingSteps = npSyllabifier.getTracingSteps();
	assertEquals(15, tracingSteps.size());
	tracingStep = tracingSteps.get(0);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
	tracingStep = tracingSteps.get(1);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "i", true);
	tracingStep = tracingSteps.get(2);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.BUILT_ALL_NODES, "o", true);
	tracingStep = tracingSteps.get(3);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
	tracingStep = tracingSteps.get(4);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "t", true);
	checkSSPTracingStep(tracingStep, "t", "Obstruents", "i", "Vowels", SHComparisonResult.LESS);
	tracingStep = tracingSteps.get(5);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "t", true);
	tracingStep = tracingSteps.get(6);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "l", true);
	checkSSPTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS);
	tracingStep = tracingSteps.get(7);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "l", true);
	tracingStep = tracingSteps.get(8);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
	tracingStep = tracingSteps.get(9);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.SSP_PASSED, "n", true);
	checkSSPTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS);
	tracingStep = tracingSteps.get(10);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE, "n", true);
	tracingStep = tracingSteps.get(11);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
	tracingStep = tracingSteps.get(12);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
	tracingStep = tracingSteps.get(13);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.APPLYING_RULE, "", true);
	tracingStep = tracingSteps.get(14);
	checkNPTracingStep(tracingStep, NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE, "", true);
	}

	private void checkNPTracingStep(NPTracingStep step, NPSyllabificationStatus status, String segment, boolean success) {
		assertEquals(status, step.getStatus());
		if (step.getSegment1() != null)
			assertEquals(segment, step.getSegment1().getSegment());
		assertEquals(success, step.isSuccessful());
	}
}
