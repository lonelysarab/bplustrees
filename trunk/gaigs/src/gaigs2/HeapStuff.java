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
Author: Ben Tidman
Date:  6-13-2005
This class was originally created to allow the heap structure access to several methods used for drawing things in the animation window.  recursiveTree now also uses several methods from this class so its name is less appropriate.  In general this class contains several methods that can be used to draw different shapes in the animation window.
*/

package gaigs2;
import java.io.*;
import java.awt.*;
import java.util.*;

public abstract class HeapStuff extends StructureType
{

 protected double Lenx;		//Side length for x
 protected double Leny;		//Side length for y
 protected double Startx;		//Starting x cord for shape
 protected double Starty;		//Starting y cord for shape
 protected double TDx;		//Used for 3D effect, defines diagonal for top and side rec
 protected double TDy;		//Used for 3D effect, defines diagonal for top and side rec		


 public HeapStuff() 
 {
	super();
 }

 public void calcDimsAndStartPts(LinkedList llist, draw d) 
 {
	super.calcDimsAndStartPts(llist,d);
 }

 boolean emptyStruct() 
 {
	return(false);
 }

 //Array stuff

 //  This  procedure, given  the x and y coords of left hand corner      *)
 //  of the node whose top needs to be drawn, draws the 3D top of the    *)
 //  box.    
 protected void  drawTop( double x, double y, LinkedList llist, draw d) 
 {
	double TDTx[], TDTy[];
	
	TDTx = new double [5];
	TDTy = new double [5];
	// Three dimenstional top x-coordinates  *)
	TDTx[0]=x;
	TDTx[1]=x+TDx;
	TDTx[2]=x+Lenx+TDx;
	TDTx[3]=x+Lenx;
	TDTx[4]=x;
	//  Three dimenstional top y-coordinates  *)
	TDTy[0]=y;
	TDTy[1]=y+TDy;
	TDTy[2]=y+TDy;
	TDTy[3]=y;
	TDTy[4]=y;
	GKS.set_fill_int_style(bsDiagCross,LightGray,llist, d);
	GKS.fill_area(5,TDTx,TDTy,llist, d);
	GKS.polyline(5,TDTx,TDTy,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist, d);
 }

 //  This  procedure, given  the x and y coords of left hand corner      *)
 //  of the node whose top needs to be drawn, draws the 3D top of the    *)
 //  box.    
 protected void  drawTop( double x, double y, double width, LinkedList llist, draw d) 
 {
	double TDTx[], TDTy[];
	
	TDTx = new double [5];
	TDTy = new double [5];
	// Three dimenstional top x-coordinates  *)
	TDTx[0]=x;
	TDTx[1]=x+TDx;
	TDTx[2]=x+width+TDx;
	TDTx[3]=x+width;
	TDTx[4]=x;
	//  Three dimenstional top y-coordinates  *)
	TDTy[0]=y;
	TDTy[1]=y+TDy;
	TDTy[2]=y+TDy;
	TDTy[3]=y;
	TDTy[4]=y;
	GKS.set_fill_int_style(bsDiagCross,LightGray,llist, d);
	GKS.fill_area(5,TDTx,TDTy,llist, d);
	GKS.polyline(5,TDTx,TDTy,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist, d);
 }
 


 //     Given the x and y point that marks the upper right hand  corner  *)
 //  of the square  node, and  the  node's  dimensions, this  procedure  *)
 //  draws the three-dimensional side portion of the rectangular  node.  *)
 protected void drawSide(double  StartX, double StartY, LinkedList llist, draw d)  
 {
	double TDSx[], TDSy[];

	TDSx = new double [5];
	TDSy = new double [5];
	//  Make the array to be used in the  Polylines of  *)
	//  the 3D side portion of box.                     *)          
	TDSx[0]=StartX;                    
	TDSx[1]=StartX+TDx; 
	TDSx[2]=StartX+TDx; 
	TDSx[3]=StartX;                       
	TDSx[4]=StartX;
	TDSy[0]=StartY;
	TDSy[1]=StartY+TDy;
	TDSy[2]=StartY-Leny+TDy;
	TDSy[3]=StartY-Leny;
	TDSy[4]=StartY;
	GKS.set_fill_int_style(bsDiagCross,LightGray,llist, d);
	GKS.fill_area(5,TDSx,TDSy,llist, d);
	GKS.polyline(5,TDSx,TDSy,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist, d);
 }


