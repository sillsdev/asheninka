// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.Comparator;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

import com.ibm.icu.text.RuleBasedCollator;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Andy Black
 *
 */
public abstract class SylParserBaseController extends ApproachEditorController implements Initializable {

	protected LanguageProject languageProject;
	protected CVApproach cvApproach;
	protected SHApproach shApproach;
	protected ONCApproach oncApproach;
	protected String sICURules = "";
	protected RuleBasedCollator collatorViaRules;

	protected void makeColumnHeaderWrappable(@SuppressWarnings("rawtypes") TableColumn col) {
		Label label = new Label(col.getText());
		label.setStyle("-fx-padding: 8px;");
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
	
		StackPane stack = new StackPane();
		stack.getChildren().add(label);
		stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
		label.prefWidthProperty().bind(stack.prefWidthProperty());
		col.setGraphic(stack);
	}

	protected int adjustIndexValue(int value, int max) {
		if (value >= max) {
			value = max-1;
		} else if (value < 0) {
			value = 0;
		}
		return value;
	}

	protected void handleInsertNewItem(ObservableList<? extends SylParserObject> list,
			TableView<? extends SylParserObject> tableView) {
		int i = list.size() - 1;
		tableView.requestFocus();
		tableView.getSelectionModel().select(i);
		tableView.getFocusModel().focus(i);
		tableView.scrollTo(i);
	}

	protected void handleRemoveItem(ObservableList<? extends SylParserObject> list,
			SylParserObject obj, TableView<? extends SylParserObject> tableView) {
		int i = list.indexOf(obj);
		obj = null;
		if (i >= 0) {
			list.remove(i);
			int max = tableView.getItems().size();
			i = adjustIndexValue(i, max);
			// select the last one used
			tableView.requestFocus();
			tableView.getSelectionModel().select(i);
			tableView.getFocusModel().focus(i);
			tableView.scrollTo(i);
		}
		tableView.refresh();
	}

	protected void handlePreviousItem(ObservableList<? extends SylParserObject> list,
			SylParserObject obj, TableView<? extends SylParserObject> tableView) {
		int i = list.indexOf(obj) - 1;
		if (i >= 0) {
			tableView.requestFocus();
			tableView.getSelectionModel().select(i);
			tableView.getFocusModel().focus(i);
			tableView.scrollTo(i);
		}
		tableView.refresh();
	}
	protected void handleNextItem(ObservableList<? extends SylParserObject> list,
			SylParserObject obj, TableView<? extends SylParserObject> tableView) {
		int i = list.indexOf(obj) + 1;
		if (i < tableView.getItems().size()) {
			tableView.requestFocus();
			tableView.getSelectionModel().select(i);
			tableView.getFocusModel().focus(i);
			tableView.scrollTo(i);
		}
		tableView.refresh();
	}

	protected void setColumnICURules(TableColumn<? extends SylParserObject, String> tableColumn, String sICURules) {
		try {
			collatorViaRules = new RuleBasedCollator(sICURules);
			Comparator<String> comparatorViaRules = Comparator.comparing(String::toString,
					collatorViaRules);
			tableColumn.setComparator((String s1, String s2) -> {
				return comparatorViaRules.compare(s1, s2);
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void processAnalysisTableCell(TableCell<? extends SylParserObject, String> cell, Text text, String item, boolean empty) {
		Color textColor = languageProject.getAnalysisLanguage().getColor();
		Font fontToUse = languageProject.getAnalysisLanguage().getFont();
		processCell(cell, item, empty, textColor, fontToUse);
	}

	protected void processCell(TableCell<? extends SylParserObject, String> cell, String item,
			boolean empty, Color textColor, Font fontToUse) {
		Text text;
		if (item == null || empty) {
			cell.setText(null);
			cell.setStyle("");
		} else {
			cell.setStyle("");
			text = new Text(item.toString());
			// Get it to wrap.
			text.wrappingWidthProperty().bind(cell.getTableColumn().widthProperty());
			SylParserObject obj = (SylParserObject) cell.getTableRow().getItem();
			if (obj != null && obj.isActive()) {
				if (textColor != null) {
					text.setFill(textColor);
				} else {
					text.setFill(Constants.ACTIVE);
				}
			} else {
				text.setFill(Constants.INACTIVE);
			}
			if (fontToUse != null) {
				text.setFont(fontToUse);
			}
			cell.setGraphic(text);
		}
	}

	protected void processTableCell(TableCell<? extends SylParserObject, String> cell, Text text, String item, boolean empty) {
		Font fontToUse = languageProject.getAnalysisLanguage().getFont();
		processCell(cell, item, empty, null, fontToUse);
	}

	protected void processVernacularTableCell(TableCell<? extends SylParserObject, String> cell, Text text, String item, boolean empty) {
		Color textColor = languageProject.getVernacularLanguage().getColor();
		Font fontToUse = languageProject.getVernacularLanguage().getFont();
		processCell(cell, item, empty, textColor, fontToUse);
	}
}
