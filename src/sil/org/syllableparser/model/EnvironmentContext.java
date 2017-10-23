// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import sil.org.syllableparser.model.SylParserObject;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * an Object Value
 */
public class EnvironmentContext {
	private SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass> graphemesAndNaturalClasses;
	private String envContextRepresentation;
	ObservableList<EnvironmentContextGraphemeOrNaturalClass> envContext = FXCollections.observableArrayList();
	private boolean wordBoundary = false;
	
	public EnvironmentContext() {
		this.graphemesAndNaturalClasses = new SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass>();
		this.envContextRepresentation = "";
	}

	public EnvironmentContext(SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass> graphemesAndNaturalClasses, 
			String envContextRepresentation, boolean wordBoundary) {
		super();
		this.graphemesAndNaturalClasses = new SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass>(graphemesAndNaturalClasses);
		this.envContextRepresentation = envContextRepresentation;
		this.wordBoundary = wordBoundary;
	}

	public ObservableList<EnvironmentContextGraphemeOrNaturalClass> getGraphemesAndNaturalClasses() {
		return graphemesAndNaturalClasses;
	}

	public ObservableList<EnvironmentContextGraphemeOrNaturalClass> getEnvContext() {
		return envContext;
	}

	@XmlElement(name="envContext")
	public void setEnvContext(ObservableList<EnvironmentContextGraphemeOrNaturalClass> envContext) {
		this.envContext = envContext;
	}

	public SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass> getGraphemesAndNaturalClassesProperty() {
		return graphemesAndNaturalClasses;
	}

	public void setGraphemesAndNaturalClasses(ObservableList<EnvironmentContextGraphemeOrNaturalClass> graphemesAndNaturalClasses) {
		this.graphemesAndNaturalClasses.set(graphemesAndNaturalClasses);
	}

	public String getEnvironmentContextRepresentation() {
		return envContextRepresentation;
	}
	public void setEnvironmentContextRepresentation(String environmentContextRepresentation) {
		this.envContextRepresentation = environmentContextRepresentation;
	}

	public boolean isWordBoundary() {
		return wordBoundary;
	}

	public void setWordBoundary(boolean wordBoundary) {
		this.wordBoundary = wordBoundary;
	}
}
