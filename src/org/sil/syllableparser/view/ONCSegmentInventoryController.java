// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;

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
		
		onsetColumn.setCellValueFactory(cellData -> {
			Segment segment = cellData.getValue();
			BooleanProperty bp = segment.onsetProperty();
			cellData.getValue().setOnset(bp.get());
			return bp;
		});
		onsetColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onsetColumn));
		onsetColumn.setEditable(true);

		nucleusColumn.setCellValueFactory(cellData -> {
			Segment segment = cellData.getValue();
			BooleanProperty bp = segment.nucleusProperty();
			cellData.getValue().setNucleus(bp.get());
			return bp;
		});
		nucleusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(nucleusColumn));
		nucleusColumn.setEditable(true);

		codaColumn.setCellValueFactory(cellData -> {
			Segment segment = cellData.getValue();
			BooleanProperty bp = segment.codaProperty();
			cellData.getValue().setCoda(bp.get());
			return bp;
		});
		codaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(codaColumn));
		codaColumn.setEditable(true);

		
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

	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		populateSegmentTable(ApproachType.CV);
	}

	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();
		populateSegmentTable(ApproachType.SONORITY_HIERARCHY);
	}

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		populateSegmentTable(ApproachType.ONSET_NUCLEUS_CODA);
	}
}
