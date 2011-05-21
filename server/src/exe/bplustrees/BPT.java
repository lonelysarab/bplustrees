/**************************************************************************************************
 * BPT.java
 * Representation of a B+ Tree.
 *
 *
 * @author William Clements
 * @version March 16 2011
 *************************************************************************************************/
package exe.bplustrees;

import java.io.*;
import java.util.*;
import exe.*;

import exe.pseudocode.*;

public class BPT {

  public static int minimumCapacity = 2;
  public static int order = 4;
  public ArrayList wholeTree; //helps with printing and debugging the tree
  public static boolean isInDebuggingMode = false;

  /*
   * Constructor makes an object the represents a tree
   */
  public BPT() {
  }

  /*
   * Constructor can insert multiple items into the tree
   * @param numberArray   numbers to be inserted into the tree
   */
  public BPT(Integer[] numberArray) throws IOException {
    for (int i = 0; i < numberArray.length; i++) {
      insert(numberArray[i]);
    }
  }


  /*
   * make a snapshot for the visualization
   */
  public void snap(String state, int splitDepth, int x, int line, int color) throws IOException {
    BPlusTree.show.writeSnap(BPlusTree.TITLE, BPlusTree.doc_uri(state),
            BPlusTree.make_uri(state, splitDepth, x, line, color), BPlusTree.visualTree);
  }

  /*
   * make a snapshot for the visualization
   */
  public void snap(String state, int splitDepth, int x, int line, int color, TreeNode n) throws IOException {
    GAIGStree showNewNode = new GAIGStree(false, "New Node", "#000000",0.0, 0.8, 0.2, 0.99, 0.2);
    showNewNode.setRoot(n);
    BPlusTree.show.writeSnap(BPlusTree.TITLE, BPlusTree.doc_uri(state),
            BPlusTree.make_uri(state, splitDepth, x, line, color), BPlusTree.visualTree, showNewNode);
  }

  /*
   * Inserts a number into the tree
   * @param key   the integer that is being inserted
   * @return      true if key did not already exist and was insert
   */
  public boolean insert(int key) throws IOException {
    snap("insert", 0, key, 1, PseudoCodeDisplay.YELLOW);

    //set up current and child objects to assist in traveling down the tree
    BPTNode current = null;
    BPTNode child = BPlusTree.root;
    TreeNode vcurrent = null;
    TreeNode vchild = BPlusTree.visualRoot;

    if (BPlusTree.root == null) {
      snap("insert", 0, key, 2, PseudoCodeDisplay.YELLOW);

      //there is no root. put the key in the root
      BPlusTree.root = new BPTNode(key);

      //update visualization
      BPlusTree.visualRoot.setValue("" + key);
      BPlusTree.visualRoot.setHexColor("#f1f701");
      snap("insert", 0, key, 3, PseudoCodeDisplay.YELLOW);
      BPlusTree.visualRoot.setHexColor("#eeeeff");

    } else {
      snap("insert", 0, key, 4, PseudoCodeDisplay.YELLOW);

      //traverse down to the leaf
      int index = 0;
      while (child != null) {
        
        //prepare to move down the tree
        current = child;

        //update visualization
        vcurrent = vchild;
        vcurrent.setHexColor("#f1f701");
        snap("insert", 0, key, 5, PseudoCodeDisplay.YELLOW);
        vcurrent.setHexColor("#eeeeff");
        vchild = vcurrent.getChild();

        // look through the key list in a node. decide where to go down the tree.
        index = 0;
        while (key > current.keyList.get(index) && index < current.keyList.size() - 1) {
          index++;
          
          //update visualization
          if (vchild != null)
            vchild = vchild.getSibling();
        }

        //child will look ahead down the tree
        if (key >= current.keyList.get(index)) //take the pointer to the right of the number
        {
          child = current.pointerList.get(index + 1);

          //update visualization
          if (vchild != null)
            vchild = vchild.getSibling();
        } else //take the pointer on the left of the number
        {
          child = current.pointerList.get(index);
        }

      }

      //at this point current represents a leaf node
      if (current.keyList.get(index) != key) { //key is not in the leaf

        //the visual tree
        vcurrent.setHexColor("#f1f701");
        snap("insert", 0, key, 15, PseudoCodeDisplay.YELLOW);
        vcurrent.setHexColor("#eeeeff");

        current.addToNode(key, null);

        // the visual tree
        vcurrent.setValue(current.toString());
        vcurrent.setHexColor("#f1f701");
        snap("insert", 0, key, 16, PseudoCodeDisplay.YELLOW);
        vcurrent.setHexColor("#eeeeff");

        //the leaf must be split.
        //there are too many keys in the leaf
        if (current.keyList.size() == order) {
          snap("insert", 0, key, 17, PseudoCodeDisplay.YELLOW);
          snap("insert", 0, key, 18, PseudoCodeDisplay.YELLOW);
          split(current, vcurrent);
        }

      } else {
        snap("insert", 0, key, 21, PseudoCodeDisplay.YELLOW);
        return false; // The integer to be inserted already exists.
      }
    }

    //test print of the root
    if (isInDebuggingMode) {
      System.out.print("current root view: ");
      for (int i = 0; i < BPlusTree.root.keyList.size(); i++) {
        System.out.print(BPlusTree.root.keyList.get(i) + " ");
      }
      System.out.println();
      printTree();
    }

    snap("insert", 0, key, 23, PseudoCodeDisplay.YELLOW);
    return true;
  }

