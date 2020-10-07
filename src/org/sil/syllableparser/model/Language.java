// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.sil.lingtree.model.ColorXmlAdaptor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.NodeOrientation;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * @author Andy Black
 *
 */
//@XmlAccessorType(XmlAccessType.FIELD)
public class Language  {

	private Font font;
	private String fontFamily;
	private double fontSize;
	private String fontType;
	private Color color;
	private BooleanProperty rightToLeft;
	private SortingOption sortingOption;
	protected final StringProperty code;
	protected final StringProperty ldmlFile;
	protected final StringProperty useSameLanguage;
	protected final StringProperty icuRules;
	protected NodeOrientation orientation;

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

	@XmlJavaTypeAdapter(ColorXmlAdaptor.class)
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getCode() {
		return code.get();
	}

	public StringProperty codeProperty() {
		return code;
	}

	public void setCode(String code) {
		this.code.set(code);
	}

	public BooleanProperty RightToLeftProperty() {
		return rightToLeft;
	}

	public void setRightToLeft(boolean value) {
		this.rightToLeft.set(value);
	}

	public SortingOption getSortingOption() {
		return sortingOption;
	}

	public void setSortingOption(SortingOption sortingOption) {
		this.sortingOption = sortingOption;
	}

	public boolean isRightToLeft() {
		return rightToLeft.get();
	}

	public String getIcuRules() {
		return icuRules.get();
	}

	public StringProperty icuRulesProperty() {
		return icuRules;
	}

	public void setIcuRules(String rules) {
		this.icuRules.set(rules);
	}

	public String getLdmlFile() {
		return ldmlFile.get();
	}

	public StringProperty ldmlFileProperty() {
		return ldmlFile;
	}

	public void setLdmlFile(String file) {
		this.ldmlFile.set(file);
	}

	public String getUseSameLanguage() {
		return useSameLanguage.get();
	}

	public StringProperty useSameLanguageProperty() {
		return code;
	}

	public void setUseSameLanguage(String option) {
		this.useSameLanguage.set(option);
	}

	public NodeOrientation getOrientation() {
		return isRightToLeft() ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
	}

	public Language(String fontFamily, double fontSize, String fontType, SortingOption option, String code, String icuRules, String useSameLanguage, String ldmlFile) {
		super();
		this.fontFamily = fontFamily;
		this.fontSize = fontSize;
		this.fontType = fontType;
		font = createFont(fontFamily, fontSize, fontType);
		rightToLeft = new SimpleBooleanProperty(false);
		sortingOption = option;
		this.code = new SimpleStringProperty(code);
		this.icuRules = new SimpleStringProperty(icuRules);
		this.useSameLanguage = new SimpleStringProperty(useSameLanguage);
		this.ldmlFile = new SimpleStringProperty(ldmlFile);
	}

	public Language() {
		fontFamily = "System";
		fontSize = 12.0;
		fontType = "Regular";
		font = createFont(fontFamily, fontSize, fontType);
		rightToLeft = new SimpleBooleanProperty(false);
		sortingOption = SortingOption.DEFAULT_ORDER;
		code = new SimpleStringProperty("");
		ldmlFile = new SimpleStringProperty("");
		icuRules = new SimpleStringProperty("");
		useSameLanguage = new SimpleStringProperty("");
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

	public String getAnyIcuRules() {
		if (sortingOption == SortingOption.DEFAULT_ORDER) {
			return "";
		}
		return getIcuRules();
	}
}
