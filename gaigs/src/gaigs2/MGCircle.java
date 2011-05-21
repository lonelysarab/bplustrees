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

// MGCircle.java
// test class, trying to draw 2 on a screen using scale / translate tools
// ... load from XML using Xerces DOM stuff (that's now Andrew's job.)
// Mike Gilmore

package gaigs2;
import java.io.*;
import java.awt.*;
import java.util.*;

import org.jdom.*;

class MGCircle extends StructureType {
    int colorcode;
    double radius;
    double x, y; // coords

    public MGCircle() {
	super();
	colorcode = Red;
	radius = 0.2;
	x = y = 0.5;
    }

//     public MGCircle(double x1, double y1, double x2, double y2) {
// 	super(x1,y1,x2,y2);
// 	colorcode = Blue;
// 	x = 0.5;
// 	y = 0.5;
// 	radius = 0.1;
//     }

//     public MGCircle(Double x1, Double y1, Double x2, Double y2) {
// 	super(x1.doubleValue(), y1.doubleValue(), x2.doubleValue(), y2.doubleValue());
// 	//DebugOutput.print("In MGCircle Doublex4 constructor:");
// 	//DebugOutput.print("\tx1 " + x1 + " y1 " + y1 + " x2 " + x2 + " y2 " + y2);
// 	colorcode = Green;
// 	x = 0.5;
// 	y = 0.5;
// 	radius = 0.1;
//     }

//     public MGCircle(double x1, double y1, double x2, double y2, int color) {
// 	super(x1,y1,x2,y2);
// 	colorcode = color;
// 	x = 0.5;
// 	y = 0.5;
// 	radius = 0.1;
//     }

//     public void calcDimsAndStartPts(LinkedList llist, draw d) {
// 	super.calcDimsAndStartPts(llist,d);
//     }

//     boolean emptyStruct() {
// 	return false;
//     }

    void loadStructure(StringTokenizer st, LinkedList llist, draw d) throws VisualizerLoadException {
	// nothing for now
	String junk = st.nextToken(); // read off the ***^***
    }

    public void loadStructure(Element my_root, LinkedList llist, draw d) throws VisualizerLoadException {
	load_name_and_bounds(my_root, llist, d);
	//calcDimsAndStartPts(llist, d);
    }

    void drawStructure(LinkedList llist, draw d) {
	drawTitle(llist, d);
	super.drawStructure(llist, d);
	LGKS.set_fill_int_style(bsSolid, colorcode, llist, d);
	LGKS.circle_fill(x, y, radius, llist, d);
	LGKS.set_textline_color(Black, llist, d); // necessary for the outline? yes.
	LGKS.circle(x, y, radius, llist, d);
    }
}
