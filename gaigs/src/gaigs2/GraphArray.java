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

// GraphArray - liberal code cloning from MD_Array.java, Graph_Network.java 
// and NonLinearStructure.java (because can't inherit from multiple classes)
// Richard Teviotdale - 8/9/04

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


public class GraphArray extends LinearList {

    // stuff from nonLinear
    protected int HSelfConnectedNodeTextAlign;
    protected int VSelfConnectedNodeTextAlign;
    double xlab, ylab;
    /*
    protected double Lenx;
    protected double Leny;
    protected double Startx;
    protected double Starty;
    protected double TDx, TDy;
    */
    protected static double TreeSideBorder = 0.05;

	// graph_network stuff
    boolean itIsGraph;
    int numnodes;
    boolean Coordsknown;
    Vector ConnArray;   // essentially an array of Graph_NetworkNodes

    // back to MD_Array Stuff
    int Numrows, Numcols;
    double Maxlabelwidth;

    // MD_Array functions follow ====================================
    // ==============================================================
    public GraphArray () {
	super();
	Maxlabelwidth = 0.0;


	// from Graph_Network constructor
    	itIsGraph = false;
        numnodes = 0;
        ConnArray = new Vector(5,5);
    }


    boolean emptyStruct() {
  
	return nodelist.size() == 0; 
    }


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

