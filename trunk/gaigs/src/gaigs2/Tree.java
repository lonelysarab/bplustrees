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
Purpose: This class provides support for the tree data structure, as well as many other
useful tools for its Animal implementation. It utilizes the "Sven Moen" algorithm to provide
minimum spacing in the actual drawing window. The algorithm works by going from node to node
constructing a contour (outline) surrounding the node. It then pushes the contours together until
their sides nearly meet; thus forming a tight and tidy data tree. The tree class also supports the
creation of new nodes, a color matching system to support Animal's limited color options, both a
horizontal and vertical layout, and a structure that allows for both binary (2 children only) and
general node systems. Otherwise, this class contains many of the same methods that one would expect
to see in a tree class.

Author: Ethan Dereszynski with "Layout" and relative helper methods by Sven Moen
Date: 7-22-02

*/

/*
Major editing done to this code to make it so that it generates a GAIGS snapshot instead of
an animal animation

Author: Ben Tidman
Date: 5-12-05
*/
package gaigs2;
// Import needed packages
import java.io.*;
import java.awt.Point;
import gaigs2.*;

public class Tree {

        // Initializes needed variables
        private TreeNode root;		//Retain root information
        private int vertSep = 1; 	//Vertical distance between each node on adjacent levels in surplus to the border
        private int startX = 5;	//Starting x location of root
        private int startY = 5; 	//Starting y location of root
        private int nodeHeight = 10;	//Default height of all nodes
        private int defaultBorder = 5;	//Default border area surrounding every node1
        private int nodeCount = 0;	//Count of all the nodes in this particular tree; also serves as the unique identifier
                                        //for each node and it's edge (line to parent)

        // The width of every node is based off the length of its text

        private boolean moveState = false; // Sets a continuous motion
        private boolean vertical = false; // Defines the structure of the tree (vertical or horizontal)

        // Constructor -- just builds a very basic tree with some default
        // data already entered.
        public Tree () {

                root = null;
        }

        // Constructor -- builds a General Tree with a set start position for the root
        public Tree (int x, int y) {

                startX = x;	// Set the starting x and y coordinates of the root
                startY = y;
                root = null;
        }

        public void setRoot(TreeNode r) { root = r; }	// Resets the root
        public void setVertSep(int v) { vertSep = v; }	// Sets the vertical separation
        public void setDefaultBorder(int h) { defaultBorder = h; }	// Sets the horizontal separation
        public void setStartX(int x) { 	// Sets the starting x coordinate
                if(vertical = true)
                        startY = x;
                else
                        startX = x;
        }
        public void setStartY(int y) { 	// Sets the starting y coordinate
                if(vertical = true)
                        startX = y;
                else
                        startY = y;
        }
        public void setNodeCount(int n) { nodeCount = n; }	// Let's the user set the node count
        public void setNodeHeight(int h) { nodeHeight = h; }	// Default height of all nodes in the tree can be set
        public void setMoveState(boolean m) { moveState = m; }	// Needed? Let's the user keep track of continuous motion

        public void setHorizontal() { 				// Set the tree to a horizontal layout
                if(vertical == true) {
                        vertical = false;
                        int temp = startX;			// Start coordinates must be swapped if it's a horizontal tree
                        startX = startY;
                        startY = temp;
                }
                else
                        vertical = false;
        }

        public void setVertical () { 				// Set the tree to a vertical layout
                if(vertical == false) {
                        vertical = true;
                        int temp = startX;			// Start coordinates must be swapped if it's a vertical tree
                        startX = startY;
                        startY = temp;
                }
                else
                        vertical = true;
        }

