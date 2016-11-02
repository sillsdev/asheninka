// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javax.xml.bind.annotation.XmlTransient;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * @author Andy Black
 *
 */
//@XmlAccessorType(XmlAccessType.FIELD)
public class Language {

	private Font font;
	private String fontFamily;
	private double fontSize;
	private String fontType;

	/**
	 * @return the font
	 */
	//@XmlTransient
	public Font getFont() {
		return font;
	}

	/**
	 * @param font
	 *            the font to set
	 */
	@XmlTransient
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @return the fontFamily
	 */
	public String getFontFamily() {
		return fontFamily;
	}

	/**
	 * @param fontFamily
	 *            the fontFamily to set
	 */
	public void setFontFamily(String fontFamily) {
		this.font = createFont(fontFamily, this.fontSize, this.fontType);
	}

	/**
	 * @return the fontSize
	 */
	public double getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize
	 *            the fontSize to set
	 */
	public void setFontSize(double fontSize) {
		this.font = createFont(this.fontFamily, fontSize, this.fontType);
	}

	/**
	 * @return the fontType
	 */
	public String getFontType() {
		return fontType;
	}

	/**
	 * @param fontType
	 *            the fontType to set
	 */
	public void setFontType(String fontType) {
		this.font = createFont(this.fontFamily, this.fontSize, fontType);
	}

	public Language(String fontFamily, double fontSize, String fontType) {
		super();
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontType = fontType;
		font = createFont(fontFamily, fontSize, fontType);
	}

	public Language() {
		fontFamily = "System";
		fontSize = 12.0;
		fontType = "Regular";
		font = createFont(fontFamily, fontSize, fontType);
	}

	public Font createFont(String fontFamily, double fontSize, String fontType) {
		String[] types = fontType.split(" ");
		FontWeight weight = FontWeight.NORMAL;
		FontPosture posture = FontPosture.REGULAR;
		for (String value : types) {
			switch (value) {
			case "Bold":
				weight = FontWeight.BOLD;
				break;

			case "Italic":
				posture = FontPosture.ITALIC;
				break;

			default:
				break;
			}
		}
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontType = fontType;
		return Font.font(fontFamily, weight, posture, fontSize);
	}

}
