/**
 * 
 */
package sil.org.syllableparser.view;

import sil.org.syllableparser.model.ApproachView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



/**
 * @author Andy Black
 *
 */
public class ONCApproachController {
	
	private ObservableList<ApproachView> views = FXCollections.observableArrayList() ;
	
	public ONCApproachController() {
		super();
		views.add(new ApproachView("Segment Inventory", "handleSegmentInventory"));
		views.add(new ApproachView("Onset Segments", "handleOnsetSegments"));
		
	}

	

}
