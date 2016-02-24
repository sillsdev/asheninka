/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */
public abstract class CheckBoxColumnController extends SylParserBaseController {
	
	@FXML
	protected CheckBox checkBoxColumnHead;
	protected ContextMenu checkBoxContextMenu = new ContextMenu();
	protected MenuItem selectAll = new MenuItem("Select All");
	protected MenuItem clearAll = new MenuItem("Clear All");
	protected MenuItem toggle = new MenuItem("Toggle");
	
	abstract protected void handleCheckBoxSelectAll();

	abstract protected void handleCheckBoxClearAll();

	abstract protected void handleCheckBoxToggle();


	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCut()
	 */
	@Override
	void handleCut() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCopy()
	 */
	@Override
	void handleCopy() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handlePaste()
	 */
	@Override
	void handlePaste() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// TODO Auto-generated method stub

	}

	protected void initializeCheckBoxContextMenu(ResourceBundle bundle) {
		// set up context menu
		selectAll.setOnAction((event) -> {
			handleCheckBoxSelectAll();
		});
		clearAll.setOnAction((event) -> {
			handleCheckBoxClearAll();
		});
		toggle.setOnAction((event) -> {
			handleCheckBoxToggle();
		});
		selectAll.setText(bundle.getString("checkbox.context.menu.selectall"));
		clearAll.setText(bundle.getString("checkbox.context.menu.clearall"));
		toggle.setText(bundle.getString("checkbox.context.menu.toggle"));
		checkBoxContextMenu.getItems().addAll(selectAll, clearAll, toggle);
		checkBoxColumnHead.setContextMenu(checkBoxContextMenu);
	}

	/**
	 * Called when the user clicks on the check box column header
	 */
	@FXML
	protected void handleCheckBoxColumnHead() {
		// make sure the check box stays checked
		checkBoxColumnHead.setSelected(true);
		// show the check box context menu
		checkBoxColumnHead.contextMenuProperty().get()
				.show(checkBoxColumnHead, Side.BOTTOM, 0.0, 0.0);
	}

}
