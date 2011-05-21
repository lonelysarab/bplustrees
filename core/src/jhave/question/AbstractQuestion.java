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
 * AbstractQuestion.java
 *
 * Created on July 6, 2004, 4:13 PM
 */

package jhave.question;

import org.jdom.*;

/**
 *
 * @author  Chris Gaffney
 */
public abstract class AbstractQuestion implements Question {
    /** If the question has been displayed */
    private boolean displayed = false;
    /** The answer entered by the user. */
    private String answer = null;
    /** The questions id. */
    private String id = "-1";
    /** The question the user is asked. */
    private String question;
    /** The questions type. */
    private byte questionType;
    /** The possible answers. */
    private String[] possibleAnswers = {""};
    
    /**
     * Instantiate an new question and parse it.
     * @param script the question script to parse.
     */
    public AbstractQuestion(String script) throws QuestionParseException {
        parse(script);
    }
    
    public AbstractQuestion(Element question) throws QuestionParseException {
        parse(question);
    }
    
    /**
     * Returns whether the question has been displayed
     * @return boolean if the question has been displayed
     */
    public boolean getDisplayed() {
	return displayed;
    }

    /**
     * Returns the answer the user entered, null if the user has not entered
     * an answer.
     * @return String the answer entered by the user.
     */
    public String getAnswer() {
        return answer;
    }
    
    /**
     * Returns the questions ID.
     * @return String question id.
     */
    public String getID() {
        return id;
    }
    
    /**
     * Returns an array of possible answers.
     * @return String[] possible answers.
     */
    public String[] getPossibleAnswers() {
        return possibleAnswers;
    }
    
    /**
     * Returns a string representing all possible answers.
     * @return String possible answers string.
     */
    public abstract String getAnswersString();
    
    /**
     * Returns the question the user is asked.
     * @return String question the user is asked.
     */
    public String getQuestion() {
        return question;
    }
    
    /**
     * Returns the type of question as specified by the TYPE_* constants.
     * @return byte question type.
     */
    public byte getQuestionType() {
        return questionType;
    }
    
    /**
     * Returns the type of question as a String.
     * @return String question type.
     */
    public String getTypeString() {
        switch(getQuestionType()) {
            case TYPE_FILL_IN_THE_BLANK:
                return "FB";
            case TYPE_MULTIPLE_CHOICE:
                return "MC";
            case TYPE_MULTIPLE_SELECTION:
                return "MS";
            case TYPE_TRUE_FALSE:
                return "TF";
            default:
                return null;
        }
    }
    
    /**
     * If the users answer is correct, always false if the user has not entered
     * an answer.
     * @return boolean if the answer is correct.
     */
    public abstract boolean isCorrect();
    
    /**
     * Parse the question script.
     * @param script the question script to parse.
     */
    protected abstract void parse(String script) throws QuestionParseException;
    protected abstract void parse(Element question) throws QuestionParseException;
    
    /**
     * Reset the question to specify that no answer was entered.
     */
    public void reset() {
        answer = null;
    }
    
    /**
     * Set whether the question has been displayed.
     * @param displayed whether the question has been displayed.
     */
    public void setDisplayed(boolean displayed) {
	this.displayed = displayed;
    }

    /**
     * Set the users answer to the question.
     * @param answer users answer.
     */
    public void setAnswer(String answer) {
        this.answer = trimQuotes(answer);
    }
    
    /**
     * Sets the question ID.
     * @param id the new question ID.
     */
    protected void setID(String id) {
        this.id = trimQuotes(id);
    }
    
    /**
     * Sets the array of possible answers.
     * @param answers the possible answers.
     */
    protected void setPossibleAnswers(String[] answers) {
        String[] trimmed = new String[answers.length];
        for(int i = 0; i < answers.length; i++) {
            if(answers[i] != null) {
                trimmed[i] = trimQuotes(answers[i]);
            } else {
                trimmed[i] = null;
            }
        }
        this.possibleAnswers = trimmed;
    }
    
    /**
     * Sets the question asked the user.
     * @param question the question.
     */
    protected void setQuestion(String question) {
        this.question = trimQuotes(question);
    }
    
    /**
     * Sets the questions type.
     * @param type the questions type.
     */
    protected void setQuestionType(byte type) {
        this.questionType = type;
    }
    
    /**
     * If a String is contained in a set of quotes it will trim those quotes
     * and return the modified string.
     * @param str String to trim.
     * @return String the trimmed string.
     */
    protected static String trimQuotes(String str) {
        try {
            if(str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
                return str.substring(1, str.length() - 1);
            }
        } catch(StringIndexOutOfBoundsException e) {
            // Do nothing and just return the original.
        }
        return str;
    }
    
    /**
     * Returns a string representation of the Question. Should be called by
     * child classes in their toString methods. Children should also tak on
     * a closing ']' to the string.
     * @return String string representation of the Question.
     */
    public String toString() {
        StringBuffer string = new StringBuffer();
        
        string.append("[type=");
        string.append(getQuestionType());
        string.append(", id=");
        string.append(getID());
        string.append(", question=");
        string.append(getQuestion());
        string.append(", answer=");
        string.append(getAnswer());
        string.append(", isCorrect=");
        string.append(isCorrect());
        
        return string.toString();
    }
}
