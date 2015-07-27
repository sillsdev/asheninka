/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public abstract class SylParserObject {

	protected UUID uuid;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	public static int findIndexInSylParserObjectListByUuid(ObservableList<SylParserObject> list,
			UUID uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
		int index = -1;
		for (SylParserObject sylParserObject : list) {
			index++;
			if (sylParserObject.getUuid().equals(uuid)) {
				return index;
			}
		}
		return -1;
	}
}
