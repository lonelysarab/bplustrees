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

//Ben Tidman
//Structure that draws multiple general trees

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

public class UFTree extends NonLinearStruct  
{
 double xspacing,yspacing;  
 boolean  Dne;   // TO determine when Tree has been built
 GTN NextNode;
 int CurrLevel, numNodes, nn, badCommand, weight, path;
 double ModifierSum, x, y;
 double NextPos [], Modifier[];
 double CenterShift, xMin,xMax, yMin,yMax, maxXCord, minXCord;
 String cpf, cpu, command;
 
 final static String newTree = "\\tree"; //new tree delimeter
 LinkedList trees;

 public UFTree () 
 {
	//root = null;  // needed??
	CurrLevel = 0;
	ModifierSum = 0.0;
	maxXCord = 0.0;
	minXCord = 0.0;
	NextPos = new double [MaxLevels];
	Modifier = new double [MaxLevels];
	trees = new LinkedList();
 }

 public boolean emptyStruct()  
 { 
	if (nodelist.size() == 0)
		return(true);
	else
		return(false);
 }

 // Responsible for loading the number of lines per node
 // Receives a line delimited tokenizer.  It will only process one line of 
 // that tokenizer.  It must read that line, create a space/tab delimited
 // tokenizer from it, grab the number of lines per node from that tokenizer.
 // Structures that can contain additional information following the number
 // of lines per node (such as trees) should then override this generic 
 // loadLinesPerNodeInfo with their own version of the method which will call
 // on the super version to get the actual lines per node and add additional 
 // code to process the other information
 public void loadLinesPerNodeInfo(StringTokenizer st, LinkedList llist, draw d)throws VisualizerLoadException	 
 {
	String tempString, tempString2;
	if (st.hasMoreTokens()) 
		tempString = st.nextToken();
	else 
		throw ( new VisualizerLoadException ("Expected lines per node - found end of string"));
	
	StringTokenizer t = new StringTokenizer (tempString, " \t");
	if (t.hasMoreTokens()) 
		tempString2 = t.nextToken();
	else 
		throw ( new VisualizerLoadException ("Expected lines per node - found " + tempString));
	
	linespernode = Format.atoi(tempString2);

	xspacing = 1.5;
	yspacing = 1.5;
	
	if (t.hasMoreTokens()) 
		tempString2 = t.nextToken();
	else 
		return;
	
	xspacing =  Format.atof(tempString2);

	if (t.hasMoreTokens()) 
		tempString2 = t.nextToken();
	else 
		return;
	
	yspacing =  Format.atof(tempString2);
 }


	 
 public void calcDimsAndStartPts(LinkedList llist, draw d)  
 {
	/*  Determines the following variables Lenx, Leny, Startx, Starty,
	    TitleStarty   */
	// Task: Calculate the length and height of a node and its starting point.
	//   It also computes the y-coordinate of the title

 	double TitleToSSGap, MinGt, MaxGt, Diam, MinTitlex, MaxTitlex;
 	int NumNodes,NumLines;
	super.calcDimsAndStartPts(llist,d);
	// With circular nodes, not adding Textheight works better to finetune the circle size
	Lenx=Maxstringlength - .02;
	Leny=((linespernode+1)*Textheight)+((linespernode-1)*(0.5*Textheight));
	TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
	TDy=TDx/Math.sqrt(3);
	TitleToSSGap=2*Titleheight;
	NumLines=title.size();
	Startx = 0.0;
	Starty=Topy-IconHeight-IconToTitleGap-(NumLines*Titleheight)-((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0) - .15;
	TitleStarty = (Topy - IconHeight - IconToTitleGap - Titleheight) + .05;
 }


 //                 PROCEDURE DrawGeneralTree                            *)
 //                                                                      *)
 // This procedure calls on xCoord to obtain the x coordinate of each    *)
 // node in the tree, and then calls on ApplyModifier to to make any     *)
 // adjustments to the node positions that couldn't be made in xCoord.   *)
 // Finally, it does a pre-order traversal of the tree and draws each    *)
 // node to its rightful place in the tree, and connects appropriate     *)
 // nodes to form the binary tree.                                       *)
 void drawStructure (LinkedList llist, draw d)  
 {
	int x;
	double TempLoc, xline[], yline[]; 
	double scootch = 0.0;
	GTN gtn = new GTN();
	String text, c;
	StringTokenizer checkCommand;

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
	for (x = 0; x < MaxLevels; ++x) 
	{
        	Modifier[x]=0.0;
        	NextPos[x]=0.1;//0.025;//TreeSideBorder+(Lenx/2.0); // The rightmost position at which we can plot a node *)
	}

	//Scale drawings for more then 10 items
	if(nn >= 10)
	{
		xspacing -= (nn - 9) * .065;
	}

	//adjust the node starting position so that the sets don't overlap
	Starty -= ((nn / 7 + 2)) * .05;
	
	nodelist.reset();
	while(nodelist.hasMoreElements())
	{
		if (nodelist.hasMoreElements())
			gtn = (GTN)(nodelist.nextElement());
		else
			gtn = (GTN)(nodelist.currentElement());
		TempLoc= xCoord(gtn);
		ApplyModifier(gtn);
	}
      
	if(maxXCord + (Lenx / 2) < 1.0)
	{
		ModifierSum = Lenx / 2;
		if(nodelist.size() > 2)
		    scootch = ((.975 - (maxXCord + (Lenx / 2))) / ((double)nodelist.size() - 1.0));   
		else
		{
		    scootch = ((1.0 - (maxXCord + (Lenx / 2))) / (double)nodelist.size());		
		    ModifierSum = scootch / 2;
		}
	}
	else
		ModifierSum = (((maxXCord - minXCord) / 2) - .5) * -1;

	nodelist.reset();
	x = 0;
	while(nodelist.hasMoreElements())
	{
		if (nodelist.hasMoreElements())
			gtn = (GTN)(nodelist.nextElement());
		else
			gtn = (GTN)(nodelist.currentElement());
		
		ApplyModifier(gtn);
		if(x == (nodelist.size() / 2) && badCommand == 1)
		{
		    LGKS.set_text_align(0, 2, llist, d);
		    LGKS.set_textline_color(4, llist, d);
		    text = "Bad command!  No action taken.";
		    LGKS.text(CenterScreen, (gtn.Gy + .13), text, llist, d);
		    LGKS.set_text_align(1, 2, llist, d);
		}
		// The factor needed to shift the tree to the center *)
		xMin=CenterScreen-(0.5*Maxtitlelength);
		xMax=CenterScreen+(0.5*Maxtitlelength);
		yMax=Topy-IconHeight-IconToTitleGap;
		yMin=yMax;
		drawWalk(gtn, llist, d);
		Xcenter=(xMin+xMax)/2.0;
		Ycenter=(yMin+yMax)/2;
		snapheight=yMax-yMin;
		snapwidth=xMax-xMin;

		ModifierSum += scootch;
		x++;
	}

	LGKS.set_text_align(1, 2, llist, d);
	//Print Text info to screen
	if(weight == 0)
	    text = "Weighted Union: On";
	else
	    text = "Weighted Union: Off";
	LGKS.text(.0, (TitleEndy - .06), text, llist, d);

	if(path == 0)
	    text = "Path Compresion: On";
	else
	    text = "Path Compresion: Off";
	LGKS.text(.45, (TitleEndy - .06), text, llist, d);

	text = "Current Command:";
	LGKS.set_textline_color(2, llist, d);
	LGKS.text(.0, (TitleEndy - .11), text, llist, d);	
	LGKS.text(.0, (TitleEndy - .15), command, llist, d);
	LGKS.set_textline_color(3, llist, d);
	text = "Average Comparsions Per Find: " + cpf;
	LGKS.text(.45, (TitleEndy - .11), text, llist, d);
	text = "Average Comparsions Per Union: " + cpu;
	LGKS.text(.45, (TitleEndy - .15), text, llist, d);

	LGKS.set_textline_color(1, llist, d);
	drawSets(llist, d);
 }

 //Method that draws all elements in each set starting with the root and then
 //calling traverse to get the rest of the tree
 void drawSets(LinkedList llist, draw d)
 {
	int temp;
	double stringLength = 0.0;
	String s = "";
	StringTokenizer getWeight;
	GTN gtn = new GTN();
	GTN TempChild;

	x = .0;
	y = TitleEndy - .24;


	nodelist.reset();
	while(nodelist.hasMoreElements())
	{
		if (nodelist.hasMoreElements())
			gtn = (GTN)(nodelist.nextElement());
		else
			gtn = (GTN)(nodelist.currentElement());
		
		//traverse down current tree
		s = traverse(gtn, s, llist, d, true);
		s += "}";


	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
        
	temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(s);

	//		temp = d.getGraphics().getFontMetrics(defaultFont).stringWidth(s);

		stringLength = ((double)temp / (double) d.getSize().width);
	
		//gets the weight of the current set
		getWeight = new StringTokenizer(s, ",");
		LGKS.set_text_align(0, 2, llist, d);
		LGKS.text(gtn.Gx, (gtn.Gy + Lenx + .02), ("" + getWeight.countTokens()), llist, d);

		LGKS.set_text_align(1, 2, llist, d);
		//these checks insure that the set info will be drawn within
		//the bounds of the window
		if((x + stringLength) >= 1.0)
		{
		    x = 0;
	       	    y -= .05;
		    LGKS.text(x, y, s, llist, d);
		    x = stringLength + .05;
		}
		else
		{
		    LGKS.text(x, y, s, llist, d);
		     x += stringLength + .05;
		}

		s = "";
	}
 }

 //Method that recursively trverses the tree concantanating each node onto 
 //String s
 String traverse(GTN Root, String s, LinkedList llist, draw d, boolean first)
 {
	GTN TempChild;
	String text;
	
	if(Root != null)  
	{ 
		if(first)
		{
			Root.textInNode.reset();
			text = (String)Root.textInNode.currentElement();
			if(text.charAt(0) == '\\')
				text = text.substring(2);			

			s += (text + " {" + text);
			//LGKS.text(x, y, s, llist, d);
			//x += (.015 * (2 * text.length())) + .03;
			first = false;
		}
		else
		{
			Root.textInNode.reset();
			text = (String)Root.textInNode.currentElement();
			if(text.charAt(0) == '\\')
				text = text.substring(2);

			s += (", " + text);
			//LGKS.text(x, y, s, llist, d);
			//x += (.015 * text.length()) + .03;
		}
		TempChild = Root.Children;
		while  (TempChild != null) // Recursively get Root's children *)
		{ 
			s = traverse(TempChild, s, llist, d, first);
			TempChild = TempChild.Siblings;
		}
	}
	return s;
 }

 double xCoord(GTN Root) 
 {
	/*
	GIVEN  : The pointer to the node whose coordinates we're currently
                trying to find.
	TASK   : Find the xCoord of this node, using the principles used in
                Algorithm 3 of "Tidy Drawings of Trees", by Charles Wetherell
                and Alfred Shannon.  Note that this is a recursive procedure,
                which calls on itself at a given level to find the position of
                the children at the level below. 
	RETURN: Ultimately, the xCoord of the Root node of the tree. */
  
      
	double AccumXCoords, ChildAvgPos,Tempx, Holder8087;
	int ChildCount;
	GTN TempChild; 

 	if  (Root == null) 
	{
		return(0.0);
	}
	else 
	{
		CurrLevel=CurrLevel+1;
		TempChild=Root.Children;
		ChildCount=0;
		AccumXCoords=0;
		while  (TempChild != null) 
		{
			ChildCount=ChildCount+1;
			Holder8087=xCoord(TempChild);
			AccumXCoords=AccumXCoords+Holder8087;
			TempChild=TempChild.Siblings;
		}
		CurrLevel=CurrLevel-1;
		if (ChildCount==0) 
			ChildAvgPos=0.0;
		else
			ChildAvgPos=AccumXCoords/((double)ChildCount);
		if (NextPos[CurrLevel]>ChildAvgPos)
			Tempx=NextPos[CurrLevel];
		else
			Tempx=ChildAvgPos;   // The average of node's children's positions *)
		if (Root.Children == null) 
		{
			Root.GModifier=0.0;
			Root.Gx=Tempx;
		}
		else 
		{
			Modifier[CurrLevel]=Math.max(Modifier[CurrLevel],Tempx-ChildAvgPos);
			Root.GModifier=Modifier[CurrLevel];
			Root.Gx=Modifier[CurrLevel]+ChildAvgPos;
		}
		Root.Gy=Starty-(Root.Glevel*(yspacing*Lenx));
		NextPos[CurrLevel]=Root.Gx+(xspacing*Lenx);
		
		return(Root.Gx);
	}
 }

 private void ApplyModifier(GTN Root) 
 {

	/*
	GIVEN  : The node to which to apply the accumulated modifier.
	TASK   : Make recursive calls to accumulate the modifier sum up the tree,
                filling in each individual node's modifier along the way. */

	GTN TempChild;

	if (Root != null)  
	{
		Root.Gx=Root.Gx+ModifierSum;
		if(minXCord > Root.Gx)
		{
			minXCord = Root.Gx;
		}
		else if(maxXCord < Root.Gx)
		{
			maxXCord = Root.Gx;
		}
		ModifierSum=ModifierSum+Root.GModifier;
		TempChild=Root.Children;
		while  (TempChild != null) 
		{
			ApplyModifier(TempChild);
			TempChild=TempChild.Siblings;
		}
		ModifierSum=ModifierSum-Root.GModifier;
		Root.GModifier = 0.0;
	}
 }


 private void  drawWalk(GTN Root, LinkedList llist , draw d)   
 {

	/*
	GIVEN  : The root of the binary tree to draw (whose node's x and y coordinates are now
                determined).
	TASK   : Do a pre-order traversal of the tree, drawing each node, along with proper
                connectors, as each node is visited. */

	GTN TempChild, TempChild2;

	if  (Root != null)  
	{ 
		drawCircNode(Root.Gx, Root.Gy,Lenx/2,Root.textInNode,llist,d);
		TempChild=Root.Children;
		while  (TempChild != null)  // Connect this root to its children *)
		{  
			drawConnectingLine(Root.Gx+CenterShift,Root.Gy,TempChild.Gx+CenterShift,TempChild.Gy,Lenx/2.0,false,Black_Color, llist,d);
			TempChild=TempChild.Siblings;
		}
		TempChild2=Root.Children;
		while  (TempChild2 != null) // Recursively draw Root's children *)
		{ 
			drawWalk(TempChild2, llist, d);
			TempChild2=TempChild2.Siblings;
		}
	}
 }
	
 public void loadStructure (StringTokenizer st, LinkedList llist, draw d)throws VisualizerLoadException  
 { 
	String s;
	GTN gtn = new GTN();
	Dne = false;                   // For handling EOSS in the recursive procedure *)
	
	s = st.nextToken();
	if(!(st.hasMoreTokens()))
		throw (new VisualizerLoadException("Encountered Bad Data When Expecting number of total nodes"));
	nn = Format.atoi(s);
	numNodes = nn;

	s = st.nextToken();
	if(!(st.hasMoreTokens()))
		throw (new VisualizerLoadException("Encountered Bad Data When Expecting number of total nodes"));
	path = Format.atoi(s);

	s = st.nextToken();
	if(!(st.hasMoreTokens()))
		throw (new VisualizerLoadException("Encountered Bad Data When Expecting number of total nodes"));
	weight = Format.atoi(s);

	s = st.nextToken();
	if(!(st.hasMoreTokens()))
		throw (new VisualizerLoadException("Encountered Bad Data When Expecting number of total nodes"));
	badCommand = Format.atoi(s);

	s = st.nextToken();
	if(!(s.equals(newTree)))
		throw (new VisualizerLoadException("Encountered Bad Data When Expecting New Tree Delimeter"));

	while(!Dne && numNodes > 0)
	{
		if(st.hasMoreTokens())
			s = st.nextToken();
		else
			s = "-1";
		
		try 
		{
			gtn = getGTNode (st, s, linespernode, llist, d);
			numNodes--;
		}
		catch ( EndOfSnapException e ) 
		{
			Dne = true;
		}
		if (!Dne) 
		{
			buildGeneralTree(st, gtn, llist,d); // Build Tree *)
			nodelist.append(gtn);
			Dne = false;
		}
	}

	//loop that gets the final info values from the file
	for(int x = 0; x < 4; x++)
	{
		if (st.hasMoreTokens()) 
			s = st.nextToken();
	        else 
			throw (new VisualizerLoadException ("End of data when expecting info"));

		if(x == 0)
			cpf = s;
		else if(x == 1)
			cpu = s;
		else if(x == 2)
			command = s;
	}
 }


 public void buildGeneralTree (StringTokenizer st, GTN PresentNode, LinkedList llist, draw d) throws VisualizerLoadException  
 { 
	String s;
	GTN LastChild;
	
	if(st.hasMoreTokens() && numNodes > 0)
	{
		s = st.nextToken();	

		if(!(s.equals(newTree)) && !(s.equals(EndSnapShot)))	
		{	
			try 
			{
				NextNode = getGTNode(st, s, linespernode, llist,d); 
				numNodes--;
			}
			catch ( EndOfSnapException e ) 
			{
				Dne = true;
			}
			LastChild = NextNode;  
			while (!Dne && (NextNode.Glevel > PresentNode.Glevel)) 
			{
				// We must insert NextNode as the LastChild of the PresentNode...*)
				if (PresentNode.Children == null) // Special case *)
					PresentNode.Children=NextNode;
				else
					LastChild.Siblings=NextNode;
				LastChild=NextNode;
				buildGeneralTree(st, NextNode,llist,d);
			}
		}
		else
		{
			Dne = true;
		}
	}
	else
		Dne = true;
 }

 public GTN getGTNode (StringTokenizer st, String s, int linesPerNode, LinkedList llist, draw d) throws EndOfSnapException, VisualizerLoadException 
 {	
	GTN gtn = new GTN();	

	if(s.equals("-1"))
		throw (new EndOfSnapException("End of Snap Shot Reached"));

	gtn.Glevel = Format.atoi(s);
	gtn.textInNode = getTextNode(st, linesPerNode, llist,d);
	
	return(gtn);
 }
}

class GTN 
{
 int Glevel;
 double Gx, Gy, GModifier;
 GTN Siblings,Children;
 LinkedList textInNode; 

 public GTN () 
 {
	textInNode = new LinkedList();
	Siblings = null;
	Children = null;
 }
}
