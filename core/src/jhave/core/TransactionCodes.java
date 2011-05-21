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
 * TransactionList.java
 *
 * Created on May 21, 2002, 11:12 PM
 */

package jhave.core;

/**
 * Interfaces that contains all the constants for client - server transactions.
 * <p>
 * Key: <br>
 *  <li> (type object) - Mandatory to be sent with the transaction code. <br>
 *  <li> [type object] - Optional to be sent with the transcation code. <br>
 *  <li> | - Seperates transactions. All transactions after first do not contain the transaction code. <br>
 *  <li> (String1) (String2) - The space between sent Strings implies " ".
 *
 * @author      Chris Gaffney
 * @version     1.1
 *
 * Revision History:
 *  Version 1.0:
 *      -Original Version
 *  Version 1.1:
 *      -Added: LOG_MESSAGE
 *      -Added: FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM
 */
public interface TransactionCodes {
    
    /**
     * -115 (Message)
     * Specifies that a message was sent internally and should be logged.
     * <p>
     * This code should be sent to log message that are not normally fired by the client.
     * These events include downloading algorithm lists and input generators, which do
     * not normally fire an NetworkEvent. 
     *
     * @since   1.1
     */
    public static final int LOG_MESSAGE = -115;
    
    /** 
     * 1 (String category) | (Vector algorithmList) 
     * Requests a connection to the server.
     * <p>
     * This is the first code sent to the server to request an available connect slot.
     * Along with the connection code the client sends the category of algorithms used.
     * Followed after the transaction with the code and category type the client a second
     * transaction is sent to the server. This transaction contains a Vector containing 
     * the Algorithm objects created by downloading the list of available algorithms.
     * <p>
     * In response to: None<br>
     * Confirmation for: FS_LOGIN_CONFIRMATION <br>
     * Denial for: FS_LOGIN_DENIAL
     *
     * @see     #FS_LOGIN_CONFIRMATION
     * @see     #FS_LOGIN_DENIAL
     * @since   1.0
     */
    public static final int FC_CONNECT_TO_SERVER = 1;
    
    /** 
     * 2 [String message] 
     * Response to a ping from the server.
     * <p>
     * Pings are sent periodically from the server, approximatly every 4 minutes from last ping.
     * The server uses pings to make sure the client is still connected. If FC_PONG is not 
     * received 5 minutes after the last ping from the server, the server will close the
     * socket connection with the client, ending all transactions.
     * <p>
     * In response to: FS_PING <br>
     * Confirmation for:  None <br>
     * Denial for: None
     *
     * @see     #FC_REQUEST_PING
     * @see     #FS_PING
     * @since   1.0
     */
    public static final int FC_PONG = 2;
    
    /** 
     * -99 [String message] 
     * Requests the server to Ping the Client.
     * <p>
     * Requests that the server sends a ping to the client. It then follows the ping process.
     * <p>
     * In response to: None <br>
     * Confirmation for:  FS_PING <br>
     * Denial for: None
     *
     * @see     #FC_PONG
     * @see     #FS_PING
     * @since   1.0
     */
    public static final int FC_REQUEST_PING = -99;
    
    /**
     * 3 (String algorithm.GetAlgoName) 
     * Requests a static script to be sent to the Client.
     * <p>
     * Requests that a static (non-generated) script be sent from the server. The server
     * will then send the script along with FS_SEND_STATIC_SCRIPT or tell the client that
     * an error has occured (FS_ERROR_GETTING_STATIC_SCRIPT).
     * <p>
     * In response to: None <br>
     * Confirmation for: None <br>
     * Denial for: None
     *
     * @see     #FS_SEND_STATIC_SCRIPT
     * @see     #FS_ERROR_GETTING_STATIC_SCRIPT
     * @since   1.0
     */
    public static final int FC_REQUEST_STATIC_SCRIPT = 3;
    
