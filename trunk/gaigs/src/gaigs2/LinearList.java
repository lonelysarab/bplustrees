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
import java.awt.image.*;

import org.jdom.*;

public class LinearList extends StructureType {

    protected final static int Up = 0;
    protected final static int Down = 1;
    protected final static int Right = 2;
    protected double Lenx;
    protected double Leny;
    protected double Startx;
    protected double Starty;
    protected double TDx;
    protected double TDy;

    public LinearList () {
	super();
    }

    void loadStructure(Element struct, LinkedList llist, draw d) 
	throws VisualizerLoadException {
	load_name_and_bounds(struct, llist, d);

	Iterator iter = struct.getChildren().iterator();
	while(iter.hasNext()) {
	    Element child = (Element) iter.next();
	    if( child.getName().compareTo("list_item") == 0 )
		getItem(child, llist, d);
	}
    } // loadStructure(element)

    protected void getItem(Element item, LinkedList llist, draw d) {
	Element label = item.getChild("label");

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);

	// load text, w/ nodecolor
	int num_lines = 0;
	String label_line = "";
	LinkedList textInNode = new LinkedList();

	if( item.getAttributeValue("color").charAt(0) != '#' )
	    label_line = "\\" + color_str_to_char( item.getAttributeValue("color") );
	else
	    label_line = "\\" + item.getAttributeValue("color");

	if(label == null)
	    // shouldnt happen. required.
	    textInNode.append(label_line);
	else {
	    String labelText = label.getText().trim();
	    StringTokenizer st = new StringTokenizer(labelText, "\f\r\n");
	    while(st.hasMoreTokens()) {
		num_lines++;
		label_line += st.nextToken();
		textInNode.append(label_line);
		String tlinenocolor = textwocolor(label_line);
		int tlinelength = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tlinenocolor);
		double temp;
		if(  Maxstringlength < ( temp = (double) tlinelength / (double) GaigsAV.preferred_width )  )
		    Maxstringlength = temp;
		label_line = "";
	    }

	    if(labelText == ""){
		num_lines = 1;
		textInNode.append(label_line + " ");
		String tlinenocolor = textwocolor(label_line + " ");
		int tlinelength = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tlinenocolor);
		double temp = (double)tlinelength / (double)GaigsAV.preferred_width;
		if(Maxstringlength < temp)
		    Maxstringlength = temp;