	// now do the graph stuff
        loadGraph(st, llist, d);

    }

    private ArrayNode getArrayNode(StringTokenizer st, LinkedList llist, draw d)  throws 
	EndOfSnapException, VisualizerLoadException {
	
	ArrayNode an;
	String tstr;
	int temp;
	double check;

	an = new ArrayNode();
	if (st.hasMoreTokens())
	    an.row = st.nextToken();
	else
	    throw (new VisualizerLoadException ("Reached end when expecting row label"));
	//if (an.row.compareTo(EndSnapShot) == 0)
	if (an.row.compareTo(new String("***END_ARRAY***")) == 0)
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


    public void calcDimsAndStartPts(LinkedList llist, draw d) {

	double TitleToSSGap,
	    MinGt,MaxGt,Diam,
	    MinTitlex,MaxTitlex;
	int NumNodes,NumLines;

	super.calcDimsAndStartPts(llist,d);	
	Lenx = Maxstringlength; // + Textheight/*0.015*/ /*0.015 was once "Textheight"*/;
	if (linespernode==1) 
	    Leny=Textheight*1.50;
	else
	    Leny=(linespernode*Textheight)+
		((linespernode-1)*(0.50*Textheight));
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
	Startx=/*CenterScreen*/ -0.04; //-(0.5*snapwidth)+Maxlabelwidth+
            //(0.5*Textheight);
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
	    GKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
	    if (Numcols>1)          //  We can draw column labels...  *)
		GKS.text(Startx+(CenterScreen*Lenx),Starty+TDy
			 +(0.5*Textheight),an.col,llist,d);
	    for (rows = 1; rows <= Numrows; rows++) {
		if (cols==1)  { // Must draw Row labels *)
		    GKS.set_text_align(TA_RIGHT,TA_TOP,llist,d);
		    GKS.text(SRx,SRy,an.row,llist,d);
		    SRy=SRy-Leny;
		}
		GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
		LinkedList temp = new LinkedList();
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

	// print debug info to file
	//drawDebugFile("/tmp/debug");
	// now draw the graph part
    	drawGraph(llist, d);
 
    }

    
    // Graph_Network functions follow ===============================
    // ==============================================================
    
    /* probably don't want the constuctor as using the one above from MD_Array
    public Graph_Network(String whichType)  {
        
        if (whichType.compareTo("GRAPH") == 0)
            itIsGraph = true;
        else
            itIsGraph = false;
        numnodes = 0;
        ConnArray = new Vector(5,5);
    }
    */
    
    
    /*  Given: a boolean to tell between a Graph and Network)
        Task: Builds List of Connected Nodes to put in the ConnectedNodes
        field of a the Graph_NetworkNode object */
    private LinkedList GetCon(StringTokenizer st, boolean loadingGraph)
    throws VisualizerLoadException {
        
        // Iterate through connected nodes for current node, appending to list
        // of connected nodes until EndNode is reached
        
        ConnNodeRec CNPT;
        String s;
        LinkedList cn = new LinkedList();
        
        if (st.hasMoreTokens())
            s = st.nextToken();
        else
            throw (new VisualizerLoadException("Expecting connected node number but encountered end"));
        while (s.trim().compareTo("32767") != 0) {   // haven't yet reached EndNode
            CNPT = ExtractArrowAndColor(s);
            if (!loadingGraph)    {  // then get edgeweight
                if (st.hasMoreTokens())
                    s = st.nextToken();
                else
                    throw (new VisualizerLoadException("Expecting edgeweight but encountered end"));
                CNPT.EdgeWeight = s.trim();
            }
            cn.append(CNPT);
            if (st.hasMoreTokens())
                s = st.nextToken();
            else
                throw (new VisualizerLoadException("Expecting connected node number but encountered end"));
        }
        return(cn);
    }
    
     /* GIVEN  : The Text Line of the current label to draw.
        TASK   : Extract the the delimiters, if any.  The first potential delimeter, /A, signifies that an
                 arrow is to be drawn on this connecting line.  The second potential delimeter, /*, where * is
                 one of the valid color delimeters, signifies that this edge and label are to be colored.
        RETURN : A ConnNodeRec with appropriate values stuffed into its fields */
    private ConnNodeRec ExtractArrowAndColor(String s)  {
        
        ConnNodeRec cnr = new ConnNodeRec();  // thisConnNode, UseArrow, EColor
        int ExtPt;
        
        cnr.UseArrow=false;
        cnr.EColor=Black_Color;
        ExtPt=1;
        if  (s.length()>2) {    // We have a potential delimeter to extract... *)
            if (s.charAt(0)==Delim && s.charAt(1)==UseArrow) { // Extract (potentially) the Arrow Delimeter *)
                cnr.UseArrow=true;
                ExtPt=3;
                if (s.length()>4)  { // We have a potential color delimeter to extract... *)
                    if (s.charAt(2)==Delim && inHighlightColors(s.charAt(3)))  {
			if (s.charAt(3) != '#') {
			    cnr.EColor=s.charAt(3);
			    ExtPt=5;
			}
			else {
			    cnr.EColor=s.charAt(3);
			    cnr.HexColor=s.substring(4,10);
			    ExtPt=11;
			}
                    }
                }
            }
            else if (s.charAt(0)==Delim && inHighlightColors(s.charAt(1)))  {
		if (s.charAt(1) != '#') {
		    cnr.EColor=s.charAt(1);
		    ExtPt=3;
		}
		else {
		    cnr.EColor=s.charAt(1);
		    cnr.HexColor=s.substring(2,8);
		    ExtPt=9;
		}
            }
        }
        if (ExtPt > 1)
            cnr.thisConnNode = Format.atoi(s.substring(ExtPt-1));
        else
            cnr.thisConnNode = Format.atoi(s);
        return(cnr);
    }
    
    
    /*
    public void calcDimsAndStartPts(LinkedList llist, draw d) {
        
        double ArrowLength,NodeToArrowGap,ArrowToNextNodeSep, TitleToSSGap,MinGt,MaxGt,Diam,
        MinTitlex,MaxTitlex;
        int NumLines;
        
        super.calcDimsAndStartPts(llist, d);
        // With circular nodes, not adding Textheight works better to finetune the circle size
        Lenx=Maxstringlength; //  +  (Textheight);
        Leny=((linespernode+1)*Textheight)+
        ((linespernode-1)*(0.5*Textheight));
        TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
        TDy=TDx/Math.sqrt(3);
        TitleToSSGap=2*Titleheight;
        NumLines=title.size();
        if  (Coordsknown) {           // We have pending dimensions - modify them slightly... *)
            MinGt=Ycenter-((0.5*snapheight)+(0.5*Lenx));
            MaxGt=Ycenter+((0.5*snapheight)+(0.5*Lenx))+
            (NumLines*Titleheight)+
            ((NumLines-1)*(0.5*Titleheight))+TitleToSSGap;
            Ycenter=(MinGt+MaxGt)/2;
            snapheight=MaxGt-MinGt;
            MinTitlex=CenterScreen-(0.5*Maxtitlelength);
            MaxTitlex=CenterScreen+(0.5*Maxtitlelength);
            MinGt=Xcenter-((0.5*snapwidth)+(0.5*Lenx));
            MaxGt=Xcenter+((0.5*snapwidth)+(0.5*Lenx));
            if (MinTitlex<MinGt)
                MinGt=MinTitlex;
            if (MaxTitlex>MaxGt)
                MaxGt=MaxTitlex;
            Xcenter=(MinGt+MaxGt)/2.0;
            snapwidth=MaxGt-MinGt;
        }
        else  {	                  // coordinates aren't known, must calculate dimensions... *)
            Xcenter=CenterScreen;
            Ycenter=CenterScreen;
            Diam=Lenx+(2.0*((Lenx/2.0)/Math.sin(((2.0*Math.PI)/(2.0*((double)numnodes)))/2.0)));
            snapheight=(NumLines*Titleheight)+
            ((NumLines-1)*(0.5*Titleheight))+TitleToSSGap+
            (Lenx/2.0) +  // To account for potential self-connecting nodes *)+
            Diam;
            snapwidth=Math.max(Maxtitlelength,Diam);
            Startx=CenterScreen;
            Starty=CenterScreen+(0.5*snapheight)-(NumLines*Titleheight)-
            ((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0)-(0.5*Diam);
        }
        TitleStarty=Ycenter+(0.5*snapheight)-Titleheight;
    }
    */
    
    //public void loadStructure(StringTokenizer st, LinkedList llist, draw d)
    public void loadGraph(StringTokenizer st, LinkedList llist, draw d)
    throws VisualizerLoadException {
        
                /* Pseudocode:
                 
                        Grab the first integer label (and possible coords)
                        WHILE the label is not the EndOfSnap {
                                construct a graph_netnode
                                fill it fields with the appropriate info, using
                                                getTextnode and GetCon
                                Add it to the vector
                                Grab the next integer label
                        }
                 */
        double maxx = 0;
        double maxy = 0;
        double minx = 0;
        double miny = 0;
        String s;
        String tstr1 = null;
        String tstr2 = null;
        String tstr3 = null;
        StringTokenizer t;
        Graph_NetworkNode gnn;
        int tempint;
        double tempx, tempy;
        
        
        numnodes=0;
        if (st.hasMoreElements())
            s = st.nextToken();
        else
            throw (new VisualizerLoadException("Expecting graph integer label but encountered end"));
        if (s.compareTo(EndSnapShot) != 0) {
            t = new StringTokenizer(s, " \t");
            if (t.hasMoreElements())
                tstr1 = t.nextToken();
            else
                throw (new VisualizerLoadException("Expecting graph integer label but encountered end"));
            if (t.hasMoreElements())
                tstr2 = t.nextToken();
            else
                tstr2 = null;
            if (t.hasMoreElements())
                tstr3 = t.nextToken();
            else if (tstr2 != null)
                throw (new VisualizerLoadException("Second coordinate missing for node " + tstr1));
            if (tstr2 != null) {
                Coordsknown = true;
                minx=CenterScreen-(Maxtitlelength/2.0);
                maxx=CenterScreen+(Maxtitlelength/2.0);
                miny=1000000000.0;
                maxy= -1000000000.0;
            }
            else
                Coordsknown = false;
        }
        
        while (s.compareTo(EndSnapShot) != 0) {
            numnodes=numnodes+1;
            gnn = new Graph_NetworkNode();
            if (Coordsknown) {
                tempint = Format.atoi(tstr1);
                tempx = Format.atof(tstr2);
                tempy = Format.atof(tstr3);
                if (tempint != numnodes)
                    throw (new VisualizerLoadException("Expecting integer label " + numnodes + " but scanned " + tempint));
                if  (tempx<minx)
                    minx=tempx;
                if (tempx>maxx)
                    maxx=tempx;
                if (tempy<miny)
                    miny=tempy;
                if (tempy>maxy)
                    maxy=tempy;
                gnn.xCoord = tempx;
                gnn.yCoord = tempy;
            }
            else {  // coords not known
                tempint = Format.atoi(tstr1);
                if (tempint != numnodes)
                    throw (new VisualizerLoadException("Expecting integer label " + numnodes + " but scanned " + tempint));
            }
            // The following three lines executed whether coords known or not
            gnn.ConnectedNodes = GetCon(st, itIsGraph);
            try {
                gnn.textInNode = getTextNode(st, linespernode, llist, d);
            }
            catch ( EndOfSnapException e ) {
                throw (new VisualizerLoadException("Unexpected end of text string"));
            }
            ConnArray.addElement(gnn);
            // Now go get the next node
            if (st.hasMoreElements())
                s = st.nextToken();
            else
                throw (new VisualizerLoadException("Expecting graph integer label but encountered end"));
            tstr1 = null; tstr2 = null; tstr3 = null;
            if (s.compareTo(EndSnapShot) != 0) {
                t = new StringTokenizer(s, " \t");
                if (t.hasMoreElements())
                    tstr1 = t.nextToken();
                else
                    throw (new VisualizerLoadException("Expecting graph integer label but encountered end"));
                if (t.hasMoreElements())
                    tstr2 = t.nextToken();
                else
                    tstr2 = null;
                if (t.hasMoreElements())
                    tstr3 = t.nextToken();
                else if (tstr2 != null)
                    throw (new VisualizerLoadException("Second coordinate missing for node " + tstr1));
            }
        } // while loop
        if  (Coordsknown) { // We must calculate xcenter and ycenter right away. *)
            snapheight=maxy-miny;
            snapwidth=maxx-minx;
            Xcenter=(minx+maxx)/2.0;
            Ycenter=(miny+maxy)/2.0;
        }
    }
    
    // Richard Teviotdale
    // for debugging purposes
    public void drawDebugFile(String file)
    {
	Graph_NetworkNode thisNode;
	ConnNodeRec thisConnNodeRec;
	try
	{
		PrintWriter out = new PrintWriter(new FileWriter(file));
		for(int i = 0; i < numnodes; i++)
		{
		    thisNode = (Graph_NetworkNode) ConnArray.elementAt(i);
		    out.println("Node:\t" + i);
		    out.println("\tX:\t" + thisNode.xCoord);
		    out.println("\tY:\t" + thisNode.yCoord);
		    thisNode.textInNode.reset();
		    while(thisNode.textInNode.hasMoreElements())
		    {
		    	out.println("\tText Element:\t" + thisNode.textInNode.currentElement());
			thisNode.textInNode.nextElement();
		    }

		    thisNode.ConnectedNodes.reset();
		    while(thisNode.ConnectedNodes.hasMoreElements())
		    {
			    thisConnNodeRec = (ConnNodeRec) thisNode.ConnectedNodes.currentElement();
		    	out.println("\tConn Element:\t" + thisConnNodeRec.thisConnNode);

			thisNode.ConnectedNodes.nextElement();
		    }

		}
		out.close();
	}
	catch(IOException e)
	{
			System.out.println("Error: " + e.toString());
	}

    }
    
    //public void drawStructure(LinkedList llist, draw d){
    public void drawGraph(LinkedList llist, draw d){
        
        int CurrNode,NoC,TeC;
        ConnNodeRec CurrConnNode;
        double dy,dx, xvect,yvect,midx,midy;
        boolean SelfConnector;
        int Halign,Valign;
        Graph_NetworkNode presentNode, CurrConnNodeData;
        
        
        if (emptyStruct()) {
            super.drawStructure(llist,d);  // to handle empty structure
            return;
        }
        if (!Coordsknown)
            CalcGraphCoords();     // Find coordinates by using the 'circle-arc' method *)
        for (CurrNode=0; CurrNode < numnodes; ++CurrNode)  {
            // Draw nodes and appropriately connect them... *)
            presentNode = (Graph_NetworkNode) ConnArray.elementAt(CurrNode);
            drawCircNode(presentNode.xCoord,presentNode.yCoord,Lenx/2.0,
            presentNode.textInNode,llist,d);
            // Now we must draw the connectors to the nodes to which it is connected... *)
            presentNode.ConnectedNodes.reset();
            while (presentNode.ConnectedNodes.hasMoreElements())  { // DrawConnectors *)
                CurrConnNode = (ConnNodeRec) presentNode.ConnectedNodes.nextElement();
                CurrConnNodeData = (Graph_NetworkNode)(ConnArray.elementAt(CurrConnNode.thisConnNode-1));
                new_drawConnectingLine(presentNode.xCoord,presentNode.yCoord,
                CurrConnNodeData.xCoord,
                CurrConnNodeData.yCoord,
                Lenx/2.0,
                CurrConnNode.UseArrow,
                (new String("" + CurrConnNode.EColor)) + CurrConnNode.HexColor, llist,d);
                if (!itIsGraph) {
                    // We must write the weight at the mid-point of the connecting line... *)
                    SelfConnector=((presentNode.xCoord ==CurrConnNodeData.xCoord) &&
                    (presentNode.yCoord == CurrConnNodeData.yCoord));
                    // The perpendicular line's slope is the negative reciprocal *)
                    if (SelfConnector)  { // We need to find the point (midx, midy) at which to begin drawing text *)
                        Halign=HSelfConnectedNodeTextAlign;
                        Valign=VSelfConnectedNodeTextAlign;
                        midx=xlab;midy=ylab; // calcualted in DrawSelfConnector *)
                    }
                    else {    // Normal Connector - we must find the midpoint of the line... *)
                        midx=(presentNode.xCoord + CurrConnNodeData.xCoord)/2.0;
                        midy=(presentNode.yCoord + CurrConnNodeData.yCoord)/2.0;
                        Halign=TA_CENTER;
                        Valign=TA_BASELINE;
                    }
                    // Finally, we are ready to set the attributes and draw in the weight... *)
                    // Set Attributes back to normal... *)
                    GKS.set_text_align(Halign,Valign,llist,d);
                    // For labels on non-self-connected nodes, put the label
                    //   on a white background *)
                    // Careful -- I don't think we do this in the WWW version
                    if ((Halign==TA_CENTER) && (Valign==TA_BASELINE))
                        GKS.set_fill_int_style(bsClear,White,llist,d);
                    // GetHighlightColors(CurrConnNode.EColor,NoC,TeC);
                    DrawWhiteBoxForLabel(midx, midy,CurrConnNode.EdgeWeight, llist,d);
                    NoC = new_extractColor((new String("" + CurrConnNode.EColor)) + CurrConnNode.HexColor);
                    TeC = new_extractTextColorForHighlightedNodes((new String("" + CurrConnNode.EColor)) + CurrConnNode.HexColor);
                    GKS.set_textline_color(NoC,llist,d);
                    
                    GKS.text(midx,midy,CurrConnNode.EdgeWeight,llist,d);
                    // Set Attributes back to normal... *)
                    GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
                    GKS.set_textline_color(Black,llist,d);
                }	// if
            }	 // while
        }	// for
        
        
    }
   
	/*
    public boolean emptyStruct()   {
        return (numnodes == 0);
    }
    */
    
    
    /* GIVEN  : The need to find the coordinates of each node in the graph.
        TASK   : Assign each node a set of coordinates by arranging them in a circle whose circumference
                 is 2 times the number of nodes.
        RETURN : Each nodes xCoord and yCoord fields appropriately assigned. */
    private void CalcGraphCoords() {
        
        double radius,Angle;
        int count;
        Graph_NetworkNode gnn;
        
        Angle=(2.0*Math.PI)/(2.0*((double)numnodes));
        radius=(Lenx/2.0)/Math.sin(Angle/2.0); // The radius of the big circle on which nodes will be plotted. *)
        for (count = 0; count < numnodes; ++ count) {  //  Assign each node its coordinates *)
            gnn = (Graph_NetworkNode) ConnArray.elementAt(count);
            gnn.xCoord = Startx+(Math.cos((((double)count)*2.0)*Angle)*radius);
            gnn.yCoord = Starty+(Math.sin((((double)count)*2.0)*Angle)*radius);
            ConnArray.setElementAt(gnn, count);
        }
    }
    
    
    
    private void  DrawWhiteBoxForLabel(double midx, double midy, String s, LinkedList llist,
    draw d)  {
        //  This routine ensures that network labels are drawn on a white
        //  background in the Java version
		//  edited by Richard Teviotdale 08-19-2004
        double LengthRect,HtRect;
        double NodeX[],NodeY[];
        int temp;
        //double epsilon = 0.01;

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
        
        NodeX = new double[5];
        NodeY = new double[5];
	temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(s);

        
//         NodeX = new double[5];
//         NodeY = new double[5];
//         temp = d.getGraphics().getFontMetrics(defaultFont).stringWidth(s);


        LengthRect = ((double) temp / (double) d.getSize().width);
        HtRect = Textheight / 2.0; //+ epsilon/2.0;
        LengthRect = LengthRect / 2.0; //+ epsilon;
        //HtRect=HtRect/2.0;
        NodeX[0] = midx - (LengthRect * 0.75);
        NodeY[0] = midy + (HtRect * 1.5);
        NodeX[1] = midx - (LengthRect * 0.75);
        NodeY[1] = midy - (HtRect * 0.5); //epsilon/2.0;//-HtRect;
        NodeX[2] = midx + (LengthRect * 2);
        NodeY[2] = midy - (HtRect * 0.5); //epsilon/2.0;//-HtRect;
        NodeX[3] = midx + (LengthRect * 2);
        NodeY[3] = midy + (HtRect * 1.5);
        NodeX[4] = NodeX[0]; //1];
        NodeY[4] = NodeY[0]; //1];
        //GKS.set_textline_color(White,snapsht.seginfo);
        GKS.set_fill_int_style(bsSolid,White,llist,d);
        GKS.fill_area(5,NodeX,NodeY,llist,d);
    }
    
    private class ConnNodeRec  {
        
        int thisConnNode;
        boolean UseArrow;
        char EColor;
	String HexColor;
        String EdgeWeight;
        
        public ConnNodeRec() {
            EdgeWeight = new String("");	  // would null be better to use as flag here?
	    HexColor = new String("");
        }
        
    }

    private class Graph_NetworkNode  {
        
        double xCoord,yCoord;
        LinkedList ConnectedNodes;
        LinkedList textInNode;  // Linked list of strings corresponding to linespernode
        
        public Graph_NetworkNode() {
            
        }
    }


    // NonLinearStructure functions follow ==========================
    // ==============================================================

    /* get rid of constructor
    public NonLinearStruct () {
    }
    */

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

	double xf,yf,af;
	int NC,TC;


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
	TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	GKS.set_textline_color(NC,llist,d);
	GKS.polyline(2,xLine,yLine,llist,d);
	GKS.set_textline_color(Black,llist,d);
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
	NC = new_extractColor(EdgeColor);
	TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
	GKS.set_textline_color(NC,llist,d);
	GKS.polyline(2,xLine,yLine,llist,d);
	GKS.set_textline_color(Black,llist,d);
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
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    NC = new_extractColor(EdgeColor);
	    TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    NC = new_extractColor(EdgeColor);
	    TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MinorAxis,MajorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    NC = new_extractColor(EdgeColor);
	    TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	    NC = new_extractColor(EdgeColor);
	    TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
	    GKS.set_textline_color(NC,llist,d);
	    GKS.ellipse(CX,CY,StartAngle,EndAngle,MajorAxis,MinorAxis,llist,d);
	    GKS.set_textline_color(Black,llist,d);
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
	TC = 	extractTextColorForHighlightedNodes(EdgeColor);
	GKS.set_fill_int_style(bsSolid,NC,llist,d);
	GKS.fill_area(4,xArr,yArr,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
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
	NC = new_extractColor(EdgeColor);
	TC = 	new_extractTextColorForHighlightedNodes(EdgeColor);
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
	//GKS.set_textline_color(Black,llist,d);  // Needed?
    
	if (TLP.length()>2) { // Potential Highlight Delimeter... *)
	    String TempString = new String (TLP);
	    if (TempString.charAt(0)==Delim && inHighlightColors(TempString.charAt(1))) {
		NodeColor = new_extractColor(TempString.substring(1));
		TextColor = new_extractTextColorForHighlightedNodes(TempString.substring(1));
		GKS.set_fill_int_style(bsSolid,NodeColor,llist,d);
		GKS.circle_fill(Centerx,Centery,NodeRadius,llist,d);
		GKS.set_textline_color(Black,llist,d);
		GKS.circle(Centerx,Centery,NodeRadius,llist,d);
		GKS.set_textline_color(TextColor,llist,d);
		GKS.set_fill_int_style(bsClear,NodeColor,llist,d);   // To insure
		//the text background is invisible }
		//  Now we must create a new string that removes  *)
		//  the compose character delimeter...            *)
		TLP = ( (TempString.charAt(1) != '#') ? TempString.substring(2) : TempString.substring(8) ) ;
		//		TLP = TempString.substring(2);
	    }
	    else {	  // Don't highlight node
		GKS.set_textline_color(Black,llist,d);
		GKS.circle(Centerx,Centery,NodeRadius,llist,d);
	    }
	}
	else {	// Don't highlight node
	    GKS.set_textline_color(Black,llist,d);
	    GKS.circle(Centerx,Centery,NodeRadius,llist,d);
	}
	// Write in the text. *)
	TextStartx=Centerx;
	TextStartY=Centery+(0.5*(((linespernode-1)*Textheight)+((linespernode-1)*
								(0.5*Textheight))));
	GKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);	// TA_TOP?  TA_BASELINE?
	// We already have the head node in the list
	GKS.text(TextStartx,TextStartY,TLP,llist,d);
	// check for more
	while (TextList.hasMoreElements()) { 
	    TLP = (String) TextList.nextElement();
	    //  Ready textstarts for next line.  *)
	    TextStartY=TextStartY-(1.5*Textheight);
	    GKS.text(TextStartx,TextStartY,TLP,llist,d);
	} //                        (*  WHILE  *)
	GKS.set_textline_color(Black,llist,d);
	GKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
	GKS.set_fill_int_style(bsClear,White,llist,d);
    }

}
