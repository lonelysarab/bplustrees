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
 * Question.java
 *
 * Created on July 6, 2004, 4:04 PM
 */

package jhave.question;

/**
 *
 * @author  Chris Gaffney
 */
public interface Question {
    /** Fill in the blank type question. */
    public static final byte TYPE_FILL_IN_THE_BLANK = 1;
    /** Multiple choice type question. */
    public static final byte TYPE_MULTIPLE_CHOICE = 2;
    /** True / False type question. */
    public static final byte TYPE_TRUE_FALSE = 3;
    /** Multiple selection type question. */
    public static final byte TYPE_MULTIPLE_SELECTION = 4;

    /**
     * Returns whether the question has been displayed
     * @return boolean if the question has been displayed
     */
    public boolean getDisplayed();
    
    /**
     * Returns the answer the user entered, null if the user has not entered
     * an answer.
     * @return String the answer entered by the user.
     */
    public String getAnswer();
    
    /**
     * Returns the questions ID.
     * @return String question id.
     */
    public String getID();
    
    /**
     * Returns an array of possible answers.
     * @return String[] possible answers.
     */
    public String[] getPossibleAnswers();
    
    /**
     * Returns a string representing all possible answers.
     * @return String possible answers string.
     */
    public String getAnswersString();
    
    /**
     * Returns the question the user is asked.
     * @return String question the user is asked.
     */
    public String getQuestion();
    
    /**
     * Returns the type of question as specified by the TYPE_* constants.
     * @return byte question type.
     */
    public byte getQuestionType();
    
    /**
     * Returns the type of question as a String.
     * @return String question type.
     */
    public String getTypeString();

    /**
     * If the users answer is correct, always false if the user has not entered
     * an answer.
     * @return boolean if the answer is correct.
     */
    public boolean isCorrect();
    
    /**
     * Reset the question to specify that no answer was entered.
     */
    public void reset();
    
    /**
     * Set whether the question has been displayed.
     * @param displayed whether the question has been displayed.
     */
    public void setDisplayed(boolean displayed);

    /**
     * Set the users answer to the question.
     * @param answer users answer.
     */
    public void setAnswer(String answer);

    /**
     * Returns an object containing the correct answer.
     * @return an object containing the correct answer.
     */
    public Object getCorrectAnswer();
}
