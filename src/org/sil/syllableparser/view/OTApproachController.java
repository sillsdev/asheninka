// Copyright (c) 2021-2025 SIL International
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
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTApproachView;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.OTSegmenter;
import org.sil.syllableparser.service.parsing.OTSyllabifier;
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
public class OTApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private OTApproach otApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentOTApproachController;

	public OTApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("cv.view.segmentinventory"),
				"handleCVSegmentInventory"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(bundle.getString("ot.view.constraints"),
				"handleOTConstraints"));
		views.add(new ApproachView(bundle.getString("ot.view.constraintrankings"),
				"handleOTConstraintRankings"));
		views.add(new ApproachView(bundle.getString("ot.view.words"), "handleOTWords"));
		views.add(new ApproachView(bundle.getString("ot.view.wordspredictedvscorrect"),
				"handleOTWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("cv.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("cv.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentOTApproachController == null) {
			sView = prefs.getLastOTApproachViewUsed();
			return sView;
		}
		String sClass = currentOTApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			sView = OTApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			sView = OTApproachView.NATURAL_CLASSES.toString();
			break;

		case "org.sil.syllableparser.view.OTConstraintsController":
			sView = OTApproachView.CONSTRAINTS.toString();
			break;

		case "org.sil.syllableparser.view.OTConstraintRankingsController":
			sView = OTApproachView.RANKINGS.toString();
			break;

		case "org.sil.syllableparser.view.OTWordsController":
			sView = OTApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.OTWordsPredictedVsCorrectController":
			sView = OTApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = OTApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = OTApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setOTApproachData(OTApproach otApproach, ObservableList<Word> words) {
		this.otApproachData = otApproach;
		languageProject = otApproach.getLanguageProject();
		this.words = words;
	}

	public void handleCVSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.setViewItemUsed(prefs.getLastCVSegmentInventoryViewItemUsed());
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentOTApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/CVNaturalClasses.fxml");
		CVNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData.getLanguageProject().getCVApproach(), ApproachType.OPTIMALITY_THEORY);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastCVNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleOTConstraints() {
		FXMLLoader loader = createFXMLLoader("fxml/OTConstraints.fxml");
		OTConstraintsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleOTConstraintRankings() {
		FXMLLoader loader = createFXMLLoader("fxml/OTConstraintRankings.fxml");
		OTConstraintRankingsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleOTWords() {
		handleOTWords(0, false, false);
	}

	public void handleOTWords(int index, boolean fResetIndex, boolean fCheckFilters) {
		FXMLLoader loader = createFXMLLoader("fxml/OTWords.fxml");
		OTWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData, words);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.applyWordFilters();
		mainApp.updateStatusBarNumberOfItems("");
		if (fResetIndex) {
			controller.setFocusOnWord(index, fCheckFilters);
		}
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false);
	}

	public void handleOTWordsPredictedVsCorrect(int index) {
		FXMLLoader loader = createFXMLLoader("fxml/OTWordsPredictedVsCorrect.fxml");
		OTWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData, words);
		controller.initWordsFilter();
		controller.applyWordFilter();
		controller.setFocusOnWord(index);
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false, true);
	}

	public void handleOTWordsPredictedVsCorrect() {
		handleOTWordsPredictedVsCorrect(0);
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastOTGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(otApproachData);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastOTEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastOTApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentOTApproachController.handleInsertNewItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentOTApproachController.handleRemoveItem();
	}


	@Override
	void handlePreviousItem() {
		currentOTApproachController.handlePreviousItem();
	}

	@Override
	void handleNextItem() {
		currentOTApproachController.handleNextItem();
	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		String sSuccess = bundle.getString("label.success");
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sSyllabificationFailure = bundle.getString("label.otsyllabificationfailure");
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				OTSegmenter segmenter;
				OTSyllabifier syllabifier;

				segmenter = new OTSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				syllabifier = new OTSyllabifier(languageProject.getOTApproach());
				syllabifier.setBundle(bundle);

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setOTParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setOTPredictedSyllabification("");
						continue;
					}
					List<OTSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					fSuccess = syllabifier.syllabify(segmentsInWord);
					word.setOTLingTreeDescription(syllabifier.getLingTreeDescriptionOfCurrentWord());
					if (!fSuccess) {
						word.setOTParserResult(sSyllabificationFailure);
						word.setOTPredictedSyllabification("");
						continue;
					}
					word.setOTPredictedSyllabification(syllabifier
							.getSyllabificationOfCurrentWord());
					word.setOTParserResult(sSuccess);
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
			if (currentOTApproachController instanceof OTWordsController) {
				OTWordsController otController = (OTWordsController) currentOTApproachController;
				otController.updateStatusBarWords(otController.getPredictedWords(),
						otController.getPredictedEqualsCorrectWords());
			}
		});

		Platform.runLater(task);
	}

	@Override
	void handleConvertPredictedToCorrectSyllabification() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/OTPredictedToCorrectSyllabificationChooser.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					MainApp.kApplicationTitle, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			CVPredictedToCorrectSyllabificationChooserController controller = loader
					.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(otApproachData, words);
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
			String resource = "fxml/OTComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			OTComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(otApproachData);
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
			if (currentOTApproachController instanceof OTWordsPredictedVsCorrectController) {
				OTWordsPredictedVsCorrectController predController = (OTWordsPredictedVsCorrectController) currentOTApproachController;
				wordsToUse = predController.getOTWordsPredictedVsCorrectTable().getItems();
			}
			for (Word word : wordsToUse) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				if (currentOTApproachController instanceof OTWordsPredictedVsCorrectController) {
					handleOTWordsPredictedVsCorrect(index);
				} else {
					handleOTWords(index, true, true);
					rootController.selectApproachViewItem(Constants.OT_WORDS_VIEW_INDEX);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return otApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return otApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return otApproachData.getHyphenatedWordsXLingPaper(words);
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
		currentOTApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentOTApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentOTApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentOTApproachController != null) {
			return currentOTApproachController.anythingSelected();
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
		currentOTApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentOTApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentOTApproachController.handleToolBarCut();
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
			String resource = "fxml/OTTryAWord.fxml";
			String title = bundle.getString("label.tryaword") + " - " + bundle.getString("approach.ot");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					bundle);

			OTTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(otApproachData);

			if (currentOTApproachController instanceof OTWordsController) {
				OTWordsController otWordsController = (OTWordsController) currentOTApproachController;
				setWordForTryAWord(controller, otWordsController.getOTWordsTable());
			} else if (currentOTApproachController instanceof OTWordsPredictedVsCorrectController) {
				OTWordsPredictedVsCorrectController predController = (OTWordsPredictedVsCorrectController) currentOTApproachController;
				setWordForTryAWord(controller, predController.getOTWordsPredictedVsCorrectTable());
			}

			tryAWordDialogStage.initModality(Modality.NONE);
			tryAWordDialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	public void toggleView() {
		String sClass = currentOTApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			handleCVNaturalClasses();
			handleCVSegmentInventory();
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			handleCVSegmentInventory();
			handleCVNaturalClasses();
			break;

		case "org.sil.syllableparser.view.OTConstraintsController":
			handleCVNaturalClasses();
			handleOTConstraints();
			break;

		case "org.sil.syllableparser.view.OTConstraintRankingsController":
			handleCVNaturalClasses();
			handleOTConstraintRankings();
			break;

		case "org.sil.syllableparser.view.OTWordsController":
			handleCVNaturalClasses();
			handleOTWords();
			break;

		case "org.sil.syllableparser.view.OTWordsPredictedVsCorrectController":
			handleCVNaturalClasses();
			handleOTWordsPredictedVsCorrect();
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
		if (currentOTApproachController instanceof OTWordsController ) {
			OTWordsController controller = (OTWordsController) currentOTApproachController;
			controller.handleFilterCorrectSyllabifications();
		}
	}

	@Override
	void handleFilterPredictedSyllabifications() {
		if (currentOTApproachController instanceof OTWordsController ) {
			OTWordsController controller = (OTWordsController) currentOTApproachController;
			controller.handleFilterPredictedSyllabifications();
		}
	}

	public void handleFilterWords() {
		if (currentOTApproachController instanceof OTWordsController ) {
			OTWordsController controller = (OTWordsController) currentOTApproachController;
			controller.handleFilterWords();
		} else if (currentOTApproachController instanceof OTWordsPredictedVsCorrectController ) {
			OTWordsPredictedVsCorrectController controller = (OTWordsPredictedVsCorrectController) currentOTApproachController;
			controller.handleFilterWords();
		}
	}

	@Override
	void handleRemoveAllFilters() {
		if (currentOTApproachController instanceof OTWordsController ) {
			OTWordsController controller = (OTWordsController) currentOTApproachController;
			controller.handleRemoveAllFilters();
		} else if (currentOTApproachController instanceof OTWordsPredictedVsCorrectController ) {
			OTWordsPredictedVsCorrectController controller = (OTWordsPredictedVsCorrectController) currentOTApproachController;
			controller.handleRemoveFiltersWord();
		}
	}
}
