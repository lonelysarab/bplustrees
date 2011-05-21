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
 * ConnectionPanel.java
 *
 * Created on July 15, 2002, 9:12 AM
 */

package jhave.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jhave.core.JHAVETranslator;
/**
 * JPanel that displays a log of the connections between the client and server.
 * Also available is a one line status bar which displays the last event sent
 * by either the client or the server.
 * @author  Chris Gaffney
 */
public class ConnectionPanel extends JPanel implements jhave.event.NetworkListener, jhave.core.TransactionCodes {
    
    ///////////////////////////////////////////////////////////////////////////
    // Constants
    
    /** The component that displays what is currently happening. */
    private static final JLabel statusBar = new JLabel();
    /** The component that displays all previous interactions. */
    private static final JTextArea connectionHistory = new JTextArea();
    /** JScrollPane that contains the connectionHistory */
    private static final JScrollPane connectionHistoryScrollPane = new JScrollPane(connectionHistory);
    
    static {
        connectionHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        connectionHistoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    // End Constants
    ///////////////////////////////////////////////////////////////////////////
    // Globals
    
    /** Client properties which are used to bring up the config dialog. */
    private ClientProperties properties;
    /** Number of transactions that have been logged */
    private static int transactions = 0;
    
    // Ping time variables
    /** If a request for a ping has been sent. */
    private boolean isWaitingForPing = false;
    /** Time at which the ping was sent. */
    private long sentPingRequest = 0;
    
    // End Globals
    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /** 
     * Creates a new instance of ConnectionPanel.
     * @param controller the network controller that this ConnectionPanel gets it's events from.
     */
    public ConnectionPanel(ClientNetworkController controller) {
        super(new BorderLayout());
        properties = controller.getClientProperties();
        controller.addNetworkListener(this);
        setStatus(JHAVETranslator.translateMessage("startedLogging"));
//        setStatus("Started Logging Connection");
        connectionHistory.setEditable(false);
        add(BorderLayout.CENTER, connectionHistoryScrollPane);
    }
    
    // End Constructors
    ///////////////////////////////////////////////////////////////////////////
    // NetworkListener Methods
    
    /**
     * Method invoked when any inbound transactions are made.
     * @param networkEvent Event sent from the network controller.
     */
    public void inboundTransaction(jhave.event.NetworkEvent networkEvent) {
        if(networkEvent.hasTransactionCode()) {
            switch(networkEvent.getTransactionCode()) {
                case FS_LOGIN_CONFIRMATION:
                  setStatus(JHAVETranslator.translateMessage("connEstablished"));
//                    setStatus("Connection Established");
                    break;
                case FS_LOGIN_DENIAL:
                  setStatus(JHAVETranslator.translateMessage("connRefused"));
//                    setStatus("Error: Connection Refused");
                    break;
                case FS_INVALID_NAME_OR_PASSWORD:
                  setStatus(JHAVETranslator.translateMessage("invalidUserPass"));
//                    setStatus("Error: Invalid Username or Password");
                    break;
                case FS_PING:
                    if(isWaitingForPing) {
                      setStatus(JHAVETranslator.translateMessage("pingWait",
                          new Long(System.currentTimeMillis() - sentPingRequest)));
//                        setStatus("Ping Received (Latency " + (System.currentTimeMillis() - sentPingRequest) + "ms)");
                        isWaitingForPing = false;
                    } else {
                        setStatus(JHAVETranslator.translateMessage("pingPong"));
//                        setStatus("Ping Received - Sending Pong");
                    }
                    break;
                case FS_SEND_STATIC_SCRIPT:
                  setStatus(JHAVETranslator.translateMessage("recStatic"));
//                    setStatus("Received Static Script");
                    break;
                case FS_ERROR_GETTING_STATIC_SCRIPT:
                  setStatus(JHAVETranslator.translateMessage("errorRecStatic"));
//                    setStatus("Error: Problem Retrieving Static Script");
                    break;
                case FS_SEND_GENERATED_SCRIPT:
                  setStatus(JHAVETranslator.translateMessage("recGenerated"));
		    //                    setStatus("Received Generated Script");
		    // Show the message for two seconds before disappearing.
		    // Note that this does not slow the rendering of the 
		    // showfile; this is inelegant but seems to be the best way
		    // to remove the message after the visualization has been 
		    // displayed without writing a lot of additional code.
		    try{
			Thread.sleep(2000);
		    }catch(java.lang.InterruptedException interrupt){
			// Simply continue on with method execution.
		    }
		    // Make the message disappear.
		    setStatus("");
                    break;
                case FS_ERROR_GETTING_GENERATED_SCRIPT:
                  setStatus(JHAVETranslator.translateMessage("errorRecGenerated"));
//                    setStatus("Error: Problem Retrieving Generated Script");
                    break;
                case FS_ALGORITHM_NEEDS_INPUT:
                  setStatus(JHAVETranslator.translateMessage("errorNeedInput"));
//                    setStatus("Error: Algorithm Needs Input from Input Generator");
                    break;
                case FS_ERROR_GETTING_QUIZ:
                  setStatus(JHAVETranslator.translateMessage("errorRecQuiz"));
//                    setStatus("Error: Could not get Quiz");
                    break;
                case FS_NO_QUIZ_IN_DATABASE:
                  setStatus(JHAVETranslator.translateMessage("reqQuizNotFound"));
//                    setStatus("Requested Quiz is not in the Database");
                    break;
                case FS_EVALUATED_QUESTION:
                  setStatus(JHAVETranslator.translateMessage("recQuestionEval"));
//                    setStatus("Recevied Question Evaluation");
                    break;
                case FS_QUIZ_TIME_EXPIRED:
                  setStatus(JHAVETranslator.translateMessage("errorQuizNotAvail"));
//                    setStatus("Error: Quiz is no longer Available");
                    break;
                case LOG_MESSAGE:
                  setStatus(JHAVETranslator.translateMessage("null", 
                      networkEvent.getMessage()));
                    setStatus(networkEvent.getMessage());
                    break;
                case FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM:
                  setStatus(JHAVETranslator.translateMessage("errorIncorrectDataSet"));
//                    setStatus("Error: Incorrect Dataset Requested - Algorithm Generated with Normal Dataset");
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Method invoked when any outbound transactions are made.
     * @param networkEvent Event sent from the network controller.
     */
    public void outboundTransaction(jhave.event.NetworkEvent networkEvent) {
        if(networkEvent.hasTransactionCode()) {
            switch(networkEvent.getTransactionCode()) {
                case FC_CONNECT_TO_SERVER:
                  setStatus(JHAVETranslator.translateMessage("reqConnServer"));
//                    setStatus("Requesting Connection to Server");
                    break;
                case FC_PONG:
                    break;
                case FC_REQUEST_GENERATED_SCRIPT:
                  setStatus(JHAVETranslator.translateMessage("reqGenerated"));
//                    setStatus("Requesting Generated Script");
                    break;
                case FC_REQUEST_PING:
                    sentPingRequest = System.currentTimeMillis();
                    isWaitingForPing = true;
                    setStatus(JHAVETranslator.translateMessage("requestPing"));
//                    setStatus("Requesting Ping");
                    break;
                case FC_REQUEST_STATIC_SCRIPT:
                  setStatus(JHAVETranslator.translateMessage("reqStatic"));
//                    setStatus("Requesting Static Script");
                    break;
                case FC_SUBMIT_ANSWER_TO_QUESTION:
                  setStatus(JHAVETranslator.translateMessage("submitAnswer"));
//                    setStatus("Submitting Students Answer");
                    break;
                case LOG_MESSAGE:
                  setStatus(JHAVETranslator.translateMessage("null",
                      networkEvent.getMessage()));
//                    setStatus(networkEvent.getMessage());
                    break;
                default:
                    break;
            }
        }
    }
    
    // End NetworkListener Methods
    ///////////////////////////////////////////////////////////////////////////
    // Get and Set Methods
    
    /**
     * Returns the status bar as a JPanel.
     * @return JPanel contains status bar.
     */
    public JPanel getStatusPanel() {
        JPanel returnedPanel = new JPanel(new BorderLayout());
        
        returnedPanel.add(BorderLayout.CENTER, statusBar);
        
        return returnedPanel;
    }
    
    /**
     * Convenience method to both change the status bar and append to the text area.
     * @param status new message to add and set.
     */
    public void setStatus(String status) {
        EventQueue.invokeLater(new EventQueueRunner(status));
    }
    
    /**
     * Steps transactions by 1.
     */
    private synchronized void stepTransactions() {
        transactions++;
    }
    
    // End Get and Set Methods
    ///////////////////////////////////////////////////////////////////////////
    // Classes
    
    /**
     * Designed to be injected into the EventQueue to change or append
     * the connection status to the components.
     */
    private class EventQueueRunner implements Runnable {
        /** Status to be displayed. */
        private String status;
        
        /**
         * Instantiates a new EventQueueRunner.
         * @param status connection status.
         */
        public EventQueueRunner(String status) {
            this.status = status;
        }
        
        /**
         * Called when the activated from the eventqueue.
         */
        public void run() {
            stepTransactions();
            statusBar.setText(" " + status);
            connectionHistory.append(" " + transactions + ". " + status + "\n");
            connectionHistoryScrollPane.getViewport().setViewPosition(new Point(0, connectionHistory.getHeight()));
        }
    }
    
    // End Classes
    ///////////////////////////////////////////////////////////////////////////
}