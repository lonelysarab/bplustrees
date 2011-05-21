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

package exe;

/**
 * <p><code>GAIGSqueue</code> extends the <code>GAIGSlist</code> class, providing
 * the ability to implement a standard queue data structure and also create GAIGS
 * visualizations of its state. Use the various constructors to specify the general
 * parameters for the queue visualization, and use the <code>toXML</code> method to
 * actually generate the queue XML for snapshots.</p>
 * 
 * <p>A method is also provided to set the presentation color of a queue cell.
 * A default queue cell color can be set by using the appropriate constructor. 
 * Methods to get a cell color are inherited from <code>GAIGSlist</code>.</p>
 * 
 * @author Myles McNally (current version) 
 * @version 6/20/06
 */

public class GAIGSqueue extends GAIGSlist {
    
    
//---------------------- Constructors -------------------------------------------


    /**
     * Use all default values for instance variables
     */
    public GAIGSqueue() {
        super(DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
    /**
     * Explicitly set all  instance variables.
     * 
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSqueue(String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        super(name, color, x1, y1, x2, y2, fontSize);
    }
    
      
//---------------------- Queue Methods -----------------------------------------


    /**
     * Adds an item to the queue. Default color will be used
     * for this item.
     * 
     * @param     v     The value to be stored. 
     */
    public void enqueue (Object v) {
    addLast(v);

    }
    
    /**
     * Adds an item with an associated color to the queue.
     * 
     * @param     v     The value to be stored. 
     * @param     c     The display color for this item.  
     */
    public void enqueue (Object v, String c) {
    addLast(v, c);
    }
    
    /**
     * removes an item from the queue and returns it.
     * 
     * @return     An <code>Object</code> containing popped value  
     */
    public Object dequeue () {
        return removeFirst();
    }
     
    /**   
     * returns but does not remove an item from the queue.
     *
     * @return      An <code>Object</code> containing the next value to be dequeued.
     */
    public Object peek () {
        return getFirst();
    }
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the queue
     * 
     * @return     A <code>String</code> containing GAIGS XML code for the queue  
     */
    public String toXML() {
        return toXML("queue");
    }
}
