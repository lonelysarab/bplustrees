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
 * ClientNetworkController.java
 *
 * Created on May 31, 2002, 6:42 PM
 */

package jhave.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import jhave.Algorithm;
import jhave.client.misc.AlgorithmComboBoxModel;
import jhave.core.JHAVETranslator;
import jhave.core.TransactionCodes;
import jhave.event.NetworkEvent;
import jhave.event.NetworkListener;
/**
 * This thread handles all client side transactions between Client and Server.
 * @author Chris Gaffney
 */
class ClientNetworkController extends Thread implements TransactionCodes {
    
    /////////////////////////////////////////////////////////////////////////////////
    // Thread specific constants and globals
    
    // End thread specific constants and globals
    /////////////////////////////////////////////////////////////////////////////////
    // Globals
    
    /** The Socket with which we connect to the server. */
    private Socket clientSideSocket;
    /** The stream that handles output to the server. */
    private ObjectOutputStream outToServer;
    /** The stream that handle input from the server. */
    private ObjectInputStream inFromServer;
    /** The ClientProperties object that contains all the connection values used by the client. */
    private ClientProperties clientProperties;
    /** ComboBoxModel that contains the list of Algorithms */
    private AlgorithmComboBoxModel algorithmList = null;
    /** If the client is connected to the server. */
    private boolean isConnected = false;
    /** If the user has logged into the server with user name and password. */
    private boolean isLoggedIn = false;
    /** The username to log into the server with. */
    private String username = null;
    /** The password to log into the server with. */
    private char[] password = null;
    /** The last Algorithm that was viewed. */
    private Algorithm lastAlgorithm = null;
    /** If the user is currently taking a quiz for a grade. */
    private boolean quizForGrade = false;
    /** If the server has responded to the login attempt. */
    private boolean loginResponse = false;
    /** If the user is in show answer mode. */
    private boolean showAnswers = false;
    
    /** The NetworkListeners that have been registered with this object */
    private static final EventListenerList networkListeners = new EventListenerList();
    
    /** */
    private static ClientNetworkController singleton = null;
    
    // End Globals
    /////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Creates a new instance of ClientTransactionController.
     * @param clientProperties The properties that define the connection to the server.
     * @throws IOException an error occured loading the algorithm list.
     */
    public ClientNetworkController(ClientProperties clientProperties) throws IOException {
        super();
        this.clientProperties = clientProperties;
	username = clientProperties.getUsername();

	// DEBUG OUTPUT
	if (username != null) System.out.println("Username is " + username);
	else {
	    System.out.println("no_username");
	    username = new String("no_username");
	}
	if (System.getProperty("jhave.client.TRAKLAusername") != null) System.out.println(System.getProperty("jhave.client.TRAKLAusername"));
	    else System.out.println("no TRAKLA username");
	if (System.getProperty("jhave.client.TRAKLAseed") != null) System.out.println(System.getProperty("jhave.client.TRAKLAseed"));
	    else System.out.println("no TRAKLA seed");
	if (System.getProperty("jhave.client.TRAKLAcourse") != null) System.out.println(System.getProperty("jhave.client.TRAKLAcourse"));
	    else System.out.println("no TRAKLA course");
	if (System.getProperty("jhave.client.TRAKLAquizid") != null) System.out.println(System.getProperty("jhave.client.TRAKLAquizid"));
	    else System.out.println("no TRAKLA quizid");
	if (System.getProperty("jhave.client.TRAKLAnormalizedquizvalue") != null) System.out.println(System.getProperty("jhave.client.TRAKLAnormalizedquizvalue"));
	    else System.out.println("no TRAKLA normalized quiz value");

        singleton = this;
    }
    
    // End constructors
    /////////////////////////////////////////////////////////////////////////////////
    // Thread specific methods (Methods specific to this being a thread)
    
    /**
     * Method invoked when the thread is started.
     */
    public void run() {
        while(true) {
            String currentMessage = "";
            int networkCode = NetworkEvent.NO_TRANSACTION_CODE;
            
            try {
                currentMessage = (String)(inFromServer.readObject());
            } catch (EOFException e) {
                // The server has shutdown the socket on its end.
//                System.err.println("Server is probably being restarted.");
              System.err.println(JHAVETranslator.translateMessage("serverMaybeRestarts"));
                break;
            } catch (ClassNotFoundException e) {
              System.err.println(JHAVETranslator.translateMessage("unknownInfoFromServer"));
//                System.err.println("Unknown information sent from server.");
                e.printStackTrace();
            } catch (SocketException e) {
                // Should just ignore this as its an artifact of the socket being closed
                // and no command sent from the server to disconnect.
                // Also it is caused when the Server cuts the socket and the client trys
                // to talk with the server. Ie - The server pings out and doesn't send
                // a disconnect code.
                if(e.getMessage().equalsIgnoreCase("socket closed")) {
                    fireOutboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
                        JHAVETranslator.translateMessage("disconnected")));
//                        "Disconnected from Server"));
                    break;
                } else {
                    e.printStackTrace();
                }
                break;
            } catch (IOException e) {
                // Not sure what happend. More general error occured.
                e.printStackTrace();
            }
            
