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
//Class that draws an array and a binary tree for use with heap animations
//and its got comenting too!(imagine that)

package gaigs2;
import java.io.*;
import java.awt.*;
import java.util.*;

public class Heap extends HeapStuff 
{
 int Numrows, Numcols, movements, comparsions;
 double Maxlabelwidth;
 String status;
 String [] values;
 
 double xspacing,yspacing;
 boolean  Dne;
 BTN NextNode;
 int CurrLevel;
 double ModifierSum;
 double NextPos [], Modifier[];
 double CenterShift, xMin, xMax, yMin, yMax;

 public Heap() 
 {
	super();
	Maxlabelwidth = 0;
	xspacing = 2.0;
	yspacing = 1.5;
	CurrLevel = 0;
	ModifierSum = 0.0;
	NextPos = new double [MaxLevels];
	Modifier = new double [MaxLevels];
 }

 //this sets the dimensions for the array
 public void calcDimsAndStartPts(LinkedList llist, draw d) 
 {
	double TitleToSSGap, MinGt, MaxGt, Diam, MinTitlex, MaxTitlex;
	int NumNodes, NumLines;

	super.calcDimsAndStartPts(llist,d);
	
	//define side lengths
	Lenx = (Maxstringlength + Textheight) - .025;

	if (linespernode == 1) 
		Leny = Textheight * 1.30;
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
	Startx = (CenterScreen -(0.5 * snapwidth) + Maxlabelwidth + (0.5 * Textheight)) - .4;
	Starty = Topy-IconHeight-IconToTitleGap-(NumLines*Titleheight)-((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0) + .05;
	
	//define starting y cord for title
	TitleStarty = (Ycenter + .43);
 }

 boolean emptyStruct() 
 {
	return(false);
 }
 
 //Even though the information loaded here is stored in an ArrayNode it is used to draw both structures
 void loadStructure (StringTokenizer st, LinkedList llist, draw d) throws  VisualizerLoadException  
 {
	boolean done = false; 
	ArrayNode an = new ArrayNode();
	BTN btn = new BTN();
	String s;
	
	Dne=false;                   // For handling EOSS in the recursive procedure *)

	Xcenter=CenterScreen; // These Snapshots are always centered *)
	Ycenter=CenterScreen; 

	if (st.hasMoreTokens())
		s = st.nextToken();
	else
		throw (new VisualizerLoadException ("Reached end when expecting number of rows"));
	
	Numrows = Format.atoi(s);	
	values = new String [Numrows];	

	for(int x = 0; x < Numrows; x++)
	{
		if (st.hasMoreTokens())
			s = st.nextToken();
		else
			throw (new VisualizerLoadException ("Reached end when expecting value"));
		
		values[x] = s;
		
		an = getArrayNode(values[x], x, llist, d);
		nodelist.append(an);
	}
	
	//Scale drawings for more then 10 items
	if(Numrows >= 10)
		xspacing -= (Numrows - 9) * .1;
	try
	{
		btn = getBTNode (values[0], 0, 'L', 0, linespernode, llist, d);	
	}
	catch ( EndOfSnapException e )
	{ 
		Dne = true;
	}
	if (!Dne) 
	{
		buildBinaryTree(0, 0, btn, llist, d); // Build Tree *)
		nodelist.append(btn);
	}

	//loop that get the final values from the file
	for(int x = 0; x < 4; x++)
	{
		if (st.hasMoreTokens()) 
			s= st.nextToken();
	        else 
			throw (new VisualizerLoadException ("End of data when expecting data"));
		
		if(x == 0)
			movements = Format.atoi(s);
		else if(x == 1)
			comparsions = Format.atoi(s);
		else if(x == 2)
			status = s;
	}
 }

 public BTN getBTNode (String value, int level, char child, int in, int linesPerNode, LinkedList llist, draw d) throws EndOfSnapException, VisualizerLoadException 
 {	
	String s;
	BTN btn; 
	Integer a;

	btn = new BTN();

	btn.Blevel = level;
	
	btn.Childtype = child;

	a = new Integer(in);
	btn.index = a.toString();

	btn.textInNode.append(value);
	return(btn);
 }

 //Grabs all the information for one item in the array from a BTN.  That is a row label, column lable 	
 //and node info.  That information is then stored in an ArrayNode
 private ArrayNode getArrayNode(String value, int x, LinkedList llist, draw d)
 {
	ArrayNode an;
	Integer a;

	an = new ArrayNode();
	
	//get row #
	a = new Integer(x);
	an.row = a.toString();
	
	//should only ever have 1 column
	an.col = "1";
	
	//get info in node
	an.textInNode.append(value);
	return(an);
 }

 //Here we draw our structures
 void drawStructure (LinkedList llist, draw d)
 {
 	
	
	double xline[], yline[];
	String text;
	
	super.drawStructure(llist,d);
        xline = new double [2];
        yline = new double [2];
        yline[0]=TitleEndy - .05;
        yline[1]=yline[0];
        xline[0]= 0;
        xline[1]= 1;
        // The polyline is drawn immediately under the title/caption 
	GKS.polyline(2,xline,yline,llist,d);

	//start array draw

	ArrayNode an;
	int  rows;
        double SRx,SRy,PermStarty;

    	if (emptyStruct()) 
	{
	    super.drawStructure(llist,d);  // to handle empty structure
	    return;
        }
	
	if(Numrows > 10)
		Leny = .6/Numrows;	


        //  Must initialize row and column label counters.  *)
        SRx = Startx - (0.5 * Textheight);
        SRy = Starty  /*-(0.5*Leny)*/;
	nodelist.reset();
	
        PermStarty = Starty;
       	
	drawTop(Startx,Starty,llist,d);
	GKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);

	for (rows = 0; rows < Numrows; rows++) 
	{
		an = (ArrayNode) nodelist.nextElement();
		// Must draw Row labels *)
		GKS.set_text_align(TA_RIGHT,TA_TOP,llist,d);
		GKS.text(SRx, SRy, an.row, llist,d);
		SRy=SRy-Leny;
		
		GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
		drawRectNode(an.textInNode,llist,d);
	}
	Startx=Startx+Lenx;
	Starty=PermStarty;
	

        //  Now that the array is drawn, we must draw the 3-dimensional  *)
        //  side of the array...                                         *)
        for (rows = 1; rows <= Numrows; rows++) 
	{
		drawSide(Startx,Starty,llist,d);
		Starty=Starty-Leny;
	}
	//end array draw

	//start tree draw

	double TitleToSSGap,MinGt,MaxGt,Diam,MinTitlex,MaxTitlex;
      	int NumNodes,NumLines;


	super.calcDimsAndStartPts(llist,d);
	// With circular nodes, not adding Textheight works better to finetune the circle size
	Lenx=Maxstringlength - .01;//+(Textheight);
	Leny=((linespernode+1)*Textheight)+((linespernode-1)*(0.5*Textheight));
	TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
	TDy=TDx/Math.sqrt(3);
	TitleToSSGap=2*Titleheight;
	NumLines=title.size();
	Startx=CenterScreen;
	Starty=Topy-IconHeight-IconToTitleGap-(NumLines*Titleheight)-((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0) + .05;
	TitleStarty=Topy-IconHeight-IconToTitleGap-Titleheight;

	int x;
	double TempLoc;
	BTN btn = new BTN();

	if (emptyStruct()) 
	{
		super.drawStructure(llist,d);  // to handle empty structure
		return;
        }
	for (x = 0; x < MaxLevels; x++) 
	{
		Modifier[x] = 0.0;
		NextPos[x] = TreeSideBorder+(Lenx/2.0); // The rightmost position at which we can plot a node *)
	}

	btn = (BTN)nodelist.nextElement();
	
	TempLoc = xCoord(btn); 
	ApplyModifier(btn);
	CenterShift = (CenterScreen-btn.Bx) + .16;
	// The factor needed to shift the tree to the center *)
	xMin = CenterScreen - (0.5 * Maxtitlelength);
	xMax = CenterScreen + (0.5 * Maxtitlelength);
	yMax = Topy - IconHeight - IconToTitleGap;
	yMin = yMax;
	drawWalk(btn, llist, d);
	Xcenter = (xMin + xMax) / 2.0;
	Ycenter = (yMin + yMax) / 2;
	snapheight = yMax - yMin;
	snapwidth = xMax - xMin;

	//end tree draw

	//Print Text info to screen
	text = "Movements: " + movements;
	GKS.text(.07, .17, text, llist, d);
	text = " Comparsions: " + comparsions;
	GKS.text(.07, .12, text, llist, d);
	text = "Status info";
	GKS.text(.8, .17, text, llist, d);	
	GKS.text(.8, .12, status, llist, d);
 }


 double xCoord(BTN Root) 
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
  
	double Leftx,Rightx,Tempx,Avg;

	if  (Root == null) 
		return(0.0);
	else 
	{
		CurrLevel=CurrLevel+1;
		Leftx=xCoord(Root.Lchild);
		Rightx=xCoord(Root.Rchild);
		if ((Root.Lchild == null) && (Root.Rchild != null)) 
			Leftx=Rightx-(xspacing*Lenx);
		else if ((Root.Rchild == null) && (Root.Lchild != null))
			Rightx=Leftx+(xspacing*Lenx);
		CurrLevel=CurrLevel-1;
		Avg=(Rightx+Leftx)/2.0;
 		if (NextPos[CurrLevel]>Avg)
			Tempx=NextPos[CurrLevel];
		else
		Tempx=Avg;           // The average of node's children's positions *)
		if ((Root.Lchild == null) && (Root.Rchild==null))   // We have a leaf node *)
		{ 
			Root.BModifier=0.0;
			Root.Bx=Tempx;
			// Note that Modifier for current level will not change *)
		}
		else 
		{
			// Only non-leaf nodes can make Modifier change *)
			Modifier[CurrLevel]=Math.max(Modifier[CurrLevel],Tempx-Avg);
			Root.BModifier=Modifier[CurrLevel];
			Root.Bx=Modifier[CurrLevel]+Avg;
		}
		Root.By=Starty-(Root.Blevel*(yspacing*Lenx));
		NextPos[CurrLevel]=Root.Bx+(xspacing*Lenx);
		return(Root.Bx);
	}
 }

