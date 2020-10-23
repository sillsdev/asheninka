// Copyright (c) 2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.Segment;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;

/**
 * @author Andy Black
 *
 */

public class ONCSegmentInventoryController extends CVSegmentInventoryController {

	@FXML
	protected TableColumn<Segment, Boolean> onsetColumn;
	@FXML
	protected TableColumn<Segment, Boolean> nucleusColumn;
	@FXML
	protected TableColumn<Segment, Boolean> codaColumn;
	@FXML
	protected CheckBox onsetCheckBox;
	@FXML
	protected CheckBox nucleusCheckBox;
	@FXML
	protected CheckBox codaCheckBox;

	public ONCSegmentInventoryController() {
		super();
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		super.setApproach(ApplicationPreferences.ONC_SEGMENTS);

		onsetColumn.setCellValueFactory(cellData -> cellData.getValue().onsetProperty());
		onsetColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onsetColumn));
		onsetColumn.setEditable(true);

		nucleusColumn.setCellValueFactory(cellData -> cellData.getValue().nucleusProperty());
		nucleusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(nucleusColumn));
		nucleusColumn.setEditable(true);

		codaColumn.setCellValueFactory(cellData -> cellData.getValue().codaProperty());
		codaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(codaColumn));
		codaColumn.setEditable(true);

		cvSegmentTable.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Object o = event.getTarget();
				handleClickOnCheckBoxInTable(o);
			}

		});

		onsetCheckBox.setOnAction((event) -> {
			if (currentSegment != null) {
				currentSegment.setOnset(onsetCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentSegment);
				displayFieldsPerActiveSetting(currentSegment);
			}
		});
		nucleusCheckBox.setOnAction((event) -> {
			if (currentSegment != null) {
				currentSegment.setNucleus(nucleusCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentSegment);
				displayFieldsPerActiveSetting(currentSegment);
			}
		});
		codaCheckBox.setOnAction((event) -> {
			if (currentSegment != null) {
				currentSegment.setCoda(codaCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentSegment);
				displayFieldsPerActiveSetting(currentSegment);
			}
		});
	}

	protected void handleClickOnCheckBoxInTable(Object o) {
		if (o instanceof CheckBoxTableCell) {
			@SuppressWarnings("unchecked")
			CheckBoxTableCell<Segment, Boolean> cbtc = (CheckBoxTableCell<Segment, Boolean>) o;
			int index = cbtc.getIndex();
			if (index < 0) {
				return;
			}
			Segment segment = cvSegmentTable.getItems().get(index);
			boolean value;
			if (segment != null) {
				switch (cbtc.getId()) {
				case "onsetColumn":
					value = !segment.isOnset();
					segment.setOnset(value);
					onsetCheckBox.setSelected(value);
					break;
				case "nucleusColumn":
					value = !segment.isNucleus();
					segment.setNucleus(value);
					nucleusCheckBox.setSelected(value);
					break;
				case "codaColumn":
					value = !segment.isCoda();
					segment.setCoda(value);
					codaCheckBox.setSelected(value);
					break;
				}
			}
		}
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
			onsetCheckBox.setSelected(segment.isOnset());
			nucleusCheckBox.setSelected(segment.isNucleus());
			codaCheckBox.setSelected(segment.isCoda());
			displayFieldsPerActiveSetting(segment);
		} else {
			onsetCheckBox.setSelected(false);
			nucleusCheckBox.setSelected(false);
			codaCheckBox.setSelected(false);
		}
	}

	public void displayFieldsPerActiveSetting(Segment segment) {
		super.displayFieldsPerActiveSetting(segment);
		onsetCheckBox.setDisable(!segment.isActive());
		nucleusCheckBox.setDisable(!segment.isActive());
		codaCheckBox.setDisable(!segment.isActive());
	}

	@Override
	public void setSegment(Segment segment) {
		super.setSegment(segment);
		onsetCheckBox.setSelected(segment.isOnset());
		nucleusCheckBox.setSelected(segment.isNucleus());
		codaCheckBox.setSelected(segment.isCoda());
	}
}