        public TreeNode getRoot() { return root; }	// Returns the root
        public int getVertSep() { return vertSep; }	// Returns the current vertical separation factor
        public int getDefaultBorder() { return defaultBorder; }	// Returns the current horizontal separation factor
        public int getStartX() { return startX; }	// Returns the current starting x coordinate
        public int getStartY() { return startY; }	// Returns the current starting y coordinate
        public int getNodeCount() { return nodeCount; }	// Returns the current amount of nodes in the tree
        public int getNodeHeight() { return nodeHeight; }	// Returns the current default height of all nodes
        public boolean getMoveState() { return moveState; }	// Returns the current move state of the tree
        public boolean isVertical() { return vertical; }	// Returns whether or not the tree is vertical

        // This first calls Sven Moens algorithm that provides offsets for each of the nodes (from the given start position)
        // and then calls the setCoord method to finalize each coordinate position for the nodes.
        public void setTree() {

                if(vertical == true) {
                        swapDimension(getRoot());
                        layoutHelper(getRoot());	// Call the Sven Moen algorithm
                        setCoord(getRoot(), startX, startY);	// Finalize the coordinate positions
                        swapDimension(getRoot());
                }
                else
                        layoutHelper(getRoot());	// Call the Sven Moen algorithm
                        setCoord(getRoot(), startX, startY);	// Finalize the coordinate positions

        }

        /* Lays out the tree node spacing in a typical tidy fashion */
        public void layoutHelper (TreeNode t) {

                TreeNode n;

                if(t == null)
                        return;

                n = t.getChild();

                while(n != null) {
                        layoutHelper(n);
                        n = n.getSibling();
                }

                if(t.getChild() != null)
                        attachParent(t, join(t));
                else
                        layoutLeaf(t);

        }

        /* Attaches the specified node to its children, setting offsets */
        private void attachParent(TreeNode t, int h) {

                int x, y1, y2;

                x = t.getBorder() + vertSep;
                y2 = (h - t.getHeight())/2 - t.getBorder();
                y1 = y2 + t.getHeight() + 2 * t.getBorder() - h;
                t.getChild().getOffset().x = x + t.getWidth();
                t.getChild().getOffset().y = y1;
                t.getContour().setUpperHead(new Polyline(t.getWidth(), 0, new Polyline(x, y1, t.getContour().getUpperHead())));
                t.getContour().setLowerHead(new Polyline(t.getWidth(), 0, new Polyline(x, y2, t.getContour().getLowerHead())));

        }

        /* Arranges contour for leaf node appropriately */
        private void layoutLeaf (TreeNode t) {

                t.getContour().setUpperTail(new Polyline(t.getWidth() + 2 * t.getBorder(), 0, null));
                t.getContour().setUpperHead(t.getContour().getUpperTail());
                t.getContour().setLowerTail(new Polyline(0, -(t.getHeight()) - 2 * t.getBorder(), null));
                t.getContour().setLowerHead(new Polyline(t.getWidth() + 2 * t.getBorder(), 0, t.getContour().getLowerTail()));

        }

        /* Joins children/siblings together, mergin contours */
        private int join (TreeNode t) {

                TreeNode c;
                int d, h, sum;

                c = t.getChild();
                t.setContour(c.getContour());
                sum = h = c.getHeight() + 2 * c.getBorder();
                c = c.getSibling();
                while(c != null) {
                        d = merge(t.getContour(), c.getContour());
                        c.getOffset().y = d + h;
                        c.getOffset().x = 0;
                        h = c.getHeight() + 2 * c.getBorder();
                        sum += d + h;
                        c = c.getSibling();
                }

                return sum;
        }

