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
import java.util.Vector;

/**
 * This class extends <code>question</code> to provide support for
 * multiple-selection questions.
 * <p>
 * In order to use <code>msQuestion</code> or any of <code>question</code>'s 
 * other derived classes in a script-producing program, the script-producing 
 * program must import the package <code>exe</code>.
 * </p>
 * <p>
 * An <code>msQuestion</code> is added to a showfile by placing a question
 * reference at the end of the snapshot in which the question is to appear and
 * then by appending the question information to the question section at the 
 * end of the file.
 * </p>
 * <p>
 * Question reference for an <code>msQuestion</code>:
 * </p>
 * <p>
 * MSQUESTION id
 * </p>
 * <p> 
 * Question information for an <code>msQuestion</code>:
 * </p>
 * <p>
 * MSQUESTION id
 * <br>The question text appears here.</br>
 * <br>ENDTEXT</br>
 * <br>Correct answer choice 1 appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>Incorrect answer choice 1 appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>Correct answer choice 2 appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>Incorrect answer choice 2 appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>. . .</br>
 * <br>Incorrect answer choice <i>n</i> appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>ANSWER</br>
 * <br>1</br>
 * <br>3</br>
 * <br>ENDANSWER</br>
 * </p>
 * <p>
 * Note that if the correct answer is added first, the correct answer is 1; if 
 * the correct answer is added second, the correct answer is 2; ...; if the 
 * correct answer is added as item <i>n</i>, the correct answer is <i>n</i>.
 * After all the answers have been added and the correct answers have been set,
 * the answer choices can be shuffled using the <code>shuffle()</code> method.
 *
 * @author Andrew Jungwirth
 */
public class msQuestion extends question{
    /* DATA: */

    /**
     * Holds the answer choices for this question.
     */
    protected Vector <String> choices;

    /**
     * Keeps tracks of the correct answers' positions in the 
     * <code>Vector</code> of choices.
     * Note that the values stored are one greater than the index of the
     * answers in the <code>Vector</code>. For instance, if a correct answer is
     * the first answer stored in <code>choices</code>, the value of 1 will be
     * stored in <code>correctChoices</code> as an <code>Integer</code> value.
     */
    protected Vector <Integer >correctChoices;

    /* METHODS: */

    /**
     * Constructs a new <code>msQuestion</code> object that is distinguished
     * by <code>id</code> and outputs its information to <code>out</code>.
     *
     * @param out Specifies the output stream to which this 
     *            <code>msQuestion</code> should perform its write operations.
     * @param id  Gives this <code>msQuestion</code> a <code>String</code> that
     *            can be used to uniquely identify it from other 
     *            <code>question</code> objects in a collection.
     */    
    public msQuestion(PrintWriter out, String id){
	super(out, id);
	choices = new Vector <String> ();
	correctChoices = new Vector <Integer> (10, 20);
    }

    public void insertQuestion(){
	try{
	    out.println("MSQUESTION " + id);
	}catch(Exception e){
	    System.err.println(e.toString() + 
			       " at insertQuestion() in msQuestion: " + id);
	    e.printStackTrace();
	}
    }

    public void animalInsertQuestion(){
	try{
	    out.println("MSQUESTION \"" + id + "\"");
	}catch(Exception e){
	    System.err.println(e.toString() +
			       " at animalInsertQuestion() in msQuestion: " + 
			       id);
	    e.printStackTrace();
	}
    }

    /**
     * Adds an answer choice to the <code>Vector</code> that stores the answer 
     * choices for this <code>msQuestion</code>.
     * The value passed to <code>choice</code> is added to the end of the 
     * <code>Vector</code> of answer choices.
     *
     * @param choice Specifies the text of the answer choice that is to be
     *               added to the <code>Vector</code> of choices. This is the
     *               answer choice that appears in the question window when 
     *               this <code>msQuestion</code> is asked.
     */
    public void addChoice(String choice){
	choices.addElement(choice.trim());
    }

    /**
     * Adds a correct answer choice to the <code>Vector</code> that is used to 
     * track the positions of the correct answers to this 
     * <code>msQuestion</code>. 
     * The value passed to <code>choice</code> is added as an 
     * <code>Integer</code> value to <code>correctChoices</code>.
     * <p>
     * Note that the value passed to <code>choice</code> must be one greater 
     * than the correct answer choice's index in the 
     * <code>choices Vector</code>. For example, the first answer added via 
     * <code>addChoice(String)</code> is 1.
     * </p>
     *
     * @param choice Indicates a correct answer to this 
     *               <code>msQuestion</code>. The value must be one greater
     *               than the answer's index in the <code>Vector</code> of 
     *               choices, as described above.
     */
    public void setAnswer(int choice){
	correctChoices.addElement(new Integer(choice));
    }

