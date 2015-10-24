/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.*;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

/**
 * @author Andy Black
 *
 */
public class CVApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections
			.observableArrayList();
	private CVApproach cvApproachData;
	private ApproachController currentCVApproachController;

	public CVApproachController(ResourceBundle bundle,
			Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(
				bundle.getString("cv.view.segmentinventory"),
				"handleCVSegmentInventory"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(
				bundle.getString("cv.view.syllablepatterns"),
				"handleCVSyllablePatterns"));
		views.add(new ApproachView(bundle.getString("cv.view.exceptionlist"),
				"handleCVExceptionsList"));
		views.add(new ApproachView(bundle.getString("cv.view.words"),
				"handleCVWords"));
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

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentCVApproachController.handleInsertNewItem();
		
	}
	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentCVApproachController.handleRemoveItem();
		
	}
}