    /**
     * 4 (Algorithm algorithm.GetAlgoName()) ("old" or "new") (String parameters)
     * Requests the server to generate a script and send it to the client.
     * <p>
     * Requests a generated script from the server. The script can either be one generated before hand
     * by specifying "old" or a newly generated script ("new"). 
     * <p>
     * In response to: None <br>
     * Confirmation for: None <br>
     * Denial for: None
     * 
     * @see     #FS_SEND_GENERATED_SCRIPT
     * @see     #FS_ERROR_GETTING_GENERATED_SCRIPT
     * @see     #FS_ALGORITHM_NEEDS_INPUT
     * @see     #FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM
     * @since 1.0
     */
    public static final int FC_REQUEST_GENERATED_SCRIPT = 4;
    
    /**
     * 5 (String algorithm.GetAlgoName()) (String question ID) (String answer)
     * Submits the students answer to an asked question to the server.
     * <p>
     * First string is that returned by an <code>Algorithm</code> GetAlgoName() method.
     * The second is the questions ID Number. The third token is the answer given by the
     * student.
     * <p>
     * In response to: None <br>
     * Confirmation for: None <br>
     * Denial for: None
     *
     * @see     #FS_EVALUATED_QUESTION
     * @see     #FS_FINAL_RESULTS_TO_QUIZ_AND_LAST_QUESTION
     * @since   1.0
     */
    public static final int FC_SUBMIT_ANSWER_TO_QUESTION = 5;
    
    /**
     * 100 [String message]
     * Confirms to the client that a connection has been established.
     * <p>
     * In response to: FC_CONNECT_TO_SERVER <br>
     * Confirmation for: FC_CONNECT_TO_SERVER <br>
     * Denial for: None 
     *
     * @see     #FC_CONNECT_TO_SERVER
     * @see     #FS_LOGIN_DENIAL
     * @since 1.0
     */
    public static final int FS_LOGIN_CONFIRMATION = 100;
    
    /**
     * 101 [String Message]
     * Informs the client that their connection has been denied.
     * <p>
     * In response to: FC_CONNECT_TO_SERVER <br>
     * Confirmation for:  None <br>
     * Denial for: FC_CONNECT_TO_SERVER
     *
     * @see     #FC_CONNECT_TO_SERVER
     * @see     #FS_LOGIN_DENIAL
     * @since   1.0
     */
    public static final int FS_LOGIN_DENIAL = 101;
    
    /**
     * 200 [String Message]
     * Sends a ping to the client to see if it is still there.
     * <p>
     * In response to: FC_REQUEST_PING or Servers ping rotation <br>
     * Confirmation for:  FC_PONG <br>
     * Denial for: None
     * 
     * @see     #FC_REQUEST_PING
     * @see     #FC_PONG
     * @since   1.0
     */
    public static final int FS_PING = 200;
    
    /**
     * 300 (String scriptName) (String visualizer) | (String script)
     * Server sends a static (non-generated) to the client.
     * <p>
     * In response to: FC_REQUEST_STATIC_SCRIPT <br>
     * Confirmation for: FC_REQUEST_STATIC_SCRIPT <br>
     * Denial for: None
     *
     * @see     #FC_REQUEST_STATIC_SCRIPT
     * @see     #FS_ERROR_GETTING_STATIC_SCRIPT
     * @since   1.0
     */
    public static final int FS_SEND_STATIC_SCRIPT = 300;
    
    /**
     * 301 [String reason] [String scriptName] [String message]
     * An error occured while trying to retrieve the Script.
     * <p>
     * The error most likly means that the algorithm you requested is not available on
     * the server you are connected to. More information is sent with the code.
     * <p>
     * In response to: FC_REQUEST_STATIC_SCRIPT <br>
     * Confirmation for: None <br>
     * Denial for: FC_REQUEST_STATIC_SCRIPT
     *
     * @see     #FC_REQUEST_STATIC_SCRIPT
     * @see     #FS_SEND_STATIC_SCRIPT
     * @since   1.0
     */
    public static final int FS_ERROR_GETTING_STATIC_SCRIPT = 301;
    
