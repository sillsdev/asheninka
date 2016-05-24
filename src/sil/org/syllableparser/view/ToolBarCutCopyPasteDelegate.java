/**
 * 
 */
package sil.org.syllableparser.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;

/**
 * @author Andy Black
 *
 * based on code from 
 *   http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
 *   http://bekwam.blogspot.com/2014/11/javafx-cut-copy-and-paste-from-toolbar.html and
 *   http://bekwam.blogspot.com/2014/10/the-delegate-pattern-and-javafx.html
 */
public class ToolBarCutCopyPasteDelegate {

	protected Clipboard systemClipboard = Clipboard.getSystemClipboard();
	TextField lastFocusedTF;
	String lastSelectedText;
	IndexRange lastSelectedRange;

	@FXML
	public Button buttonToolbarEditCut;
	@FXML
	public Button buttonToolbarEditCopy;
	@FXML
	public Button buttonToolbarEditPaste;

	public void updateToolBarForClipboard(TextField focusedTF, String selectedText,
			IndexRange selectedRange) {

		if (systemClipboard == null) {
			systemClipboard = Clipboard.getSystemClipboard();
		}

		if (systemClipboard.hasString()) {
			adjustForClipboardContents();
		} else {
			adjustForEmptyClipboard();
		}

		String sSelected = focusedTF.getSelectedText();
		if (sSelected != null && sSelected.length() > 0) {
			adjustForSelection();

		} else {
			adjustForDeselection();
		}

		lastFocusedTF = focusedTF;
		lastSelectedText = selectedText;
		lastSelectedRange = selectedRange;
	}

	public TextField getLastFocusedTF() {
		return lastFocusedTF;
	}

	public String getLastSelectedText() {
		return lastSelectedText;
	}

	public IndexRange getLastSelectedRange() {
		return lastSelectedRange;
	}
	
	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	public void adjustForEmptyClipboard() {
		buttonToolbarEditPaste.setDisable(true); // nothing to paste
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForClipboardContents() {
		buttonToolbarEditPaste.setDisable(false); // something to paste
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForSelection() {
		buttonToolbarEditCut.setDisable(false);
		buttonToolbarEditCopy.setDisable(false);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForDeselection() {
		buttonToolbarEditCut.setDisable(true);
		buttonToolbarEditCopy.setDisable(true);
	}

}