        /* Merges two polygons together. Returns total height of final polygon */
        private int merge(Polygon c1, Polygon c2) {

                int x, y, total, d;
                Polyline lower, upper, b;

                x = y = total = 0;
                upper = c1.getLowerHead();
                lower = c2.getUpperHead();

                while ((lower != null) && (upper != null)) {	// compute offset total 

                        d = offset(x, y, lower.getDX(), lower.getDY(), upper.getDX(), upper.getDY());
                        y += d;
                        total += d;

                        if (x + lower.getDX() <= upper.getDX()) {
                                y += lower.getDY();
                                x += lower.getDX();
                                lower = lower.getLink();
                        }

                        else {
                                y -= upper.getDY();
                                x -= upper.getDX();
                                upper = upper.getLink();
                        }

                }

                // Store result in c1 

                if (lower != null) {
                        b = bridge(c1.getUpperTail(), 0, 0, lower, x, y);
                        c1.setUpperTail((b.getLink() != null) ? c2.getUpperTail() : b);
                        c1.setLowerTail(c2.getLowerTail());
                }

                else { //upper 

                        b = bridge(c2.getLowerTail(),x,y,upper,0,0);
                        if (b.getLink() == null)
                                c1.setLowerTail(b);
                }

                c1.setLowerHead(c2.getLowerHead());

                return total;

        }

        /* Calculates the offset for specified points */
        private int offset (int p1, int p2, int a1, int a2, int b1, int b2) {

          int d, s, t;

          if (b1 <= p1 || p1 + a1 <= 0)
            return 0;

          t = b1 * a2 - a1 * b2;
          if (t > 0)
            if (p1 < 0) {
              s = p1*a2 ;
              d = s/a1 - p2;
            }
            else if (p1 > 0) {
              s = p1*b2;
              d = s/b1 - p2;
            }
            else
              d = -p2;
          else
          if ( b1 < p1 + a1) {
            s = (b1 - p1) * a2;
            d = b2 - (p2 + s/a1);
          }
          else if (b1 > p1 + a1) {
            s = (a1+p1) * b2;
            d = s/b1 - (p2 + a2);
          }
          else
            d = b2 - (p2+a2);

          if (d > 0)
            return d;
          else
            return 0;

        }

        /* Bridge */
        private Polyline bridge (Polyline line1, int x1, int y1, Polyline line2,
                                int x2, int y2) {

                int dy, dx, s;
                Polyline r;

                dx = x2 + line2.getDX() - x1;
                if (line2.getDX() == 0)
                        dy = line2.getDY();
                else {
                        s = dx * line2.getDY();
                        dy = s/line2.getDX();
                }

                r = new Polyline (dx, dy, line2.getLink());
                line1.setLink(new Polyline(0, y2 + line2.getDY() - dy - y1, r));
                return r;

        }

        /* Sets the absolute coordinates of the tree nodes */
        private void setCoord (TreeNode t, int off_x, int off_y) {

                TreeNode c, s;
                int cur_y;

                // Set the node's position (coordinate)
                t.getPosition().x = off_x + t.getOffset().x;
                t.getPosition().y = off_y + t.getOffset().y;

                /* Plant child node */
                c = t.getChild();
                if (c != null) {
                        setCoord(c, t.getPosition().x, t.getPosition().y);

                        /* Plant sibling nodes */
                        s = c.getSibling();
                        cur_y = t.getPosition().y + c.getOffset().y;
                        while (s != null) {
                                setCoord(s, t.getPosition().x + c.getOffset().x, cur_y);
                                cur_y = cur_y + s.getOffset().y;
                                s = s.getSibling();
                        }
                }


                // Swap the two values to create a Vertical tree if necessary
                if (vertical) {
                        int temp = t.getPosition().x;
                        t.getPosition().x = t.getPosition().y;
                        t.getPosition().y = temp;
                }

        }

        // Creates and edge (lineToParent) for the node given.
        // Each edge has a unique id number that is shared by the child from which it spawns
        public Edge makeEdge(TreeNode parent, TreeNode child) {

                return new Edge(parent, child, child.getID());

        }

        // Used to swap the dimensions in the case of spacing a vertical tree
        public void swapDimension(TreeNode t) {

                int temp = t.getWidth();
                t.setWidth(t.getHeight());
                t.setHeight(temp);

                if(t.getSibling() != null)
                        swapDimension(t.getSibling());
                if(t.getChild() != null)
                        swapDimension(t.getChild());
        }
}