            NetworkEvent inboundEvent = new NetworkEvent(this, currentMessage);
          
            switch(inboundEvent.getTransactionCode()) {
                //FAKE CLIENT JN
		// Successes
	        case FS_SUCCESSFUL_QUIZ_LOGIN:
            System.out.println(JHAVETranslator.translateMessage("receivedFromServer",
                inboundEvent.getMessage()));
//		    System.out.println(inboundEvent.getMessage()+" received from server");
		    loginResponse = true;
		    break;
	        case FS_SUCCESSFUL_QUIZ_DB_UPDATE:
            System.out.println(JHAVETranslator.translateMessage("receivedFromServer",
                inboundEvent.getMessage()));
//		    System.out.println(inboundEvent.getMessage()+" received from server");
		    break;
	        case FS_UNSUCCESSFUL_QUIZ_DB_UPDATE:
            System.out.println(JHAVETranslator.translateMessage("receivedFromServer",
                inboundEvent.getMessage()));
//		    System.out.println(inboundEvent.getMessage()+" received from server");
		    break;
		//FAKE CLIENT JN
	        case FS_LOGIN_CONFIRMATION:
            System.out.println(JHAVETranslator.translateMessage("receivedFromServer",
                inboundEvent.getMessage()));
//		    System.out.println(inboundEvent.getMessage()+" received from server");
                    setConnected(true);
                case FS_EVALUATED_QUESTION:
                case FS_FINAL_RESULTS_TO_QUIZ_AND_LAST_QUESTION:
                    fireInboundTransaction(inboundEvent);
                    break;
                    // Other
                case FS_PING:
                    fireInboundTransaction(inboundEvent);
                    try {
                        pong();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
                    // Errors
	        case FS_UNSUCCESSFUL_QUIZ_LOGIN_OUTSIDE_TIME_RANGE:
	        case FS_UNSUCCESSFUL_QUIZ_LOGIN_BAD_PASSWD:
	        case FS_UNSUCCESSFUL_QUIZ_LOGIN_NO_STUDENT_ID:
                case FS_LOGIN_DENIAL:
                case FS_INVALID_NAME_OR_PASSWORD:
                  System.err.println(JHAVETranslator.translateMessage("receivedFromServer",
                      inboundEvent.getMessage()));
//		    System.err.println(inboundEvent.getMessage()+" received from server");
		    username = null;
                    password = null;
                    setLoggedIn(false);
		    loginResponse = true;
                case FS_ERROR_GETTING_STATIC_SCRIPT:
                case FS_ALGORITHM_NEEDS_INPUT:
                case FS_ERROR_GETTING_GENERATED_SCRIPT:
                case FS_NO_QUIZ_IN_DATABASE:
                case FS_QUIZ_TIME_EXPIRED:
                case FS_ERROR_GETTING_QUIZ:
                case FS_ERROR_EXISTING_DATASET_NOT_VALID_FOR_REQUESTED_ALGORITHM:
                    fireInboundTransaction(inboundEvent);
                    break;
                    // Receive additional transaction(s)
                case FS_SEND_STATIC_SCRIPT:
                case FS_SEND_GENERATED_SCRIPT:
                    try {
                        currentMessage += " " + (String)inFromServer.readObject();
                    } catch (ClassNotFoundException e) {
                      System.err.println(JHAVETranslator.translateMessage(
                          "unknownInfoFromServer"));
//                        System.err.println("Unknown information sent from server.");
                        e.printStackTrace();
                    } catch (IOException e) {
                      currentMessage += JHAVETranslator.translateMessage(
                          "errorRetrievingScriptURL");
//                        currentMessage += " Error Retrieving Script URL";
                    }
                    fireInboundTransaction(new NetworkEvent(this, currentMessage));
                    break;
                case NetworkEvent.NO_TRANSACTION_CODE:
                  System.err.println("noTransCode");
//                    System.err.println("No transaction code received with message");
                    break;
                default:
                  System.err.println(JHAVETranslator.translateMessage("unknownCode", 
                      new Object[] { new Integer(networkCode), 
                      inboundEvent.getMessage()
                  }));
//                    System.err.println("Unknown code sent from Server.");
//                    System.err.println("Code: " + networkCode);
//                    System.err.println("Message: " + inboundEvent.getMessage());
                    break;
            }
        }
    }
    
    // End thread specific methods
    /////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Closes the connection to the server.
     * @throws IOException there was an error closing the connection.
     */
    public void close() throws IOException {
        setConnected(false);
        setLoggedIn(false);
        
        // Here we close the socket and streams
        if(outToServer != null) {
            outToServer.close();
        }
        if(inFromServer != null) {
            inFromServer.close();
        }
        if(clientSideSocket != null) {
            clientSideSocket.close();
        }
    }
    
    // End receiving methods
    /////////////////////////////////////////////////////////////////////////////////
    // Get, set, and is methods
    
    /**
     *
     * @throws IOException there was an error initializing the controller.
     */
    public static ClientNetworkController getInstance() {
        return singleton;
    }

    /**
     * Downloads, parses, caches, and returns a list of the available Alogrithm Choices.
     * @param listURL The URL where the list is located
     * @return AlgorithmComboBoxModel the list of algorithm Choices
     * @throws IOException problem getting/reading from location
     */
    public AlgorithmComboBoxModel getAlgorithmList(URL listURL) throws IOException {
        fireOutboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
            JHAVETranslator.translateMessage("downloadListOfAlgo")));
