// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class ONCSyllabifierTest extends ONCSyllabifierTestBase {

	@Test
	public void wordToSegmentToSyllableCodasOnMaxAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g)))(R(N(\\L e(\\G e)))(C(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 1, "dan", "onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fun", "onc", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		checkSyllabification("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", false, 0, "", "", "(W)");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g)))(R(N(\\L e(\\G e)))(C(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 1, "dan", "onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fun", "onc", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", false, 0, "", "", "(W)");
		checkSyllabification("ibabe", false, 0, "", "", "(W)"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g)))(R(N(\\L e(\\G e)))(C(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 1, "dan", "onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fun", "onc", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		checkSyllabification("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g)))(R(N(\\L e(\\G e)))(C(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 1, "dan", "onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fun", "onc", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		checkSyllabification("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", false, 0, "", "", "(W)");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g)))(R(N(\\L e(\\G e)))(C(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 1, "dan", "onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fun", "onc",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", false, 0, "", "", "(W)");
		checkSyllabification("ibabe", false, 0, "", "", "(W)"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "onc.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g)))(R(N(\\L e(\\G e)))(C(\\L m(\\G m))))))");
		checkSyllabification("dankgem", false, 1, "dan", "onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fun", "onc", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funi", true, 2, "fun.i", "onc.n",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		checkSyllabification("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", true, 2, "don.ni", "onc.on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donnli", true, 2, "don.nli", "onc.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dankgem", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dampidon", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		checkSyllabification("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", false, 0, "", "", "(W)");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dankgem", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dampidon", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", false, 0, "", "", "(W)");
		checkSyllabification("ibabe", false, 0, "", "", "(W)"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		checkSyllabification("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("tad", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dangem", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dankgem", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dampidon", false, 1, "da", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("dovdek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("fuhgt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fungt", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "oon.on",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("do", true, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("funglo", false, 1, "fu", "on", "(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("funi", true, 2, "fu.ni", "on.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))))(σ(O(\\L n(\\G n)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "oon.on",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))))(σ(O(\\L k(\\G k)))(R(N(\\L a(\\G a))))))");
		checkSyllabification("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		checkSyllabification("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "on.oon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "on.ooon",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(R(N(\\L i(\\G i))))))");
		checkSyllabification("donni", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		checkSyllabification("dolnti", false, 1, "do", "on", "(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
	}

	@Test
	public void traceSyllabifyWordTest() {
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("", false, 0, "", "", "(W)");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(0, tracingSteps.size());

		checkSyllabifyWord("A", true, 1, "A", "n", "(W(σ(R(N(\\L a(\\G A))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
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

		checkSyllabifyWord("ta", true, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("tan", true, 1, "tan", "onc", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))(C(\\L n(\\G n))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("Chiko", true, 2, "Chi.ko", "on.on",
				"(W(σ(O(\\L ch(\\G Ch)))(R(N(\\L i(\\G i)))))(σ(O(\\L k(\\G k)))(R(N(\\L o(\\G o))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "onc.on.onc",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))(C(\\L m(\\G m)))))(σ(O(\\L p(\\G p)))(R(N(\\L i(\\G i)))))(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))(C(\\L n(\\G n))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("dapgek", false, 1, "da", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L a(\\G a))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("doldek", false, 1, "do", "on",
				"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("dlofun", true, 2, "dlo.fun", "oon.onc",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(R(N(\\L o(\\G o)))))(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("funmo", true, 2, "fun.mo", "onc.on",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L m(\\G m)))(R(N(\\L o(\\G o))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("funmlo", true, 2, "fun.mlo", "onc.oon",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n)))))(σ(O(\\L m(\\G m))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("funmdo", false, 1, "fun", "onc",
				"(W(σ(O(\\L f(\\G f)))(R(N(\\L u(\\G u)))(C(\\L n(\\G n))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("flu", true, 1, "flu", "oon", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("flum", true, 1, "flum", "oonc",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(R(N(\\L u(\\G u)))(C(\\L m(\\G m))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("iae", true, 1, "iae", "nnn", "(W(σ(R(N(\\L i(\\G i))(\\L a(\\G a))(\\L e(\\G e))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("babe", false, 0, "", "", "(W)"); // b not in hierarchy
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("ibabe", false, 1, "i", "n", "(W(σ(R(N(\\L i(\\G i))))))"); // b not in hierarchy
		tracingSteps = oncSyllabifier.getTracingSteps();
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
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tan", false, 1, "ta", "on", "(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("a", false, 0, "", "", "(W)");
		tracingSteps = oncSyllabifier.getTracingSteps();
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
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tanlo", true, 2, "ta.nlo", "on.oon",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L o(\\G o))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tano", true, 2, "ta.no", "on.on",
				"(W(σ(O(\\L t(\\G t)))(R(N(\\L a(\\G a)))))(σ(O(\\L n(\\G n)))(R(N(\\L o(\\G o))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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
		checkSyllabifyWord("tan", false, 0, "", "", "(W)");
		tracingSteps = oncSyllabifier.getTracingSteps();
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
		oncApproach = languageProject.getONCApproach();
		oncSyllabifier = new ONCSyllabifier(oncApproach);
		oncSyllabifier.setDoTrace(true);
		checkSyllabifyWord("etan", false, 1, "e", "n", "(W(σ(R(N(\\L e(\\G e))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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

		checkSyllabifyWord("eb", false, 1, "e", "n", "(W(σ(R(N(\\L e(\\G e))))))");
		tracingSteps = oncSyllabifier.getTracingSteps();
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
