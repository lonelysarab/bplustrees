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
import java.sql.*;

/** A QuizResultProcessor that writes the quiz results to a database.
 */
public class QuizDBProcessor extends QuizResultProcessor implements TransactionCodes
{
    private String dataSource;
    private String DBLogin;
    private String DBPass;

    /** Constructor for the class
     *
     * @param clientConn The associated ClientConnection (should just be "this"
     * @param quizID name of the quiz taken
     * @param studentID login of the student
     * @param numQuestions number of questions in the quiz
     * @param numCorrect number of question answered correctly
     * @param endTime the time the quiz was finished
     * @param dataSource location of the database
     * @param DBlogin login name for the database
     */
    public QuizDBProcessor(ClientConnection clientConn,String quizID,String studentID,String numQuestions,String numCorrect,
			   String endTime,String dataSource,String DBLogin)
    {
	super(clientConn,quizID,studentID,numQuestions,numCorrect,endTime);
    
	this.dataSource = dataSource;
	this.DBLogin = DBLogin;
    }
    
    public void processResults() throws Exception
    {
	try
	{
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    String dbURL = "jdbc:mysql:" + dataSource;
	    Connection conn = DriverManager.getConnection(dbURL,DBLogin,System.getProperty("jhave.server.dbpassword"));
	    
	    Statement stmt = conn.createStatement();
	    String query = "INSERT INTO quiz_taken VALUES('"+studentID+"','"+quizID+"','"+numQuestions+"','"+numCorrect+"','"+endTime+"')";
	    stmt.executeUpdate(query);
	    stmt.close();
	    conn.close();
	    clientConn.logger.info("Sending " + FS_SUCCESSFUL_QUIZ_DB_UPDATE+" to client");
	    clientConn.sendtoClient(""+FS_SUCCESSFUL_QUIZ_DB_UPDATE+" db updated");
	}
	catch(Exception e)
	{
	    clientConn.logger.info(e.toString());
	}
    }
}