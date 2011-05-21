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
 * TFQuestion.java
 *
 * Created on July 6, 2004, 4:36 PM
 */

package jhave.question;

import java.util.StringTokenizer;
import org.jdom.*;
/**
 * True / False Question implementation.
 * @author  Chris Gaffney
 */
public class TFQuestion extends AbstractQuestion {
    /** The correct answer. */
    private boolean correctAnswer;
    
    /** Creates a new instance of TFQuestion */
    public TFQuestion(String script) throws QuestionParseException {
        super(script);
        setQuestionType(TYPE_TRUE_FALSE);
    }
    
    public TFQuestion(Element root) throws QuestionParseException {
        super(root);
        setQuestionType(TYPE_TRUE_FALSE);
    }
    
    /**
     * Returns a string representing all possible answers.
     * @return String possible answers string.
     */
    public String getAnswersString() {
        return (correctAnswer ? "\"1\"" : "\"0\"");
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
        return correctAnswer && getAnswer().equalsIgnoreCase("true") ||
                !correctAnswer && getAnswer().equalsIgnoreCase("false");
    }
    
    protected void parse(Element root) {
        setQuestion( root.getChild("question_text").getText().trim() );
        correctAnswer = root.getChild("answer_option").getText().trim().equalsIgnoreCase("true");
        setID( root.getAttributeValue( "id" ) );
        setPossibleAnswers(new String[] {"True", "False"});
    } // parse(Element)
    
    /**
     * Parse the question script.
     * @param script the question script to parse.
     */
    protected void parse(String script) throws QuestionParseException {
        StringTokenizer lineTokenizer = new StringTokenizer(script, "\n");
        
        String header = lineTokenizer.nextToken();
        String question = lineTokenizer.nextToken();
        
        // ENDTEXT
        lineTokenizer.nextToken();
        // ANSWER
        lineTokenizer.nextToken();
        
        String correct = lineTokenizer.nextToken();
        
        // ENDANSWER
        lineTokenizer.nextToken();
        
        correctAnswer = correct.equalsIgnoreCase("t");
        
        setID(header.substring(header.indexOf(" ")).trim());
        setQuestion(question);
        setPossibleAnswers(new String[] {"True", "False"});
    } // parse(String)
    
    /**
     * Returns a string representation of the Question.
     * @return String string representation of the Question.
     */
    public String toString() {
        return super.toString() + ", correctAnswer=" + correctAnswer + ']';
    }

    /**
     * Returns a Boolean indicating the correct answer.
     * @return a Boolean indicating the correct answer.
     */
    public Object getCorrectAnswer()
    {
	return new Boolean(correctAnswer);
    }
}