 //  Given the postion to start the text of this node, and the       *)
 //  pointer to the linked list of lines of text comprising the      *)
 //  current node, this procedure properly draws the node            *)
 //  (with text).                                                    *)
 protected void drawRectNode(LinkedList TextList, LinkedList llist, draw d)  
 {
	String TLP;
	int pos,NodeColor, TextColor;
	double Nodex[],Nodey[]; 
	double Textx,Texty;

	Nodex = new double [5];
	Nodey = new double [5];

	TextList.reset();
	TLP = (String) TextList.nextElement();
	GKS.set_textline_color(Black,llist,d);
	Nodex[0]=Startx;
	Nodey[0]=Starty;
	Nodex[1]=Startx+Lenx;
	Nodey[1]=Starty;
	Nodex[2]=Startx+Lenx;
	Nodey[2]=Starty-Leny;
	Nodex[3]=Startx;
	Nodey[3]=Starty-Leny;
	Nodex[4]=Nodex[0];
	Nodey[4]=Nodey[0];
	if (TLP.length() == 0) 
		TLP = new String (" ");
	if (TLP.length()>2)	// Potential Highlight Delimeter... *)
	{ 
		String TempString = new String (TLP);
		if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) 
		{ 
			NodeColor = extractColor(TempString.charAt(1));
			TextColor = extractTextColorForHighlightedNodes(TempString.charAt(1));
			GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
			GKS.set_textline_color(TextColor,llist,d);
			GKS.fill_area(5,Nodex,Nodey,llist,d);
			//  Now we must create a new string that removes  *)
			//  the compose character delimeter...            *)
			TLP = TempString.substring(2);
		}
	}
	Textx=Startx+(0.5*Textheight);
	Texty=Starty-0.002;
	//  We have first line in TextList  *)
	GKS.set_text_align(TA_CENTER,TA_TOP,llist,d);
	// We already have the head node in the list
	GKS.text(Startx+(Lenx/2),Texty,TLP,llist,d);
	// check for more
	while (TextList.hasMoreElements()) 
	{ 
		TLP = (String) TextList.nextElement();
		//  Ready textstarts for next line.  *)
		Texty=Texty-(Textheight)-0.002;
		GKS.text(Startx+(Lenx/2),Texty,TLP,llist,d);
	} //                        (*  WHILE  *)
	//  Set Text Color Back to the default...  *)
	GKS.set_textline_color(Black,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
	GKS.polyline(5,Nodex,Nodey,llist,d);
	
	Starty=Starty-Leny;
 }


