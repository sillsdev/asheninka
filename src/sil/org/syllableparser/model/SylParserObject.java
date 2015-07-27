/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;

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
}
