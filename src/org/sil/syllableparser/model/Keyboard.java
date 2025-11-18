/**
 * Copyright (c) 2025 SIL Global
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.model;

import java.util.Locale;

import org.sil.utility.service.keyboards.KeyboardInfo;

/**
 * @author Andy Black
 * Singleton pattern for keyboard information for gloss keyboard
 *
 */
public class Keyboard extends KeyboardInfo {

    private static Keyboard instance;
    
    private Keyboard(){
    	super(new Locale("en"), "English", 0);
    }
    
    public static Keyboard getInstance(){
        if(instance == null){
            instance = new Keyboard();
        }
        return instance;
    }

}
