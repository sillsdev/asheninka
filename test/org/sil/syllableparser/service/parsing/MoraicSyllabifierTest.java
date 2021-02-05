// Copyright (c) 2020-2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
import org.sil.syllableparser.model.moraicapproach.MoraicTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class MoraicSyllabifierTest extends MoraicSyllabifierTestBase {

	@Test
	public void wordToSegmentToSyllableCodasOnMaxAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", true, 1, "tad", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L d(\\G d)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dankgem", true, 2, "dank.gem", "σμμc.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))(\\L k(\\G k))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		checkSyllabification("dovdek", true, 2, "dov.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L v(\\G v))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		checkSyllabification("fuhgt", false, 1, "fuh", "σμμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L h(\\G h)))))");
		checkSyllabification("fungt", false, 1, "fung", "σμμc", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fug", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L g(\\G g)))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "σμμ.σσμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae",false, 1, "ia", "μμ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a)))))");
		checkSyllabification("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "σμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "σμ.σσσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", true, 2, "don.ni", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donnli", true, 2, "don.nli", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("dolnti", true, 2, "doln.ti", "σμμc.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L i(\\G i)))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", false, 0, "", "", "(W)");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", true, 1, "tad", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L d(\\G d)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dankgem", true, 2, "dank.gem", "σμμc.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))(\\L k(\\G k))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		checkSyllabification("dovdek", true, 2, "dov.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L v(\\G v))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		checkSyllabification("fuhgt", false, 1, "fuh", "σμμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L h(\\G h)))))");
		checkSyllabification("fungt", false, 1, "fung", "σμμc", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fug", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L g(\\G g)))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "σμμ.σσμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", false, 0, "", "", "(W)");
		checkSyllabification("ibabe", false, 0, "", "", "(W)"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "σμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "σμ.σσσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", true, 2, "don.ni", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donnli", true, 2, "don.nli", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("dolnti", true, 2, "doln.ti", "σμμc.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L i(\\G i)))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnMaxNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", true, 1, "tad", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L d(\\G d)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dankgem", true, 2, "dank.gem", "σμμc.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))(\\L k(\\G k))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		checkSyllabification("dovdek", true, 2, "dov.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L v(\\G v))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		checkSyllabification("fuhgt", false, 1, "fuh", "σμμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L h(\\G h)))))");
		checkSyllabification("fungt", false, 1, "fung", "σμμc", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fug", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L g(\\G g)))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", true, 2, "fun.glo", "σμμ.σσμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g))(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", true, 2, "ia.e", "μμ.μ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a))))(σ(μ(\\L e(\\G e)))))");
		checkSyllabification("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "σμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "σμ.σσσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", true, 2, "don.ni", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donnli", true, 2, "don.nli", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("dolnti", true, 2, "doln.ti", "σμμc.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L i(\\G i)))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");	
		checkSyllabification("tad", true, 1, "tad", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L d(\\G d)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dankgem", true, 2, "dank.gem", "σμμc.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))(\\L k(\\G k))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		checkSyllabification("dovdek", true, 2, "dov.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L v(\\G v))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		checkSyllabification("fuhgt", false, 1, "fuh", "σμμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L h(\\G h)))))");
		checkSyllabification("fungt", false, 1, "fung", "σμμc",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fug", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L g(\\G g)))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		checkSyllabification("do", true, 1, "do", "σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", true, 2, "fung.lo", "σμμc.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g))))(σ(O(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", false, 1, "ia", "μμ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a)))))");
		checkSyllabification("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", true, 2, "don.ni", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donnli", true, 2, "don.nli", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("dolnti", true, 2, "doln.ti", "σμμc.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L i(\\G i)))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", false, 0, "", "", "(W)");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", true, 1, "tad", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L d(\\G d)))))");
		languageProject.getSyllabificationParameters().setUseWeightByPosition(false);
		checkSyllabification("tad", true, 1, "tad", "σμc", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a))(\\L d(\\G d)))))");
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dankgem", true, 2, "dank.gem", "σμμc.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))(\\L k(\\G k))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		checkSyllabification("dovdek", true, 2, "dov.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L v(\\G v))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		checkSyllabification("fuhgt", false, 1, "fuh", "σμμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L h(\\G h)))))");
		checkSyllabification("fungt", false, 1, "fung", "σμμc",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fug", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L g(\\G g)))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		checkSyllabification("do", true, 1, "do", "σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", true, 2, "fung.lo", "σμμc.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g))))(σ(O(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", false, 0, "", "", "(W)");
		checkSyllabification("ibabe", false, 0, "", "", "(W)"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", true, 2, "don.ni", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donnli", true, 2, "don.nli", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("dolnti", true, 2, "doln.ti", "σμμc.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L i(\\G i)))))");
	}

	@Test
	public void wordToSegmentToSyllableCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", true, 1, "tad", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L d(\\G d)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", true, 2, "dan.gem", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dankgem", true, 2, "dank.gem", "σμμc.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n))(\\L k(\\G k))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L m(\\G m)))))");
		checkSyllabification("dampidon", true, 3, "dam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		checkSyllabification("dovdek", true, 2, "dov.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L v(\\G v))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		checkSyllabification("fuhgt", false, 1, "fuh", "σμμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L h(\\G h)))))");
		checkSyllabification("fungt", false, 1, "fung", "σμμc", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fug", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L g(\\G g)))))");
		checkSyllabification("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", true, 2, "fung.lo", "σμμc.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))(\\L g(\\G g))))(σ(O(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", true, 2, "ia.e", "μμ.μ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a))))(σ(μ(\\L e(\\G e)))))");
		checkSyllabification("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "don.li", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "don.lyi", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", true, 2, "don.ni", "σμμ.σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donnli", true, 2, "don.nli", "σμμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("dolnti", true, 2, "doln.ti", "σμμc.σμ", 
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))(\\L n(\\G n))))(σ(O(\\L t(\\G t)))(μ(\\L i(\\G i)))))");
		// now check for syllable template case of dyesyesyete
		Optional<Template> sylTemplate = languageProject.getActiveAndValidTemplates().stream()
				.filter(t -> t.getID().equals("f6679cc7-f616-468b-85a2-75276f0373ef")).findFirst();
		if (sylTemplate.isPresent()) {
			sylTemplate.get().setActive(true);
		}
		checkSyllabification("dyesyesyete", true, 4, "dyes.yes.ye.te", "σσμμ.σμμ.σμ.σμ", 
				"(W(σ(O(\\L d(\\G d))(\\L y(\\G y)))(μ(\\L e(\\G e)))(μ(\\L s(\\G s))))(σ(O(\\L y(\\G y)))(μ(\\L e(\\G e)))(μ(\\L s(\\G s))))(σ(O(\\L y(\\G y)))(μ(\\L e(\\G e))))(σ(O(\\L t(\\G t)))(μ(\\L e(\\G e)))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnAllButFirstTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", false, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dankgem", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dampidon", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dovdek", false, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("fuhgt", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fungt", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "σσμ.σμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "σσμ.σμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", false, 1, "ia", "μμ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a)))))");
		checkSyllabification("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "σμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "σμ.σσσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", false, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dolnti", false, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnEveryTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", false, 0, "", "", "(W)");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", false, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");

		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dankgem", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dampidon", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dovdek", false, 1, "do", "σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("fuhgt", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fungt", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "σσμ.σμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "σσμ.σμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", false, 0, "", "", "(W)");
		checkSyllabification("ibabe", false, 0, "", "", "(W)"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "σμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "σμ.σσσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", false, 1, "do", "σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dolnti", false, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
	}

	@Test
	public void wordToSegmentToSyllableNoCodasOnNoOnsetsRequiredTest() {
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(
				OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		languageProject.getSyllabificationParameters().setMaxMorasPerSyllable(2);
		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		checkSyllabification("", false, 0, "", "", "(W)");
		checkSyllabification("b", false, 0, "", "", "(W)");
		checkSyllabification("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		checkSyllabification("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("tad", false, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		checkSyllabification("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dangem", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dankgem", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dampidon", false, 1, "da", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))))");
		checkSyllabification("dovdek", false, 1, "do", "σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("fuhgt", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fungt", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("dlofugh", false, 2, "dlo.fu", "σσμ.σμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("dlofun", false, 2, "dlo.fu", "σσμ.σμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("do", true, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("funglo", false, 1, "fu", "σμ", "(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))))");
		checkSyllabification("funi", true, 2, "fu.ni", "σμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u))))(σ(O(\\L n(\\G n)))(μ(\\L i(\\G i)))))");
		checkSyllabification("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		checkSyllabification("fluka", true, 2, "flu.ka", "σσμ.σμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u))))(σ(O(\\L k(\\G k)))(μ(\\L a(\\G a)))))");
		checkSyllabification("iae", true, 2, "ia.e", "μμ.μ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a))))(σ(μ(\\L e(\\G e)))))");
		checkSyllabification("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		checkSyllabification("donli", true, 2, "do.nli", "σμ.σσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donlyi", true, 2, "do.nlyi", "σμ.σσσμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))))(σ(O(\\L n(\\G n))(\\L l(\\G l))(\\L y(\\G y)))(μ(\\L i(\\G i)))))");
		checkSyllabification("donni", false, 1, "do", "σμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
		checkSyllabification("dolnti", false, 1, "do", "σμ", "(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))))");
	}

	@Test
	public void traceSyllabifyWordTest() {
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("", false, 0, "", "", "(W)");
		List<MoraicTracingStep> tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(0, tracingSteps.size());

		checkSyllabifyWord("A", true, 1, "A", "μ", "(W(σ(μ(\\L a(\\G A)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		MoraicTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("ta", true, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(false);
		checkSyllabifyWord("tan", true, 1, "tan", "σμc", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a))(\\L n(\\G n)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabifyWord("tan", true, 1, "tan", "σμμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L n(\\G n)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("Chiko", true, 2, "Chi.ko", "σμ.σμ",
				"(W(σ(O(\\L ch(\\G Ch)))(μ(\\L i(\\G i))))(σ(O(\\L k(\\G k)))(μ(\\L o(\\G o)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "ch", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "i", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "k", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(false);
		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "σμc.σμ.σμc",
				"(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a))(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))(\\L n(\\G n)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", "p", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(12);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(13);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabifyWord("tampidon", true, 3, "tam.pi.don", "σμμ.σμ.σμμ",
				"(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))(μ(\\L m(\\G m))))(σ(O(\\L p(\\G p)))(μ(\\L i(\\G i))))(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L n(\\G n)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(14, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "m", "Nasals", "p", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "p", "Obstruents", "i", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "i", "Vowels", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(10);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(11);
		checkTracingStep(tracingStep, "o", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(12);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(13);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(false);
		checkSyllabifyWord("dapgek", true, 2, "dap.gek", "σμc.σμc",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a))(\\L p(\\G p))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e))(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "k", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabifyWord("dapgek", true, 2, "dap.gek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L a(\\G a)))(μ(\\L p(\\G p))))(σ(O(\\L g(\\G g)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "p", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "p", "Obstruents", "g", "Obstruents", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "g", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "k", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(false);
		checkSyllabifyWord("doldek", true, 2, "dol.dek", "σμc.σμc",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o))(\\L l(\\G l))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e))(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "d", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "d", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.APPENDED_TO_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "k", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabifyWord("doldek", true, 2, "dol.dek", "σμμ.σμμ",
				"(W(σ(O(\\L d(\\G d)))(μ(\\L o(\\G o)))(μ(\\L l(\\G l))))(σ(O(\\L d(\\G d)))(μ(\\L e(\\G e)))(μ(\\L k(\\G k)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "l", "Liquids", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "l", "Liquids", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "d", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "d", "Obstruents", "e", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "e", "Vowels", "k", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "k", "Obstruents", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "k", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setUseWeightByPosition(true);
		checkSyllabifyWord("dlofun", true, 2, "dlo.fun", "σσμ.σμμ",
				"(W(σ(O(\\L d(\\G d))(\\L l(\\G l)))(μ(\\L o(\\G o))))(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "d", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "f", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "o", "Vowels", "f", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "n", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmo", true, 2, "fun.mo", "σμμ.σμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))))(σ(O(\\L m(\\G m)))(μ(\\L o(\\G o)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmlo", true, 2, "fun.mlo", "σμμ.σσμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n))))(σ(O(\\L m(\\G m))(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(10, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(9);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("funmdo", false, 1, "fun", "σμμ",
				"(W(σ(O(\\L f(\\G f)))(μ(\\L u(\\G u)))(μ(\\L n(\\G n)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "m", "Nasals", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", "Nasals", "d", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);

		checkSyllabifyWord("flu", true, 1, "flu", "σσμ", "(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "u", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "u", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("flum", true, 1, "flum", "σσμμ",
				"(W(σ(O(\\L f(\\G f))(\\L l(\\G l)))(μ(\\L u(\\G u)))(μ(\\L m(\\G m)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(6, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "f", "Obstruents", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "l", "Liquids", "u", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "u", "Vowels", "m", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "m", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "m", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("iae", true, 2, "ia.e", "μμ.μ", "(W(σ(μ(\\L i(\\G i)))(μ(\\L a(\\G a))))(σ(μ(\\L e(\\G e)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(7, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "a", "Vowels", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "e", "Vowels", SHComparisonResult.EQUAL,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.MAXIMUM_MORAS_IN_SYLLABLE_FOUND_START_NEW_SYLLABLE, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "e", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "e", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		checkSyllabifyWord("babe", false, 0, "", "", "(W)"); // b not in hierarchy
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "b", null, null, null, null,
				MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED, false);

		checkSyllabifyWord("ibabe", false, 1, "i", "μ", "(W(σ(μ(\\L i(\\G i)))))"); // b not in hierarchy
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "i", "Vowels", "b", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "b", null, "a", "Vowels", SHComparisonResult.MISSING1,
				MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);
		
		languageProject.getSyllabificationParameters().setCodasAllowed(false);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tan", false, 1, "ta", "σμ", "(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(5, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);

		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("a", false, 0, "", "", "(W)");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "a", null, null, null, null,
				MoraicSyllabificationStatus.ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED, false);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(true);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tanlo", true, 2, "ta.nlo", "σμ.σσμ",
				"(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a))))(σ(O(\\L n(\\G n))(\\L l(\\G l)))(μ(\\L o(\\G o)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(9, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", "l", "Liquids", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "l", "Liquids", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(8);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("tano", true, 2, "ta.no", "σμ.σμ",
				"(W(σ(O(\\L t(\\G t)))(μ(\\L a(\\G a))))(σ(O(\\L n(\\G n)))(μ(\\L o(\\G o)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(8, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "a", "Vowels", "n", "Nasals", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(4);
		checkTracingStep(tracingStep, "n", "Nasals", "o", "Vowels", SHComparisonResult.LESS,
				MoraicSyllabificationStatus.ADDED_AS_ONSET, true);
		tracingStep = tracingSteps.get(5);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(6);
		checkTracingStep(tracingStep, "o", "Vowels", null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(7);
		checkTracingStep(tracingStep, "o", null, null, null, null,
				MoraicSyllabificationStatus.ADDING_FINAL_SYLLABLE_TO_WORD, true);
		
		// check for a segment that is not in any natural class
		// remove /a/ from vowels
		//List<SHNaturalClass>
		List<SHNaturalClass> natClasses = languageProject.getSHApproach().getActiveSHNaturalClasses();
		SHNaturalClass vowels = natClasses.get(0);
		vowels.getSegments().remove(0);
		checkSyllabifyWord("tan", false, 0, "", "", "(W)");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(3, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES, false);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, null, null, null, null, null,
				MoraicSyllabificationStatus.NO_WORD_INITIAL_TEMPLATE_MATCHED, false);

		languageProject.getSyllabificationParameters().setCodasAllowed(true);
		languageProject.getSyllabificationParameters().setOnsetMaximization(false);
		languageProject.getSyllabificationParameters().setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		moraicApproach = languageProject.getMoraicApproach();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		muSyllabifier.setDoTrace(true);
		checkSyllabifyWord("etan", false, 1, "e", "μ", "(W(σ(μ(\\L e(\\G e)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "e", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "e", "Vowels", "t", "Obstruents", SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "t", "Obstruents", "a", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT, false);

		checkSyllabifyWord("eb", false, 1, "e", "μ", "(W(σ(μ(\\L e(\\G e)))))");
		tracingSteps = muSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "e", "Vowels", "b", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET, false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "e", "Vowels", "b", null, SHComparisonResult.MISSING2,
				MoraicSyllabificationStatus.ADDED_AS_MORA, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "b", null, null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.ADDING_SYLLABLE_TO_WORD, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, "b", null, null, null, SHComparisonResult.MORE,
				MoraicSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET, false);
	}
}
