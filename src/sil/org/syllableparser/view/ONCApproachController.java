// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.model.ApproachType;
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
	
	public String getViewUsed() {
		String sView = "unknown";
//		String sClass = currentCVApproachController.getClass().getName();
//		switch (sClass) {
//		case "sil.org.syllableparser.view.CVApproachController":
//			sView = ApproachType.CV.toString();
//			break;
//
//		case "sil.org.syllableparser.view.ONCApproachController":
//			sView = ApproachType.ONSET_NUCLEUS_CODA.toString();
//			break;
//		
//		default:
//			break;
//		}
	return sView;
	}
	
	public int getViewItemUsed() {
		return 0;
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
	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
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

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleToolBarCopy()
	 */
	@Override
	public void handleToolBarCopy() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleCompareImplementations()
	 */
	@Override
	public void handleCompareImplementations() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void handleTryAWord() {
		// TODO Auto-generated method stub
		
	}

}