    /**
     * 400 (String scriptName) (String visualizer) | (String script)
     * Server sends a generated script to the client.
     * <p>
     * If the generated script needs more input then the server will send back
     * FS_ALGORITHM_NEEDS_INPUT to the client. The client should then start a
     * InputGenerator to collect information from the user and finally re-request
     * the script with parameters taken from the input generator.
     * <p>
     * In response to: FC_REQUEST_GENERATED_SCRIPT <br>
     * Confirmation for:  FC_REQUEST_GENERATED_SCRIPT <br>
     * Denial for: None
     *
     * @see     #FC_REQUEST_GENERATED_SCRIPT
     * @see     #FS_ERROR_GETTING_GENERATED_SCRIPT
     * @see     #FS_ALGORITHM_NEEDS_INPUT
     * @see     #FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM
     * @since   1.0
     */
    public static final int FS_SEND_GENERATED_SCRIPT = 400;
    
    /**
     * 401 [String message]
     * Specifys that the given input is not enough to generated the algorithm.
     * <p>
     * Afters receiving this the client should load an InputGenerator to collect
     * needed input from the student.
     * <p>
     * In response to: FC_REQUEST_GENERATED_SCRIPT <br>
     * Confirmation for: None <br>
     * Denial for: FC_REQUEST_GENERATED_SCRIPT
     *
     * @see     #FC_REQUEST_GENERATED_SCRIPT
     * @see     #FS_SEND_GENERATED_SCRIPT
     * @see     #FS_ERROR_GETTING_GENERATED_SCRIPT
     * @see     #FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM
     * @since   1.0
     */
    public static final int FS_ALGORITHM_NEEDS_INPUT = 401;
    
    /**
     * 402 [String reason]
     * An error occured while trying to generate the script, possibly the script is not available on the server.
     * <p>
     * In response to: FC_REQUEST_GENERATED_SCRIPT <br>
     * Confirmation for: None <br>
     * Denial for: FC_REQUEST_GENERATED_SCRIPT
     *
     * @see     #FC_REQUEST_GENERATED_SCRIPT
     * @see     #FS_SEND_GENERATED_SCRIPT
     * @see     #FS_ALGORITHM_NEEDS_INPUT
     * @see     #FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM
     * @since   1.0
     */
    public static final int FS_ERROR_GETTING_GENERATED_SCRIPT = 402;
    
    /**
     * 404 [String message]
     * The dataset that was last used is not valid for the requested algorithm. This is most likely an
     * error on the part of the client, aswell the message is simply informative as the server treats
     * the dataset as being a new dataset when generating the script.
     * <p>
     * In response to: FC_REQUEST_GENERATED_SCRIPT <br>
     * Confirmation for: None - is Error <br>
     * Denial for: None - is informative <br>
     *
     * @see     #FC_REQUEST_GENERATED_SCRIPT
     * @see     #FS_SEND_GENERATED_SCRIPT
     * @see     #FS_ALGORITHM_NEEDS_INPUT
     * @see     #FS_ERROR_GETTING_GENERATED_SCRIPT
     * @since   1.1
     */
    public static final int FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM = 404;
    
    /**
     * 405 [String message]
     * An error occured while trying to access the request quiz, possibly the quiz is not on this server.
     * <p>
     * In response to: Unknown <br>
     * Confirmation for: None <br>
     * Denial for: Unknown
     *
     * @since   1.0
     */
    public static final int FS_NO_QUIZ_IN_DATABASE = 405;
    
    /**
     * 406 [String message]
     * An invalid username or password was used to log into the server.
     * <p>
     * In response to: FC_CONNECT_TO_SERVER? <br>
     * Confirmation for: None <br>
     * Denial for: FC_CONNECT_TO_SERVER?
     *
     * @since   1.0
     */
    public static final int FS_INVALID_NAME_OR_PASSWORD = 406;
    
    /**
     * 407 [String error]
     * You quiz you have requested is no longer available.
     * <p>
     * In response to: Unknown <br>
     * Confirmation for: None <br>
     * Denial for: Unknown
     *
     * @since   1.0
     */
    public static final int FS_QUIZ_TIME_EXPIRED = 407;
    
