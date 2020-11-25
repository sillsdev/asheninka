/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.filter;

import org.sil.syllableparser.model.Word;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class WordsFilter {

	WordsFilterType wordsFilterType;
	ColumnFilterType filterType;
	boolean matchCase;
	boolean matchDiacritics;
	String textToMatch;
	String loweredTextToMatch;
	
	public WordsFilter(WordsFilterType wordsFilterType, ColumnFilterType filterType, boolean matchCase,
			boolean matchDiacritics, String textToMatch) {
		this.wordsFilterType = wordsFilterType;
		this.filterType = filterType;
		this.matchCase = matchCase;
		this.matchDiacritics = matchDiacritics;
		setTextToMatch(textToMatch);
	}

	public WordsFilterType getWordsFilterType() {
		return wordsFilterType;
	}

	public void setWordsFilterType(WordsFilterType wordsFilterType) {
		this.wordsFilterType = wordsFilterType;
	}

	public ColumnFilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(ColumnFilterType filterType) {
		this.filterType = filterType;
	}

	public boolean isMatchCase() {
		return matchCase;
	}

	public void setMatchCase(boolean matchCase) {
		this.matchCase = matchCase;
	}

	public boolean isMatchDiacritics() {
		return matchDiacritics;
	}

	public void setMatchDiacritics(boolean matchDiacritics) {
		this.matchDiacritics = matchDiacritics;
	}

	public String getTextToMatch() {
		return textToMatch;
	}

	public void setTextToMatch(String textToMatch) {
		this.textToMatch = textToMatch;
		loweredTextToMatch = textToMatch.toLowerCase();
	}

	public ObservableList<Word> applyFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (filterType) {
		case ANYWHERE:
			filteredWords = applyAnywhereFilter(words);
			break;
		case AT_END:
			filteredWords = applyAtEndFilter(words);
			break;
		case AT_START:
			filteredWords = applyAtStartFilter(words);
			break;
		case BLANKS:
			filteredWords = applyBlankFilter(words);
			break;
		case NON_BLANKS:
			filteredWords = applyNonBlankFilter(words);
			break;
		case REGULAR_EXPRESSION:
			filteredWords = applyRegularExpressionFilter(words);
			break;
		case WHOLE_ITEM:
			filteredWords = applyWholeItemFilter(words);
			break;
		default:
			break;
		}
		return filteredWords;
	}
	
	public ObservableList<Word> applyAnywhereFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (wordsFilterType) {
		case CORRECT:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCorrectSyllabification().contains(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getCorrectSyllabification()).toLowerCase().contains(loweredTextToMatch));
			}
			break;
		case CV_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCVPredictedSyllabification().contains(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getCVPredictedSyllabification()).toLowerCase().contains(loweredTextToMatch));
			}
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getONCPredictedSyllabification().contains(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getONCPredictedSyllabification()).toLowerCase().contains(loweredTextToMatch));
			}
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getSHPredictedSyllabification().contains(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getSHPredictedSyllabification()).toLowerCase().contains(loweredTextToMatch));
			}
			break;
		case WORDS:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getWord().contains(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getWord()).toLowerCase().contains(loweredTextToMatch));
			}
			break;
		default:
			break;
		}
		return filteredWords;
	}

	public ObservableList<Word> applyAtEndFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (wordsFilterType) {
		case CORRECT:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCorrectSyllabification().endsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getCorrectSyllabification()).toLowerCase().endsWith(loweredTextToMatch));
			}
			break;
		case CV_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCVPredictedSyllabification().endsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getCVPredictedSyllabification()).toLowerCase().endsWith(loweredTextToMatch));
			}
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getONCPredictedSyllabification().endsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getONCPredictedSyllabification()).toLowerCase().endsWith(loweredTextToMatch));
			}
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getSHPredictedSyllabification().endsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getSHPredictedSyllabification()).toLowerCase().endsWith(loweredTextToMatch));
			}
			break;
		case WORDS:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getWord().endsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getWord()).toLowerCase().endsWith(loweredTextToMatch));
			}
			break;
		default:
			break;
		}
		return filteredWords;
	}

	public ObservableList<Word> applyAtStartFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (wordsFilterType) {
		case CORRECT:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCorrectSyllabification().startsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getCorrectSyllabification()).toLowerCase().startsWith(loweredTextToMatch));
			}
			break;
		case CV_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCVPredictedSyllabification().startsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getCVPredictedSyllabification()).toLowerCase().startsWith(loweredTextToMatch));
			}
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getONCPredictedSyllabification().startsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getONCPredictedSyllabification()).toLowerCase().startsWith(loweredTextToMatch));
			}
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getSHPredictedSyllabification().startsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getSHPredictedSyllabification()).toLowerCase().startsWith(loweredTextToMatch));
			}
			break;
		case WORDS:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getWord().startsWith(textToMatch));	
			} else {
				filteredWords = words.filtered(w -> (w.getWord()).toLowerCase().startsWith(loweredTextToMatch));
			}
			break;
		default:
			break;
		}
		return filteredWords;
	}

	public ObservableList<Word> applyBlankFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (wordsFilterType) {
		case CORRECT:
			filteredWords = words.filtered(w -> w.getCorrectSyllabification().equals(""));
			break;
		case CV_PREDICTED:
			filteredWords = words.filtered(w -> w.getCVPredictedSyllabification().equals(""));
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			filteredWords = words.filtered(w -> w.getONCPredictedSyllabification().equals(""));
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			filteredWords = words.filtered(w -> w.getSHPredictedSyllabification().equals(""));
			break;
		case WORDS:
			// Should not be any
			filteredWords = words.filtered(w -> w.getWord().equals(""));
			break;
		default:
			break;
		}
		return filteredWords;
	}

	public ObservableList<Word> applyNonBlankFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (wordsFilterType) {
		case CORRECT:
			filteredWords = words.filtered(w -> !w.getCorrectSyllabification().equals(""));
			break;
		case CV_PREDICTED:
			filteredWords = words.filtered(w -> !w.getCVPredictedSyllabification().equals(""));
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			filteredWords = words.filtered(w -> !w.getONCPredictedSyllabification().equals(""));
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			filteredWords = words.filtered(w -> !w.getSHPredictedSyllabification().equals(""));
			break;
		case WORDS:
			// Should be all of them
			filteredWords = words.filtered(w -> !w.getWord().equals(""));
			break;
		default:
			break;
		}
		return filteredWords;
	}

	public ObservableList<Word> applyRegularExpressionFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		String adjustedRegularExpression = adjustTextToMatchFoRegExpression();
		String loweredAdjustedRegularExpression = adjustedRegularExpression.toLowerCase();
		switch (wordsFilterType) {
		case CORRECT:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCorrectSyllabification().matches(adjustedRegularExpression));	
			} else {
				filteredWords = words.filtered(w -> (w.getCorrectSyllabification()).toLowerCase().matches(loweredAdjustedRegularExpression));
			}
			break;
		case CV_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCVPredictedSyllabification().matches(adjustedRegularExpression));	
			} else {
				filteredWords = words.filtered(w -> (w.getCVPredictedSyllabification()).toLowerCase().matches(loweredAdjustedRegularExpression));
			}
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getONCPredictedSyllabification().matches(adjustedRegularExpression));	
			} else {
				filteredWords = words.filtered(w -> (w.getONCPredictedSyllabification()).toLowerCase().matches(loweredAdjustedRegularExpression));
			}
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getSHPredictedSyllabification().matches(adjustedRegularExpression));	
			} else {
				filteredWords = words.filtered(w -> (w.getSHPredictedSyllabification()).toLowerCase().matches(loweredAdjustedRegularExpression));
			}
			break;
		case WORDS:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getWord().matches(adjustedRegularExpression));	
			} else {
				filteredWords = words.filtered(w -> (w.getWord()).toLowerCase().matches(loweredAdjustedRegularExpression));
			}
			break;
		default:
			break;
		}
		return filteredWords;
	}

	protected String adjustTextToMatchFoRegExpression() {
		StringBuilder sb = new StringBuilder();
		if (!textToMatch.startsWith("^") && !textToMatch.startsWith(".*")) {
			sb.append(".*");
		}
		sb.append(textToMatch);
		if (!textToMatch.endsWith("$") && !textToMatch.endsWith(".*")) {
			sb.append(".*");
		}
		return sb.toString();
	}

	public ObservableList<Word> applyWholeItemFilter(ObservableList<Word> words) {
		ObservableList<Word> filteredWords = FXCollections.observableArrayList();
		switch (wordsFilterType) {
		case CORRECT:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCorrectSyllabification().equals(textToMatch));	
			} else {
				String loweredTextToMatch = textToMatch.toLowerCase();
				filteredWords = words.filtered(w -> (w.getCorrectSyllabification()).toLowerCase().equals(loweredTextToMatch));
			}
			break;
		case CV_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getCVPredictedSyllabification().equals(textToMatch));	
			} else {
				String loweredTextToMatch = textToMatch.toLowerCase();
				filteredWords = words.filtered(w -> (w.getCVPredictedSyllabification()).toLowerCase().equals(loweredTextToMatch));
			}
			break;
		case MORAIC_PREDICTED:
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			break;
		case ONC_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getONCPredictedSyllabification().equals(textToMatch));	
			} else {
				String loweredTextToMatch = textToMatch.toLowerCase();
				filteredWords = words.filtered(w -> (w.getONCPredictedSyllabification()).toLowerCase().equals(loweredTextToMatch));
			}
			break;
		case OT_PREDICTED:
			break;
		case SH_PREDICTED:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getSHPredictedSyllabification().equals(textToMatch));	
			} else {
				String loweredTextToMatch = textToMatch.toLowerCase();
				filteredWords = words.filtered(w -> (w.getSHPredictedSyllabification()).toLowerCase().equals(loweredTextToMatch));
			}
			break;
		case WORDS:
			if (matchCase) {
				filteredWords = words.filtered(w -> w.getWord().equals(textToMatch));	
			} else {
				String loweredTextToMatch = textToMatch.toLowerCase();
				filteredWords = words.filtered(w -> (w.getWord()).toLowerCase().equals(loweredTextToMatch));
			}
			break;
		default:
			break;
		}
		return filteredWords;
	}
}
