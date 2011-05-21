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
 * Abstract class GAIGSbase - provides basic instance variables and functionality
 * for linear GAIGS structures and for <code>GAIGSarray</code>.
 * 
 * @author Myles McNally
 * @version 6/20/06
 */

public abstract class GAIGSbase implements GAIGSdatastr {
    
    
    /**
     * Display name.
     */
    String name;
    
        /**
     * Display color.
     */
    String color;
    
    /**
     * Left bound for display.
     */
    double x1;
    
    /**
     * Bottom bound for display.
     */
    double y1;
    
    /**
     * Right bound for display.
     */
    double x2;
    
    /**
     * Top bound for display.
     */
    double y2;
    
    /**
     * Font size for display.
     */
    double fontSize;

//---------------------- Display Setter/Getter Methods ---------------------------------


    /**
     * Set the value of the name to be displayed.
     * 
     * @param       name        The display name.
     */
    public void setName (String name) {
        this.name = name;
    }
    
    /**
     * Get the value of the name.
     * 
     * @return      The display name.
     */
    public String getName () {
        return name;
    } 
}
