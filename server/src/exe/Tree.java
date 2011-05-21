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

package exe;

import java.io.*;

/**
 * This class provides support for binary and general trees, with methods to
 * output these trees in both GAIGS formats. A <code>Tree</code> object
 * contains a reference to the root of the tree, and children can be added to
 * this root using the various methods in the <code>TreeNode</code> class. The 
 * methods of this class are used to set whether the <code>Tree</code> is
 * binary or general and to control the way the <code>Tree</code> is output in
 * a GAIGS animation.
 * <p>
 * In order to use any of these tree classes in a script-producing program, the
 * script-producing program must import the package <code>exe</code>.
 * </p>
 *
 * @author Sven Moen (original author)
 * @author Ethan Dereszynski (adaptations)
 * @author Ben Tidman (GAIGS adaptations)
 * @author Andrew Jungwirth (more GAIGS adaptations and Javadoc comments)
 * @author Myles McNally (changed access status of instance variables)
 */


public class Tree {
    /* DATA: */

    /**
     * Contains a reference to the root of this <code>Tree</code>.
     */
    protected TreeNode root;

    /**
     * Maintains the binary status of this <code>Tree</code>. A value of 
     * <code>true</code> means this <code>Tree</code> is binary, and a value of
     * <code>false</code> means this <code>Tree</code> is general. Defaults to
     * <code>false</code>, making the default a general <code>Tree</code>.
     */
    protected boolean binary = false;

    /**
     * Stores the left-most x-bound for this <code>Tree</code> within the 
     * normalized [0,1] space. Defaults to <code>0.0</code> if no bounds are
     * set. The bounds variables can be used to localize this <code>Tree</code>
     * in a section of the drawing window in the new GAIGS XML format to allow
     * multiple structures to appear in the same snapshot.
     */
    protected double x1 = 0.0; 

    /**
     * Stores the lower y-bound for this <code>Tree</code> within the 
     * normalized [0,1] space. Defaults to <code>0.0</code> if no bounds are
     * set. The bounds variables can be used to localize this <code>Tree</code>
     * in a section of the drawing window in the new GAIGS XML format to allow 
     * multiple structures to appear in the same snapshot.
     */
    protected double y1 = 0.0;

    /**
     * Stores the right-most x-bound for this <code>Tree</code> within the 
     * normalized [0,1] space. Defaults to <code>1.0</code> if no bounds are
     * set. The bounds variables can be used to localize this <code>Tree</code>
     * in a section of the drawing window in the new GAIGS XML format to allow
     * multiple structures to appear in the same snapshot.
     */
    protected double x2 = 1.0;

    /**
     * Stores the upper y-bound for this <code>Tree</code> within the
     * normalized [0,1] space. Defaults to <code>1.0</code> if no bounds are 
     * set. The bounds variables can be used to localize this <code>Tree</code>
     * in a section of the drawing window in the new GAIGS XML format to allow
     * multiple structures to appear in the same snapshot.
     */
    protected double y2 = 1.0;

    /**
     * Holds the minimum font size for this <code>Tree</code> when drawn in the
     * new GAIGS XML format. Used to specify a font size that will be readable
     * when this <code>Tree</code> is drawn in a smaller portion of the screen.
     */
    protected double font_size = 0.03;

    /**
     * Contains the horizontal spacing of this <code>Tree</code>. Used to
     * define how many node widths are between nodes on the x-axis when this
     * <code>Tree</code> is drawn in either GAIGS format. Defaults to a value
     * of <code>1.25</code>, which produces just about as tight a tree as is
     * readable.
     */
    protected double x_spacing = 1.25;

    /**
     * Contains the vertical spacing of this <code>Tree</code>. Used to define
     * how many node widths are between nodes on the y-axis when this 
     * <code>Tree</code> is drawn in either GAIGS format. Defaults to a value
     * of <code>1.25</code>, which produces just about as tight a tree as is
     * readable.
     */
    protected double y_spacing = 1.25;

    /**
     * Keeps track of the number of lines of text for each 
     * <code>TreeNode</code>'s label. This is used in the old GAIGS format, but
     * it is not necessary in the new GAIGS XML format because the number of 
     * lines for each node can be determined from the XML script file. Default 
     * value is <code>1</code>.
     */
    protected int lines_per_node = 1;

