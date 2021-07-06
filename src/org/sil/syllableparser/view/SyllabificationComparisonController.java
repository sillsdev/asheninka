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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class SyllabificationComparisonController implements Initializable {

	@FXML
	protected CheckBox useCVApproach;
	@FXML
	protected CheckBox useSHApproach;
	@FXML
	protected CheckBox useONCApproach;
	@FXML
	protected CheckBox useMoraicApproach;
	@FXML
	protected CheckBox useNuclearProjectionApproach;
	@FXML
	protected CheckBox useOTApproach;
	@FXML
	protected WebView browser;
	@FXML
	protected WebEngine webEngine;
	@FXML
	protected Button compareButton;
	
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
		//handleCompare();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(sWindowParams, dialogStage, 533.0, 637.0);
		useCVApproach.setSelected(preferences.getLastUseCVApproachValue());
		useSHApproach.setSelected(preferences.getLastUseSHApproachValue());
		useONCApproach.setSelected(preferences.getLastUseONCApproachValue());
		useMoraicApproach.setSelected(preferences.getLastUseMoraicApproachValue());
		useNuclearProjectionApproach.setSelected(preferences.getLastUseNuclearProjectionApproachValue());
		useOTApproach.setSelected(preferences.getLastUseOTApproachValue());
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
	
	public boolean isUseCVApproach() {
		return useCVApproach.isSelected();
	}

	public void setUseCVApproach(boolean value) {
		this.useCVApproach.setSelected(value);
	}

	public boolean isUseSHApproach() {
		return useSHApproach.isSelected();
	}

	public void setUseSHApproach(boolean value) {
		this.useSHApproach.setSelected(value);;
	}

	public boolean isUseONCApproach() {
		return useONCApproach.isSelected();
	}

	public void setUseONCApproach(boolean value) {
		this.useONCApproach.setSelected(value);
	}

	public boolean isUseMoraicApproach() {
		return useMoraicApproach.isSelected();
	}

	public void setUseMoraicApproach(boolean value) {
		this.useMoraicApproach.setSelected(value);
	}

	public boolean isUseNuclearProjectionApproach() {
		return useNuclearProjectionApproach.isSelected();
	}

	public void setUseNuclearProjectionApproach(boolean value) {
		this.useNuclearProjectionApproach.setSelected(value);
	}

	public boolean isUseOTApproach() {
		return useOTApproach.isSelected();
	}

	public void setUseOTApproach(boolean value) {
		this.useOTApproach.setSelected(value);
	}

	public boolean isUseCVApproachDisabled() {
		return useCVApproach.isDisabled();
	}

	public void setUseCVApproachDisabled(boolean value) {
		this.useCVApproach.setSelected(value);
	}

	public boolean isUseSHApproachDisabled() {
		return useSHApproach.isDisabled();
	}

	public void setUseSHApproachDisabled(boolean value) {
		this.useSHApproach.setSelected(value);;
	}

	public boolean isUseONCApproachDisabled() {
		return useONCApproach.isDisabled();
	}

	public void setUseONCApproachDisabled(boolean value) {
		this.useONCApproach.setSelected(value);
	}

	public boolean isUseMoraicApproachDisabled() {
		return useMoraicApproach.isDisabled();
	}

	public void setUseMoraicApproachDisabled(boolean value) {
		this.useMoraicApproach.setSelected(value);
	}

	public boolean isUseNuclearProjectionApproachDisabled() {
		return useNuclearProjectionApproach.isDisabled();
	}

	public void setUseNuclearProjectionApproachDisabled(boolean value) {
		this.useNuclearProjectionApproach.setSelected(value);
	}

	public boolean isUseOTApproachDisabled() {
		return useOTApproach.isDisabled();
	}

	public void setUseOTApproachDisabled(boolean value) {
		this.useOTApproach.setSelected(value);
	}

	public boolean twoOrMoreToCompare() {
		int i = 0;
		if (!useCVApproach.isDisabled() && useCVApproach.isSelected()) {
			i++;
		}
		if (!useSHApproach.isDisabled() && useSHApproach.isSelected()) {
			i++;
		}
		if (!useONCApproach.isDisabled() && useONCApproach.isSelected()) {
			i++;
		}
		if (!useMoraicApproach.isDisabled() && useMoraicApproach.isSelected()) {
			i++;
		}
		if (!useNuclearProjectionApproach.isDisabled() && useNuclearProjectionApproach.isSelected()) {
			i++;
		}
		if (!useOTApproach.isDisabled() && useOTApproach.isSelected()) {
			i++;
		}
		if (i > 1) {
			return true;
		}
		return false;
	}

	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	protected void handleCompare() {
		if (!twoOrMoreToCompare()) {
			reportTooFewApproachesToCompare();
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
				SyllabificationsComparer comparer = new SyllabificationsComparer(langProject);
				comparer.setUseCVApproach(useCVApproach.isSelected());
				comparer.setUseSHApproach(useSHApproach.isSelected());
				comparer.setUseONCApproach(useONCApproach.isSelected());
				comparer.setUseMoraicApproach(useMoraicApproach.isSelected());
				comparer.setUseNPApproach(useNuclearProjectionApproach.isSelected());
				comparer.setUseOTApproach(useOTApproach.isSelected());
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

	protected void reportTooFewApproachesToCompare() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(bundle.getString("label.comparesyllabificationserrorheader"));
		alert.setHeaderText(null);
		alert.setContentText(bundle.getString("label.comparesyllabificationserrorcontent"));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());
		alert.showAndWait();
	}
	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	protected void handleCancel() {
		preferences.setLastUseCVApproachValue(useCVApproach.isSelected());
		preferences.setLastUseSHApproachValue(useSHApproach.isSelected());
		preferences.setLastUseONCApproachValue(useONCApproach.isSelected());
		preferences.setLastUseMoraicApproachValue(useMoraicApproach.isSelected());
		preferences.setLastUseNuclearProjectionApproachValue(useNuclearProjectionApproach.isSelected());
		preferences.setLastUseOTApproachValue(useOTApproach.isSelected());
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
