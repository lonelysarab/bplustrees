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
import java.util.*;
import java.io.*;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;

/** QuizResultProcessor for Finnish HUT server
 */
public class HUTProcessor extends QuizResultProcessor implements TransactionCodes
{
    private String courseID;
    private String score;
    private String seed;

    /** Constructor
     * 
     * @param clientConn The associated ClientConnection (should just be "this"
     * @param studentID login of the student
     * @param quizID name of the quiz taken
     * @param courseID the course the quiz was taken for
     * @param score the student's score on the quiz
     * @param seed HUT seed
     * @param numQuestions number of questions in the quiz
     * @param numCorrect number of correct answers
     */
    public HUTProcessor(ClientConnection clientConn,String studentID,String quizID,String courseID,
			String score,String seed,String numQuestions,String numCorrect)
    {
	super(clientConn,quizID,studentID,numQuestions,numCorrect,null);
    
	this.courseID = courseID;
	this.score = score;
	this.seed = seed;
    }

    /** Contacts the HUT server using the given parameters.
     */
    public void processResults() throws Exception
    {
	XmlRpcClient rpcClient;
	
	// This needs to change to reflect info on Finnish
	// server.  The default values are below, but they
	// can be overridden with command line params read
	// and set in GAIGSServer.java
	String DEFAULT_PORT = "9876";
	String DEFAULT_HOST = "trakla2.agrostis.cs.hut.fi";
	
	rpcClient = null;
	try
	{
	    int port = Integer.parseInt(System.getProperty("xmlrpc.server.port",DEFAULT_PORT));
	    String host = System.getProperty("xmlrpc.server.host",DEFAULT_HOST);
	    rpcClient = new XmlRpcClient(host,port);
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
	catch(NumberFormatException e)
	{
	    e.printStackTrace();
	}
	
	Vector params = new Vector();
	
	params.add(studentID);
	params.add(new Integer(courseID)); // This is an int for them and a string for us
	params.add(quizID); // This must be of form x.y
	// params.add(new Integer(2)); // Right now this is hard-coded for testing but ultimately
	// it will have to be in rounded form (numCorrect/numQuestions) * normalizedScore
	if (((float)Integer.parseInt(numQuestions)) != 0)
	    params.add( Math.round( ((float)Integer.parseInt(numCorrect))/((float)Integer.parseInt(numQuestions))
				    * ((float)Integer.parseInt(score)) ) );
	else
	    params.add(new Integer(0));
	params.add(new Integer(score));
	params.add(seed);
	
	try
	{
	    Map result = (Map)rpcClient.execute("result.addResult",params);
	    boolean ok = ((Boolean)result.get("operationOk")).booleanValue();
	    if(ok) clientConn.logger.info("Client Connection to TRAKLA Operation OK");
	    else clientConn.logger.info((String)result.get("message"));
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
}
