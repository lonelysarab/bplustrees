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
The Polyline is another data type utilized by the Sven Moen algorithm. They are
used to create a Polygon outline around each node in the tree. Polylines consist
of a starting x and y location, followed by the head of another Polyline to
which they are linked.

Author: Sven Moen; adapted by Ethan Dereszynski
Date: 7-22-02
*/

package gaigs2;
import gaigs2.*;

public class Polyline {

        private int dx, dy;	// Starting x and y location
        private Polyline link = null;	// Link to the next Polyline at the foot of this.

        // Possible constructors
        public Polyline () {

        }

        public Polyline (int x, int y, Polyline l) {

                // variable assignments
                dx = x;
                dy = y;
                link = l;

        }

        public Polyline (int x, int y ) {

                // variable assignments
                dx = x;
                dy = y;

        }

        // Access and retrieval methods
        public void setDX(int x) { dx = x;}
        public void setDY(int y) { dy = y;}
        public void setLink(Polyline l) { link = l;}

        public int getDX() { return dx; }
        public int getDY() { return dy; }
        public Polyline getLink() { return link; }


}
