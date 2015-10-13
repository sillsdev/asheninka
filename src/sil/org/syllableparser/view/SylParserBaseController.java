/**
 * 
 */
package sil.org.syllableparser.view;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/**
 * @author Andy Black
 *
 */
public abstract class SylParserBaseController extends ApproachController implements Initializable {

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

}
