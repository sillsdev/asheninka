// Copyright (c) 2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.Segment;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * @author Andy Black
 *
 */

public class MoraicSegmentInventoryController extends CVSegmentInventoryController {

	@FXML
	protected TableColumn<Segment, String> morasColumn;
	@FXML
	protected TextField morasField;
	private UnaryOperator<TextFormatter.Change> filter;

	public MoraicSegmentInventoryController() {
		super();
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		super.setApproach(ApplicationPreferences.MORAIC_SEGMENTS);

		morasColumn.setCellValueFactory(cellData -> cellData.getValue().morasBornProperty());

		filter = new UnaryOperator<TextFormatter.Change>() {
			@Override
			public TextFormatter.Change apply(TextFormatter.Change change) {
				String text = change.getText();
				for (int i = 0; i < text.length(); i++) {
					if (!Character.isDigit(text.charAt(i)))
						return null;
				}
				return change;
			}
		};
		morasField.setTextFormatter(new TextFormatter<String>(filter));
		morasField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setMorasBorn(morasField.getText());
			}
		});
	}

	/**
	 * Fills all text fields to show details about the CV segment. If the
	 * specified segment is null, all text fields are cleared.
	 * 
	 * @param segment
	 *            the segment or null
	 */
	protected void showCVSegmentDetails(Segment segment) {
		super.showCVSegmentDetails(segment);
		if (segment != null) {
			morasField.setText(segment.getMorasBorn());
			displayFieldsPerActiveSetting(segment);
		} else {
			morasField.setText("0");
		}
	}

	public void displayFieldsPerActiveSetting(Segment segment) {
		super.displayFieldsPerActiveSetting(segment);
		morasField.setDisable(!segment.isActive());
	}

	@Override
	public void setSegment(Segment segment) {
		super.setSegment(segment);
		morasField.setText(segment.getMorasBorn());
	}
	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	protected TextField[] createTextFields() {
		return new TextField[] { segmentField, graphemesField, descriptionField, morasField };
	}
}