  /*
   * for debugging
   */
  public String printTree() {
    //find depth
    int level = 0;
    BPTNode tempNode = BPlusTree.root;
    wholeTree = new ArrayList<String>();
    while (tempNode != null) {
      wholeTree.add(level, "");
      tempNode = tempNode.pointerList.get(0);
      level++;
    }
    System.out.println("levels present: " + level);
    ArrayList<String> treeArray = printTree(BPlusTree.root, 0);
    String rtnStr = "";
    for (int i = 0; i < treeArray.size(); i++) {
      rtnStr += treeArray.get(i) + "\n";
    }
    return rtnStr;
  }

  /*
   * for debugging
   */
  public ArrayList<String> printTree(BPTNode node, int level) {
    for (int j = 0; j < node.keyList.size(); j++) { //store all the integers in an array
      wholeTree.set(level, "" + wholeTree.get(level) + node.keyList.get(j) + " ");
      if (node.pointerList.get(j) != null) //go down the tree
      {
        printTree(node.pointerList.get(j), level + 1);
      }
    }
    //visit the last pointer on the end of the interior node
    if (node.pointerList.get(node.keyList.size()) != null) {
      printTree(node.pointerList.get(node.keyList.size()), level + 1);
    }
    wholeTree.set(level, "" + wholeTree.get(level) + " | ");
    return wholeTree;
  }