 //  Given the postion to start the text of this node, and the       *)
 //  pointer to the linked list of lines of text comprising the      *)
 //  current node, this procedure properly draws the node            *)
 //  (with text).                                                    *)
 //   Special delimiters used in drawing array values.
 //   ! = do not draw node
 //   \S = gray out this node
 //   \A = Draw an arrow under node
 //   \Ttext: = Draws any text under node
 //   `~ = draw an empty node
 protected void drawRectNode(String TLP, double xmid, double ymid, LinkedList llist, draw d)  
 {
	int pos,NodeColor, TextColor;
	double Nodex[],Nodey[]; 
	double Textx,Texty;

	Nodex = new double [5];
	Nodey = new double [5];

	GKS.set_textline_color(Black,llist,d);
	Nodex[0]=xmid;
	Nodey[0]=ymid;
	Nodex[1]=xmid+Lenx;
	Nodey[1]=ymid;
	Nodex[2]=xmid+Lenx;
	Nodey[2]=ymid-Leny;
	Nodex[3]=xmid;
	Nodey[3]=ymid-Leny;
	Nodex[4]=Nodex[0];
	Nodey[4]=Nodey[0];
	if (TLP.length() == 0) 
		TLP = new String (" ");
	if (TLP.charAt(0)==Delim && TLP.charAt(1) == 'A') 
		TLP = TLP.substring(2);
	if (TLP.charAt(0)==Delim && TLP.charAt(1) == 'T') 
		TLP = TLP.substring(TLP.indexOf(":") + 1);
	if (TLP.length()>2)	// Potential Highlight Delimeter... *)
	{ 
		String TempString = new String (TLP);
		if(TempString.substring(0, 2).compareTo("\\S") == 0)
		{
			NodeColor = LightGray;
			TLP = "";
			GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
			GKS.fill_area(5,Nodex,Nodey,llist,d);
		}
		if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) 
		{
			NodeColor = extractColor(TempString.charAt(1));
			TextColor = extractTextColorForHighlightedNodes(TempString.charAt(1));
			GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
			GKS.set_textline_color(TextColor,llist,d);
			GKS.fill_area(5,Nodex,Nodey,llist,d);
			//  Now we must create a new string that removes  *)
			//  the compose character delimeter...            *)
			TLP = TempString.substring(2);
			if (TLP.compareTo("`~") == 0) 
				TLP = new String ("");
		}
	}
	Textx=xmid+(0.5*Textheight);
	Texty=ymid-0.002;
	//  We have first line in TextList  *)
	GKS.set_text_align(TA_CENTER,TA_TOP,llist,d);
	// We already have the head node in the list
	GKS.text(xmid+(Lenx/2) - .005,Texty,TLP,llist,d);
	GKS.set_textline_color(Black,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
	GKS.polyline(5,Nodex,Nodey,llist,d);
	
	ymid=ymid-Leny;
 }

//  Given the postion to start the text of this node, and the       *)
 //  pointer to the linked list of lines of text comprising the      *)
 //  current node, this procedure properly draws the node            *)
 //  (with text).                                                    *)
 //   Special delimiters used in drawing array values.
 //   ! = do not draw node
 //   \S = gray out this node
 //   \A = Draw an arrow under node
 //   \Ttext: = Draws any text under node
 //   `~ = draw an empty node
 protected void drawRectNode(String TLP, double xmid, double ymid, double width, LinkedList llist, draw d)  
 {
	int pos,NodeColor, TextColor;
	double Nodex[],Nodey[]; 
	double Textx,Texty;

	Nodex = new double [5];
	Nodey = new double [5];

	GKS.set_textline_color(Black,llist,d);
	Nodex[0]=xmid;
	Nodey[0]=ymid;
	Nodex[1]=xmid+width;
	Nodey[1]=ymid;
	Nodex[2]=xmid+width;
	Nodey[2]=ymid-Leny;
	Nodex[3]=xmid;
	Nodey[3]=ymid-Leny;
	Nodex[4]=Nodex[0];
	Nodey[4]=Nodey[0];
	if (TLP.length() == 0) 
		TLP = new String (" ");
	if (TLP.charAt(0)==Delim && TLP.charAt(1) == 'A') 
		TLP = TLP.substring(2);
	if (TLP.charAt(0)==Delim && TLP.charAt(1) == 'T') 
		TLP = TLP.substring(TLP.indexOf(":") + 1);
	if (TLP.length()>2)	// Potential Highlight Delimeter... *)
	{ 
		String TempString = new String (TLP);
		if(TempString.substring(0, 2).compareTo("\\S") == 0)
		{
			NodeColor = LightGray;
			TLP = "";
			GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
			GKS.fill_area(5,Nodex,Nodey,llist,d);
		}
		if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) 
		{
			NodeColor = extractColor(TempString.charAt(1));
			TextColor = extractTextColorForHighlightedNodes(TempString.charAt(1));
			GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
			GKS.set_textline_color(TextColor,llist,d);
			GKS.fill_area(5,Nodex,Nodey,llist,d);
			//  Now we must create a new string that removes  *)
			//  the compose character delimeter...            *)
			TLP = TempString.substring(2);
			if (TLP.compareTo("`~") == 0) 
				TLP = new String ("");
		}
	}
	Textx=xmid+(0.5*Textheight);
	Texty=ymid-0.002;
	//  We have first line in TextList  *)
	GKS.set_text_align(TA_CENTER,TA_TOP,llist,d);
	// We already have the head node in the list
	GKS.text((xmid+(width/2)) - .005, Texty,TLP,llist,d);
	GKS.set_textline_color(Black,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
	GKS.polyline(5,Nodex,Nodey,llist,d);
	
	ymid=ymid-Leny;
 }


 //end array stuff

 //start tree stuff

 //      GIVEN  : the  x and y coordinates of the two tree or graph nodes to connect,
 //               the radius of the nodes, whether or not to connect with an arrow, and the color of the edge.
 //      TASK   : Draw the line that connects their centers, but doesn't go inside either
 //               of the two nodes with connecting arrow, if necessary (Note that the arrow
 //               always points to the line at x2, y2.  Also note that if the points x1,y1 and
 //               x2, y2 are the same, this procedure draws a "self connecting" arc.            *)

 public void drawConnectingLine(double x1, double y1, double x2, double y2, double NodeRadius, char EdgeColor, LinkedList llist, draw d) 
 {
	double xf,yf,af;
	int NC,TC;

	if ((x1<x2) && (y1<=y2)) // 3rd quad *)
		drawConnector(x1,y1,x2,y2, -1.0, -1.0, NodeRadius, EdgeColor, llist,d);
	else if ((x1<=x2) && (y1>y2)) // 2nd quad *)
		drawConnector(x1,y1,x2,y2, -1.0,1.0, NodeRadius, EdgeColor, llist,d);
	else if ((x1>=x2) && (y1<y2)) // 4th quad *)
		drawConnector(x1,y1,x2,y2,1.0, -1.0, NodeRadius, EdgeColor, llist,d);
	else if ((x1>x2) && (y1>=y2)) // 1st quad *)
		drawConnector(x1,y1,x2,y2,1.0,1.0, NodeRadius, EdgeColor, llist,d);
 }


