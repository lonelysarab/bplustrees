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
 * QuizInfo.java
 *
 * Created on July 02, 2005
 */
package jhave.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

import jhave.core.JHAVETranslator;
import jhave.core.TransactionCodes;
import jhave.event.QuestionEvent;
import jhave.event.QuestionListener;
import jhave.question.Question;

/**
 *
 * This class stores data for quizzes and formats them as an XML string.
 * @author Adam Klein
 */
public class QuizInfo implements QuestionListener, TransactionCodes {
    
    /** Name of algorithm */
    private String quizId;
    /** Name of student */
    private String studentId;
    /** Number of questions in quiz */
    private int numQuestions;
    /** Number of questions correct */
    private int numCorrect;
    /** Date and time of quiz initialization */
    private String startTime;
    /** Date and time of quiz submission */
    private String endTime;
    /** List of questions asked */
    private LinkedList questions;
    /** Has the quiz info been sent **/
    private boolean quizSent = false;
   
    /** Reference to the server connection for sending the XML string */
    private ClientNetworkController networkController;
    /** DateFormat for creating timestamps */
    private SimpleDateFormat timeFormat;
    
    /** Creates a new instance of QuizInfo
     *  @param qid The quizID
     *  @param sid The studentID
     */
    public QuizInfo(String qid, String sid, ClientNetworkController controller) {
        quizId = qid;
        studentId = sid;
	networkController = controller;
        numQuestions = 0;
        
        questions = new LinkedList();
        
        timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startTime = timeFormat.format(new Date());
    }

    /**
     *  Uses a reference to the network controller to transmit
     *  the quiz results to the server.
     */
    public void sendResults() {
    	if (!quizSent) {
    		try {
    			System.out.println(JHAVETranslator.translateMessage("sendXMLString",
    					this.toString()));
//  			System.out.println("Sending XML String:\n" + this.toString());
    			networkController.sendXMLString(FC_QUIZ_COMPLETED, this.toString());
    			quizSent = true;
    		} catch(IOException e) {
    			System.out.println(JHAVETranslator.translateMessage("errorSendingQuizResults",
    					e.toString()));
//  			System.out.println("An error occured while trying to send quiz results: " + e.toString());
    		}
    	}
    }
    
    /**
     *  Uses a reference to the network controller to transmit
     *  the quiz results to the server.  This version is used for 
     *  Visualizers like Matrix, which are based on number of steps
     *  done correctly in the student-simulated simulation of algorithm.
     *  It could also be used in a situation where the student exits before
     *  completing the quiz and you want to assign a "0 correct out of 1" score.
     */
    public void sendResults(int numStepsCorrect, int numSteps) {
    	if (!quizSent) {
    		try {
    			numCorrect = numStepsCorrect;
    			numQuestions = numSteps;
    			System.out.println(JHAVETranslator.translateMessage("sendXMLString",
    					this.toString()));
//  			System.out.println("Sending XML String:\n" + this.toString());
    			networkController.sendXMLString(FC_QUIZ_COMPLETED, this.toString());
    			quizSent = true;
    		} catch(IOException e) {
    			System.out.println(JHAVETranslator.translateMessage("errorSendingQuizResults",
    					e.toString()));
//  			System.out.println("An error occured while trying to send quiz results: " + e.toString());
    		}
    	}
    }
    
    /** Adds questions as they are stated to the user
     *  @param e The QuestionEvent
     */
    public void handleQuestion(QuestionEvent e) {
	System.out.println(e.getSource());
        if(!questions.contains(e.getQuestion())) {
            questions.add(e.getQuestion());
            numQuestions++;
        }
    }

    /**
     * Returns the total number of questions in the quiz.
     * @return the total number of questions in the quiz.
     */
    public int getNumQuestions()
    {
	return numQuestions;
    }
    
    /**
     * Returns the number of questions the students got correct.
     * @return the number of questions the students got correct.
     */
    public int getNumCorrect()
    {
	return numCorrect;
    }

    /**
     * Returns if the quiz info has been sent to the server.
     * @return if the quiz info has been sent to the server.
     */
    public boolean getQuizSent()
    {
	return quizSent;
    }

    /** Updates data and returns an XML String describing the quiz
     *  @return The XML String
     */
    public String toString() {
        endTime = timeFormat.format(new Date());
        
        String quizString = "";
        if(!questions.isEmpty()) {
	    numCorrect = 0;
            ListIterator itr = questions.listIterator();
            while(itr.hasNext()) {
                Question q = (Question)itr.next();
                quizString += "\t<question questionNumber=\"" + q.getID() + "\" isCorrect=\"";
                
                if(q.isCorrect()) {
                    numCorrect++;
                    quizString += "1";
                } else {
                    quizString += "0";
                }
                quizString += "\" questionType=\"" + q.getTypeString()
                + "\">\n\t\t<correctAnswer>" + q.getAnswersString()
                + "</correctAnswer>\n\t\t<givenAnswer>\"" + q.getAnswer()
                + "\"</givenAnswer>\n\t</question>\n";
            }
        }
        
        String xmlString = "<?xml version = \"1.0\"?>\n"
	    + "<!DOCTYPE quizResults SYSTEM \"src/quizResults.dtd\">\n\n"
	    + "<quizResults quizId=\"" + quizId + "\" studentId=\"" + studentId
	    + "\" numQuestions=\"" + numQuestions + "\" numCorrect=\"" + numCorrect
	    + ((System.getProperty("jhave.client.TRAKLAusername") != null)
	       ? "\" HUTstudentID=\"" + System.getProperty("jhave.client.TRAKLAusername")
	       : "")
	    + ((System.getProperty("jhave.client.TRAKLAquizid") != null)
	       ? "\" HUTquizID=\"" + System.getProperty("jhave.client.TRAKLAquizid")
	       : "")
	    + ((System.getProperty("jhave.client.TRAKLAcourse") != null)
	       ? "\" HUTcourseID=\"" + System.getProperty("jhave.client.TRAKLAcourse")
	       : "")
	    + ((System.getProperty("jhave.client.TRAKLAnormalizedquizvalue") != null)
	       ? "\" HUTnormalizedScore=\"" + System.getProperty("jhave.client.TRAKLAnormalizedquizvalue")
	       : "")
	    + ((System.getProperty("jhave.client.TRAKLAseed") != null)
	       ? "\" HUTseed=\"" + System.getProperty("jhave.client.TRAKLAseed")
	       : "")
	    + "\" startTime=\"" + startTime + "\" endTime=\"" + endTime + "\">\n"
	    + quizString + "</quizResults>";

        return xmlString;
    }
}
