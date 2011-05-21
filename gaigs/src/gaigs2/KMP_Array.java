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
import java.io.*;
import java.awt.*;
import java.util.*;

public class KMP_Array extends MD_Array {

	public KMP_Array(){
	  super();
	}
	void drawStructure (LinkedList llist, draw d)  {
 
		ArrayNode an;
		int  rows, cols;
        double SRx,SRy,PermStarty, PermStartx;
		String testStr;
		boolean drawIt = false;
		int colForFirstSubChar = -1;
		int colForLastSubChar = -1;

    	if (emptyStruct()) {
			super.drawStructure(llist,d);  // to handle empty structure
			return;
        }
        //  Must initialize row and column label counters.  *)
        SRx=Startx-(0.5*Textheight);
        SRy=Starty  /*-(0.5*Leny)*/;
		nodelist.reset();
		an = (ArrayNode) nodelist.nextElement();
        PermStarty=Starty;
		PermStartx = Startx;
        for (cols = 1; cols <= Numcols; cols++) { 
          GKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
          if (Numcols>1)          //  We can draw column labels...  *)
            GKS.text(Startx+(CenterScreen*Lenx),Starty+TDy
                   +(0.5*Textheight),an.col,llist,d);
          for (rows = 1; rows <= Numrows; rows++) {
            if (cols==1)  { // Must draw Row labels *)
                GKS.set_text_align(TA_RIGHT,TA_TOP,llist,d);
                GKS.text(SRx,SRy,an.row,llist,d);
                SRy=SRy-Leny;
			}
			// Only draw the rectangular node when the text isn't a period.
			// Keep track of which is the first node to not have a period, so 
			// we can draw an index below it after we're done with the last row
            GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
			if (rows == 2) {	  // 2 is row containing the substring, with alignment array
							  // below that
				an.textInNode.reset();
				testStr = (String) an.textInNode.nextElement();
				if (testStr.compareTo(".") == 0)  { // Would something other than a 
										// period be a good flagging character?
					if (drawIt && colForLastSubChar == -1)
					  colForLastSubChar = cols-1;  // This count based on starting at 1
									// Don't draw this column since it has a -1.
									// The previous column is the last one to draw
					drawIt = false;
				}
				else  {
					drawIt = true;
					if (colForFirstSubChar == -1)
					  colForFirstSubChar = cols;  // Note this col. count starts at 1
				}
			}
            an.textInNode.reset();
            if (rows == 1) 	 // Draw every node for the master string
				drawRectNode(false, an.textInNode,llist,d);
			else if (drawIt)  // in row 2 or 3 -- the substring or the align array
				drawRectNode(false, an.textInNode,llist,d);
			if (nodelist.hasMoreElements())
				an = (ArrayNode) nodelist.nextElement();
		  }
        Startx=Startx+Lenx;
        Starty=PermStarty;
		}
		// After the last row is drawn, add an index for the substring & align array. 
		// Leny was from Starty for each line of text.  SRy should give the appropriate
		// y coordinate for the row of indices
		Startx = PermStartx;
		SRy = SRy - 1.5 * Leny;
        GKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
        for (cols = colForFirstSubChar; cols <= colForLastSubChar; cols++) { 
          GKS.text(Startx+((cols-1)*Lenx)+(CenterScreen*Lenx),SRy+TDy
                   +(0.5*Textheight),Integer.toString(cols-1).trim(),llist,d);
		}
        GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
	}

}

