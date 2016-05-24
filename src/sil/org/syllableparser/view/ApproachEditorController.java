/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Approach;
import sil.org.syllableparser.model.cvapproach.CVApproach;

/**
 * @author Andy Black
 *
 */
public abstract class ApproachEditorController {

	protected MainApp mainApp;
	protected ResourceBundle bundle;
	protected Locale locale;
	protected RootLayoutController rootController;
	protected Clipboard systemClipboard = Clipboard.getSystemClipboard();

	abstract void handleInsertNewItem();

	abstract void handleRemoveItem();

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

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private String getSelectedText(TextField[] textFields) {
		for (TextField tf : textFields) {
			String text = tf.getSelectedText();
			if (text != null && text.length() > 0) {
				return text;
			}
		}
		return null;
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
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

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	protected TextField getFocusedTextField() {
		TextField[] textFields = createTextFields();
		return findFocusedTextField(textFields);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	void handleCopy() {
		TextField[] textFields = createTextFields();
		String text = getSelectedText(textFields);
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		systemClipboard.setContent(content);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	void handleCut() {
		TextField focusedTF = getFocusedTextField();
		if (focusedTF != null) {
			String text = focusedTF.getSelectedText();

			ClipboardContent content = new ClipboardContent();
			content.putString(text);
			systemClipboard.setContent(content);

			IndexRange range = focusedTF.getSelection();
			String origText = focusedTF.getText();
			String firstPart = origText.substring(0, range.getStart());
			String lastPart = origText.substring(range.getEnd(), origText.length());
			focusedTF.setText(firstPart + lastPart);

			focusedTF.positionCaret(range.getStart());
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
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

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	boolean anythingSelected() {
		TextField[] textFields = createTextFields();
		if (textFields != null && textFields.length > 0) {
			for (TextField tf : textFields) {
				String sSelected = tf.getSelectedText();
				if (sSelected != null && sSelected.length() > 0) {
					return true;
				}
			}
		}
		return false;
	}
}
