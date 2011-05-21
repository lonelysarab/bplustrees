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
 * Visualizer.java
 *
 * Created on July 30, 2004, 1:47 PM
 */

package jhave.core;

import java.io.*;
import java.util.*;
import jhave.event.*;

import java.net.URI;
import javax.swing.JComponent;
import jhave.question.Question;
import javax.swing.event.EventListenerList;
/**
 * Base class for all visualizer implementations.
 * @author  Chris Gaffney
 */
public abstract class Visualizer {
    /** The visualizer is controllable. */
    public static final int CAP_CONTROLLABLE = 1;
    /** The visualizer is animated, default is slide show style. */
    public static final int CAP_ANIMATED = 2;
    /** The visualizer supports stepping forward. */
    public static final int CAP_STEP_FORWARD = 4;
    /** The visualizer supports stepping backward. */
    public static final int CAP_STEP_BACKWARD = 8;
    /** The visualizer supports playing frames / key frames in succession. */
    public static final int CAP_PLAY = 16;
    /** Visualizer supports stopping play. Not available if play is not supported. */
    public static final int CAP_STOP = 32;
    /** Visualizer supports pausing play. Not available if play is not supported. */
    public static final int CAP_PAUSE = 64;
    /** Visualizer supports going directly to a specified frame. */
    public static final int CAP_GOTO_FRAME = 128;
    /** Visualizer supports zooming. */
    public static final int CAP_ZOOM = 256;
    /** Visualizer supports finishing/grading. */
    public static final int CAP_FINISH = 512;
    /** Visualizer supports toggling model answer / student answer. */
    public static final int CAP_MODEL = 1024;
    
    /** Container of the event listeners. */
    private EventListenerList listenerList;
    /** The visualizers capabilities as created from a mask of the CAP_* constants. */
    private int capabilities = 0;
    
    /** */
    private List eventQueue;
    /** */
    private boolean hasQuestionListener = false;
    /** */
    private boolean hasDocumentationListener = false;
    /** */
    private boolean hasAudioTextListener = false; // Added by TLN, 6/8/07
    
    /**
     * Creates a new instance of Visualizer
     * @param script the InputStream that will contain the script to be read.
     * @throws IOException an error occurs while reading from the InputStream.
     */
    public Visualizer(InputStream script) throws IOException {
        listenerList = new EventListenerList();
        eventQueue = new LinkedList();
    }
    
    /**
     * Returns the Visualizers rendering pane.
     * @return JComponent the Visualizers rendering pane.
     */
    public abstract JComponent getRenderPane();
    
    /**
     * Register a QuestionListener with this visualizer.
     * @param listener the QuestionListener to register.
     */
    public void addQuestionListener(QuestionListener listener) {
        listenerList.add(QuestionListener.class, listener);
        if(!hasQuestionListener) {
            Iterator itr = eventQueue.iterator();
            while(itr.hasNext()) {
                Object o = itr.next();
                if(o instanceof QuestionEvent) {
                    fireQuestionEvent((QuestionEvent)o);
                    itr.remove();
                }
            }
            hasQuestionListener = true;
        }
    }
    
    /**
     * Unregister a QuestionListener with this visualizer.
     * @param listener the QuestionListener to unregister.
     */
    public void removeQuestionListener(QuestionListener listener) {
        listenerList.remove(QuestionListener.class, listener);
    }
    
    /**
     * Fire a QuestionEvent to all registered listeners.
     * @param q the question to be attached to the QuestionEvent.
     */
    protected void fireQuestionEvent(Question q) {
        QuestionEvent e = new QuestionEvent(this, q);
        if(hasQuestionListener) {
            fireQuestionEvent(e);
        } else {
            eventQueue.add(e);
        }
    }
    
