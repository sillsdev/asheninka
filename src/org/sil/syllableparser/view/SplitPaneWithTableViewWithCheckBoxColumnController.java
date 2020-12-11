/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.SylParserBase;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */
public abstract class SplitPaneWithTableViewWithCheckBoxColumnController extends TableViewWithCheckBoxColumnController {

	@FXML
	SplitPane splitPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		splitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue) {
		        ApplicationPreferences prefs = mainApp.getApplicationPreferences();
				prefs.setPreferencesKey(sApproach + splitPane.getId(), newValue.doubleValue());
			}
		});
	}

	protected void initializeTableColumnWidthsAndSplitDividerPosition() {
		ApplicationPreferences prefs = mainApp.getApplicationPreferences();
		Double d = prefs.getDoubleValue(sApproach + splitPane.getId(), .4);
		splitPane.getDividers().get(0).setPosition(d);
		initializeTableColumnWidths(prefs);
	}
}
