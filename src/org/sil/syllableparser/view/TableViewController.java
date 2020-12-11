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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Andy Black
 *
 */
public abstract class TableViewController extends SylParserBaseController {

	ObservableList<? extends SylParserBase> list;
	TableView<? extends SylParserBase> tableView;
	protected String sApproach = "";
	protected  ApplicationPreferences prefs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (TableColumn<? extends SylParserBase, ?> column: tableView.getColumns()) {
			  column.widthProperty().addListener(new ChangeListener<Number>() {
			    @Override
			      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
			        prefs.setPreferencesKey(sApproach + column.getId(), newWidth.doubleValue());
			    }
			  });
			}

	}

	public void setApproach(String sApproach) {
		this.sApproach = sApproach;
	}

	public void setTableView(TableView<? extends SylParserBase> tableView) {
		this.tableView = tableView;
	}

	protected void initializeTableColumnWidths(ApplicationPreferences prefs) {
        this.prefs = prefs;
		for (TableColumn<? extends SylParserBase, ?> column : tableView.getColumns()) {
			Double d = prefs.getDoubleValue(sApproach + column.getId(), column.getPrefWidth());
			column.setPrefWidth(d);
		}
	}

}