    /**
     * 408 [String error]
     * There was an error loading the requested quiz, possibly server trouble.
     * <p>
     * In response to: Unknown <br>
     * Confirmation for: None <br>
     * Denial for: Unknown
     *
     * @since       1.0
     */
    public static final int FS_ERROR_GETTING_QUIZ = 408;
    
    /**
     * 501 [String unknown]
     * Not sure at the functionality of this code.
     * <p>
     * In response to: ?? <br>
     * Confirmation for: ?? <br>
     * Denial for: ??
     * 
     * @since       1.0
     */
    public static final int FS_EVALUATED_QUESTION = 501;
    
    /**
     * 502 ("theKey") (String message)or((String number of questions) (String questions correct) ("1"))
     * Sends the final quiz results to the client.
     * <p>
     * In response to: ?? <br>
     * Confirmation for:  ?? <br>
     * Denial for: ??
     *
     * @since       1.0
     */
    public static final int FS_FINAL_RESULTS_TO_QUIZ_AND_LAST_QUESTION = 502;

    /**
     * 6 [String message with three tokens(quizID, studentID, password)]
     * Is sent to the server when a students requests a login.
     * <p>
     * In response to: none <br>
     * Confirmation for: none <br>
     * Denial for: none
     *
     * @since       2.0
     */
    public static final int FC_REQUEST_QUIZ_LOGIN=6;
    
    /**
     * 7 [XML String containing results of the quiz]
     * Is sent to the server when a completes a quiz.
     * <p>
     * In response to: none <br>
     * Confirmation for: none <br>
     * Denial for: none
     *
     * @since       2.0
     */
    public static final int FC_QUIZ_COMPLETED=7;
    
    /**
     * 600 [String message]
     * Is sent to the client upon a successful login.
     * <p>
     * In response to: FC_REQUEST_QUIZ_LOGIN <br>
     * Confirmation for: FC_REQUEST_QUIZ_LOGIN <br>
     * Denial for: none
     *
     * @since       2.0
     */
    public static final int FS_SUCCESSFUL_QUIZ_LOGIN=600;

    /**
     * 601 [String message]
     * Is sent to the client if the studentID is not in the database.
     * <p>
     * In response to: FC_REQUEST_QUIZ_LOGIN <br>
     * Confirmation for: none <br>
     * Denial for: FC_REQUEST_QUIZ_LOGIN
     *
     * @since 2.0
     */
    public static final int FS_UNSUCCESSFUL_QUIZ_LOGIN_NO_STUDENT_ID=601;
    
    /**
     * 602 [String message]
     * Is sent to the client if the given password is not correct.
     * <p>
     * In response to: FC_REQUEST_QUIZ_LOGIN <br>
     * Confirmation for: none <br>
     * Denial for: FC_REQUEST_QUIZ_LOGIN
     *
     * @since 2.0
     */
    public static final int FS_UNSUCCESSFUL_QUIZ_LOGIN_BAD_PASSWD=602;
    
    /**
     * 603 [String message]
     * Is sent to the client if the time range for the given quiz has passed.
     * <p>
     * In response to: FC_REQUEST_QUIZ_LOGIN <br>
     * Confirmation for: none <br>
     * Denial for: FC_REQUEST_QUIZ_LOGIN
     *
     * @since 2.0
     */
    public static final int FS_UNSUCCESSFUL_QUIZ_LOGIN_OUTSIDE_TIME_RANGE=603;
    
    /**
     * 604 [String message]
     * Is sent to the client when the quiz information is added to the database
     * <p>
     * In response to: FC_QUIZ_COMPLETED <br>
     * Confirmation for: FC_QUIZ_COMPLETED <br>
     * Denial for: none
     *
     * @since 2.0
     */
    public static final int FS_SUCCESSFUL_QUIZ_DB_UPDATE=604;
    
    /**
     * 605 [String message]
     * Is sent to the client if the XML string is not formatted correctly
     * <p>
     * In response to: FC_QUIZ_COMPLETED <br>
     * Confirmation for: none <br>
     * Denial for: FC_QUIZ_COMPLETED
     *
     * @since 2.0
     */
    public static final int FS_UNSUCCESSFUL_QUIZ_DB_UPDATE=605;
    
}