  /*
   * Splits a node or a leaf within the tree and places the 
   * newly created node on the right of the node being split
   * 
   * @param currentNodeToBeSplit    The node that is being split
   * @param vcurrentNodeToBeSplit    A duplicate node being split in the BPlusTree class
   */
  public void split(BPTNode currentNodeToBeSplit, TreeNode vcurrentNodeToBeSplit) throws IOException {
    //update visualization
    BPlusTree.splitScope++; //update visualization
    snap("split", BPlusTree.splitScope, 0, 1, PseudoCodeDisplay.YELLOW);

    //Find the medianIndex
    int medianIndex = (int) Math.ceil(((double) currentNodeToBeSplit.keyList.size()) / 2.0);

    if (currentNodeToBeSplit.pointerList.get(0) == null) { //spliting a leaf
      snap("split", BPlusTree.splitScope, 0, 3, PseudoCodeDisplay.YELLOW);

      //Make a new leaf node. 
      //Move the key at medianIndex and all the numbers > the median from the old leaf to the new
      BPTNode newLeafNode = new BPTNode(currentNodeToBeSplit.keyList.remove(medianIndex));
      currentNodeToBeSplit.pointerList.remove(medianIndex);
      //update visualization
      TreeNode vnewLeafNode = new TreeNode(""+newLeafNode.keyList.get(0));
      vnewLeafNode.setHexColor("#eeeeff");
      vcurrentNodeToBeSplit.setValue(currentNodeToBeSplit.toString());
      vcurrentNodeToBeSplit.setHexColor("#f1f701");
      snap("split", BPlusTree.splitScope, 0, 5, PseudoCodeDisplay.YELLOW, vnewLeafNode);
      vcurrentNodeToBeSplit.setHexColor("#eeeeff");
      while (currentNodeToBeSplit.keyList.size() > medianIndex) {
        newLeafNode.addToNode(currentNodeToBeSplit.keyList.remove(medianIndex), null);
        currentNodeToBeSplit.pointerList.remove(medianIndex);

        //update visualization
        vnewLeafNode = new TreeNode(newLeafNode.toString());
        vcurrentNodeToBeSplit.setValue(currentNodeToBeSplit.toString());
        vcurrentNodeToBeSplit.setHexColor("#f1f701");
        snap("split", BPlusTree.splitScope, 0, 11, PseudoCodeDisplay.YELLOW, vnewLeafNode);
        vcurrentNodeToBeSplit.setHexColor("#eeeeff");
      }

      //a new node is made at the root
      if (currentNodeToBeSplit.parentPointer == null) 
      {
        snap("split", BPlusTree.splitScope, 0, 16, PseudoCodeDisplay.YELLOW, vnewLeafNode);
        
        //create a new parent
        BPTNode newParentNode = new BPTNode(newLeafNode.keyList.get(0));
        BPlusTree.root = newParentNode;

        //set the pointers that point down
        newParentNode.pointerList.set(0, currentNodeToBeSplit);
        newParentNode.pointerList.set(1, newLeafNode);

        //Fix the pointers on the leaf that was split
        currentNodeToBeSplit.parentPointer = newParentNode;
        currentNodeToBeSplit.rightLeaf = newLeafNode;        

        //the visual tree
        TreeNode vnewParentNode = new TreeNode("" + newLeafNode.keyList.get(0));
        vnewParentNode.setHexColor("#eeeeff");
        BPlusTree.visualRoot = vnewParentNode;
        BPlusTree.visualTree.setRoot(BPlusTree.visualRoot);
        vnewParentNode.setChildWithEdge(vcurrentNodeToBeSplit);
        vnewParentNode.setChildWithEdge(vnewLeafNode);
        vnewParentNode.setParent(vcurrentNodeToBeSplit.getParent());
        vnewParentNode.setValue(newParentNode.toString());
        snap("split", BPlusTree.splitScope, 0, 23, PseudoCodeDisplay.YELLOW);

      } else // a parent exist. attach node. pass a number up to the parent
      {
        snap("split", BPlusTree.splitScope, 0, 25, PseudoCodeDisplay.YELLOW);

        //adjust pointers
        newLeafNode.parentPointer = currentNodeToBeSplit.parentPointer;
        newLeafNode.rightLeaf = currentNodeToBeSplit.rightLeaf;
        currentNodeToBeSplit.rightLeaf = newLeafNode;
        currentNodeToBeSplit.getParent().addToNode(newLeafNode.keyList.get(0),newLeafNode);

        //update visualization
        vcurrentNodeToBeSplit.getParent().setChildWithEdge(new TreeNode());
        TreeNode updatingVisualNode = vcurrentNodeToBeSplit.getParent().getChild();
        for (int i=0; i<currentNodeToBeSplit.getParent().keyList.size()+1; i++) {
          updatingVisualNode.setValue(currentNodeToBeSplit.getParent().pointerList.get(i).toString());
          updatingVisualNode.setHexColor("#eeeeff");
          updatingVisualNode = updatingVisualNode.getSibling();
        }
        vcurrentNodeToBeSplit.getParent().setValue(currentNodeToBeSplit.getParent().toString());
        snap("split", BPlusTree.splitScope, 0, 26, PseudoCodeDisplay.YELLOW);

        if (currentNodeToBeSplit.parentPointer.keyList.size() == order) {
          snap("split", BPlusTree.splitScope, 0, 27, PseudoCodeDisplay.YELLOW);
          snap("split", BPlusTree.splitScope, 0, 28, PseudoCodeDisplay.YELLOW);
          split(currentNodeToBeSplit.parentPointer, vcurrentNodeToBeSplit.getParent());
        }
      }

    } else { //spliting a node
      snap("split", BPlusTree.splitScope, 0, 30, PseudoCodeDisplay.YELLOW);

      //Make a new node. put the median and all the numbers > the median in this node
      BPTNode newNodeOnTheRight = new BPTNode(currentNodeToBeSplit.keyList.remove(medianIndex));
      currentNodeToBeSplit.pointerList.remove(medianIndex);
      newNodeOnTheRight.parentPointer = currentNodeToBeSplit.parentPointer;
      newNodeOnTheRight.rightLeaf = null;
      newNodeOnTheRight.leftLeaf = null;
      while (currentNodeToBeSplit.keyList.size() > medianIndex) {
        newNodeOnTheRight.addToNode(currentNodeToBeSplit.keyList.remove(medianIndex), null);
        currentNodeToBeSplit.pointerList.remove(medianIndex);
      }

      currentNodeToBeSplit.rightLeaf = null;
      currentNodeToBeSplit.leftLeaf = null;

      if (currentNodeToBeSplit.parentPointer == null) { //a new node is made at the root
        BPTNode newParentNode = new BPTNode(newNodeOnTheRight.keyList.get(0));
        newParentNode.pointerList.set(0, currentNodeToBeSplit);
        newParentNode.pointerList.set(1, newNodeOnTheRight);
        currentNodeToBeSplit.parentPointer = newParentNode;
        BPlusTree.root = newParentNode;
      } else { // a parent exist. pass a number up to the parent
        snap("split", BPlusTree.splitScope, 0, 49, PseudoCodeDisplay.YELLOW);
        currentNodeToBeSplit.parentPointer.addToNode(newNodeOnTheRight.keyList.get(0),
                newNodeOnTheRight);
        snap("split", BPlusTree.splitScope, 0, 51, PseudoCodeDisplay.YELLOW);
        if (currentNodeToBeSplit.parentPointer.keyList.size() == order) {
          snap("split", BPlusTree.splitScope, 0, 52, PseudoCodeDisplay.YELLOW);
          split(currentNodeToBeSplit.parentPointer, vcurrentNodeToBeSplit.getParent());
        }
      }
    }

    BPlusTree.splitScope--;
    return; //successfully inserted
  }

