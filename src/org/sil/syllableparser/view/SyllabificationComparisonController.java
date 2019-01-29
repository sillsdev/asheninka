// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.service.comparison.SyllabificationComparisonHTMLFormatter;
import org.sil.syllableparser.service.comparison.SyllabificationsComparer;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class SyllabificationComparisonController implements Initializable {

	@FXML
	protected WebView browser;
	@FXML
	protected WebEngine webEngine;

	protected Stage dialogStage;
	protected MainApp mainApp;
	protected ApplicationPreferences preferences;
	private LanguageProject langProject;
	String sWindowParams = ApplicationPreferences.LAST_SYLLABIFICATION_COMPARISON;
	protected ResourceBundle bundle;
	protected Locale locale;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		webEngine = browser.getEngine();
		handleCompare();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(sWindowParams, dialogStage, 533.0, 637.0);
	}

	public void setData(LanguageProject langProj) {
		langProject = langProj;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setOnCloseRequest(event -> {
			handleCancel();
		});
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	protected void handleCompare() {
		// sleeper code is from
		// http://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code
		// We do this so the "Please wait..." message loads and shows in the web
		// view
		Task<Void> sleeper = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				SyllabificationsComparer comparer = new SyllabificationsComparer(langProject);
				comparer.compareSyllabifications();
				SyllabificationComparisonHTMLFormatter formatter = new SyllabificationComparisonHTMLFormatter(
						comparer, locale, LocalDateTime.now());
				String result = formatter.format();
				webEngine.loadContent(result);
			}
		});
		new Thread(sleeper).start();

		String sPleaseWaitMessage = Constants.PLEASE_WAIT_HTML_BEGINNING
				+ bundle.getString("label.pleasewait") + Constants.PLEASE_WAIT_HTML_MIDDLE
				+ bundle.getString("label.pleasewaitcomparer")
				+ Constants.PLEASE_WAIT_HTML_ENDING;
		webEngine.loadContent(sPleaseWaitMessage);
	}
	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	protected void handleCancel() {
		preferences.setLastWindowParameters(sWindowParams, dialogStage);
		dialogStage.close();
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	protected void handleHelp() {
		mainApp.showNotImplementedYet();
	}
}
