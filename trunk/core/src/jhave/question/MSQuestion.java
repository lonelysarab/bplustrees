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
 * MSQuestion.java
 *
 * Created on July 6, 2004, 4:36 PM
 */

package jhave.question;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.jdom.Element;

/**
 * Multiple Selection Question implementation.
 * @author  Chris Gaffney
 */
public class MSQuestion extends AbstractQuestion {
    /** List of all correct answers. */
    private LinkedList correctAnswers;
    
    /** Creates a new instance of MSQuestion */
    public MSQuestion(String script) throws QuestionParseException {
        super(script);
        setQuestionType(TYPE_MULTIPLE_SELECTION);
    }
    
    public MSQuestion(Element root) throws QuestionParseException {
        super(root);
        setQuestionType(TYPE_MULTIPLE_SELECTION);
    }
    
    /**
     * Returns a string representing all possible answers.
     * @return String possible answers string.
     */
    public String getAnswersString() {
        String answersString = "";
        
        if(!correctAnswers.isEmpty()) {
            ListIterator itr = correctAnswers.listIterator();
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
        StringTokenizer numberTokenizer = new StringTokenizer(getAnswer());
        int count = 0;
        while(numberTokenizer.hasMoreTokens()) {
            Integer value = new Integer(numberTokenizer.nextToken());
            if(correctAnswers.contains(value)) {
                count++;
            }
            if(!(correctAnswers.contains(value))) {
                return false;
            }
        }
        if(count == correctAnswers.size()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Parse the question script.
     * @param script the question script to parse.
     */
    protected void parse(String script) throws QuestionParseException {
        StringTokenizer lineTokenizer = new StringTokenizer(script, "\n");
        correctAnswers = new LinkedList();
        
        String header = lineTokenizer.nextToken();
        String question = lineTokenizer.nextToken();
        
        // ENDTEXT
        lineTokenizer.nextToken();
        
        String choice = lineTokenizer.nextToken();
        LinkedList choices = new LinkedList();
        while(!choice.equals("ANSWER")) {
            choices.add(choice);
            
            // ENDCHOICE
            lineTokenizer.nextToken();
            
            choice = lineTokenizer.nextToken();
        }
        // ANSWER (Breaks on ANSWER)
        
        // TODO: Need to check for NumberFormatException and throw a QuestionParseException
        StringTokenizer numberTokenizer = new StringTokenizer(lineTokenizer.nextToken());
        while(numberTokenizer.hasMoreTokens()) {
            correctAnswers.add(new Integer(Integer.parseInt(trimQuotes(numberTokenizer.nextToken()))));
        }
        
        // ENDANSWER
        lineTokenizer.nextToken();
        
        setID(header.substring(header.indexOf(" ")).trim());
        setQuestion(question);
        setPossibleAnswers((String[])choices.toArray(new String[0]));
    } // parse(String)

    protected void parse(Element question) throws QuestionParseException {
	setID(question.getAttributeValue("id"));
	setQuestion(question.getChild("question_text").getText().trim());

	LinkedList choices = new LinkedList();
	Iterator iterator = question.getChildren().iterator();
	correctAnswers = new LinkedList();
	int i = 0;
	while( iterator.hasNext() ) {
	    Element child = (Element) iterator.next();
	    if( child.getName().equals("answer_option") ) {
		i++;
		choices.add( child.getText().trim() );
		if (child.getAttributeValue("is_correct").equalsIgnoreCase("yes"))
		    correctAnswers.add(new Integer(i));
	    }
	}
	// from McQuestion:
// 	String answerContent = (String)choices.get(correctAnswer-1);
// 	Collections.shuffle(choices);
// 	correctAnswer = choices.indexOf(answerContent) + 1;
	setPossibleAnswers((String[])choices.toArray(new String[0]));
    } // parse(Element)
    
    /**
     * Returns a string representation of the Question.
     * @return String string representation of the Question.
     */
    public String toString() {
        return super.toString() + ", correctAnswers=" + correctAnswers.toString() + ']';
    }

    /**
     * Returns a LinkedList containing the correct answers.
     * @return a LinkedList containing the correct answers.
     */
    public Object getCorrectAnswer()
    {
	return correctAnswers;
    }
}
