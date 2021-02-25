/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.service.LingTreeInteractor;
import org.sil.syllableparser.service.NPRuleValidator;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class NPRuleSVGTest {
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	LanguageProject languageProject;
	NPRuleValidator validator;
	NPRule rule;
	CVSegmentOrNaturalClass affected;
	CVSegmentOrNaturalClass context;
	String lingTreeDescription;
	String buildSVG;
	String adjustedSVG;
	protected LingTreeInteractor ltInteractor;

	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);

		validator = NPRuleValidator.getInstance();
		ltInteractor = LingTreeInteractor.getInstance();
		ltInteractor.initializeParameters(languageProject);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void ruleBuildAllTest() {
		affected = new CVSegmentOrNaturalClass("[V]", "", false, "", true);
		rule = new NPRule("build", "", affected, null, NPRuleAction.BUILD, NPRuleLevel.ALL, true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='24.6953125' height='135.6828125' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"0.099609375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"1.4794921875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"7.34765625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"2.859375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"0.0\" y=\"115.6828125\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"85.73359375\" x2=\"7.34765625\" y2=\"99.73359375\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = buildSVG;
		checkLingTreeAndSVG("(N''(N'(N(\\L [V]))))", buildSVG, adjustedSVG);
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");
	}

	@Test
	public void ruleOnsetTest() {
		affected = new CVSegmentOrNaturalClass("[C]", "", false, "", true);
		context = new CVSegmentOrNaturalClass("[V]", "", false, "", true);
		rule = new NPRule("attach", "", affected, context, NPRuleAction.ATTACH, NPRuleLevel.N_DOUBLE_BAR,
				true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.3671875' height='135.6828125' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.4296875\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.0\" y=\"115.6828125\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"7.3359375\" y2=\"99.73359375\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"36.1513671875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"42.01953125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"37.53125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"42.01953125\" y1=\"49.77265625\" x2=\"42.01953125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.671875\" y=\"115.6828125\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"42.01953125\" y1=\"85.73359375\" x2=\"42.01953125\" y2=\"99.73359375\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.3671875' height='135.6828125' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.4296875\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.0\" y=\"115.6828125\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"7.3359375\" y2=\"99.73359375\" stroke=\"#000000\" stroke-width=\"1.0\"" +
				Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"36.1513671875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"42.01953125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"37.53125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"42.01953125\" y1=\"49.77265625\" x2=\"42.01953125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.671875\" y=\"115.6828125\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"42.01953125\" y1=\"85.73359375\" x2=\"42.01953125\" y2=\"99.73359375\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N''(\\L [C])(N'(N(\\L [V]))))", buildSVG, adjustedSVG);
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");

		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		rule.setRuleAction(NPRuleAction.AUGMENT);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.3671875' height='63.7609375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.4296875\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"7.3359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.671875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"42.01953125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.3671875' height='63.7609375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.4296875\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"7.3359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" +
				Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"34.671875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"24.677734375\" y1=\"13.81171875\" x2=\"42.01953125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N''(\\L [C])(\\L [V]))", buildSVG, adjustedSVG);
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");
	}

	@Test
	public void ruleCodaTest() {
		affected = new CVSegmentOrNaturalClass("[C]", "", false, "", true);
		context = new CVSegmentOrNaturalClass("[V]", "", false, "", true);
		rule = new NPRule("attach", "", affected, context, NPRuleAction.ATTACH, NPRuleLevel.N_BAR,
				true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.3671875' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.8212890625\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<text x=\"2.859375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"24.689453125\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"24.689453125\" y1=\"13.81171875\" x2=\"42.03125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.3671875' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.8212890625\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<text x=\"2.859375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"24.689453125\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"24.689453125\" y1=\"13.81171875\" x2=\"42.03125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" +
				Constants.SVG_DASHED_LINE + "/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N'(N(\\L [V]))(\\L [C]))", buildSVG, adjustedSVG);
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");
	}

	@Test
	public void ruleDiphthongTest() {
		affected = new CVSegmentOrNaturalClass("i", "", true, "", true);
		context = new CVSegmentOrNaturalClass("[V]", "", false, "", true);
		rule = new NPRule("attach", "", affected, context, NPRuleAction.ATTACH, NPRuleLevel.N,
				true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='50.63671875' height='63.7609375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.77734375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"37.73046875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">i</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"39.18359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='50.63671875' height='63.7609375' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.77734375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"37.73046875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">i</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"39.18359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" +
				Constants.SVG_DASHED_LINE + "/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N(\\L [V])(\\L i))", buildSVG, adjustedSVG);
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");
	}

	@Test
	public void ruleLeftAdjoinTest() {
		affected = new CVSegmentOrNaturalClass("s", "", true, "", true);
		context = new CVSegmentOrNaturalClass("[C]", "", false, "", true);
		rule = new NPRule("left adjoin", "", affected, context, NPRuleAction.LEFT_ADJOIN, NPRuleLevel.N_DOUBLE_BAR, true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.16796875' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.2919921875\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.5400390625\" y1=\"13.81171875\" x2=\"7.248046875\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"4.7021484375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">s</text>\n" +
				"<line x1=\"7.248046875\" y1=\"49.77265625\" x2=\"7.248046875\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.583984375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.5400390625\" y1=\"13.81171875\" x2=\"41.83203125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.49609375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"41.83203125\" y1=\"49.77265625\" x2=\"41.83203125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.16796875' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.2919921875\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.5400390625\" y1=\"13.81171875\" x2=\"7.248046875\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"4.7021484375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">s</text>\n" +
				"<line x1=\"7.248046875\" y1=\"49.77265625\" x2=\"7.248046875\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"34.583984375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.5400390625\" y1=\"13.81171875\" x2=\"41.83203125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"34.49609375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"41.83203125\" y1=\"49.77265625\" x2=\"41.83203125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N''(N''(\\L s))(N''(\\L [C])))", buildSVG, adjustedSVG);
		
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='56.408203125' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"16.60205078125\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"22.47021484375\" y1=\"13.81171875\" x2=\"5.8681640625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"3.322265625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">s</text>\n" +
				"<line x1=\"5.8681640625\" y1=\"49.77265625\" x2=\"5.8681640625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"33.2041015625\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"22.47021484375\" y1=\"13.81171875\" x2=\"39.072265625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"31.736328125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"39.072265625\" y1=\"49.77265625\" x2=\"39.072265625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='56.408203125' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"16.60205078125\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"22.47021484375\" y1=\"13.81171875\" x2=\"5.8681640625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"3.322265625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">s</text>\n" +
				"<line x1=\"5.8681640625\" y1=\"49.77265625\" x2=\"5.8681640625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"33.2041015625\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"22.47021484375\" y1=\"13.81171875\" x2=\"39.072265625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"31.736328125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"39.072265625\" y1=\"49.77265625\" x2=\"39.072265625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N'(N'(\\L s))(N'(\\L [C])))", buildSVG, adjustedSVG);

		rule.setRuleLevel(NPRuleLevel.N);
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='53.6484375' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"15.912109375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"20.400390625\" y1=\"13.81171875\" x2=\"4.48828125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"1.9423828125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">s</text>\n" +
				"<line x1=\"4.48828125\" y1=\"49.77265625\" x2=\"4.48828125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"31.82421875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"20.400390625\" y1=\"13.81171875\" x2=\"36.3125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"28.9765625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"36.3125\" y1=\"49.77265625\" x2=\"36.3125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='53.6484375' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"15.912109375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<text x=\"0.0\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"20.400390625\" y1=\"13.81171875\" x2=\"4.48828125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"1.9423828125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">s</text>\n" +
				"<line x1=\"4.48828125\" y1=\"49.77265625\" x2=\"4.48828125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"31.82421875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"20.400390625\" y1=\"13.81171875\" x2=\"36.3125\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"28.9765625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[C]</text>\n" +
				"<line x1=\"36.3125\" y1=\"49.77265625\" x2=\"36.3125\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N(N(\\L s))(N(\\L [C])))", buildSVG, adjustedSVG);

		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");
	}

	@Test
	public void ruleRightAdjoinTest() {
		affected = new CVSegmentOrNaturalClass("i", "", true, "", true);
		context = new CVSegmentOrNaturalClass("[V]", "", false, "", true);
		rule = new NPRule("right adjoin", "", affected, context, NPRuleAction.RIGHT_ADJOIN, NPRuleLevel.N_DOUBLE_BAR, false, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.19140625' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.3974609375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.099609375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.6455078125\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.6455078125\" y1=\"13.81171875\" x2=\"41.943359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"37.98828125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">*i</text>\n" +
				"<line x1=\"41.943359375\" y1=\"49.77265625\" x2=\"41.943359375\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='59.19140625' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"17.3974609375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<text x=\"0.099609375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.6455078125\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N''</text>\n" +
				"<line x1=\"24.6455078125\" y1=\"13.81171875\" x2=\"41.943359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"37.98828125\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">*i</text>\n" +
				"<line x1=\"41.943359375\" y1=\"49.77265625\" x2=\"41.943359375\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N''(N''(\\L [V]))(N''(\\L *i)))", buildSVG, adjustedSVG);

		rule.setRuleLevel(NPRuleLevel.N_BAR);
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='56.431640625' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.08740234375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<text x=\"1.4794921875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"23.95556640625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"23.95556640625\" y1=\"13.81171875\" x2=\"40.5634765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"36.6083984375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">*i</text>\n" +
				"<line x1=\"40.5634765625\" y1=\"49.77265625\" x2=\"40.5634765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='56.431640625' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.08740234375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<text x=\"1.4794921875\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"23.95556640625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N'</text>\n" +
				"<line x1=\"23.95556640625\" y1=\"13.81171875\" x2=\"40.5634765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"36.6083984375\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">*i</text>\n" +
				"<line x1=\"40.5634765625\" y1=\"49.77265625\" x2=\"40.5634765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N'(N'(\\L [V]))(N'(\\L *i)))", buildSVG, adjustedSVG);

		rule.setRuleLevel(NPRuleLevel.N);
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		buildSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='53.671875' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.77734375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<text x=\"2.859375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"39.18359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"35.228515625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">*i</text>\n" +
				"<line x1=\"39.18359375\" y1=\"49.77265625\" x2=\"39.18359375\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"</svg>\n";
		adjustedSVG = "﻿<?xml version='1.0' standalone='no'?>\n" +
				"<svg width='53.671875' height='99.721875' version='1.1' xmlns='http://www.w3.org/2000/svg' contentScriptType='text/javascript'>\n" +
				"<script  id=\"clientEventHandlersJS\">\n" +
				"function OnClickLingTreeNode(node){}\n" +
				"</script>\n" +
				"<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n" +
				"<text x=\"18.77734375\" y=\"7.800000000000001\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<text x=\"2.859375\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"7.34765625\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"0.0\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">[V]</text>\n" +
				"<line x1=\"7.34765625\" y1=\"49.77265625\" x2=\"7.34765625\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"/>\n" +
				"<text x=\"34.6953125\" y=\"43.7609375\" font-family=\"System\" font-size=\"12.0\" fill=\"#000000\">N</text>\n" +
				"<line x1=\"23.265625\" y1=\"13.81171875\" x2=\"39.18359375\" y2=\"27.811718749999997\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"<text x=\"35.228515625\" y=\"79.721875\" font-family=\"System\" font-size=\"12.0\" fill=\"#008000\">*i</text>\n" +
				"<line x1=\"39.18359375\" y1=\"49.77265625\" x2=\"39.18359375\" y2=\"63.77265625\" stroke=\"#000000\" stroke-width=\"1.0\"" + Constants.SVG_DASHED_LINE + "/>\n" +
				"</svg>\n";
		checkLingTreeAndSVG("(N(N(\\L [V]))(N(\\L *i)))", buildSVG, adjustedSVG);

		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.setRule(rule);
		validator.validate();
		assertFalse(validator.isValid());
		checkLingTreeAndSVG("", "", "");
	}

	void checkLingTreeAndSVG(String ltDesc, String ltSVG, String adjustedSVG) {
		rule.setIsValid(validator.isValid());
		lingTreeDescription = rule.createLingTreeDescription();
		assertEquals(ltDesc, lingTreeDescription);
		String svg = ltInteractor.createSVG(lingTreeDescription, true);
		assertEquals(ltSVG, svg);
		svg = rule.adjustForAffectedSVG(svg);
		assertEquals(adjustedSVG, svg);
	}
}
