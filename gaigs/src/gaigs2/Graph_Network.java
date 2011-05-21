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

public class  Graph_Network  extends NonLinearStruct {
    
    boolean itIsGraph;
    int numnodes;
    boolean Coordsknown;
    Vector ConnArray;   // essentially an array of Graph_NetworkNodes
    
    public Graph_Network(String whichType)  {
        
        if (whichType.compareTo("GRAPH") == 0)
            itIsGraph = true;
        else
            itIsGraph = false;
        numnodes = 0;
        ConnArray = new Vector(5,5);
    }
    
    
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

    void loadStructure(Element myRoot, LinkedList llist, draw d)
	throws VisualizerLoadException {
	load_name_and_bounds(myRoot, llist, d);

	Coordsknown = true;

	double maxx = 0;
        double maxy = 0;
        double minx = 0;
        double miny = 0;
	Element child;

	Hashtable names_to_index = new Hashtable();

	Iterator iter = myRoot.getChildren().iterator();
	if( !iter.hasNext() )
	    return;
	child = (Element) iter.next();

	while( child.getName().compareTo("vertex") != 0 ) {
	    if( !iter.hasNext() )
		return;
	    child = (Element) iter.next();
	}

	// child should be pointing at the first vertex Element now.
	// initial loading
	numnodes = 0;
	while( true ) {
	    loadNode(child, names_to_index, numnodes, llist, d);
	    numnodes++;
	    if( !iter.hasNext() )
		break;
	    child = (Element) iter.next();
	}

	// still have to load the edges (since we needed to build names_to_index first)
	iter = myRoot.getChildren().iterator();
	if( !iter.hasNext() )
	    return;
	child = (Element) iter.next();

	while( child.getName().compareTo("vertex") != 0 ) {
	    if( !iter.hasNext() )
		return;
	    child = (Element) iter.next();
	}

	// child should be pointing at the first vertex Element again.
	int node_num = 0;
	while( true ) {
	    loadConnections(child, names_to_index, node_num, llist, d);
	    node_num++;
	    if( !iter.hasNext() )
		break;
	    child = (Element) iter.next();
	}
    }

    protected void loadConnections(Element vertex, Hashtable names_to_index, int node_num, LinkedList llist, draw d)
	throws VisualizerLoadException {

	( (Graph_NetworkNode) ConnArray.get(node_num) ).ConnectedNodes = new LinkedList();

	if(vertex == null)
	    throw new VisualizerLoadException("loadConnections passed a null vertex");
	
	Iterator iter = vertex.getChildren().iterator();
	Element child;
	if( !iter.hasNext() )
	    return;
	child = (Element) iter.next();

	while( child.getName().compareTo("edge") != 0 ) {
	    if( !iter.hasNext() )
		return;
	    child = (Element) iter.next();
	}

	// should be pointing at first edge now.
	
	while( true ) {
	    ConnNodeRec connection = new ConnNodeRec();
	    Element label = child.getChild("label");

	    // edge label
	    if(label != null)
		connection.EdgeWeight = label.getText();

	    // arrow?
	    connection.UseArrow = child.getAttributeValue("directed").compareTo("true") == 0;
	    
	    // edge color
	    String color = child.getAttributeValue("color");
	    if( color.charAt(0) == '#' ) {
		connection.EColor = '#';
		connection.HexColor = color.substring(1);
	    }
	    else
		connection.EColor = color_str_to_char(color);

	    // edge target
	    connection.thisConnNode = ( (Integer) names_to_index.get( child.getAttributeValue("target") ) ).intValue();

	    // edge done, put on list
	    ( (Graph_NetworkNode) ConnArray.get(node_num) ).ConnectedNodes.append(connection);

	    if( !iter.hasNext() )
		return;
	    child = (Element) iter.next();
	}
    }

