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
 * FIBQuestion.java
 *
 * Created on July 6, 2004, 4:36 PM
 */

package jhave.question;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jdom.Element;

/**
 * Fill in the Blank Question Implementation.
 * @author  Chris Gaffney
 */
public class FIBQuestion extends AbstractQuestion {
    /** Set of all possible correct answers. */
    private HashSet correctAnswers;
    
    /** Creates a new instance of FIBQuestion */
    public FIBQuestion(String script) throws QuestionParseException {
        super(script);
        setQuestionType(TYPE_FILL_IN_THE_BLANK);
    }
    
    public FIBQuestion(Element root) throws QuestionParseException {
        super(root);
        setQuestionType(TYPE_FILL_IN_THE_BLANK);
    }
    
    /**
     * Returns a string representing all possible answers.
     * @return String possible answers string.
     */
    public String getAnswersString() {       
        String answersString = "";
        
        if(!correctAnswers.isEmpty()) {
            Iterator itr = correctAnswers.iterator();
            answersString += "\n";
            while(itr.hasNext()) {
                answersString += "\"" + itr.next() + "\"\n";
            }
        }
        
        return answersString;
    }
    
    /**
     * If the users answer is correct, always false if the user has not entered
     * an answer.
     * @return boolean if the answer is correct.
     */
    public boolean isCorrect() {
        if(getAnswer() == null) {
            return false;
        }
        return correctAnswers.contains(getAnswer().toLowerCase());
    }
    
    /**
     * Parse the question script.
     * @param script the question script to parse.
     */
    protected void parse(String script) throws QuestionParseException {
        StringTokenizer lineTokenizer = new StringTokenizer(script, "\n");
        correctAnswers = new HashSet();
        
        String header = lineTokenizer.nextToken();
        String question = lineTokenizer.nextToken();
        
        // ENDTEXT
        lineTokenizer.nextToken();
        // ANSWER
        lineTokenizer.nextToken();
        
        String line = lineTokenizer.nextToken();
        while(!line.equalsIgnoreCase("ENDANSWER")) {
            correctAnswers.add(trimQuotes(line.toLowerCase()));
            line = lineTokenizer.nextToken();
        }
        
        // ENDANSWER - TODO: Add check
        
        setID(header.substring(header.indexOf(" ")).trim());
        setQuestion(question);
        setPossibleAnswers(new String[] {""});
    } // parse(String)
    
    protected void parse(Element question) throws QuestionParseException {
        setID( question.getAttributeValue("id") );
        setQuestion( question.getChild("question_text").getText().trim() );
        setPossibleAnswers(new String[] {""});
        
        correctAnswers = new HashSet();
        Iterator iterator = question.getChildren().iterator();
        while( iterator.hasNext() ) {
            Element child = (Element) iterator.next();
            if( child.getName().equals("answer_option") )
                correctAnswers.add( child.getText().trim() );
        }
    } // parse(Element)
    
    /**
     * Returns a string representation of the Question.
     * @return String string representation of the Question.
     */
    public String toString() {
        return super.toString() + ", correctAnswers=" + correctAnswers.toString() + ']';
    }

    /**
     * Returns a HashSet containing all possible correct answers.
     * @return HashSet containing all possible correct answers.
     */
    public Object getCorrectAnswer()
    {
	return correctAnswers;
    }
}
