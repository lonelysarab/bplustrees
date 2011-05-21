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
 * This class is used to store a collection of <code>question</code> objects.
 * A <code>questionCollection</code> object contains a reference to the
 * <code>PrintWriter</code> output stream to which it is to write
 * <code>question</code> information and maintains a <code>Vector</code> of the
 * <code>question</code>s that have been added to the collection. Using a 
 * <code>questionCollection</code> in a script-producing program provides a 
 * simple mechanism for managing <code>question</code>s and properly inserting 
 * them into the showfile. 
 * <p>
 * Modifications to this class have been made that allow the user to use
 * probability functions to control the insertion of <code>question</code>s
 * into the showfile. These changes make it possible to evenly distribute 
 * <code>question</code>s throughout an animation and precisely control when 
 * <code>question</code>s are asked.
 * </p>
 * <p>
 * In order to use the <code>question</code> support provided by
 * <code>questionCollection</code>, a script-producing program must import the
 * package <code>exe</code>. Note that a <code>questionCollection</code> only
 * allows the insertion of <code>question</code>s of the types 
 * <code>fibQuestion</code>, <code>mcQuestion</code>, <code>msQuestion</code>,
 * and <code>tfQuestion</code>. Therefore, this class is intended for use with
 * programs that produce scripts in the ANIMAL or old GAIGS formats.
 * </p>
 *
 * @author ? (original author)
 * @author Ben Tidman (probability modifications and original comments)
 * @author Andrew Jungwirth (editing and Javadoc comments)
 */
public class questionCollection{
    /* DATA: */

    /** 
     * Stores a reference to the <code>PrintWriter</code> output stream to
     * which this <code>questionCollection</code> should write its information.
     */
    PrintWriter out;

    /**
     * Maintains a list of the <code>question</code> objects that have been 
     * added to this <code>questionCollection</code>.
     */
    Vector <question> Questions;

    /** 
     * Stores the maximum number of <code>question</code>s to be inserted into
     * the showfile.
     * This value is used to control probabilistic insertion of 
     * <code>question</code> objects into this <code>questionCollection</code>.
     * If <code>questionCollection(PrintWriter)</code> was used to construct
     * this <code>questionCollection</code>, <code>numQuestions</code> is set
     * to -1 to indicate that the probability functions should not be used.
     */
    double numQuestions;

    /**
     * Stores the number of possible opportunities to insert
     * <code>question</code>s into the showfile. 
     * This value is used to control probabilistic insertion of 
     * <code>question</code> objects into this <code>questionCollection</code>.
     * By setting this variable to be the number of snapshots in the showfile,
     * <code>question</code>s can be evenly distributed throughout the 
     * animation. If <code>questionCollection(PrintWriter)</code> was used to
     * construct this <code>questionCollection</code>, <code>numPossible</code>
     * is set to -1 to indicate that the probability functions should not be 
     * used.
     */
    double numPossible;

    /**
     * Counts the number of <code>question</code> objects added to this
     * <code>questionCollection</code>.
     * This number is used to determine the probability of whether a question
     * will be added to the showfile when <code>addQuestion(question)</code> is
     * called.
     */
    double countq; 

    /**
     * Used in conjunction with <code>countq</code> to determine the
     * probability of inserting a <code>question</code> when 
     * <code>addQuestion(question)</code> is called.
     */
    double countp;

    /**
     * Stores the current probability that a <code>question</code> will be
     * added to the showfile when <code>addQuestion(question)</code> is called.
     */
    double prob;

    /* METHODS: */

    /**
     * Constructs a new <code>questionCollection</code> by specifying the 
     * <code>PrintWriter</code> output stream to which this collection should
     * write its information. 
     * <p>
     * This constructor creates a <code>questionCollection</code> that does not
     * use any of the new probability functions. Therefore, this constructor
     * can be used by script-producing programs that use different methods to
     * determine when <code>question</code>s should be added to the showfile.
     * </p>
     *
     * @param out Indicates the <code>PrintWriter</code> output stream to which
     *            this <code>questionCollection</code> should write its
     *            information.
     */
    public questionCollection(PrintWriter out){
        this.out = out;
        Questions = new Vector <question> (10, 20);
        numQuestions = -1;
        numPossible = -1;
        countq = 0.0;
	countp = 0.0;
	prob = 1.0;
    }

