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
This class extends the VisEdge class to work for general
nodes in a general tree. The edge links together two
general nodes, and the VisEdge class allows the user
to make numerous graphical changes to the edge itself.

Author: Ethan Dereszynski
Date: 7 - 22 - 02
*/

package gaigs2;
import gaigs2.*;

public class Edge extends VisEdge {

        // The starting and ending Node of the edge
        private TreeNode start;
        private TreeNode end;

        private String color;	// The color of the edge

        private int id;	// The unique ID of the edge
        private int sx,  sy, ex, ey; // The starting and ending coordinates

        public Edge (TreeNode s, TreeNode e) {

                start = s;	// Set the starting node
                end = e;	// set the ending node

        }

        public Edge (TreeNode s, TreeNode e, int i) {

                start = s;
                end = e;
                id = i;
		ex = s.getPosition().x + (s.getWidth()/2)+1;
		ey = s.getPosition().y + s.getHeight()- 5;
		sx = e.getPosition().x + (e.getWidth()/2)+1;
		sy = e.getPosition().y-1;
        }

        // Empty constructor
        public Edge () { }

        // Access Methods
        public void setStart(TreeNode s) { start = s; }
        public void setEnd(TreeNode e) { end = e; }
        public void setStartX(int x) { sx = x; }
        public void setEndX(int x) { ex = x; }
        public void setStartY(int y) { sy = y; }
        public void setEndY(int y) { ey = y; }
        public void setID (int i) { id = i; }

        // Set Methods
        public TreeNode getStart() { return start; }	// Returns the start node
        public TreeNode getEnd() { return end; }	// Returns the end node
        public int getStartX() { return sx; }	// Returns the start X-Coordinate
        public int getStartY() { return sy; }	// Returns the start Y-Coordinate
        public int getEndX() { return ex; }	// Returns the end X-Coordinate
        public int getEndY() { return ey; }	// Returns the end Y-Coordinate
        public int getID() { return id; }	// Returns the unique ID
}
