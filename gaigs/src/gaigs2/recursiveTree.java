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
This structure draws a tree with each node being an array.  Nodes can be different sizes
and the tree will automatically be drawn with close to minimum spacing.  This structure 
was created with the intention of using it to show the divide and conquer sorting algorithms.

    Special delimiters used in drawing array values.
    ! = do not draw node
    \S = gray out this node
    \A = Draw an arrow under node
    \Ttext: = Draws any text under node
    `~ = draw an empty node
*/

package gaigs2;
import java.io.*;
import java.awt.*;
import java.util.*;
import gaigs2.*;

public class recursiveTree extends HeapStuff{
    
    double xspacing, yspacing;
    Tree tree;
    // one-time only variables needed in implementation of various operations
    boolean  Dne;   // TO determine when Tree has been built
    TreeNode NextNode;
    double CenterShift, xMin,xMax, yMin,yMax;
    // End one-time only variables
    String info;

    public recursiveTree() {
	tree = new Tree(); 
	tree.setVertical();
    }
    
    public boolean emptyStruct()  {
        if (nodelist.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
 

    public void calcDimsAndStartPts(LinkedList llist, draw d)  {
        /*  Determines the following variables Lenx, Leny, Startx, Starty, TitleStarty   */
        // Task: Calculate the length and height of a node and its starting point.
        //   It also computes the y-coordinate of the title
        double TitleToSSGap,
        MinGt,MaxGt,Diam,
        MinTitlex,MaxTitlex;
        int NumNodes,NumLines;
        
        super.calcDimsAndStartPts(llist,d);

        TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
        TDy=TDx/Math.sqrt(3);
        TitleToSSGap=2*Titleheight;
        NumLines=title.size();
        Startx=CenterScreen;
        Starty=Topy-IconHeight-IconToTitleGap-(NumLines*Titleheight)-
        ((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0);
        TitleStarty=Topy-IconHeight-IconToTitleGap-Titleheight;
    }
    
    //This method initiates the tree drawing process.  The tree is drawn by using the 
    //coordinates calculated by the Tree class.
    void drawStructure(LinkedList llist, draw d)  {
        int x;
        double TempLoc;  

        double [] xline = new double [2];
        double [] yline = new double [2];
        yline[0]= Starty + .1;
        yline[1]= yline[0];
        xline[0]= 0;
        xline[1]= 1;
        // The polyline is drawn immediately under the title/caption 
	GKS.polyline(2,xline,yline,llist,d);
	
	Starty += .05;
	//draw info on the screen
	if(info.compareTo(" ") != 0)
	{
		GKS.set_text_align(0, 2, llist, d);
		GKS.text(.5, Starty, info, llist, d);
	}

	Starty = 1 - (Starty - .1);
	tree.setStartX(50 - tree.getRoot().getWidth()/2);
	tree.setStartY((int)(Starty*100));
        tree.setTree();
        if (emptyStruct()) {
            super.drawStructure(llist,d);  // to handle empty structure
            return;
        }
        drawWalk(tree.getRoot(), llist, d);
    }
    
    private void  drawWalk(TreeNode Root, LinkedList llist , draw d)   {
        
    /*
       GIVEN  : The root of the tree to draw (whose node's x and y coordinates are now
                determined).
       TASK   : Do a pre-order traversal of the tree, drawing each node, along with proper
                connectors, as each node is visited. */
        
	double xcord, ycord;
        // Used to draw edge
	double[] xLine = new double [2];
	double[] yLine = new double [2];
	Edge e;
        
        if  (Root != null && Root.getValue().compareTo("") != 0 && Root.getValue().charAt(0) != '!')  
	{
	    xcord = (((double)Root.getPosition().x)/100);
	    ycord = 1-(((double)Root.getPosition().y)/100);
	    //first draw the array
	    drawArray(xcord, ycord,Root,llist,d);
	    //If not the root draw line to the parent of this node
	    if(Root.getParent() != null)
	    {
	    	e = tree.makeEdge(Root.getParent(), Root);
		xLine[0] = (((double)e.getStartX())/100);
		xLine[1] = (((double)e.getEndX())/100);
		yLine[0] = 1-(((double)e.getStartY())/100);
		yLine[1] = 1-(((double)e.getEndY())/100);
		GKS.set_textline_color(LightGray,llist,d);
		GKS.polyline(2,xLine,yLine,llist,d);
		GKS.set_textline_color(Black,llist,d);
	    }
	    //draw this node's left child
            drawWalk(Root.getChild(),llist,d);
	    //draw this node's right child
            drawWalk(Root.getSibling(),llist,d);
	    //draw arrows and text associated with the array for this node
	    //so that it goes over top of everything else
	    drawArrowText(xcord, ycord, Root, llist,d);
        }
	else if(Root != null)
	    drawWalk(Root.getSibling(),llist,d);
    }

    //Method used to draw the array for each node.
    private void drawArray(double xmid, double ymid, TreeNode root, LinkedList llist , draw d)
    {
        double SRx,SRy,PermStarty, width;
	StringTokenizer array = new StringTokenizer(root.getValue());
	String temp = "";
	int num = array.countTokens();

    	if (emptyStruct()) 
	{
	    super.drawStructure(llist,d);  // to handle empty structure
	    return;
        }

        //  Must initialize row and column label counters.  *)
        SRx = xmid;
        SRy = ymid;
	nodelist.reset();
	
        PermStarty = ymid;
	width = (((double) root.getWidth())/100.0)/((double) num);

	for(int x = 0; x < num; x++) 
	{
		GKS.set_text_align(TA_CENTER,TA_BASELINE,llist,d);
		if(x == 0)	
		{
			GKS.set_textline_color(Blue,llist,d);
			GKS.text(xmid - .03, ymid - (Leny / 2) - .01, root.getTag(), llist,d);
			GKS.set_textline_color(Black,llist,d);
		}
		temp = array.nextToken();
		if(width <= Lenx)
		{			
			drawRectNode(temp ,xmid,ymid,llist,d);
			drawTop(xmid,ymid,llist,d);
			xmid+=Lenx;
		}
		else
		{
			drawRectNode(temp ,xmid,ymid,width,llist,d);
			drawTop(xmid,ymid,width,llist,d);
			xmid+=width;
		}
	}
       	drawSide(xmid,ymid,llist,d);
    }

    //Method that draws an arrow under a specific array value if specified.  
    //If arrow drawn return true, else return false.
    public void drawArrowText(double xcord, double ycord, TreeNode root, LinkedList llist , draw d)
    {
	StringTokenizer array = new StringTokenizer(root.getValue());
	String temp = "", text = "";
	int num = array.countTokens();
	double width = (((double) root.getWidth())/100.0)/((double) num);

	for(int x = 0; x < num; x++) 
	{
	    temp = array.nextToken();
	    if (temp.charAt(0)==Delim && temp.charAt(1) == 'A') 
	    {
		temp = temp.substring(2);
		if(width <= Lenx)
		{
			drawGraphNetArrow(xcord + (Lenx/2), ycord, xcord + (Lenx/2), ycord - Leny, 'B', llist, d);
			if (temp.charAt(0)==Delim && temp.charAt(1) == 'T') 
	    		{
				text = temp.substring(2, temp.indexOf(":"));
				GKS.set_textline_color(Blue,llist,d);
				GKS.text(xcord + (Lenx/2) - .005, ycord - Leny - .01, text, llist,d);
			}	
		}
		else
		{
			drawGraphNetArrow(xcord + (width /2), ycord, xcord + (width /2), ycord - Leny, 'B', llist, d);
			if (temp.charAt(0)==Delim && temp.charAt(1) == 'T') 
	    		{
				text = temp.substring(2, temp.indexOf(":"));
				GKS.set_textline_color(Blue,llist,d);
				GKS.text(xcord + (width /2) - .005, ycord - Leny - .01, text, llist,d);
			}
		}
	    }
	    else if(temp.charAt(0)==Delim && temp.charAt(1) == 'T')
	    {
		text = temp.substring(2, temp.indexOf(":"));
		if(width <= Lenx)
		{
			GKS.set_textline_color(Blue,llist,d);
			GKS.text(xcord + (Lenx/2) - .005, ycord - Leny, text, llist,d);		
		}
		else
		{
			GKS.set_textline_color(Blue,llist,d);
			GKS.text(xcord + (width /2) - .005, ycord - Leny, text, llist,d);
		}	    	
	    }
	    if(width <= Lenx)
	    	xcord+=Lenx;
	    else
		xcord+=width;
	}
	GKS.set_textline_color(Black,llist,d);
    }
    
    //Loads the root then calls recursive method buildTree that loads the rest of the tree.
    public void loadStructure(StringTokenizer st, LinkedList llist, draw d)throws VisualizerLoadException  
    {   
        Lenx=.06;
        Leny=.06;

	//read in root
        try {
            NextNode = getNode(st, null, llist,d);
            tree.setTree();
	    nodelist.append(NextNode);
        }
        catch ( EndOfSnapException e ) {
            Dne = true;
        }
	buildTree(st, tree.getRoot(), llist,d); // Build Tree *)
    }

    //Builds the tree by reading in the nodes in pre-order.  PresentNode is the current
    //parent.
    public void buildTree(StringTokenizer st, TreeNode PresentNode, LinkedList llist, draw d)
    throws VisualizerLoadException  {
        try {
            NextNode = getNode(st, PresentNode, llist,d);
        }
        catch ( EndOfSnapException e ) {
            Dne = true;
        }
        if (!Dne) {
            if ((NextNode.getID()>PresentNode.getID()) && NextNode.isLeftChild())
	    {
                PresentNode.insertLeftChild(NextNode);
		NextNode.setParent(PresentNode);
           	tree.setTree();
                buildTree(st, PresentNode.getChild(),llist,d);
            }
        }
        if (!Dne) {  // The prior recursive call could set Dne to true
            if (NextNode.getID()>PresentNode.getID() && NextNode.isRightChild())	
            {
                PresentNode.insertRightChild(NextNode);
		NextNode.setParent(PresentNode);
           	tree.setTree();
                buildTree(st, PresentNode.getChild().getSibling(),llist,d);
            }
        }    
    }
    
    //Reads in all the information associated with each node and calculates the needed
    //for the boxes in each array.
    //Special delimiters used in drawing array values.
    //! = do not draw node
    //\S = gray out this node
    //\A = Draw an arrow under node
    //\Ttext: = Draws any text under node
    //`~ = draw an empty node
    public TreeNode getNode(StringTokenizer st, TreeNode currentParent, LinkedList llist, draw d) throws EndOfSnapException, VisualizerLoadException {
        
	int size, level, longest = 0;
	char childtype;
        String s, con, tag = "";
        TreeNode tn; 

	//read info
	if(currentParent == null)
	{
		//read movements
		if (st.hasMoreTokens()){
 	           s = st.nextToken();
	            if (s.compareTo(EndSnapShot) == 0)
	                throw (new EndOfSnapException());
 	       	}
	       	else throw (new VisualizerLoadException("Encountered end of data when expected number of movements"));
		info = s;
	}

	//read tag
	if (st.hasMoreTokens()){
 	   s = st.nextToken();
	      if (s.compareTo(EndSnapShot) == 0)
	         throw (new EndOfSnapException());
 	}
	else throw (new VisualizerLoadException("Encountered end of data when expected number of movements"));
	tag = s;

	//read size
	if (st.hasMoreTokens()){
            s = st.nextToken();
            if (s.compareTo(EndSnapShot) == 0)
                throw (new EndOfSnapException());
        }
        else throw (new VisualizerLoadException("Encountered end of data when expected node size"));
	size = Integer.parseInt(s);

	//read level
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            if (s.compareTo(EndSnapShot) == 0)
                throw (new EndOfSnapException());
        }
        else throw (new VisualizerLoadException("Encountered end of data when expected tree level"));
        level = Integer.parseInt(s);

	//read in child type
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            if (s.compareTo(EndSnapShot) == 0)
                throw (new EndOfSnapException());
        }
        else throw (new VisualizerLoadException("Encountered end of data when expected R or L"));
        s = s.trim().toUpperCase();
        if (s.charAt(0) != 'R' &&  s.charAt(0) != 'L')
            throw (new VisualizerLoadException("Encountered " + s + " when expecting R or L child type"));
        childtype = s.charAt(0);
	
	//read in first element in the array at this node
	if (st.hasMoreTokens())
		s = st.nextToken();
	else
		throw (new VisualizerLoadException ("Reached end when expecting value"));
	con = s;

	//Strip tags off the front of string to find out how long it will be when drawn
	String temp = s;
	if(temp.indexOf("!") != -1)
		temp = temp.substring(temp.lastIndexOf("!") + 1);
	if(temp.indexOf("\\") != -1)
		temp = temp.substring(temp.lastIndexOf("\\") + 2);
	if(temp.indexOf(":") != -1)
		temp = temp.substring(temp.lastIndexOf(":") + 1);

	if(temp.length() > longest)
		longest = temp.length();

	//read in the rest
	for(int x = 1; x < size; x++)
	{
		if (st.hasMoreTokens())
			s = st.nextToken();
		else
			throw (new VisualizerLoadException ("Reached end when expecting value"));
		temp = s;

		//Strip tags off the front of string to find out how long it will be when drawn
		if(temp.indexOf("!") != -1)
			temp = temp.substring(temp.lastIndexOf("!") + 1);
		if(temp.indexOf("\\") != -1)
			temp = temp.substring(temp.lastIndexOf("\\") + 2);
		if(temp.indexOf(":") != -1)
			temp = temp.substring(temp.lastIndexOf(":") + 1);

		if(temp.length() > longest)
			longest = temp.length();

		con += " " + s;
	}
	
	if(longest < 3)	
		tn = new TreeNode(con, currentParent, null, null, (int)((Lenx*size)*100), (int)(Leny*100)+5, 4, level, tag);
	else
		tn = new TreeNode(con, currentParent, null, null, (int)(((.025 * longest)*size)*100), (int)(Leny*100)+5, 4, level, tag);

	if(childtype == 'L')
		tn.setLeftChild(true);
	else if (childtype == 'R')
		tn.setRightChild(true);

	if(currentParent == null)
		tree.setRoot(tn);

        return(tn);
    }
}