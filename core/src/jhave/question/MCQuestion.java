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
 * MCQuestion.java
 *
 * Created on July 6, 2004, 4:36 PM
 */

package jhave.question;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Element;

/**
 * Multiple Choice Question implementation.
 * @author  Chris Gaffney
 */
public class MCQuestion extends AbstractQuestion {
    /** Index of the correct answer. */
    private int correctAnswer;
    
    /** Creates a new instance of MCQuestion */
    public MCQuestion(String script) throws QuestionParseException {
        super(script);
        setQuestionType(TYPE_MULTIPLE_CHOICE);
    }
    
    public MCQuestion(Element root) throws QuestionParseException {
        super(root);
        setQuestionType(TYPE_MULTIPLE_CHOICE);
    }
    
    /**
     * Returns a string representing all possible answers.
     * @return String possible answers string.
     */
    public String getAnswersString() {
        return "\"" + correctAnswer + "\"";
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
        if(Character.isDigit(getAnswer().trim().charAt(0))) {
            if(Integer.parseInt(getAnswer()) == correctAnswer) {
                return true;
            } else {
                return false;
            }
        } else {
            List answers = Arrays.asList(getPossibleAnswers());
            int index = answers.indexOf(getAnswer());
            if(index == correctAnswer) {
                return true;
            } else {
                return false;
            }
        }
    }
    
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
        this.correctAnswer = Integer.parseInt(trimQuotes(lineTokenizer.nextToken()));
        
        // ENDANSWER
        lineTokenizer.nextToken();
        
        // Shuffle
        String answerContent = (String)choices.get(this.correctAnswer - 1);
        Collections.shuffle(choices);
        this.correctAnswer = choices.indexOf(answerContent) + 1;
        
        // Set the header, the question, and the set of possible answers
        setID(header.substring(header.indexOf(" ")).trim());
        setQuestion(question);
        setPossibleAnswers((String[])choices.toArray(new String[0]));
    } // parse(String)
    
    protected void parse(Element question) throws QuestionParseException {
        setID(question.getAttributeValue("id"));
        setQuestion( question.getChild("question_text").getText().trim() );
        
        LinkedList choices = new LinkedList();
        Iterator iterator = question.getChildren().iterator();
        correctAnswer = -1;
        int i = 0;
        while( iterator.hasNext() ) {
            Element child = (Element) iterator.next();
            if (child.getName().equals("answer_option")) {
                i++;
                choices.add(child.getText().trim());
                if (child.getAttributeValue("is_correct").equalsIgnoreCase("yes"))
                    correctAnswer = i;
            }
        }
        
        if(correctAnswer == -1) {
            System.out.println("No correct answer for a MCQuestion");
            return;
        }
        
        String answerContent = (String)choices.get(correctAnswer-1);
        Collections.shuffle(choices);
        correctAnswer = choices.indexOf(answerContent) + 1;
        setPossibleAnswers((String[])choices.toArray(new String[0]));
    } // parse(Element)
    
    /**
     * Returns a string representation of the Question.
     * @return String string representation of the Question.
     */
    public String toString() {
        return super.toString() + ", correctAnswer=" + correctAnswer + ']';
    }

    /**
     * Returns the Integer corresponding to the correct answer.
     * @return The Integer corresponding to the correct answer.
     */
    public Object getCorrectAnswer()
    {
	return new Integer(correctAnswer);
    }
}
