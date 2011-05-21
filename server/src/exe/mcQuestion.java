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
 * mulitple-choice questions.
 * <p>
 * In order to use <code>mcQuestion</code> or any of <code>question</code>'s 
 * other derived classes in a script-producing program, the script-producing 
 * program must import the package <code>exe</code>.
 * </p>
 * <p>
 * An <code>mcQuestion</code> is added to a showfile by placing a question
 * reference at the end of the snapshot in which the question is to appear and
 * then by appending the question information to the question section at the 
 * end of the file.
 * </p>
 * <p>
 * Question reference for an <code>mcQuestion</code>:
 * </p>
 * <p>
 * MCQUESTION id
 * </p>
 * <p> 
 * Question information for an <code>mcQuestion</code>:
 * </p>
 * <p>
 * MCQUESTION id
 * <br>The question text appears here.</br>
 * <br>ENDTEXT</br>
 * <br>The correct answer choice appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>Incorrect answer choice 1 appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>Incorrect answer choice 2 appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>. . .</br>
 * <br>Incorrect answer choice <i>n</i> appears here.</br>
 * <br>ENDCHOICE</br>
 * <br>ANSWER</br>
 * <br>1</br>
 * <br>ENDANSWER</br>
 * </p>
 * <p>
 * Note that if the correct answer is added first, the correct answer is 1; if 
 * the correct answer is added second, the correct answer is 2; ...; if the 
 * correct answer is added as item <i>n</i>, the correct answer is <i>n</i>.
 * After all the answers have been added and the correct answer has been set,
 * the answer choices can be shuffled using the <code>shuffle()</code> method.
 *
 * @author ? (original author)
 * @author Ben Tidman (original comments)
 * @author Andrew Jungwirth (editing and Javadoc comments)
 */
public class mcQuestion extends question{
    /* DATA: */

    /**
     * Holds the answer choices for this question.
     */
    protected Vector <String> choices;

    /**
     * Keeps track of the correct answer's position in the <code>Vector</code> 
     * of choices. 
     * Note that the value stored is one greater than the index of the answer 
     * in the <code>Vector</code>. For instance, if the correct answer is the 
     * first answer stored in <code>choices</code>, the value of 
     * <code>correctChoice</code> is 1, not 0.
     */
    protected int correctChoice;

    /**
     * Stores the <code>String</code> value of the correct answer.
     */
    protected String correctChoiceStr;
    
    /* METHODS: */

    /**
     * Constructs a new <code>mcQuestion</code> object that is distinguished
     * by <code>id</code> and outputs its information to <code>out</code>.
     *
     * @param out Specifies the output stream to which this 
     *            <code>mcQuestion</code> should perform its write operations.
     * @param id  Gives this <code>mcQuestion</code> a <code>String</code> that
     *            can be used to uniquely identify it from other 
     *            <code>question</code> objects in a collection.
     */    
    public mcQuestion(PrintWriter out, String id){
	super(out, id);
        choices = new Vector <String> (10, 20);
        correctChoice = 0;
        correctChoiceStr = null;
    }

    public void insertQuestion(){
        try{
            out.println("MCQUESTION " + id);
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at insertQuestion() in mcQuestion: " + id);
	    e.printStackTrace();
        }
    }

