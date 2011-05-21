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
 * DocumentationEvent.java
 *
 * Created on July 29, 2004, 11:15 PM
 */

package jhave.event;

import java.net.URI;
import java.util.EventObject;
/**
 * Event used for encapsulating information sent to DocumentationListeners.
 * @author  Chris Gaffney
 */
public class DocumentEvent extends EventObject {
    /** Information page type event. */
    public static final int TYPE_INFORMATION_PAGE = 0;
    /** Pseudocode type event. */
    public static final int TYPE_PSEUDOCODE_PAGE = 1;
    
    /** The type of the event. */
    private int type;
    /** The documentation url. */
    private URI resource;
    
    /**
     * Creates a new instance of DocumentationEvent 
     * @param source the object who fired that event.
     * @param type the type of event (what type of document).
     * @param page the document page.
     */
    public DocumentEvent(Object source, int type, URI resource) {
        super(source);
        this.type = type;
        this.resource = resource;
    }
    
    /**
     * Get the type of the event.
     * @return int the type of documentation event
     */
    public int getType() {
        return type;
    }
    
    /**
     * Get the URL of the document.
     * @return URL url of the document.
     */
    public URI getPage() {
        return resource;
    }
    
    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("DocumentationEvent[");
        buff.append("type=");
        buff.append(type);
        buff.append(", ");
        buff.append("uri=");
        buff.append(resource.toString());
        buff.append("]");
        
        return buff.toString();
    }
}
