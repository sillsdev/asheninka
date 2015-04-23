/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.ApproachView;
import sil.org.syllableparser.model.CVSegment;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * @author Andy Black
 *
 */
public class CVApproachController {

	private ObservableList<ApproachView> views = FXCollections
			.observableArrayList();
	private Locale locale;

	private ObservableList<CVSegment> cvSegmentInventoryData = FXCollections.observableArrayList();

	/* following or testing; will want something else eventually */
    public ObservableList<CVSegment> getCVSegmentInventoryData() {
        return cvSegmentInventoryData;
    }
	

	public CVApproachController(ResourceBundle bundle,
			Locale locale) {
		super();
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

	public void handleCVSegmentInventory() {
		System.out.println("handleCVSegmentInventory reached");
		ApproachViewNavigator.loadApproachView("CVSegmentInventory.fxml", locale);
//			FXMLLoader loader = new FXMLLoader();
//			loader.setLocation(MainApp.class
//					.getResource("/sil/org/syllableparser/view/CVSegmentInventory.fxml"));
//			loader.setResources(ResourceBundle.getBundle(
//					"sil.org.syllableparser.resources.SyllableParser", locale));
//
//			content = (AnchorPane) loader.load();
//
//			// Set the person into the controller.
//			CVSegmentInventoryController controller = loader.getController();
//			//CVSegment segment = new CVSegment("a", "a A",
//			//		"low mid unrounded vowel");
//			//controller.setSegment(segment);
//			
//	    	cvSegmentInventoryData.add(new CVSegment("a", "a A", "low mid unrounded vowel"));
//	    	cvSegmentInventoryData.add(new CVSegment("b", "b B", "voiced bilabial stop"));
//	    	cvSegmentInventoryData.add(new CVSegment("d", "d D", "voiced alveolar stop"));
//
//	    	controller.setData(this);
		// controller.setDialogStage(dialogStage);
	}

	public void handleCVNaturalClasses() {
		System.out.println("handleCVNaturalClasses reached");
	}

	public void handleCVSyllablePatterns() {
		System.out.println("handleCVSyllablePatterns reached");
	}

	public void handleCVExceptionsList() {
		System.out.println("handleCVExceptionsList reached");
	}

	public void handleCVWords() {
		System.out.println("handleCVWords reached");
	}
}
