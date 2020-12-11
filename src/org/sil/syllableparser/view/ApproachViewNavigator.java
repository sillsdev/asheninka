// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.MainApp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * @author Andy Black
 * but thoroughly based on VitaNavigator.java by jewelsea from
 * https://gist.github.com/jewelsea/6460130#file-main-java
 * Accessed 21-April-2015
 * Small JavaFX framework for swapping in and out child panes in a main FXML container. Code is for Java 8+.
 *
 */
/**
 * Utility class for controlling navigation between approach views.
 *
 * All methods on the navigator are static to facilitate
 * simple access from anywhere in the application.
 */
public class ApproachViewNavigator {
    /** The main application layout controller. */
    private static RootLayoutController rootLayoutController;
 
    /**
     * Stores the main controller for later use in navigation tasks.
     *
     * @param mainController the main application layout controller.
     */
    public static void setMainController(RootLayoutController mainController) {
    	ApproachViewNavigator.rootLayoutController = mainController;
    }
 
    /**
     * Loads the vista specified by the fxml file into the
     * vistaHolder pane of the main application layout.
     *
     * Previously loaded vista for the same fxml file are not cached.
     * The fxml is loaded anew and a new vista node hierarchy generated
     * every time this method is invoked.
     *
     * A more sophisticated load function could potentially add some
     * enhancements or optimizations, for example:
     *   cache FXMLLoaders
     *   cache loaded vista nodes, so they can be recalled or reused
     *   allow a user to specify vista node reuse or new creation
     *   allow back and forward history like a browser
     *
     * @param fxml the fxml file to be loaded.
     */
    public static void loadApproachView(FXMLLoader loader, String fxml, Locale locale) {
        try {

        	//FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ApproachViewNavigator.class.getResource(fxml));
            loader.setResources(ResourceBundle.getBundle("org.sil.syllableparser.resources.SyllableParser", locale));
        	
        	Node node = loader.load();
        			rootLayoutController.setApproachView(node //loader.load()
//                FXMLLoader.load(
//                		ApproachViewNavigator.class.getResource(
//                        fxml
//                   )
//                )
            );
        } catch (IOException e) {
            e.printStackTrace();
			MainApp.reportException(e, null);
        }
    }

}
