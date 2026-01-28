// Copyright (c) 2025-2026 SIL International
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
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproachView;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleProcessor;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleResult;
import org.sil.syllableparser.service.parsing.HyphenClasser;
import org.sil.syllableparser.service.parsing.HyphenClasserResult;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class HyphenApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private HyphenApproach hyphenApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentHyphenApproachController;

	public HyphenApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("cv.view.segmentinventory"),
				"handleHyphenSegmentInventory"));
		views.add(new ApproachView(bundle.getString("hyphen.view.hyphenclass"),
				"handleHyphenClasses"));
		views.add(new ApproachView(bundle.getString("hyphen.view.hyphenchangerules"),
				"handleHyphenChangeRules"));
		views.add(new ApproachView(bundle.getString("hyphen.view.words"), "handleHyphenWords"));
		views.add(new ApproachView(bundle.getString("hyphen.view.wordspredictedvscorrect"),
				"handleHyphenWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("cv.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("cv.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentHyphenApproachController == null) {
			sView = prefs.getLastHyphenApproachViewUsed();
			return sView;
		}
		String sClass = currentHyphenApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			sView = HyphenApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.HyphenClassController":
			sView = HyphenApproachView.HYPHEN_CLASSES.toString();
			break;

		case "org.sil.syllableparser.view.HyphenChangeRulesController":
			sView = HyphenApproachView.HYPHEN_CHANGE_RULES.toString();
			break;

		case "org.sil.syllableparser.view.HyphenWordsController":
			sView = HyphenApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.HyphenWordsPredictedVsCorrectController":
			sView = HyphenApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = HyphenApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = HyphenApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setHyphenApproachData(HyphenApproach hyphenApproach, ObservableList<Word> words) {
		this.hyphenApproachData = hyphenApproach;
		languageProject = hyphenApproach.getLanguageProject();
		this.words = words;
	}

	public void handleHyphenSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.setViewItemUsed(prefs.getLastHyphenSegmentInventoryViewItemUsed());
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentHyphenApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleHyphenClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/HyphenClasses.fxml");
		HyphenClassController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastHyphenClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleHyphenChangeRules() {
		FXMLLoader loader = createFXMLLoader("fxml/HyphenChangeRules.fxml");
		HyphenChangeRulesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleHyphenWords() {
		handleHyphenWords(0, false, false);
	}

	public void handleHyphenWords(int index, boolean fResetIndex, boolean fCheckFilters) {
		FXMLLoader loader = createFXMLLoader("fxml/HyphenWords.fxml");
		HyphenWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData, words);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.applyWordFilters();
		mainApp.updateStatusBarNumberOfItems("");
		if (fResetIndex) {
			controller.setFocusOnWord(index, fCheckFilters);
		}
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false);
	}

	public void handleHyphenWordsPredictedVsCorrect(int index) {
		FXMLLoader loader = createFXMLLoader("fxml/HyphenWordsPredictedVsCorrect.fxml");
		HyphenWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData, words);
		controller.initWordsFilter();
		controller.applyWordFilter();
		controller.setFocusOnWord(index);
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false, true);
	}

	public void handleHyphenWordsPredictedVsCorrect() {
		handleHyphenWordsPredictedVsCorrect(0);
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastHyphenGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(hyphenApproachData);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastHyphenEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastHyphenApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentHyphenApproachController.handleInsertNewItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentHyphenApproachController.handleRemoveItem();
	}

	@Override
	void handlePreviousItem() {
		currentHyphenApproachController.handlePreviousItem();
	}

	@Override
	void handleNextItem() {
		currentHyphenApproachController.handleNextItem();
	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		String sSuccess = bundle.getString("label.success");
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sHyphenClassFailure = bundle.getString("label.hyphenclassfailure");
		String sProcessFailure = bundle.getString("label.hyphenprocessfailure");
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				CVSegmenter segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				HyphenClasser hyphenClasser = new HyphenClasser(hyphenApproachData);
				HyphenChangeRuleProcessor hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphenApproachData);

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.processing") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setHyphenParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setHyphenPredictedSyllabification("");
						continue;
					}
					List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					HyphenClasserResult ncResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
					fSuccess = ncResult.success;
					if (!fSuccess) {
						String sFailureMessage0 = sHyphenClassFailure.replace("{0}",
								ncResult.sClassesSoFar);
						String sFailureMessage1 = sFailureMessage0.replace("{1}",
								ncResult.sGraphemesSoFar);
						word.setHyphenParserResult(sFailureMessage1);
						word.setHyphenPredictedSyllabification("");
						continue;
					}
					List<HyphenClassInWord> classesInWord = hyphenClasser.getClassesInWord();
					HyphenChangeRuleResult crResult = hyphenRuleProcessor.applyChangeRules(classesInWord);
					fSuccess = crResult.success;
					if (!fSuccess) {
						word.setHyphenParserResult(sProcessFailure);
						word.setHyphenPredictedSyllabification("");
						continue;
					}
					word.setHyphenPredictedSyllabification(hyphenRuleProcessor.getSyllabificationOfCurrentWord(classesInWord));
					word.setHyphenParserResult(sSuccess);
				}
				ControllerUtilities.formatTimePassed(timeStart, "Processing");
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
			if (currentHyphenApproachController instanceof HyphenWordsController) {
				HyphenWordsController hyphenController = (HyphenWordsController) currentHyphenApproachController;
				hyphenController.updateStatusBarWords(hyphenController.getPredictedWords(),
						hyphenController.getPredictedEqualsCorrectWords());
			}
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
					bundle);

			CVPredictedToCorrectSyllabificationChooserController controller = loader
					.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(hyphenApproachData, words);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	public void handleCompareImplementations() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/HyphenComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), bundle);

			HyphenComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(hyphenApproachData);
			controller.setBackupDirectoryPath(backupDirectoryPath);

			dialogStage.initModality(Modality.NONE);
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
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
			ObservableList<Word> wordsToUse = words;
			if (currentHyphenApproachController instanceof HyphenWordsPredictedVsCorrectController) {
				HyphenWordsPredictedVsCorrectController predController = (HyphenWordsPredictedVsCorrectController) currentHyphenApproachController;
				wordsToUse = predController.getHyphenWordsPredictedVsCorrectTable().getItems();
			}
			for (Word word : wordsToUse) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				if (currentHyphenApproachController instanceof HyphenWordsPredictedVsCorrectController) {
					handleHyphenWordsPredictedVsCorrect(index);
				} else {
					handleHyphenWords(index, true, true);
					rootController.selectApproachViewItem(Constants.HYPHEN_WORDS_VIEW_INDEX);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return hyphenApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return hyphenApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return hyphenApproachData.getHyphenatedWordsXLingPaper(words);
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
		currentHyphenApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentHyphenApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentHyphenApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentHyphenApproachController != null) {
			return currentHyphenApproachController.anythingSelected();
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
		currentHyphenApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentHyphenApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentHyphenApproachController.handleToolBarCut();
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
			String resource = "fxml/HyphenTryAWord.fxml";
			String title = bundle.getString("label.tryaword") + " - " + bundle.getString("approach.hyphen");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					bundle);

			HyphenTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(hyphenApproachData);

			if (currentHyphenApproachController instanceof HyphenWordsController) {
				HyphenWordsController hyphenWordsController = (HyphenWordsController) currentHyphenApproachController;
				setWordForTryAWord(controller, hyphenWordsController.getHyphenWordsTable());
			} else if (currentHyphenApproachController instanceof HyphenWordsPredictedVsCorrectController) {
				HyphenWordsPredictedVsCorrectController predController = (HyphenWordsPredictedVsCorrectController) currentHyphenApproachController;
				setWordForTryAWord(controller, predController.getHyphenWordsPredictedVsCorrectTable());
			}

			tryAWordDialogStage.initModality(Modality.NONE);
			tryAWordDialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	public void toggleView() {
		String sClass = currentHyphenApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			handleHyphenClasses();
			handleHyphenSegmentInventory();
			break;

		case "org.sil.syllableparser.view.HyphenClassesController":
			handleHyphenSegmentInventory();
			handleHyphenClasses();
			break;

		case "org.sil.syllableparser.view.HyphenChangeRulesController":
			handleHyphenClasses();
			handleHyphenChangeRules();
			break;

		case "org.sil.syllableparser.view.HyphenWordsController":
			handleHyphenClasses();
			handleHyphenWords();
			break;

		case "org.sil.syllableparser.view.HyphenWordsPredictedVsCorrectController":
			handleHyphenChangeRules();
			handleHyphenWordsPredictedVsCorrect();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			handleGraphemeNaturalClasses();
			handleEnvironments();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			handleEnvironments();
			handleGraphemeNaturalClasses();
			break;

		default:
			break;
		}
	}

	@Override
	void handleFilterCorrectSyllabifications() {
		if (currentHyphenApproachController instanceof CVWordsController ) {
			CVWordsController controller = (CVWordsController) currentHyphenApproachController;
			controller.handleFilterCorrectSyllabifications();
		}
	}

	@Override
	void handleFilterPredictedSyllabifications() {
		if (currentHyphenApproachController instanceof CVWordsController ) {
			CVWordsController controller = (CVWordsController) currentHyphenApproachController;
			controller.handleFilterPredictedSyllabifications();
		}
	}

	public void handleFilterWords() {
		if (currentHyphenApproachController instanceof CVWordsController ) {
			CVWordsController controller = (CVWordsController) currentHyphenApproachController;
			controller.handleFilterWords();
		} else if (currentHyphenApproachController instanceof CVWordsPredictedVsCorrectController ) {
			CVWordsPredictedVsCorrectController controller = (CVWordsPredictedVsCorrectController) currentHyphenApproachController;
			controller.handleFilterWords();
		}
	}

	@Override
	void handleRemoveAllFilters() {
		if (currentHyphenApproachController instanceof CVWordsController ) {
			CVWordsController controller = (CVWordsController) currentHyphenApproachController;
			controller.handleRemoveAllFilters();
		} else if (currentHyphenApproachController instanceof CVWordsPredictedVsCorrectController ) {
			CVWordsPredictedVsCorrectController controller = (CVWordsPredictedVsCorrectController) currentHyphenApproachController;
			controller.handleRemoveFiltersWord();
		}
	}
}