//            "Downloading List of Algorithms"));
        try {
            AlgorithmComboBoxModel menu = new AlgorithmComboBoxModel();
            
            BufferedReader inFromMenu = new BufferedReader(new InputStreamReader(listURL.openStream()));
            
            String menuItem;
            Vector algoFriendVector = new Vector();
            while((menuItem = inFromMenu.readLine()) != null) {
                if(menuItem.startsWith("***")) {
                    algoFriendVector.removeAllElements();
                } else {
                    Algorithm algo = new Algorithm();
                    algo.SetDescriptiveText(menuItem);
                    algoFriendVector.addElement(algo);
                    
                    for(int index = 0; index < algoFriendVector.size(); index++){
                        ((Algorithm)algoFriendVector.elementAt(index)).AddAlgoFriend(algo);
                        algo.AddAlgoFriend((Algorithm)algoFriendVector.elementAt(index));
                    }
                    
                    StringTokenizer algoProperties = new StringTokenizer(inFromMenu.readLine());
                    algo.SetAlgoName(algoProperties.nextToken());
                    algo.SetDynamicStatus(algoProperties.nextToken());
                    algo.SetVisualizerType(algoProperties.nextToken());
                    
                    menu.addElement(algo);
                }
            }
            inFromMenu.close();
            setAlgorithmList(menu);
            fireOutboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
                JHAVETranslator.translateMessage("finishedDLLoA")));
//                "Finished Downloading List of Algorithms"));
            return menu;
        } catch (IOException e) {
            NetworkEvent event = new NetworkEvent(this, LOG_MESSAGE,
                JHAVETranslator.translateMessage("errorDLLoA"));
//                "Error: Could not Download Algorithm List");
            fireOutboundTransaction(event);
            throw e;
        }
    }
    

    /**
     * In the event of autoloading and bypassing a cat file, this
     * routine determines the algo to start from its string parameter
     * @param algoInfo a colon-delimited string with the four items
     * defining an algorithm in one cat file entry
     * @return AlgorithmComboBoxModel the list of algorithm Choices
     * @throws IOException problem getting/reading from location
     */
    public AlgorithmComboBoxModel getAlgorithmList(String algoInfo) /* throws IOException */ {
        fireOutboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
            JHAVETranslator.translateMessage("downloadListOfAlgo")));
//            "Downloading List of Algorithms"));
	AlgorithmComboBoxModel menu = new AlgorithmComboBoxModel();
	String info [] = algoInfo.split(":",0);

	Algorithm algo = new Algorithm();
	algo.SetDescriptiveText(info[0]);
	algo.SetAlgoName(info[1]);
        algo.SetDynamicStatus(info[2]);
	algo.SetVisualizerType(info[3]);
	menu.addElement(algo);
	setAlgorithmList(menu);
	fireOutboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
						 JHAVETranslator.translateMessage("finishedDLLoA")));
	//                "Finished Downloading List of Algorithms"));
	return menu;


