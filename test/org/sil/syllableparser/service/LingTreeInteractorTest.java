/**
 * Copyright (c) 2019-2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;
import javafx.scene.paint.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.lingtree.model.FontInfo;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class LingTreeInteractorTest {

	@Rule
	public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

	LanguageProject language;
	Language al;
	Language vl;
	FontInfo fia;
	FontInfo fiv;
	LingTreeInteractor ltInteractor;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		language = new LanguageProject();
		al = new Language("Times", 12.0, "Regular", null, null, null, "", null, null);
		vl = new Language("Charis SIL", 13.0, "Bold", null, null, null, "", null, null);
		language.setAnalysisLanguage(al);
		language.setVernacularLanguage(vl);
		fia = new FontInfo(al.getFont());
		fiv = new FontInfo(vl.getFont());
		fia.setColor(Color.BLUE);
		fiv.setColor(Color.AQUA);
		ltInteractor = LingTreeInteractor.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void svgTest() {
		ltInteractor.initializeParameters(language);
		String svg = ltInteractor.createSVG("", true);
		assertEquals("", svg);
		svg = ltInteractor.createSVG("", false);
		assertEquals("", svg);
		svg = ltInteractor.createSVG("(W)", true);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n"
						+ "<svg width='21.208984375' height='13.81171875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n"
						+ "<script  id=\"clientEventHandlersJS\">\n"
						+ "function OnClickLingTreeNode(node){}\n"
						+ "</script>\n"
						+ "<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n"
						+ "<text x=\"0.0\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n"
						+ "</svg>\n", svg);
		svg = ltInteractor.createSVG("(W)", false);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n"
						+ "<svg width='21.208984375' height='13.81171875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n"
						+ "<script  id=\"clientEventHandlersJS\">\n"
						+ "function OnClickLingTreeNode(node){}\n"
						+ "</script>\n"
						+ "<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n"
						+ "<text x=\"0.0\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n"
						+ "</svg>\n", svg);
		svg = ltInteractor
				.createSVG(
						"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))",
						true);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n" +
						"<svg width='176.1376953125' height='192.2287109375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
						"<script  id=\"clientEventHandlersJS\">\n" +
						"function OnClickLingTreeNode(node){}\n" +
						"</script>\n" +
						"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
						"<text x=\"70.22021484375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n" +
						"<text x=\"17.7607421875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">σ</text>\n" +
						"<line x1=\"75.82470703125\" y1=\"13.81171875\" x2=\"21.208984375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"1.0810546875\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">O</text>\n" +
						"<line x1=\"21.208984375\" y1=\"49.77265625\" x2=\"5.6044921875\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"1.646728515625\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">d</text>\n" +
						"<line x1=\"5.6044921875\" y1=\"85.73359375\" x2=\"5.6044921875\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"1.646728515625\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">d</text>\n" +
						"<text x=\"33.224609375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">R</text>\n" +
						"<line x1=\"21.208984375\" y1=\"49.77265625\" x2=\"36.8134765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"32.3251953125\" y=\"120.9865234375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
						"<line x1=\"36.8134765625\" y1=\"85.73359375\" x2=\"36.8134765625\" y2=\"105.0373046875\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"33.017578125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">o</text>\n" +
						"<line x1=\"36.8134765625\" y1=\"126.9982421875\" x2=\"36.8134765625\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"33.017578125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">o</text>\n" +
						"<text x=\"126.9921875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">σ</text>\n" +
						"<line x1=\"75.82470703125\" y1=\"13.81171875\" x2=\"130.4404296875\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"94.7080078125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">O</text>\n" +
						"<line x1=\"130.4404296875\" y1=\"49.77265625\" x2=\"99.2314453125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"63.9599609375\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<line x1=\"99.2314453125\" y1=\"85.73359375\" x2=\"68.0224609375\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"63.9599609375\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<text x=\"95.1689453125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<line x1=\"99.2314453125\" y1=\"85.73359375\" x2=\"99.2314453125\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"95.1689453125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<text x=\"128.27587890625\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">l</text>\n" +
						"<line x1=\"99.2314453125\" y1=\"85.73359375\" x2=\"130.4404296875\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"128.27587890625\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">l</text>\n" +
						"<text x=\"158.060546875\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">R</text>\n" +
						"<line x1=\"130.4404296875\" y1=\"49.77265625\" x2=\"161.6494140625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"157.1611328125\" y=\"120.9865234375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
						"<line x1=\"161.6494140625\" y1=\"85.73359375\" x2=\"161.6494140625\" y2=\"105.0373046875\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"159.48486328125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">i</text>\n" +
						"<line x1=\"161.6494140625\" y1=\"126.9982421875\" x2=\"161.6494140625\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"159.48486328125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">i</text>\n" +
						"</svg>\n"
, svg);
		svg = ltInteractor
				.createSVG(
						"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))",
						false);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n" +
						"<svg width='176.1376953125' height='192.2287109375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
						"<script  id=\"clientEventHandlersJS\">\n" +
						"function OnClickLingTreeNode(node){}\n" +
						"</script>\n" +
						"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
						"<text x=\"70.22021484375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n" +
						"<text x=\"17.7607421875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">σ</text>\n" +
						"<line x1=\"75.82470703125\" y1=\"13.81171875\" x2=\"21.208984375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"1.0810546875\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">O</text>\n" +
						"<line x1=\"21.208984375\" y1=\"49.77265625\" x2=\"5.6044921875\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"1.646728515625\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">d</text>\n" +
						"<line x1=\"5.6044921875\" y1=\"85.73359375\" x2=\"5.6044921875\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"1.646728515625\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">d</text>\n" +
						"<text x=\"33.224609375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">R</text>\n" +
						"<line x1=\"21.208984375\" y1=\"49.77265625\" x2=\"36.8134765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"32.3251953125\" y=\"120.9865234375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
						"<line x1=\"36.8134765625\" y1=\"85.73359375\" x2=\"36.8134765625\" y2=\"105.0373046875\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"33.017578125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">o</text>\n" +
						"<line x1=\"36.8134765625\" y1=\"126.9982421875\" x2=\"36.8134765625\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"33.017578125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">o</text>\n" +
						"<text x=\"126.9921875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">σ</text>\n" +
						"<line x1=\"75.82470703125\" y1=\"13.81171875\" x2=\"130.4404296875\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"94.7080078125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">O</text>\n" +
						"<line x1=\"130.4404296875\" y1=\"49.77265625\" x2=\"99.2314453125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"63.9599609375\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">n</text>\n" +
						"<line x1=\"99.2314453125\" y1=\"85.73359375\" x2=\"68.0224609375\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"63.9599609375\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">n</text>\n" +
						"<text x=\"95.1689453125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">n</text>\n" +
						"<line x1=\"99.2314453125\" y1=\"85.73359375\" x2=\"99.2314453125\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"95.1689453125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">n</text>\n" +
						"<text x=\"128.27587890625\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">l</text>\n" +
						"<line x1=\"99.2314453125\" y1=\"85.73359375\" x2=\"130.4404296875\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"128.27587890625\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">l</text>\n" +
						"<text x=\"158.060546875\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">R</text>\n" +
						"<line x1=\"130.4404296875\" y1=\"49.77265625\" x2=\"161.6494140625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"157.1611328125\" y=\"120.9865234375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
						"<line x1=\"161.6494140625\" y1=\"85.73359375\" x2=\"161.6494140625\" y2=\"105.0373046875\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"159.48486328125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">i</text>\n" +
						"<line x1=\"161.6494140625\" y1=\"126.9982421875\" x2=\"161.6494140625\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"159.48486328125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#ff0000\">i</text>\n" +
						"</svg>\n"
, svg);
	}

	@Test
	public void svgRTLTest() {
		language.getVernacularLanguage().setRightToLeft(true);
		ltInteractor.initializeParameters(language);
		String svg = ltInteractor.createSVG("", true);
		assertEquals("", svg);
		svg = ltInteractor.createSVG("", false);
		assertEquals("", svg);
		svg = ltInteractor.createSVG("(W)", true);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n"
						+ "<svg width='21.208984375' height='13.81171875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n"
						+ "<script  id=\"clientEventHandlersJS\">\n"
						+ "function OnClickLingTreeNode(node){}\n"
						+ "</script>\n"
						+ "<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n"
						+ "<text x=\"0.0\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n"
						+ "</svg>\n", svg);
		svg = ltInteractor.createSVG("(W)", false);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n"
						+ "<svg width='21.208984375' height='13.81171875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n"
						+ "<script  id=\"clientEventHandlersJS\">\n"
						+ "function OnClickLingTreeNode(node){}\n"
						+ "</script>\n"
						+ "<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n"
						+ "<text x=\"0.0\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n"
						+ "</svg>\n", svg);
		svg = ltInteractor
				.createSVG(
						"(W(σ(O(\\L d(\\G d)))(R(N(\\L o(\\G o)))))(σ(O(\\L n(\\G n))(\\L n(\\G n))(\\L l(\\G l)))(R(N(\\L i(\\G i))))))",
						true);
		assertEquals(
				"﻿<?xml version='1.0' standalone='no'?>\n" +
						"<svg width='176.1376953125' height='192.2287109375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
						"<script  id=\"clientEventHandlersJS\">\n" +
						"function OnClickLingTreeNode(node){}\n" +
						"</script>\n" +
						"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
						"<text x=\"84.70849609375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">W</text>\n" +
						"<text x=\"141.48046875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">σ</text>\n" +
						"<line x1=\"90.31298828125\" y1=\"13.81171875\" x2=\"144.9287109375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"156.009765625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">O</text>\n" +
						"<line x1=\"144.9287109375\" y1=\"49.77265625\" x2=\"160.533203125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"156.575439453125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">d</text>\n" +
						"<line x1=\"160.533203125\" y1=\"85.73359375\" x2=\"160.533203125\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"156.575439453125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">d</text>\n" +
						"<text x=\"125.7353515625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">R</text>\n" +
						"<line x1=\"144.9287109375\" y1=\"49.77265625\" x2=\"129.32421875\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"124.8359375\" y=\"120.9865234375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
						"<line x1=\"129.32421875\" y1=\"85.73359375\" x2=\"129.32421875\" y2=\"105.0373046875\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"125.5283203125\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">o</text>\n" +
						"<line x1=\"129.32421875\" y1=\"126.9982421875\" x2=\"129.32421875\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"125.5283203125\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">o</text>\n" +
						"<text x=\"32.2490234375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">σ</text>\n" +
						"<line x1=\"90.31298828125\" y1=\"13.81171875\" x2=\"35.697265625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"62.3828125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">O</text>\n" +
						"<line x1=\"35.697265625\" y1=\"49.77265625\" x2=\"66.90625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"94.052734375\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<line x1=\"66.90625\" y1=\"85.73359375\" x2=\"98.115234375\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"94.052734375\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<text x=\"62.84375\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<line x1=\"66.90625\" y1=\"85.73359375\" x2=\"66.90625\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"62.84375\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">n</text>\n" +
						"<text x=\"33.53271484375\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">l</text>\n" +
						"<line x1=\"66.90625\" y1=\"85.73359375\" x2=\"35.697265625\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"33.53271484375\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">l</text>\n" +
						"<text x=\"0.8994140625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">R</text>\n" +
						"<line x1=\"35.697265625\" y1=\"49.77265625\" x2=\"4.48828125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"0.0\" y=\"120.9865234375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
						"<line x1=\"4.48828125\" y1=\"85.73359375\" x2=\"4.48828125\" y2=\"105.0373046875\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"2.32373046875\" y=\"162.251171875\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">i</text>\n" +
						"<line x1=\"4.48828125\" y1=\"126.9982421875\" x2=\"4.48828125\" y2=\"143.6994140625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
						"<text x=\"2.32373046875\" y=\"183.5158203125\" font-family=\"Charis SIL\" font-size=\"13.0\" font-weight=\"bold\" fill=\"#008000\">i</text>\n" +
						"</svg>\n", svg);
	}

	@Test
	public void lexicalFontInfoTest() {
		ltInteractor.initializeParameters(language);
		checkFontInfo(fiv, ltInteractor.getLexicalFontInfo());
		Color clti = ltInteractor.getLexicalFontInfo().getColor();
		assertEquals(null, clti);
		ltInteractor.setLexicalFontInfo(fia);;
		checkFontInfo(fia, ltInteractor.getLexicalFontInfo());
		clti = ltInteractor.getLexicalFontInfo().getColor();
		assertEquals(Color.BLUE, clti);
	}

	protected void checkFontInfo(FontInfo fiNew, FontInfo fiOld) {
		assertEquals(fiOld.getFont(), fiNew.getFont());
		assertEquals(fiOld.getFontFamily(), fiNew.getFontFamily());
		assertEquals(fiOld.getFontSize(), fiNew.getFontSize(), 0.0);
		assertEquals(fiOld.getFontType(), fiNew.getFontType());
	}

	@Test
	public void verticalGapTest() {
		double gap = 20.0;
		assertEquals(gap, ltInteractor.getVerticalGap(), 0.0);
		gap = 40.0;
		ltInteractor.setVerticalGap(40.0);
		assertEquals(gap, ltInteractor.getVerticalGap(), 0.0);
	}

	@Test
	public void initialYCoordinateTest() {
		ltInteractor.setInitialYCoordinate(40.0);
		assertEquals(40.0, ltInteractor.getInitialYCoordinate(), 0.0);
	}
}
