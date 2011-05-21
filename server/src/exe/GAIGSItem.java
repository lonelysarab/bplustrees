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
 * <p><code>GAIGSItem</code> is used by a number of GAIGS XML Support classes to store
 * an item and its associated display color.<p>Known clients are <code>GAIGSarray</code>,
 * <code>GAIGSlist</code>, <code>GAIGSqueue</code>, and <code>GAIGSstack</code>.</p>
 * 
 * @author Myles McNally 
 * @version 5/28/06
 */

public class GAIGSItem {

//---------------------- Instance Variables -------------------------------------

    /**
     * The value of this item
     */
    Object value;

    /**
     * The display color for this item
     */
    String color;
    
//---------------------- Constructors -------------------------------------------

    /**
     * Create a GAIGSItem
     * 
     * @param   v    The value.
     * @param   c    The color.
     */
    public GAIGSItem(Object v, String c) {
        value = v;
        color = c;
    }

//---------------------- Getter/Setter Methods ---------------------------------

    /**
     * Get the value of this item
     * 
     * @return      The value of this item
     */
    public Object getValue () {
        return value;
    }
    
    /**
     * Set the value of this item
     * 
     * @param    v      The value of this item
     */
    public void setValue (Object v) {
        value = v;
    }

    /**
     * Get the display color of this item
     * 
     * @return      The color of this item
     */
    public String getColor () {
        return color;
    }
    
    /**
     * Set the display color of this item
     * 
     * @param    v      The color of this item
     */
    public void setColor (String c) {
        color = c;
    }
}
