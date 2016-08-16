/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class DifferentWord extends DifferentSylParserObject {

	public DifferentWord(Word wordFrom1, Word wordFrom2) {
		super((SylParserObject)wordFrom1, (SylParserObject)wordFrom2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DifferentWord other = (DifferentWord) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((Word) objectFrom1).getWord().equals(((Word) other.objectFrom1).getWord()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((Word) objectFrom2).getWord().equals(((Word) other.objectFrom2).getWord()))
			return false;
		return true;
	}
}
