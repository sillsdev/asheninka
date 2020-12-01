// Copyright (c) 2018-2020 SIL International
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
import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.*;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproachView;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.SHSyllabifier;
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
public class SHApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private SHApproach shApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentSHApproachController;

	public SHApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("sh.view.segmentinventory"),
				"handleSHSegmentInventory"));
		views.add(new ApproachView(bundle.getString("sh.view.sonorityhierarchy"),
				"handleSHSonorityHierarchy"));
		views.add(new ApproachView(bundle.getString("sh.view.words"), "handleSHWords"));
		views.add(new ApproachView(bundle.getString("sh.view.wordspredictedvscorrect"),
				"handleSHWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("sh.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("sh.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentSHApproachController == null) {
			sView = prefs.getLastSHApproachViewUsed();
			return sView;
		}
		String sClass = currentSHApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			sView = SHApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			sView = SHApproachView.SONORITY_HIERARCHY.toString();
			break;

		case "org.sil.syllableparser.view.SHWordsController":
			sView = SHApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.SHWordsPredictedVsCorrectController":
			sView = SHApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = SHApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = SHApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setSHApproachData(SHApproach shApproach, ObservableList<Word> words) {
		this.shApproachData = shApproach;
		languageProject = shApproach.getLanguageProject();
		this.words = words;
	}

	public void handleSHSegmentInventory() {
		// CV and Sonority Hierarchy use the same segment layout
		FXMLLoader loader = createFXMLLoader("fxml/CVSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData);
		controller.setApproach(ApplicationPreferences.SH_SEGMENTS);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.setViewItemUsed(prefs.getLastSHSegmentInventoryViewItemUsed());
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentSHApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleSHSonorityHierarchy() {
		FXMLLoader loader = createFXMLLoader("fxml/SHSonorityHierarchy.fxml");
		SHSonorityHierarchyController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleSHWords() {
		handleSHWords(0, false);
	}

	public void handleSHWords(int index, boolean fResetIndex) {
		FXMLLoader loader = createFXMLLoader("fxml/SHWords.fxml");
		SHWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData, words);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		if (fResetIndex) {
			controller.setFocusOnWord(index);
		}
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false);
	}

	public void handleSHWordsPredictedVsCorrect(int index) {
		FXMLLoader loader = createFXMLLoader("fxml/SHWordsPredictedVsCorrect.fxml");
		SHWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData, words);
		controller.setFocusOnWord(index);
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false, true);
	}

	public void handleSHWordsPredictedVsCorrect() {
		FXMLLoader loader = createFXMLLoader("fxml/SHWordsPredictedVsCorrect.fxml");
		SHWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData, words);
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false, true);
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastSHGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(shApproachData);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastSHEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastSHApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentSHApproachController.handleInsertNewItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentSHApproachController.handleRemoveItem();
	}

	@Override
	void handlePreviousItem() {
		currentSHApproachController.handlePreviousItem();
	}

	@Override
	void handleNextItem() {
		currentSHApproachController.handleNextItem();
	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		String sSuccess = bundle.getString("label.success");
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sSyllabificationFailure = bundle.getString("label.shsyllabificationfailure");
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				CVSegmenter segmenter;
				SHSyllabifier syllabifier;

				segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				syllabifier = new SHSyllabifier(languageProject.getSHApproach());

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setSHParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setSHPredictedSyllabification("");
						continue;
					}
					List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					fSuccess = syllabifier.syllabify(segmentsInWord);
					word.setSHLingTreeDescription(syllabifier.getLingTreeDescriptionOfCurrentWord());
					if (!fSuccess) {
						word.setSHParserResult(sSyllabificationFailure);
						word.setSHPredictedSyllabification("");
						continue;
					}
					word.setSHPredictedSyllabification(syllabifier
							.getSyllabificationOfCurrentWord());
					word.setSHParserResult(sSuccess);
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
			if (currentSHApproachController instanceof SHWordsController) {
				SHWordsController shController = (SHWordsController) currentSHApproachController;
				shController.updateStatusBarWords(shController.getPredictedWords(),
						shController.getPredictedEqualsCorrectWords());
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
					Constants.RESOURCE_LOCATION);

			CVPredictedToCorrectSyllabificationChooserController controller = loader
					.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(shApproachData, words);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

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
			String resource = "fxml/SHComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			SHComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(shApproachData);
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
			ObservableList<Word> wordsToUse = words;
			if (currentSHApproachController instanceof SHWordsPredictedVsCorrectController) {
				SHWordsPredictedVsCorrectController predController = (SHWordsPredictedVsCorrectController) currentSHApproachController;
				wordsToUse = predController.getSHWordsPredictedVsCorrectTable().getItems();
			}
			for (Word word : wordsToUse) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				if (currentSHApproachController instanceof SHWordsPredictedVsCorrectController) {
					handleSHWordsPredictedVsCorrect(index);
				} else {
					handleSHWords(index, true);
					rootController.selectApproachViewItem(Constants.SH_WORDS_VIEW_INDEX);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return shApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return shApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return shApproachData.getHyphenatedWordsXLingPaper(words);
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
		currentSHApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentSHApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentSHApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentSHApproachController != null) {
			return currentSHApproachController.anythingSelected();
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
		currentSHApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentSHApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentSHApproachController.handleToolBarCut();
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
			String resource = "fxml/SHTryAWord.fxml";
			String title = bundle.getString("label.tryaword");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			SHTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(shApproachData);

			if (currentSHApproachController instanceof SHWordsController) {
				SHWordsController shWordsController = (SHWordsController) currentSHApproachController;
				setWordForTryAWord(controller, shWordsController.getSHWordsTable());
			} else if (currentSHApproachController instanceof SHWordsPredictedVsCorrectController) {
				SHWordsPredictedVsCorrectController predController = (SHWordsPredictedVsCorrectController) currentSHApproachController;
				setWordForTryAWord(controller, predController.getSHWordsPredictedVsCorrectTable());
			}

			tryAWordDialogStage.initModality(Modality.NONE);
			tryAWordDialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toggleView() {
		String sClass = currentSHApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			handleSHSonorityHierarchy();
			handleSHSegmentInventory();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			handleSHSegmentInventory();
			handleSHSonorityHierarchy();
			break;

		case "org.sil.syllableparser.view.SHWordsController":
			handleSHSegmentInventory();
			handleSHWords();
			break;

		case "org.sil.syllableparser.view.SHWordsPredictedVsCorrectController":
			handleSHSonorityHierarchy();
			handleSHWordsPredictedVsCorrect();
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
		if (currentSHApproachController instanceof SHWordsController ) {
			SHWordsController controller = (SHWordsController) currentSHApproachController;
			controller.handleFilterCorrectSyllabifications();
		}
	}

	@Override
	void handleFilterPredictedSyllabifications() {
		if (currentSHApproachController instanceof SHWordsController ) {
			SHWordsController controller = (SHWordsController) currentSHApproachController;
			controller.handleFilterPredictedSyllabifications();
		}
	}

	public void handleFilterWords() {
		if (currentSHApproachController instanceof SHWordsController ) {
			SHWordsController controller = (SHWordsController) currentSHApproachController;
			controller.handleFilterWords();
		} else if (currentSHApproachController instanceof SHWordsPredictedVsCorrectController) {
			SHWordsPredictedVsCorrectController controller = (SHWordsPredictedVsCorrectController) currentSHApproachController;
			controller.handleFilterWords();
		}
	}

	@Override
	void handleRemoveAllFilters() {
		if (currentSHApproachController instanceof SHWordsController ) {
			SHWordsController controller = (SHWordsController) currentSHApproachController;
			controller.handleRemoveAllFilters();
		} else if (currentSHApproachController instanceof SHWordsPredictedVsCorrectController) {
			SHWordsPredictedVsCorrectController controller = (SHWordsPredictedVsCorrectController) currentSHApproachController;
			controller.handleRemoveFiltersWord();
		}
	}
}
