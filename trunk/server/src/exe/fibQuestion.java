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

package exe;

import java.io.*;
import java.util.*;

/**
 * This class extends <code>question</code> to provide support for
 * fill-in-the-blank questions.
 * <p>
 * In order to use <code>fibQuestion</code> or any of <code>question</code>'s 
 * other derived classes in a script-producing program, the script-producing 
 * program must import the package <code>exe</code>.
 * </p>
 * <p>
 * A <code>fibQuestion</code> is added to a showfile by placing a question
 * reference at the end of the snapshot in which the question is to appear and
 * then by appending the question information to the question section at the 
 * end of the file.
 * </p>
 * <p>
 * Question reference for a <code>fibQuestion</code>:
 * </p>
 * <p>
 * FIBQUESTION id
 * </p>
 * <p> 
 * Question information for a <code>fibQuestion</code>:
 * </p>
 * <p>
 * FIBQUESTION id
 * <br>The question text appears here.</br>
 * <br>ENDTEXT</br>
 * <br>ANSWER</br>
 * <br>Correct answer possibility 1 appears here.</br>
 * <br>Correct answer possibility 2 appears here.</br>
 * <br>. . .</br>
 * <br>Correct answer possibility <i>n</i> appears here.</br>
 * <br>ENDANSWER</br>
 * </p>
 *
 * @author ? (original author)
 * @author Ben Tidman (original comments)
 * @author Andrew Jungwirth (editing and Javadoc comments)
 */
public class fibQuestion extends question{
    /* DATA: */

    /**
     * Holds the possible correct answers to this question.
     * The correct answer choices are stored as <code>String</code> objects and
     * are compared to the text entered into the question window to determine 
     * if the user's answer is correct.
     */
    protected Vector <String> correctAnswers;

    /* METHODS: */

    /**
     * Constructs a new <code>fibQuestion</code> object that is distinguished
     * by <code>id</code> and outputs its information to <code>out</code>.
     *
     * @param out Specifies the output stream to which this 
     *            <code>fibQuestion</code> should perform its write operations.
     * @param id  Gives this <code>fibQuestion</code> a <code>String</code> 
     *            that can be used to uniquely identify it from other 
     *            <code>question</code> objects in a collection.
     */    
    public fibQuestion(PrintWriter out, String id){
	super(out, id);
        correctAnswers = new Vector <String> (10, 20);
    }

    public void insertQuestion(){
        try{
            out.println("FIBQUESTION "+id);
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at insertQuestion() in fibQuestion: " + id);
	    e.printStackTrace();
        }
    }

    public void animalInsertQuestion(){
        try{
            out.println("FIBQUESTION \"" + id + "\"");
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalInsertQuestion() in fibQuestion: " + 
			       id);
	    e.printStackTrace();
        }
    }

    /**
     * Sets one of the possible correct answers for this 
     * <code>fibQuestion</code>.
     * This possible correct answer is added to the <code>Vector</code> of
     * correct answers. When the user enters an answer, the text will be 
     * compared with all the possible correct answers in the list to determine 
     * if the user's answer is correct. 
     *
     * @param answer Specifies the text of one of the possible correct answers
     *               to the <code>fibQuestion</code>. Note that this
     *               <code>String</code> must be identical to the answer that
     *               the user is required to enter into the question window's
     *               text field.
     */
    public void setAnswer(String answer){
        correctAnswers.addElement(answer.trim());
    }
  
    public void writeQuestionInfo(){
        try{
            out.println("FIBQUESTION " + id);
            out.println(questionText);
            out.println("ENDTEXT");
            out.println("ANSWER");
            for(int x = 0; x < correctAnswers.size(); x++){
                out.println((String)correctAnswers.elementAt(x));
	    }
            out.println("ENDANSWER");
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at writeQuestionInfo() in fibQuestion: " + 
			       id);
            e.printStackTrace();
        }
    }

    public void animalWriteQuestionInfo(){
        try{
            out.println("FIBQUESTION \"" + id + "\"");
            out.println(questionText);
            out.println("ENDTEXT");
            out.println("ANSWER");
            for(int x = 0; x < correctAnswers.size(); x++){
                out.println((String)correctAnswers.elementAt(x));
	    }
            out.println("ENDANSWER");
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalWriteQuestionInfo() in fibQuestion: "
			       + id);
            e.printStackTrace();
        }
    }
}
