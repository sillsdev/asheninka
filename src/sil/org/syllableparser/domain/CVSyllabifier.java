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
public class CVSyllabifier {

//	List<CVSegment> segmentsInCurrentWord;
//	List<CVNaturalClass> naturalClassesInCurrentWord;
//	List<CVNaturalClass> syllablesInCurrentWord = new LinkedList<CVNaturalClass>(
//			Arrays.asList(new CVNaturalClass()));
//	int iLongestGrapheme = 0;
//
//	public CVSyllabifier(List<CVSegment> segmentInventory) {
//		super();
//		this.segmentInventory = segmentInventory;
//		for (CVSegment seg : segmentInventory) {
//			String[] orthographemes = seg.getGraphemes().split(" +");
//			for (String orthoform : orthographemes) {
//				if (orthoform.length() > iLongestGrapheme) {
//					iLongestGrapheme = orthoform.length();
//				}
//				graphemes.put(orthoform, seg);
//			}
//		}
//	}
//
//	public List<CVSegment> getSegmentInventory() {
//		return segmentInventory;
//	}
//
//	public void setSegmentInventory(List<CVSegment> segmentInventory) {
//		this.segmentInventory = segmentInventory;
//	}
//
//	public List<CVSegment> getSegmentsInWord() {
//		return segmentsInCurrentWord;
//	}
//
//	public void setSegments(List<CVSegment> segments) {
//		this.segmentsInCurrentWord = segments;
//	}
//
//	public HashMap<String, CVSegment> getGraphemes() {
//		return graphemes;
//	}
//
//	public void setGraphemes(HashMap<String, CVSegment> graphemes) {
//		this.graphemes = graphemes;
//	}
//
//	public boolean segmentWord(String word) {
//		segmentsInCurrentWord.clear();
//		
//		if (word == null || word.isEmpty()) {
//			return true;
//		}
//
//		int iSegLength;
//		// work way through word left to right
//		for (int iStart = 0; iStart < word.length(); iStart += iSegLength) {
//			// Look for longest match
//			for (iSegLength = iLongestGrapheme; iSegLength > 0; iSegLength--) {
//				if ((iStart + iSegLength) > word.length()) {
//					continue;
//				}
//				String sTemp = word.substring(iStart, iStart + iSegLength);
//				CVSegment seg = graphemes.get(sTemp);
//				if (seg != null) {
//					segmentsInCurrentWord.add(seg);
//					break;
//				}
//			}
//			if (iSegLength == 0) {
//				// TODO: give report of how far we got or some such so user knows
//				return false;
//			}
//		}
//		return true;
//	}
}
//bool CWord::bFindParse(CCVPatternElementContainer  *pCVPattern,
//CCVSyllablePatternContainer *pSylCVPatterns)
//{
//       // If the reference to the CV pattern of the
//       // word is at the end of the word,
//       // return success.
//if (pCVPattern->isIteratorAtEnd())
//return true;
//       // remember initial (segment) position
//CCVPatternElement * pInitialCVElement =
//(CCVPatternElement *)pCVPattern->element();
//       // For each syllable pattern in the ordered
//       // list of syllable CV patterns:
//pSylCVPatterns->initializeIterator();
//while (!pSylCVPatterns->isIteratorAtEnd())
//{                            // remember position of segment iterator
//SgmlElementPointerContainer::iterator CVIter =
//pCVPattern->currentIterator();
//CCVSyllablePattern * pSylCVPattern =
//(CCVSyllablePattern *)pSylCVPatterns->element();
//       // If the syllable pattern matches the
//       // beginning of the CV pattern of the word,
//if (pSylCVPattern->GetEnabled() &&
//bSyllablePatternMatchesCVPattern(pSylCVPattern, pCVPattern))
//{
//       // Set the word pattern reference to just
//       // past the syllable pattern that matched;
//       // (this is a side effect when
//       //  bSyllablePatternMatchesCVPattern returns
//       //  true)
//       // remember position of syllable patterns
//       // iterator
//SgmlElementPointerContainer::iterator SylIter =
//pSylCVPatterns->currentIterator();
//       // Invoke this recursive procedure using the
//       // new position of the reference and the
//       // ordered list of CV patterns as arguments.
//if (bFindParse( pCVPattern, pSylCVPatterns))
//{
//       // Set the syllable break indication in the
//       // element referred to by the reference to
//       // the CV pattern of the word;
//pInitialCVElement->SetBeginsNextSyllable(true);
//       // Return success.
//return true;
//}
//       // restore position of syllable patterns
//       // iterator and segment iterator
//pSylCVPatterns->currentIterator(SylIter);
//pCVPattern->currentIterator(CVIter);
//}
//pSylCVPatterns->incrementIterator();
//}
//       // return failure
//return false;
//}