    public void animalInsertQuestion(){
        try{
            out.println("MCQUESTION \"" + id + "\"");
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalInsertQuestion() in mcQuestion: " + 
			       id);
	    e.printStackTrace();
        }
    }

    /**
     * Adds an answer choice to the <code>Vector</code> that stores the answer 
     * choices for this <code>mcQuestion</code>.
     * The value passed to <code>choice</code> is added to the end of the 
     * <code>Vector</code> of answer choices.
     *
     * @param choice Specifies the text of the answer choice that is to be
     *               added to the <code>Vector</code> of choices. This is the
     *               answer choice that appears in the question window when 
     *               this <code>mcQuestion</code> is asked.
     */
    public void addChoice(String choice){
        if((correctChoiceStr != null) && 
	   (correctChoiceStr.equals(choice.trim()))){
            correctChoice = choices.size() + 1;
	}
        choices.addElement(choice.trim());
    }

    /**
     * Sets the <code>correctChoice</code> variable that is used to track the
     * position of the correct answer to this <code>mcQuestion</code>. 
     * The value passed to <code>choice</code> is set as the
     * <code>correctChoice</code>.
     * <p>
     * Note that the value passed to <code>choice</code> must be one greater 
     * than the correct answer choice's index in the 
     * <code>choices Vector</code>. For example, the first answer added via 
     * <code>addChoice(String)</code> is 1.
     * </p>
     *
     * @param choice Indicates the correct answer to this 
     *               <code>mcQuestion</code>. The value must be one greater
     *               than the answer's index in the <code>Vector</code> of 
     *               choices, as described above.
     */
    public void setAnswer(int choice){
        correctChoice = choice;
    }

    /**
     * Sets the <code>correctChoice</code> variable that is used to track the
     * position of the correct answer to this <code>mcQuestion</code> by 
     * finding the answer choice that matches the value passed to 
     * <code>choice</code>.
     * This method performs a linear search through the <code>Vector</code> of
     * choices in order to find the specified choice. If the value passed to
     * <code>choice</code> does not exactly match any of the answer choices,
     * <code>correctChoice</code> is not changed. As such, this method should
     * not be used in new code and only remains to support legacy code.
     *
     * @param choice Defines the <code>String</code> for which this method 
     *               searches to find the correct answer to this 
     *               <code>mcQuestion</code>.
     * @deprecated   Replaced by {@link #setAnswer(int)}. Since this method may
     *               fail to properly change the value of 
     *               <code>correctChoice</code>, this method should not be used
     *               in new code.
     */
    public void setAnswer(String choice){
        for(int x = 0; x < choices.size(); x++){
            if(((String)choices.elementAt(x)).equals(choice.trim())){
                correctChoice = x + 1;
                return;
            }
        }
        correctChoiceStr = choice;
    }

    /**
     * Randomly exchanges the positions of the answer choices contained within
     * <code>choices</code> so that the answer choices appear in a different
     * order than they were added.
     * Whenever the correct answer choice is swapped with another choice, the
     * value of <code>correctChoice</code> is changed so its value still
     * accurately tracks the correct answer to this <code>mcQuestion</code>.
     */
    public void shuffle(){
	int rand;
	int size = choices.size();

	for(int x = 0; x < size; x++){	
	    rand = (int)(Math.random() * (size - 1)) + 1;
	    if(rand != x){
		swap(x, rand);
	    }
	}
    }

    /**
     * Swaps the locations of two answer choices in the <code>Vector</code> of
     * choices. 
     * If one of the specified answer choices corresponds to the 
     * correct answer <code>correctChoice</code> is changed accordingly.
     *
     * @param a Indicates the index in the <code>choices Vector</code> of the
     *          first answer choice to be swapped.
     * @param b Indicates the index in the <code>choices Vector</code> of the
     *          second answer choice to be swapped.
     */
    private void swap(int a, int b){
	String temp;

	temp = choices.get(a);
	choices.set(a, choices.get(b));
	choices.set(b, temp);

	if((a + 1) == correctChoice)
	    correctChoice = (b + 1);
	else if((b + 1) == correctChoice)
	    correctChoice = (a + 1);
    }

    public void writeQuestionInfo(){
        try{
            out.println("MCQUESTION " + id);
            out.println(questionText);
            out.println("ENDTEXT");
            for(int x = 0; x < choices.size(); x++){
                out.println((String)choices.elementAt(x));
                out.println("ENDCHOICE");
            }
            out.println("ANSWER");
            out.println(correctChoice);
            out.println("ENDANSWER");
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at writeQuestionInfo() in mcQuestion: " + id);
            e.printStackTrace();
        }
    }    

    public void animalWriteQuestionInfo(){
        try{
            out.println("MCQUESTION \"" + id + "\"");
            out.println(questionText);
            out.println("ENDTEXT");
            for(int x = 0; x < choices.size(); x++){
                out.println((String)choices.elementAt(x));
                out.println("ENDCHOICE");
            }
            out.println("ANSWER");
            out.println(correctChoice);
            out.println("ENDANSWER");
        }
        catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalwriteQuestionInfo() in mcQuestion: " 
			       + id);
            e.printStackTrace();
        }
    }    
}
