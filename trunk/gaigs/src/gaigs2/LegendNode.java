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

package gaigs2;

/**
 * Class to store the information for each element in the color legend. A
 * <code>LegendNode</code> stores the color that is to appear in the legend
 * element's box and the text that is to appear next to the this box.
 *
 * @author Andrew Jungwirth
 * @version 1.0 (29 June 2006)
 */

public class LegendNode{
    /**
     * The hex color <code>String</code> that defines the color that is to be
     * displayed in the key box.
     */
    public String color;

    /**
     * The text that is to appear next to the box. Each element of this
     * <code>LinkedList</code> should be a <code>String</code> containing a
     * line of the text that is to appear next to the box. A multiline label
     * should be stored with each line in a separate element in the
     * <code>LinkedList</code>.
     */
    public LinkedList text;

    /**
     * Default <code>LegendNode</code> constructor. The color is set to black
     * (i.e., "#000000"), and the text <code>LinkedList</code> is initialized
     * using its default constructor.
     */
    public LegendNode(){
	color = "#000000";
	text = new LinkedList();
    }
}

