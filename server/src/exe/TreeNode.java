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


/**
 * This class maintains the information for a single node in the 
 * <code>Tree</code> data structure. A <code>TreeNode</code> object inherits
 * from the <code>VisNode</code> class and adds additional data members and
 * methods to allow these nodes to be used in binary and general trees.
 * <p>
 * In order to use any of these tree classes in a script-producing program, the
 * script-producing program must import the package <code>exe</code>.
 * </p>
 *
 * @author Sven Moen (original author)
 * @author Ethan Dereszynski (adaptations)
 * @author Ben Tidman (GAIGS adaptations)
 * @author Andrew Jungwirth (more GAIGS adaptations and Javadoc comments)
 */
public class TreeNode extends VisNode{
    /* DATA: */

    /**
     * Stores the value that is displayed within the circular node when this
     * <code>TreeNode</code> is output to an animation file. If this 
     * <code>String</code> is multiple lines, the node label will have multiple
     * lines when it is output to the showfile.
     */
    private String value;

    /** 
     * Contains a reference to the parent of this <code>TreeNode</code> in the
     * <code>Tree</code> structure. If this <code>TreeNode</code> has no
     * parent, the value of <code>parent</code> is <code>null</code>.
     */
    private TreeNode parent;

    /**
     * Maintains a reference to the left-most child of this 
     * <code>TreeNode</code> in the <code>Tree</code> structure. If this 
     * <code>TreeNode</code> has no children, the value of <code>child</code> 
     * is <code>null</code>.
     */
    private TreeNode child;

    /**
     * Keeps a reference to the sibling of this <code>TreeNode</code> in the 
     * <code>Tree</code> structure. This is the child of this node's parent
     * that is to the right of this node. If this <code>TreeNode</code> has no
     * sibling, the value of <code>sibling</code> is <code>null</code>.
     */
    private TreeNode sibling;
    
    /** 
     * Stores an <code>int</code> value that can be used to distinguish this
     * <code>TreeNode</code> from other <code>TreeNode</code> objects in a
     * <code>Tree</code> structure.
     */
    private int id;

    /**
     * Keeps a reference to the <code>Edge</code> connecting this 
     * <code>TreeNode</code> to its <code>parent</code>. If this 
     * <code>TreeNode</code>'s <code>parent</code> is <code>null</code>, then
     * <code>lineToParent</code> will also be <code>null</code>.
     */
    private Edge lineToParent;

    /**
     * Indicates if this <code>TreeNode</code> is a left child of its parent in
     * a binary <code>Tree</code> structure. A value of <code>true</code>
     * means that this <code>TreeNode</code> is the left binary child of its
     * parent; otherwise, <code>leftChild</code> stores a value of
     * <code>false</code>.
     */
    private boolean leftChild = false;
    
    /**
     * Indicates if this <code>TreeNode</code> is a right child of its parent
     * in a binary <code>Tree</code> structure. A value of <code>true</code> 
     * means that this <code>TreeNode</code> is the right binary child of its
     * parent; otherwise, <code>rightChild</code> stores a value of 
     * <code>false</code>.
     */ 
    private boolean rightChild = false;

    /**
     * Indicates if this <code>TreeNode</code> is a placeholder in a binary
     * <code>Tree</code> structure. This is necessary because a 
     * <code>TreeNode</code> only maintains a link to its left child in a 
     * binary tree and accesses its right child through this left child's 
     * <code>sibling</code> link. For this reason, if a right child is inserted
     * when no left child exists, a placeholder left child must be inserted so
     * that its <code>sibling</code> reference will point to the right child
     * that has just been inserted. This placeholder left child is not 
     * printed to the animation file but is necessary to allow the parent node
     * to access its right child. A value of <code>true</code> means that this
     * <code>TreeNode</code> is such a placeholder; otherwise, 
     * <code>placeHolder</code> stores a value of <code>false</code>.
     */
    private boolean placeHolder = false;

    /* METHODS: */    

    /**
     * Constructs a <code>TreeNode</code> with default values.
     */
    public TreeNode(){}