    /**
     *
     */
    private void fireQuestionEvent(QuestionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == QuestionListener.class) {
                ((QuestionListener)listeners[i+1]).handleQuestion(e);
            }
        }
    }
    
    /**
     * Register a DocumentationListener with this visualizer.
     * @param listener the DocumentationListener to register.
     */
    public void addDocumentationListener(DocumentationListener listener) {
        listenerList.add(DocumentationListener.class, listener);
        if(!hasDocumentationListener) {
            Iterator itr = eventQueue.iterator();
            while(itr.hasNext()) {
                Object o = itr.next();
                if(o instanceof DocumentEvent) {
                    fireDocumentationEvent((DocumentEvent)o);
                    itr.remove();
                }
            }
            hasDocumentationListener = true;
        }
    }
    
    /**
     * Unregister a DocumentationListener with this visualizer.
     * @param listener the DocumentationListener to unregister.
     */
    public void removeDocumentationListener(DocumentationListener listener) {
        listenerList.remove(DocumentationListener.class, listener);
    }
    
    /**
     * Fire a DocumentEvent to all registered DocumentationListeners.
     * @param document the URL of the document.
     * @param type the type of the document as defined in the DocumentEvent class.
     */
    protected void fireDocumentationEvent(URI document, int type) {
        DocumentEvent e = new DocumentEvent(this, type, document);
        if(hasDocumentationListener) {
            fireDocumentationEvent(e);
        } else {
            eventQueue.add(e);
        }
    }
    
    /**
     *
     */
    private void fireDocumentationEvent(DocumentEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == DocumentationListener.class) {
                ((DocumentationListener)listeners[i+1]).showDocument(e);
            }
        }
    }


    /****   AudioTextEvent handling -- TLN 6/8/07 ***********/

    
    /**
     * Register a AudioTextListener with this visualizer.
     * @param listener the AudioTextListener to register.
     */
    public void addAudioTextListener(AudioTextListener listener) {
        listenerList.add(AudioTextListener.class, listener);
        if(!hasAudioTextListener) {
            Iterator itr = eventQueue.iterator();
            while(itr.hasNext()) {
                Object o = itr.next();
                if(o instanceof AudioTextEvent) {
                    fireAudioTextEvent((AudioTextEvent)o);
                    itr.remove();
                }
            }
            hasAudioTextListener = true;
        }
    }
    
    /**
     * Unregister a AudioTextListener with this visualizer.
     * @param listener the AudioTextListener to unregister.
     */
    public void removeAudioTextListener(AudioTextListener listener) {
        listenerList.remove(AudioTextListener.class, listener);
    }
    
    /**
     * Fire a AudioTextEvent to all registered AudioTextListeners.
     * @param document the URL of the document.
     * @param the_text the text of the event as defined in the AudioTextEvent class.
     */
    protected void fireAudioTextEvent(String the_text) {
        AudioTextEvent e = new AudioTextEvent(this, the_text);
        if(hasAudioTextListener) {
            fireAudioTextEvent(e);
        } else {
            eventQueue.add(e);
        }
    }
    
    /**
     *
     */
    private void fireAudioTextEvent(AudioTextEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == AudioTextListener.class) {
                ((AudioTextListener)listeners[i+1]).speakAudioText(e);
            }
        }
    }


    /****   End AudioTextEvent handling -- TLN 6/8/07 ***********/

    
    /**
     * Returns the capabilities of the visualizer.
     * @return mask of the capabilities as defined by the CAP_* constants.
     */
    public int getCapabilities() {
        return capabilities;
    }
    
    /**
     * Sets the capabilities of the visualizer.
     * @param capabilities the mask of the capabilities.
     */
    protected void setCapabilities(int capabilities) {
        this.capabilities = capabilities;
    }
    
    /**
     * Returns the total number of frames / key frames.
     * @return the total number of frames / key frames.
     */
    public abstract int getFrameCount();
    
    /**
     * Returns the current frame, must be zero (0) based.
     * @return the current frame / key frame.
     */
    public abstract int getCurrentFrame();
    
    /**
     * Go directly to the given frame, do not animate to it. This method should
     * not block.
     * @param frame the frame / key frame number to go to, zero based.
     */
    public void gotoFrame(int frame) {
//        throw new OperationNotSupportedException("Goto frame not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Goto frame"));
    }
    
    /**
     * Step back one frame, animating if necessary. This method must block until
     * the animation or frame change is complete.
     */
    public void stepBackward() {
//        throw new OperationNotSupportedException("Step backward not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Step backward"));
    }
    
    /**
     * Step forward one frame, animating if necessary. This method must block until
     * the animation or frame change is complete.
     */
    public void stepForward() {
//        throw new OperationNotSupportedException("Step forward not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Step forward"));
    }
    
    /**
     * Play several frames in succession. This method must block until it reaches
     * the end of the script or pause / stop are called. If pause or stop is
     * called then play should stop play immediatlly if possible and
     * unblock / return.
     */
    public void play() {
//        throw new OperationNotSupportedException("Play not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Play"));
    }
    
    /**
     * Pause the visualization at the current frame. This method should make
     * a call to play unblock immeditatlly if possible.
     */
    public void pause() {
//        throw new OperationNotSupportedException("Pause not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Pause"));
    }
    
    /**
     * Stops a visualizer from playing and returns to the first frame.
     */
    public void stop() {
//        throw new OperationNotSupportedException("Stop not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Stop"));
    }
    
    /**
     * Set the zoom of the visualizer to the specified level. The parameter is
     * a percentage with 1.0 being 100% or default zoom.
     * @param level zoom percentage.
     */
    public void zoom(double level) {
//        throw new OperationNotSupportedException("Zooming not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Zooming"));
    }
    
    /**
     * Returns the current zoom level.
     * @return current zoom level.
     */
    public double getZoom() {
//        throw new OperationNotSupportedException("Zooming not supported.");
      throw new OperationNotSupportedException(
          JHAVETranslator.translateMessage("operationNotSupported", "Zooming"));
    }
    
    public void toggleModelAnswerVisible() {
        throw new OperationNotSupportedException(
                JHAVETranslator.translateMessage("operationNotSupported", "Toggle Model Answer"));
    }
    
    public int[] finish() {
    	throw new OperationNotSupportedException(
                JHAVETranslator.translateMessage("operationNotSupported", "Finish"));
    }
}