    /**
     * Holds a reference to the <code>PrintWriter</code> output stream to which
     * this <code>Tree</code> should output its snapshots when its write 
     * methods are called.
     */
    protected PrintWriter out;

    /* METHODS: */

    /**
     * Constructs a new <code>Tree</code> by specifying whether it is a binary
     * or general <code>Tree</code>.
     *
     * @param isBinary Indicates if this <code>Tree</code> is binary or
     *                 general. A value of <code>true</code> makes a binary 
     *                 <code>Tree</code>, and <code>false</code> results in a 
     *                 general <code>Tree</code>.
     */
    public Tree(boolean isBinary){
	binary = isBinary;
	root = null;
    }

    /**
     * Constructs a new <code>Tree</code> by specifying whether it is a binary
     * or general <code>Tree</code> and by giving the number of lines for node
     * labels. 
     * This is only necessary when outputting the <code>Tree</code> in the old 
     * GAIGS format since the number of lines per node can be determined from
     * an XML script file in the new GAIGS XML format.
     *
     * @param isBinary       Indicates if this <code>Tree</code> is binary or
     *                       general. A value of <code>true</code> makes a 
     *                       binary <code>Tree</code>, and <code>false</code>
     *                       results in a general <code>Tree</code>.
     * @param lines_per_node Sets the number of lines for node labels.
     */
    public Tree(boolean isBinary, int lines_per_node){
	binary = isBinary;
	this.lines_per_node = lines_per_node;
	root = null;
    }

    /**
     * Sets the root <code>TreeNode</code> for this <code>Tree</code>. 
     * This method completely resets the root for this <code>Tree</code>, and 
     * any old data will be lost. Children can then be added to this root by
     * calling the appropriate methods in the <code>TreeNode</code> class.
     *
     * @param r Specifies the new root for this <code>Tree</code>. The variable
     *          <code>root</code> is set to equal the value of <code>r</code>.
     */
    public void setRoot(TreeNode r){ root = r; }

    /**
     * Specifies whether this <code>Tree</code> is binary or general.
     * If this <code>Tree</code>'s <code>root</code> is not equal to 
     * <code>null</code>, the value of <code>binary</code> will not be changed
     * because this <code>Tree</code> is already binary or general.
     *
     * @param isBinary Sets the value of <code>binary</code>. A value of 
     *                 <code>true</code> results in a binary <code>Tree</code>,
     *                 and a value of <code>false</code> produces a general
     *                 <code>Tree</code>. The value of <code>binary</code> is 
     *                 not changed if this <code>Tree</code> already has nodes.
     */
    public void setBinary(boolean isBinary){
	if(root == null){
	    binary = isBinary;
	}
    }

    /**
     * Sets the bounds in which this <code>Tree</code> is to be drawn.
     * These bounds specify the area within the normalized [0,1] space within
     * which this <code>Tree</code> should appear in the snapshot. Used to 
     * draw this <code>Tree</code> in a portion of the viewing window so that
     * multiple structures can appear in a snapshot in the new GAIGS XML
     * format.
     *
     * @param x1 Sets the left-most bound on the horizontal axis.
     * @param y1 Sets the lower bound on the vertical axis.
     * @param x2 Sets the right-most bound on the horizontal axis.
     * @param y2 Sets the upper bound on the vertical axis.
     */
    public void setBounds(double x1, double y1, double x2, double y2){
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
    }

    /**
     * Sets the minimum font size to keep the fonts in this <code>Tree</code>
     * readable. Used to upsize the font when drawing this <code>Tree</code> in
     * a portion of the viewing window in the new GAIGS XML format.
     *
     * @param size Indicates the minimum font size for the fonts in this 
     *             <code>Tree</code>.
     */
    public void setFontSize(double size){ font_size = size; }

    /** 
     * Sets the spacing between nodes on the vertical and horizontal axes.
     *
     * @param x Specifies the number of node widths between nodes on the 
     *          horizontal axis.
     * @param y Specifies the number of node widths between nodes on the 
     *          vertical axis.
     */
    public void setSpacing(double x, double y){
	x_spacing = x;
	y_spacing = y;
    }

