/**
 * 
 */
package sil.org.syllableparser.view;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
		views.add(new ApproachView(bundle.getString("cv.view.exceptionlist"),
				"handleCVExceptionsList"));
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

		controller.setData(cvApproachData);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVNaturalClasses.fxml", locale);
		CVNaturalClassesController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setData(cvApproachData);
		controller.setMainApp(mainApp);
		controller.setLocale(locale);
	}

	public void handleCVSyllablePatterns() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVSyllablePatterns.fxml", locale);
		CVSyllablePatternsController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setData(cvApproachData);
		controller.setMainApp(mainApp);
		controller.setLocale(locale);
	}

	public void handleCVExceptionsList() {
		System.out.println("handleCVExceptionsList reached");
	}

	public void handleCVWords() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVWords.fxml", locale);
		CVWordsController controller = loader.getController();
		currentCVApproachController = controller;

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
	void handleSyllabifyWords() {
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

		for (CVWord word : cvApproachData.getCVWords()) {
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

	}
}
