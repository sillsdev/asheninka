// Copyright (c) 2021 SIL International 
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
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.ApproachView;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPApproachView;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.NPSegmenter;
import org.sil.syllableparser.service.parsing.NPSyllabifier;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;



/**
 * @author Andy Black
 *
 */
public class NPApproachController extends ApproachController  {
	
	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private NPApproach npApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentNPApproachController;

	public NPApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("cv.view.segmentinventory"),
				"handleNPSegmentInventory"));
		views.add(new ApproachView(bundle.getString("onc.view.sonorityhierarchy"),
				"handleNPSonorityHierarchy"));
		views.add(new ApproachView(bundle.getString("onc.view.syllabificationparameters"),
				"handleNPSyllabificationParameters"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(bundle.getString("np.view.rules"), "handleRules"));
		views.add(new ApproachView(bundle.getString("np.view.filters"), "handleFilters"));
		views.add(new ApproachView(bundle.getString("np.view.words"), "handleNPWords"));
		views.add(new ApproachView(bundle.getString("np.view.wordspredictedvscorrect"),
				"handleNPWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("onc.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("onc.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentNPApproachController == null) {
			sView = prefs.getLastNPApproachViewUsed();
			return sView;
		}
		String sClass = currentNPApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			sView = NPApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			sView = NPApproachView.SONORITY_HIERARCHY.toString();
			break;

		case "org.sil.syllableparser.view.NPSyllabificationParametersController":
			sView = NPApproachView.SYLLABIFICATION_PARAMETERS.toString();
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			sView = NPApproachView.NATURAL_CLASSES.toString();
			break;

		case "org.sil.syllableparser.view.NPRulesController":
			sView = NPApproachView.RULES.toString();
			break;

		case "org.sil.syllableparser.view.NPFiltersController":
			sView = NPApproachView.FILTERS.toString();
			break;

		case "org.sil.syllableparser.view.NPWordsController":
			sView = NPApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.NPWordsPredictedVsCorrectController":
			sView = NPApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = NPApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = NPApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setNPApproachData(NPApproach npApproach, ObservableList<Word> words) {
		this.npApproachData = npApproach;
		languageProject = npApproach.getLanguageProject();
		this.words = words;
	}

	public void handleNPSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.setViewItemUsed(prefs.getLastNPSegmentInventoryViewItemUsed());
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentNPApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleNPSonorityHierarchy() {
		FXMLLoader loader = createFXMLLoader("fxml/SHSonorityHierarchy.fxml");
		SHSonorityHierarchyController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleNPSyllabificationParameters() {
		FXMLLoader loader = createFXMLLoader("fxml/NPSyllabificationParameters.fxml");
		NPSyllabificationParametersController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData.getLanguageProject());
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/CVNaturalClasses.fxml");
		CVNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData.getLanguageProject().getCVApproach(),
				ApproachType.NUCLEAR_PROJECTION);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastNPCVNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleRules() {
		FXMLLoader loader = createFXMLLoader("fxml/NPRules.fxml");
		NPRulesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		int i = prefs.getLastNPRulesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleFilters() {
		FXMLLoader loader = createFXMLLoader("fxml/NPFilters.fxml");
		NPFiltersController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastNPFiltersViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleNPWords() {
		handleNPWords(0, false, false);
	}

	public void handleNPWords(int index, boolean fResetIndex, boolean fCheckFilters) {
		FXMLLoader loader = createFXMLLoader("fxml/NPWords.fxml");
		NPWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData, words);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.applyWordFilters();
		mainApp.updateStatusBarNumberOfItems("");
		if (fResetIndex) {
			controller.setFocusOnWord(index, fCheckFilters);
		}
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false);
	}

	public void handleNPWordsPredictedVsCorrect(int index) {
		FXMLLoader loader = createFXMLLoader("fxml/NPWordsPredictedVsCorrect.fxml");
		NPWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData, words);
		controller.initWordsFilter();
		controller.applyWordFilter();
		controller.setFocusOnWord(index);
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false, true);
	}

	public void handleNPWordsPredictedVsCorrect() {
		handleNPWordsPredictedVsCorrect(0);
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastNPGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(npApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastNPEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastNPApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	@Override
	void handleInsertNewItem() {
		currentNPApproachController.handleInsertNewItem();
	}

	@Override
	void handleRemoveItem() {
		currentNPApproachController.handleRemoveItem();
	}

	@Override
	void handlePreviousItem() {
		currentNPApproachController.handlePreviousItem();
	}

	@Override
	void handleNextItem() {
		currentNPApproachController.handleNextItem();
	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		String sSuccess = bundle.getString("label.success");
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sSyllabificationFailure = bundle.getString("label.oncsyllabificationfailure");
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				NPSegmenter segmenter;
				NPSyllabifier syllabifier;

				segmenter = new NPSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				syllabifier = new NPSyllabifier(languageProject.getNPApproach());

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setNPParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setNPPredictedSyllabification("");
						continue;
					}
					List<NPSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					fSuccess = syllabifier.syllabify(segmentsInWord);
					word.setNPLingTreeDescription(syllabifier.getLingTreeDescriptionOfCurrentWord());
					if (!fSuccess) {
						word.setNPParserResult(sSyllabificationFailure);
						word.setNPPredictedSyllabification("");
						continue;
					}
					word.setMoraicPredictedSyllabification(syllabifier
							.getSyllabificationOfCurrentWord());
					word.setMoraicParserResult(sSuccess);
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
			if (currentNPApproachController instanceof NPWordsController) {
				NPWordsController npController = (NPWordsController) currentNPApproachController;
				npController.updateStatusBarWords(npController.getPredictedWords(),
						npController.getPredictedEqualsCorrectWords());
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
			controller.setData(npApproachData, words);
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
			String resource = "fxml/NPComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			NPComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(npApproachData);
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
			Language vernacular = mainApp.getLanguageProject().getVernacularLanguage();
			NodeOrientation vernacularOrientation = vernacular.getOrientation();
			dialog.getEditor().setNodeOrientation(vernacularOrientation);
			dialog.getEditor().setFont(vernacular.getFont());
			String sVernacular = mainApp.getStyleFromColor(vernacular.getColor());
			dialog.getEditor().setStyle(sVernacular);

			ObservableList<String> listOfWords = FXCollections.observableArrayList();
			ObservableList<Word> wordsToUse = words;
			if (currentNPApproachController instanceof NPWordsPredictedVsCorrectController) {
				NPWordsPredictedVsCorrectController predController = (NPWordsPredictedVsCorrectController) currentNPApproachController;
				wordsToUse = predController.getNPWordsPredictedVsCorrectTable().getItems();
			}
			for (Word word : wordsToUse) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				if (currentNPApproachController instanceof NPWordsPredictedVsCorrectController) {
					handleNPWordsPredictedVsCorrect(index);
				} else {
					handleNPWords(index, true, true);
					rootController.selectApproachViewItem(Constants.NP_WORDS_VIEW_INDEX);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return npApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return npApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return npApproachData.getHyphenatedWordsXLingPaper(words);
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
		currentNPApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentNPApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentNPApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentNPApproachController != null) {
			return currentNPApproachController.anythingSelected();
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
		currentNPApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentNPApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentNPApproachController.handleToolBarCut();
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
			String resource = "fxml/NPTryAWord.fxml";
			String title = bundle.getString("label.tryaword");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			NPTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(npApproachData);

			if (currentNPApproachController instanceof NPWordsController) {
				NPWordsController npWordsController = (NPWordsController) currentNPApproachController;
				setWordForTryAWord(controller, npWordsController.getNPWordsTable());
			} else if (currentNPApproachController instanceof NPWordsPredictedVsCorrectController) {
				NPWordsPredictedVsCorrectController predController = (NPWordsPredictedVsCorrectController) currentNPApproachController;
				setWordForTryAWord(controller, predController.getNPWordsPredictedVsCorrectTable());
			}

			tryAWordDialogStage.initModality(Modality.NONE);
			tryAWordDialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@Override
	public void toggleView() {
		String sClass = currentNPApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVSegmentInventoryController":
			handleNPSyllabificationParameters();
			handleNPSegmentInventory();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			handleNPSyllabificationParameters();
			handleNPSonorityHierarchy();
			break;

		case "org.sil.syllableparser.view.SyllabificationParametersController":
			// nothing to do
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			handleNPSyllabificationParameters();
			handleCVNaturalClasses();
			break;

		case "org.sil.syllableparser.view.RulesController":
			handleNPSyllabificationParameters();
			handleRules();
			break;

		case "org.sil.syllableparser.view.FiltersController":
			handleNPSyllabificationParameters();
			handleFilters();
			break;

		case "org.sil.syllableparser.view.NPWordsController":
			handleNPSyllabificationParameters();
			handleNPWords();
			break;

		case "org.sil.syllableparser.view.NPWordsPredictedVsCorrectController":
			handleNPSyllabificationParameters();
			handleNPWordsPredictedVsCorrect();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			handleNPSyllabificationParameters();
			handleEnvironments();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			handleNPSyllabificationParameters();
			handleGraphemeNaturalClasses();
			break;

		default:
			break;
		}
	}

	@Override
	void handleFilterCorrectSyllabifications() {
		if (currentNPApproachController instanceof NPWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentNPApproachController;
			controller.handleFilterCorrectSyllabifications();
		}
	}

	@Override
	void handleFilterPredictedSyllabifications() {
		if (currentNPApproachController instanceof NPWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentNPApproachController;
			controller.handleFilterPredictedSyllabifications();
		}
	}

	public void handleFilterWords() {
		if (currentNPApproachController instanceof NPWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentNPApproachController;
			controller.handleFilterWords();
		} else if (currentNPApproachController instanceof NPWordsPredictedVsCorrectController ) {
			NPWordsPredictedVsCorrectController controller = (NPWordsPredictedVsCorrectController) currentNPApproachController;
			controller.handleFilterWords();
		}
	}

	@Override
	void handleRemoveAllFilters() {
		if (currentNPApproachController instanceof NPWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentNPApproachController;
			controller.handleRemoveAllFilters();
		} else if (currentNPApproachController instanceof NPWordsPredictedVsCorrectController ) {
			NPWordsPredictedVsCorrectController controller = (NPWordsPredictedVsCorrectController) currentNPApproachController;
			controller.handleRemoveFiltersWord();
		}
	}
}
