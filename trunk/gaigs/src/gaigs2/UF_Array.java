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
// import java.io.*;
// import java.awt.*;
// import java.util.*;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

public class UF_Array extends LinearList
{
 
 int cpf, cpu, Numrows, Numcols, badCommand, weight;
 double Maxlabelwidth;
 String command;
 
 public UF_Array ()
 {
	super();
 }

 boolean emptyStruct()
 {
	return(false);
 }

 void loadStructure (StringTokenizer st, LinkedList llist, draw d) throws  VisualizerLoadException
 {
	ArrayNode an = new ArrayNode();
	String s;
	boolean done = false;

	if (st.hasMoreTokens())
		s = st.nextToken();
	else
		throw (new VisualizerLoadException ("Reached end when expecting number of rows"));
	
	Numrows = Format.atoi(s);	

	if (st.hasMoreTokens())
		s = st.nextToken();
	else
		throw (new VisualizerLoadException ("Reached end when expecting number of rows"));
	
	Numcols = Format.atoi(s);

	if (st.hasMoreTokens())
	    s = st.nextToken();
	else
	    throw (new VisualizerLoadException ("Reached end when expecting valid command indicator"));
	
	weight = Format.atoi(s);

	if (st.hasMoreTokens())
		s = st.nextToken();
	else
		throw (new VisualizerLoadException ("Reached end when expecting valid command indicator"));
	
	badCommand = Format.atoi(s);
	
	//get array stuff
	for(int x = 0; x < Numrows; x++)
	{
		try 
		{
			an = getArrayNode(st,llist,d);
		}
		catch ( EndOfSnapException e ) 
		{
			done = true;
		}
	    
		if (!done) 
			nodelist.append(an);		
	}

	//loop that get the final values from the file
	for(int x = 0; x < 4; x++)
	{
		if (st.hasMoreTokens()) 
			s= st.nextToken();
	        else 
			throw (new VisualizerLoadException ("End of data when expecting data"));
		
		if(x == 0)
			cpf = Format.atoi(s);
		else if(x == 1)
			cpu = Format.atoi(s);
		else if(x == 2)
			command = s;
	}
 }

 private ArrayNode getArrayNode(StringTokenizer st, LinkedList llist, draw d) throws EndOfSnapException, VisualizerLoadException
 {
	ArrayNode an;
	String tstr;
	int temp;
	double check;

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


	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
        
	temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tstr);


	//	temp = d.getGraphics().getFontMetrics(defaultFont).stringWidth(tstr);

	check = ((double) temp / (double) d.getSize().width /*maxsize*/);
	if (check > Maxlabelwidth)
		Maxlabelwidth = check;

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

