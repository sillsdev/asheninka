// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package sil.org.environmentparser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.*;

public class EnvironmentErrorListener {
	
	public static class VerboseListener extends BaseErrorListener {
		EnvironmentErrorListener listener = new EnvironmentErrorListener();
	    LinkedList<EnvironmentErrorInfo> errorMessages = new LinkedList<EnvironmentErrorInfo>(
				Arrays.asList(new EnvironmentErrorInfo()));
		
	    @Override
	    public void syntaxError(Recognizer<?, ?> recognizer,
	                            Object offendingSymbol,
	                            int line, int charPositionInLine,
	                            String msg,
	                            RecognitionException e)
	    {
	        EnvironmentErrorInfo info = new EnvironmentErrorInfo(offendingSymbol, line, charPositionInLine, msg, e);
	        errorMessages.add(info);
	    }

		public List<EnvironmentErrorInfo> getErrorMessages() {
			return errorMessages;
		}
		
		public void clearErrorMessageList() {
			errorMessages.clear();
		}

	}

}
