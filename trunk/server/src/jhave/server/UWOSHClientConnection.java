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
import jhave.Algorithm;
import jhave.server.parser.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.net.*;
import java.util.logging.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.input.JDOMParseException;
import javax.xml.xpath.*;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

public class UWOSHClientConnection extends ClientConnection implements TransactionCodes
{
    public UWOSHClientConnection(Socket s, int uid)
    {
    	super(s,uid);
    }

    /* Accepts the login information from the student and then sends
     * the appropriate information back to the server.  Since this is
     * only a "fake" client and server at this stage to only input
     * that will generate a successful login is "hanoi login
     * password"*/
    void cmd_requestQuizLogin(StringTokenizer st) throws InvalidCommandException
    {
    	logger.info("Received quiz login request from client");
    	if( st.countTokens() != 3 )
    		throw new InvalidCommandException("Expecting 3 tokens for quiz login, received " + st.countTokens());
    	String quiz_name = st.nextToken();
    	String email = st.nextToken();
    	String password = st.nextToken();

    	logger.info(quiz_name+" "+email+" "+password);
    }

    /* Accepts an XML file in the form of a string.  This string is
     * then passed to a DOMParser where it is validated.  If the
     * validation is successful the data base is updated. If the
     * validation is not the error is returned to the client.  If the
     * file is not in XML format but rather the old GAIGS syntax,
     * things still work but less infor is returned to the student in
     * the email that is constructed.
     */
    void cmd_quizCompleted(String xmlString)
    {
    	logger.info("Received quiz completed call from client");
    	try
    	{
    		SAXBuilder builder = new SAXBuilder();
    		org.jdom.Document doc = builder.build(new StringReader(xmlString));
    		Iterator it = doc.getDescendants();
    		it.next();
    		Element quizResults = (Element)it.next();
    		String quizID = quizResults.getAttributeValue("quizId");
    		String studentID = quizResults.getAttributeValue("studentId");
    		String numQuestions = quizResults.getAttributeValue("numQuestions");
    		String numCorrect = quizResults.getAttributeValue("numCorrect");
    		String startTime = quizResults.getAttributeValue("startTime"); /* we don't care about this anymore */
    		String endTime = quizResults.getAttributeValue("endTime");
		String HUTstudentID = quizResults.getAttributeValue("HUTstudentID");
		String HUTquizID = quizResults.getAttributeValue("HUTquizID");
		String HUTcourseID = quizResults.getAttributeValue("HUTcourseID");
		String HUTnormalizedScore = quizResults.getAttributeValue("HUTnormalizedScore");
		String HUTseed = quizResults.getAttributeValue("HUTseed");
    		String visualExt = "";
    		
    		for( int i = 0; i < algoList.size(); i++)
    		{
    			Algorithm temp = (Algorithm)algoList.get(i);
    			if( quizID.equals(temp.GetAlgoName()))
    			{
    				if("gaigs".equalsIgnoreCase(temp.GetVisualizerType())) visualExt = ".sho";
    				else if("samba".equalsIgnoreCase(temp.GetVisualizerType())) visualExt = ".sam";
    				else if("animalscript".equalsIgnoreCase(temp.GetVisualizerType())) visualExt = ".asu";
    				else if("animal".equalsIgnoreCase(temp.GetVisualizerType())) visualExt = ".aml";
    				else if("xaal".equalsIgnoreCase(temp.GetVisualizerType())) visualExt = ".xaal";
    				else if("matrix".equalsIgnoreCase(temp.GetVisualizerType())) visualExt = ".matrix";
    				break;
    			}
    		}

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String dbURL = "jdbc:mysql://localhost/Jhave";
		Connection conn = DriverManager.getConnection(dbURL,"root",System.getProperty("jhave.server.dbpassword"));
		
		Statement stmt = conn.createStatement();
		String query = "select send_email from user where email='" + studentID + "'";
		ResultSet res = stmt.executeQuery(query);

		res.next();
		Byte sendEmail = res.getByte("send_email");

		if(sendEmail == 1)
		{
		    QuizEmailProcessor emailSender = new QuizEmailProcessor(this,quizID,studentID,numQuestions,numCorrect,endTime,
									    "root","//localhost/Jhave",visualExt,uid,xmlString,givenInput);
		    emailSender.processResults();
		}

		if(HUTstudentID != null)
		{
		    logger.info("1");
		    HUTProcessor HUTProc = new HUTProcessor(this,HUTstudentID,HUTquizID,HUTcourseID,HUTnormalizedScore,HUTseed,
							    numQuestions,numCorrect);
		    logger.info("2");
		    HUTProc.processResults();
		    logger.info("3");
		}		

		QuizDBProcessor DBWriter = new QuizDBProcessor(this,quizID,studentID,numQuestions,numCorrect,
							       endTime,"//localhost/Jhave","root");
		DBWriter.processResults();

    	}	
    	catch(JDOMParseException e)
    	{
	    logger.info("Sending " + FS_UNSUCCESSFUL_QUIZ_DB_UPDATE + " to client");
	    sendtoClient(""+FS_UNSUCCESSFUL_QUIZ_DB_UPDATE+" "+e.toString());
    	}
    	catch(Exception e)
    	{
	    e.printStackTrace();
	}
    }  
}		
