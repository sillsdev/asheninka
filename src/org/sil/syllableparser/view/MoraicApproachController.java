// Copyright (c) 2020-2025 SIL International 
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
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.model.moraicapproach.MoraicApproachView;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.MoraicSegmenter;
import org.sil.syllableparser.service.parsing.MoraicSyllabifier;
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
public class MoraicApproachController extends ApproachController  {
	
	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private MoraicApproach moraicApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentMoraicApproachController;

	public MoraicApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("onc.view.segmentinventory"),
				"handleMoraicSegmentInventory"));
		views.add(new ApproachView(bundle.getString("onc.view.sonorityhierarchy"),
				"handleMoraicSonorityHierarchy"));
		views.add(new ApproachView(bundle.getString("onc.view.syllabificationparameters"),
				"handleMoraicSyllabificationParameters"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(bundle.getString("moraic.view.templates"), "handleTemplates"));
		views.add(new ApproachView(bundle.getString("moraic.view.filters"), "handleFilters"));
		views.add(new ApproachView(bundle.getString("moraic.view.words"), "handleMoraicWords"));
		views.add(new ApproachView(bundle.getString("moraic.view.wordspredictedvscorrect"),
				"handleMoraicWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("onc.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("onc.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentMoraicApproachController == null) {
			sView = prefs.getLastMoraicApproachViewUsed();
			return sView;
		}
		String sClass = currentMoraicApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.MoraicSegmentInventoryController":
			sView = MoraicApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			sView = MoraicApproachView.SONORITY_HIERARCHY.toString();
			break;

		case "org.sil.syllableparser.view.MoraicSyllabificationParametersController":
			sView = MoraicApproachView.SYLLABIFICATION_PARAMETERS.toString();
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			sView = MoraicApproachView.NATURAL_CLASSES.toString();
			break;

		case "org.sil.syllableparser.view.MoraicTemplatesController":
			sView = MoraicApproachView.TEMPLATES.toString();
			break;

		case "org.sil.syllableparser.view.MoraicFiltersController":
			sView = MoraicApproachView.FILTERS.toString();
			break;

		case "org.sil.syllableparser.view.MoraicWordsController":
			sView = MoraicApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.MoraicWordsPredictedVsCorrectController":
			sView = MoraicApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = MoraicApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = MoraicApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setMoraicApproachData(MoraicApproach moraicApproach, ObservableList<Word> words) {
		this.moraicApproachData = moraicApproach;
		languageProject = moraicApproach.getLanguageProject();
		this.words = words;
	}

	public void handleMoraicSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/MoraicSegmentInventory.fxml");
		MoraicSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.setViewItemUsed(prefs.getLastMoraicSegmentInventoryViewItemUsed());
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentMoraicApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleMoraicSonorityHierarchy() {
		FXMLLoader loader = createFXMLLoader("fxml/SHSonorityHierarchy.fxml");
		SHSonorityHierarchyController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleMoraicSyllabificationParameters() {
		FXMLLoader loader = createFXMLLoader("fxml/MoraicSyllabificationParameters.fxml");
		MoraicSyllabificationParametersController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData.getLanguageProject());
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/CVNaturalClasses.fxml");
		CVNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData.getLanguageProject().getCVApproach(),
				ApproachType.MORAIC);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastMoraicCVNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleTemplates() {
		FXMLLoader loader = createFXMLLoader("fxml/MoraicTemplates.fxml");
		MoraicTemplatesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		int i = prefs.getLastMoraicTemplatesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleFilters() {
		FXMLLoader loader = createFXMLLoader("fxml/MoraicFilters.fxml");
		MoraicFiltersController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastMoraicFiltersViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleMoraicWords() {
		handleMoraicWords(0, false, false);
	}

	public void handleMoraicWords(int index, boolean fResetIndex, boolean fCheckFilters) {
		FXMLLoader loader = createFXMLLoader("fxml/MoraicWords.fxml");
		MoraicWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData, words);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		controller.applyWordFilters();
		mainApp.updateStatusBarNumberOfItems("");
		if (fResetIndex) {
			controller.setFocusOnWord(index, fCheckFilters);
		}
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false);
	}

	public void handleMoraicWordsPredictedVsCorrect(int index) {
		FXMLLoader loader = createFXMLLoader("fxml/MoraicWordsPredictedVsCorrect.fxml");
		MoraicWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData, words);
		controller.initWordsFilter();
		controller.applyWordFilter();
		controller.setFocusOnWord(index);
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(false, true);
	}

	public void handleMoraicWordsPredictedVsCorrect() {
		handleMoraicWordsPredictedVsCorrect(0);
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastMoraicGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(moraicApproachData);
		controller.initializeTableColumnWidthsAndSplitDividerPosition();
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastMoraicEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastMoraicApproachViewUsed(getViewUsed());
		mainApp.getController().setFiltersDisabled(true);
	}

	@Override
	void handleInsertNewItem() {
		currentMoraicApproachController.handleInsertNewItem();
	}

	@Override
	void handleRemoveItem() {
		currentMoraicApproachController.handleRemoveItem();
	}

	@Override
	void handlePreviousItem() {
		currentMoraicApproachController.handlePreviousItem();
	}

	@Override
	void handleNextItem() {
		currentMoraicApproachController.handleNextItem();
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
				MoraicSegmenter segmenter;
				MoraicSyllabifier syllabifier;

				segmenter = new MoraicSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				syllabifier = new MoraicSyllabifier(languageProject.getMoraicApproach());

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setMoraicParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setMoraicPredictedSyllabification("");
						continue;
					}
					List<MoraicSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					fSuccess = syllabifier.syllabify(segmentsInWord);
					word.setMoraicLingTreeDescription(syllabifier.getLingTreeDescriptionOfCurrentWord());
					if (!fSuccess) {
						word.setMoraicParserResult(sSyllabificationFailure);
						word.setMoraicPredictedSyllabification("");
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
			if (currentMoraicApproachController instanceof MoraicWordsController) {
				MoraicWordsController muController = (MoraicWordsController) currentMoraicApproachController;
				muController.updateStatusBarWords(muController.getPredictedWords(),
						muController.getPredictedEqualsCorrectWords());
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
			controller.setData(moraicApproachData, words);
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
			String resource = "fxml/MoraicComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), bundle);

			MoraicComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(moraicApproachData);
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
			if (currentMoraicApproachController instanceof MoraicWordsPredictedVsCorrectController) {
				MoraicWordsPredictedVsCorrectController predController = (MoraicWordsPredictedVsCorrectController) currentMoraicApproachController;
				wordsToUse = predController.getMoraicWordsPredictedVsCorrectTable().getItems();
			}
			for (Word word : wordsToUse) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				if (currentMoraicApproachController instanceof MoraicWordsPredictedVsCorrectController) {
					handleMoraicWordsPredictedVsCorrect(index);
				} else {
					handleMoraicWords(index, true, true);
					rootController.selectApproachViewItem(Constants.MORAIC_WORDS_VIEW_INDEX);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return moraicApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return moraicApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return moraicApproachData.getHyphenatedWordsXLingPaper(words);
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
		currentMoraicApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentMoraicApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentMoraicApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentMoraicApproachController != null) {
			return currentMoraicApproachController.anythingSelected();
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
		currentMoraicApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentMoraicApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentMoraicApproachController.handleToolBarCut();
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
			String resource = "fxml/MoraicTryAWord.fxml";
			String title = bundle.getString("label.tryaword") + " - " + bundle.getString("approach.moraic");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					bundle);

			MoraicTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(moraicApproachData);

			if (currentMoraicApproachController instanceof MoraicWordsController) {
				MoraicWordsController npWordsController = (MoraicWordsController) currentMoraicApproachController;
				setWordForTryAWord(controller, npWordsController.getMoraicWordsTable());
			} else if (currentMoraicApproachController instanceof MoraicWordsPredictedVsCorrectController) {
				MoraicWordsPredictedVsCorrectController predController = (MoraicWordsPredictedVsCorrectController) currentMoraicApproachController;
				setWordForTryAWord(controller, predController.getMoraicWordsPredictedVsCorrectTable());
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
		String sClass = currentMoraicApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.MoraicSegmentInventoryController":
			handleMoraicSyllabificationParameters();
			handleMoraicSegmentInventory();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			handleMoraicSyllabificationParameters();
			handleMoraicSonorityHierarchy();
			break;

		case "org.sil.syllableparser.view.MoraicSyllabificationParametersController":
			// nothing to do
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			handleMoraicSyllabificationParameters();
			handleCVNaturalClasses();
			break;

		case "org.sil.syllableparser.view.TemplatesController":
			handleMoraicSyllabificationParameters();
			handleTemplates();
			break;

		case "org.sil.syllableparser.view.FiltersController":
			handleMoraicSyllabificationParameters();
			handleFilters();
			break;

		case "org.sil.syllableparser.view.MoraicWordsController":
			handleMoraicSyllabificationParameters();
			handleMoraicWords();
			break;

		case "org.sil.syllableparser.view.MoraicWordsPredictedVsCorrectController":
			handleMoraicSyllabificationParameters();
			handleMoraicWordsPredictedVsCorrect();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			handleMoraicSyllabificationParameters();
			handleEnvironments();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			handleMoraicSyllabificationParameters();
			handleGraphemeNaturalClasses();
			break;

		default:
			break;
		}
	}

	@Override
	void handleFilterCorrectSyllabifications() {
		if (currentMoraicApproachController instanceof MoraicWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentMoraicApproachController;
			controller.handleFilterCorrectSyllabifications();
		}
	}

	@Override
	void handleFilterPredictedSyllabifications() {
		if (currentMoraicApproachController instanceof MoraicWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentMoraicApproachController;
			controller.handleFilterPredictedSyllabifications();
		}
	}

	public void handleFilterWords() {
		if (currentMoraicApproachController instanceof MoraicWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentMoraicApproachController;
			controller.handleFilterWords();
		} else if (currentMoraicApproachController instanceof MoraicWordsPredictedVsCorrectController ) {
			MoraicWordsPredictedVsCorrectController controller = (MoraicWordsPredictedVsCorrectController) currentMoraicApproachController;
			controller.handleFilterWords();
		}
	}

	@Override
	void handleRemoveAllFilters() {
		if (currentMoraicApproachController instanceof MoraicWordsController ) {
			WordsControllerCommon controller = (WordsControllerCommon) currentMoraicApproachController;
			controller.handleRemoveAllFilters();
		} else if (currentMoraicApproachController instanceof MoraicWordsPredictedVsCorrectController ) {
			MoraicWordsPredictedVsCorrectController controller = (MoraicWordsPredictedVsCorrectController) currentMoraicApproachController;
			controller.handleRemoveFiltersWord();
		}
	}
}
