/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVSegment {
	private final StringProperty segment;
	private final StringProperty graphemes;
	private final StringProperty comment;

	public CVSegment(String segment, String graphemes, String comment) {
		super();
		this.segment = new SimpleStringProperty(segment);
		this.graphemes = new SimpleStringProperty(graphemes);
		this.comment = new SimpleStringProperty(comment);
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

	public String getComment() {
		return comment.get();
	}

	public StringProperty commentPropery() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment.set(comment);
	}

}