 //This maybe scales the tree?
 private void ApplyModifier(BTN Root) 
 {
	/*
	GIVEN  : The node to which to apply the accumulated modifier.
	TASK   : Make recursive calls to accumulate the modifier sum up the tree,
                filling in each individual node's modifier along the way. */

	if (Root != null)  
	{
		Root.Bx=Root.Bx+ModifierSum;
		ModifierSum=ModifierSum+Root.BModifier;
		ApplyModifier(Root.Lchild);
		ApplyModifier(Root.Rchild);
		ModifierSum=ModifierSum-Root.BModifier;
	}
 }

 private void  drawWalk(BTN Root, LinkedList llist , draw d)   
 {
	/*GIVEN  : The root of the binary tree to draw (whose node's x and y coordinates are now determined).
	TASK   : Do a pre-order traversal of the tree, drawing each node, along with proper connectors, as each node is visited. */

	// From drawstruct

	if  (Root != null)  
	{ 
		drawCircNode(Root.Bx+CenterShift,Root.By,Lenx/2,Root.textInNode,Root.index,llist,d);
		if (Root.Bx+CenterShift+(0.5*Lenx)>xMax)
			xMax=Root.Bx+CenterShift+(0.5*Lenx);
		else if  (Root.Bx+CenterShift-(0.5*Lenx)<xMin) 
			xMin=Root.Bx+CenterShift-(0.5*Lenx);
		if (Root.By-(0.5*Lenx)<yMin) 
			yMin=Root.By-(0.5*Lenx);
		if (Root.Lchild != null)  // Connect Left Child *)
			drawConnectingLine(Root.Bx+CenterShift, Root.By, Root.Lchild.Bx+CenterShift, Root.Lchild.By, Lenx/2.0, Black_Color, llist, d);
		if (Root.Rchild != null) // Connect Right Child *)
			drawConnectingLine(Root.Bx+CenterShift, Root.By, Root.Rchild.Bx+CenterShift, Root.Rchild.By, Lenx/2.0, Black_Color, llist,d);
		drawWalk(Root.Lchild,llist,d);
		drawWalk(Root.Rchild,llist,d);
	}
 }