    /**
     * Constructs a new <code>questionCollection</code> by specifying the
     * <code>PrintWriter</code> output stream to which this collection should 
     * write its information and the maximum number of <code>question</code>s
     * that can be added to this collection.
     * <p>
     * Invoking this constructor creates a <code>questionCollection</code> that
     * uses the value passed to <code>numq</code> to determine the probability
     * of adding a <code>question</code> when <code>addQuestion</code> is
     * called.
     * </p>
     *
     * @param out  Specifies the <code>PrintWriter</code> output stream to 
     *             which this <code>questionCollection</code> should write its 
     *             information.
     * @param numq Indicates the maximum number of <code>question</code>s that
     *             can be added to this <code>questionCollection</code>. This 
     *             value is used to determine the probability of adding 
     *             <code>question</code>s to this collection.
     */
    public questionCollection(PrintWriter out, int numq){
        this.out = out;
        Questions = new Vector <question> (10, 20);
        numQuestions = (double)numq;
        numPossible = -1.0;
        countq = 0.0;
	countp = 0.0;
	prob = .25;
    }

    /**
     * Constructs a new <code>questionCollection</code> by specifying the 
     * <code>PrintWriter</code> output stream to which this collection should
     * write its information, the maximum number of <code>question</code>s that
     * can be added to this collection, and the number of possibilities to add
     * <code>question</code>s to this collection.
     * <p>
     * Calling this constructor creates a <code>questionCollection</code> that
     * uses the value passed to <code>numq</code> to determine the probability
     * of adding a <code>question</code> when <code>addQuestion</code> is 
     * called and uses the value passed to <code>nump</code> to evenly
     * distribute the <code>question</code>s throughout the showfile's
     * snapshots.
     * </p>
     *
     * @param out  Indicates the <code>PrintWriter</code> output stream to 
     *             which this <code>questionCollection</code> should write its
     *             information.
     * @param numq Sets the maximum number of <code>question</code>s that can
     *             be added to this <code>questionCollection</code>. This value
     *             is used to determine the probability of adding 
     *             <code>question</code>s to this collection.
     * @param nump Specifies the number of opportunities to add a 
     *             <code>question</code> to this 
     *             <code>questionCollection</code>. This number is used to
     *             evenly distribute <code>question</code>s throughout the
     *             showfile.
     */
    public questionCollection(PrintWriter out, int numq, int nump){
        this.out = out;
        Questions = new Vector <question> (10, 20);
        numQuestions = (double)numq;
        numPossible = (double)nump;
        countq = 0.0;
	countp = 0.0;
	prob = numQuestions / numPossible;
    }

    /**
     * Adds a <code>question</code> to this <code>questionCollection</code>.
     * If this collection was created by calling 
     * <code>questionCollection(PrintWriter)</code>, the <code>question</code>
     * will always be added to this collection. If either of the other two
     * constructors was used to create this <code>questionCollection</code>, 
     * the probability functions will be used to determine whether the 
     * <code>question</code> is added to this collection. Note that, in these
     * cases, the <code>question</code> will never be added if the maximum 
     * number of <code>question</code>s has been reached.
     *
     * @param q Indicates the <code>question</code> that should be added to
     *          this collection. Note that only <code>fibQuestion</code>,
     *          <code>mcQuestion</code>, <code>msQuestion</code>, and
     *          <code>tfQuestion</code> objects can be added to a
     *          <code>questionCollection</code>; any other type of object will
     *          never be added to this collection.
     * @return  Gives a value of <code>true</code> if the specified 
     *          <code>question</code> was added to this collection; otherwise, 
     *          <code>false</code> is returned.
     */ 
    public boolean addQuestion(question q){
	// Only add the question if it is a valid question for this collection.
	if(q instanceof fibQuestion || q instanceof mcQuestion ||
	   q instanceof msQuestion || q instanceof tfQuestion){
	    double rand = Math.random();
	    boolean always = false;
	
	    // If there are fewer possibilities than questions that still need 
	    // to be asked, ask the question.
	    if(numPossible != -1 && 
	       ((numQuestions - countq) + countp) >= numPossible){
		always = true;
	    }

	    // Prevent adding too many questions.
	    if(numQuestions != -1 && countq >= numQuestions){
		always = false;
	    }

	    // If the number of questions has not been specified, add the
	    // question. Otherwise only add the question if the above
	    // conditions have been met.
	    if(numQuestions == -1 || always || 
	       (countq < numQuestions && (rand + prob) >= 1.0)){
		Questions.addElement(q);
		countq++;
		always = true;
	    }else{
		always = false;
	    }

	    incPos();

	    return always;
	}else{
	    return false;
	}
    }