//         try {
//             AlgorithmComboBoxModel menu = new AlgorithmComboBoxModel();
//             
//             BufferedReader inFromMenu = new BufferedReader(new InputStreamReader(listURL.openStream()));
//             
//             String menuItem;
//             Vector algoFriendVector = new Vector();
//             while((menuItem = inFromMenu.readLine()) != null) {
//                 if(menuItem.startsWith("***")) {
//                     algoFriendVector.removeAllElements();
//                 } else {
//                     Algorithm algo = new Algorithm();
//                     algo.SetDescriptiveText(menuItem);
//                     algoFriendVector.addElement(algo);
//                     
//                     for(int index = 0; index < algoFriendVector.size(); index++){
//                         ((Algorithm)algoFriendVector.elementAt(index)).AddAlgoFriend(algo);
//                         algo.AddAlgoFriend((Algorithm)algoFriendVector.elementAt(index));
//                     }
//                     
//                     StringTokenizer algoProperties = new StringTokenizer(inFromMenu.readLine());
//                     algo.SetAlgoName(algoProperties.nextToken());
//                     algo.SetDynamicStatus(algoProperties.nextToken());
//                     algo.SetVisualizerType(algoProperties.nextToken());
//                     
//                     menu.addElement(algo);
//                 }
//             }
//             inFromMenu.close();
//             setAlgorithmList(menu);
//             fireOutboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
//                 JHAVETranslator.translateMessage("finishedDLLoA")));
// //                "Finished Downloading List of Algorithms"));
//             return menu;
//         } catch (IOException e) {
//             NetworkEvent event = new NetworkEvent(this, LOG_MESSAGE,
//                 JHAVETranslator.translateMessage("errorDLLoA"));
// //                "Error: Could not Download Algorithm List");
//             fireOutboundTransaction(event);
//             throw e;
//         }
    }
    

    /**
     * Returns the cached category list.
     * @return ComboBoxModel containing downloaded algorithm list.
     */
    public AlgorithmComboBoxModel getAlgorithmList() {
        return algorithmList;
    }
    
    /**
     * Sets the cached algorithm list.
     * @param algorithmList algorithm list to set to cached version.
     */
    private void setAlgorithmList(AlgorithmComboBoxModel algorithmList) {
        this.algorithmList = algorithmList;
    }
    
    /**
     * Returns the algorithm with the given name.
     * @param algorithmName the name of the algorithm we are trying to find.
     * @return Algorithm the algorithm with the appropriate name.
     */
    public Algorithm getAlgorithmByName(String algorithmName) {
        Algorithm returnedAlgorithm = null;
        
        for(int index = 0; index < algorithmList.getSize(); index++) {
            if(((Algorithm)algorithmList.getElementAt(index)).GetAlgoName().equalsIgnoreCase(algorithmName)) {
                returnedAlgorithm = (Algorithm)algorithmList.getElementAt(index);
                break;
            }
        }
        
        return returnedAlgorithm;
    }
    
    /**
     * Returns the last algorithm viewed by the user.
     * @return Algorithm the last algorithm viewed by the user.
     */
    public Algorithm getLastAlgorithm() {
        return lastAlgorithm;
    }
    
    /**
     * Given an algorithm it returns the URL of the algorithms InputGenerator script.
     * If the algorithm does not use an input generator then null is returned.
     * @param algorithm the algorithm we are getting an input generator for.
     * @return URL location of algorithms input generator script.
     */
    public URL getInputGenerator(Algorithm algorithm) {
        URL returnedURL = null;
        if(algorithm.GetAlwaysNeedsInputGenerator() && hasInputGenerator(algorithm)) {
            try {
                returnedURL = new URL(clientProperties.getWebroot() + "ingen/" + algorithm.GetAlgoName() + ".igs");
            } catch (MalformedURLException e) {
                // Shouldnt happen
              System.err.println(JHAVETranslator.translateMessage("malformedURLInGen"));
//                System.err.println("Error - MalforedURLException getInputGenerator()");
            }
        }
        return returnedURL;
    }
    
    /**
     * Given an algorithm it will check to see if an input generator file exists for the algorithm.
     * @param algorithm the algorithm we are checking to see if it has an input generator.
     * @return boolean if the algorithm has an input generator file available.
     */
    public boolean hasInputGenerator(Algorithm algorithm) {
        boolean returnedState = true;

	// These lines were moved to prevent the "Retrieving algorithm
	// information." message from appearing after the "Received Generated
	// Script" message - A.J., 6-22-06.
	// 	NetworkEvent event = new NetworkEvent(this, NetworkEvent.LOG_MESSAGE, "Retrieving algorithm information.");
	// 	fireOutboundTransaction(event);

        try {
            InputStream tempStream = new URL(clientProperties.getWebroot() + "ingen/" + algorithm.GetAlgoName() + ".igs").openStream();
            tempStream.close();
        } catch (IOException e) {
            returnedState = false;
        }
        
        return returnedState;
    }
    
    /**
     * Returns the client properties object used for the connection.
     * @return ClientProperties client settings used to connect to the server.
     */
    public ClientProperties getClientProperties() {
        return clientProperties;
    }
    
    /**
     * Sets the connection state.
     * @param aFlag condition of connection state.
     */
    protected void setConnected(boolean aFlag) {
        isConnected = aFlag;
        clientProperties.setIsConnected(aFlag);
    }
    
    /**
     * Returns if the client is connected to the server or not.
     * @return boolean if client is connected.
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Sets the state of the client being logged in.
     * @param aFlag condition of logged in state.
     */
    protected void setLoggedIn(boolean aFlag) {
        isLoggedIn = aFlag;
    }
    
    /**
     * Returns if the client has logged in with a valid user name and password.
     * @return boolean if the client has logged in.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * Sets the username and password.
     * @return int if the user ok'd or canceled the dialog.
     */
    public int setNameAndPassword() {
        LoginDialog login = new LoginDialog();
        int returnedState = login.showDialog(null);
        
        // Check to make sure the user clicked login then set the user name and password
        if(returnedState == LoginDialog.LOGIN_OPTION) {
            // The user hit login.
            this.username = login.getUsername();
            this.password = login.getPassword();
        } else {
            // The user hit cancel. Do nothing.
        }
        
        return returnedState;
    }
    
    /**
     * Returns if the user is in show answer mode.
     * @return if the user is in show answer mode.
     */
    public boolean getShowAnswers()
    {
	return showAnswers;
    }

    /**
     * Set if the user is running in show answer mode.
     * @param showAnswers if the user is running in show answer mode
     */
    public void setShowAnswers(boolean showAnswers)
    {
	this.showAnswers = showAnswers;
    }

    /**
     * Returns if the user is taking a quiz for a grade.
     * @return boolean if the user is taking a quiz for a grade.
     */
    public boolean getQuizForGrade() {
        return quizForGrade;
    }
    
    /**
     * Sets if the user is taking a quiz for a grade.
     * @param quizForGrade if the user is taking a quiz for a grade.
     */
    public void setQuizForGrade(boolean quizForGrade) {
        this.quizForGrade = quizForGrade;
    }
    
    /**
     * Convenience method for returning the currently selected Algorithm.
     * @return Algorithm the currently selected Algorithm.
     */
    public Algorithm getSelectedAlgorithm() {
        return (Algorithm)getAlgorithmList().getSelectedItem();
    }

    /**
     * Method for returning the username to the quiz information object.
     * @return String the username.
     */
    public String getUsername() {
        return username;
    }

    
    // End get, set, and is methods
    /////////////////////////////////////////////////////////////////////////////////
    // NetworkEvent methods
    
    /**
     * Registers a NetworkListener so it can receive events.
     * @param newListener NetworkEvent listener to register
     */
    public void addNetworkListener(NetworkListener newListener) {
        networkListeners.add(NetworkListener.class, newListener);
    }
    
    /**
     * Unregisters a NetworkListener so it no longer receives events.
     * @param listenerToRemove NetworkEvent to be unregistered
     */
    public void removeNetworkListener(NetworkListener listenerToRemove) {
        networkListeners.remove(NetworkListener.class, listenerToRemove);
    }
    
    /**
     * Fires the network event on all registered NetworkListeners. Fires the
     * events from a seperate thread.
     * @param event The event to be fired
     */
    protected synchronized void fireOutboundTransaction(final NetworkEvent event) {
        if(event != null) {
            // Guaranteed to return a non-null array
            final Object[] listeners = networkListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                final int j = i;
                if (listeners[i] == NetworkListener.class) {
                    class NetworkEventFiringThread extends Thread {
                        public void run() {
                            // Process the listeners last to first, notifying
                            // those that are interested in this event
                            ((NetworkListener)listeners[j+1]).outboundTransaction(event);
                        }
                    }
                    new NetworkEventFiringThread().start();
                }
            }
        }
    }
    
    /**
     * Fires the network event on all registered NetworkListeners. Fires the
     * events from a seperate thread.
     * @param event The event to be fired
     */
    protected synchronized void fireInboundTransaction(final NetworkEvent event) {
        if(event != null) {
            // Guaranteed to return a non-null array
            final Object[] listeners = networkListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                final int j = i;
                if (listeners[i] == NetworkListener.class) {
                    class NetworkEventFiringThread extends Thread {
                        public void run() {
                            // Process the listeners last to first, notifying
                            // those that are interested in this event
                            ((NetworkListener)listeners[j+1]).inboundTransaction(event);
                        }
                    }
                    new NetworkEventFiringThread().start();
                }
            }
        }
    }
    
    // End Network event methods
    /////////////////////////////////////////////////////////////////////////////////
    // Outbound connection methods
    
    /**
     * Convenience method for sending a transaction to the server and firing a network event.
     * Only transactions that should be fired to listeners should use this method. Sending
     * passwords and other sensitive information should be done manually from the code, so
     * to not fire a NetworkEvent with information that could intercepted. As well transactions
     * without transaction codes, such as objects, should be sent manually.
     * @param transactionCode code of the transaction.
     * @param message message sent along with the transaction.
     * @throws IOException error occured sending to the server.
     */
    protected void sendToServer(int transactionCode, String message) throws IOException {
        if(isConnected()) {
            NetworkEvent event = new NetworkEvent(this, transactionCode, message);
 
            synchronized (outToServer) {
                outToServer.writeObject(event.getTransactionMessage());
                outToServer.flush();
            }
            
            fireOutboundTransaction(event);
        }
    }
    
    /**
     * Connects to server with cached algorithm list.
     * @throws IOException problem sending information to the server.
     */
    public void connectToServer() throws IOException {
        connectToServer(getAlgorithmList().getAlgorithmVector());
    }
    
    /**
     * Requests a connection to the server.
     * @param algorithms algorithms that are in the selected category.
     * @throws IOException problem sending information to the server.
     */
    public void connectToServer(Vector algorithms) throws IOException {
        fireInboundTransaction(new NetworkEvent(this, LOG_MESSAGE,
            JHAVETranslator.translateMessage("connectingTo", 
                getClientProperties().getServer())));
//            "Connecting to " + getClientProperties().getServer()));
        try {
            if(!isConnected()) {
                clientSideSocket = new Socket(clientProperties.getServer(), clientProperties.getPort());
                outToServer = new ObjectOutputStream(clientSideSocket.getOutputStream());
                outToServer.flush();
                inFromServer = new ObjectInputStream(clientSideSocket.getInputStream());
                start();
                
                outToServer.writeObject(FC_CONNECT_TO_SERVER + " " + clientProperties.getCategory() + " " + username);
                outToServer.flush();
                
                NetworkEvent event = new NetworkEvent(this, FC_CONNECT_TO_SERVER, clientProperties.getCategory() + " " + username);
                fireOutboundTransaction(event);
                
                outToServer.writeObject(algorithms);
                outToServer.flush();
            }
        } catch (IOException e) {
            NetworkEvent event = new NetworkEvent(this, NetworkEvent.LOG_MESSAGE,
                JHAVETranslator.translateMessage("couldNotConnect"));
//                "Error: Could not connect to server");
            fireOutboundTransaction(event);
            throw e;
        }
    }
    
    /**
     * Requests that the server pings the client.
     * @throws IOException an error occured while talking to the server.
     */
    public void requestPing() throws IOException {
        sendToServer(FC_REQUEST_PING,
            JHAVETranslator.translateMessage("requestPing"));
//            "Requesting Ping");
    }
    
    /**
     * Responds to a Ping from the server.
     * @throws IOException Problem occured when sending message
     */
    private void pong() throws IOException {
        sendToServer(FC_PONG,
            JHAVETranslator.translateMessage("pong"));
//            "Pong");
    }
    
    /**	if(isLoggedIn())

     * Requests a static script for the currently selected algorithm.
     * @throws IOException a problem occured sending information to the server.
     */
    public void requestStaticScript() throws IOException {
        requestStaticScript(getSelectedAlgorithm());
    }
    
    /**
     * Requests a static script from the server
     * @param algorithm Algorithm to request
     * @throws IOException a problem occured sending information to the server.
     */
    public void requestStaticScript(Algorithm algorithm) throws IOException {
        lastAlgorithm = algorithm;
        sendToServer(FC_REQUEST_STATIC_SCRIPT, algorithm.GetAlgoName());
    }
    
    /**
     * Requests a scored static script for the currently selected algorithm.
     * @throws IOException a problem occured sending information to the server.
     */
    public void requestScoredStaticScript() throws IOException {
        requestScoredStaticScript(getSelectedAlgorithm());
    }
    
    /**
     * Requests a scored static script for an algorithm.
     * @param algorithm Algorithm to request.
     * @throws IOException a problem occured sending information to the server.
     */
    //Note by TN: In the new way of interaction for quizzes, we assume
    //that the user has logged in prior to this.  Hence if the
    //username is non-null they must have already successfully logged
    //in.  So this method is greatly simplified.  The old version of
    //the method appears in commented-out fashion below.
    public void requestScoredStaticScript(Algorithm algorithm) throws IOException {
//         // Make sure the user has entered a password and username.
//         if(username == null || password == null) {
//             if(setNameAndPassword() == LoginDialog.CANCEL_OPTION) {
//                 // If the user canceled we return otherwise do nothing.
//                 return;
//             }
//         }
        
        if (username != null) setLoggedIn(true);
        lastAlgorithm = algorithm;
//         // Create a string from the char array
//         String passwordString = "";
//         for (int index = 0; index < password.length; index++) {
//             passwordString += password[index];
//         }
//         // Write to server - Not sure if the synchronization is needed. Someone with more thread experience please fix. FIXME
//         synchronized (outToServer) {
//             outToServer.writeObject(FC_REQUEST_QUIZ_LOGIN + " " + algorithm.GetAlgoName() + " " + username + " " + passwordString);
//             outToServer.flush();
//         }
// 	while(!loginResponse) {
// 	    // Wait for a login response from the server
// 	}
// 	loginResponse = false;
	if(isLoggedIn()) requestStaticScript(algorithm);
    }
    
