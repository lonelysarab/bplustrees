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
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

import org.jdom.*;

public class MD_Array extends LinearList {

    int Numrows, Numcols;
    double Maxlabelwidth;

    public MD_Array () {
	super();
	Maxlabelwidth = 0.0;
    }


    boolean emptyStruct() {
  
	if (nodelist.size() == 0) 
	    return(true);
	else
	    return(false);
    }

    void loadStructure(Element struct, LinkedList llist, draw d) 
	throws VisualizerLoadException {

	load_name_and_bounds(struct,llist,d);

	Vector row_labels = new Vector();
	Vector col_labels = new Vector();
	Iterator iter = struct.getChildren().iterator();

	Numrows = 0;
	Numcols = 0;

	while( iter.hasNext() ) {
	    Element child = (Element) iter.next();
	    if( child.getName().compareTo("row_label") == 0 ) {
		row_labels.add( child.getText() );
	    }
	    else if( child.getName().compareTo("column_label") == 0 ) {
		col_labels.add( child.getText() );
	    }
	    else if( child.getName().compareTo("column") == 0 ) {
		loadColumn( child, row_labels, col_labels, llist, d );
		Numcols++;
	    }
	}
    } // loadStructure(element)

    void loadColumn(Element column, Vector row_labels, Vector col_labels, LinkedList llist, draw d) 
	throws VisualizerLoadException {

	int rownum = 0;
	boolean label_rows = ( Numcols == 0 && row_labels.size() > 0 );

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
	ArrayNode an = new ArrayNode();
	if( col_labels.size() > Numcols )
	    an.col = (String) col_labels.elementAt(Numcols); // label the column (col member of 1st ArrayNode in a col)
	else
	    an.col = "";

	Iterator iter = column.getChildren().iterator();
	while( iter.hasNext() ) {
	    Element list_item = (Element) iter.next();

	    // load text, cell color
	    int num_lines = 0;
	    String label_line = " ";
	    an.textInNode = new LinkedList();

	    if( list_item.getAttributeValue("color").charAt(0) != '#' )
		label_line = "\\" + color_str_to_char( list_item.getAttributeValue("color") );
	    else{
		label_line = "\\" + list_item.getAttributeValue("color");
	    }

	    Element labelEl = list_item.getChild("label");
	    String labelText;
	    if( labelEl != null )
		labelText = labelEl.getText().trim();
	    else{
		labelText = " ";
		label_line += " ";
	    }
	    StringTokenizer st = new StringTokenizer( labelText, "\f\r\n");
	    while(st.hasMoreTokens()) {
		num_lines++;
		label_line += st.nextToken();
		an.textInNode.append(label_line);
		
		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( textwocolor(label_line) );
		double check = ((double) temp / (double) GaigsAV.preferred_width);
		if (check > Maxstringlength)
		    Maxstringlength = check;

		label_line = "";
	    }

	    if(labelText.trim() == ""){
		an.textInNode.append(label_line + " ");

		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(textwocolor(label_line + " "));
		double check = ((double)temp / (double)GaigsAV.preferred_width);
		if(check > Maxstringlength)
		    Maxstringlength = check;

		label_line = "";

		num_lines = 1;
	    }

	    if( label_rows && rownum < row_labels.size() ) {
		an.row = (String) row_labels.elementAt(rownum);
		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(
		     textwocolor( (String) row_labels.elementAt(rownum) ) );
		double check = ((double) temp / (double) GaigsAV.preferred_width);
		if (check > Maxlabelwidth)
		    Maxlabelwidth = check;
	    }
	    else
		an.row = "";

	    if(num_lines > linespernode)
		linespernode = num_lines;

	    nodelist.append(an);
	    rownum++;
	    an = new ArrayNode();
	}

	if( Numrows != 0 && rownum != Numrows )
	    throw new VisualizerLoadException("Inconsistent number of rows in col " + Numcols);
	Numrows = rownum;
    } // loadColumn(element)

    void loadStructure (StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException {

	boolean done = false; 
	ArrayNode an = new ArrayNode();
	String s;

	Xcenter=CenterScreen; // These Snapshots are always centered *)
	Ycenter=CenterScreen; 
	if (st.hasMoreTokens())
	    s = st.nextToken();
	else
	    throw (new VisualizerLoadException ("Reached end when expecting number of rows"));
	Numrows = Format.atoi(s);
	if (st.hasMoreTokens())
	    s = st.nextToken();
	else
	    throw (new VisualizerLoadException ("Reached end when expecting number of columns"));
	Numcols = Format.atoi(s);
	while (!done) {
	    try {
		an = getArrayNode(st,llist,d);
	    }
	    catch ( EndOfSnapException e ) {
		done = true;
	    }
	    if (!done) nodelist.append(an);
	}

    } // loadStructure(st)

    private ArrayNode getArrayNode(StringTokenizer st, LinkedList llist, draw d)  throws 
	EndOfSnapException, VisualizerLoadException {
	
	ArrayNode an;
	String tstr;
	int temp;
	double check;
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);

	an = new ArrayNode();
	if (st.hasMoreTokens())
	    an.row = st.nextToken();
	else
	    throw (new VisualizerLoadException ("Reached end when expecting row label"));
	if (an.row.compareTo(EndSnapShot) == 0)
	    throw (new EndOfSnapException ());
	an.row = an.row.trim();
	if (an.row.length() == 0) 
	    an.row = new String (" ");
	tstr = textwocolor(an.row);

	temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tstr);

