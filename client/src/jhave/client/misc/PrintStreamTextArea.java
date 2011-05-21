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
 * PrintStreamTextArea.java
 *
 * Created on March 2, 2003, 4:55 PM
 * Last commit on $Date: 2007/08/05 02:36:54 $
 */
package jhave.client.misc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

import jhave.core.JHAVETranslator;
/**
 * A JTextArea that reads from a print stream.
 * @author Chris Gaffney
 * @version $Revision: 1.3 $
 */
public class PrintStreamTextArea extends JTextArea {
    
    /** InputStream that writes to the JTextArea. */
    PipedInputStream inputStream = new PipedInputStream() {
        /**
         * Closes this and related streams.
         * @throws IOException error occured closing the stream.
         */
        public void close() throws IOException {
            keepRunning = false;
            super.close();
            outputStream.close();
        }
    };
    
    /**
     * OutputStream that is written to to send to the JTextArea.
     */
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream() {
        /**
         * Closes this and related streams.
         * @throws IOException error occured closing the stream.
         */
        public void close() throws IOException {
            keepRunning = false;
            super.close();
        }
    };
    
    /** The PipedOutputStream that connects the PipedInputStream and ByteArraryOutputStream. */
    PipedOutputStream connectorStream = new PipedOutputStream();
    /** If the ReaderThread should keep looking for input. */
    private boolean keepRunning = true;
    
    /**
     * Creates a new instance of PrintStreamTextArea.
     * @throws IOException an error occured while creating the print stream.
     */
    public PrintStreamTextArea() throws IOException {
        setEditable(false);
        setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
        
        connectorStream.connect(inputStream);
        new PrintStreamTextArea.ReaderThread().start();
        new PrintStreamTextArea.WriterThread().start();
    }
    
    /**
     * Returns the Inputstream that writes to the JTextArea.
     * @return InputStream the input stream that writes to the JTextArea.
     */
    public InputStream getInputStream() {
        return inputStream;
    }
    
    /**
     * Returns the OutputStream that is written to to write on the JTextArea.
     * @return OutputStream the OutputStream that is written to to write on the JTextArea.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    /**
     * Reads from the PipedInputStream and writes to the JTextArea.
     */
    private class WriterThread extends Thread {
        /** Buffers the input to be printed. */
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        /**
         * Method invoked when the thread is started.
         */
        public void run() {
            char[] buff = new char[256];
            int count;
            boolean caretAtEnd = false;
            try {
                Document doc = getDocument();
                while(-1 != (count = reader.read(buff, 0, buff.length))) {
                    caretAtEnd = getCaretPosition() == doc.getLength() ? true : false;
                    append(String.valueOf(buff, 0, count));
                    if(caretAtEnd) {
                        setCaretPosition(doc.getLength());
                    }
                }
            } catch(IOException e) {
//                JOptionPane.showMessageDialog(null, "Error reading from BufferedReader: " + e);
              JOptionPane.showMessageDialog(null, 
                  JHAVETranslator.translateMessage("errorReading", e));
                System.exit(1);
            }
        }
    }
    
    /**
     * Reads from the ByteArray stream and writes to the PipedOutput stream. This
     * way if a thread writes to the stream and dies before another thread writes to
     * the stream there won't be a problem.
     */
    private class ReaderThread extends Thread {
        /**
         * Method invoked when the thread is started.
         */
        public void run() {
            while(keepRunning) {
                // Check for bytes in the stream.
                if(outputStream.size() > 0) {
                    byte[] buffer = null;
                    
                    synchronized(outputStream) {
                        buffer = outputStream.toByteArray();
                        outputStream.reset(); // Clear the buffer.
                    }
                    try {
                        // Send the extracted data to
                        // the PipedOutputStream.
                        connectorStream.write(buffer, 0, buffer.length);
                    } catch(IOException e) {
                        // FIXME: Do nothing?
                    }
                } else {
                    try {
                        // Check the ByteArrayOutputStream every
                        // 1 second for new data.
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        // FIXME: Do nothing?
                    }
                }
            }
        }
    }
}