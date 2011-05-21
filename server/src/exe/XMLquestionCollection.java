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
 * This class stores a collection of XML <code>questions</code> to be 
 * displayed along with GAIGS snapshots. It inherits the functionality of the
 * original <code>questionCollection</code> class but is used to store
 * <code>question</code>s in the new GAIGS XML format.
 * <p>
 * An <code>XMLquestionCollection</code> object contains a reference to the 
 * <code>PrintWriter</code> output stream to which it is to write
 * <code>question</code> information and maintains a <code>Vector</code> of the
 * <code>question</code>s that have been added to the collection. Using an 
 * <code>XMLquestionCollection</code> in a script-producing program provides a 
 * simple mechanism for managing <code>question</code>s and properly inserting 
 * them into the showfile. 
 * </p>
 * <p>
 * In order to use the XML <code>question</code> support provided by
 * <code>XMLquestionCollection</code>, a script-producing program must import
 * the package <code>exe</code>. Note that an 
 * <code>XMLquestionCollection</code> only allows the insertion of 
 * <code>question</code>s of the types <code>XMLfibQuestion</code>, 
 * <code>XMLmcQuestion</code>, <code>XMLmsQuestion</code>, and 
 * <code>XMLtfQuestion</code>. Therefore, this class is intended for use with
 * programs that produce scripts in the new GAIGS XML format.
 * </p>
 *
 * @author Andrew Jungwirth
 * @author Myles McNally (changes in how probabilistic questioning occurs)
 */
public class XMLquestionCollection extends questionCollection{
    
    
    /* METHODS: */

    /**
     * Constructs a new <code>XMLquestionCollection</code> by specifying the 
     * <code>PrintWriter</code> output stream to which this collection should
     * write its information. 
     * <p>
     * This constructor creates an <code>XMLquestionCollection</code> that does
     * not use any of the new probability functions. Therefore, this 
     * constructor can be used by script-producing programs that use different
     * methods to determine when <code>question</code>s should be added to the
     * showfile.
     * </p>
     *
     * @param out Indicates the <code>PrintWriter</code> output stream to which
     *            this <code>XMLquestionCollection</code> should write its
     *            information.
     */
    public XMLquestionCollection(PrintWriter out){
    	super(out);
    }

    /**
     * Constructs a new <code>XMLquestionCollection</code> by specifying the
     * <code>PrintWriter</code> output stream to which this collection should 
     * write its information and the maximum number of <code>question</code>s
     * that can be added to this collection.
     * <p>
     * Invoking this constructor creates an <code>XMLquestionCollection</code> 
     * that uses the value passed to <code>numq</code> to determine the
     * probability actually asking a <code>question</code> when
     * <code>addQuestion</code> is called.
     * </p>
     *
     * @param out  Specifies the <code>PrintWriter</code> output stream to 
     *             which this <code>XMLquestionCollection</code> should write
     *             its information.
     * @param numq Indicates the maximum number of <code>question</code>s that
     *             can be added to this <code>XMLquestionCollection</code>.
     *             This value is used to determine the probability of actually 
     *             asking this<code>question</code>.
     */
    public XMLquestionCollection(PrintWriter out, int numq){
    	super(out, numq);
	}

    /**
     * Constructs a new <code>XMLquestionCollection</code> by specifying the 
     * <code>PrintWriter</code> output stream to which this collection should
     * write its information, the maximum number of <code>question</code>s that
     * can be added to this collection, and the number of possibilities to add
     * <code>question</code>s to this collection.
     * <p>
     * Calling this constructor creates an <code>XMLquestionCollection</code>
     * that uses the value passed to <code>numq</code> to determine the
     * probability of adding a <code>question</code> when
     * <code>addQuestion</code> is called and uses the value passed to
     * <code>nump</code> to evenly distribute the <code>question</code>s
     * throughout the showfile's snapshots.
     * </p>
     *
     * @param out  Indicates the <code>PrintWriter</code> output stream to 
     *             which this <code>XMLquestionCollection</code> should write
     *             its information.
     * @param numq Indicates the maximum number of <code>question</code>s that
     *             can be added to this <code>XMLquestionCollection</code>.
     *             This value is used to determine the probability of actually 
     *             asking this<code>question</code>.
     * @param nump Specifies the number of opportunities to add a 
     *             <code>question</code> to this 
     *             <code>XMLquestionCollection</code>. This number is used to
     *             evenly distribute <code>question</code>s throughout the
     *             showfile.  NO LONGER RELEVANT & NOW IGNORED
     * @deprecated  Replaced by {@link #XMLquestionCollection(PrintWriter, int))}.
     *              Due to changes in how <code>XMLquestionCollection</code> 
     *              handles probabilistic questioning, tne <code>nump</code>
     *              parameter is no longer necessary and is simply ignored.
     */
    public XMLquestionCollection(PrintWriter out, int numq, int nump){
    	super(out, numq);
    }