 // GIVEN  : The coordinates of the two nodes to connect to each other, and the factors indicating
 //        where the second node lies with respect to the first node.
 // TASK   : Appropriately connect the two nodes to each other.  *)

 public void drawConnector(double x1, double y1, double x2, double y2, double xfct, double yfct, double NodeRadius, char EdgeColor, LinkedList llist, draw d)  
 {	  

	double xLine[], yLine[];
	double Angle,Adjustx, Adjusty;
	int NC, TC;

	xLine = new double [2];
	yLine = new double [2];
	if (x1==x2)	// Undefined slope *)
	{ 
		xLine[0]=x1;yLine[0]=y1-(yfct*NodeRadius);
		xLine[1]=x2;yLine[1]=y2+(yfct*NodeRadius);
	}
	else	// The slope is defined! *)
	{                    
		Angle=Math.abs(Math.atan((y2-y1)/(x2-x1))); // get angle of line with the horizontal  *)
		Adjustx=Math.cos(Angle)*NodeRadius;
		Adjusty=Math.sin(Angle)*NodeRadius;
		xLine[0]=x1-(xfct*Adjustx);yLine[0]=y1-(yfct*Adjusty);
		xLine[1]=x2+(xfct*Adjustx);yLine[1]=y2+(yfct*Adjusty);
	}
	NC = extractColor(EdgeColor);
	TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	GKS.set_textline_color(NC,llist,d);
	GKS.polyline(2,xLine,yLine,llist,d);
	GKS.set_textline_color(Black,llist,d);
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
	TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	GKS.set_fill_int_style(bsSolid,NC,llist,d);
	GKS.fill_area(4,xArr,yArr,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
    }

 //                         PROCEDURE DrawCircularNode               *)
 //                                                                  *)
 //    Given the  point at which the center of the node is to be     *)
 //  drawn, the radius of the node, and the                          *)
 //  linked list of lines of text comprising the current node, this  *)
 //  procedure properly  draws this node to the  open  workstation,  *)
 //  highlighting the node as needed.                                *)
 public void drawCircNode(double Centerx, double Centery, double NodeRadius, LinkedList TextList, String index, LinkedList llist, draw d)   
 {
	int NodeColor,TextColor, pos;
	double TextStartx,TextStartY;
	String TLP;
 
	TextList.reset();
	TLP = (String) TextList.nextElement();
	if (TLP.length() == 0) 
		TLP = new String (" ");
    
	if (TLP.length()>2)	// Potential Highlight Delimeter... *)
	{ 
        	String TempString = new String (TLP);
        	if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) 
		{
			NodeColor = extractColor(TempString.charAt(1));
			TextColor = extractTextColorForHighlightedNodes(TempString.charAt(1));
			GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
            		GKS.circle_fill(Centerx,Centery,NodeRadius,llist,d);
            		GKS.set_textline_color(Black,llist,d);
            		GKS.circle(Centerx,Centery,NodeRadius,llist,d);
            		GKS.set_textline_color(TextColor,llist,d);
            		GKS.set_fill_int_style(bsClear,NodeColor,llist,d);   // To insure
                        //the text background is invisible 
	
			//  Now we must create a new string that removes  *)
  			//  the compose character delimeter...            *)
			TLP = TempString.substring(2);
		}
		else	 // Don't highlight node
		{	 
          		GKS.set_textline_color(Black,llist,d);
            		GKS.circle(Centerx,Centery,NodeRadius,llist,d);
		}
	}
	else	// Don't highlight node
	{	
        	GKS.set_textline_color(Black,llist,d);
        	GKS.circle(Centerx,Centery,NodeRadius,llist,d);
    	}

	// Write in the text. *)
	TextStartx = Centerx;
	TextStartY = Centery + (0.5 * (((linespernode - 1) * Textheight) + ((linespernode - 1) * (0.5 * Textheight))));
    
	GKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);	// TA_TOP?  TA_BASELINE?
	// We already have the head node in the list
   	 GKS.text(TextStartx,TextStartY,TLP,llist,d);
	// check for more
    	while (TextList.hasMoreElements()) 
	{ 
		TLP = (String) TextList.nextElement();
		//  Ready textstarts for next line.  *)
		TextStartY=TextStartY-(1.5*Textheight);
		GKS.text(TextStartx,TextStartY,TLP,llist,d);
	} //                        (*  WHILE  *)
	GKS.set_textline_color(Black,llist,d);
	GKS.text(TextStartx,(TextStartY - (Leny/2) - .01),index,llist,d);
	GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
 }
}