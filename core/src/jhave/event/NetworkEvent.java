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
 * NetworkEvent.java
 *
 * Created on June 12, 2002, 2:16 AM
 */
package jhave.event;

import java.util.EventObject;
import java.util.StringTokenizer;

import jhave.core.TransactionCodes;
/** 
 * Network event. This event is used as the parameter for a network listener
 * object. It is given a String and then parses out the transaction code by
 * using ' ' (single space) as a delimiter and expecting the code to be the
 * first entry in the message. It can also be given the items seperatly.
 * It then stores this code and the message with out the code.
 *
 * @author  Chris Gaffney
 * @version 1.0
 */
public class NetworkEvent extends EventObject implements TransactionCodes {
    
    /** 
     * If no transaction code was sent from the server. Put here because this code
     * is unique to NetworkEvents and not an actual transaction.
     */
    public static final int NO_TRANSACTION_CODE = -1000;
    
    /** The transaction code. */
    private int transactionCode = NO_TRANSACTION_CODE;
    /** The message received from the server. */
    private String message = null;
    /** If the event has a transaction code. */
    private boolean hasTransactionCode = false;
    /** If an extra transaction was sent from the server. */
    private boolean hasAppendedTransaction = false;
    /** Object appended from a 2nd transaction */
    private Object appendedTransaction = null;
    
    /**
     * Instantiates a new NetworkEvent with given data from the server (inbound).
     * Event is specified as an outbound event.
     * @param source The object that the event originates from
     * @param message The message received from the server
     */
    public NetworkEvent(Object source, String message) {
        super(source);
        message = message.trim();
        
        if(message.length() > 0 && (Character.isDigit(message.charAt(0)) || Character.isDigit(message.charAt(1)))) {
            try {
                transactionCode = Integer.parseInt(message.substring(0, message.indexOf(" ")));
                hasTransactionCode = true;
                this.message = message.substring(message.indexOf(" ") + 1);
            } catch (NumberFormatException e) {
                // Shouldn't happen but just in case.
                transactionCode = NO_TRANSACTION_CODE;
            }
        } else {
            this.message = message;
        }
    }
    
    /**
     * Instantiates a new NetworkEvent with given data from the client (outbound).
     * @param source The object that the event originates from
     * @param transactionCode The transaction code
     * @param message The additional information given from the server
     */
    public NetworkEvent(Object source, int transactionCode, String message) {
        super(source);
        this.transactionCode = transactionCode;
        hasTransactionCode = true;
        this.message = message.trim();
    }
    
    /**
     * Returns transaction code for the transaction between client and server.
     * @return int The transaction code of the event
     */
    public int getTransactionCode() {
        return transactionCode;
    }
    
    /**
     * Returns a tokenized version of the message using ' ' (single space) as the delimiter,
     * the transaction code is not included. Creates a new StringTokenizer on every call.
     * @return StringTokenizer The message
     */
    public StringTokenizer getMessageTokenized() {
        return new StringTokenizer(message);
    }
    
    /**
     * Returns a String version of the message. The transaction code is not included.
     * @return String The message.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Returns if teh network event has a transaction code.
     * @return boolean if the network event has a transcation code.
     */
    public boolean hasTransactionCode() {
        return hasTransactionCode;
    }
    
    /**
     * Creates a message in a format that can be sent to the server.
     * @return String message in a format the server understands.
     */
    public String getTransactionMessage() {
        String returnedString = "";
        
        if(hasTransactionCode()) {
            returnedString += getTransactionCode() + " ";
        }
        
        returnedString += message;
        return returnedString;
    }
}