    /**
     * Stores a reference to the <code>PrintWriter</code> output stream to 
     * which this <code>Tree</code> is to output its information.
     *
     * @param out Indicates the output stream to which this <code>Tree</code>
     *            should print its information when its write methods are 
     *            called.
     */
    public void setOut(PrintWriter out){ this.out = out; }

    /**
     * Modifies the number of lines used for node labels.
     * This value is only used in old GAIGS snapshot specifications so it is
     * not necessary to change <code>lines_per_node</code> in a 
     * script-producing program that uses the new GAIGS XML format.
     *
     * @param lines_per_node Sets the number of lines needed for node labels in
     *                       this <code>Tree</code>. 
     */
    public void setLinesPerNode(int lines_per_node){
	this.lines_per_node = lines_per_node; 
    }

    /**
     * Returns the <code>root</code> of this <code>Tree</code>.
     *
     * @return Gives a reference to the <code>root</code> of this 
     *         <code>Tree</code>. If the <code>root</code> has not been set,
     *         <code>null</code> is returned.
     */
    public TreeNode getRoot(){ return root; }

    /**
     * Indicates if this <code>Tree</code> is binary or general.
     *
     * @return Yields <code>true</code> if this <code>Tree</code> is binary or
     *         <code>false</code> if this <code>Tree</code> is general.
     */
    public boolean isBinary(){ return binary; }

    /**
     * Writes this <code>Tree</code> to the specified <code>PrintWriter</code>
     * output stream as an old GAIGS format snapshot.
     * If the value of <code>out</code> has not been set using the 
     * <code>setOut(PrintWriter)</code> method, an <code>Exception</code> will
     * be thrown.
     *
     * @param title Specifies the title for the snapshot so that it can be
     *              properly inserted into the snapshot specification.
     */
    public void writeGAIGSTree(String title){
	if(root == null){
	    if(binary){
		out.println("BinaryTree");
	    }else{
		out.println("GeneralTree");
	    }

	    out.println(lines_per_node + " " + x_spacing + " " + 
			y_spacing + "\n" + title + "\n***\\***\n***^***");
	}else{
	    int level = 0;

	    if(binary){
		out.println("BinaryTree");
	    }else{
		out.println("GeneralTree");
	    }
	
	    out.println(lines_per_node + " " + x_spacing + " " + 
			y_spacing + "\n" + title + "\n***\\***");
	    out.println(level);
	    if(binary){
		out.println("R");
	    }
	    out.println("\\" + root.getHexColor() + root.getValue());

	    if(root.getChild() != null){
		writeHelper(root.getChild(), level+1);
	    }

	    out.println("***^***");
	}
    }

    /**
     * Recursively writes the rest of this <code>Tree</code> in the old GAIGS
     * format snapshot. 
     * This is a helper method to <code>writeGAIGSTree</code> and is used to
     * finish writing the tree snapshot.
     *
     * @param current Indicates the current node in the recursive traversal of 
     *                this <code>Tree</code>.
     * @param level   Specifies the current depth in this <code>Tree</code>,
     *                which is needed for old GAIGS snapshot descriptions.
     */
    private void writeHelper(TreeNode current, int level){
	if(!current.isPlaceHolder()){
	    out.println(level);
	    if(binary){
		if(current.isLeftChild()){
		    out.println("L");
		}else{
		    out.println("R");
		}
	    }
	    out.println("\\" + current.getHexColor() + current.getValue());

	    if(current.getChild() != null){
		writeHelper(current.getChild(), level+1);
	    }
	}
	if(current.getSibling() != null){
	    writeHelper(current.getSibling(), level);
	}
    }