    // This method is passed a Vector containing Integer values that represent
    // the correct answers to the question as described above. The 
    // correctChoices data member will then reference this Vector. This method
    // can therefore be used to set the correct answers with a single method
    // call instead of multiple calls to setAnswer(int).
    /**
     * Sets the correct answers to this <code>msQuestion</code> with a single
     * method call.
     * The <code>Vector</code> passed to <code>correctChoices</code> is set as
     * the <code>Vector</code> of correct answers to this question. 
     * <p>
     * Note that this <code>Vector</code> must contain <code>Integer</code>
     * values that are one greater than the correct answer choices' indices in
     * in the <code>choices Vector</code>. For example, the first answer added
     * via <code>addChoice(String)</code> is 1.
     * </p>
     *
     * @param correctChoices Contains the correct answer choices for this
     *                       <code>msQuestion</code>. This <code>Vector</code>
     *                       must contain <code>Integer</code> values one
     *                       greater than the correct choices' indices in the
     *                       <code>choices Vector</code>, as described above.
     */
    public void setAnswer(Vector <Integer> correctChoices){
	this.correctChoices = correctChoices;
    }

    /**
     * Randomly exchanges the positions of the answer choices contained within
     * <code>choices</code> so that the answer choices appear in a different
     * order than they were added.
     * Whenever a correct answer choice is swapped with another choice, the
     * corresponding value in <code>correctChoices</code> is changed so that
     * <code>correctChoices</code> accurately tracks the correct answers to 
     * this <code>msQuestion</code>.
     */
    public void shuffle(){
	int rand;
	int size = choices.size();

	for(int c = 0; c < size; c++){
	    rand = (int)(Math.random() * (size - 1)) + 1;
	    if(rand != c){
		swap(c, rand);
	    }
	}
    }

    /**
     * Swaps the locations of two answer choices in the <code>Vector</code> of
     * choices. 
     * If one of the specified answer choices corresponds to a 
     * correct answer <code>correctChoices</code> is changed accordingly.
     *
     * @param a Indicates the index in the <code>choices Vector</code> of the
     *          first answer choice to be swapped.
     * @param b Indicates the index in the <code>choices Vector</code> of the
     *          second answer choice to be swapped.
     */
    private void swap(int a, int b){
	String temp;
	int size = correctChoices.size();
	boolean aCorrect = false, bCorrect = false;
	int aPos = -1, bPos = -1;

	temp = choices.elementAt(a);
	choices.set(a, choices.elementAt(b));
	choices.set(b, temp);

	for(int c = 0; c < size; c++){
	    if((a + 1) == 
	       ((Integer)correctChoices.elementAt(c)).intValue()){
		aCorrect = true;
		aPos = c;
	    }
	    if((b + 1) == 
	       ((Integer)correctChoices.elementAt(c)).intValue()){
		bCorrect = true;
		bPos = c;
	    }
	}

	if(aCorrect && !bCorrect){
	    correctChoices.removeElementAt(aPos);
	    correctChoices.addElement(new Integer(b + 1));
	}else if(bCorrect && !aCorrect){
	    correctChoices.removeElementAt(bPos);
	    correctChoices.addElement(new Integer(a + 1));
	}
    }

    public void writeQuestionInfo(){
	int size = choices.size();

	try{
	    out.println("MSQUESTION " + id);
	    out.println(questionText);
	    out.println("ENDTEXT");
	    for(int c = 0; c < size; c++){
		out.println((String)choices.elementAt(c));
		out.println("ENDCHOICE");
	    }
	    out.println("ANSWER");
	    size = correctChoices.size();
	    for(int a = 0; a < size; a++){
		out.println(((Integer)correctChoices.elementAt(a)).intValue());
	    }
	    out.println("ENDANSWER");
	}catch(Exception e){
	    System.err.println(e.toString() + 
			       " at writeQuestionInfo() in msQuestion: " + id);
	    e.printStackTrace();
	}
    }

    public void animalWriteQuestionInfo(){
	int size = choices.size();

	try{
	    out.println("MSQUESTION \"" + id + "\"");
	    out.println(questionText);
	    out.println("ENDTEXT");
	    for(int c = 0; c < size; c++){
		out.println((String)choices.elementAt(c));
		out.println("ENDCHOICE");
	    }
	    out.println("ANSWER");
	    size = correctChoices.size();
	    for(int a = 0; a < size; a++){
		out.println(((Integer)correctChoices.elementAt(a)).intValue());
	    }
	    out.println("ENDANSWER");
	}catch(Exception e){
	    System.err.println(e.toString() + 
			       " at animalWriteQuestionInfo() in msQuestion: " 
                               + id);
	    e.printStackTrace();
	}
    }
}

