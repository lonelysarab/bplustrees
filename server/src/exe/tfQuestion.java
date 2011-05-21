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

/**
 * This class extends <code>question</code> to provide support for true-false 
 * questions.
 * <p>
 * In order to use <code>tfQuestion</code> or any of <code>question</code>'s 
 * other derived classes in a script-producing program, the script-producing 
 * program must import the package <code>exe</code>.
 * </p>
 * <p>
 * A <code>tfQuestion</code> is added to a showfile by placing a question
 * reference at the end of the snapshot in which the question is to appear and
 * then by appending the question information to the question section at the 
 * end of the file.
 * </p>
 * <p>
 * Question reference for a <code>tfQuestion</code>:
 * </p>
 * <p>
 * TFQUESTION id
 * </p>
 * <p> 
 * Question information for a <code>tfQuestion</code>:
 * </p>
 * <p>
 * TFQUESTION id
 * <br>The question text appears here.</br>
 * <br>ENDTEXT</br>
 * <br>ANSWER</br>
 * <br>T (if the answer is true) or F (if the answer is false).</br>
 * <br>ENDANSWER</br>
 * </p>
 *
 * @author ? (original author)
 * @author Ben Tidman (original comments)
 * @author Andrew Jungwirth (editing and Javadoc comments)
 */
public class tfQuestion extends question{
    /* DATA: */

    /** 
     * Stores the correct answer to this <code>tfQuestion</code>.
     */
    protected boolean correctAnswer;

    /* METHODS: */

    /**
     * Constructs a new <code>tfQuestion</code> object that is distinguished
     * by <code>id</code> and outputs its information to <code>out</code>.
     *
     * @param out Specifies the output stream to which this 
     *            <code>tfQuestion</code> should perform its write operations.
     * @param id  Gives this <code>tfQuestion</code> a <code>String</code> 
     *            that can be used to uniquely identify it from other 
     *            <code>question</code> objects in a collection.
     */    
    public tfQuestion(PrintWriter out, String id){
	super(out, id);
    }

    public void insertQuestion(){
        try{
            out.println("TFQUESTION " + id);
        }
	catch(Exception e){
            System.out.println(e.toString() + 
			       " at insertQuestion() in tfQuestion: " + id);
            e.printStackTrace();
        }
    }

    public void animalInsertQuestion(){
        try{
            out.println("TFQUESTION \"" + id + "\"");
        }
	catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalInsertQuestion() in tfQuestion: " + 
			       id);
            e.printStackTrace();
        }
    }

    /**
     * Sets the correct answer to this <code>tfQuestion</code>.
     * 
     * @param answer Specifies the answer to this <code>tfQuestion</code>. A
     *               value of <code>true</code> sets the answer to T, and a 
     *               value of <code>false</code> sets the answer to F.
     */
    public void setAnswer(boolean answer){
        correctAnswer = answer;
    }

    public void writeQuestionInfo(){
        try{
            out.println("TFQUESTION " + id);
            out.println(questionText);
            out.println("ENDTEXT");
            out.println("ANSWER");
            if(correctAnswer){
                out.println("T");
            }else{
                out.println("F");
	    }
            out.println("ENDANSWER");
        }
	catch(Exception e){
            System.out.println(e.toString() + 
			       " at writeQuestionInfo() in tfQuestion: " + id);
            e.printStackTrace();
        }
    }

    public void animalWriteQuestionInfo(){
        try{
            out.println("TFQUESTION \"" + id + "\"");
            out.println(questionText);
            out.println("ENDTEXT");
            out.println("ANSWER");
            if(correctAnswer){
                out.println("T");
            }else{
                out.println("F");
	    }
            out.println("ENDANSWER");
        }
	catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalWriteQuestionInfo() in tfQuestion: " 
			       + id);
            e.printStackTrace();
        }
    }
}
