/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public abstract class SylParserObject {

	protected String id;
	
	@XmlAttribute(name="id")
	@XmlID
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	protected void createUUID() {
		UUID uuid = UUID.randomUUID();
		setID(uuid.toString());
	}

	public static int findIndexInSylParserObjectListByUuid(ObservableList<SylParserObject> list,
			String uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
		int index = -1;
		for (SylParserObject sylParserObject : list) {
			index++;
			if (sylParserObject.getID().equals(uuid)) {
				return index;
			}
		}
		return -1;
	}
}
