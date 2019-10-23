// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * an Object Value
 */
public class EnvironmentContext {
	private SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass> graphemesAndNaturalClasses;
	ObservableList<EnvironmentContextGraphemeOrNaturalClass> envContext = FXCollections.observableArrayList();
	private boolean wordBoundary = false;
	
	public EnvironmentContext() {
		this.graphemesAndNaturalClasses = new SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass>();
	}

	public EnvironmentContext(SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass> graphemesAndNaturalClasses, 
			boolean wordBoundary) {
		super();
		this.graphemesAndNaturalClasses = new SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass>(graphemesAndNaturalClasses);
		this.wordBoundary = wordBoundary;
	}

	public ObservableList<EnvironmentContextGraphemeOrNaturalClass> getGraphemesAndNaturalClasses() {
		return graphemesAndNaturalClasses;
	}

	public ObservableList<EnvironmentContextGraphemeOrNaturalClass> getEnvContext() {
		return envContext;
	}
	@XmlElementWrapper(name = "environmentContexts")
	@XmlElement(name="envContextGOrNC")
	public void setEnvContext(ObservableList<EnvironmentContextGraphemeOrNaturalClass> envContext) {
		this.envContext = envContext;
	}

	public SimpleListProperty<EnvironmentContextGraphemeOrNaturalClass> getGraphemesAndNaturalClassesProperty() {
		return graphemesAndNaturalClasses;
	}

	public void setGraphemesAndNaturalClasses(ObservableList<EnvironmentContextGraphemeOrNaturalClass> graphemesAndNaturalClasses) {
		this.graphemesAndNaturalClasses.set(graphemesAndNaturalClasses);
	}

	public boolean isWordBoundary() {
		return wordBoundary;
	}

	public void setWordBoundary(boolean wordBoundary) {
		this.wordBoundary = wordBoundary;
	}
}