    /**
     * Adds a <code>question</code> to this <code>XMLquestionCollection</code>.
     *
     * @param q Indicates the <code>question</code> that should be added to
     *          this collection. Note that only <code>XMLfibQuestion</code>,
     *          <code>XMLmcQuestion</code>, <code>XMLmsQuestion</code>, and
     *          <code>XMLtfQuestion</code> objects can be added to an
     *          <code>XMLquestionCollection</code>; any other type of object 
     *          will never be added to this collection.
     * @return  Gives a value of <code>true</code> if the specified 
     *          <code>question</code> was added to this collection; otherwise, 
     *          <code>false</code> is returned.
     */ 
    public boolean addQuestion(question q){
    	// Only add the question if it is a valid question for this collection.
    	if (q instanceof XMLfibQuestion || q instanceof XMLmcQuestion ||
	       q instanceof XMLmsQuestion  || q instanceof XMLtfQuestion){
    		
    		Questions.addElement(q);
    		return true;
    		
    	} else
    		return false;
    }

    /**
     * Adds a <code>question</code> to this <code>XMLquestionCollection</code>
     * and gives the user the option to ignore the probability functions to
     * ensure that the <code>question</code> will be added. 
     *
     * @param q      Indicates the <code>question</code> that should be added
     *               to this collection. Note that only 
     *               <code>XMLfibQuestion</code>, <code>XMLmcQuestion</code>, 
     *               <code>XMLmsQuestion</code>, and 
     *               <code>XMLtfQuestion</code> objects can be added to an
     *               <code>XMLquestionCollection</code>; any other type of
     *               object will never be added to this collection.
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
    	if (q instanceof XMLfibQuestion || q instanceof XMLmcQuestion ||
    	    q instanceof XMLmsQuestion  || q instanceof XMLtfQuestion){  
            q.setMustBeAsked(always);
        		Questions.addElement(q);
        		return true;
        	} else
        		return false;
        }



    // This method writes out the questions at the end of the showfile. All
    // that must be done after calling this method is to print the </show> tag
    // to the output stream and call its close() method to finish producing the
    // showfile.
    /**
     * <p>Writes out the information for each <code>question</code> stored in this
     * collection in the new GAIGS XML format.
     * All that must be done after calling this method is to print the </show>
     * tag to the output stream and call its <code>close()</code> method to
     * finish producing the showfile. Printing the <code>question</code>s to
     * the end of the showfile with this method allows the 
     * <code>question</code>s to be properly inserted into the animation at the
     * snapshots in which their references appear.<p>
     * 
     * <p>If no limit on the number of questions asked has been set, all questions
     * in the collection are written out.  If a limit has been set, first all
     * questions marked as must be asked are written out (but not more than the
     * limit).  Then other questions are written out probablistically, up to the
     * limit (for both types of questions).  This ensures that exactly the limit
     * number of questions will be asked, as long, of course, that there are at least
     * that many questions in the collection.</p>
     * 
     */
	public void writeQuestionsAtEOSF() {
		int count = 0;
		try { int i;
			if (Questions.size() > 0){
				out.println("<questions>");
			
				// if no limit on the number of questions has been set,
				// just write them all out
				if (numQuestions == -1)
					for(int q = 0; q < Questions.size(); q++) {
						((question)Questions.elementAt(q)).writeQuestionInfo();
					}
			
				else { // a limit on the number of questions has been set
									
					// first write out questions marked "mustBeAsked", but not more than the limit
					for (int q = 0; q < Questions.size()  && count < numQuestions; q++)
						if ( ((question)Questions.elementAt(q)).mustBeAsked == true ) {
							((question)Questions.elementAt(q)).writeQuestionInfo();
							count++;
						}

					// then write out questions not so marked, up to the limit
					int skipCount = 0;
					double rand;
					for (int q = 0; q < Questions.size()  && count < numQuestions; q++) {
						rand = Math.random();
						if ( ((question)Questions.elementAt(q)).mustBeAsked != true )
							if (rand < questionProbability(count, skipCount)) {
								((question)Questions.elementAt(q)).writeQuestionInfo();
								count++;
							} else
								skipCount++;
					}
					}
				out.println("</questions>");
			}
			

		} catch (Exception e) {
			System.err.println(e.toString() + " at writeQuestionsAtEOSF()");
			e.printStackTrace();
		}
	}
	
   /**
    */
   private double questionProbability(int count, int skipCount){
   	double numPossible = Questions.size();

   	if(numQuestions > 0 && numPossible > 0 && (numPossible - (count + skipCount)) > 0) {
   		return (numQuestions - count) / (numPossible - (count + skipCount));
   	}
   	return 0;
   }
}