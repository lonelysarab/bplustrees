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
 * <p>The GAIGSdatastr interface provides constants used as default values
 * for the name, color, position and font size of GAIGS structures.  It
 * requires methods for getting and setting the name of the structure,
 * and a <code>toXML</code> method, which is used to generate
 * the XML script for a structure.</p>
 * 
 * @author Myles McNally 
 * @version 6/20/06
 */

public interface GAIGSdatastr {

    /**
     * Default name for a GAIGS structure
     */
    String DEFAULT_NAME = null;
    
    /**
     * Default color for a GAIGS structure
     */
    String DEFAULT_COLOR = "#000000";
    
    /**
     * Default left bound for a GAIGS structure
     */
    double DEFAULT_X1 = 0;
    
    /**
     * Default bottom bound for a GAIGS structure
     */
    double DEFAULT_Y1 = 0;
    
     /**
     * Default right bound for a GAIGS structure
     */
    double DEFAULT_X2 = 1;
    
    /**
     * Default top bound for a GAIGS structure
     */
    double DEFAULT_Y2 = 1;
    
    /**
     * Default font size for a GAIGS structure
     */
    double DEFAULT_FONT_SIZE = 0.05;
    
    
    
    /**
     * Create and return the GAIGS XML script for this structure
     * 
     * @return      The <code>String</code> containing the script
     */
    String toXML();
    
    /**
     * Get the name for this structure
     * 
     * @return      The <code>String</code> containing the structure name
     */
    String getName();
    
    /**
     * Set the name for this structure
     * 
     * @param      name	The <code>String</code> containing the structure name
     */
    void setName (String name);
    
}
