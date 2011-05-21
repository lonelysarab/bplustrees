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
 * AudioTextEvent.java
 *
 * Created on June 2007
 */

package jhave.event;

import java.net.URI;
import java.util.EventObject;

/**
 * Event used for encapsulating information sent to AudioTextListeners.
 * @author  Tom Naps
 */
public class AudioTextEvent extends EventObject {

    /** The text to be spoken. (A file if contains .au or .wav) */
    private String the_text_to_speak;

    /**
     * Creates a new instance of AudioTextEvent that is text-to-speech
     * @param source the object who fired that event.
     * @param the_text the text we want to hear.
     */
    public AudioTextEvent(Object source, String the_text) {
        super(source);
        the_text_to_speak = the_text;
    }

    
    /**
     * Get the type of the event -- text-to-speech or audio file.
     * @return boolean true if text-to-speech event
     */
    public boolean is_text_to_speech() {
        return !(the_text_to_speak.contains(".au") || the_text_to_speak.contains(".wav"));
    }
    
    /**
     * Get the URI of the audio file.  Only should be called when
     * is_text_to_speech returns false
     * @return URI uri of the audio file.
     */
    public URI getAudioResource() {
	URI audioURI = null;
	if (!is_text_to_speech()) { // Then it must be a URL
	    try {
		audioURI = new URI(the_text_to_speak);
		// If it does have a scheme definition then it's already set to go.
		if(audioURI.getScheme() == null) {
		    // Now we recreate it with the appended rel scheme
		    audioURI = new URI("Rel:" + the_text_to_speak);
		} 
	    }
	    catch (Exception e) {
		System.out.println(the_text_to_speak + " is not an audio file");
	    }
	    return audioURI;
	}
	else {
	    System.out.println(the_text_to_speak + " is not an audio file");
	    return null;
	}
    }
    
    /**
     * Get the text of the audio event.
     * @return text of the event.
     */
    public String getText() {
        return the_text_to_speak;
    }
    
    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("AudioTextEvent[");
	buff.append( (is_text_to_speech() ? " text-to-speech " : " audio file " ) );
        buff.append( (is_text_to_speech() ? the_text_to_speak : getAudioResource().toString()) );
        buff.append("]");
        
        return buff.toString();
    }
}
