// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.List;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.syllableparser.model.cvapproach.CVTraceInfo;
import org.sil.syllableparser.service.parsing.CVNaturalClasser;
import org.sil.syllableparser.service.parsing.CVNaturalClasserResult;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.CVSyllabifier;
import org.sil.syllableparser.service.parsing.CVSyllabifierResult;
import org.sil.syllableparser.service.parsing.CVTryAWordHTMLFormatter;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

/**
 * @author Andy Black
 *
 */
public class CVTryAWordController extends TryAWordController {

	private CVApproach cva;

	public void setData(CVApproach cvApproachData) {
		cva = cvApproachData;
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

				ObservableList<CVNaturalClass> naturalClasses;
				CVSegmenter segmenter;
				// ObservableList<Segment> segmentInventory;
				CVNaturalClasser naturalClasser;
				List<CVSyllablePattern> patterns;
				CVSyllabifier syllabifier;
				List<CVSyllablePattern> cvPatterns;

				// segmentInventory =
				// cva.getLanguageProject().getSegmentInventory();
				segmenter = new CVSegmenter(cva.getLanguageProject().getActiveGraphemes(), cva
						.getLanguageProject().getActiveGraphemeNaturalClasses());
				naturalClasses = cva.getCVNaturalClasses();
				naturalClasser = new CVNaturalClasser(naturalClasses);
				patterns = cva.getActiveCVSyllablePatterns();
				syllabifier = new CVSyllabifier(patterns, null);
				cvPatterns = syllabifier.getActiveCVPatterns();
				CVTraceInfo traceInfo = new CVTraceInfo(sWordToTry, segmenter, naturalClasser,
						syllabifier);

				CVSegmenterResult segResult = segmenter.segmentWord(sWordToTry);
				traceInfo.setSegmenterResult(segResult);
				String sLingTreeDescription = "";
				boolean fSuccess = segResult.success;
				if (fSuccess) {
					List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					CVNaturalClasserResult ncResult = naturalClasser
							.convertSegmentsToNaturalClasses(segmentsInWord);
					traceInfo.setNaturalClasserResult(ncResult);
					fSuccess = ncResult.success;
					if (fSuccess) {
						List<List<CVNaturalClassInSyllable>> naturalClassesInWord = naturalClasser
								.getNaturalClassListsInCurrentWord();
						syllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
						syllabifier.setDoTrace(true);
						traceInfo.setSyllabifier(syllabifier);
						fSuccess = syllabifier.convertNaturalClassesToSyllables();
						CVSyllabifierResult syllabifierResult = new CVSyllabifierResult();
						syllabifierResult.success = fSuccess;
						traceInfo.setSyllabifierResult(syllabifierResult);
						sLingTreeDescription = syllabifier.getLingTreeDescriptionOfCurrentWord();
					}
				}
				CVTryAWordHTMLFormatter formatter = new CVTryAWordHTMLFormatter(traceInfo, cva
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
		return ApplicationPreferences.LAST_CV_TRY_A_WORD;
	}

	@Override
	protected String getLastTryAWordUsed() {
		return preferences.getLastCVTryAWordUsed();
	}

	@Override
	protected void setLastTryAWordUsed(String lastWordTried) {
		mainApp.getApplicationPreferences().setLastCVTryAWordUsed(lastWordTried);
	}
}
