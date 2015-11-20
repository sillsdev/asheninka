/**
 * 
 */
package sil.org.syllableparser.view;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Andy Black
 *
 */
public class ControllerUtilities {
	public static void createToolbarButtonWithImage(String sUrl, Button buttonToolbar,
			Tooltip buttonTooltip, String sTooltip) {
		ImageView imageView = new ImageView();
		Image icon = new Image("file:src/sil/org/syllableparser/resources/images/" + sUrl);
		imageView.setImage(icon);
		buttonToolbar.setGraphic(imageView);
		buttonTooltip = new Tooltip(sTooltip);
		buttonToolbar.setTooltip(buttonTooltip);
	}
}
