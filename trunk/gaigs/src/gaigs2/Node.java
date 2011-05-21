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

import java.awt.image.*;

import java.util.*; // NOTE: if you want a standard LinkedList, use java.util.LinkedList

import org.jdom.*;

public class Node extends StructureType {

    double x, y; // center coords
    double r;    // radius

    int color;   // node color

    int labelcolor; // text color
    String label; // only set up for a single line of text

    
    // This is the constructor that is called (unless you modify jhave2/gaigs/src/gaigs2/draw.java's
    //  assignStructureType(jdom.Element) method).
    public Node() {
	super(); // necessary

	color = White; // our hex notation is "#RRGGBB"
	               // otherwise, in xml: white,black,green,blue,yellow,red,white,light blue,magenta,yellow
	labelcolor = Black;
	label = null;

	x = 0.50;
	y = 0.50;
	r = 0.25;
    } // Node()	


    // This initialization method gets passed a jdom.Element whose name is eg "Node" or "MyNode"
    // gaigs_sho.dtd must be modified, adding a eg "Node" or "MyNode" element to
    //  the list of structure types a snap can contain.
    // If the xml element tag name is not the same as the class name, jhave2/gaigs/src/gaigs2/draw.java
    //  must be modified. The method assignStructureType(jdom.Element) must construct an object of this class.
    public void loadStructure(Element rootEl, LinkedList thingsToRender, draw drawerObj) {
	// this call loads the name and bounds if your xml structure-element has a name and/or bounds
	//  like the built-in structures.
	load_name_and_bounds(rootEl, thingsToRender, drawerObj);

	// used to find text width
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);


	////////////////////
	// MINI JDOM DEMO
	List children = rootEl.getChildren();
	Iterator iter = children.iterator();

	Element labelEl;

	while( iter.hasNext() ) {
	    Element child = (Element) iter.next();

	    if( child.getName().equals("name") ) {
		// already done in load_name_and_bounds(..)
		String junkName;
		junkName = child.getText();
	    }
	    else if( child.getName().equals("bounds") ) {
		// already done in load_name_and_bounds(..)
		double junkBound;
		junkBound = Format.atof( child.getAttributeValue("x1") );
	    }
	    else if( child.getName().equals("nodelabel") )
		labelEl = child;
	}

	// can get elements directly:
	labelEl = rootEl.getChild("nodelabel");
	
	// but a "name" element is optional, so it may give back "null".
	// this will give us "null" back since the element is not there:
	Element nullEl = rootEl.getChild("abcdefg");

	// the XML is validated against the DTD, so if there is a #REQUIRED attribute or adefault value
	//  we can safely assume it is there
	x = Format.atof( rootEl.getAttributeValue("x") );
	y = Format.atof( rootEl.getAttributeValue("y") );

	color = colorStringToInt( rootEl.getAttributeValue("color") );

	if( labelEl != null ) {
	    label = labelEl.getAttributeValue("text");
	    labelcolor = colorStringToTextColorInt( rootEl.getAttributeValue("color") );

	    // base the radius on the length of the label
	    int text_width_pixels = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( label );
	    double normalized_width = ((double) text_width_pixels / (double) GaigsAV.preferred_width);
	    r = (normalized_width + Textheight) / 2.0;
	}
    } // loadStructure(Element,LinkedList,draw)


    // Use the LGKS object to draw the structure
    public void drawStructure(LinkedList thingsToRender, draw drawerObj) {
	// draw the circle(filled)
	LGKS.set_fill_int_style(bsSolid, color, thingsToRender, drawerObj);
	LGKS.circle_fill(x, y, r, thingsToRender, drawerObj);

	// draw the circle outline
	LGKS.set_textline_color(Black, thingsToRender, drawerObj);
	LGKS.circle(x, y, r, thingsToRender, drawerObj);

	// draw the label
	LGKS.set_textline_color(labelcolor, thingsToRender, drawerObj);
	LGKS.set_text_align(TA_CENTER, TA_BOTTOM, thingsToRender, drawerObj);
	LGKS.text(x, y, label, thingsToRender, drawerObj);
    } // drawStructure()

} // class Node