    /**
     * Constructs a <code>TreeNode</code> by specifying its <code>value</code>.
     *
     * @param v Specifies the label that appears within the circular node when
     *          this <code>TreeNode</code> is output to the animation file. If
     *          <code>v</code> contains multiple lines, this 
     *          <code>TreeNode</code>'s label will have multiple lines.
     */
    public TreeNode(String v){
	value = v;
    }

    /**
     * Constructs a <code>TreeNode</code> by giving its <code>value</code> and
     * <code>id</code>.
     *
     * @param v Indicates the value that appears within the circular node when
     *          this <code>TreeNode</code> is output to the animation file. If
     *          <code>v</code> contains multiple lines, this 
     *          <code>TreeNode</code>'s label will have multiple lines.
     * @param i Sets the <code>id</code> for this <code>TreeNode</code>. Giving
     *          each <code>TreeNode</code> in a <code>Tree</code> a unique
     *          <code>id</code> makes it possible to find a specific 
     *          <code>TreeNode</code> in a <code>Tree</code> structure.
     */
    public TreeNode(String v, int i){
	value = v;
	id = i;
    }

    /** 
     * Constructs a <code>TreeNode</code> by passing in the values for 
     * <code>value</code>, <code>parent</code>, <code>child</code>,
     * <code>sibling</code>, and <code>id</code>.
     * 
     * @param v Specifies the value that appears within the circular node when
     *          this <code>TreeNode</code> is output to the animation file. If
     *          <code>v</code> contains multiple lines, this 
     *          <code>TreeNode</code>'s label will have multiple lines.
     * @param p Indicates the <code>parent</code> value for this 
     *          <code>TreeNode</code>. This is a reference to this
     *          <code>TreeNode</code>'s parent node in a <code>Tree</code>
     *          structure.
     * @param c Sets the <code>child</code> value for this 
     *          <code>TreeNode</code>. This is a reference to this
     *          <code>TreeNode</code>'s left-most child in a <code>Tree</code>
     *          structure.
     * @param s Gives the <code>sibling</code> value for this 
     *          <code>TreeNode</code>. This is a reference to the child to the
     *          right of this <code>TreeNode</code> in a <code>Tree</code>
     *          structure.
     * @param i Sets the <code>id</code> for this <code>TreeNode</code>. Giving
     *          each <code>TreeNode</code> in a <code>Tree</code> a unique
     *          <code>id</code> makes it possible to find a specific 
     *          <code>TreeNode</code> in a <code>Tree</code> structure.
     */
    public TreeNode(String v, TreeNode p, TreeNode c, TreeNode s, int i) {
	value = v;
	parent = p;
	child = c;
	sibling = s;
	id = i;
    }

    /**
     * Constructs a <code>TreeNode</code> that matches the information 
     * contained within the <code>VisNode copy</code>. 
     * This method is useful for making <code>TreeNode</code>s that represent
     * the <code>VisNode</code>s in a <code>VisualGraph</code>, such as in the
     * visualization of graph-searching algorithms. After this method is used
     * create a <code>TreeNode</code> copy of the given <code>VisNode</code>,
     * the rest of this <code>TreeNode</code>'s data can be set via method
     * calls to properly link the copied <code>VisNode</code> into a 
     * <code>Tree</code> structure.
     *
     * @param copy Gives the <code>VisNode</code> that contains the information
     *             that should be used to initialize the data that this 
     *             <code>TreeNode</code> inherits from the <code>VisNode</code>
     *             class. The new <code>TreeNode</code> is constructed to be a
     *             copy of <code>copy</code>.
     */
    public TreeNode(VisNode copy){
	cindex = copy.getChar();
	hexColor = copy.getHexColor();
	cost = copy.getCost();
	heuristic = copy.getHeuristic();
	closed = copy.isClosed();
	pred = copy.getPred();
	x = copy.getX();
	y = copy.getY();
 
	parent = null;
	child = null;
	sibling = null;
    }

    /**
     * Retrieves the <code>value</code> stored within this 
     * <code>TreeNode</code>.
     *
     * @return Gives the <code>String</code> that appears inside this
     *         <code>TreeNode</code> when it is displayed on the screen.
     */
    public String getValue(){ return value; }

