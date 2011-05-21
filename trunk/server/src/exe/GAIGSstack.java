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
 * <p><code>GAIGSstack</code> extends the <code>GAIGSlist</code> class, providing
 * the ability to implement a standard stack data structure and also create GAIGS
 * visualizations of its state. Use the various constructors to specify the general
 * parameters for the stack visualization, and use the <code>toXML</code> method to
 * actually generate the stack XML for snapshots.</p>
 * 
 * <p>A method is also provided to set the presentation color of a stack cell.
 * A default stack cell color can be set by using the appropriate constructor. 
 * Methods to get a cell color are inherited from <code>GAIGSlist</code>.</p>
 * 
 * @author Myles McNally (current version)
 * @version 6/20/06
 */

public class GAIGSstack extends GAIGSlist{   
    

//---------------------- Constructors -------------------------------------------


    /**
     * Use all default values for instance variables
     */
    public GAIGSstack() {
        super();
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
    public GAIGSstack(String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        super(name, color, x1, y1, x2, y2, fontSize);
    }
    
    
//---------------------- Stack Methods -----------------------------------------


    /**
     * Adds an item to the stack. Default color will be used
     * for this item.
     * 
     * @param     v     The value to be stored. 
     */
    public void push (Object v) {
        addFirst(v);
    }
    
    /**
     * Adds an item with an associated color to the stack.
     * 
     * @param     v     The value to be stored. 
     * @param     c     The display color for this item.  
     */
    public void push (Object v, String c) {
        addFirst(v, c);
    }
    
    /**
     * removes an item from the stack and returns it.
     * 
     * @return     An <code>Object</code> containing popped value  
     */
    public Object pop () {
        return removeFirst();
    }

    /**
     * returns but does not remove an item from the stack.
     *
     * @return      An <code>Object</code> containing the next value to be popped.
     */
    public Object peek () {
        return getFirst();
    }
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the stack
     * 
     * @return     A <code>String</code> containing GAIGS XML code for the stack  
     */
    public String toXML() {
        return toXML("stack");
    }

}
