// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.textfield.TextFields;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.*;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVApproachView;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.syllableparser.service.parsing.CVNaturalClasser;
import org.sil.syllableparser.service.parsing.CVNaturalClasserResult;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.CVSyllabifier;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private CVApproach cvApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentCVApproachController;

	public CVApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("cv.view.segmentinventory"),
				"handleCVSegmentInventory"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(bundle.getString("cv.view.syllablepatterns"),
				"handleCVSyllablePatterns"));
		views.add(new ApproachView(bundle.getString("cv.view.words"), "handleCVWords"));
		views.add(new ApproachView(bundle.getString("cv.view.wordspredictedvscorrect"),
				"handleCVWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("cv.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("cv.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentCVApproachController == null) {
			sView = prefs.getLastCVApproachViewUsed();
			return sView;
		}
		String sClass = currentCVApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			sView = CVApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			sView = CVApproachView.NATURAL_CLASSES.toString();
			break;

		case "org.sil.syllableparser.view.CVSyllablePatternsController":
			sView = CVApproachView.SYLLABLE_PATTERNS.toString();
			break;

		case "org.sil.syllableparser.view.CVWordsController":
			sView = CVApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.CVWordsPredictedVsCorrectController":
			sView = CVApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = CVApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = CVApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setCVApproachData(CVApproach cvApproach, ObservableList<Word> words) {
		this.cvApproachData = cvApproach;
		languageProject = cvApproach.getLanguageProject();
		this.words = words;
	}

	public void handleCVSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
		controller.setViewItemUsed(prefs.getLastCVSegmentInventoryViewItemUsed());
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentCVApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/CVNaturalClasses.fxml");
		CVNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
		int i = prefs.getLastCVNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	public void handleCVSyllablePatterns() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSyllablePatterns.fxml");
		CVSyllablePatternsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	public void handleCVWords() {
		handleCVWords(0, false);
	}

	public void handleCVWords(int index, boolean fResetIndex) {
		FXMLLoader loader = createFXMLLoader("fxml/CVWords.fxml");
		CVWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData, words);
		if (fResetIndex) {
			controller.setFocusOnWord(index);
		}
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	public void handleCVWordsPredictedVsCorrect() {
		FXMLLoader loader = createFXMLLoader("fxml/CVWordsPredictedVsCorrect.fxml");
		CVWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData, words);
		controller.setFocusOnWord(0);
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
		int i = prefs.getLastCVGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
		int i = prefs.getLastCVEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastCVApproachViewUsed(getViewUsed());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentCVApproachController.handleInsertNewItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentCVApproachController.handleRemoveItem();
	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		String sSuccess = bundle.getString("label.success");
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sNaturalClassFailure = bundle.getString("label.cvnaturalclassfailure");
		String sSyllabificationFailure = bundle.getString("label.cvsyllabificationfailure");
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				ObservableList<CVNaturalClass> naturalClasses;
				CVSegmenter segmenter;
				ObservableList<Segment> segmentInventory;
				// List<Segment> cvSegmentInventory;
				CVNaturalClasser naturalClasser;
				List<CVNaturalClass> cvNaturalClasses;
				List<CVSyllablePattern> patterns;
				CVSyllabifier syllabifier;
				List<CVSyllablePattern> cvPatterns;

				// segmentInventory = languageProject.getSegmentInventory();
				segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				// cvSegmentInventory = segmenter.getActiveSegmentInventory();
				naturalClasses = cvApproachData.getCVNaturalClasses();
				naturalClasser = new CVNaturalClasser(naturalClasses);
				cvNaturalClasses = naturalClasser.getActiveNaturalClasses();
				patterns = cvApproachData.getActiveCVSyllablePatterns();
				syllabifier = new CVSyllabifier(patterns, null);
				cvPatterns = syllabifier.getActiveCVPatterns();

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setCVParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setCVPredictedSyllabification("");
						continue;
					}
					List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					CVNaturalClasserResult ncResult = naturalClasser
							.convertSegmentsToNaturalClasses(segmentsInWord);
					fSuccess = ncResult.success;
					if (!fSuccess) {
						String sFailureMessage0 = sNaturalClassFailure.replace("{0}",
								ncResult.sClassesSoFar);
						String sFailureMessage1 = sFailureMessage0.replace("{1}",
								ncResult.sGraphemesSoFar);
						word.setCVParserResult(sFailureMessage1);
						word.setCVPredictedSyllabification("");
						continue;
					}
					List<List<CVNaturalClassInSyllable>> naturalClassesInWord = naturalClasser
							.getNaturalClassListsInCurrentWord();
					syllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
					fSuccess = syllabifier.convertNaturalClassesToSyllables();
					if (!fSuccess) {
						word.setCVParserResult(sSyllabificationFailure);
						word.setCVPredictedSyllabification("");
						continue;
					}
					word.setCVPredictedSyllabification(syllabifier
							.getSyllabificationOfCurrentWord());
					word.setCVParserResult(sSuccess);
				}
				ControllerUtilities.formatTimePassed(timeStart, "Syllabifying");
				scene.setCursor(currentCursor);
				// sleep for a second since it all happens so quickly
				Thread.sleep(1000);
				updateProgress(0, 0);
				done();
				return null;
			}
		};

		statusBar.textProperty().bind(task.messageProperty());
		statusBar.progressProperty().bind(task.progressProperty());

		// remove bindings again
		task.setOnSucceeded(event -> {
			statusBar.textProperty().unbind();
			statusBar.progressProperty().unbind();
			ControllerUtilities.setDateInStatusBar(statusBar, bundle);
		});

		Platform.runLater(task);

	}

	@Override
	void handleConvertPredictedToCorrectSyllabification() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/CVPredictedToCorrectSyllabificationChooser.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					MainApp.kApplicationTitle, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			CVPredictedToCorrectSyllabificationChooserController controller = loader
					.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(cvApproachData, words);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleCompareImplementations() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/CVComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			CVComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(cvApproachData);
			controller.setBackupDirectoryPath(backupDirectoryPath);

			dialogStage.initModality(Modality.NONE);
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	void handleFindWord() {
		try {
			// TextFields wordToFind
			String title = bundle.getString("program.name");
			String contentText = bundle.getString("label.wordtofind");
			TextInputDialog dialog = ControllerUtilities.getTextInputDialog(mainApp, title,
					contentText, bundle);

			ObservableList<String> listOfWords = FXCollections.observableArrayList();
			for (Word word : words) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				handleCVWords(index, true);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return cvApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return cvApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return cvApproachData.getHyphenatedWordsXLingPaper(words);
	}

	public ObservableList<Word> getWords() {
		return words;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCopy()
	 */
	@Override
	public void handleCopy() {
		currentCVApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentCVApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentCVApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentCVApproachController != null) {
			return currentCVApproachController.anythingSelected();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCopy()
	 */
	@Override
	public void handleToolBarCopy() {
		currentCVApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentCVApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentCVApproachController.handleToolBarCut();
	}

	public String getBackupDirectoryPath() {
		return backupDirectoryPath;
	}

	/**
	 * @param backupDirectoryPath
	 */
	public void setBackupDirectoryPath(String backupDirectoryPath) {
		this.backupDirectoryPath = backupDirectoryPath;

	}

	@Override
	void handleTryAWord() {
		try {
			Stage tryAWordDialogStage = new Stage();
			String resource = "fxml/CVTryAWord.fxml";
			String title = bundle.getString("label.tryaword");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			CVTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(cvApproachData);

			if (currentCVApproachController instanceof CVWordsController) {
				CVWordsController cvWordsController = (CVWordsController) currentCVApproachController;
				TableView<Word> cvWordsTable = cvWordsController.getCVWordsTable();
				Word cvWord = (Word) cvWordsTable.getSelectionModel().getSelectedItem();
				if (cvWord != null) {
					String sCurrentWord = cvWord.getWord();
					controller.getWordToTry().setText(sCurrentWord);
				}
			}

			tryAWordDialogStage.initModality(Modality.NONE);
			tryAWordDialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
