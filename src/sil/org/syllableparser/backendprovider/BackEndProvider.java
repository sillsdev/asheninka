/**
 * 
 */
package sil.org.syllableparser.backendprovider;

import java.io.File;

import sil.org.syllableparser.MainApp;


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