    protected void loadNode(Element vertex, Hashtable names_to_index, int node_num, LinkedList llist, draw d)
	throws VisualizerLoadException {
	
	String name = vertex.getAttributeValue("id");
	names_to_index.put(name, new Integer(node_num + 1)); // + 1 cuz drawStructure relies on numberings from 1..

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
	Graph_NetworkNode gnn = new Graph_NetworkNode();
	Element child;
	// xml elements:
	Element label; // optional
	Element position; // optional

	label = vertex.getChild("label");
	position = vertex.getChild("position");

	// load text, nodecolor
	int num_lines = 0;
	String label_line = "";
	gnn.textInNode = new LinkedList();

	if( vertex.getAttributeValue("color").charAt(0) != '#' )
	    label_line = "\\" + color_str_to_char( vertex.getAttributeValue("color") );
	else
	    label_line = "\\" + vertex.getAttributeValue("color");

	if(label == null)
	    gnn.textInNode.append(label_line);
	else {
	    StringTokenizer st = new StringTokenizer(label.getText().trim(), "\f\r\n");
	    while(st.hasMoreTokens()) {
		num_lines++;
		label_line += st.nextToken();
		gnn.textInNode.append(label_line);
		
		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( textwocolor(label_line) );
		double check = ((double) temp / (double) GaigsAV.preferred_width);
		if (check > Maxstringlength)
		    Maxstringlength = check;
		
		label_line = "";
	    }
	}
	
	// load position
	if(position != null) {
	    //Coordsknown = true;
	    gnn.xCoord = Format.atof( position.getAttributeValue("x") );
	    gnn.yCoord = Format.atof( position.getAttributeValue("y") );
	}
	else
	    Coordsknown = false;
	
	// update linespernode
	if(num_lines > linespernode)
	    linespernode = num_lines;

	ConnArray.add(gnn);
    }
    
    public void loadStructure(StringTokenizer st, LinkedList llist, draw d)
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
    
    
    public void drawStructure(LinkedList llist, draw d){
        
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
                    LGKS.set_text_align(Halign,Valign,llist,d);
                    // For labels on non-self-connected nodes, put the label
                    //   on a white background *)
                    // Careful -- I don't think we do this in the WWW version
                    if ((Halign==TA_CENTER) && (Valign==TA_BASELINE))
                        LGKS.set_fill_int_style(bsClear,White,llist,d);
                    // GetHighlightColors(CurrConnNode.EColor,NoC,TeC);
                    DrawWhiteBoxForLabel(midx, midy,CurrConnNode.EdgeWeight, llist,d);
                    NoC = new_extractColor((new String("" + CurrConnNode.EColor)) + CurrConnNode.HexColor);
                    TeC = new_extractTextColorForHighlightedNodes((new String("" + CurrConnNode.EColor)) + CurrConnNode.HexColor);
                    //LGKS.set_textline_color(NoC,llist,d);
		    LGKS.set_textline_color(Black,llist,d);
                    
                    LGKS.text(midx,midy,CurrConnNode.EdgeWeight,llist,d);
                    // Set Attributes back to normal... *)
                    LGKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
                    //LGKS.set_textline_color(Black,llist,d);
                }	// endif we have to draw weights
            }	 // endwhile have more connected nodes
        }	// endfor all nodes
        
        
    }
    
    public boolean emptyStruct()   {
        return (numnodes == 0);
    }
    
    
    
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
        double LengthRect,HtRect;
        double NodeX[],NodeY[];
        int temp;
        double epsilon = 0.003;
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
        
        NodeX = new double[5];
        NodeY = new double[5];
	temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(s);

	    // This strategy works also, but the BufferedImage seems
	    // guaranteed and the java docs regarding
	    // FontRenderContext seem to indicate less reliable

	//	temp = (int)(defaultFont.getStringBounds(s, new FontRenderContext(new AffineTransform(),true,true))).getWidth();

	LengthRect = ((double) temp / (double) GaigsAV.preferred_width /*maxsize*/);
//         LengthRect = ((double) temp / (double) d.getSize().width);
        HtRect= LGKS.minScale(Textheight) + epsilon/2.0;
        LengthRect=LengthRect/2.0 + 2.0 * epsilon;
        //HtRect=HtRect/2.0;
        NodeX[0]=midx-LengthRect;
        NodeY[0]=midy+HtRect;
        NodeX[1]=midx-LengthRect;
        NodeY[1]=midy - epsilon/2.0;//-HtRect;
        NodeX[2]=midx+LengthRect;
        NodeY[2]=midy - epsilon/2.0;//-HtRect;
        NodeX[3]=midx+LengthRect;
        NodeY[3]=midy+HtRect;
        NodeX[4]=NodeX[0];
        NodeY[4]=NodeY[0];
        //LGKS.set_textline_color(White,snapsht.seginfo);
        LGKS.set_fill_int_style(bsSolid,White,llist,d);
        LGKS.fill_area(5,NodeX,NodeY,llist,d);
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
	    EColor = Black_Color;
        }
        
    }
    private class Graph_NetworkNode  {
        
        double xCoord,yCoord;
        LinkedList ConnectedNodes;
        LinkedList textInNode;  // Linked list of strings corresponding to linespernode
        
        public Graph_NetworkNode() {
            
        }
    }
}

