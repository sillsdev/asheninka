/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;

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
		views.add(new ApproachView(bundle.getString("onc.view.words"), "handleONCWords"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}
	
	public void handleONCSegmentInventory() {
		System.out.println("handleONCSegmentInventory reached");
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

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleSyllabifyWords()
	 */
	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#createNewWord(java.lang.String)
	 */
	@Override
	void createNewWord(String word) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#getHyphenatedWords()
	 */
	@Override
	ArrayList<String> getHyphenatedWords() {
		// TODO Auto-generated method stub
		return null;
	}
}