    /**
     * Writes this <code>Tree</code> to the specified <code>PrintWriter</code>
     * output stream in the new GAIGS XML format.
     * If the value of <code>out</code> has not been set using the 
     * <code>setOut(PrintWriter)</code> method, an <code>Exception</code> will
     * be thrown.
     */
    public void writeGAIGSXMLTree(){
	if(root == null){
	    return;
	}

	out.println("<tree x_spacing = \"" + x_spacing + 
		    "\" y_spacing = \"" + y_spacing + "\">");
	out.println("<bounds x1 = \"" + x1 + "\" y1 = \"" + y1 + 
		    "\" x2 = \"" + x2 + "\" y2 = \"" + y2 + 
		    "\" fontsize = \"" + font_size + "\"/>");
	if(binary){
	    out.println("<binary_node color = \"" + 
			root.getHexColor() + "\">");
	}else{
	    out.println("<tree_node color = \"" + root.getHexColor() + "\">");
	}
	out.println("<label>" + root.getValue() + "</label>");

	// Construct the tree recursively in a depth-first manner.
	if(root.getChild() != null){
	    writeXMLHelper(root.getChild());
	}

	if(binary){
	    out.println("</binary_node>\n</tree>");
	}else{
	    out.println("</tree_node>\n</tree>");
	}
    }

    /**
     * Writes this <code>Tree</code> to the specified <code>PrintWriter</code>
     * output stream in the new GAIGS XML format.
     * If the value of <code>out</code> has not been set using the 
     * <code>setOut(PrintWriter)</code> method, an <code>Exception</code> will
     * be thrown.
     * <p>
     * This method is identical to <code>writeGAIGSXMLTree</code>, except that
     * it prints the optional <code>name</code> element that gives this
     * <code>Tree</code> a label in the snapshot.
     * </p>
     *
     * @param name Specifies the label/title for this <code>Tree</code> in the
     *             snapshot. This is a title that appears only above this 
     *             structure and is smaller than the title for the entire 
     *             snapshot. If an individual title for this <code>Tree</code>
     *             is not desired, use {@link #writeGAIGSXMLTree()}.
     */
    public void writeGAIGSXMLTree(String name){
	if(root == null){
	    return;
	}

	out.println("<tree x_spacing = \"" + x_spacing + 
		    "\" y_spacing = \"" + y_spacing + "\">");
	out.println("<name>" + name + "</name>");
	out.println("<bounds x1 = \"" + x1 + "\" y1 = \"" + y1 + 
		    "\" x2 = \"" + x2 + "\" y2 = \"" + y2 + 
		    "\" fontsize = \"" + font_size + "\"/>");
	if(binary){
	    out.println("<binary_node color = \"" + 
			root.getHexColor() + "\">");
	}else{
	    out.println("<tree_node color = \"" + root.getHexColor() + "\">");
	}
	out.println("<label>" + root.getValue() + "</label>");

	// Construct the tree recursively in a depth-first manner.
	if(root.getChild() != null){
	    writeXMLHelper(root.getChild());
	}

	if(binary){
	    out.println("</binary_node>\n</tree>");
	}else{
	    out.println("</tree_node>\n</tree>");
	}
    }

    /**
     * Recursively writes the rest of this <code>Tree</code> in the new GAIGS
     * XML format.
     * This is a helper method to <code>writeGAIGSXMLTree</code> and is used to
     * finish writing this <code>Tree</code>'s information to the showfile.
     *
     * @param current Indicates the current node in the recursive traversal of 
     *                this <code>Tree</code>.
     */
    private void writeXMLHelper(TreeNode current){
	if(!current.isPlaceHolder()){
	    if(binary){
		if(current.isLeftChild()){
		    out.println("<left_node color = \"" + 
				current.getHexColor() + "\">");
		}else{
		    out.println("<right_node color = \"" + 
				current.getHexColor() + "\">");
		}
	    }else{
		out.println("<tree_node color = \"" + 
			    current.getHexColor() + "\">");
	    }
	    out.println("<label>" + current.getValue() + "</label>");

	    if(current.getChild() != null){
		writeXMLHelper(current.getChild());
	    }

	    if(binary){
		if(current.isLeftChild()){
		    out.println("</left_node>");
		}else{
		    out.println("</right_node>");
		}
	    }else{
		out.println("</tree_node>");
	    }

	    out.println("<tree_edge color = \"" + 
			current.getLineToParent().getHexColor() + "\">");
	    out.println("</tree_edge>");
	}

	if(current.getSibling() != null){
	    writeXMLHelper(current.getSibling());
	}
    }
}

