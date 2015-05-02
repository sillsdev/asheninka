/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.ResourceBundle;

import sil.org.syllableparser.model.ApproachView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



/**
 * @author Andy Black
 *
 */
public class ONCApproachController extends ApproachController  {
	
	private ObservableList<ApproachView> views = FXCollections.observableArrayList() ;
	
	public ONCApproachController(ResourceBundle bundle) {
		super();
		views.add(new ApproachView(bundle.getString("onc.view.segmentinventory"), "handleONCSegmentInventory"));
		views.add(new ApproachView(bundle.getString("onc.view.syllablepatterns"), "handleONCSyllablePatterns"));
		views.add(new ApproachView(bundle.getString("onc.view.exceptionlist"), "handleONCExceptionList"));
		views.add(new ApproachView(bundle.getString("onc.view.words"), "handleONCWords"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}
	
	public void handleONCSegmentInventory() {
		System.out.println("handleCVSegmentInventory reached");
	}

	public void handleONCSyllablePatterns() {
		System.out.println("handleONCSyllablePatterns reached");
	}
	
	public void handleONCExceptionList() {
		System.out.println("handleONCExceptionList reached");
	}
	
	public void handleONCWords() {
		System.out.println("handleONCWords reached");
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		// TODO Auto-generated method stub
		
	}

}