 public void calcDimsAndStartPts(LinkedList llist, draw d)
 {
	double TitleToSSGap, MinGt, MaxGt, Diam, MinTitlex, MaxTitlex;
	int NumNodes, NumLines;

	super.calcDimsAndStartPts(llist,d);
	
	//define side lengths
	Lenx = (Maxstringlength + Textheight) - .03;

	if (linespernode == 1) 
		Leny = Textheight * 1.50;
	else
		Leny = (linespernode * Textheight) + ((linespernode - 1) * (0.50 * Textheight));
	
	//define diagonals for top and side of array
	TDx = (Lenx + Leny) / 5.0;           // Height is a third of their average *)
	TDy = TDx / Math.sqrt(3);

	NumNodes = nodelist.size();     //NumberOfNodes(snapsht);

	TitleToSSGap = (2 * Titleheight); //space needed for title?
	NumLines = title.size();

	//this messes with the demensions of the snapshot
	snapheight = (NumLines * Titleheight) + ((NumLines - 1) * (0.5 * Titleheight)) + TitleToSSGap + Textheight + (0.5 * Textheight) + TDy + (Numrows * Leny);
	snapwidth = (Numcols * Lenx) + TDx + (0.5 * Textheight) + Maxlabelwidth;

	//define starting positions for structure
	Startx = (CenterScreen -(0.5 * snapwidth) + Maxlabelwidth + (0.5 * Textheight)) - .3;
	Starty = Topy-IconHeight-IconToTitleGap-(NumLines*Titleheight)-((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0);
	
	//define starting y cord for title
	TitleStarty = TitleEndy;
 }

 void drawStructure (LinkedList llist, draw d)
 {
	ArrayNode an;
	int  rows, cols;
        double SRx, SRy, PermStarty, xline[], yline[];
	String text;
	
	super.drawStructure(llist,d);
        xline = new double [2];
        yline = new double [2];
        yline[0] = TitleEndy - .05;
        yline[1] = yline[0];
        xline[0] = 0;
        xline[1] = 1;
        // The polyline is drawn immediately under the title/caption 
	LGKS.polyline(2,xline,yline,llist,d);

    	if (emptyStruct()) 
	{
	    super.drawStructure(llist,d);  // to handle empty structure
	    return;
        }

    	if (emptyStruct()) 
	{
		super.drawStructure(llist,d);  // to handle empty structure
	    	return;
        }
	if(Numrows > 10)
		Leny = .6/Numrows;
        //  Must initialize row and column label counters.  *)
        SRx=Startx-(0.5*Textheight);
        SRy=Starty  /*-(0.5*Leny)*/;
	nodelist.reset();
	an = (ArrayNode) nodelist.nextElement();
        PermStarty=Starty;
        for (cols = 1; cols <= Numcols; cols++) 
	{ 
		drawTop(Startx,Starty,llist,d);
		LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
		if (Numcols>1)          //  We can draw column labels...  *)
			LGKS.text(Startx+(CenterScreen*Lenx),Starty+TDy+(0.5*Textheight),an.col,llist,d);
		for (rows = 1; rows <= Numrows; rows++) 
		{
			if (cols==1)  // Must draw Row labels *)
			{ 
				LGKS.set_text_align(TA_RIGHT,TA_TOP,llist,d);
				LGKS.text(SRx,SRy,an.row,llist,d);
				SRy=SRy-Leny;
			}
			LGKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
			drawRectNode(false, an.textInNode,llist,d);
			if (nodelist.hasMoreElements())
				an = (ArrayNode) nodelist.nextElement();
		}
		Startx=Startx+Lenx;
		Starty=PermStarty;
	}
        //  Now that the array is drawn, we must draw the 3-dimensional  *)
        //  side of the array...                                         *)
        for (rows = 1; rows <= Numrows; rows++) 
	{
		drawSide(Startx,Starty,llist,d);
		Starty=Starty-Leny;
	}
	
	Starty=PermStarty;

	drawSets(llist, d);
	
	LGKS.set_text_align(0, 2, llist, d);
	
	//Print Text info to screen
	if(badCommand == 1)
	{
	    text = "Bad command!  No action taken.";
	    LGKS.set_textline_color(4, llist, d);
	    LGKS.text(CenterScreen, TitleEndy - .07, text, llist, d);
	}
	    
	LGKS.set_text_align(1, 2, llist, d);
	LGKS.set_textline_color(3, llist, d);
	text = "Comparsions Per Find: " + cpf;
	LGKS.text(.1, .12, text, llist, d);
	text = "Comparsions Per Union: " + cpu;
	LGKS.text(.1, .07, text, llist, d);
	LGKS.set_textline_color(2, llist, d);
	text = "Current Command";
	LGKS.text(.55, Starty + Leny, text, llist, d);	
	LGKS.text(.55, (Starty + Leny) - .05, command, llist, d);

	if(weight == 0)
	    text = "Weighted Union: On";
	else
	    text = "Weighted Union: Off";

	LGKS.text(.55, Starty + Leny + .05, text, llist, d);
 }

 //method that draws all the set information on the screen
 void drawSets(LinkedList llist, draw d)
 {
	LinkedList sets = new LinkedList();
	ArrayNode an = new ArrayNode();
	String text, set = "";
	boolean match = false, first = true;
	double yCord, xCord;
	int temp, sum = 0;
	double stringLength = 0.0;

	LGKS.set_text_align(1, 2, llist, d);

	nodelist.reset();
	an = (ArrayNode) nodelist.nextElement();
	//this loop grabs all the names of the different sets
	for(int x = 0; x < Numrows; x++)
	{
		an.textInNode.reset();
		text = (String) an.textInNode.currentElement();
		if(text.charAt(0) == '\\')
				text = text.substring(2);
		if(sets.size() == 0)
			sets.append(text);
		else
		{
			sets.reset();
			for(int y = 0; y < sets.size(); y++)
			{
				if(((String)sets.currentElement()).equals(text))
					match = true;
				else if(sets.hasMoreElements())
					sets.nextElement();
			}
			if(!match)
				sets.append(text);
		}
		match = false;
		if (nodelist.hasMoreElements())
			an = (ArrayNode) nodelist.nextElement();
	}	
	
	sets.reset();
	yCord = (Starty + Leny) - .15;
	xCord = .55;
	//this loop concantanates all the elements of the current set
	//onto a String and then prints them out to the screen.
	while(sets.hasMoreElements())
	{
		set += (String)sets.currentElement();
		set += "{";
		nodelist.reset();
		an = (ArrayNode) nodelist.nextElement();
		for(int x = 0; x < Numrows; x++)
		{
			an.textInNode.reset();
			text = (String) an.textInNode.currentElement();
			if(text.charAt(0) == '\\')
				text = text.substring(2);
			if(text.equals((String)sets.currentElement()) && first)
			{
				set += "" + x;
				first = false;
				sum++;
			}
			else if(text.equals((String)sets.currentElement()))
			{
			    sum++;
			    set += ", " + x;
			}

			if (nodelist.hasMoreElements())
				an = (ArrayNode) nodelist.nextElement();
		}
		set += "}";


		BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
        
		temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(set);



		//		temp = d.getGraphics().getFontMetrics(defaultFont).stringWidth(set);

		stringLength = ((double)temp / (double) d.getSize().width);

		if((xCord + stringLength) >= .95)
		{
		    xCord = .55;
	       	    yCord -= .1;
		    //output weights of each set
		    text = "" + sum;
		    LGKS.text(xCord + ( stringLength / 2), yCord + .04, text, llist, d);
		    //output set
		    LGKS.text(xCord, yCord, set, llist, d);
		    xCord += stringLength + .05;
		}
		else
		{
		    //output weights of each set
		    text = "" + sum;
		    LGKS.text(xCord + ( stringLength / 2), yCord + .04, text, llist, d);
		    //output set
		    LGKS.text(xCord, yCord, set, llist, d);
		    xCord += stringLength + .05;
		}
		sum = 0;
		set = "";
		sets.nextElement();
		first = true;
	}
 }
}
