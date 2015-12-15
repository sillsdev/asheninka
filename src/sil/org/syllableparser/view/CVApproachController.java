/**
 * 
 */
package sil.org.syllableparser.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.model.*;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVNaturalClasser;
import sil.org.syllableparser.model.cvapproach.CVSegment;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmenter;
import sil.org.syllableparser.model.cvapproach.CVSyllabifier;
import sil.org.syllableparser.model.cvapproach.CVSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import sil.org.syllableparser.model.cvapproach.CVWord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;

/**
 * @author Andy Black
 *
 */
public class CVApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private CVApproach cvApproachData;
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
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public void setCVApproachData(CVApproach cvApproach) {
		this.cvApproachData = cvApproach;
	}

	public void handleCVSegmentInventory() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVSegmentInventory.fxml", locale);
		CVSegmentInventoryController controller = loader.getController();
		currentCVApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setLocale(locale);

		controller.setData(cvApproachData);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVNaturalClasses.fxml", locale);
		CVNaturalClassesController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		controller.setData(cvApproachData);
	}

	public void handleCVSyllablePatterns() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVSyllablePatterns.fxml", locale);
		CVSyllablePatternsController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		controller.setData(cvApproachData);
	}

	public void handleCVWords() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVWords.fxml", locale);
		CVWordsController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		controller.setData(cvApproachData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentCVApproachController.handleInsertNewItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentCVApproachController.handleRemoveItem();

	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		
		long timeStart = System.currentTimeMillis(); 

        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
        		ObservableList<CVNaturalClass> naturalClasses;
        		CVSegmenter segmenter;
        		ObservableList<CVSegment> segmentInventory;
        		List<CVSegment> cvSegmentInventory;
        		CVNaturalClasser naturalClasser;
        		List<CVNaturalClass> cvNaturalClasses;
        		ObservableList<CVSyllablePattern> patterns;
        		CVSyllabifier syllabifier;
        		List<CVSyllablePattern> cvPatterns;

        		segmentInventory = cvApproachData.getCVSegmentInventory();
        		segmenter = new CVSegmenter(segmentInventory);
        		cvSegmentInventory = segmenter.getSegmentInventory();
        		naturalClasses = cvApproachData.getCVNaturalClasses();
        		naturalClasser = new CVNaturalClasser(naturalClasses);
        		cvNaturalClasses = naturalClasser.getNaturalClasses();
        		patterns = cvApproachData.getCVSyllablePatterns();
        		syllabifier = new CVSyllabifier(patterns, null);
        		cvPatterns = syllabifier.getCvPatterns();

        		int max = cvApproachData.getCVWords().size();
        		int i = 0;
        		for (CVWord word : cvApproachData.getCVWords()) {
                    updateMessage(bundle.getString("label.syllabifying") + word.getCVWord());
                    updateProgress(i++, max);

        			boolean fSuccess = segmenter.segmentWord(word.getCVWord());
        			if (!fSuccess) {
        				word.setPredictedSyllabification("could not parse into segments");
        				continue;
        			}
        			List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
        			fSuccess = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
        			if (!fSuccess) {
        				word.setPredictedSyllabification("could not parse into natural classes");
        				continue;
        			}
        			List<CVNaturalClassInSyllable> naturalClassesInWord = naturalClasser
        					.getNaturalClassesInCurrentWord();
        			syllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
        			fSuccess = syllabifier.convertNaturalClassesToSyllables();
        			if (!fSuccess) {
        				word.setPredictedSyllabification("could not parse natural classes into syllables");
        				continue;
        			}
        			List<CVSyllable> syllablesInWord = syllabifier.getSyllablesInCurrentWord();
        			word.setPredictedSyllabification(syllabifier.getSyllabificationOfCurrentWord());
        		}
        		long timePassed = System.currentTimeMillis() - timeStart;
        		System.out.println("Syllabification took " + timePassed + " milliseconds");
        		// sleep for a second since it all happens so quickly       		
   				Thread.sleep(1000);updateProgress(0, 0);
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

        new Thread(task).start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sil.org.syllableparser.view.ApproachController#createNewWord(java.lang
	 * .String)
	 */
	@Override
	void createNewWord(String word) {
		CVWord newWord = new CVWord(word, "", "");
		cvApproachData.getCVWords().add(newWord);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#getHyphenatedWords()
	 */
	@Override
	public ArrayList<String> getHyphenatedWords() {
		return cvApproachData.getHyphenatedWords();
	}
}
