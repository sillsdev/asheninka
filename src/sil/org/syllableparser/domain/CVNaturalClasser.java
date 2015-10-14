/**
 * 
 */
package sil.org.syllableparser.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSegment;

/**
 * @author Andy Black
 *
 */
public class CVNaturalClasser {

	List<CVSegment> segmentsInCurrentWord;
	List<CVNaturalClass> naturalClasses;
	List<CVNaturalClass> naturalClassesInCurrentWord = new LinkedList<CVNaturalClass>(
			Arrays.asList(new CVNaturalClass()));
	HashMap<String, CVNaturalClass> segmentToNaturalClass = new HashMap<>();

	public CVNaturalClasser(List<CVNaturalClass> naturalClasses) {
		super();
		this.naturalClasses = naturalClasses;
		for (CVNaturalClass nc : naturalClasses) {
			setSegmentToNaturalClassMapping(nc);
		}
	}

	protected void setSegmentToNaturalClassMapping(CVNaturalClass nc) {
		for (Object snc : nc.getSegmentsOrNaturalClasses()) {
			if (snc instanceof CVSegment) {
				segmentToNaturalClass.put(((CVSegment) snc).getSegment(), nc);
			} else if (snc instanceof CVNaturalClass) {
				setSegmentToNaturalClassMapping(((CVNaturalClass) snc));
			}
		}
	}

	public List<CVSegment> getSegmentsInCurrentWord() {
		return segmentsInCurrentWord;
	}

	public void setSegmentsInCurrentWord(List<CVSegment> segmentsInCurrentWord) {
		this.segmentsInCurrentWord = segmentsInCurrentWord;
	}

	public List<CVNaturalClass> getNaturalClasses() {
		return naturalClasses;
	}

	public void setNaturalClasses(List<CVNaturalClass> naturalClasses) {
		this.naturalClasses = naturalClasses;
	}

	public List<CVNaturalClass> getNaturalClassesInCurrentWord() {
		return naturalClassesInCurrentWord;
	}

	public void setNaturalClassesInCurrentWord(
			List<CVNaturalClass> naturalClassesInCurrentWord) {
		this.naturalClassesInCurrentWord = naturalClassesInCurrentWord;
	}

	public HashMap<String, CVNaturalClass> getSegmentToNaturalClass() {
		return segmentToNaturalClass;
	}

	public void setSegmentToNaturalClass(
			HashMap<String, CVNaturalClass> segmentToNaturalClass) {
		this.segmentToNaturalClass = segmentToNaturalClass;
	}

	public boolean convertSegmentsToNaturalClasses(
			List<CVSegment> segmentsInCurrentWord) {
		// TODO: allow for nested natural classes in a natural class
		naturalClassesInCurrentWord.clear();

		for (CVSegment seg : segmentsInCurrentWord) {
			String sTemp = seg.getSegment();
			CVNaturalClass nc = segmentToNaturalClass.get(sTemp);
			if (nc == null) {
				return false;
			}
			naturalClassesInCurrentWord.add(nc);
		}

		return true;

		// m_pSegments->First(); // For each segment in the word
		// while (m_pSegments->bIsInList())
		// {
		// CSegment * pSegment;
		// COrthForm * pOrthForm;
		// pSegment = m_pSegments->GetCurrentSegment();
		// pOrthForm = m_pSegments->GetCurrentOrthForm();
		// // Create an instance of an element of
		// // the pattern.
		// pCVElement = new CCVPatternElement();
		// // Set the segment reference to the
		// // appropriate segment.
		// pCVElement->SetSegment(pSegment, pOrthForm);
		// // Set the CV type reference to the CV type
		// // of the segment.
		// CNaturalClassContainer * pNatClasses =
		// pCVApproach->GetNaturalClasses()->GetNaturalClasses();
		// pNatClasses->initializeIterator();
		// while (!pNatClasses->isIteratorAtEnd())
		// {
		// CNaturalClass * pNatClass = (CNaturalClass *)pNatClasses->element();
		// if (pNatClass->GetEnabled() &&
		// pNatClass->bHasSegment(pSegment))
		// pCVElement->AddNaturalClassToCVTypes(pNatClass);
		// pNatClasses->incrementIterator();
		// }
		// // Set any word-initial values.
		// if (m_pSegments->bIsFirst())
		// pCVElement->SetWordPosition(CCVPatternElement::eWordPosition::eInitial);
		// // Set any word-final values.
		// if (m_pSegments->bIsLast())
		// pCVElement->SetWordPosition(CCVPatternElement::eWordPosition::eFinal);
		// pCVPatternList->insert(pCVElement);
		// // Go to next segment in list
		// m_pSegments->Next();
		// }

	}
}
