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

import java.io.PrintWriter;

/**
 * This class allows fill-in-the-blank questions to be displayed along with
 * GAIGS snapshots. It inherits the functionality of the 
 * <code>fibQuestion</code> class but outputs its information in the new GAIGS
 * XML format.
 * <p>
 * In order to use <code>XMLfibQuestion</code> or any of 
 * <code>question</code>'s other derived classes in a script-producing program,
 * the script-producing program must import the package <code>exe</code>.
 * </p>
 * <p>
 * An <code>XMLfibQuestion</code> is added to a showfile by placing a question
 * reference at the end of the snapshot in which the question is to appear and
 * then by appending the question information to the question section at the 
 * end of the file.
 * </p>
 * <p>
 * Question reference for an <code>XMLfibQuestion</code>:
 * </p>
 * <p>
 * &lt;question_ref ref = "id"/&gt;
 * </p>
 * <p>
 * Question information for an <code>XMLfibQuestion</code>:
 * </p>
 * <p>
 * &lt;question type = "FIBQUESTION" id = "id"&gt;
 * <br>&nbsp;&nbsp;&lt;question_text&gt;The question text appears here.
 *                 &lt;/question_text&gt;</br>
 * <br>&nbsp;&nbsp;&lt;answer_option is_correct = "yes"&gt;Correct answer 
 *                                                         possibility 1 
 *                                                         appears here.
 *                 &lt;/answer_option&gt;</br>
 * <br>&nbsp;&nbsp;&lt;answer_option is_correct = "yes"&gt;Correct answer 
 *                                                         possibility 2
 *                                                         appears here.
 *                 &lt;/answer_option&gt;</br>
 * <br>&nbsp;&nbsp;. . .</br>
 * <br>&nbsp;&nbsp;&lt;answer_option is_correct = "yes"&gt;Correct answer 
 *                                                         possibility <i>n</i>
 *                                                         appears here.
 *                 &lt;/answer_option&gt;</br>
 * <br>&lt;question&gt;</br>
 * </p>
 *
 * @author Andrew Jungwirth
 */
public class XMLfibQuestion extends fibQuestion{
    /* METHODS: */

    /**
     * Constructs a new <code>XMLfibQuestion</code> object that is 
     * distinguished by <code>id</code> and outputs its information to 
     * <code>out</code>.
     *
     * @param out Specifies the output stream to which this 
     *            <code>XMLfibQuestion</code> should perform its write 
     *            operations.
     * @param id  Gives this <code>XMLfibQuestion</code> a <code>String</code> 
     *            that can be used to uniquely identify it from other 
     *            <code>question</code> objects in a collection.
     */    
    public XMLfibQuestion(PrintWriter out, String id){
	super(out, id);
    }

    /**
     * Inserts the GAIGS XML &lt;question_ref&gt; tag for this 
     * <code>XMLfibQuestion</code> at the current position in the specified
     * <code>PrintWriter</code> output stream.
     */
    public void insertQuestion(){
	try{
	    out.println("<question_ref ref = \"" + id + "\"/>");
	}catch(Exception e){
	    System.err.println(e.toString() + 
			       " at insertQuestion() in XMLfibQuestion: " + 
			       id);
	    e.printStackTrace();
	}
    }

    // This method is inherited from fibQuestion and is used to add another 
    // acceptable answer String to the Vector that stores all the correct 
    // answers for the question.
    // public void setAnswer(String answer){ . . . }

    /**
     * Writes this <code>XMLfibQuestion</code>'s information at the current
     * position in the specified <code>PrintWriter</code> output stream using 
     * the GAIGS XML format.
     * <p>
     * This method is called by the <code>XMLquestionCollection</code>'s 
     * <code>writeQuestionsAtEOSF()</code> method and likely will never have to
     * be invoked explicitly.
     * </p>
     */
    public void writeQuestionInfo(){
	try{
	    out.println("<question type = \"FIBQUESTION\" id = \"" +
			id + "\">");
	    out.println("<question_text>" + questionText + "</question_text>");
	    for(int a = 0; a < correctAnswers.size(); a++){
		out.println("<answer_option is_correct = \"yes\">" +
			    ((String)correctAnswers.elementAt(a)) +
			    "</answer_option>");
	    }
	    out.println("</question>");
	}catch(Exception e){
	    System.err.println(e.toString() + 
			       " at writeQuestionInfo() in XMLfibQuestion: " + 
			       id);
	    e.printStackTrace();
	}
    }
}
