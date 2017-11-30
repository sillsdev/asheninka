// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Andy Black
 *
 */
public class HandleExceptionMessage {

	/**
	 * @param sErrorKind
	 * @param sHeader
	 * @param sContent
	 * @param fStop
	 */
	public static void show(String sErrorKind, String sHeader, String sContent, boolean fStop) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(sErrorKind);
		alert.setHeaderText(sHeader);
		alert.setContentText(sContent);

		alert.showAndWait();
		if (fStop) {
			System.exit(1);
		}
	}
}
