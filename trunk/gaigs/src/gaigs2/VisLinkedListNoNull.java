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

public class VisLinkedListNoNull extends LinearList {

    public VisLinkedListNoNull () {
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
		((linespernode-1)*(0.15*Textheight));
	TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
	TDy=TDx/Math.sqrt(3);
	NumNodes= nodelist.size();     //NumberOfNodes(snapsht);
	ArrowLength=ArrowPercentofLenx*Lenx;
	NodeToArrowGap=0.5*Textheight;
	TitleToSSGap=2*Titleheight;
	NumLines=title.size();
	ArrowToNextNodeSep=0.5*Textheight;
	snapheight=(NumLines*Titleheight)+((NumLines-1)* (0.5*Titleheight))+
	    TitleToSSGap+TDy+Leny;
	snapwidth=Math.max(Maxtitlelength,
                           (NumNodes*(Lenx+TDx))+((NumNodes-1)*
                                                  ((ArrowLength-(0.5*TDx))+ArrowToNextNodeSep)));
	Startx=CenterScreen-(0.5*snapwidth);
	Starty=CenterScreen-(0.5*snapheight)+Leny;
	TitleStarty=Ycenter+(0.5*snapheight)-Titleheight;

    }
	
    //    This procedure  calculates and draws the 'slash' that sig-  *)
    //  nifies a NIL pointer, assuming that startx and starty are     *)
    //  positioned at the upper right hand corner of the last node    *)
    //  in the list                                                   *)
    private void drawSlash(LinkedList llist, draw d)  {

	double xslash[], yslash [];

	xslash = new double [2];
	yslash = new double [2];
	xslash[0]=Startx;
	xslash[1]=Startx+TDx;
	yslash[0]=Starty-Leny;
	yslash[1]=Starty+TDy;
	LGKS.polyline(2,xslash,yslash,llist,d);
    }

	
    void drawStructure (LinkedList llist, draw d)  {
 
        double NodeSep, NodetoArrowSep;
        LinkedList NP;     

    	if (emptyStruct()) {
	    super.drawStructure(llist,d);  // to handle empty structure
	    return;
        }
	// Draw 3d top of stack and queue. *)
        NodetoArrowSep=(0.5*TDx);
        NodeSep=NodetoArrowSep+(ArrowPercentofLenx*Lenx)+
	    (0.5*Textheight); // <- ArrowToNextNodeSep *)
	nodelist.reset();
	while (nodelist.hasMoreElements()) {
	    NP = (LinkedList) nodelist.nextElement();
            drawTop(Startx,Starty,llist,d);
            drawRectNode(true,NP, llist,d);
            drawSide(Startx,Starty,llist,d);
            if (nodelist.hasMoreElements()) 
		drawArrow(Startx+(0.5*TDx),Starty-(0.5*Leny),Right,llist,d);
	    //else
		//drawSlash(llist,d);
            Startx=Startx+NodeSep;
	}
    }

}
