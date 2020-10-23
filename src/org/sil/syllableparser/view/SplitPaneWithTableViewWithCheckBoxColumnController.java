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
public abstract class SplitPaneWithTableViewWithCheckBoxColumnController extends CheckBoxColumnController {

	@FXML
	SplitPane splitPane;

	ObservableList<? extends SylParserBase> list;
	TableView<? extends SylParserBase> tableView;
	protected String sApproach = "";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (TableColumn<? extends SylParserBase, ?> column: tableView.getColumns()) {
			  column.widthProperty().addListener(new ChangeListener<Number>() {
			    @Override
			      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
			        ApplicationPreferences prefs = mainApp.getApplicationPreferences();
			        prefs.setPreferencesKey(sApproach + column.getId(), newWidth.doubleValue());
			    }
			  });
			}

		splitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue) {
		        ApplicationPreferences prefs = mainApp.getApplicationPreferences();
				prefs.setPreferencesKey(sApproach + splitPane.getId(), newValue.doubleValue());
			}
		});
	}

	public void setApproach(String sApproach) {
		this.sApproach = sApproach;
	}

	public void setTableView(TableView<? extends SylParserBase> tableView) {
		this.tableView = tableView;
	}

	protected void initializeTableColumnWidthsAndSplitDividerPosition() {
		ApplicationPreferences prefs = mainApp.getApplicationPreferences();
		Double d = prefs.getDoubleValue(sApproach + splitPane.getId(), .4);
		splitPane.getDividers().get(0).setPosition(d);
		for (TableColumn<? extends SylParserBase, ?> column : tableView.getColumns()) {
			d = prefs.getDoubleValue(sApproach + column.getId(), column.getPrefWidth());
			column.setPrefWidth(d);
		}
	}

}
