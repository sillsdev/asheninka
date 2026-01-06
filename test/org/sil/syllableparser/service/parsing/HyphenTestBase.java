/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.service.parsing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;

import javafx.collections.ObservableList;

/**
 * 
 */
public class HyphenTestBase {

	HyphenApproach hyphenApproach;
	protected ObservableList<HyphenClass> hyphenClasses;
	protected CVSegmenter segmenter;
	protected ObservableList<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	HyphenClasser hyphenClasser;
	protected List<HyphenClassInWord> classesInWord = new ArrayList<HyphenClassInWord>();

	/**
	 * 
	 */
	public HyphenTestBase() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File("test/org/sil/syllableparser/testData/hyphen.ashedata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		hyphenApproach = languageProject.getHyphenApproach();
		segmentInventory = languageProject.getSegmentInventory();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		hyphenClasses = hyphenApproach.getHyphenClasses();
		hyphenClasser = new HyphenClasser(hyphenApproach);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
