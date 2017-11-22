// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
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
 *         an Entity
 */
public class Segment extends SylParserObject {
	private final StringProperty segment;
	private final StringProperty graphemes;
	private final StringProperty description;
	private final SimpleListProperty<Grapheme> graphemesAsList;
	ObservableList<Grapheme> graphs = FXCollections.observableArrayList();

	// private final StringProperty graphsRepresentation;

	public Segment() {
		super();
		this.segment = new SimpleStringProperty("");
		this.graphemes = new SimpleStringProperty("");
		this.graphemesAsList = new SimpleListProperty<Grapheme>();
		this.description = new SimpleStringProperty("");
		// this.graphsRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public Segment(String segment, String graphsRepresentation, String description) {
		super();
		this.segment = new SimpleStringProperty(segment);
		this.graphemes = new SimpleStringProperty(graphsRepresentation);
		this.graphemesAsList = new SimpleListProperty<Grapheme>();
		this.description = new SimpleStringProperty(description);
		// this.graphsRepresentation = new
		// SimpleStringProperty(graphsRepresentation);
		createUUID();
	}

	public Segment(String segment, String description, SimpleListProperty<Grapheme> graphemeList) {
		super();
		this.segment = new SimpleStringProperty(segment);
		// this.graphemes = new SimpleStringProperty(graphsRepresentation);
		this.graphemesAsList = new SimpleListProperty<Grapheme>();
		this.description = new SimpleStringProperty(description);
		String graphs = graphemeList.stream().map(Grapheme::getForm)
				.collect(Collectors.joining(" "));
		this.graphemes = new SimpleStringProperty(graphs);
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

	// public String getGraphemes() {
	// return graphemes.get().trim();
	// }
	//
	// public StringProperty graphemesProperty() {
	// return graphemes;
	// }
	//
	// public void setGraphemes(String graphemes) {
	// this.graphemes.set(graphemes);
	// }

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	@XmlElementWrapper(name = "graphemesAsList")
	@XmlElement(name = "grapheme")
	public ObservableList<Grapheme> getGraphs() {
		return graphs;
	}

	public void setGraphs(ObservableList<Grapheme> graphs) {
		this.graphs = graphs;
		// TODO: can we do this with lambdas?
		for	(Grapheme graph : graphs) {
			graph.setOwningSegment(this);
		}
	}
	
	public List<Grapheme> getActiveGraphs() {
		return graphs.stream().filter(grapheme -> grapheme.isActive()).collect(Collectors.toList());
	}

	public SimpleListProperty<Grapheme> graphemesListProperty() {
		return graphemesAsList;
	}

	public String getGraphemes() {
		return graphemes.get();
	}

	public StringProperty graphemesProperty() {
		return graphemes;
	}

	public void setGraphemes(String graphsRepresentation) {
		this.graphemes.set(graphsRepresentation);
	}

	public void updateGraphemes() {
		String graphsRepresentation = graphemes.getValue();
		// Remove any active graphemes which are no longer in graphemes
		List<String> graphemesInRepresentation = new LinkedList<String>(
				Arrays.asList(graphsRepresentation.split(" +")));
		List<Grapheme> missingGraphs = graphs
				.stream()
				.filter(graph -> graph.isActive()
						&& !graphemesInRepresentation.contains(graph.getForm()))
				.collect(Collectors.toList());
		if (missingGraphs.size() > 0) {
			for (Grapheme grapheme : missingGraphs) {
				graphs.remove(grapheme);
			}
		}

		// add any missing grapheme forms that the user added to graphemes
		Iterator<String> iter = graphemesInRepresentation.iterator();
		while (iter.hasNext()) {
			String graphemeForm = iter.next();
			if (graphs.stream().filter(graph -> graph.getForm().equals(graphemeForm))
					.collect(Collectors.toList()).size() > 0) {
				iter.remove();
			}
		}
		for (String graphemeForm : graphemesInRepresentation) {
			if (graphemeForm.trim().length() > 0) {
				Grapheme newGrapheme = new Grapheme();
				newGrapheme.setForm(graphemeForm);
				newGrapheme.setOwningSegment(this);
				graphs.add(newGrapheme);
			}
		}
	}

	@Override
	public int hashCode() {
		String sCombo = segment.getValueSafe() + graphemes.getValueSafe();
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
			if (!getActiveGraphs().equals(seg.getActiveGraphs())) {
				result = false;
			}
		}
		return result;
	}
}