		label_line = "";
	    }
	}

	if(num_lines > linespernode)
	    linespernode = num_lines;

	nodelist.append(textInNode);
    }

    void loadStructure (StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException {

	boolean done = false; 
	LinkedList templl = new LinkedList();

	Xcenter=CenterScreen; // These Snapshots are always centered *)
	Ycenter=CenterScreen; 

	while (!done) {
	    try {
		templl = getTextNode(st, linespernode, llist,d);
	    }
	    catch ( EndOfSnapException e ) {
		done = true;
	    }
	    if (!done) nodelist.append(templl);
	}
    } // loadStructure(st)

    boolean emptyStruct() {
  
	if (nodelist.size() == 0) 
	    return(true);
	else
	    return(false);
    }

    //  This  procedure, given  the x and y coords of left hand corner      *)
    //  of the node whose top needs to be drawn, draws the 3D top of the    *)
    //  box.                                                                *)
    protected void  drawTop( double x, double y, LinkedList llist, draw d) {
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
	LGKS.set_fill_int_style(bsDiagCross,LightGray,llist, d);
	LGKS.fill_area(5,TDTx,TDTy,llist, d);
	LGKS.polyline(5,TDTx,TDTy,llist,d);
	LGKS.set_fill_int_style(bsClear,White,llist, d);
    }

    //     Given the x and y point that marks the upper right hand  corner  *)
    //  of the square  node, and  the  node's  dimensions, this  procedure  *)
    //  draws the three-dimensional side portion of the rectangular  node.  *)
    protected void drawSide(double  StartX, double StartY, LinkedList llist, draw d)  {

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
	LGKS.set_fill_int_style(bsDiagCross,LightGray,llist, d);
	LGKS.fill_area(5,TDSx,TDSy,llist, d);
	LGKS.polyline(5,TDSx,TDSy,llist,d);
	LGKS.set_fill_int_style(bsClear,White,llist, d);
    }

    //    Draws the arrow  that will be appropriately  inserted in stacks,  *)
    //  queues, and linked lists.                                           *)
    protected void  drawArrow(double BaseX,double BaseY, int  Direction, 
			      LinkedList llist, draw d)  {

	double Trix[], Triy[], Linex[],Liney[];
	double ArrowLength, TriSide;
	Trix = new double [4];
	Triy = new double [4];
	Linex = new double [2];
	Liney = new double [2];
	ArrowLength=(ArrowPercentofLenx*Lenx);
	TriSide=(0.33333333*ArrowLength)/Math.sqrt(3);
	switch  (Direction) {
	case Up: 
	    Linex[0]=BaseX;Liney[0]=BaseY;
	    Linex[1]=BaseX;Liney[1]=BaseY+(0.66666667*ArrowLength);
	    Trix[0]=BaseX-TriSide;Triy[0]=Liney[1];
	    Trix[1]=BaseX;Triy[1]=BaseY+ArrowLength;
	    Trix[2]=BaseX+TriSide;Triy[2]=Triy[0];
	    Trix[3]=Trix[0];Triy[3]=Triy[0];
	    break;
	case Down:
	    Linex[0]=BaseX;Liney[0]=BaseY;
	    Linex[1]=BaseX;Liney[1]=BaseY-(0.66666667*ArrowLength);
	    Trix[0]=BaseX-TriSide;Triy[0]=Liney[1];
	    Trix[1]=BaseX;Triy[1]=BaseY-ArrowLength;
	    Trix[2]=BaseX+TriSide;Triy[2]=Triy[0];
	    Trix[3]=Trix[0];Triy[3]=Triy[0];
	    break;
	case Right:
	    Linex[0]=BaseX;Liney[0]=BaseY;
	    Linex[1]=BaseX+(0.66666667*ArrowLength);Liney[1]=BaseY;
	    Trix[0]=Linex[1];Triy[0]=BaseY+TriSide;
	    Trix[1]=BaseX+ArrowLength;Triy[1]=BaseY;
	    Trix[2]=Trix[0];Triy[2]=BaseY-TriSide;
	    Trix[3]=Trix[0];Triy[3]=Triy[0];
	    break;
	}
	LGKS.set_fill_int_style(bsSolid,Black,llist,d);
	LGKS.polyline(2,Linex,Liney,llist,d);
	LGKS.fill_area(4,Trix,Triy,llist,d);
	LGKS.set_fill_int_style(bsClear,White,llist,d);
    }

    //  Given the postion to start the text of this node, and the       *)
    //  pointer to the linked list of lines of text comprising the      *)
    //  current node, this procedure properly draws the node            *)
    //  (with text).                                                    *)
    protected void drawRectNode(// Instead access StartX & StartY -- VAR NodeStartx,NodeStarty:real;
				boolean drawingLinkedList,
				LinkedList TextList, LinkedList llist, draw d)  {

	String TLP;
	int pos,NodeColor, TextColor;
	double Nodex[],Nodey[]; 
	double Textx,Texty;

	Nodex = new double [5];
	Nodey = new double [5];

	TextList.reset();

	if(TextList.hasMoreElements()){
	    TLP = (String) TextList.nextElement();
	}else{
	    TLP = " ";
	}

	LGKS.set_textline_color(Black,llist,d);
	Nodex[0]=Startx;Nodey[0]=Starty;
	Nodex[1]=Startx+Lenx;Nodey[1]=Starty;
	Nodex[2]=Startx+Lenx;Nodey[2]=Starty-Leny;
	Nodex[3]=Startx;Nodey[3]=Starty-Leny;
	Nodex[4]=Nodex[0];Nodey[4]=Nodey[0];
	if (TLP.length() == 0) 
	    TLP = new String (" ");
	if (TLP.length()>2) { // Potential Highlight Delimeter... *)
	    String TempString = new String (TLP);
	    if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) {
		NodeColor = new_extractColor(TempString.substring(1));
		TextColor = new_extractTextColorForHighlightedNodes(TempString.substring(1));
		LGKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
		LGKS.set_textline_color(TextColor,llist,d);
		LGKS.fill_area(5,Nodex,Nodey,llist,d);
		//  Now we must create a new string that removes  *)
		//  the compose character delimeter...            *)
		TLP = ( (TempString.charAt(1) != '#') ? TempString.substring(2) : TempString.substring(8) ) ;
	    }
	}
	Textx=Startx+(0.5*Textheight);
	Texty=Starty-0.002;
	//  We have first line in TextList  *)
	LGKS.set_text_align(TA_CENTER,TA_TOP,llist,d);
	// We already have the head node in the list
	LGKS.text(Startx+(Lenx/2),Texty,TLP,llist,d);
	// check for more
	while (TextList.hasMoreElements()) { 
	    TLP = (String) TextList.nextElement();
	    //  Ready textstarts for next line.  *)
	    Texty=Texty-(Textheight)-0.002;
	    LGKS.text(Startx+(Lenx/2),Texty,TLP,llist,d);
	} //                        (*  WHILE  *)
	//  Set Text Color Back to the default...  *)
	LGKS.set_textline_color(Black,llist,d);
	LGKS.set_fill_int_style(bsClear,White,llist,d);
	LGKS.polyline(5,Nodex,Nodey,llist,d);
	if (drawingLinkedList) // Adjust x-coord accordingly *)
	    Startx=Startx+Lenx;
	else                 // We need a vertical accumulation - adjust y-coord accordingly *)
	    Starty=Starty-Leny;
    }

}   // end of Linear list class 