 public void buildBinaryTree (int root, int level, BTN PresentNode, LinkedList llist, draw d) throws VisualizerLoadException  
 { 
	if(((root*2) + 1) < Numrows) 
	{
		try
		{
			NextNode = getBTNode(values[(root*2) + 1], (level + 1), 'L', ((root*2) + 1), linespernode, llist,d);
		}
		catch ( EndOfSnapException e )
		{
			throw (new VisualizerLoadException("Encountered end of data when expected tree level"));
		}
		PresentNode.Lchild=NextNode;
		buildBinaryTree((root*2) + 1, level + 1, PresentNode.Lchild, llist, d);
	}
	if(((root*2) + 2) < Numrows) 
	{
		try
		{
			NextNode = getBTNode(values[(root*2) + 2], (level + 1), 'R', ((root*2) + 2), linespernode, llist,d);
		}
		catch ( EndOfSnapException e )
		{
			throw (new VisualizerLoadException("Encountered end of data when expected tree level"));
		}
		PresentNode.Rchild=NextNode;
   		buildBinaryTree(((root*2) + 2), (level + 1), PresentNode.Rchild, llist, d);   
	}
 }
}

class BTN 
{
 int Blevel;
 char Childtype;
 double Bx, By, BModifier;
 BTN Lchild,Rchild;
 LinkedList textInNode;
 String index; 

 public BTN () 
 {
	textInNode = new LinkedList();
	Lchild = null;
	Rchild = null;
 }
}


