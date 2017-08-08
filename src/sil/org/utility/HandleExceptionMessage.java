// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Andy Black
 *
 */
public class HandleExceptionMessage {

	public static void show(String sHeader, String sStackTrace, boolean fStop) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Found");
		alert.setHeaderText(sHeader);
		alert.setContentText(sStackTrace);

		alert.showAndWait();
		if (fStop) {
			System.exit(1);
		}
	}
}
