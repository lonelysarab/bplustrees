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
 * This is the abstract base class that defines the essential data and methods 
 * of a <code>question</code> object.
 * A <code>question</code> object encapsulates the state information needed to
 * uniquely identify the <code>question</code>, print the 
 * <code>question</code>'s information to a <code>PrintWriter</code> output 
 * stream, and store the text of the <code>question</code>.
 * <p>
 * In order to use the question support given by <code>question</code> and its
 * derived classes in a script-producing program, the package <code>exe</code>
 * must be imported in the script-producing program.
 * </p>
 *
 * @author ? (original author)
 * @author Ben Tidman (original comments)
 * @author Andrew Jungwirth (editing and Javadoc comments)
 */
public abstract class question{
    /* DATA: */

    /**
     * Defines the <code>String</code> value that is used to uniquely identify
     * this <code>question</code> from other <code>question</code> objects in a
     * collection.
     */
    protected String id;

    /**
     * Specifies the <code>PrintWriter</code> output stream to which the
     * <code>question</code> is to perform its write operations.
     */
    protected PrintWriter out;

    /**
     * Stores the text that is displayed in the question window for this
     * <code>question</code>.
     */
    protected String questionText;

    /**
     * Specifies whether this <code>question</code> is marked as must be asked
     * when probablistic questioning is used.  Default is false.
     */
    protected boolean mustBeAsked = false;
    
    /* METHODS: */

    /**
     * Constructs a new <code>question</code> object that is distinguished by
     * <code>id</code> and outputs its information to <code>out</code>.
     *
     * @param out Specifies the output stream to which this 
     *            <code>question</code> should perform its write operations.
     * @param id  Gives this <code>question</code> a <code>String</code> that 
     *            can be used to uniquely identify it from other 
     *            <code>question</code> objects in a collection.
     */
    public question(PrintWriter out, String id){
	this.out = out;
	this.id = id.trim();
    }

    /**
     * Sets the text that is displayed in the question pop-up window when this
     * <code>question</code> is included in a showfile.
     *
     * @param questionText Specifies the text to be displayed in the question 
     *                     window when this <code>question</code> is asked.
     */
    public void setQuestionText(String questionText){
        this.questionText = questionText.trim();
    }
    
    /**
     * Specifies whether this <code>question</code> is mark as must be asked
     * when probablistic questioning is used.
     *
     * @param flag   the new value for <code>mustBeAsked</code>.
     */
    public void setMustBeAsked(boolean flag){
        this.mustBeAsked = flag;
    }
    
    /**
     * Returns this <code>question</code>'s <code>id</code>.
     *
     * @return Gives the <code>String</code> value used to uniquely identify
     *         this <code>question</code>.
     */
    public String getID(){
        return id;
    }

    /**
     * Writes this <code>question</code>'s information at the current position
     * in the specified <code>PrintWriter</code> output stream using the GAIGS
     * question format.
     */
    public abstract void writeQuestionInfo();

    /** 
     * Writes this <code>question</code>'s information at the current position
     * in the specified <code>PrintWriter</code> output stream using the ANIMAL
     * question format.
     */
    public abstract void animalWriteQuestionInfo();

    /**
     * Writes a reference to this <code>question</code> at the current position
     * in the specified <code>PrintWriter</code> output stream using the GAIGS
     * question reference format.
     */
    public abstract void insertQuestion();

    /**
     * Writes a reference to this <code>question</code> at the current position
     * in the specified <code>PrintWriter</code> output stream using the ANIMAL
     * question reference format.
     */
    public abstract void animalInsertQuestion();
}

