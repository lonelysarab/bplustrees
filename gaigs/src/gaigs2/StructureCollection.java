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

// StructureCollection
//  keeps track of 1 or more structures to be drawn to a screenshot.
// - Mike Gilmore
// - June 17 2005


// Functions that should be OR'd:
//  from StructureType:
//     boolean emptyStruct() (if relevant)
//     loadStructure(...)
//     drawStructure(..)*
//     calcDimsAndStartPts(..)** (necessary? i dont think so.. optional initialization)
//  if your structure has other LocalizedStructures within it:
//     update_children()
// notes-
// *: should call super.drawStructure if emptyStruct: nothing other than title to draw
// **: should call super's function

package gaigs2;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import java.awt.image.*;

import org.jdom.*;

class StructureCollection extends StructureType {

    // PROTECTED

    protected java.util.LinkedList children;

//     protected void update_children() {
// 	for(int i = 0; i < children.size(); i++)
// 	    update_child(children.get(i));
//     }

//     protected void update_child(StructureType child) {
	
//     }

    // CONSTRUCTORS


    // draw bounds = the whole drawing area
    public StructureCollection () {
	super();
	children = new java.util.LinkedList();
    }


    // PUBLIC

    // TODO -?- reads XML tree, rooted from it's own node
    //  OR  -?- the root XML-tree processor adds children to this externally.. need an addChild function. <--
//     public void loadStructure(Element snap, Linkedlist llist, draw d) {
// 	// load my stuff
//     }

    public void loadStructure(StringTokenizer st, LinkedList llist, draw d) {
	// this ST function, not useful to this class
    }

    public void addChild(StructureType child) {
	children.add(child);
    }

    // MAKE SURE this is called, when transitioned to XML. and on its children.
    //
    // Establish the protected variables that determine starting point,
    // length, and height of a node.  Also the starting vertical coordinate of
    // the title (and its ending vertical coordinate).  
    // When node-oriented structures override this, they will
    // need to establish all of these variables.  When a non-node-oriented
    // structure overrides it, only the starting and ending points of the title need
    // to be determined.  HOWEVER, in the latter case,
    // the super class's calcDimsAndStartPts should
    // probably be called to insure reliable settings for all variables.
    public void calcDimsAndStartPts(LinkedList llist, draw d) {
	super.calcDimsAndStartPts(llist,d);
	Iterator i = children.iterator();
	while(i.hasNext()) {
	    StructureType child = (StructureType) i.next();
	    child.calcDimsAndStartPts(llist,d);
	    // anything else ? change min/max bounds maybe?
	}
    }

    // All overriding drawStructures should call super on this method when they are
    // empty;
    void drawStructure (LinkedList llist, draw d){
	super.drawStructure(llist,d);

	Iterator i = children.iterator();
	while(i.hasNext()) {
	    StructureType child = (StructureType) i.next();
	    child.drawTitle(llist,d);
	    child.drawStructure(llist,d);
	}
// 	for(int i = 0; i < children.size(); i++)
// 	    ( (StructureType) (children.get(i)) ).drawStructure(llist,d);
    }

    // OR this if need be
    boolean emptyStruct() {
	return children.isEmpty();
    }

}
