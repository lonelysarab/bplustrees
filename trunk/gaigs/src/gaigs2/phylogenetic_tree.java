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
import java.util.List;
import java.awt.image.*;
import java.text.DecimalFormat;

import org.jdom.*;
import org.jdom.input.SAXBuilder;


public class phylogenetic_tree extends StructureType  {

	phylo_treeNode treeRoot = new phylo_treeNode();
   
	public static void main(String [] args) {
		phylogenetic_tree t = new phylogenetic_tree();
		System.out.println("In main");
		System.out.println(args[0]);
		
		SAXBuilder builder = new SAXBuilder();
		
		try {
			
			Document doc = builder.build(args[0]);
			Element root = doc.getRootElement();

			System.out.println(root.getName());

			
			t.loadStructure(root, new LinkedList(), null);
		}
		catch (JDOMException e){System.out.println("JDOM exception");}
		catch (IOException e){System.out.println("IOException" + e);}
		catch (Exception e){System.out.println("Something here "+e);}
	}
	

    
    public phylogenetic_tree() {

    }
    
    public boolean emptyStruct()  {
//        if (nodelist.size() == 0) {
//            return true;
//        } else {
            return false;
//        }
    }
    

   
    void drawStructure(LinkedList llist, draw d)  {
        
        if (emptyStruct()) {
            super.drawStructure(llist,d);  // to handle empty structure
            return;
        }
        else {
        	super.drawStructure(llist, d);
  
        	drawTree(llist, d, treeRoot);
//        	LGKS.set_fill_int_style(bsSolid, White, llist, d);
//        	LGKS.circle_fill(0.5, 0.5, 0.25, llist, d);
        }
    }
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    void drawTree(LinkedList llist, draw d, phylo_treeNode curr){
      	LGKS.set_text_height(Textheight/2.0, llist, d);
    	double circleRadius = Textheight/8;
//    	System.out.println("Color is "+ curr.getNodeColor());
//    	System.out.println("Color int is "+colorStringToInt(curr.getNodeColor()));

    	//Has parent. Draw line to parent and label the edge.
    	if (curr.getParent() != null) 
    	{
    		double[] ptsx = new double[2];
    		ptsx[0] = curr.getX();
    		ptsx[1] = curr.getParent().getX();
    		double[] ptsy = new double[2];
    		ptsy[0] = curr.getY();
    		ptsy[1] = curr.getParent().getY();
    		LGKS.polyline(2, ptsx, ptsy, llist, d);
//    		LGKS.set_text_height(.03, llist, d);
    		LGKS.set_text_align(1, 0, llist, d);
    		String label = decimalFormat.format(curr.getWeight()) + "   ";
    		LGKS.text((ptsx[0]+ptsx[1])/2.0,(ptsy[0]+ptsy[1])/2.0 , label, llist, d);
    	}
    	//Interior Node, set smaller radius and recurse.
    	if (curr.getLeftChild() != null)
    	{
    		circleRadius = Textheight/12;
    		drawTree(llist, d, curr.getLeftChild());
    		drawTree(llist, d, curr.getRightChild());
    	}
    	//No children, ie leaf. Draw node label.
    	else {
    		double ptsx = curr.getX();
    		double ptsy = curr.getY() + .05;
//    		LGKS.set_text_height(.08, llist, d);
          	LGKS.set_text_height(Textheight, llist, d);
    		LGKS.set_text_align(0, 0, llist, d);
    		LGKS.text(ptsx, ptsy, curr.getLabel(), llist, d);
    		}
    	//All Nodes draw a circle. Radius changes when node is a interior node.
    	int color = colorStringToInt(curr.getNodeColor());
    	LGKS.set_fill_int_style(bsSolid, color, llist, d);
    	LGKS.circle_fill(curr.getX(), curr.getY(), circleRadius, llist, d);

    }

    
    public void loadStructure(StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException  {} 

    public void loadStructure(Element tree, LinkedList llist, draw d)
	throws VisualizerLoadException {
	
	load_name_and_bounds(tree,llist,d);
	Element root = tree.getChild("binary_node");

	treeRoot.setNodeColor(root.getAttributeValue("color"));
	treeRoot.setLabel(root.getChild("label").getText());
	
	makeTree(root, treeRoot);
	leafGaps = countLeaves(treeRoot) -1;
	count = 0;
	setLeaves(treeRoot);
	fillLocs(treeRoot);
    }
    
    /**
     * Gets the leftmost desendant of a node.
     * @param curr
     * @return
     */
	public phylo_treeNode leftMostNode(phylo_treeNode curr){
		if (curr.getLeftChild() != null)
			return leftMostNode(curr.getLeftChild());
		else 
			return curr;
	}
/**
 * Gets the rightmost desendant of a node.	
 * @param curr
 * @return
 */
	public phylo_treeNode rightMostNode(phylo_treeNode curr){
		if (curr.getRightChild() != null)
			return rightMostNode(curr.getRightChild());
		else 
			return curr;
	} 
    
   /**
    * Fills interior nodes x and y locations.
    *  
    * @param curr - (sub)tree root node to have loctions
    * below filled in.
    */
	public void fillLocs (phylo_treeNode curr) {
		if (curr.isNotLeaf()) 
		{	
			double x1, x2, y;
			x1 = leftMostNode(curr).getX();
			x2 = rightMostNode(curr).getX();
			x2 = (x1+x2)/2;
			curr.setX(x2);
			y = leftMostNode(curr).getY();
			curr.setY(y - (rightMostNode(curr).getX() -x1)/1);
//			curr.setY(y - (x2 -x1));
//			System.out.println(curr.getX() + " " + curr.getY());
		}
		
		if (curr.getLeftChild() != null)
			fillLocs(curr.getLeftChild());
		if (curr.getRightChild() != null)
			fillLocs(curr.getRightChild());
	}
    
    /**
     * Recursive call to count the number of leaves in a tree.
     * Set leafGaps to be 1 less than the return.
     * 
     * @param root
     * @return # of leaves in the tree.
     */
	public int countLeaves(phylo_treeNode root){
		if (root.getLeftChild()==null)
			return 1;
		else return (countLeaves(root.getLeftChild()) + countLeaves(root.getRightChild()));
	}
	private int count;
	private int leafGaps;
	
	/**Sets the X and Y values for each leaf.
	 * Uses class level variables count and leafGaps
	 * to evenly space the leaves x values . leafGaps must be 1 less
	 * than the actual leafCount.
	 * 
	 * @param root
	 */
	public void setLeaves(phylo_treeNode root){
		//Trees have 2 children or 0, so if left is null, right is also null.
		//leafGaps = 0, only 1 leaf in the tree, ie a root. must explicitly set its 
		//x value, otherwise it gets a 0/0.0, or NaN.
		if (leafGaps ==0)
		{
			root.setX(0.0);
			root.setY(1.0);
		}
		else if (root.getLeftChild()==null){
			root.setX(count/ (double)leafGaps);
			root.setY(1.0);
			count++;
//			System.out.println(root.getX() + " " + root.getY());
		}
		else  {
			setLeaves(root.getLeftChild());
			setLeaves(root.getRightChild());
		}
	}
    /**Moves through the JDOM tree to create
     * a tree copy of the tree using phylo_treeNodes.
     * 
     * 
     * @param root -JDOM element, at the level of Binary Node for the inital
     * call, subsequent calls at level of left and right nodes.
     * @param currentNode - Root of the (sub)tree being constructed.
     */
	
    public void makeTree(Element root, phylo_treeNode currentNode){
    
    	Element left = root.getChild("left_node");
    	if ( left != null)
    	{
    		phylo_treeNode lchild = new phylo_treeNode(currentNode);
    		currentNode.setLeftChild(lchild);
    		double weight = Double.parseDouble(((Element)(root.getChildren("tree_edge").get(0))).getChild("label").getText());
    		String edgeColor = ((Element)(root.getChildren("tree_edge").get(0))).getAttributeValue("color");
    		String nodeColor = left.getAttributeValue("color");
    		String nodeLabel = left.getChild("label").getText();
    		lchild.setWeight(weight);
    		lchild.setEdgeColor(edgeColor);
    		lchild.setNodeColor(nodeColor);
    		if (nodeLabel != null)
    			lchild.setLabel(nodeLabel);
    		else
    			lchild.setLabel(" ");
    
    		makeTree(left, lchild);
    	}
    	Element right = root.getChild("right_node");
    	if (right != null)
    	{
    		phylo_treeNode rchild = new phylo_treeNode(currentNode);
    		currentNode.setRightChild(rchild);
    		double weight = Double.parseDouble(((Element)(root.getChildren("tree_edge").get(1))).getChild("label").getText());
    		String edgeColor = ((Element)(root.getChildren("tree_edge").get(1))).getAttributeValue("color");
    		String nodeColor = right.getAttributeValue("color");
    		String nodeLabel = right.getChild("label").getText();
    		rchild.setWeight(weight);
    		rchild.setEdgeColor(edgeColor);
    		rchild.setNodeColor(nodeColor);
    		if (nodeLabel != null)
    			rchild.setLabel(nodeLabel);
    		else
    			rchild.setLabel(" ");
    
    		makeTree(right, rchild);
    	}
    }
    
    private class phylo_treeNode {
		phylo_treeNode left, right,parent;
		double x= -999, y=-999;
		double edgeWeight;
		String edgeColor, label, nodeColor = new String("#000000");

		public phylo_treeNode() {
		}
		public phylo_treeNode(phylo_treeNode parent) {
			this.parent = parent;
		}
		
		public void setRightChild(phylo_treeNode rChild) {right = rChild;}
		public void setLeftChild(phylo_treeNode lChild) {left = lChild;}
		
		public phylo_treeNode getRightChild(){return right;}
		public phylo_treeNode getLeftChild(){return left;}
		
		public void setX(double x){this.x = x;}
		public void setY(double y){this.y = y;}
		
		public double getX(){return x;}
		public double getY(){return y;}
		
		public void setWeight(double weight){edgeWeight = weight;}
		public double getWeight(){return edgeWeight;}
		
		public void setEdgeColor(String edgeColor){this.edgeColor = edgeColor;}
		public String getNodeColor(){return nodeColor;}
		
		public void setNodeColor(String nodeColor){this.nodeColor = nodeColor;}
		public String getEdgeColor(){return edgeColor;}
		
		public void setLabel(String label){this.label = label;}
		public String getLabel(){return label;}
		
		public phylo_treeNode getParent(){return parent;}
		public void setParent(phylo_treeNode parent){this.parent = parent;;}
		
		public boolean isNotLeaf(){
			if (left == null || right == null)
				return false;
			else return true;
		}	
	}
}