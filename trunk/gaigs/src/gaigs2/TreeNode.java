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

/*
Purpose: This node class was based off the one created by Sven Moen to fit into his algorithm, as it contains many
objects unique only to his algorithm (polygon, polyline, contours, etc.). However, it also extends the VisNode class
created by Jeff Lucas so that it might contain visual elements (color, highlighted status, etc.). The node
works both as a binary type, or a general type. In addition, each node has a number of methods that aid in drawing
it in Animal. These nodes work off a parent, child, sibling system, though, as aforementioned, they also have binary
support -- such as right child and left child identifiers.

Author: Sven Moen -- translated and adapted by Ethan Dereszynski
Date: 7 - 22 - 02

*/

/*
Major editing done to this code to make it so that it generates a GAIGS snapshot instead of
an animal animation

Author: Ben Tidman
Date: 5-12-05
*/

package gaigs2;
// Import needed packages
import java.awt.*;
import java.util.*;
import gaigs2.*;

public class TreeNode extends VisNode {

        private String value;	// The value stored within the node
	private String tag;
        private TreeNode parent, child, sibling;	// The parent, child, and siblings of the node
        private int width = 40, height = 20, border = 10;	// Width, height, and border of the node
        private Point pos, offset;	// Offset of the node from previous node, and positional coordinates
                                // of this node
        private Polygon contour;	// The contour surrounding the node
        private int id;			// The integer id of this node (unique value)
        private Point oldPos = new Point(0,0);	// Used to monitor shifts in positions
        private Edge lineToParent;	// The line from this node to its parent
        private int timer = 20;	// Used to track animation times (by the user)
        private int delay = 0; // Used to set delays by the user

        // Variables related to binary node status
        private boolean leftChild = false;	// Is it a left child, a right child, or just a place holder?
        private boolean rightChild = false;
        private boolean placeHolder = false;

        // Basic constructors
        public TreeNode () {
        }

        public TreeNode (String v) {

                value = v;	// Create a node with only a set value

        }

        public TreeNode (String v, int i) {

                value = v;	// Create a node with a set value
                id = i;		// and a set id

        }

        // Create a node with complete information
        public TreeNode (String v, TreeNode p, TreeNode c, TreeNode s, int w, int h, int b, int i) {

                value = v;
                parent = p;
                child = c;
                sibling = s;
                width = w;
                height = h;
                border = b;
                id = i;
                pos = new Point(0,0);
                oldPos = new Point(0,0);
                offset = new Point(0,0);
                contour = new Polygon();

        }

        // Create a node with complete information
        public TreeNode (String v, TreeNode p, TreeNode c, TreeNode s, int w, int h, int b, int i, String t) {

                value = v;
                parent = p;
                child = c;
                sibling = s;
                width = w;
                height = h;
                border = b;
                id = i;
                pos = new Point(0,0);
                oldPos = new Point(0,0);
                offset = new Point(0,0);
                contour = new Polygon();
		tag = t;
        }

        // A simple node constructor that contains the essential information (the rest can be provided by default)
        public TreeNode (String v, TreeNode p, TreeNode c, TreeNode s, int i) {

                value = v;
                parent = p;
                child = c;
                sibling = s;
                id = i;
                pos = new Point(0,0);
                offset = new Point(0,0);
                contour = new Polygon();

        }

        /* Access methods */
        public String getValue() { return value; }	// Returns the value stored within the node
        public String getTag() { return tag; }	// Returns the value stored within the node
        public TreeNode getParent() { return parent; }	// Returns the node's parent node
        public TreeNode getChild() { return child; }	// Returns the node's child node
        public TreeNode getSibling() { return sibling; }	// Returns the node's sibling node
        public int getWidth () { return width; }	// Returns the width dimension of the node
        public int getHeight () { return height; }	// Returns the height dimension of the node
        public int getBorder () { return border; }	// Returns the node's border (area between it and its contour)
        public Point getPosition () { return pos; }	// Returns the up-to-date coordinate location of the node
        public Point getOldPosition () { return oldPos; }	// Returns the older coordinate location of the node
        public Point getOffset () { return offset; }	// Returns the offset of the node from it's predecessor
        public Polygon getContour () { return contour; }	// Returns the contour of the node
        public int getID () { return id; }	// Returns the node's unique id (useful for tracking nodes and their parent lines)
        public Edge getLineToParent() { return lineToParent; }	// Return the node's edge, or it's lineToParent

        // Binary Node Commands
        public boolean isLeftChild() { return leftChild; }	// Is this node a left child?
        public boolean isRightChild() { return rightChild; }	// a right child?
        public boolean isPlaceHolder() { return placeHolder; }  // or just a place holder?

