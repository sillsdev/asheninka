// Copyright (c) 2025-2026 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.List;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;
import org.sil.syllableparser.model.hyphenapproach.HyphenTraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleProcessor;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleResult;
import org.sil.syllableparser.service.parsing.HyphenClasser;
import org.sil.syllableparser.service.parsing.HyphenClasserResult;
import org.sil.syllableparser.service.parsing.HyphenTryAWordHTMLFormatter;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

/**
 * @author Andy Black
 *
 */
public class HyphenTryAWordController extends TryAWordController {

	private HyphenApproach hyphena;

	public void setData(HyphenApproach hyphenApproachData) {
		hyphena = hyphenApproachData;
	}

	/**
	 * Called when the user clicks Try it.
	 */
	@FXML
	private void handleTryIt() {
		sWordToTry = setWordAsString();
		if (sWordToTry.length() == 0) {
			return; // just in case...
		}
		// sleeper code is from
		// http://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code
		// We do this so the "Please wait..." message loads and shows in the web
		// view
		Task<Void> sleeper = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(1); // probably not needed, but we do it anyway
										// in case its needed on slower
										// machines...
				} catch (InterruptedException e) {
				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				CVSegmenter segmenter = new CVSegmenter(hyphena.getLanguageProject().getActiveGraphemes(), hyphena
						.getLanguageProject().getActiveGraphemeNaturalClasses());
				HyphenClasser hyphenClasser = new HyphenClasser(hyphena);
				HyphenChangeRuleProcessor hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphena);

				HyphenTraceInfo traceInfo = new HyphenTraceInfo(sWordToTry, segmenter, hyphenClasser,
						hyphenRuleProcessor);
				CVSegmenterResult segResult = segmenter.segmentWord(sWordToTry);
				traceInfo.setSegmenterResult(segResult);
				String sLingTreeDescription = "";
				boolean fSuccess = segResult.success;
				if (fSuccess) {
					List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					HyphenClasserResult hcResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
					traceInfo.setHyphenClasserResult(hcResult);
					fSuccess = hcResult.success;
					if (fSuccess) {
						List<HyphenClassInWord> classesInWord = hyphenClasser.getClassesInWord();
						hyphenRuleProcessor.setDoTrace(true);
						HyphenChangeRuleResult crResult = hyphenRuleProcessor.applyChangeRules(classesInWord);
						traceInfo.setStates(hyphenRuleProcessor.getTraceInfo().getStates());
						traceInfo.setHyphenChangeRuleResult(crResult);
						fSuccess = crResult.success;
					}
				}
				HyphenTryAWordHTMLFormatter formatter = new HyphenTryAWordHTMLFormatter(traceInfo, hyphena
						.getLanguageProject(), locale);
				formatter.setLingTreeDescription(sLingTreeDescription);
				String sResult = formatter.format();
				webEngine.loadContent(sResult);
			}
		});
		new Thread(sleeper).start();
		createAndShowPleaseWaitMessage();
	}

	@Override
	protected String getLastTryAWord() {
		return ApplicationPreferences.LAST_HYPHEN_TRY_A_WORD_USED;
	}

	@Override
	protected String getLastTryAWordUsed() {
		return preferences.getLastHyphenTryAWordUsed();
	}

	@Override
	protected void setLastTryAWordUsed(String lastWordTried) {
		mainApp.getApplicationPreferences().setLastHyphenTryAWordUsed(lastWordTried);
	}
}
