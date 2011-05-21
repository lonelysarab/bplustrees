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
 * <p><code>GAIGSlabel</code> is not a true GAIGS structure, and is just a work around
 * for placing labels in GAIGS visualizations. The XML code generated is for an array
 * with no items, and hence only the structure's name is displayed. Care must be taken
 * in setting the bounds for the label, as the name of a structure is placed within these
 * bound but not aligned with any of them.  Some experiementation will probably be required.</p>
 * 
 * <p>Use the constructor to specify the label's text, bounds, and font size, and use the
 * <code>toXML</code> method to actually generate the XML for snapshots.</p>
 * 
 * @author Myles McNally 
 * @version 5/28/06
 */


public class GAIGSlabel extends GAIGSbase {
    
//---------------------- Constructors -------------------------------------------

    
    /**
     * Explicitly set all  instance variables.
     * 
     * @param       name            Display name of this structure.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSlabel(String name, double x1, double y1, double x2, double y2, double fontSize) {
        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.fontSize = fontSize;
    }
    
    
//---------------------- Convenience Methods ---------------------------------
    
    /**
     * Set the value of the name to be displayed.
     * 
     * @param       name        The display name.
     */
    public void setLabel (String name) {
        setName(name);
    }  

    /**
     * Get the value of the name.
     * 
     * @return      The display name.
     */
    public String getLabel () {
        return getName();
    }  
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current label state.  This is 
     * actually <code>GAIGSarray</code> XML, but with no items specified.  Hence
     * only the name of the structure is displayed.
     * 
     * @return     A String containing GAIGS XML code for the stack  
     */
    public String toXML() {
        String xmlString = "";
    
        xmlString += "<array>" + "\n";
        if (name != null)
            xmlString += "<name>" + name + "</name>" + "\n";
        xmlString += "<bounds "
                     + "x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2
                     + "\" fontsize=\"" + fontSize + "\"/>" + "\n";       
        xmlString += "</array>" + "\n";
        
        return xmlString;
    }
}
