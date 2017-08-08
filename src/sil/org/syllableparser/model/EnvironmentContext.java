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
	private final SimpleListProperty<GraphemeOrNaturalClass> graphemesAndNaturalClasses;
	private final StringProperty envContextRepresentation;
	ObservableList<GraphemeOrNaturalClass> envContext = FXCollections.observableArrayList();
	
	public EnvironmentContext() {
		this.graphemesAndNaturalClasses = new SimpleListProperty<GraphemeOrNaturalClass>();
		this.envContextRepresentation = new SimpleStringProperty("");
	}

	public EnvironmentContext(SimpleListProperty<GraphemeOrNaturalClass> graphemesAndNaturalClasses, 
			String envContextRepresentation) {
		super();
		this.graphemesAndNaturalClasses = new SimpleListProperty<GraphemeOrNaturalClass>(graphemesAndNaturalClasses);
		this.envContextRepresentation = new SimpleStringProperty(envContextRepresentation);
	}

	public ObservableList<GraphemeOrNaturalClass> getGraphemesAndNaturalClasses() {
		return graphemesAndNaturalClasses;
	}

	public ObservableList<GraphemeOrNaturalClass> getEnvContext() {
		return envContext;
	}

	@XmlElement(name="envContext")
	public void setEnvContext(ObservableList<GraphemeOrNaturalClass> envContext) {
		this.envContext = envContext;
	}

	public SimpleListProperty<GraphemeOrNaturalClass> getGraphemesAndNaturalClassesProperty() {
		return graphemesAndNaturalClasses;
	}

	public void setGraphemesAndNaturalClasses(ObservableList<GraphemeOrNaturalClass> graphemesAndNaturalClasses) {
		this.graphemesAndNaturalClasses.set(graphemesAndNaturalClasses);
	}

	public String getEnvironmentContextRepresentation() {
		return envContextRepresentation.get();
	}
	public StringProperty envContextRepresentationProperty() {
		return envContextRepresentation;
	}
	public void setEnvironmentContextRepresentation(String environmentContextRepresentation) {
		this.envContextRepresentation.set(environmentContextRepresentation);
	}
}
