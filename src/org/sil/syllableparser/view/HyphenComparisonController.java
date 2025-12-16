// Copyright (c) 2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.service.BackupFileRestorer;
import org.sil.syllableparser.service.comparison.ApproachLanguageComparisonHTMLFormatter;
import org.sil.syllableparser.service.comparison.HyphenApproachLanguageComparer;
import org.sil.syllableparser.service.comparison.HyphenApproachLanguageComparisonHTMLFormatter;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class HyphenComparisonController extends ComparisonController {

	private HyphenApproach currentCva;
	private HyphenApproach cva1;
	private HyphenApproach cva2;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(ApplicationPreferences.LAST_HYPHEN_COMPARISON, dialogStage, 533.0, 637.0);
	}

	public void setData(HyphenApproach hyphenApproachData) {
		currentCva = hyphenApproachData;
	}

	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	protected void handleCompare() {
		setHyphenApproachesToUse();
		setHyphenApproachDataSetInfoToUse();
		
		if (sDataSet1Info.equals(sDataSet2Info)) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(MainApp.kApplicationTitle);
			alert.setHeaderText(bundle.getString("label.sameimplementationsheader"));
			alert.setContentText(bundle.getString("label.sameimplementationscontent"));
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(mainApp.getNewMainIconImage());
			alert.showAndWait();
			return;
		}

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
				HyphenApproachLanguageComparer comparer = new HyphenApproachLanguageComparer(cva1, cva2);
				comparer.setDataSet1Info(sDataSet1Info);
				comparer.setDataSet2Info(sDataSet2Info);
				comparer.compare();
				ApproachLanguageComparisonHTMLFormatter formatter = new HyphenApproachLanguageComparisonHTMLFormatter(
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

		// okClicked = true;
		// dialogStage.close();
	}

	protected void setHyphenApproachesToUse() {
		if (currentImplementation.isSelected()) {
			cva1 = currentCva;
		} else {
			cva1 = getHyphenApproachFromBackup(backupDirectory1);
		}
		cva2 = getHyphenApproachFromBackup(backupDirectory2);
	}

	protected void setHyphenApproachDataSetInfoToUse() {
		try {
			String backupComment;
			if (currentImplementation.isSelected()) {
				this.sDataSet1Info = bundle.getString(sCurrentImplementationResourceLabel);
			} else {
				backupComment = getBackupComment(backupDirectory1);
				sDataSet1Info = backupDirectory1 + " (" + backupComment + ")";
			}
			backupComment = getBackupComment(backupDirectory2);
			sDataSet2Info = backupDirectory2 + " (" + backupComment + ")";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	private HyphenApproach getHyphenApproachFromBackup(String backupDirectory) {
		LanguageProject languageProject = new LanguageProject();
		File backupFile = new File(backupDirectory);
		BackupFileRestorer restorer = new BackupFileRestorer(backupFile);
		restorer.doRestore(languageProject, locale);
		return languageProject.getHyphenApproach();
	}
}