    /**
     * Adds a <code>question</code> to this <code>questionCollection</code> and
     * gives the user the option to ignore the probability functions to ensure
     * that the <code>question</code> will be added. 
     * This method functions identically to <code>addQuestion(question)</code>,
     * except that it allows the user to force adding the <code>question</code>
     * to this collection as long as the maximum number of 
     * <code>question</code>s has not already been reached.
     *
     * @param q      Indicates the <code>question</code> that should be added
     *               to this collection. Note that only 
     *               <code>fibQuestion</code>, <code>mcQuestion</code>, 
     *               <code>msQuestion</code>, and <code>tfQuestion</code> 
     *               objects can be added to a <code>questionCollection</code>;
     *               any other type of object will never be added to this 
     *               collection.
     * @param always Specifies if the probability functions should be used to
     *               determine whether the <code>question</code> is added or 
     *               ignored. A value of <code>true</code> bypasses the 
     *               probability functions and causes the <code>question</code>
     *               to be added as long as the maximum number of 
     *               <code>question</code>s has not already been reached. A 
     *               value of <code>false</code> makes this method function
     *               identically to <code>addQuestion(question)</code>.
     * @return       Yields a value of <code>true</code> if the given
     *               <code>question</code> was added to this collection;
     *               otherwise returns <code>false</code>.
     */
    public boolean addQuestion(question q, boolean always){
	// Only add the question if it is a valid question for this collection.
	if(q instanceof fibQuestion || q instanceof mcQuestion ||
	   q instanceof msQuestion || q instanceof tfQuestion){
	    double rand = Math.random();

	    // If there are fewer possibilities than questions that still need
	    // to be asked, ask the question.
	    if(numPossible != -1 && 
	       ((numQuestions - countq) + countp) >= numPossible){
		always = true;
	    }

	    // Prevent adding too many questions.
	    if(numQuestions != -1 && countq >= numQuestions){
		always = false;
	    }

	    // If the number of questions has not been specified, add the
	    // question. Otherwise only add the question if the above
	    // conditions have been met.
	    if(numQuestions == -1 || always || 
	       (countq < numQuestions && (rand + prob) >= 1.0)){
		Questions.addElement(q);
		countq++;
		always = true;
	    }else{
		always = false;
	    }

	    incPos();

	    return always;
	}else{
	    return false;
	}
    }

    /**
     * Increments <code>countp</code> and then adjusts the probability of
     * inserting a <code>question</code> accordingly.
     * This method is used internally by the class and should probably have
     * been made <code>private</code> but was left <code>public</code> to 
     * ensure compatibility with old code. There will likely never be any need
     * to call on this method explicitly.
     */
    public void incPos(){
	countp++;
	adjustProbability();
    }

    /**
     * Recalculates the probability of inserting a <code>question</code> into
     * this <code>questionCollection</code>.
     * This method is called by <code>incPos()</code> and should probably have
     * been made <code>private</code> but was left <code>public</code> to 
     * ensure compatibility with old code. There will likely never be any need
     * to call on this method explicitly.
     */
    public void adjustProbability(){
	if(numQuestions > 0 && numPossible > 0 && (numPossible - countp) > 0){
	    prob = (double)((numQuestions - countq) / (numPossible - countp));
	}
    }

    /**
     * Inserts the old GAIGS format reference for the <code>question</code> 
     * stored at index <code>index</code> in this 
     * <code>questionCollection</code>'s <code>Vector</code> of 
     * <code>question</code>s at the current point in the showfile.
     *
     * @param index Specifies which <code>question</code>'s reference should be
     *              inserted into the showfile by giving its index in this
     *              collection's <code>Vector</code> of <code>question</code>s.
     *              If the value of <code>index</code> is not a valid index to
     *              this <code>Vector</code>, no <code>question</code> 
     *              reference is inserted.
     * @deprecated  Replaced by {@link #insertQuestion(String)}. Since calls to
     *              <code>addQuestion</code> do not always add a 
     *              <code>question</code> to this collection (due to the 
     *              probability functions), a <code>question</code>'s 
     *              <code>id</code> might be different from its index in the 
     *              <code>Vector</code>, causing this method to insert a 
     *              different <code>question</code> than the programmer 
     *              intended. Therefore, this method is unreliable when using
     *              the new probability functions and remains only to support 
     *              old code.
     */
    public void insertQuestion(int index){
        if(index >= 0 && index < Questions.size()){
	    ((question)Questions.elementAt(index)).insertQuestion();
	}
    }

