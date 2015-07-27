/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVSegment extends SylParserObject {
	private final StringProperty segment;
	private final StringProperty graphemes;
	private final StringProperty description;
	public CVSegment() {
		super();
		this.segment = new SimpleStringProperty("");
		this.graphemes = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		uuid = UUID.randomUUID();
	}

	public CVSegment(String segment, String graphemes, String comment) {
		super();
		this.segment = new SimpleStringProperty(segment);
		this.graphemes = new SimpleStringProperty(graphemes);
		this.description = new SimpleStringProperty(comment);
		uuid = UUID.randomUUID();
	}

	public String getSegment() {
		return segment.get();
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
	
	public static int findIndexInListByUuid(ObservableList<CVSegment> list,
			UUID uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
		int index = -1;
		for (SylParserObject sylParserObject : list) {
			index++;
			if (sylParserObject.getUuid() == uuid) {
				return index;
			}
		}
		return -1;
	}
}
