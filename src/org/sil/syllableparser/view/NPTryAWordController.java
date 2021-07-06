// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.List;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.model.npapproach.NPTraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.NPSegmenter;
import org.sil.syllableparser.service.parsing.NPSyllabifier;
import org.sil.syllableparser.service.parsing.NPSyllabifierResult;
import org.sil.syllableparser.service.parsing.NPTryAWordHTMLFormatter;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

/**
 * @author Andy Black
 *
 */
public class NPTryAWordController extends TryAWordController {

	private NPApproach npApproach;

	public void setData(NPApproach moraicApproachData) {
		npApproach = moraicApproachData;
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

				NPSegmenter segmenter;
				NPSyllabifier syllabifier;

				segmenter = new NPSegmenter(npApproach.getLanguageProject()
						.getActiveGraphemes(), npApproach.getLanguageProject()
						.getActiveGraphemeNaturalClasses());
				syllabifier = new NPSyllabifier(npApproach);
				NPTraceInfo traceInfo = new NPTraceInfo(sWordToTry, segmenter, syllabifier);

				CVSegmenterResult segResult = segmenter.segmentWord(sWordToTry);
				traceInfo.setSegmenterResult(segResult);
				String sLingTreeDescription = "";
				boolean fSuccess = segResult.success;
				if (fSuccess) {
					List<NPSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
						syllabifier.setDoTrace(true);
						traceInfo.setSyllabifier(syllabifier);
						fSuccess = syllabifier.syllabify(segmentsInWord);
						NPSyllabifierResult syllabifierResult = new NPSyllabifierResult();
						syllabifierResult.success = fSuccess;
						traceInfo.setSyllabifierResult(syllabifierResult);
						sLingTreeDescription = syllabifier.getLingTreeDescriptionOfCurrentWord();
				}
				NPTryAWordHTMLFormatter formatter = new NPTryAWordHTMLFormatter(traceInfo,
						npApproach.getLanguageProject(), locale);
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
		return ApplicationPreferences.LAST_NP_TRY_A_WORD;
	}

	@Override
	protected String getLastTryAWordUsed() {
		return preferences.getLastNPTryAWordUsed();
	}

	@Override
	protected void setLastTryAWordUsed(String lastWordTried) {
		mainApp.getApplicationPreferences().setLastNPTryAWordUsed(lastWordTried);
	}
}
