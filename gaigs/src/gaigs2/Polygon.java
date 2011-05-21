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
This is a new data type that is used to retain the polygon (consisting
of Polylines) surroundingeach of the nodes. The polygon is used to retain a
tidy shape for the tree,and are forced as close together as possible without
touching in a tidy tree.

Author: Sven Moen; adapted by Ethan Dereszynski
Date: 7-22-02
*/

package gaigs2;
import gaigs2.*;

public class Polygon {

        // Polylines that makes up the Polygon
        private Polyline lowerHead, lowerTail;
        private Polyline upperHead, upperTail;


        // Various access and retrieval methods
        public void setLowerHead(Polyline lh) { lowerHead = lh;}
        public void setLowerTail(Polyline lt) { lowerTail = lt;}
        public void setUpperHead(Polyline uh) { upperHead = uh;}
        public void setUpperTail(Polyline ut) { upperTail = ut;}

        public Polyline getLowerHead() { return lowerHead; }
        public Polyline getLowerTail() { return lowerTail; }
        public Polyline getUpperHead() { return upperHead; }
        public Polyline getUpperTail() { return upperTail; }

}
