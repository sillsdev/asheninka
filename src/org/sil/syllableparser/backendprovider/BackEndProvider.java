// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.backendprovider;

import java.io.File;


/**
 * @author Andy Black
 *
 */
public abstract class BackEndProvider {
    /**
     * Loads language data from the backend storage. The current language data will
     * be replaced.
     * 
     * @param file
     */
    public abstract void loadLanguageDataFromFile(File file);

    /**
     * Saves the current language data to the backend storage.
     * 
     * @param file
     */
    public abstract void saveLanguageDataToFile(File file);

}