        public TreeNode getRightChild() { 			// returns the node's right child
                if (child != null && child.getSibling() != null && child.getSibling().isPlaceHolder() != true && child.getSibling().isRightChild() == true)
                        return child.getSibling();
                else
                        return null;
        }

        public TreeNode getLeftChild() { 			// returns the node's left child
                if (child != null && child.isPlaceHolder() != true && child.isLeftChild() == true)
                        return child;
                else
                        return null;
        }

        /* Set methods */
        public void setValue(String v) { value = v; }	// Set the node's value
        public void setParent(TreeNode p) { parent = p; }	// Set the node's parent
        public void setChild(TreeNode c) { child = c; }	// Set the node's child
        public void setSibling(TreeNode s) { sibling = s; }	// Set the node's sibling
        public void setWidth(int w) { width = w; }	// Set the width dimension of the node
        public void setHeight(int h) { height = h; }	// Set the height dimension of the node
        public void setBorder(int b) { border = b; }	// Set the node's border (area between it and its contour)
        public void setPosition(Point p) { pos = p; }	// Set the node's up-to-date coordinate location
        public void setOldPosition (Point p) { oldPos = p; } // Sets the older coordinate location of the node
        public void setOffset(Point o) { offset = o; }	// Sets the offset of the node from it's predecessor
        public void setContour(Polygon c) { contour = c; }	// Sets the contour (outline that considers the border) of the node
        public void setID (int i) { id = i; }	// Sets the ID of the node
        public void setLineToParent(Edge e) { lineToParent = e; }	// Sets the edge (lineToParent) of the node
     
        // Binary Node commands
        public void setLeftChild (boolean t) { leftChild = t; }		// Flags the node as a left Child
        public void setRightChild (boolean t) { rightChild = t; }	// Flags the node as a right Child
        public void setPlaceHolder (boolean t) { placeHolder = t; }	// Flags the node as a place holder


 
        // Allows for the insertion of a right child
        public void insertRightChild (TreeNode rc) {

                if (child == null) {	// This means a place holder will be needed to fill the child's spot
                        // Create a space holder with some default values
                        child = new TreeNode();
                        child.setWidth(rc.getWidth());
                        child.setHeight(rc.getHeight());
                        child.setBorder(rc.getBorder());
                        child.setValue("");
                        child.setPosition(new Point(0,0));
                        child.setOffset(new Point(0,0));
                        child.setContour(new Polygon());
                        child.setID(rc.getID());
                        child.setParent(this);
                        child.setChild(null);
                        child.setSiblingWithEdge(rc);
                        child.setPlaceHolder(true);
                        child.getSibling().setRightChild(true);	// Then set its sibling to be the right child
                }
                else {	// Otherwise, no place holder is needed and the node can simply be inserted
                        child.setSiblingWithEdge(rc);
                        child.getSibling().setRightChild(true);
                }

        }

        public void insertLeftChild (TreeNode lc) {

                if (child == null) {	// There is no previous node there, so just insert the new node in as the left child
                        setChildWithEdge(lc);
                        child.setLeftChild(true);
                }
                else {	// Otherwise, copy all the information from the left child into the already existing node
                        child.setWidth(lc.getWidth());
                        child.setHeight(lc.getHeight());
                        child.setBorder(lc.getBorder());
                        child.setValue(lc.getValue());
                        child.setPosition(lc.getPosition());
                        child.setOffset(lc.getOffset());
                        child.setContour(lc.getContour());
                        child.setID(lc.getID());
                        child.setLineToParent(new Edge(this, child, child.getID()));
                        child.setPlaceHolder(false);
                        child.setLeftChild(true);
                        lc.setParent(null);
                        lc.setSibling(null);
                        lc.setChild(null);
                }

		if(child.getSibling() == null)
		{
                        TreeNode temp = new TreeNode();
 			temp.setWidth(lc.getWidth());
                        temp.setHeight(lc.getHeight());
                        temp.setBorder(lc.getBorder());
                        temp.setValue("");
                        temp.setPosition(new Point(0,0));
                        temp.setOffset(new Point(0,0));
                        temp.setContour(new Polygon());
                        temp.setID(lc.getID());
                        temp.setParent(this);
                        temp.setChild(null);
                        temp.setPlaceHolder(true);
                        temp.setRightChild(true);	// Then set its sibling to be the right child
			child.setSibling(temp);
		}

        }

        // A convenient tool to set children and create their edge in one step
        public void setChildWithEdge(TreeNode c) {

                child = c;
                child.setLineToParent(new Edge(this, child, child.getID()));

        }

        // A convenient tool to set siblings and create their edges in one step
        public void setSiblingWithEdge(TreeNode c) {

                sibling = c;
                sibling.setLineToParent(new Edge(parent, sibling, sibling.getID()));

        }
}