    /**
     * Inserts the old GAIGS format reference for the <code>question</code> 
     * with <code>id ID</code> in this <code>questionCollection</code> at the 
     * current point in the showfile. 
     * <p>
     * This method does a linear search on the <code>Vector</code> of 
     * <code>question</code>s to find the <code>question</code> with an 
     * <code>id</code> that matches <code>ID</code>. If a match is found, the
     * reference for the matching <code>question</code> is inserted into the
     * current point in the animation file; otherwise, no change is made to the
     * showfile.
     * </p>
     *
     * @param ID Indicates the <code>id</code> value of the 
     *           <code>question</code> that should be asked in the current 
     *           snapshot of the animation. If no <code>question</code> is 
     *           found with an <code>id</code> that matches <code>ID</code>,
     *           no <code>question</code> reference is inserted into the
     *           showfile. If a match is found, a reference to the matching
     *           <code>question</code> is inserted at the current point in the
     *           specified <code>PrintWriter</code> output stream.
     */
    public void insertQuestion(String ID){
	boolean found = false;
	int x = 0;
	
	while(!found && x < Questions.size()){
    	    if(ID.equals(((question)Questions.elementAt(x)).id)){
	        found = true;
		((question)Questions.elementAt(x)).insertQuestion();
	    }
	    x++;
	}
    }

    /**
     * Inserts the ANIMAL format reference for the <code>question</code> stored
     * at index <code>index</code> in this <code>questionCollection</code>'s 
     * <code>Vector</code> of <code>question</code>s at the current point
     * in the showfile.
     *
     * @param index Specifies which <code>question</code>'s reference should be
     *              inserted into the showfile by giving its index in this
     *              collection's <code>Vector</code> of <code>question</code>s.
     *              If the value of <code>index</code> is not a valid index to
     *              this <code>Vector</code>, no <code>question</code> 
     *              reference is inserted.
     * @deprecated  Replaced by {@link #animalInsertQuestion(String)}. Since 
     *              calls to <code>addQuestion</code> do not always add a 
     *              <code>question</code> to this collection (due to the 
     *              probability functions), a <code>question</code>'s 
     *              <code>id</code> might be different from its index in the 
     *              <code>Vector</code>, causing this method to insert a 
     *              different <code>question</code> than the programmer 
     *              intended. Therefore, this method is unreliable when using
     *              the new probability functions and remains only to support 
     *              old code.
     */
    public void animalInsertQuestion(int index){
        if((index >= 0)&&(index < Questions.size())){
            ((question)Questions.elementAt(index)).animalInsertQuestion();
	}
    }

    /**
     * Inserts the ANIMAL format reference for the <code>question</code> with 
     * <code>id ID</code> in this <code>questionCollection</code> at the 
     * current point in the showfile.
     * <p>
     * This method does a linear search on the <code>Vector</code> of 
     * <code>question</code>s to find the <code>question</code> with an 
     * <code>id</code> that matches <code>ID</code>. If a match is found, the
     * reference for the matching <code>question</code> is inserted into the
     * current point in the animation file; otherwise, no change is made to the
     * showfile.
     * </p>
     *
     * @param ID Indicates the <code>id</code> value of the 
     *           <code>question</code> that should be asked in the current 
     *           snapshot of the animation. If no <code>question</code> is 
     *           found with an <code>id</code> that matches <code>ID</code>,
     *           no <code>question</code> reference is inserted into the
     *           showfile. If a match is found, a reference to the matching
     *           <code>question</code> is inserted at the current point in the
     *           specified <code>PrintWriter</code> output stream.
     */
    public void animalInsertQuestion(String ID){
	boolean found = false;
	int x = 0;

	while(!found && x < Questions.size()){
	    if(ID.equals(((question)Questions.elementAt(x)).id)){
		found = true;
		((question)Questions.elementAt(x)).animalInsertQuestion();
	    }
	    x++;
	}
    }

    /**
     * Writes out the information for each <code>question</code> stored in this
     * collection in the old GAIGS format.
     * Calling this method after the rest of the animation file has been 
     * written causes the <code>question</code> information to be appended to 
     * the end of the file so that the <code>question</code>s can be added to
     * the animation at the snapshots in which their references appear.
     */
    public void writeQuestionsAtEOSF(){
        try{
            out.println("STARTQUESTIONS");
        }catch(Exception e){
            System.out.println(e.toString() + " at writeQuestionsAtEOSF()");
            e.printStackTrace();
        }
        for(int x = 0; x < Questions.size(); x++){
            ((question)Questions.elementAt(x)).writeQuestionInfo();
        }
    }

    /**
     * Writes out the information for each <code>question</code> stored in this
     * collection in the ANIMAL format.
     * Calling this method after the rest of the animation file has been 
     * written causes the <code>question</code> information to be appended to 
     * the end of the file so that the <code>question</code>s can be added to
     * the animation at the snapshots in which their references appear.
     */
    public void animalWriteQuestionsAtEOSF(){
        try{
            out.println("STARTQUESTIONS");
        }catch(Exception e){
            System.out.println(e.toString() + 
			       " at animalWriteQuestionsAtEOSF()");
            e.printStackTrace();
        }
        for(int x = 0; x < Questions.size(); x++){
            ((question)Questions.elementAt(x)).animalWriteQuestionInfo();
        }
    }
}

