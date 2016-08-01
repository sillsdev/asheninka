/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	private final StringProperty graphemes;
	private final StringProperty description;
	public Segment() {
		super();
		this.segment = new SimpleStringProperty("");
		this.graphemes = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		createUUID();
	}

	public Segment(String segment, String graphemes, String description) {
		super();
		this.segment = new SimpleStringProperty(segment);
		this.graphemes = new SimpleStringProperty(graphemes);
		this.description = new SimpleStringProperty(description);
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

	public String getGraphemes() {
		return graphemes.get();
	}

	public StringProperty graphemesProperty() {
		return graphemes;
	}

	public void setGraphemes(String graphemes) {
		this.graphemes.set(graphemes);
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
		String sCombo = segment.getValueSafe() + graphemes.getValueSafe();
		return sCombo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
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
