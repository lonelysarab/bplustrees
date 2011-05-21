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

public class BinaryTree extends NonLinearStruct  {
    
    double xspacing, yspacing;
    
    // one-time only variables needed in implementation of various operations
    boolean  Dne;   // TO determine when Tree has been built
    //BinaryTreeNode root;  // needed??
    BinaryTreeNode NextNode; // used only in BuildBinaryTree recursive function.
    int CurrLevel;
    double ModifierSum;
    double NextPos [], Modifier[];
    double CenterShift, xMin,xMax, yMin,yMax;
    // End one-time only variables
    
    public BinaryTree() {
        //root = null;  // needed??
        CurrLevel = 0;
        ModifierSum = 0.0;
        NextPos = new double [MaxLevels];
        Modifier = new double [MaxLevels];
    }
    
    public boolean emptyStruct()  {
        if (nodelist.size() == 0) {
            return true;
        } else {
            return false;
        }
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
        
        xspacing = 2.0;
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
        TempLoc=xCoord((BinaryTreeNode)(nodelist.currentElement()));
        ApplyModifier((BinaryTreeNode)(nodelist.currentElement()));
        CenterShift=CenterScreen-((BinaryTreeNode)(nodelist.currentElement())).Bx;
        // The factor needed to shift the tree to the center *)
        xMin=CenterScreen-(0.5*Maxtitlelength);
        xMax=CenterScreen+(0.5*Maxtitlelength);
        yMax=Topy-IconHeight-IconToTitleGap;
        yMin=yMax;
        drawWalk((BinaryTreeNode)(nodelist.currentElement()), llist, d);
        Xcenter=(xMin+xMax)/2.0;
        Ycenter=(yMin+yMax)/2;
        snapheight=yMax-yMin;
        snapwidth=xMax-xMin;
    }
    
    
    double xCoord(BinaryTreeNode Root) {
        
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
        else {
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
            if ((Root.Lchild == null) && (Root.Rchild==null))  {  // We have a leaf node *)
                Root.BModifier=0.0;
                Root.Bx=Tempx;
                // Note that Modifier for current level will not change *)
            }
            else {
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
    
    private void ApplyModifier(BinaryTreeNode Root) {
        
    /*
       GIVEN  : The node to which to apply the accumulated modifier.
       TASK   : Make recursive calls to accumulate the modifier sum up the tree,
                filling in each individual node's modifier along the way. */
        
        if (Root != null)  {
            Root.Bx=Root.Bx+ModifierSum;
            ModifierSum=ModifierSum+Root.BModifier;
            ApplyModifier(Root.Lchild);
            ApplyModifier(Root.Rchild);
            ModifierSum=ModifierSum-Root.BModifier;
        }
    }
    
    private void  drawWalk(BinaryTreeNode Root, LinkedList llist , draw d)   {
        
    /*
       GIVEN  : The root of the binary tree to draw (whose node's x and y coordinates are now
                determined).
       TASK   : Do a pre-order traversal of the tree, drawing each node, along with proper
                connectors, as each node is visited. */
        
        // From drawstruct
        
        if  (Root != null)  {
            drawCircNode(Root.Bx+CenterShift,Root.By,Lenx/2,Root.textInNode,llist,d);
            if (Root.Bx+CenterShift+(0.5*Lenx)>xMax)
                xMax=Root.Bx+CenterShift+(0.5*Lenx);
            else if  (Root.Bx+CenterShift-(0.5*Lenx)<xMin)
                xMin=Root.Bx+CenterShift-(0.5*Lenx);
            if (Root.By-(0.5*Lenx)<yMin)
                yMin=Root.By-(0.5*Lenx);
            if (Root.Lchild != null) { // Connect Left Child *)
                new_drawConnectingLine(Root.Bx+CenterShift,Root.By,
				       Root.Lchild.Bx+CenterShift,
				       Root.Lchild.By,Lenx/2.0,false,
				       Root.Lchild.edge_color,llist,d);
		if(Root.Lchild.edge_label != null) {                       // <-- draw edge label
		    // weighted avg of positions
		    double text_x = (Root.Bx*0.5 + Root.Lchild.Bx*0.5) + CenterShift;
		    double text_width = Root.Lchild.edge_label.length() * 0.65 * Textheight;
		    double text_y = (Root.By*0.5 + Root.Lchild.By*0.5);
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
		    LGKS.set_textline_color(Black_Color, llist, d);
		    LGKS.text( text_x, text_y,
			       Root.Lchild.edge_label, llist, d);
		}
	    }
            if (Root.Rchild != null) { // Connect Right Child *)
                new_drawConnectingLine(Root.Bx+CenterShift,Root.By,
				       Root.Rchild.Bx+CenterShift,
				       Root.Rchild.By,Lenx/2.0,false,
				       Root.Rchild.edge_color,llist,d);
		if(Root.Rchild.edge_label != null) {
		    // weighted avg of positions
		    double text_x = (Root.Bx*0.5 + Root.Rchild.Bx*0.5) + CenterShift;
		    double text_width = Root.Rchild.edge_label.length() * 0.65 * Textheight;
		    double text_y = (Root.By*0.5 + Root.Rchild.By*0.5);
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
		    LGKS.set_textline_color(Black_Color, llist, d);
		    LGKS.text( text_x, text_y,
			       Root.Rchild.edge_label, llist, d);
		}
	    }
            drawWalk(Root.Lchild,llist,d);
            drawWalk(Root.Rchild,llist,d);
        }
    } // drawWalk
    
    public void loadStructure(StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException  {
        
        BinaryTreeNode BTN = new BinaryTreeNode();
        
        Dne=false;                   // For handling EOSS in the recursive procedure *)
        try {
            BTN = getBTNode(st, linespernode, llist, d);
        }
        catch ( EndOfSnapException e ) {
            Dne = true;
        }
        if (!Dne) {
            buildBinaryTree(st,BTN, llist,d); // Build Tree *)
            nodelist.append(BTN);
        }
        
    } // loadStructure(st)

    public void loadStructure(Element tree, LinkedList llist, draw d)
	throws VisualizerLoadException {
	
	load_name_and_bounds(tree,llist,d);
	load_spacing(tree,llist,d);
	
	BinaryTreeNode BTN;
	Element root = tree.getChild("binary_node");
	if(root != null) {
	    BTN = getBTNode(root, 0, 'R', llist, d);
	    buildBinaryTree(root, BTN, 0, llist, d);
	    nodelist.append(BTN);
	}
    } // loadStructure(element)
    
    protected void load_spacing(Element tree, LinkedList llist, draw d) {
	xspacing = Format.atof(tree.getAttributeValue("x_spacing"));
	yspacing = Format.atof(tree.getAttributeValue("y_spacing"));
    }
    
    
    public void buildBinaryTree(StringTokenizer st, BinaryTreeNode PresentNode, LinkedList llist, draw d)
	throws VisualizerLoadException  {

        try {
            NextNode = getBTNode(st, linespernode, llist,d);
        }
        catch ( EndOfSnapException e ) {
            Dne = true;
        }
        if (!Dne) {
            if ((NextNode.Blevel>PresentNode.Blevel) &&
            (NextNode.Childtype=='L')) {
                PresentNode.Lchild=NextNode;
                //buildBinaryTree(st, NextNode/*PresentNode.Lchild??*/,llist,d);
                buildBinaryTree(st, /*NextNode*/PresentNode.Lchild,llist,d);
            }
        }
        if (!Dne) {  // The prior recursive call could set Dne to true
            if ((NextNode.Blevel>PresentNode.Blevel) &&
            (NextNode.Childtype=='R'))	{
                PresentNode.Rchild=NextNode;
                //buildBinaryTree(st, NextNode/*PresentNode.Rchild??*/,llist,d);
                buildBinaryTree(st, /*NextNode*/PresentNode.Rchild,llist,d);
            }
        }
        
    } // buildBinaryTree(st)

    public void buildBinaryTree(Element root, BinaryTreeNode PresentNode, int level, LinkedList llist, draw d)
	throws VisualizerLoadException {

	Element left = root.getChild("left_node");
	if(left != null) {
	    BinaryTreeNode left_node;
	    Iterator iter = root.getChildren().iterator();
	    Element tree_edge = null;
	    while( iter.hasNext() ) {
		tree_edge = (Element) iter.next();
		if(tree_edge.getName().compareTo("left_node") == 0)
		    break;
	    }
	    if( iter.hasNext() )
		tree_edge = (Element) iter.next();
	    if( tree_edge.getName().compareTo("tree_edge") == 0 )
		left_node = getBTNode(left, tree_edge, level+1, 'L', llist, d);
	    else
		left_node = getBTNode(left, level+1, 'L', llist, d);
	    PresentNode.Lchild = left_node;
	    buildBinaryTree(left, left_node, level+1, llist, d);
	}
	Element right = root.getChild("right_node");
	if(right != null) {
	    BinaryTreeNode right_node;
	    Iterator iter = root.getChildren().iterator();
	    Element tree_edge = null;
	    while( iter.hasNext() ) {
		tree_edge = (Element) iter.next();
		if(tree_edge.getName().compareTo("right_node") == 0)
		    break;
	    }
	    if( iter.hasNext() )
		tree_edge = (Element) iter.next();
	    if( tree_edge.getName().compareTo("tree_edge") == 0 )
		right_node = getBTNode(right, tree_edge, level+1, 'R', llist, d);
	    else
		right_node = getBTNode(right, level+1, 'R', llist, d);
	    PresentNode.Rchild = right_node;
	    buildBinaryTree(right, right_node, level+1, llist, d);
	}
    } // buildBinaryTree(element)
    
    
    public BinaryTreeNode getBTNode(StringTokenizer st, int linesPerNode, LinkedList llist, draw d) throws
    EndOfSnapException, VisualizerLoadException {
        
        String s;
        BinaryTreeNode btn;
        
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            if (s.compareTo(EndSnapShot) == 0)
                throw (new EndOfSnapException());
        }
        else throw (new VisualizerLoadException("Encountered end of data when expected tree level"));
        btn = new BinaryTreeNode();
        btn.Blevel = Format.atoi(s);
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            if (s.compareTo(EndSnapShot) == 0)
                throw (new EndOfSnapException());
        }
        else throw (new VisualizerLoadException("Encountered end of data when expected R or L"));
        s = s.trim().toUpperCase();
        if (s.charAt(0) != 'R' &&  s.charAt(0) != 'L')
            throw (new VisualizerLoadException("Encountered " + s + " when expecting R or L child type"));
        btn.Childtype = s.charAt(0);
        btn.textInNode = getTextNode(st, linesPerNode, llist,d);
        return(btn);
    } // getBTNode(st)

    public BinaryTreeNode getBTNode(Element node, Element tree_edge, int level, char side, LinkedList llist, draw d) {
	BinaryTreeNode btn = getBTNode(node, level, side, llist, d);
	if(tree_edge != null) {
	    btn.edge_color = tree_edge.getAttributeValue("color");
	    Element label = tree_edge.getChild("label");
	    if(label != null)
		btn.edge_label = label.getText();
	}
	else
	    System.out.println("Null tree_edge"); // shouldnt happen
	return btn;
    }

    public BinaryTreeNode getBTNode(Element node, int level, char side, LinkedList llist, draw d) {
	BinaryTreeNode btn = new BinaryTreeNode();

	btn.Blevel = level;
	btn.Childtype = side;

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
	    btn.textInNode.append(label_line);
	else {
	    StringTokenizer st = new StringTokenizer(label.getText().trim(), "\f\r\n");
	    while(st.hasMoreTokens()) {
		num_lines++;
		label_line += st.nextToken();
		btn.textInNode.append(label_line);

		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( textwocolor(label_line) );
		double check = ((double) temp / (double) GaigsAV.preferred_width);
		if (check > Maxstringlength)
		    Maxstringlength = check;

		label_line = "";
	    }
	}

	if(num_lines > linespernode)
	    linespernode = num_lines;

	return btn;
    } // getBTNode(element)   
	    
    
    private class BinaryTreeNode {
        
        String edge_color;
	String edge_label;
        
        int Blevel;
        char Childtype;
        double Bx, By, BModifier;
        BinaryTreeNode Lchild,Rchild;
        LinkedList textInNode;
        
        public BinaryTreeNode() {
            textInNode = new LinkedList();
            Lchild = null;
            Rchild = null;

	    edge_color = "#888888";
	    edge_label = null;
        }
        
    }
}
