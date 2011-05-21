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

package jhave.server;

import jhave.core.TransactionCodes;

/** This is the superclass for any subclass that processes the results
 * of a quiz.
 */
public abstract class QuizResultProcessor implements TransactionCodes
{
    protected String quizID;
    protected String studentID;
    protected String numQuestions;
    protected String numCorrect;
    protected String endTime;
    protected ClientConnection clientConn;

    /** Constructor for the QuizResultProcessor
     *
     * @param clientConn The associated ClientConnection (should just be "this"
     * @param quizID name of the quiz taken
     * @param studentID login of the student
     * @param numQuestions number of questions in the quiz
     * @param numCorrect number of question answered correctly
     * @param endTime the time the quiz was finished
     */
    protected QuizResultProcessor(ClientConnection clientConn,String quizID,String studentID,String numQuestions,String numCorrect,
			       String endTime)
    {
	this.clientConn = clientConn;
	this.quizID = quizID;
	this.studentID = studentID;
	this.numQuestions = numQuestions;
	this.numCorrect = numCorrect;
	this.endTime = endTime;
    }

    /** Processes the results that are stored within the
     * QuizResultProcessor object based on definition of the subclass.
     */
    abstract void processResults() throws Exception;
}