//     /**
//      * Requests a scored static script for an algorithm.
//      * @param algorithm Algorithm to request.
//      * @throws IOException a problem occured sending information to the server.
//      */
//     public void requestScoredStaticScript(Algorithm algorithm) throws IOException {
//         // Make sure the user has entered a password and username.
//         if(username == null || password == null) {
//             if(setNameAndPassword() == LoginDialog.CANCEL_OPTION) {
//                 // If the user canceled we return otherwise do nothing.
//                 return;
//             }
//         }
//         
//         setLoggedIn(true);
//         lastAlgorithm = algorithm;
//         // Create a string from the char array
//         String passwordString = "";
//         for (int index = 0; index < password.length; index++) {
//             passwordString += password[index];
//         }
//         // Write to server - Not sure if the synchronization is needed. Someone with more thread experience please fix. FIXME
//         synchronized (outToServer) {
//             outToServer.writeObject(FC_REQUEST_QUIZ_LOGIN + " " + algorithm.GetAlgoName() + " " + username + " " + passwordString);
//             outToServer.flush();
//         }
// 	while(!loginResponse) {
// 	    // Wait for a login response from the server
// 	}
// 	loginResponse = false;
// 	if(isLoggedIn()) requestStaticScript(algorithm);
//     }
//     
    /**
     * Requests a generated script for the currently selected algorithm.
     * @param newData if new data is going to be generated for the algorithm.
     * @param parameters Parameters to use for generated script.
     * @throws IOException a problem occured sending information to the server.
     */
    public void requestGeneratedScript(String parameters, boolean newData) throws IOException {
        requestGeneratedScript(getSelectedAlgorithm(), parameters, newData);
    }
    
    /**
     * Requests a generated script with given parameters.
     * @param newData if the algorithm is using new data.
     * @param algorithm Algorithm to request.
     * @param parameters Parameters to use for generated script.
     * @throws IOException problem occured sending information to the server.
     */
    public void requestGeneratedScript(Algorithm algorithm, String parameters, boolean newData) throws IOException {
        String message = "";
        lastAlgorithm = algorithm;
        if(newData) {
          message = JHAVETranslator.translateMessage("requestGeneratedNew",
              new Object[] { algorithm.GetAlgoName(), parameters});
//            message = algorithm.GetAlgoName() + " new " + parameters;
        } else {
          message = JHAVETranslator.translateMessage("requestGeneratedOld",
              new Object[] { algorithm.GetAlgoName(), parameters});
//            message = algorithm.GetAlgoName() + " old " + parameters;
        }
        
        sendToServer(FC_REQUEST_GENERATED_SCRIPT, message);
	
    }
    
    /**
     * Requests a generated script for the currently selected algorithm.
     * @param parameters Parameters to use for generated script.
     * @throws IOException a problem occured sending information to the server. */
    public void requestScoredGeneratedScript(String parameters) throws IOException {
        requestScoredGeneratedScript(getSelectedAlgorithm(), parameters);
    }
    
    /**
     * Requests a generated script with given parameters as well as logs into the server.
     * @param algorithm Algorithm to request.
     * @param parameters Parameters to use for generated script.
     * @throws IOException problem occured sending information to the server. */
    //Note by TN: In the new way of interaction for quizzes, we assume
    //that the user has logged in prior to this.  Hence if the
    //username is non-null they must have already successfully logged
    //in.  So this method is greatly simplified.  The old version of
    //the method appears in commented-out fashion below.
    public void requestScoredGeneratedScript(Algorithm algorithm, String parameters) throws IOException {
        // Make sure the user has entered a password and username.
//         if(username == null || password == null) {
//             if(setNameAndPassword() == LoginDialog.CANCEL_OPTION) {
//                 // If the user canceled we return otherwise do nothing.
//                 return;
//             }
//         }
        
        if (username != null) setLoggedIn(true);
        lastAlgorithm = algorithm;
        // Create a string from the char array
//         String passwordString = "";
//         for (int index = 0; index < password.length; index++) {
//             passwordString += password[index];
//         }
        
        // Write to server - Im not sure if the synchronization is needed, but I don't want multiple threads causeing problems.
//         synchronized (outToServer) {
//             outToServer.writeObject(FC_REQUEST_QUIZ_LOGIN + " " + algorithm.GetAlgoName() + " " + username + " " + passwordString);
//             outToServer.flush();
//         }
// 	while(!loginResponse) {
// 	    // Wait for a login response from the server
// 	}
// 	loginResponse = false;
	if(isLoggedIn()) requestGeneratedScript(algorithm, parameters, true);
    }
    