    /**
     * Returns a reference to the <code>parent</code> of this 
     * <code>TreeNode</code> in the <code>Tree</code> structure.
     *
     * @return Gives a reference to the <code>TreeNode</code> that is this
     *         <code>TreeNode</code>'s parent in the <code>Tree</code>. This 
     *         value is <code>null</code> if this <code>TreeNode</code> has no 
     *         <code>parent</code>.
     */
    public TreeNode getParent(){ return parent; }

    /**
     * Returns a reference to the <code>child</code> of this 
     * <code>TreeNode</code> in the <code>Tree</code> structure.
     * 
     * @return Yields a reference to the <code>TreeNode</code> that is this
     *         <code>TreeNode</code>'s left-most child in the 
     *         <code>Tree</code>. This value is <code>null</code> if this
     *         <code>TreeNode</code> has no <code>child</code>.
     */
    public TreeNode getChild(){ return child; }

    /**
     * Returns a reference to the <code>sibling</code> of this 
     * <code>TreeNode</code> in the <code>Tree</code> structure.
     *
     * @return Gives a reference to the <code>TreeNode</code> that is the child
     *         to this right of this <code>TreeNode</code> in the 
     *         <code>Tree</code>. This value is <code>null</code> if this
     *         <code>TreeNode</code> has no <code>sibling</code>.
     */
    public TreeNode getSibling(){ return sibling; }

    /**
     * Gives the <code>id</code> assigned to this <code>TreeNode</code>.
     *
     * @return Yields the value stored in <code>id</code>, used to uniquely 
     *         identify <code>TreeNode</code>s in a <code>Tree</code>.
     */
    public int getID(){ return id; }

    /**
     * Returns a reference to the <code>Edge</code> that connects this
     * <code>TreeNode</code> to its <code>parent</code> in the 
     * <code>Tree</code>.
     *
     * @return Gives a reference to the <code>Edge</code> that connects this
     *         <code>TreeNode</code> to its <code>parent</code> in the 
     *         <code>Tree</code>. If this <code>TreeNode</code> has no 
     *         <code>parent</code> or if no <code>lineToParent</code> has been
     *         set for this <code>TreeNode</code>, the value returned will be
     *         <code>null</code>.
     */
    public Edge getLineToParent(){ return lineToParent; }

    /**
     * Indicates if this <code>TreeNode</code> is a left child in a binary
     * <code>Tree</code>.
     *
     * @return Gives a value of <code>true</code> if this <code>TreeNode</code>
     *         is a left child in a binary <code>Tree</code>. A value of 
     *         <code>false</code> is returned if this <code>TreeNode</code> is
     *         a right child or a placeholder or if it is a node in a 
     *         general <code>Tree</code>.
     */
    public boolean isLeftChild(){ return leftChild; }

    /**
     * Indicates if this <code>TreeNode</code> is a right child in a binary 
     * <code>Tree</code>.
     *
     * @return Gives a value of <code>true</code> if this <code>TreeNode</code>
     *         is a right child in a binary <code>Tree</code>. Returns 
     *         <code>false</code> if this <code>TreeNode</code> is a left child
     *         or a placeholder or if it is a node in a general 
     *         <code>Tree</code>.
     */
    public boolean isRightChild(){ return rightChild; }

    /**
     * Indicates if this <code>TreeNode</code> is a placeholder in a binary
     * <code>Tree</code>.
     *
     * @return Yields <code>true</code> if this <code>TreeNode</code> is a 
     *         placeholder in a binary <code>Tree</code>. Returns 
     *         <code>false</code> if this <code>TreeNode</code> is a left child
     *         or a right child or if it is a node in a general
     *         <code>Tree</code>.
     */
    public boolean isPlaceHolder(){ return placeHolder; }

    /**
     * Returns a reference to this <code>TreeNode</code>'s right child in a 
     * binary <code>Tree</code>.
     *
     * @return Gives a reference to this <code>TreeNode</code>'s right child in
     *         a binary tree. Returns <code>null</code> if this 
     *         <code>TreeNode</code> has no right child or if the 
     *         <code>Tree</code> is not a binary <code>Tree</code>.
     */
    public TreeNode getRightChild(){
	if(child != null && child.getSibling() != null && 
	   !child.getSibling().isPlaceHolder() && 
	   child.getSibling().isRightChild()){
	    return child.getSibling();
	}else{
	    return null;
	}
    }

