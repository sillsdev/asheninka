// Copyright (c) 2019-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.List;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.ONCSegmenter;
import org.sil.syllableparser.service.parsing.ONCSyllabifier;
import org.sil.syllableparser.service.parsing.ONCSyllabifierResult;
import org.sil.syllableparser.service.parsing.ONCTryAWordHTMLFormatter;
import org.sil.syllableparser.service.parsing.TryAWordHTMLFormatter;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

/**
 * @author Andy Black
 *
 */
public class ONCTryAWordController extends TryAWordController {

	private ONCApproach oncApproach;

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
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

				ONCSegmenter segmenter;
				ONCSyllabifier syllabifier;

				segmenter = new ONCSegmenter(oncApproach.getLanguageProject()
						.getActiveGraphemes(), oncApproach.getLanguageProject()
						.getActiveGraphemeNaturalClasses());
				syllabifier = new ONCSyllabifier(oncApproach);
				ONCTraceInfo traceInfo = new ONCTraceInfo(sWordToTry, segmenter, syllabifier);

				CVSegmenterResult segResult = segmenter.segmentWord(sWordToTry);
				traceInfo.setSegmenterResult(segResult);
				String sLingTreeDescription = "";
				boolean fSuccess = segResult.success;
				if (fSuccess) {
					List<ONCSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
						syllabifier.setDoTrace(true);
						traceInfo.setSyllabifier(syllabifier);
						fSuccess = syllabifier.syllabify(segmentsInWord);
						ONCSyllabifierResult syllabifierResult = new ONCSyllabifierResult();
						syllabifierResult.success = fSuccess;
						traceInfo.setSyllabifierResult(syllabifierResult);
						sLingTreeDescription = syllabifier.getLingTreeDescriptionOfCurrentWord();
				}
				ONCTryAWordHTMLFormatter formatter = new ONCTryAWordHTMLFormatter(traceInfo,
						oncApproach.getLanguageProject(), locale);
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
		return ApplicationPreferences.LAST_ONC_TRY_A_WORD;
	}

	@Override
	protected String getLastTryAWordUsed() {
		return preferences.getLastONCTryAWordUsed();
	}

	@Override
	protected void setLastTryAWordUsed(String lastWordTried) {
		mainApp.getApplicationPreferences().setLastONCTryAWordUsed(lastWordTried);
	}
}
