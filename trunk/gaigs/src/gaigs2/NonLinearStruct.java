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

public abstract class NonLinearStruct extends StructureType {

    // Trees, graphs, nets
    // Contains common methods such as drawCircularNode, drawConnector, others??

    // Used in aligning text labels on self-connecting edges:
    protected int HSelfConnectedNodeTextAlign;
    protected int VSelfConnectedNodeTextAlign;
    double xlab, ylab;
    protected double Lenx;
    protected double Leny;
    protected double Startx;
    protected double Starty;
    protected double TDx, TDy;
    protected static double TreeSideBorder = 0.05;


    public NonLinearStruct () {
    }

    //      GIVEN  : the  x and y coordinates of the two tree or graph nodes to connect,
    //               the radius of the nodes, whether or not to connect with an arrow, and the color of the edge.
    //      TASK   : Draw the line that connects their centers, but doesn't go inside either
    //               of the two nodes with connecting arrow, if necessary (Note that the arrow
    //               always points to the line at x2, y2.  Also note that if the points x1,y1 and
    //               x2, y2 are the same, this procedure draws a "self connecting" arc.            *)

	
	
    public void drawConnectingLine(double x1, double y1, double x2, double y2,double NodeRadius,
				   boolean WithArrow, char EdgeColor, LinkedList llist, draw d) {
	
	double xf,yf,af;
	int NC,TC;
	
	if( x1 != x2 && y1 != y2 ) {
	    NodeRadius = LGKS.minScale(NodeRadius);
	    NodeRadius = NodeRadius / LGKS.scaledVectorMagnitudeRatio(x1,y1,x2,y2);
	}
	
	if ((x1<x2) && (y1<=y2)) // 3rd quad *)
	    drawConnector(x1,y1,x2,y2, -1.0, -1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	else if ((x1<=x2) && (y1>y2)) // 2nd quad *)
	    drawConnector(x1,y1,x2,y2, -1.0,1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	else if ((x1>=x2) && (y1<y2)) // 4th quad *)
	    drawConnector(x1,y1,x2,y2,1.0, -1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	else if ((x1>x2) && (y1>=y2)) // 1st quad *)
	    drawConnector(x1,y1,x2,y2,1.0,1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	else                          // x1 = x2 AND y1 = y2 - DrawSelfConnector *)
	    drawSelfConnector(x1,y1, NodeRadius, WithArrow, EdgeColor, llist,d);
	
    }
    
    public void new_drawConnectingLine(double x1, double y1, double x2, double y2,double NodeRadius,
				       boolean WithArrow, String EdgeColor, LinkedList llist, draw d) {
	    
	    //System.out.println("Drawing connector w/ color " + EdgeColor);
	    
	    double xf,yf,af;
	    int NC,TC;

	    if( x1 != x2 && y1 != y2 ) {
		NodeRadius = LGKS.minScale(NodeRadius);
		NodeRadius = NodeRadius / LGKS.scaledVectorMagnitudeRatio(x1,y1,x2,y2);
	    }

	    if ((x1<x2) && (y1<=y2)) // 3rd quad *)
		new_drawConnector(x1,y1,x2,y2, -1.0, -1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	    else if ((x1<=x2) && (y1>y2)) // 2nd quad *)
		new_drawConnector(x1,y1,x2,y2, -1.0,1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	    else if ((x1>=x2) && (y1<y2)) // 4th quad *)
		new_drawConnector(x1,y1,x2,y2,1.0, -1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	    else if ((x1>x2) && (y1>=y2)) // 1st quad *)
		new_drawConnector(x1,y1,x2,y2,1.0,1.0, NodeRadius, WithArrow, EdgeColor, llist,d);
	    else                          // x1 = x2 AND y1 = y2 - DrawSelfConnector *)
		new_drawSelfConnector(x1,y1, NodeRadius, WithArrow, EdgeColor, llist,d);

	}

 
    // GIVEN  : The coordinates of the two nodes to connect to each other, and the factors indicating
    //        where the second node lies with respect to the first node.
    // TASK   : Appropriately connect the two nodes to each other.  *)
    public void drawConnector(double x1,double y1,double x2,double y2,double xfct,double yfct,
			      double NodeRadius, boolean WithArrow, char EdgeColor, LinkedList llist, draw d)  {	  

	/* Potentially needs to know
	   NodeRadius     :real;
	   WithArrow      :boolean;
	   EdgeColor      :char  */

	double xLine[], yLine[];
	double Angle,Adjustx, Adjusty;
	int NC, TC;

	xLine = new double [2];
	yLine = new double [2];
	if (x1==x2) { // Undefined slope *)
	    xLine[0]=x1;yLine[0]=y1-(yfct*NodeRadius);
	    xLine[1]=x2;yLine[1]=y2+(yfct*NodeRadius);
	}
	else   {                     // The slope is defined! *)
	    Angle=Math.abs(Math.atan((y2-y1)/(x2-x1))); // get angle of line with the horizontal  *)
	    Adjustx=Math.cos(Angle)*NodeRadius;
	    Adjusty=Math.sin(Angle)*NodeRadius;
	    xLine[0]=x1-(xfct*Adjustx);yLine[0]=y1-(yfct*Adjusty);
	    xLine[1]=x2+(xfct*Adjustx);yLine[1]=y2+(yfct*Adjusty);
	}
	NC = extractColor(EdgeColor);
	//TC = extractTextColorForHighlightedNodes(EdgeColor);
	LGKS.set_textline_color(NC,llist,d);
	LGKS.polyline(2,xLine,yLine,llist,d);
	LGKS.set_textline_color(Black,llist,d);
	if (WithArrow) // Draw in arrow *)
	    drawGraphNetArrow(x2,y2,xLine[1],yLine[1], EdgeColor, llist, d);

    }

    public void new_drawConnector(double x1,double y1,double x2,double y2,double xfct,double yfct,
				  double NodeRadius, boolean WithArrow, String EdgeColor, LinkedList llist, draw d)  {	  

	    /* Potentially needs to know
	       NodeRadius     :real;
	       WithArrow      :boolean;
	       EdgeColor      :char  */

	    double xLine[], yLine[];
	    double Angle,Adjustx, Adjusty;
	    int NC, TC;

	    xLine = new double [2];
	    yLine = new double [2];
	    if (x1==x2) { // Undefined slope *)
		xLine[0]=x1;yLine[0]=y1-(yfct*NodeRadius);
		xLine[1]=x2;yLine[1]=y2+(yfct*NodeRadius);
	    }
	    else   {                     // The slope is defined! *)
		Angle=Math.abs(Math.atan((y2-y1)/(x2-x1))); // get angle of line with the horizontal  *)
		Adjustx=Math.cos(Angle)*NodeRadius;
		Adjusty=Math.sin(Angle)*NodeRadius;
		xLine[0]=x1-(xfct*Adjustx);yLine[0]=y1-(yfct*Adjusty);
		xLine[1]=x2+(xfct*Adjustx);yLine[1]=y2+(yfct*Adjusty);
	    }
	    NC = colorStringToInt(EdgeColor);
	    //TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
	    LGKS.set_textline_color(NC,llist,d);
	    LGKS.polyline(2,xLine,yLine,llist,d);
	    LGKS.set_textline_color(Black,llist,d);
	    if (WithArrow) // Draw in arrow *)
		new_drawGraphNetArrow(x2,y2,xLine[1],yLine[1], EdgeColor, llist, d);

	}

    //  GIVEN  : The x- and y-coordinates of the node to be connected to itself.
    //   TASK   : Appropriately connect this node to itself using the GKS$GDP Draw Elliptical Arc.  *)
    //     { Reference -- see calculations on paper filed in Gaigs-PC folder }


    public void drawSelfConnector( double x, double y, double NodeRadius,
				   boolean WithArrow, char EdgeColor, LinkedList llist, draw d)  {

	/* Potentially needs to know
	   x1,y1,x2,y2    :real;
	   NodeRadius     :real;
	   WithArrow      :boolean;
	   EdgeColor      :char  */

	double FocDist =0.8;        // the distance between foci is 80% of the node radius *)
	double SepAngle =Math.PI/8.0;  // half the angle between the intersections of the ellipse with the circle. *)

	double DistCentToFoci,
	    CX,CY,
	    CosSA,SinSA,
	    PX,PY,
	    F1X,F1Y,DistPF1,
	    MajorAxis,MinorAxis,Theta,
	    StartAngle,EndAngle;
	int NC, TC;

	/* Initially we compute some values shared by all cases -- the lengths of major
	   and minor axes and the angle between the major axis and the point of intersection
	   between circular node and ellipse. */
	DistCentToFoci=FocDist*NodeRadius/2.0;
	CX=x+DistCentToFoci+NodeRadius;
	CY=y;
	CosSA=Math.cos(SepAngle);
	SinSA=Math.sin(SepAngle);
	PX=x+CosSA*NodeRadius;
	PY=y+SinSA*NodeRadius;
	F1X=x+NodeRadius;
	F1Y=y;
	DistPF1=Math.sqrt(Math.pow((PX-F1X),2)+Math.pow((PY-F1Y),2));
	MajorAxis=(DistPF1+Math.sqrt(Math.pow((DistPF1),2)+4.0*DistCentToFoci*
				     (1.0+DistCentToFoci-CosSA)*NodeRadius))/2.0;
	MinorAxis=Math.sqrt(Math.pow(MajorAxis,2)+Math.pow(DistCentToFoci,2));
	Theta=Math.atan(Math.abs((CY-PY)/(CX-PX)));
	// Now performs computations necessary for each of four cases }
	if ((y>=x) && (y>= -x+1))   { // we want ellispe directed upward }
	    CX=x;
	    CY=y+DistCentToFoci+NodeRadius;
	    StartAngle=1.5*Pi+Theta;
	    EndAngle=1.5*Pi-Theta;
	    xlab=x;
	    ylab=CY+MajorAxis;
	    HSelfConnectedNodeTextAlign=TA_CENTER;
	    VSelfConnectedNodeTextAlign=TA_BOTTOM;
	    NC = extractColor(EdgeColor);
	    TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	    LGKS.set_textline_color(NC,llist,d);
	    LGKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
	    LGKS.set_textline_color(Black,llist,d);
	    PX=x+CosSA*NodeRadius;
	    PY=y+SinSA*NodeRadius;
	}
	else if ((y<=x) && (y<= -x+1))  {  //we want ellipse directed downward }
	    CX=x;
	    CY=y-DistCentToFoci-NodeRadius;
	    StartAngle=0.5*Pi+Theta;
	    EndAngle=0.5*Pi-Theta;
	    xlab=x;
	    ylab=CY-MajorAxis;
	    HSelfConnectedNodeTextAlign=TA_CENTER;
	    VSelfConnectedNodeTextAlign=TA_TOP;
	    NC = extractColor(EdgeColor);
	    TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	    LGKS.set_textline_color(NC,llist,d);
	    LGKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
	    LGKS.set_textline_color(Black,llist,d);
	    PX=x+CosSA*NodeRadius;
	    PY=y-SinSA*NodeRadius;
	}
	else if ((y>=x) && (y<= -x+1)) {  // we want ellipse directed to left }
	    CX=x-DistCentToFoci-NodeRadius;
	    CY=y;
	    StartAngle=Theta;
	    EndAngle=2.0*Pi-Theta;
	    xlab=CX-MajorAxis;
	    ylab=y;
	    HSelfConnectedNodeTextAlign=TA_RIGHT;
	    VSelfConnectedNodeTextAlign=TA_CENTER;
	    NC = extractColor(EdgeColor);
	    TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	    LGKS.set_textline_color(NC,llist,d);
	    LGKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
	    LGKS.set_textline_color(Black,llist,d);
	    PX=x-CosSA*NodeRadius;
	    PY=y+SinSA*NodeRadius;
	}
	else                       {  // Must have ellipse directed to right }
	    CX=x+DistCentToFoci+NodeRadius;
	    CY=y;
	    StartAngle=Pi+Theta;
	    EndAngle=Pi-Theta;
	    xlab=CX+MajorAxis;
	    ylab=y;
	    HSelfConnectedNodeTextAlign=TA_LEFT;
	    VSelfConnectedNodeTextAlign=TA_BASELINE;
	    NC = extractColor(EdgeColor);
	    TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	    LGKS.set_textline_color(NC,llist,d);
	    LGKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
	    LGKS.set_textline_color(Black,llist,d);
	    PX=x+CosSA*NodeRadius;
	    PY=y+SinSA*NodeRadius;
	}
	if (WithArrow) 
	    drawGraphNetArrow(x,y,PX,PY, EdgeColor, llist,d);
    }




    public void new_drawSelfConnector( double x, double y, double NodeRadius,
				       boolean WithArrow, String EdgeColor, LinkedList llist, draw d)  {

	    /* Potentially needs to know
	       x1,y1,x2,y2    :real;
	       NodeRadius     :real;
	       WithArrow      :boolean;
	       EdgeColor      :char  */

	    //System.out.println("Draw self-connector " + x + "," + y + " w/ color " + EdgeColor);

	    double FocDist =0.8;        // the distance between foci is 80% of the node radius *)
	    double SepAngle =Math.PI/8.0;  // half the angle between the intersections of the ellipse with the circle. *)

	    double DistCentToFoci,
		CX,CY,
		CosSA,SinSA,
		PX,PY,
		F1X,F1Y,DistPF1,
		MajorAxis,MinorAxis,Theta,
		StartAngle,EndAngle;
	    int NC, TC;

	    /* Initially we compute some values shared by all cases -- the lengths of major
	       and minor axes and the angle between the major axis and the point of intersection
	       between circular node and ellipse. */
	    DistCentToFoci=FocDist*NodeRadius/2.0;
	    CX=x+DistCentToFoci+NodeRadius;
	    CY=y;
	    CosSA=Math.cos(SepAngle);
	    SinSA=Math.sin(SepAngle);
	    PX=x+CosSA*NodeRadius;
	    PY=y+SinSA*NodeRadius;
	    F1X=x+NodeRadius;
	    F1Y=y;
	    DistPF1=Math.sqrt(Math.pow((PX-F1X),2)+Math.pow((PY-F1Y),2));
	    MajorAxis=(DistPF1+Math.sqrt(Math.pow((DistPF1),2)+4.0*DistCentToFoci*
					 (1.0+DistCentToFoci-CosSA)*NodeRadius))/2.0;
	    MinorAxis=Math.sqrt(Math.pow(MajorAxis,2)+Math.pow(DistCentToFoci,2));
	    Theta=Math.atan(Math.abs((CY-PY)/(CX-PX)));

	    NC = colorStringToInt(EdgeColor);
	    TC = Black;
	    LGKS.set_textline_color(NC,llist,d);
	    //LGKS.ellipse(0.2, 0.2, 0.0, Pi, 0.2, 0.4, llist, d);
	    // Now performs computations necessary for each of four cases }
	    if ((y>=x) && (y>= -x+1))   { // we want ellispe directed upward }
		CX=x;
		CY=y+DistCentToFoci+NodeRadius;
		StartAngle=1.5*Pi+Theta;
		EndAngle=1.5*Pi-Theta;
		xlab=x;
		ylab=CY+MajorAxis;
		HSelfConnectedNodeTextAlign=TA_CENTER;
		VSelfConnectedNodeTextAlign=TA_BOTTOM;
		System.out.println("minor axis: " + MinorAxis + " major axis: " + MajorAxis);
		LGKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
		LGKS.set_textline_color(Black,llist,d);
		PX=x+CosSA*NodeRadius;
		PY=y+SinSA*NodeRadius;
	    }
	    else if ((y<=x) && (y<= -x+1))  {  //we want ellipse directed downward }
		CX=x;
		CY=y-DistCentToFoci-NodeRadius;
		StartAngle=0.5*Pi+Theta;
		EndAngle=0.5*Pi-Theta;
		xlab=x;
		ylab=CY-MajorAxis;
		HSelfConnectedNodeTextAlign=TA_CENTER;
		VSelfConnectedNodeTextAlign=TA_TOP;
		System.out.println("minor axis: " + MinorAxis + " major axis: " + MajorAxis);
		LGKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
		LGKS.set_textline_color(Black,llist,d);
		PX=x+CosSA*NodeRadius;
		PY=y-SinSA*NodeRadius;
	    }
	    else if ((y>=x) && (y<= -x+1)) {  // we want ellipse directed to left }
		CX=x-DistCentToFoci-NodeRadius;
		CY=y;
		StartAngle=Theta;
		EndAngle=2.0*Pi-Theta;
		xlab=CX-MajorAxis;
		ylab=y;
		HSelfConnectedNodeTextAlign=TA_RIGHT;
		VSelfConnectedNodeTextAlign=TA_CENTER;
		System.out.println("minor axis: " + MinorAxis + " major axis: " + MajorAxis);
		LGKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
		LGKS.set_textline_color(Black,llist,d);
		PX=x-CosSA*NodeRadius;
		PY=y+SinSA*NodeRadius;
	    }
	    else                       {  // Must have ellipse directed to right }
		CX=x+DistCentToFoci+NodeRadius;
		CY=y;
		StartAngle=Pi+Theta;
		EndAngle=Pi-Theta;
		xlab=CX+MajorAxis;
		ylab=y;
		HSelfConnectedNodeTextAlign=TA_LEFT;
		VSelfConnectedNodeTextAlign=TA_BASELINE;
		System.out.println("minor axis: " + MinorAxis + " major axis: " + MajorAxis);
		LGKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
		LGKS.set_textline_color(Black,llist,d);
		PX=x+CosSA*NodeRadius;
		PY=y+SinSA*NodeRadius;
	    }
	    if (WithArrow) 
		new_drawGraphNetArrow(x,y,PX,PY, EdgeColor, llist,d);
	}


    //  GIVEN : The coordinates of the node at whose center we want to point the arrow, and the coordinates at which the
    //         point of the arrow should be placed.
    //  TASK  : Appropriately draw the arrow so that it points at the center of the node. *)
    public void drawGraphNetArrow(double Ncentx,double Ncenty,double Arrx,double Arry,
				  char EdgeColor, LinkedList llist, draw d)  {

	/* Potentially needs to know
	   x1,y1,x2,y2    :real;
	   NodeRadius     :real;
	   WithArrow      :boolean;
	   EdgeColor      :char   <<-- I think just this */

	double Angle,
	    ArrLen,
	    PerpAngle,
	    ArrPerp,
	    ax,
	    ay,
	    xfact,yfact,afact;
	double xArr[] , yArr[];
	int NC, TC;

	xArr = new double [4];
	yArr = new double [4];
	xArr[0]=Arrx;              // The point of the arrow *)
	yArr[0]=Arry;
	xArr[3]=Arrx;
	yArr[3]=Arry;
	ArrLen=Lenx/3.0;
	ArrPerp=(ArrLen/2.0)*Math.sqrt(3);
	if ((Arrx>Ncentx) && (Arry>=Ncenty)) { 
	    xfact=1.0;yfact=1.0;afact=1.0;
	}
	else if ((Arrx>=Ncentx)  && (Arry<Ncenty)) { 
	    xfact=1.0;yfact= -1.0;afact= -1.0;
	}
	else if ((Arrx<=Ncentx) && (Arry>Ncenty)) {
	    xfact= -1.0;yfact=1.0;afact= -1.0;
	}
	else /*if ((Arrx<Ncentx) && (Arry<=Ncenty)) */ 
	    /* if above not needed?? Since xfact, yfact, afact
	       must be defined by time get to next statement */
	    {
		xfact= -1.0;yfact= -1.0;afact=1.0;
	    }
	if (Ncentx==Arrx)  { // Undefined slope *)
	    xArr[1]=Arrx+(0.5*ArrLen);yArr[1]=Arry+(yfact*ArrPerp);
	    xArr[2]=Arrx-(0.5*ArrLen);yArr[2]=yArr[1];
	}
	else if (Ncenty==Arry)  { //  (* horizontal arrow *)
	    xArr[1]=Arrx+(xfact*ArrPerp);yArr[1]=Arry+(0.5*ArrLen);
	    xArr[2]=xArr[1];yArr[2]=Arry-(0.5*ArrLen);
	}
	else   {                      // slope is defined and not 0 *)
	    Angle=Math.abs(Math.atan((Arry-Ncenty)/(Arrx-Ncentx)));
	    PerpAngle=Math.PI/2.0-Angle;
	    ax=Arrx+(xfact*(Math.cos(Angle)*ArrPerp));
	    ay=Arry+(yfact*(Math.sin(Angle)*ArrPerp));
	    xArr[1]=ax-(Math.cos(PerpAngle)*(0.5*ArrLen));
	    yArr[1]=ay+(afact*(Math.sin(PerpAngle)*(0.5*ArrLen)));
	    xArr[2]=ax+(Math.cos(PerpAngle)*(0.5*ArrLen));
	    yArr[2]=ay-(afact*(Math.sin(PerpAngle)*(0.5*ArrLen)));
	}
	NC = extractColor(EdgeColor);
	TC = extractTextColorForHighlightedNodes(EdgeColor);
	LGKS.set_fill_int_style(bsSolid,NC,llist,d);
	LGKS.fill_area(4,xArr,yArr,llist,d);
	LGKS.set_fill_int_style(bsClear,White,llist,d);
    }

    public void new_drawGraphNetArrow(double Ncentx,double Ncenty,double Arrx,double Arry,
				      String EdgeColor, LinkedList llist, draw d)  {

	    /* Potentially needs to know
	       x1,y1,x2,y2    :real;
	       NodeRadius     :real;
	       WithArrow      :boolean;
	       EdgeColor      :char   <<-- I think just this */

	    double Angle,
		ArrLen,
		PerpAngle,
		ArrPerp,
		ax,
		ay,
		xfact,yfact,afact;
	    double xArr[] , yArr[];
	    int NC, TC;

	    xArr = new double [4];
	    yArr = new double [4];
	    xArr[0]=Arrx;              // The point of the arrow *)
	    yArr[0]=Arry;
	    xArr[3]=Arrx;
	    yArr[3]=Arry;
	    ArrLen=Lenx/3.0;
	    ArrPerp=(ArrLen/2.0)*Math.sqrt(3);
	    if ((Arrx>Ncentx) && (Arry>=Ncenty)) { 
		xfact=1.0;yfact=1.0;afact=1.0;
	    }
	    else if ((Arrx>=Ncentx)  && (Arry<Ncenty)) { 
		xfact=1.0;yfact= -1.0;afact= -1.0;
	    }
	    else if ((Arrx<=Ncentx) && (Arry>Ncenty)) {
		xfact= -1.0;yfact=1.0;afact= -1.0;
	    }
	    else /*if ((Arrx<Ncentx) && (Arry<=Ncenty)) */ 
		/* if above not needed?? Since xfact, yfact, afact
		   must be defined by time get to next statement */
	    {
		xfact= -1.0;yfact= -1.0;afact=1.0;
	    }
	    if (Ncentx==Arrx)  { // Undefined slope *)
		xArr[1]=Arrx+(0.5*ArrLen);yArr[1]=Arry+(yfact*ArrPerp);
		xArr[2]=Arrx-(0.5*ArrLen);yArr[2]=yArr[1];
	    }
	    else if (Ncenty==Arry)  { //  (* horizontal arrow *)
		xArr[1]=Arrx+(xfact*ArrPerp);yArr[1]=Arry+(0.5*ArrLen);
		xArr[2]=xArr[1];yArr[2]=Arry-(0.5*ArrLen);
	    }
	    else   {                      // slope is defined and not 0 *)
		Angle=Math.abs(Math.atan((Arry-Ncenty)/(Arrx-Ncentx)));
		PerpAngle=Math.PI/2.0-Angle;
		ax=Arrx+(xfact*(Math.cos(Angle)*ArrPerp));
		ay=Arry+(yfact*(Math.sin(Angle)*ArrPerp));
		xArr[1]=ax-(Math.cos(PerpAngle)*(0.5*ArrLen));
		yArr[1]=ay+(afact*(Math.sin(PerpAngle)*(0.5*ArrLen)));
		xArr[2]=ax+(Math.cos(PerpAngle)*(0.5*ArrLen));
		yArr[2]=ay-(afact*(Math.sin(PerpAngle)*(0.5*ArrLen)));
	    }
	    NC = colorStringToInt(EdgeColor);
	    TC = Black;
	    LGKS.set_fill_int_style(bsSolid,NC,llist,d);
	    LGKS.fill_area(4,xArr,yArr,llist,d);
	    LGKS.set_fill_int_style(bsClear,White,llist,d);
	}


    //                         PROCEDURE DrawCircularNode               *)
    //                                                                  *)
    //    Given the  point at which the center of the node is to be     *)
    //  drawn, the radius of the node, and the                          *)
    //  linked list of lines of text comprising the current node, this  *)
    //  procedure properly  draws this node to the  open  workstation,  *)
    //  highlighting the node as needed.                                *)
    public void drawCircNode(double Centerx, double Centery,
			     double NodeRadius,
			     LinkedList TextList,
			     LinkedList llist, draw d)   {

	//final static int NumPoints =2;

	int NodeColor,TextColor, pos;
	double TextStartx,TextStartY;
	String TLP;

	TextList.reset();

	TLP = (String) TextList.nextElement();
	if (TLP.length() == 0) 
	    TLP = new String (" ");
	//LGKS.set_textline_color(Black,llist,d);  // Needed?
    
	if (TLP.length()>2) { // Potential Highlight Delimeter... *)
	    String TempString = new String (TLP);
	    if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) {
		NodeColor = new_extractColor(TempString.substring(1));
		TextColor = new_extractTextColorForHighlightedNodes(TempString.substring(1));
		LGKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
		LGKS.circle_fill(Centerx,Centery,NodeRadius,llist,d);
		LGKS.set_textline_color(Black,llist,d);
		LGKS.circle(Centerx,Centery,NodeRadius,llist,d);
		LGKS.set_textline_color(TextColor,llist,d);
		LGKS.set_fill_int_style(bsClear,NodeColor,llist,d);   // To insure
		//the text background is invisible }
		//  Now we must create a new string that removes  *)
		//  the compose character delimeter...            *)
		TLP = ( (TempString.charAt(1) != '#') ? TempString.substring(2) : TempString.substring(8) ) ;
		//		TLP = TempString.substring(2);
	    }
	    else {	  // Don't highlight node
		LGKS.set_textline_color(Black,llist,d);
		LGKS.circle(Centerx,Centery,NodeRadius,llist,d);
	    }
	}
	else {	// Don't highlight node
	    LGKS.set_textline_color(Black,llist,d);
	    LGKS.circle(Centerx,Centery,NodeRadius,llist,d);
	}
	// Write in the text. *)

	final double linespacing = 1.0*LGKS.minScale(Textheight); // mg - mult was 1.5. 0.8 seemed a bit too cramped (but no overlap)
	TextStartx=Centerx;
	TextStartY=Centery + ( 0.5* ((linespernode-1)*linespacing) ) ;

	LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
	// We already have the head node in the list
	LGKS.text(TextStartx,TextStartY,TLP,llist,d);
	// check for more
	while (TextList.hasMoreElements()) { 
	    TLP = (String) TextList.nextElement();
	    //  Ready textstarts for next line.  *)
	    TextStartY -= linespacing;
	    LGKS.text(TextStartx,TextStartY,TLP,llist,d);
	} //                        (*  WHILE  *)
	LGKS.set_textline_color(Black,llist,d);
	LGKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
	LGKS.set_fill_int_style(bsClear,White,llist,d);
    }


}

