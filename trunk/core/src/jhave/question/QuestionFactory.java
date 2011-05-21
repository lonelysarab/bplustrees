/* 
This file is part of JHAVE -- Java Hosted Algorithm Visualization
Environment, developed by Tom Naps, David Furcy (both of the
University of Wisconsin - Oshkosh), Myles McNally (Alma College), and
numerous other contributors who are listed at the http://jhave.org
site

JHAVE is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your
option) any later version.

JHAVE is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with the JHAVE. If not, see:
<http://www.gnu.org/licenses/>.
*/

/*
 * QuestionFactory.java
 *
 * Created on July 6, 2004, 5:13 PM
 */

package jhave.question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import jhave.core.JHAVETranslator;

import org.jdom.Element;

/**
 * Library of functions for the parsing of questions from a Question Script.
 * @author  Chris Gaffney
 */
public class QuestionFactory {
    
    /** Don't allow the class to be instantiated. */
    private QuestionFactory() {}
    
    /**
     * Parse a single Question from a question script, skipping through the script
     * until a question is found.
     * @param script String representation of the script.
     * @return Question parsed from the script.
     * @throws QuestionParseException error occured parsing the script due to syntax errors.
     */
    public static Question parseQuestion(String script) throws QuestionParseException {
        StringTokenizer tok = new StringTokenizer(script, "\n");
        String line = tok.nextToken();
        
        if(getQuestionType(line) < 0) {
            while(getQuestionType(line) < 0) {
                line = tok.nextToken();
            }
        }
        StringBuffer question = new StringBuffer();
        question.append(line);
        
        while(!(line = tok.nextToken()).equals("ENDANSWER")) {
            question.append(line);
            question.append("\n");
        }
        
        switch(getQuestionType(question.toString())) {
            case -1:
                // Throw an exception or something
                throw new QuestionParseException();
            case Question.TYPE_FILL_IN_THE_BLANK:
                return new FIBQuestion(script);
            case Question.TYPE_MULTIPLE_CHOICE:
                return new MCQuestion(script);
            case Question.TYPE_TRUE_FALSE:
                return new TFQuestion(script);
            case Question.TYPE_MULTIPLE_SELECTION:
                return new MSQuestion(script);
            default:
                // Unknown... do something here.
                throw new QuestionParseException();
        }
    }
    
    /**
     * Parse a single Question from a question script, skipping through the script
     * until a question is found.
     * @param script Character stream to read from.
     * @return Question parsed from the script.
     * @throws IOException error occured reading the script.
     * @throws QuestionParseException error occured parsing the script due to syntax errors.
     */
    public static Question parseQuestion(Reader script) throws IOException, QuestionParseException {
        BufferedReader reader;
        if(script instanceof BufferedReader) {
            reader = (BufferedReader)script;
        } else {
            reader = new BufferedReader(script);
        }
        StringBuffer string = new StringBuffer(1024);
        
        String line;
        while((line = reader.readLine()) != null) {
            string.append(line);
            string.append("\n");
        }
        
        return parseQuestion(string.toString());
    }

    public static Collection questionCollectionFromXML( Element questions ) throws QuestionParseException {
	Iterator iterator = questions.getChildren().iterator();
	List returned = new LinkedList();

	//System.out.println("In question factory.");

	while( iterator.hasNext() ) {
	    Element child = (Element) iterator.next();
	    Question question;

	    String type = child.getAttributeValue("type");
	    switch( getQuestionType(type) ) {
            case -1:
                // Throw an exception or something
//		System.out.println("ERROR: Invalid question type.");
              System.err.println(JHAVETranslator.translateMessage(
                  "errorInvalidQuestionType"));
                throw new QuestionParseException(/*type + " is not a valid question type."*/);
            case Question.TYPE_FILL_IN_THE_BLANK:
                question = new FIBQuestion(child);
		break;
            case Question.TYPE_MULTIPLE_CHOICE:
                question = new MCQuestion(child);
		break;
            case Question.TYPE_TRUE_FALSE:
                question = new TFQuestion(child);
		break;
            case Question.TYPE_MULTIPLE_SELECTION:
                question = new MSQuestion(child);
		break;
            default:
                // Unknown... do something here.
//		System.out.println("ERROR: unkown question parse exception.");
              System.out.println(JHAVETranslator.translateMessage(
                  "unknownParseError"));
                throw new QuestionParseException();
	    }

	    returned.add(question);
	} // while more questions to make

	return returned;
    } // questionCollectionFromXML( element )
    
