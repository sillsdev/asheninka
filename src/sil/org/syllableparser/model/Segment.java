// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * @author Andy Black
 *
 * an Entity
 */
public class Segment extends SylParserObject {
	private final StringProperty segment;
	//private final StringProperty graphemes;
	private final StringProperty description;
	private final SimpleListProperty<Grapheme> graphemes;	
	ObservableList<Grapheme> graphs = FXCollections.observableArrayList();
	private final StringProperty graphsRepresentation;
	
	public Segment() {
		super();
		this.segment = new SimpleStringProperty("");
		this.graphemes = new SimpleListProperty<Grapheme>();
		this.description = new SimpleStringProperty("");
		this.graphsRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public Segment(String segment, String description, String graphsRepresentation) {
		super();
		this.segment = new SimpleStringProperty(segment);
		this.graphemes = new SimpleListProperty<Grapheme>();
		this.description = new SimpleStringProperty(description);
		this.graphsRepresentation = new SimpleStringProperty(graphsRepresentation);
		createUUID();
	}

	public String getSegment() {
		return segment.get().trim();
	}

	public StringProperty segmentProperty() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment.set(segment);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
	
	@XmlElementWrapper(name = "graphemes")
	@XmlElement(name = "grapheme")
	public ObservableList<Grapheme> getGraphemes() {
		return graphs;
	}

	public void setGraphemes(ObservableList<Grapheme> graphs) {
		this.graphs = graphs;
	}

	public SimpleListProperty<Grapheme> graphemesProperty() {
		return graphemes;
	}

	public String getGraphsRepresentation() {
		return graphsRepresentation.get();
	}
	public StringProperty graphsRepresentationProperty() {
		return graphsRepresentation;
	}
	public void setGraphsRepresentation(String graphsRepresentation) {
		this.graphsRepresentation.set(graphsRepresentation);
	}

	public static int findIndexInSegmentsListByUuid(ObservableList<Segment> list,
			String uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
		int index = -1;
		for (SylParserObject sylParserObject : list) {
			index++;
			if (sylParserObject.getID() == uuid) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public int hashCode() {
		String sCombo = segment.getValueSafe() + graphsRepresentation.getValueSafe();
		return sCombo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		boolean result = true;
		Segment seg = (Segment) obj;
		if (!getSegment().equals(seg.getSegment())) {
			result = false;
		} else {
			if (!getGraphemes().equals(seg.getGraphemes())) {
				result = false;
			}
		}
		return result;
	}
}
