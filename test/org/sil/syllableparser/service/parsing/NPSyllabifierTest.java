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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Test;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
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
	public void traceSyllabifyWordTest() {
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("", false, 0, "", "(W)");
		List<NPTracingStep> tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(0, tracingSteps.size());

		checkSyllabifyWord("A", true, 1, "A", "(W(σ(R(N(\\L a(\\G A))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		NPTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("ta", true, 1, "ta", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("tan", true, 1, "tan", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("Chiko", true, 2, "Chi.ko", "(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "ch", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "i", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", "p", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.CODA_OR_ONSET, ONCSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(12);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(13);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("dapgek", false, 1, "da", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		checkSyllabifyWord("doldek", false, 1, "do", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		checkSyllabifyWord("dlofun", true, 2, "dlo.fun", "(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "f", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "o", "Vowels", "f", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmo", true, 2, "fun.mo", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L m(\\G m)))(R(N(\\L o(\\G o))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmlo", true, 2, "fun.mlo", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L m(\\G m))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmdo", false, 1, "fun", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "d", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND, false);

		checkSyllabifyWord("flu", true, 1, "flu", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "u", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "u", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("flum", true, 1, "flum", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))(C(\\L m(\\G m))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.CODA, ONCSyllabificationStatus.ADDED_AS_CODA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("iae", true, 1, "iae", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", "Vowels", "e", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "a", "Vowels", "e", "Vowels", SHComparisonResult.EQUAL,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "e", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("babe", false, 0, "", "(W)"); // b not in hierarchy
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "b", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED, false);

		checkSyllabifyWord("ibabe", false, 1, "i", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);
		
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tan", false, 1, "ta", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("a", false, 0, "", "(W)");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED, false);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tanlo", true, 2, "ta.nlo", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tano", true, 2, "ta.no", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))))(σ(O(\\L n(\\G n)))(R(N(\\L o(\\G o))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET_OR_NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
		
		// check for a segment that is not in any natural class
		// remove /a/ from vowels
		List<SHNaturalClass> natClasses = languageProject.getSHApproach().getActiveSHNaturalClasses();
		SHNaturalClass vowels = natClasses.get(0);
		vowels.getSegments().remove(0);
		checkSyllabifyWord("tan", false, 0, "", "(W)");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, null, null, null, null, null,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED, false);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		npApproach = languageProject.getNPApproach();
		npSyllabifier = new NPSyllabifier(npApproach);
		npSyllabifier.setDoTrace(true);
		checkSyllabifyWord("etan", false, 1, "e", "(W(σ(R(N(\\L e(\\G e))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "e", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "e", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "e", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);

		checkSyllabifyWord("eb", false, 1, "e", "(W(σ(R(N(\\L e(\\G e))))))");
		tracingSteps = npSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "e", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "e", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.NUCLEUS_TEMPLATE_MATCHED, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "e", "Vowels", "b", null, SHComparisonResult.MISSING2,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "b", null, null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS_OR_CODA, ONCSyllabificationStatus.EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE, false);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "b", null, null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.ONSET, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "b", null, null, null, SHComparisonResult.MORE,
				ONCSyllabifierState.NUCLEUS, ONCSyllabificationStatus.EXPECTED_NUCLEUS_NOT_FOUND, false);
	}
}
