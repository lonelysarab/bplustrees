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

// A sample (and simple) extension of the GAIGS StructureType

package gaigs2;

import java.awt.image.*;

import java.util.*; 

import org.jdom.*;

public class foobar extends StructureType {

    double circle_center_x, circle_center_y; // center coords
    double circle_rad;		// radius

    int circle_color;		// node color

    int circle_labelcolor;	// text color
    String circle_label;      // only set up for a single line of text

    
    // Must provide a parameterless constructor for instantiation via reflection
    public foobar() {
	super();		// necessary

	circle_color = White;	// our hex notation is "#RRGGBB"
	circle_labelcolor = Black;
	circle_label = null;
	circle_center_x = 0.50;
	circle_center_y = 0.50;
	circle_rad = 0.25;
    } // foobar()	


    // This initialization method gets passed a jdom.Element whose
    // name is "foobar".  So gaigs_sho.dtd must be modified, adding a
    // "foobar" element to the list of structure types a snap can
    // contain.
    public void loadStructure(Element rootEl, LinkedList thingsToRender, draw drawerObj) {
	// These two calls load and initialize the name and bounds if
	// your xml structure-element has a name and/or bounds like
	// the built-in structures.
	load_name_and_bounds(rootEl, thingsToRender, drawerObj);
	calcDimsAndStartPts(thingsToRender, drawerObj);

	// JDOM, AS WE NEED IT, IS EASY TO USE
	List children = rootEl.getChildren(); // getChildren returns a list
	Iterator iter = children.iterator(); // which we will iterate through

	Element labelEl;

	// NOTE: This is an unnecessary illustrative loop only, since
	// we could get what we want directly
	while( iter.hasNext() ) {
	    Element child = (Element) iter.next(); // walk through the list of children

	    if( child.getName().equals("name") ) {
		// Just showing we could get it if we wanted,
		//  but already done for us in load_name_and_bounds(..)
		String junkName;
		junkName = child.getText(); // get the text of this node in the XML tree
	    }
	    else if( child.getName().equals("bounds") ) {
		//  Just showing we could get it if we wanted,
		//  but already done for us in load_name_and_bounds(..)
		double junkBound;
		junkBound = Format.atof( child.getAttributeValue("x1") ); // get an attribute
	    }
	    else if( child.getName().equals("nodelabel") )
		labelEl = child; 
	} // End illustrative loop

	// Here we could get elements directly --
	labelEl = rootEl.getChild("nodelabel");
	
	// The XML is validated against the DTD, so if there is a
	// #REQUIRED attribute or a default value we can safely assume
	// it is there
	circle_center_x = Format.atof( rootEl.getAttributeValue("x") );
	circle_center_y = Format.atof( rootEl.getAttributeValue("y") );

	circle_color = colorStringToInt( rootEl.getAttributeValue("color") );

	if( labelEl != null ) {
	    circle_label = labelEl.getAttributeValue("text");
	    circle_labelcolor = colorStringToTextColorInt( rootEl.getAttributeValue("color") );
	    circle_rad = normalized_width(circle_label)  / 2.0;
	}
    } // loadStructure


    // Use the LGKS object to draw the structure
    public void drawStructure(LinkedList thingsToRender, draw drawerObj) {
	// draw the circle(filled)
	super.drawStructure(thingsToRender, drawerObj);
	LGKS.set_fill_int_style(bsSolid, circle_color, thingsToRender, drawerObj);
	LGKS.circle_fill(circle_center_x, circle_center_y, circle_rad, thingsToRender, drawerObj);

	// draw the circle outline
	LGKS.set_textline_color(Black, thingsToRender, drawerObj);
	LGKS.circle(circle_center_x, circle_center_y, circle_rad, thingsToRender, drawerObj);

	// draw the label
	LGKS.set_textline_color(circle_labelcolor, thingsToRender, drawerObj);
	LGKS.set_text_align(TA_CENTER, TA_BOTTOM, thingsToRender, drawerObj);
	LGKS.text(circle_center_x, circle_center_y, circle_label, thingsToRender, drawerObj);
    } // drawStructure

} // class foobar