  /*
   * Deletes a number in the tree
   * @param key   the integer to be removed
   * @return      true if key existed and was removed
   */
  public boolean delete(int key) throws IOException {
    snap("delete", 0, key, 1, PseudoCodeDisplay.YELLOW);

    //make your way down the tree
    BPTNode parent = null;
    BPTNode current = BPlusTree.root;

    TreeNode vparent = null;
    TreeNode vcurrent = BPlusTree.visualRoot;

    int index = 0;
    while (current != null) {

      // look through the key set in a node. decide where to go down the tree.
      index = 0;
      while (key > current.keyList.get(index) && index < current.keyList.size() - 1) {
        index++;
      }

      //move down the tree. choose a direction.
      parent = current;
      if (key >= parent.keyList.get(index)) //take the pointer to the right of the number
      {
        current = parent.pointerList.get(index + 1);
      } else //take the pointer on the left of the number
      {
        current = parent.pointerList.get(index);
      }

      //the visual tree
      vparent = vcurrent;
      vcurrent = vparent.getChild();
      vparent.setHexColor("#f1f701");
      snap("delete", 0, key, 6, PseudoCodeDisplay.YELLOW);
      vparent.setHexColor("#eeeeff");
    }

    //parent == leaf node
    if (parent.keyList.get(index) == key) { //key is, in fact, in the leaf

      //remove key from the leaf
      parent.keyList.remove(index);
      parent.pointerList.remove(0); //all these pointers are blank

      // the visual tree
      vparent.setValue(parent.toString());
      vparent.setHexColor("#f1f701");
      snap("delete", 0, key, 14, PseudoCodeDisplay.YELLOW);
      vparent.setHexColor("#eeeeff");

      //If the number of elements in the leaf falls below minimumCapacity
      //things need to be rearranged.
      if (parent.keyList.size() < minimumCapacity) {

        //distribute elements evenly between two adjacted nodes
        if (parent.rightLeaf != null && parent.parentPointer != null
                && parent.parentPointer.keyList.get(0) == parent.rightLeaf.parentPointer.keyList.get(0)
                && parent.rightLeaf.keyList.size() < minimumCapacity) {


          int medianIndex = (int) Math.ceil(((double) parent.keyList.size()) / 2.0);
          for (int i = 0; i < medianIndex; i++) {
            parent.addToNode(parent.rightLeaf.keyList.get(0), null);
            parent.rightLeaf.keyList.remove(0);
            parent.rightLeaf.pointerList.remove(0);
          }
          int indxOfKeyToAdjacentLeaf = parent.parentPointer.keyList.indexOf(parent.keyList.get(0)) + 1;
          parent.parentPointer.keyList.set(indxOfKeyToAdjacentLeaf, parent.rightLeaf.keyList.get(0));

          // the visual tree
          vparent.setValue(parent.toString());
          vparent.setHexColor("#f1f701");
          snap("delete", 0, key, 20, PseudoCodeDisplay.YELLOW);
          vparent.setHexColor("#eeeeff");
          vparent.getSibling().setValue(parent.rightLeaf.toString());
          vparent.getSibling().setHexColor("#f1f701");
          snap("delete", 0, key, 24, PseudoCodeDisplay.YELLOW);
          vparent.getSibling().setHexColor("#eeeeff");
          vparent.getParent().setValue(parent.parentPointer.toString());
          vparent.getParent().setHexColor("#f1f701");
          snap("delete", 0, key, 25, PseudoCodeDisplay.YELLOW);
          vparent.getParent().setHexColor("#eeeeff");

        } else //distributing elements cannot be done. there are too few elements.
        {
          for (int i = 0; i < parent.rightLeaf.keyList.size(); i++) {
            parent.addToNode(parent.rightLeaf.keyList.get(i), null);
          }
          int indexOfKeyToRemove = parent.parentPointer.keyList.indexOf(parent.keyList.get(0)) + 1;
          parent.parentPointer.keyList.remove(indexOfKeyToRemove);
          parent.parentPointer.pointerList.remove(indexOfKeyToRemove + 1);
          if (parent.rightLeaf.rightLeaf != null) {
            parent.rightLeaf.rightLeaf = parent;
            parent.rightLeaf = parent.rightLeaf.rightLeaf;
          } else {
            parent.rightLeaf = null;
          }

          // the visual tree
          vparent.setValue(parent.toString());
          if (vparent.getSibling() != null)
            vparent.getSibling().deactivate();
          vparent.setHexColor("#f1f701");
          snap("delete", 0, key, 30, PseudoCodeDisplay.YELLOW);
          vparent.setHexColor("#eeeeff");

        }

      }
    } else //key is not in the leaf
    {
      snap("delete", 0, key, 41, PseudoCodeDisplay.YELLOW);
      return false;
    }

    snap("delete", 0, key, 42, PseudoCodeDisplay.YELLOW);
    return true; //successfully deleted
  }
}