    /**
     * Returns a reference to this <code>TreeNode</code>'s left child in a 
     * binary <code>Tree</code>.
     *
     * @return Gives a reference to this <code>TreeNode</code>'s left child in
     *         a binary tree. Returns <code>null</code> if this 
     *         <code>TreeNode</code> has no left child or if the 
     *         <code>Tree</code> is not a binary <code>Tree</code>.
     */
    public TreeNode getLeftChild(){
	if(child != null && !child.isPlaceHolder() && child.isLeftChild()){
	    return child;
	}else{
	    return null;
	}
    }

    /**
     * Sets this <code>TreeNode</code>'s <code>value</code> that appears
     * inside the circular node when it is drawn on the screen.
     *
     * @param v Specifies the value that should be displayed within this
     *          <code>TreeNode</code> when it is displayed on the screen. If
     *          <code>v</code> contains multiple lines, this 
     *          <code>TreeNode</code>'s label will have multiple lines.
     */
    public void setValue(String v){ value = v; }

    /**
     * Assigns a reference to this <code>TreeNode</code>'s <code>parent</code>.
     * 
     * @param p Indicates the parent of this <code>TreeNode</code> in the 
     *          <code>Tree</code>. A reference to this parent is stored in this
     *          <code>TreeNode</code>'s <code>parent</code> variable.
     */
    public void setParent(TreeNode p){ parent = p; }

    /**
     * Assigns a reference to this <code>TreeNode</code>'s <code>child</code>.
     *
     * @param c Indicates the left-most child of this <code>TreeNode</code> in 
     *          the <code>Tree</code>. A reference to this child is stored in 
     *          this <code>TreeNode</code>'s <code>child</code> variable.
     */
    public void setChild(TreeNode c){ child = c; }

    /**
     * Assigns a reference to this <code>TreeNode</code>'s
     * <code>sibling</code>.
     *
     * @param s Indicates the sibling of this <code>TreeNode</code> in the
     *          <code>Tree</code>. A reference to this sibling is stored in
     *          this <code>TreeNode</code>'s <code>sibling</code> variable.
     */
    public void setSibling(TreeNode s){ sibling = s; }

    /**
     * Sets the <code>id</code> for this <code>TreeNode</code>.
     *
     * @param i Specifies the unique <code>int</code> value that is used to
     *          identify this <code>TreeNode</code>.
     */
    public void setID(int i){ id = i; }

    /**
     * Assigns a reference to this <code>TreeNode</code>'s
     * <code>lineToParent</code>, the <code>Edge</code> that connects this
     * <code>TreeNode</code> with its parent in the <code>Tree</code>.
     *
     * @param e Indicates the <code>Edge</code> that connects this 
     *          <code>TreeNode</code> to its parent in the <code>Tree</code>.
     *          A reference to this <code>Edge</code> is stored in this
     *          <code>TreeNode</code>'s <code>lineToParent</code> variable.
     */
    public void setLineToParent(Edge e){ lineToParent = e; }

    /**
     * Assigns the left child status of this <code>TreeNode</code> in a binary
     * <code>Tree</code>.
     *
     * @param t Specifies the left child status of this <code>TreeNode</code>.
     *          The value of <code>t</code> is assigned to 
     *          <code>leftChild</code>; a value of <code>true</code> means that
     *          it is a left child, and a value of <code>false</code> means
     *          that it is not a left child.
     */
    public void setLeftChild(boolean t){ leftChild = t; }

    /**
     * Assigns the right child status of this <code>TreeNode</code> in a binary
     * <code>Tree</code>.
     *
     * @param t Specifies the right child status of this <code>TreeNode</code>.
     *          The value of <code>t</code> is assigned to
     *          <code>rightChild</code>; a value of <code>true</code> means
     *          that it is a right child, and a value of <code>false</code>
     *          means that it is not a right child.
     */
    public void setRightChild(boolean t){ rightChild = t; }

    /**
     * Assigns the placeholder status of this <code>TreeNode</code> in a binary
     * <code>Tree</code>.
     *
     * @param t Specifies the placeholder status of this <code>TreeNode</code>.
     *          The value of <code>t</code> is assigned to 
     *          <code>placeHolder</code>; a value of <code>true</code> means
     *          that it is a placeholder, and a value of <code>false</code>
     *          means that it is not a placeholder.
     */
    public void setPlaceHolder(boolean t){ placeHolder = t; }