//     /**
//      * Requests a generated script with given parameters as well as logs into the server.
//      * @param algorithm Algorithm to request.
//      * @param parameters Parameters to use for generated script.
//      * @throws IOException problem occured sending information to the server. */
//     public void requestScoredGeneratedScript(Algorithm algorithm, String parameters) throws IOException {
//         // Make sure the user has entered a password and username.
//         if(username == null || password == null) {
//             if(setNameAndPassword() == LoginDialog.CANCEL_OPTION) {
//                 // If the user canceled we return otherwise do nothing.
//                 return;
//             }
//         }
//         
//         setLoggedIn(true);
//         lastAlgorithm = algorithm;
//         // Create a string from the char array
//         String passwordString = "";
//         for (int index = 0; index < password.length; index++) {
//             passwordString += password[index];
//         }
//         
//         // Write to server - Im not sure if the synchronization is needed, but I don't want multiple threads causeing problems.
//         synchronized (outToServer) {
//             outToServer.writeObject(FC_REQUEST_QUIZ_LOGIN + " " + algorithm.GetAlgoName() + " " + username + " " + passwordString);
//             outToServer.flush();
//         }
// 	while(!loginResponse) {
// 	    // Wait for a login response from the server
// 	}
// 	loginResponse = false;
// 	if(isLoggedIn()) requestGeneratedScript(algorithm, parameters, true);
//     }
//     
    /**
     * Submits a students answer to a question.
     * @param qid The string identifying the question.
     * @param answer The answer given by the student.
     * @throws IOException problem occured sending information to the server. */
    public void submitStudentAnswerToQuestion(String qid, String answer) throws IOException {
        submitStudentAnswerToQuestion(getSelectedAlgorithm(), qid, answer);
    }
    
    /**
     * Submits a students answer to a question.
     * @param algorithm The algorithm with which the question pertains.
     * @param qid The string identifying the question.
     * @param answer The answer given by the student.
     * @throws IOException problem occured sending information to the server.
     */
    public void submitStudentAnswerToQuestion(Algorithm algorithm, String qid, String answer) throws IOException {
        sendToServer(FC_SUBMIT_ANSWER_TO_QUESTION, algorithm.GetAlgoName() + " " + qid + " " + answer);
    }
    
    // End Outbound connection methods
    /////////////////////////////////////////////////////////////////////////////////


    //FAKE CLIENT JN
    // These are the two operations that are part of the "fake" client
    // and server.
    public void sendLogin(int i,String s) throws IOException
    {
	sendToServer(i,s);
    }
    
    public void sendXMLString(int i,String s) throws IOException
    {
	sendToServer(i,s);
    }
    //FAKE CLIENT JN
}


