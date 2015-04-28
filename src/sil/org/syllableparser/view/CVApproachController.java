/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.ApproachView;
import sil.org.syllableparser.model.CVApproach;
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
	private CVApproach cvApproachData;

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

	public void setCVApproachData(CVApproach data) {
		this.cvApproachData = data;
	}
	public void handleCVSegmentInventory() {
		System.out.println("handleCVSegmentInventory reached");
		
		FXMLLoader loader = new FXMLLoader();
        ApproachViewNavigator.loadApproachView(loader, "CVSegmentInventory.fxml", locale);
		CVSegmentInventoryController controller = loader.getController();
		
	    controller.setData(cvApproachData);
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
