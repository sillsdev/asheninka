// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import sil.org.syllableparser.model.cvapproach.CVTraceInfo;
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVNaturalClasserResult;
import sil.org.syllableparser.service.CVSegmenter;
import sil.org.syllableparser.service.CVSegmenterResult;
import sil.org.syllableparser.service.CVSyllabifier;
import sil.org.syllableparser.service.CVSyllabifierResult;
import sil.org.syllableparser.service.CVTryAWordHTMLFormatter;
import sil.org.utility.StringUtilities;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVTryAWordController implements Initializable {

	@FXML
	private TextField wordToTry;
	@FXML
	private Button tryItButton = new Button();
	@FXML
	WebView browser;
	@FXML
	WebEngine webEngine;

	Stage dialogStage;
	private MainApp mainApp;
	private ApplicationPreferences preferences;
	private String sWordToTry;
	private ResourceBundle bundle;
	private Locale locale;
	private CVApproach cva;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		tryItButton.setText(bundle.getString("label.tryit"));
		setTryItButtonDisable();
		// browser = new WebView();
		webEngine = browser.getEngine();

		wordToTry.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				setTryItButtonDisable();
			}
		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				wordToTry.requestFocus();
			}
		});
	}

	// Following getters and setters are for testing

	public TextField getWordToTry() {
		return wordToTry;
	}

	public void setWordToTry(TextField wordToTry) {
		this.wordToTry = wordToTry;
	}

	public Button getTryItButton() {
		return tryItButton;
	}

	public void setTryItButton(Button tryItButton) {
		this.tryItButton = tryItButton;
	}

	public void setTryItButtonDisable() {
		sWordToTry = wordToTry.getText();
		if (StringUtilities.isNullOrEmpty(sWordToTry)) {
			tryItButton.setDisable(true);
		} else {
			tryItButton.setDisable(false);
		}
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;

		this.dialogStage.setOnCloseRequest(event -> {
			handleClose();
		});
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		String lastWordTried = preferences.getLastCVTryAWordUsed();
		wordToTry.setText(lastWordTried);
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_CV_TRY_A_WORD, dialogStage, 533., 637.);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setData(CVApproach cvApproachData) {
		cva = cvApproachData;
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
				boolean fSuccess = segResult.success;
				if (fSuccess) {
					List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
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
					}
				}
				CVTryAWordHTMLFormatter formatter = new CVTryAWordHTMLFormatter(traceInfo, cva
						.getLanguageProject(), locale);
				String sResult = formatter.format();
				webEngine.loadContent(sResult);
			}
		});
		new Thread(sleeper).start();

		String sPleaseWaitMessage = Constants.PLEASE_WAIT_HTML_BEGINNING
				+ bundle.getString("label.pleasewait") + Constants.PLEASE_WAIT_HTML_MIDDLE
				+ bundle.getString("label.pleasewaittaw") + Constants.PLEASE_WAIT_HTML_ENDING;
		webEngine.loadContent(sPleaseWaitMessage);

	}

	/**
	 * Called when the user clicks Close.
	 */
	@FXML
	private void handleClose() {
		String lastWordTried = wordToTry.getText();
		mainApp.getApplicationPreferences().setLastCVTryAWordUsed(lastWordTried);
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_TRY_A_WORD, dialogStage);
		dialogStage.close();
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}
}
