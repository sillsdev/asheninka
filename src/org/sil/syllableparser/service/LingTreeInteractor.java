/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import javafx.scene.paint.Color;

import org.sil.lingtree.model.FontInfo;
import org.sil.lingtree.model.LingTreeTree;
import org.sil.lingtree.service.TreeBuilder;
import org.sil.lingtree.service.TreeDrawer;
import org.sil.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 *         Singleton pattern for handling LingTree interaction
 */
public class LingTreeInteractor {

	private static LingTreeInteractor instance;

	private LingTreeTree origTree;
	private FontInfo fiAnalysis;
	private FontInfo fiVernacular;

	private LingTreeInteractor() {
		origTree = new LingTreeTree();
	}

	public static LingTreeInteractor getInstance() {
		if (instance == null) {
			instance = new LingTreeInteractor();
		}
		return instance;
	}

	public void initializeParameters(LanguageProject language) {
		fiAnalysis = new FontInfo(language.getAnalysisLanguage().getFont());
		fiAnalysis.setColor(Color.BLACK);
		origTree.setNonTerminalFontInfo(fiAnalysis);
		origTree.setEmptyElementFontInfo(fiAnalysis);
		fiVernacular = new FontInfo(language.getVernacularLanguage().getFont());
		origTree.setLexicalFontInfo(fiVernacular);
		origTree.setGlossFontInfo(fiVernacular);
		origTree.setShowFlatView(true);
		origTree.setLineWidth(1.0);
		origTree.setInitialXCoordinate(0.0);
		double dinit = fiAnalysis.getFontSize() *.65;// attempt to keep the top from getting too far down
		origTree.setInitialYCoordinate(dinit);
		origTree.setHorizontalGap(20.0);
		origTree.setVerticalGap(20.0);
		origTree.setLexGlossGapAdjustment(0.0);
		origTree.setUseRightToLeftOrientation(language.getVernacularLanguage().isRightToLeft());
	}

	public String createSVG(String lingTreeDescription, boolean fSuccess) {
		String result = "";
		if (lingTreeDescription.length() > 0) {
			if (fSuccess)
				fiVernacular.setColor(Color.GREEN);
			else
				fiVernacular.setColor(Color.RED);
			origTree.setFontsAndColors();
			LingTreeTree ltTree = TreeBuilder.parseAString(lingTreeDescription, origTree);
			TreeDrawer drawer = new TreeDrawer(ltTree);
			StringBuilder sb = drawer.drawAsSVG();
			result = sb.toString();
		}
		return result;
	}

	public FontInfo getLexicalFontInfo() {
		return origTree.getLexicalFontInfo();
	}

	public void setLexicalFontInfo(FontInfo fi) {
		origTree.setLexicalFontInfo(fi);
	}

	public double getVerticalGap() {
		return origTree.getVerticalGap();
	}

	public void setVerticalGap(double gap) {
		origTree.setVerticalGap(gap);
	}
}
