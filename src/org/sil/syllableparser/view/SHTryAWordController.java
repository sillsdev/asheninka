// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.List;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.SHSyllabifier;
import org.sil.syllableparser.service.parsing.SHSyllabifierResult;
import org.sil.syllableparser.service.parsing.SHTryAWordHTMLFormatter;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

/**
 * @author Andy Black
 *
 */
public class SHTryAWordController extends TryAWordController {

	private SHApproach sonHierApproach;

	public void setData(SHApproach shApproachData) {
		sonHierApproach = shApproachData;
	}

	/**
	 * Called when the user clicks Try it.
	 */
	@FXML
	private void handleTryIt() {
		sWordToTry = wordToTry.getText();
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

				CVSegmenter segmenter;
				SHSyllabifier syllabifier;

				segmenter = new CVSegmenter(sonHierApproach.getLanguageProject()
						.getActiveGraphemes(), sonHierApproach.getLanguageProject()
						.getActiveGraphemeNaturalClasses());
				syllabifier = new SHSyllabifier(sonHierApproach);
				SHTraceInfo traceInfo = new SHTraceInfo(sWordToTry, segmenter, syllabifier);

				CVSegmenterResult segResult = segmenter.segmentWord(sWordToTry);
				traceInfo.setSegmenterResult(segResult);
				boolean fSuccess = segResult.success;
				if (fSuccess) {
					List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
						syllabifier.setDoTrace(true);
						traceInfo.setSyllabifier(syllabifier);
						fSuccess = syllabifier.syllabify(segmentsInWord);
						SHSyllabifierResult syllabifierResult = new SHSyllabifierResult();
						syllabifierResult.success = fSuccess;
						traceInfo.setSyllabifierResult(syllabifierResult);
				}
				SHTryAWordHTMLFormatter formatter = new SHTryAWordHTMLFormatter(traceInfo,
						sonHierApproach.getLanguageProject(), locale);
				String sResult = formatter.format();
				webEngine.loadContent(sResult);
			}
		});
		new Thread(sleeper).start();
		createAndShowPleaseWaitMessage();
	}

	@Override
	protected String getLastTryAWord() {
		return ApplicationPreferences.LAST_SH_TRY_A_WORD;
	}

	@Override
	protected String getLastTryAWordUsed() {
		return preferences.getLastSHTryAWordUsed();
	}

	@Override
	protected void setLastTryAWordUsed(String lastWordTried) {
		mainApp.getApplicationPreferences().setLastSHTryAWordUsed(lastWordTried);
	}
}
