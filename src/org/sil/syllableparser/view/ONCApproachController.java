// Copyright (c) 2019-2020 SIL International 
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
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproachView;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.ONCSegmenter;
import org.sil.syllableparser.service.parsing.ONCSyllabifier;
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
public class ONCApproachController extends ApproachController  {
	
	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private ONCApproach oncApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentONCApproachController;

	public ONCApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("onc.view.segmentinventory"),
				"handleONCSegmentInventory"));
		views.add(new ApproachView(bundle.getString("onc.view.sonorityhierarchy"),
				"handleONCSonorityHierarchy"));
		views.add(new ApproachView(bundle.getString("onc.view.syllabificationparameters"),
				"handleSyllabificationParameters"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(bundle.getString("onc.view.templates"), "handleTemplates"));
		views.add(new ApproachView(bundle.getString("onc.view.filters"), "handleFilters"));
		views.add(new ApproachView(bundle.getString("onc.view.words"), "handleONCWords"));
		views.add(new ApproachView(bundle.getString("onc.view.wordspredictedvscorrect"),
				"handleONCWordsPredictedVsCorrect"));
		views.add(new ApproachView(bundle.getString("onc.view.graphemenaturalclasses"),
				"handleGraphemeNaturalClasses"));
		views.add(new ApproachView(bundle.getString("onc.view.environments"), "handleEnvironments"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public String getViewUsed() {
		String sView = "unknown";
		if (currentONCApproachController == null) {
			sView = prefs.getLastONCApproachViewUsed();
			return sView;
		}
		String sClass = currentONCApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.ONCSegmentInventoryController":
			sView = ONCApproachView.SEGMENT_INVENTORY.toString();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			sView = ONCApproachView.SONORITY_HIERARCHY.toString();
			break;

		case "org.sil.syllableparser.view.SyllabificationParametersController":
			sView = ONCApproachView.SYLLABIFICATION_PARAMETERS.toString();
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			sView = ONCApproachView.NATURAL_CLASSES.toString();
			break;

		case "org.sil.syllableparser.view.TemplatesController":
			sView = ONCApproachView.TEMPLATES.toString();
			break;

		case "org.sil.syllableparser.view.FiltersController":
			sView = ONCApproachView.FILTERS.toString();
			break;

		case "org.sil.syllableparser.view.ONCWordsController":
			sView = ONCApproachView.WORDS.toString();
			break;

		case "org.sil.syllableparser.view.ONCWordsPredictedVsCorrectController":
			sView = ONCApproachView.PREDICTED_VS_CORRECT_WORDS.toString();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			sView = ONCApproachView.ENVIRONMENTS.toString();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			sView = ONCApproachView.GRAPHEME_NATURAL_CLASSES.toString();
			break;

		default:
			break;
		}
		return sView;
	}

	public void setONCApproachData(ONCApproach shApproach, ObservableList<Word> words) {
		this.oncApproachData = shApproach;
		languageProject = shApproach.getLanguageProject();
		this.words = words;
	}

	public void handleONCSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/ONCSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData);
		controller.setViewItemUsed(prefs.getLastONCSegmentInventoryViewItemUsed());
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentONCApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}

	public void handleONCSonorityHierarchy() {
		FXMLLoader loader = createFXMLLoader("fxml/SHSonorityHierarchy.fxml");
		SHSonorityHierarchyController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleSyllabificationParameters() {
		FXMLLoader loader = createFXMLLoader("fxml/SyllabificationParameters.fxml");
		SyllabificationParametersController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData.getLanguageProject());
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/CVNaturalClasses.fxml");
		CVNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData.getLanguageProject().getCVApproach(),
				ApproachType.ONSET_NUCLEUS_CODA);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastONCCVNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleTemplates() {
		FXMLLoader loader = createFXMLLoader("fxml/Templates.fxml");
		TemplatesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData);
		int i = prefs.getLastONCTemplatesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleFilters() {
		FXMLLoader loader = createFXMLLoader("fxml/Filters.fxml");
		FiltersController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastONCFiltersViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleONCWords() {
		handleONCWords(0, false);
	}

	public void handleONCWords(int index, boolean fResetIndex) {
		FXMLLoader loader = createFXMLLoader("fxml/ONCWords.fxml");
		ONCWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData, words);
		mainApp.updateStatusBarNumberOfItems("");
		if (fResetIndex) {
			controller.setFocusOnWord(index);
		}
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleONCWordsPredictedVsCorrect() {
		FXMLLoader loader = createFXMLLoader("fxml/ONCWordsPredictedVsCorrect.fxml");
		ONCWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData, words);
		controller.setFocusOnWord(0);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleGraphemeNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/GraphemeNaturalClasses.fxml");
		GraphemeNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastONCGraphemeNaturalClassesViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	public void handleEnvironments() {
		FXMLLoader loader = createFXMLLoader("fxml/Environments.fxml");
		EnvironmentsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(oncApproachData);
		mainApp.updateStatusBarNumberOfItems("");
		int i = prefs.getLastONCEnvironmentsViewItemUsed();
		controller.setViewItemUsed(i);
		prefs.setLastONCApproachViewUsed(getViewUsed());
	}

	@Override
	void handleInsertNewItem() {
		currentONCApproachController.handleInsertNewItem();
	}

	@Override
	void handleRemoveItem() {
		currentONCApproachController.handleRemoveItem();
	}

	@Override
	void handlePreviousItem() {
		currentONCApproachController.handlePreviousItem();
	}

	@Override
	void handleNextItem() {
		currentONCApproachController.handleNextItem();
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
				ONCSegmenter segmenter;
				ONCSyllabifier syllabifier;

				segmenter = new ONCSegmenter(languageProject.getActiveGraphemes(),
						languageProject.getActiveGraphemeNaturalClasses());
				syllabifier = new ONCSyllabifier(languageProject.getONCApproach());

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					String sWord = word.getWord();
					CVSegmenterResult result = segmenter.segmentWord(sWord);
					boolean fSuccess = result.success;
					if (!fSuccess) {
						word.setONCParserResult(sSegmentFailure.replace("{0}",
								sWord.substring(result.iPositionOfFailure)));
						word.setONCPredictedSyllabification("");
						continue;
					}
					List<ONCSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					fSuccess = syllabifier.syllabify(segmentsInWord);
					word.setONCLingTreeDescription(syllabifier.getLingTreeDescriptionOfCurrentWord());
					if (!fSuccess) {
						word.setONCParserResult(sSyllabificationFailure);
						word.setONCPredictedSyllabification("");
						continue;
					}
					word.setONCPredictedSyllabification(syllabifier
							.getSyllabificationOfCurrentWord());
					word.setONCParserResult(sSuccess);
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
			if (currentONCApproachController instanceof ONCWordsController) {
				ONCWordsController oncController = (ONCWordsController) currentONCApproachController;
				oncController.updateStatusBarWords(oncController.getPredictedWords(),
						oncController.getPredictedEqualsCorrectWords());
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
			controller.setData(oncApproachData, words);

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
			String resource = "fxml/ONCComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			ONCComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(oncApproachData);
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
				handleONCWords(index, true);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		return oncApproachData.getHyphenatedWordsListWord(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		return oncApproachData.getHyphenatedWordsParaTExt(words);
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		return oncApproachData.getHyphenatedWordsXLingPaper(words);
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
		currentONCApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentONCApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentONCApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentONCApproachController != null) {
			return currentONCApproachController.anythingSelected();
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
		currentONCApproachController.handleToolBarCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentONCApproachController.handleToolBarPaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentONCApproachController.handleToolBarCut();
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
			String resource = "fxml/ONCTryAWord.fxml";
			String title = bundle.getString("label.tryaword");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, tryAWordDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			ONCTryAWordController controller = loader.getController();
			controller.setDialogStage(tryAWordDialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(oncApproachData);

			if (currentONCApproachController instanceof ONCWordsController) {
				ONCWordsController shWordsController = (ONCWordsController) currentONCApproachController;
				TableView<Word> shWordsTable = shWordsController.getONCWordsTable();
				Word shWord = (Word) shWordsTable.getSelectionModel().getSelectedItem();
				if (shWord != null) {
					String sCurrentWord = shWord.getWord();
					controller.getWordToTry().setText(sCurrentWord);
				}
			}

			tryAWordDialogStage.initModality(Modality.NONE);
			tryAWordDialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toggleView() {
		String sClass = currentONCApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.ONCSegmentInventoryController":
			handleSyllabificationParameters();
			handleONCSegmentInventory();
			break;

		case "org.sil.syllableparser.view.SHSonorityHierarchyController":
			handleSyllabificationParameters();
			handleONCSonorityHierarchy();
			break;

		case "org.sil.syllableparser.view.SyllabificationParametersController":
			// nothing to do
			break;

		case "org.sil.syllableparser.view.CVNaturalClassesController":
			handleSyllabificationParameters();
			handleCVNaturalClasses();
			break;

		case "org.sil.syllableparser.view.TemplatesController":
			handleSyllabificationParameters();
			handleTemplates();
			break;

		case "org.sil.syllableparser.view.FiltersController":
			handleSyllabificationParameters();
			handleFilters();
			break;

		case "org.sil.syllableparser.view.ONCWordsController":
			handleSyllabificationParameters();
			handleONCWords();
			break;

		case "org.sil.syllableparser.view.ONCWordsPredictedVsCorrectController":
			handleSyllabificationParameters();
			handleONCWordsPredictedVsCorrect();
			break;

		case "org.sil.syllableparser.view.EnvironmentsController":
			handleSyllabificationParameters();
			handleEnvironments();
			break;

		case "org.sil.syllableparser.view.GraphemeNaturalClassesController":
			handleSyllabificationParameters();
			handleGraphemeNaturalClasses();
			break;

		default:
			break;
		}
	}
}
