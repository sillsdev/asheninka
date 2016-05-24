/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.model.ApproachView;
import sil.org.syllableparser.model.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



/**
 * @author Andy Black
 *
 */
public class ONCApproachController extends ApproachController  {
	
	private ObservableList<ApproachView> views = FXCollections.observableArrayList() ;
	private ObservableList<Word> words = FXCollections.observableArrayList();

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
	 * @see sil.org.syllableparser.view.ApproachController#getHyphenatedWords()
	 */
	@Override
	public ArrayList<String> getHyphenatedWords(ObservableList<Word> words) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<String> getParaTExtHyphenatedWords(ObservableList<Word> words) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getXLingPaperHyphenatedWords(ObservableList<Word> words) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void handleConvertPredictedToCorrectSyllabification() {
		
	}

	@Override
	void handleFindWord() {
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleCopy()
	 */
	@Override
	public void handleCopy() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