    /**
     * Parse an entire script for all of its questions.
     * @param script String representation of the script.
     * @return Collection of all the questions parsed from the script.
     * @throws QuestionParseException error occured parsing the script due to syntax errors.
     */
    public static Collection parseScript(String script) throws QuestionParseException {
        StringTokenizer tok = new StringTokenizer(script, "\n");
        
        //System.out.println(script);
        String line = tok.nextToken();
        while(tok.hasMoreTokens() && !line.equals("STARTQUESTIONS")) {
            line = tok.nextToken();
        }
        
        if(!tok.hasMoreTokens()) {
            // Behavior when there is no question section found.
            // Is there something better that we should do here?
            return new LinkedList();
        }
        
        StringBuffer question = new StringBuffer();
        List returned = new LinkedList();
        
        while(tok.hasMoreTokens()) {
            while(!(line = tok.nextToken()).equals("ENDANSWER")) {
                question.append(line);
                question.append('\n');
            }
            question.append(line);
            question.append('\n');
            
            returned.add(parseQuestion(question.toString()));
            question = new StringBuffer();
        }
        
        return returned;
    }
    
    /**
     * Parse an entire script for all of its questions.
     * @param script Character stream to read from.
     * @return Collection of all the questions parsed from the script.
     * @throws IOException error occured reading the script.
     * @throws QuestionParseException error occured parsing the script due to syntax errors.
     */
    public static Collection parseScript(Reader script) throws IOException, QuestionParseException {
        BufferedReader reader;
        if(script instanceof BufferedReader) {
            reader = (BufferedReader)script;
        } else {
            reader = new BufferedReader(script);
        }
        StringBuffer string = new StringBuffer(2048);
        
        String line;
        while(!((line = reader.readLine()) == null)) {
            string.append(line);
            string.append('\n');
        }
        
        return parseScript(string.toString());
    }

    /**
     * Display a dialog to ask for user input for a given question. If the
     * question is answered incorrect the correct answer will then be
     * displayed for the user
     * @param question the question to display]
     */
    public static void showQuestionDialogWithAnswer(Question question)
    {
	QuestionView.showQuestionDialog(question, false, true);
    }

    /**
     * Display a dialog to ask for user input of the given question. Default
     * behavior is to dispose any currently displayed question and display
     * a new dialog for the new question.
     * @param question the question to display.
     */
    public static void showQuestionDialog(Question question) {
        QuestionView.showQuestionDialog(question, false, false);
    }
    
    /**
     * Display a dialog to ask for user input of the given question.
     * Dialog has its properties set specifically for Quiz Mode.
     * @param question the question to display.
     */
    public static void showQuizQuestion(Question question) {
	//        QuestionView.showQuestionDialog(question, true, false);
        QuestionView.showQuestionDialog(question, true, true);
    }
    
    /**
     * Parse a Question Script to find out which type of question it is. The
     * script must start with the identifier, otherwise it returns -1.
     * @param script Question script to find its type.
     * @return question type as defined in the Question.TYPE_* constants.
     */
    protected static byte getQuestionType(String script) {
        if(script.startsWith("FIBQUESTION")) {
            return Question.TYPE_FILL_IN_THE_BLANK;
        } else if(script.startsWith("MCQUESTION")) {
            return Question.TYPE_MULTIPLE_CHOICE;
        } else if(script.startsWith("TFQUESTION")) {
            return Question.TYPE_TRUE_FALSE;
        } else if(script.startsWith("MSQUESTION")) {
            return Question.TYPE_MULTIPLE_SELECTION;
        }
        return -1;
    }
}