	    // This strategy works also, but the BufferedImage seems
	    // guaranteed and the java docs regarding
	    // FontRenderContext seem to indicate less reliable
	//	temp = (int)(defaultFont.getStringBounds(tstr, new FontRenderContext(new AffineTransform(),true,true))).getWidth();

	check = ((double) temp / (double) GaigsAV.preferred_width /*maxsize*/);

// 	check = ((double) temp / (double) d.getSize().width /*maxsize*/);
	if (check > Maxlabelwidth)
	    Maxlabelwidth = check;
// 	if( check > Maxstringlength)
// 	    Maxstringlength = check;
	if (st.hasMoreTokens())
	    an.col = st.nextToken();
	else
	    throw (new VisualizerLoadException ("Reached end when expecting column label"));
	if (an.col.compareTo(EndSnapShot) == 0)
	    throw (new EndOfSnapException ());
	an.col = an.col.trim();
	if (an.col.length() == 0) 
	    an.col = new String (" ");
	an.textInNode = getTextNode(st, linespernode, llist,d);
	return(an);
    }


    public void calcDimsAndStartPts(LinkedList llist, draw d) {

	double TitleToSSGap,
	    MinGt,MaxGt,Diam,
	    MinTitlex,MaxTitlex;
	int NumNodes,NumLines;

	super.calcDimsAndStartPts(llist,d);	
	//	Lenx=Maxstringlength + Textheight/*0.015*/ /*0.015 was once "Textheight"*/;
	Lenx=Maxstringlength + Textheight*0.1   /*0.015*/ /*0.015 was once "Textheight"*/;
	if (linespernode==1){
	    //	    Leny=Textheight*2.0;
	    Leny=Textheight*1.1;
	}else{
	    Leny=(linespernode*Textheight)+
		((linespernode-1)*(0.50*Textheight));
}
	TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
	TDy=TDx/Math.sqrt(3);
	NumNodes= nodelist.size();     //NumberOfNodes(snapsht);
	TitleToSSGap=2*Titleheight;
	NumLines=title.size();
	snapheight=(NumLines*Titleheight)+
	    ((NumLines-1)*(0.5*Titleheight))+
	    TitleToSSGap+Textheight+(0.5*Textheight)+
	    TDy+(Numrows*Leny);
	snapwidth=(Numcols*Lenx)+TDx+(0.5*Textheight)
	    +Maxlabelwidth;
	Startx=CenterScreen-(0.5*snapwidth)+Maxlabelwidth+
            (0.5*Textheight);
	Starty=CenterScreen+(0.5*snapheight)-(NumLines*Titleheight)-
            ((NumLines-1)
             *(0.5*Titleheight))-TitleToSSGap-(Textheight)-
            (0.5*Textheight)-TDy;
	TitleStarty=Ycenter+(0.5*snapheight)-Titleheight;
    }
	
	
    void drawStructure (LinkedList llist, draw d)  {
	ArrayNode an;
	int  rows, cols;
        double SRx,SRy,PermStarty;

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
        for (cols = 1; cols <= Numcols; cols++) { 
	    drawTop(Startx,Starty,llist,d);
	    LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
	    if (Numcols>1)          //  We can draw column labels...  *)
		LGKS.text(Startx+(CenterScreen*Lenx),Starty+TDy
			 +(0.5*Textheight),an.col,llist,d);
	    for (rows = 1; rows <= Numrows; rows++) {
		if (cols==1)  { // Must draw Row labels *)
		    LGKS.set_text_align(TA_RIGHT,TA_TOP,llist,d);
		    LGKS.set_text_height(Textheight, llist, d);
		    LGKS.text(SRx,SRy,an.row,llist,d);
		    SRy=SRy-Leny;
		}
		//LGKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
		LGKS.set_text_align(TA_CENTER,TA_TOP,llist,d);
		drawRectNode(false, an.textInNode,llist,d);
		if (nodelist.hasMoreElements())
		    an = (ArrayNode) nodelist.nextElement();
	    }
	    Startx=Startx+Lenx;
	    Starty=PermStarty;
	}
        //  Now that the array is drawn, we must draw the 3-dimensional  *)
        //  side of the array...                                         *)
        for (rows = 1; rows <= Numrows; rows++) {
	    drawSide(Startx,Starty,llist,d);
	    Starty=Starty-Leny;
	}
 
    }

}