    /**
     * Adds a right child to this <code>TreeNode</code> in a binary 
     * <code>Tree</code>.
     *
     * @param rc Indicates the <code>TreeNode</code> that is to be inserted as
     *           the right child of this <code>TreeNode</code>. A reference to 
     *           <code>rc</code> is assigned to the <code>sibling</code>
     *           variable of the <code>TreeNode</code> referenced by this 
     *           <code>TreeNode</code>'s <code>child</code> variable. If this
     *           <code>TreeNode</code> has no <code>child</code>, a placeholder
     *           left child will be assigned to <code>child</code> so that
     *           <code>rc</code> can be added as its <code>sibling</code>.
     */
    public void insertRightChild(TreeNode rc){
	if(child == null){
	    // Create a placeholder so that the right child can be accessed.
	    child = new TreeNode();
	    child.setValue("");
	    child.setID(-1);
	    child.setParent(this);
	    child.setChild(null);
	    child.setPlaceHolder(true);
	    rc.setParent(this);
	    rc.setLineToParent(new Edge(this, rc));
	    rc.setRightChild(true);
	    child.setSibling(rc);
	}else{
	    rc.setParent(this);
	    rc.setLineToParent(new Edge(this, rc));
	    rc.setRightChild(true);
	    child.setSibling(rc);
	}
    }
   
    /** 
     * Adds a left child to this <code>TreeNode</code> in a binary
     * <code>Tree</code>.
     *
     * @param lc Indicates the <code>TreeNode</code> that is to be inserted as
     *           the left child of this <code>TreeNode</code>. A reference to
     *           <code>lc</code> is assigned to this <code>TreeNode</code>'s
     *           <code>child</code> variable. If <code>child</code> already
     *           contains a reference to a left child or placeholder, its 
     *           <code>sibling</code> link is copied to the new left child.
     */ 
    public void insertLeftChild (TreeNode lc) {
	lc.setParent(this);
	lc.setLineToParent(new Edge(this, lc));
	lc.setLeftChild(true);
	if(child != null)
        {
	    lc.setSibling(child.getSibling());
	}
	child = lc;
    }

    /**
     * Adds a child to this <code>TreeNode</code> and properly sets its 
     * <code>lineToParent</code> with one method call. If this 
     * <code>TreeNode</code> already has a child or children, the new child is
     * added as the right-most child of this <code>TreeNode</code>. Therefore,
     * this method is useful for building general trees as a program executes.
     * 
     * @param c Indicates the <code>TreeNode</code> that should be inserted as
     *          this <code>TreeNode</code>'s right-most child.
     */
    public void setChildWithEdge(TreeNode c){
	if(child == null){
	    child = c;
	    c.setParent(this);
	    child.setLineToParent(new Edge(this, child));
	}else{
	    TreeNode current = child;

	    while(current.getSibling() != null){
		current = current.getSibling();
	    }

	    current.setSibling(c);
	    c.setParent(this);
	    c.setLineToParent(new Edge(this, c));
	}
    }

    /**
     * Adds a sibling to this <code>TreeNode</code> and properly sets its 
     * <code>lineToParent</code> with one method call. If this
     * <code>TreeNode</code> already has a sibling, the new sibling is inserted
     * at the end of the sibling chain to become the right-most child of the
     * parent node. Therefore, this method is useful for building general trees
     * as a program executes.
     *
     * @param s Indicates the <code>TreeNode</code> that should be inserted as
     *          this <code>TreeNode</code>'s <code>parent</code>'s right-most
     *          child.
     */
    public void setSiblingWithEdge(TreeNode s) {

	if(sibling == null){
	    sibling = s;
	    s.setParent(parent);
	    sibling.setLineToParent(new Edge(parent, sibling));
	}else{
	    TreeNode current = sibling;

	    while(current.getSibling() != null){
		current = current.getSibling();
	    }

	    current.setSibling(s);
	    s.setParent(parent);
	    s.setLineToParent(new Edge(parent, s));
	}

    }
}

