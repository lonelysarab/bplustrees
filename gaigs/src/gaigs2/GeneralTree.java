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

public class GeneralTree extends NonLinearStruct  {
    
    double xspacing,yspacing;
    
    // one-time only variables needed in implementation of various operations
    boolean  Dne;   // TO determine when Tree has been built
    //BinaryTreeNode root;  // needed??
    GeneralTreeNode NextNode;
    int CurrLevel;
    double ModifierSum;
    double NextPos [], Modifier[];
    double CenterShift, xMin,xMax, yMin,yMax;
    // End one-time only variables
    
    public GeneralTree() {
        //root = null;  // needed??
        CurrLevel = 0;
        ModifierSum = 0.0;
        NextPos = new double [MaxLevels];
        Modifier = new double [MaxLevels];
    }
    
    public boolean emptyStruct()  {
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
    public void loadLinesPerNodeInfo(StringTokenizer st, LinkedList llist, draw d)
    throws VisualizerLoadException	 {
        
        String tempString, tempString2;
        
        if (st.hasMoreTokens())
            tempString = st.nextToken();
        else
            throw ( new VisualizerLoadException("Expected lines per node - found end of string"));
        StringTokenizer t = new StringTokenizer(tempString, " \t");
        if (t.hasMoreTokens())
            tempString2 = t.nextToken();
        else
            throw ( new VisualizerLoadException("Expected lines per node - found " + tempString));
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
    
    
    
    public void calcDimsAndStartPts(LinkedList llist, draw d)  {
        /*  Determines the following variables Lenx, Leny, Startx, Starty, TitleStarty   */
        // Task: Calculate the length and height of a node and its starting point.
        //   It also computes the y-coordinate of the title
        double TitleToSSGap,
        MinGt,MaxGt,Diam,
        MinTitlex,MaxTitlex;
        int NumNodes,NumLines;
        
        super.calcDimsAndStartPts(llist,d);
        // With circular nodes, not adding Textheight works better to finetune the circle size
        Lenx=Maxstringlength;//+(Textheight);
        Leny=((linespernode+1)*Textheight)+
        ((linespernode-1)*(0.5*Textheight));
        TDx=(Lenx+Leny)/5.0;           // Height is a third of their average *)
        TDy=TDx/Math.sqrt(3);
        TitleToSSGap=2*Titleheight;
        NumLines=title.size();
        Startx=CenterScreen;
        Starty=Topy-IconHeight-IconToTitleGap-(NumLines*Titleheight)-
        ((NumLines-1)*(0.5*Titleheight))-TitleToSSGap-(Lenx/2.0);
        TitleStarty=Topy-IconHeight-IconToTitleGap-Titleheight;
    }
    
    
    
    //                 PROCEDURE DrawBinaryTree                             *)
    //                                                                      *)
    // This procedure calls on xCoord to obtain the x coordinate of each    *)
    // node in the tree, and then calls on ApplyModifier to to make any     *)
    // adjustments to the node positions that couldn't be made in xCoord.   *)
    // Finally, it does a pre-order traversal of the tree and draws each    *)
    // node to its rightful place in the tree, and connects appropriate     *)
    // nodes to form the binary tree.                                       *)
    void drawStructure(LinkedList llist, draw d)  {
        int x;
        double TempLoc;
        
        if (emptyStruct()) {
            super.drawStructure(llist,d);  // to handle empty structure
            return;
        }
        for (x = 0; x < MaxLevels; ++x) {
            Modifier[x]=0.0;
            NextPos[x]=TreeSideBorder+(Lenx/2.0); // The rightmost position at which we can plot a node *)
        }
        nodelist.reset();
        TempLoc=xCoord((GeneralTreeNode)(nodelist.currentElement()));
        ApplyModifier((GeneralTreeNode)(nodelist.currentElement()));
        CenterShift=CenterScreen-((GeneralTreeNode)(nodelist.currentElement())).Gx;
        // The factor needed to shift the tree to the center *)
        xMin=CenterScreen-(0.5*Maxtitlelength);
        xMax=CenterScreen+(0.5*Maxtitlelength);
        yMax=Topy-IconHeight-IconToTitleGap;
        yMin=yMax;
        drawWalk((GeneralTreeNode)(nodelist.currentElement()), llist, d);
        Xcenter=(xMin+xMax)/2.0;
        Ycenter=(yMin+yMax)/2;
        snapheight=yMax-yMin;
        snapwidth=xMax-xMin;
    }
    
    
    
    double xCoord(GeneralTreeNode Root) {
        
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
        GeneralTreeNode TempChild;
        
        if  (Root == null)
            return(0.0);
        else {
            CurrLevel=CurrLevel+1;
            TempChild=Root.Children;
            ChildCount=0;
            AccumXCoords=0;
            while  (TempChild != null) {
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
            if (Root.Children == null) {
                Root.GModifier=0.0;
                Root.Gx=Tempx;
            }
            else {
                Modifier[CurrLevel]=Math.max(Modifier[CurrLevel],Tempx-ChildAvgPos);
                Root.GModifier=Modifier[CurrLevel];
                Root.Gx=Modifier[CurrLevel]+ChildAvgPos;
            }
            Root.Gy=Starty-(Root.Glevel*(yspacing*Lenx));
            NextPos[CurrLevel]=Root.Gx+(xspacing*Lenx);
            return(Root.Gx);
        }
    }
    
    private void ApplyModifier(GeneralTreeNode Root) {
        
    /*
       GIVEN  : The node to which to apply the accumulated modifier.
       TASK   : Make recursive calls to accumulate the modifier sum up the tree,
                filling in each individual node's modifier along the way. */
        
        GeneralTreeNode TempChild;
        
        if (Root != null)  {
            Root.Gx=Root.Gx+ModifierSum;
            ModifierSum=ModifierSum+Root.GModifier;
            TempChild=Root.Children;
            while  (TempChild != null) {
                ApplyModifier(TempChild);
                TempChild=TempChild.Siblings;
            }
            ModifierSum=ModifierSum-Root.GModifier;
        }
    }
        
    private void  drawWalk(GeneralTreeNode Root, LinkedList llist , draw d)   {
        
    /*
       GIVEN  : The root of the binary tree to draw (whose node's x and y coordinates are now
                determined).
       TASK   : Do a pre-order traversal of the tree, drawing each node, along with proper
                connectors, as each node is visited. */
        
        GeneralTreeNode TempChild, TempChild2;
        
        if  (Root != null)  {
            drawCircNode(Root.Gx+CenterShift,Root.Gy,Lenx/2,Root.textInNode,llist,d);
            TempChild=Root.Children;
            while  (TempChild != null)  { // Connect this root to its children *)
                new_drawConnectingLine(Root.Gx+CenterShift,Root.Gy,
				   TempChild.Gx+CenterShift,
				   TempChild.Gy,Lenx/2.0,false,TempChild.edge_color, llist,d);
		if(TempChild.edge_label != null) { // <-- label the line if needed
		    // weighted average of parent and child's positions
		    double text_x = (Root.Gx*0.5 + TempChild.Gx*0.5) + CenterShift;
		    double text_width = TempChild.edge_label.length() * 0.65 * Textheight;
		    double text_y = (Root.Gy*0.5 + TempChild.Gy*0.5);
		    double squarex[] = { ( text_x - (0.5 * text_width + 0.1 * Textheight) ),
		                         ( text_x + (0.5 * text_width + 0.1 * Textheight) ),
		                         ( text_x + (0.5 * text_width + 0.1 * Textheight) ),
		                         ( text_x - (0.5 * text_width + 0.1 * Textheight) ),
		                         ( text_x - (0.5 * text_width + 0.1 * Textheight) ) };
		    double squarey[] = { ( text_y - 0.1 * Textheight ),
		                         ( text_y - 0.1 * Textheight ),
		                         ( text_y + 0.7 * Textheight ),
		                         ( text_y + 0.7 * Textheight ),
		                         ( text_y - 0.1 * Textheight ) };
		    LGKS.set_fill_int_style(bsSolid, White, llist, d);
		    LGKS.fill_area(4, squarex, squarey, llist, d);

		    LGKS.set_text_height(Textheight, llist, d);
		    LGKS.set_text_align(TA_CENTER, TA_BASELINE, llist, d);
		    LGKS.set_textline_color(Black_Color,llist,d);
		    LGKS.text( text_x, text_y, TempChild.edge_label, llist, d);
		}
                TempChild=TempChild.Siblings;
            }
            TempChild2=Root.Children;
            while  (TempChild2 != null) { // Recursively draw Root's children *)
                drawWalk(TempChild2, llist, d);
                TempChild2=TempChild2.Siblings;
            }
        }
    } // drawWalk()
    
    public void loadStructure(StringTokenizer st, LinkedList llist, draw d)
    throws VisualizerLoadException  {
        
        GeneralTreeNode GTN = new GeneralTreeNode();
        
        Dne=false;                   // For handling EOSS in the recursive procedure *)
        try {
            GTN = getGTNode(st, linespernode, llist, d);
        }
        catch ( EndOfSnapException e ) {
            Dne = true;
        }
        if (!Dne) {
            buildGeneralTree(st,GTN, llist,d); // Build Tree *)
            nodelist.append(GTN);
        }
        
    } // loadStructure(st)

    public void loadStructure(Element tree, LinkedList llist, draw d)
	throws VisualizerLoadException {

	load_name_and_bounds(tree, llist, d);
	load_spacing(tree, llist, d);

	GeneralTreeNode GTN;
	Element root = tree.getChild("tree_node");
	if(root != null) {
	    GTN = getGTNode(root, 0, llist, d);
	    buildGeneralTree(root, GTN, llist, d);
	    nodelist.append(GTN);
	}
    } // loadStructure(element)

    protected void load_spacing(Element tree, LinkedList llist, draw d) {
	xspacing = Format.atof(tree.getAttributeValue("x_spacing"));
	yspacing = Format.atof(tree.getAttributeValue("y_spacing"));
    }
    
    public void buildGeneralTree(StringTokenizer st, GeneralTreeNode PresentNode, LinkedList llist, draw d)
    throws VisualizerLoadException  {
        
        GeneralTreeNode LastChild;
        
        try {
            NextNode = getGTNode(st, linespernode, llist,d);
        }
        catch ( EndOfSnapException e ) {
            Dne = true;
        }
        LastChild = NextNode;
        while (!Dne && (NextNode.Glevel>PresentNode.Glevel)) {
            // We must insert NextNode as the LastChild of the PresentNode...*)
            if (PresentNode.Children == null) // Special case *)
                PresentNode.Children=NextNode;
            else
                LastChild.Siblings=NextNode;
            LastChild=NextNode;
            buildGeneralTree(st, NextNode,llist,d);
        }
        
    } // buildGeneralTree(st)
    
    public void buildGeneralTree(Element root, GeneralTreeNode PresentNode, LinkedList llist, draw d) {
	GeneralTreeNode LastChild = null;
	
	Iterator iter = root.getChildren().iterator();
	Element child;
	while( iter.hasNext() ) {
	    child = (Element) iter.next();
	    if( child.getName().compareTo("tree_node") == 0 ) {
		if( LastChild == null ) {
		    // build PresentNode's new child
		    PresentNode.Children = getGTNode(child, PresentNode.Glevel+1, llist, d);
		    LastChild = PresentNode.Children;
		}
		else {
		    // build LastChild's new sibling
		    LastChild.Siblings = getGTNode(child, LastChild.Glevel, llist, d);
		    LastChild = LastChild.Siblings;
		}
		buildGeneralTree(child, LastChild, llist, d);
	    }
	    else if( child.getName().compareTo("tree_edge") == 0 ) {
		// customize LastChild's edge (going into)
		loadEdge(LastChild, child, llist, d);
	    }
	}
    } // buildGeneralTree(element)

    protected void loadEdge(GeneralTreeNode gtn, Element edge_desc, LinkedList llist, draw d) {
	Element label = edge_desc.getChild("label");
	if(label != null)
	    gtn.edge_label = label.getText();

	gtn.edge_color = edge_desc.getAttributeValue("color");
    }
    
    public GeneralTreeNode getGTNode(StringTokenizer st, int linesPerNode,
    LinkedList llist, draw d) throws
    EndOfSnapException, VisualizerLoadException {
        
        String s;
        GeneralTreeNode gtn;
        
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            if (s.compareTo(EndSnapShot) == 0)
                throw (new EndOfSnapException());
        }
        else throw (new VisualizerLoadException("Encountered end of data when expected tree level"));
        gtn = new GeneralTreeNode();
        gtn.Glevel = Format.atoi(s);
        gtn.textInNode = getTextNode(st, linesPerNode, llist,d);
        return(gtn);
    } // getGTNode(st)

    public GeneralTreeNode getGTNode(Element node, int level, LinkedList llist, draw d) {
	GeneralTreeNode gtn = new GeneralTreeNode();

	gtn.Glevel = level;
	
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
	String label_line;
	int num_lines = 0;

	String color = node.getAttributeValue("color");
	if( color.charAt(0) != '#' )
	    label_line = "\\" + color_str_to_char( color );
	else
	    label_line = "\\" + color;

	Element label = node.getChild("label");
	if(label == null)
	    gtn.textInNode.append(label_line);
	else {
	    StringTokenizer st = new StringTokenizer(label.getText().trim(), "\f\r\n");
	    while(st.hasMoreTokens()) {
		num_lines++;
		label_line += st.nextToken();
		gtn.textInNode.append(label_line);

		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( textwocolor(label_line) );
		double check = ((double) temp / (double) GaigsAV.preferred_width);
		if (check > Maxstringlength)
		    Maxstringlength = check;

		label_line = "";
	    }
	}

	if(num_lines > linespernode)
	    linespernode = num_lines;

	return gtn;
    }
    
    private class GeneralTreeNode {

	String edge_color;
	String edge_label;
        
        int Glevel;
        double Gx, Gy, GModifier;
        GeneralTreeNode Siblings,Children;
        LinkedList textInNode;
        
        public GeneralTreeNode() {
            textInNode = new LinkedList();
            Siblings = null;
            Children = null;

	    edge_color = "#888888";
	    edge_label = null;
        }
        
    }
}
