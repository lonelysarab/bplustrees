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

public class Queue extends LinearList {

    public Queue () {
	super();
    }
    
    
    public void calcDimsAndStartPts(LinkedList llist, draw d) {
	
	double ArrowLength,
	    NodeToArrowGap,
	    ArrowToNextNodeSep,
	    TitleToSSGap,
	    MinGt,MaxGt,Diam,
	    MinTitlex,MaxTitlex;
	int NumNodes,NumLines;

	super.calcDimsAndStartPts(llist,d);	
	Lenx=Maxstringlength+Textheight;
	if (linespernode==1) 
	    Leny=Textheight*1.15;
	else
	    Leny=(linespernode*Textheight)+
		((linespernode-1)*(0.15*Textheight)) + 
		(0.05*Textheight);
	TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
	TDy=TDx/Math.sqrt(3);
	NumNodes= nodelist.size();     //NumberOfNodes(snapsht);
	ArrowLength=ArrowPercentofLenx*Lenx;
	NodeToArrowGap=0.5*Textheight;
	TitleToSSGap=2*Titleheight;
	NumLines=title.size();
	snapheight=(NumLines*Titleheight)+
	    ((NumLines-1)*(0.5*Titleheight))+TitleToSSGap
	    +ArrowLength+NodeToArrowGap+(0.5*TDy)+
	    (NumNodes*Leny);
	snapwidth=Math.max(Maxtitlelength,Lenx+TDx);
	Startx=CenterScreen-(0.5*(Lenx+TDy));
	Starty=CenterScreen+(0.5*snapheight)-(NumLines*Titleheight)-
            ((NumLines-1)*
             (0.5*Titleheight))-TitleToSSGap-ArrowLength-(0.5*TDy);
	TitleStarty=Ycenter+(0.5*snapheight)-Titleheight;

    }
	
    void drawStructure (LinkedList llist, draw d)  {
 
        LinkedList NP;     

    	if (emptyStruct()) {
	    super.drawStructure(llist,d);  // to handle empty structure
	    return;
        }
	// Draw 3d top of stack and queue. *)
        drawTop(Startx,Starty,llist,d);
        drawArrow(Startx+(0.5*Lenx),Starty+(0.5*Textheight),
		  Up,llist,d);
	nodelist.reset();
	while (nodelist.hasMoreElements()) {
	    NP = (LinkedList) nodelist.nextElement();
            drawSide(Startx+Lenx,Starty,llist,d);
            drawRectNode(false,NP,llist,d);
	}
        drawArrow(Startx+(0.5*Lenx),Starty-(0.5*Textheight)-(ArrowPercentofLenx*Lenx),
		  Up,llist,d);
    }


}
