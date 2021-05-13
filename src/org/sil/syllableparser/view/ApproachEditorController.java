// Copyright (c) 2016-2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.MainApp;
import org.sil.utility.*;

import javafx.fxml.FXML;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * @author Andy Black
 *
 *         Code for cut, copy, and paste based on
 *         http://bekwam.blogspot.com/2014
 *         /10/cut-copy-and-paste-from-javafx-menubar.html and
 *         http://bekwam.blogspot
 *         .com/2014/11/javafx-cut-copy-and-paste-from-toolbar.html
 */
public abstract class ApproachEditorController {

	protected MainApp mainApp;
	protected ResourceBundle bundle;
	protected Locale locale;
	protected RootLayoutController rootController;
	protected Clipboard systemClipboard = Clipboard.getSystemClipboard();
	protected ToolBarCutCopyPasteDelegate toolBarDelegate;

	abstract void handleInsertNewItem();
	abstract void handleRemoveItem();
	abstract void handlePreviousItem();
	abstract void handleNextItem();

	abstract TextField[] createTextFields();

	/**
	 * @return the mainApp
	 */
	public MainApp getMainApp() {
		return mainApp;
	}

	public void setRootLayout(RootLayoutController controller) {
		rootController = controller;
	}

	/**
	 * @param mainApp
	 *            the mainApp to set
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ToolBarCutCopyPasteDelegate getToolBarDelegate() {
		return toolBarDelegate;
	}

	public void setToolBarDelegate(ToolBarCutCopyPasteDelegate toolBarDelegate) {
		this.toolBarDelegate = toolBarDelegate;
	}

	private String getSelectedText(TextField[] textFields) {
		for (TextField tf : textFields) {
			String text = tf.getSelectedText();
			if (!StringUtilities.isNullOrEmpty(text)) {
				return text;
			}
		}
		return null;
	}

	protected TextField findFocusedTextField(TextField[] textFields) {
		if (textFields != null && textFields.length > 0) {
			for (TextField tf : textFields) {
				if (tf.isFocused()) {
					return tf;
				}
			}
		}
		return null;
	}

	protected TextField getFocusedTextField() {
		TextField[] textFields = createTextFields();
		return findFocusedTextField(textFields);
	}

	public void setViewItemUsed(int value) {
		// default is to do nothing
	}
	
	void handleCopy() {
		TextField[] textFields = createTextFields();
		String text = getSelectedText(textFields);
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		systemClipboard.setContent(content);
	}

	void handleCut() {
		TextField focusedTF = getFocusedTextField();
		processCut(focusedTF, false);
	}

	protected void processCut(TextField focusedTF, boolean bUseToolBarDelegate) {
		if (focusedTF != null) {
			String text;
			IndexRange range;

			if (bUseToolBarDelegate) {
				text = toolBarDelegate.getLastSelectedText();
				range = toolBarDelegate.getLastSelectedRange();
			} else {
				text = focusedTF.getSelectedText();
				range = focusedTF.getSelection();
			}
			ClipboardContent content = new ClipboardContent();
			content.putString(text);
			systemClipboard.setContent(content);
			String origText = focusedTF.getText();
			String firstPart = origText.substring(0, range.getStart());
			String lastPart = origText.substring(range.getEnd(), origText.length());
			focusedTF.setText(firstPart + lastPart);

			focusedTF.positionCaret(range.getStart());
		}
	}

	void handlePaste() {
		if (!systemClipboard.hasContent(DataFormat.PLAIN_TEXT)) {
			rootController.adjustForEmptyClipboard();
			return;
		}

		String clipboardText = systemClipboard.getString();
		if (clipboardText == null || clipboardText.length() == 0) {
			return;
		}

		TextField focusedTF = getFocusedTextField();
		if (focusedTF == null) {
			return;
		}
		IndexRange range = focusedTF.getSelection();

		String origText = focusedTF.getText();
		processPaste(clipboardText, focusedTF, range, origText);
	}

	protected void processPaste(String clipboardText, TextField focusedTF, IndexRange range,
			String origText) {
		if (origText != null) {
			int endPos = 0;
			String updatedText = "";
			String firstPart = origText.substring(0, range.getStart());
			String lastPart = origText.substring(range.getEnd(), origText.length());

			updatedText = firstPart + clipboardText + lastPart;

			if (range.getStart() == range.getEnd()) {
				endPos = range.getEnd() + clipboardText.length();
			} else {
				endPos = range.getStart() + clipboardText.length();
			}

			focusedTF.setText(updatedText);
			focusedTF.positionCaret(endPos);
		}
	}

	boolean anythingSelected() {
		TextField[] textFields = createTextFields();
		if (textFields != null && textFields.length > 0) {
			for (TextField tf : textFields) {
				String sSelected = tf.getSelectedText();
				if (!StringUtilities.isNullOrEmpty(sSelected)) {
					return true;
				}
			}
		}
		return false;
	}

	public void handleToolBarCut() {
		TextField focusedTF = toolBarDelegate.getLastFocusedTF();
		processCut(focusedTF, true);
	}

	public void handleToolBarCopy() {
		ClipboardContent content = new ClipboardContent();
		content.putString(toolBarDelegate.getLastSelectedText());
		systemClipboard.setContent(content);
	}

	public void handleToolBarPaste() {
		if (!systemClipboard.hasContent(DataFormat.PLAIN_TEXT)) {
			toolBarDelegate.adjustForEmptyClipboard();
			return;
		}
		String clipboardText = systemClipboard.getString();
		TextField lastFocusedTF = toolBarDelegate.getLastFocusedTF();
		IndexRange lastSelectedRange = toolBarDelegate.getLastSelectedRange();
		String origText = lastFocusedTF.getText();
		processPaste(clipboardText, lastFocusedTF, lastSelectedRange, origText);
	}

	@FXML
	public void toolBarMouseReleased(MouseEvent evt) {
		TextField tf = (TextField) evt.getSource();
		String selectedText = tf.getSelectedText();
		IndexRange selectedRange = tf.getSelection();
		toolBarDelegate.updateToolBarForClipboard(tf, selectedText, selectedRange);
	}

	@FXML
	public void keyboardReleased(KeyEvent evt) {
		KeyCode code = evt.getCode();
		if (evt.isShiftDown()) {
			if (code == KeyCode.LEFT || code == KeyCode.KP_LEFT || code == KeyCode.RIGHT
					|| code == KeyCode.KP_RIGHT || code == KeyCode.HOME || code == KeyCode.END) {
				TextField tf = (TextField) evt.getSource();
				String selectedText = tf.getSelectedText();
				IndexRange selectedRange = tf.getSelection();
				toolBarDelegate.updateToolBarForClipboard(tf, selectedText, selectedRange);
			}
		} else { // attempt at trying to fix odd case where using toolbar with
					// keyboard may paste in unexpected location
			if (code == KeyCode.LEFT || code == KeyCode.KP_LEFT || code == KeyCode.RIGHT
					|| code == KeyCode.KP_RIGHT || code == KeyCode.HOME || code == KeyCode.END) {
				TextField tf = (TextField) evt.getSource();
				String selectedText = tf.getSelectedText();
				IndexRange selectedRange = tf.getSelection();
				toolBarDelegate.updateToolBarForClipboard(tf, selectedText, selectedRange);
			}
		}
	}
}
