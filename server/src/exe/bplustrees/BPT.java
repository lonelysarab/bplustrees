/**************************************************************************************************
 * BPT.java
 * Representation of a B+ Tree.
 *
 *
 * @author William Clements
 * @version May 21, 2010
 *************************************************************************************************/
package exe.bplustrees;

import java.io.*;
import java.util.*;
import exe.*;
import exe.pseudocode.*;

/*
 * Object representation of a tree in memory. Multiple nodes can be made within this class.
 */
public class BPT {

  /*
   * The order of the tree. When this many keys are inserted into a leaf, the leaf is split.
   */
  public static final int ORDER = 4;

  /*
   * If keys are being deleted from a leaf and the size of the keyList becomes this, a merge or
   * redistribution is performed.
   */
  public static final int MINIMUM_CAPACITY = ORDER / 2;

  /*
   * There is only one root in a B+ Tree.
   */
  public TreeNode root;

  /*
   * Constructor makes an object that represents a tree
   */
  public BPT() {
    root = new TreeNode();
  }

  /*
   * Constructor can insert multiple items into the tree
   * @param numberArray   numbers to be inserted into the tree
   */
  public BPT(int[] numberArray) throws IOException {
    root = new TreeNode();
    for (int i = 0; i < numberArray.length; i++) 
      insert(numberArray[i]);
  }

  /*
   * Inserts a number into the tree
   * 
   * @param key   The integer that is being inserted.
   * @return      True if key did not already exist and was insert.
   */
  public boolean insert(int key) throws IOException {
    BPlusTree.snap("insert", 0, key, 1, PseudoCodeDisplay.YELLOW);

    if (root.getValue() == null) {
      BPlusTree.visualTree.setRoot(root);
      BPlusTree.snap("insert", 0, key, 2, PseudoCodeDisplay.YELLOW);
      root.setValue("" + key + " ");
      root.setHexColor("#f1f701"); //highlight it
      BPlusTree.snap("insert", 0, key, 3, PseudoCodeDisplay.YELLOW);
      root.setHexColor("#eeeeff"); //unhighlight the node

    } else {

      //Set up pointer named current to assist in traveling down the tree
      TreeNode currentNode = root;

      //Update the visualization
      currentNode.setHexColor("#f1f701");
      BPlusTree.snap("insert", 0, key, 5, PseudoCodeDisplay.YELLOW);
      currentNode.setHexColor("#eeeeff");

      //Traverse down the tree.
      while (currentNode.getChild() != null) {
        //Grab all the keys in the node
        String[] keyList = currentNode.getValue().split(" ");
        //Look through the keys in the root node.
        int index = 0;
        while (Integer.parseInt(keyList[index]) < key && index < keyList.length - 1) {
          index++;
        }

        currentNode = currentNode.getChild(); //Go down

        //Go down and to the right of the key.
        if (key >= Integer.parseInt(keyList[index])) {
          for (int i = 0; i < index; i++) {
            currentNode = currentNode.getSibling();
          }
          currentNode = currentNode.getSibling();

        } else //Go down and to the left of the key.
        {
          for (int i = 0; i < index; i++) {
            currentNode = currentNode.getSibling();
          }

        }

      }

      //"current" is at a leaf node. If the key is NOT in the leaf, insert it.
      if (keyIsAbsentInList(currentNode.getValue().split(" "), key)) {

        //update visual tree
        currentNode.setHexColor("#f1f701");
        BPlusTree.snap("insert", 0, key, 15, PseudoCodeDisplay.YELLOW);

        //properly add it into the leaf
        addKeyToLeaf(currentNode, key);
        
        //update visual tree
        BPlusTree.id++;
        BPlusTree.tf.setQuestionText("Will a split be performed?");
        BPlusTree.snap("insert", 0, key, 16, PseudoCodeDisplay.YELLOW, BPlusTree.tf);
        currentNode.setHexColor("#eeeeff");

        //There are too many keys in the leaf. Split it.
        String[] keyList = currentNode.getValue().split(" ");
        if (keyList.length == ORDER) {
          BPlusTree.snap("insert", 0, key, 17, PseudoCodeDisplay.YELLOW);
          BPlusTree.snap("insert", 0, key, 18, PseudoCodeDisplay.YELLOW);
          split(currentNode);
        } else {
          BPlusTree.tf.setAnswer(false);
        }

      } else // The integer to be inserted already exists.
      {
        BPlusTree.snap("insert", 0, key, 21, PseudoCodeDisplay.YELLOW);
        return false; 
      }
    }

    BPlusTree.snap("insert", 0, key, 23, PseudoCodeDisplay.YELLOW);
    return true;
  }

  /*
   * Add a key into string containing numbers. They string will be in ascending order.
   * @param theNode       The Node you want a key added to.
   * @param int key       The integer you want to put in the list.
   * @return              nothing
   */
  public void addKeyToLeaf(TreeNode theNode, int key) {
    String lst = theNode.getValue();
    String[] originalList = lst.split(" ");
    String returnStr = "";

    if (lst.compareTo("") != 0) {
      for (int i = 0; i < originalList.length; i++) {
        if (key < Integer.parseInt(originalList[i])) {
          returnStr += "" + key + " ";
          key = Integer.parseInt(originalList[i]);
        } else {
          returnStr += originalList[i] + " ";
        }
      }
    }

    returnStr += "" + key + " ";
    theNode.setValue(returnStr);
    return;
  }

  /*
   * Search a String array for an integer
   * @param String[] strArray   contains some numbers. ex {"3","4"}
   * @param int  key            contains the key you are searching for. ex: 4
   * @return True if the key does not exist in the list.
   */
  public boolean keyIsAbsentInList(String[] strArray, int key) {
    boolean returnStatement = true;
    int i = 0;

    while (i < strArray.length && returnStatement) {
      if (Integer.parseInt(strArray[i]) == key) {
        returnStatement = false;
      }
      i++;
    }
    return returnStatement;
  }

  /*
   * Splits a node or a leaf within the tree and places the 
   * newly created node on the right of the node being split
   * 
   * @param currentNode    The node that is being split
   * 
   */
  public void split(TreeNode currentNode) throws IOException {
    BPlusTree.splitScope++;

    //update visualization
    BPlusTree.tf.setAnswer(true);
    BPlusTree.snap("split", BPlusTree.splitScope, 0, 1, PseudoCodeDisplay.YELLOW);

    //Find the medianIndex
    String[] keyList = currentNode.getValue().split(" ");
    int medianIndex = (int) Math.ceil(((double) keyList.length) / 2.0);

    //Split a leaf.
    if (currentNode.getChild() == null) {
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 3, PseudoCodeDisplay.YELLOW);    //////////////////////////BREAKS ON THIS ONE

      //Make a new leaf node.
      TreeNode newLeafNode = new TreeNode("");
      newLeafNode.setHexColor("#eeeeff");

      //Put all the keys >= the median into the newLeafNode
      for (int i=medianIndex; i<keyList.length; i++)
        addKeyToLeaf(newLeafNode,removeKeyAtIndex(currentNode,medianIndex));

      //Update visualization.
      currentNode.setHexColor("#f1f701");
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 5, PseudoCodeDisplay.YELLOW, newLeafNode);
      currentNode.setHexColor("#eeeeff");

      //If the root was split, a new root is made.
      if (currentNode.getParent() == null) {
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 16, PseudoCodeDisplay.YELLOW, newLeafNode);

        //Make a new parent.
        TreeNode newParentNode = new TreeNode();
        root = newParentNode;
        BPlusTree.visualTree.setRoot(root);
        newParentNode.setHexColor("#eeeeff");
        
        //Set the pointer going down and to the left.
        newParentNode.setChildWithEdge(currentNode);
        //Set the pointer going down and to the right.
        newParentNode.setChildWithEdge(newLeafNode);
        //Send a key up to the new parent from the new leaf.
        newParentNode.setValue(newLeafNode.getValue().split(" ")[0]);
        
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 23, PseudoCodeDisplay.YELLOW);

      } else // A parent exist. Attach the newLeafNode. Pass the first key up to the parent.
      {
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 25, PseudoCodeDisplay.YELLOW);
      }

    } else //Split a node.
    {
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 30, PseudoCodeDisplay.YELLOW);
    }
/*
      } else // a parent exist. attach node. pass a number up to the parent
      {
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 25, PseudoCodeDisplay.YELLOW);

        //adjust pointers
        newLeafNode.parentPointer = currentNodeToBeSplit.parentPointer;
        newLeafNode.rightLeaf = currentNodeToBeSplit.rightLeaf;
        currentNodeToBeSplit.rightLeaf = newLeafNode;
        currentNodeToBeSplit.getParent().addToNode(newLeafNode.keyList.get(0), newLeafNode);

        //update visualization
        vcurrentNodeToBeSplit.getParent().setChildWithEdge(new TreeNode());
        TreeNode updatingVisualNode = vcurrentNodeToBeSplit.getParent().getChild();
        for (int i = 0; i < currentNodeToBeSplit.getParent().keyList.size() + 1; i++) {
          updatingVisualNode.setValue(currentNodeToBeSplit.getParent().pointerList.get(i).toString());
          updatingVisualNode.setHexColor("#eeeeff");
          updatingVisualNode = updatingVisualNode.getSibling();
        }
        vcurrentNodeToBeSplit.getParent().setValue(currentNodeToBeSplit.getParent().toString());
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 26, PseudoCodeDisplay.YELLOW);

        if (currentNodeToBeSplit.parentPointer.keyList.size() == ORDER) {
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 27, PseudoCodeDisplay.YELLOW);
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 28, PseudoCodeDisplay.YELLOW);
          split(currentNodeToBeSplit.parentPointer, vcurrentNodeToBeSplit.getParent());
        }
      }

    } else { //spliting a node
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 30, PseudoCodeDisplay.YELLOW);

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

      } else  // a parent exist. pass a number up to the parent
      {
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 49, PseudoCodeDisplay.YELLOW);
        currentNodeToBeSplit.parentPointer.addToNode(newNodeOnTheRight.keyList.get(0),
                newNodeOnTheRight);
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 51, PseudoCodeDisplay.YELLOW);
        if (currentNodeToBeSplit.parentPointer.keyList.size() == ORDER)
        {
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 52, PseudoCodeDisplay.YELLOW);
          split(currentNodeToBeSplit.parentPointer, vcurrentNodeToBeSplit.getParent());
        }
      }
    }
*/
    BPlusTree.splitScope--;
    return; //successfully inserted
  }

  /* Removes a key out of a keylist in a node.
   *
   * @param TreeNode theNode  The node you want a key removed from.
   * @param int index         The index at which you want a key removed.
   * @return                  The key that was removed.
   */
  public int removeKeyAtIndex(TreeNode theNode, int index) {
    String lst = theNode.getValue();
    String[] originalList = lst.split(" ");
    String returnStr = "";

    for (int i = 0; i < originalList.length; i++) {
      if (i != index) 
        returnStr += originalList[i] + " ";
    }

    theNode.setValue(returnStr);
    return Integer.parseInt(originalList[index]);
  }

  /*
   * Deletes a number in the tree.
   *
   * @param key   The integer to be removed.
   * @return      True if key existed and was removed.
   * 
   */
  public boolean delete(int key) throws IOException {
    BPlusTree.snap("delete", 0, key, 1, PseudoCodeDisplay.YELLOW);

    //set up current and child objects to assist in traveling down the tree
    BPTNode current = null;
    BPTNode child = BPlusTree.root;
    TreeNode vcurrent = null;
    TreeNode vchild = root;

    //traverse down to the leaf
    int index = 0;
    while (child != null) {

      //prepare to move down the tree
      current = child;

      //update visualization
      vcurrent = vchild;
      vcurrent.setHexColor("#f1f701");
      BPlusTree.snap("insert", 0, key, 5, PseudoCodeDisplay.YELLOW);
      vcurrent.setHexColor("#eeeeff");
      vchild = vcurrent.getChild();

      // look through the key list in a node. decide where to go down the tree.
      index = 0;
      while (key > current.keyList.get(index) && index < current.keyList.size() - 1) {
        index++;

        //update visualization
        if (vchild != null) {
          vchild = vchild.getSibling();
        }
      }

      //child will look ahead down the tree
      if (key >= current.keyList.get(index)) //take the pointer to the right of the number
      {
        child = current.pointerList.get(index + 1);

        //update visualization
        if (vchild != null) {
          vchild = vchild.getSibling();
        }
      } else //take the pointer on the left of the number
      {
        child = current.pointerList.get(index);
      }

    }

    //at this point current represents a leaf node
    if (current.keyList.get(index) == key) { //key is, in fact, in the leaf

      //remove key from the leaf
      current.keyList.remove(index);
      current.pointerList.remove(0); //all these pointers are blank

      // the visual tree
      vcurrent.setValue(current.toString());
      vcurrent.setHexColor("#f1f701");
      BPlusTree.snap("delete", 0, key, 14, PseudoCodeDisplay.YELLOW);
      vcurrent.setHexColor("#eeeeff");

      //If the number of elements in the leaf falls below minimumCapacity
      //things need to be rearranged.
      if (current.keyList.size() < MINIMUM_CAPACITY) {

        //distribute elements evenly between two adjacted nodes
        if (current.rightLeaf != null && current.parentPointer != null
                && current.parentPointer.keyList.get(0) == current.rightLeaf.parentPointer.keyList.get(0)
                && current.rightLeaf.keyList.size() < MINIMUM_CAPACITY) {


          int medianIndex = (int) Math.ceil(((double) current.keyList.size()) / 2.0);
          for (int i = 0; i < medianIndex; i++) {
            current.addToNode(current.rightLeaf.keyList.get(0), null);
            current.rightLeaf.keyList.remove(0);
            current.rightLeaf.pointerList.remove(0);
          }
          int indxOfKeyToAdjacentLeaf = current.parentPointer.keyList.indexOf(current.keyList.get(0)) + 1;
          current.parentPointer.keyList.set(indxOfKeyToAdjacentLeaf, current.rightLeaf.keyList.get(0));

          // the visual tree
          vcurrent.setValue(current.toString());
          vcurrent.setHexColor("#f1f701");
          BPlusTree.snap("delete", 0, key, 20, PseudoCodeDisplay.YELLOW);
          vcurrent.setHexColor("#eeeeff");
          vcurrent.getSibling().setValue(current.rightLeaf.toString());
          vcurrent.getSibling().setHexColor("#f1f701");
          BPlusTree.snap("delete", 0, key, 24, PseudoCodeDisplay.YELLOW);
          vcurrent.getSibling().setHexColor("#eeeeff");
          vcurrent.getParent().setValue(current.parentPointer.toString());
          vcurrent.getParent().setHexColor("#f1f701");
          BPlusTree.snap("delete", 0, key, 25, PseudoCodeDisplay.YELLOW);
          vcurrent.getParent().setHexColor("#eeeeff");

        } else //distributing elements cannot be done. there are too few elements.
        {
          for (int i = 0; i < current.rightLeaf.keyList.size(); i++) {
            current.addToNode(current.rightLeaf.keyList.get(i), null);
          }
          int indexOfKeyToRemove = current.parentPointer.keyList.indexOf(current.keyList.get(0)) + 1;
          current.parentPointer.keyList.remove(indexOfKeyToRemove);
          current.parentPointer.pointerList.remove(indexOfKeyToRemove + 1);
          if (current.rightLeaf.rightLeaf != null) {
            current.rightLeaf.rightLeaf = current;
            current.rightLeaf = current.rightLeaf.rightLeaf;
          } else {
            current.rightLeaf = null;
          }

          // the visual tree
          vcurrent.setValue(current.toString());
          if (vcurrent.getSibling() != null) {
            vcurrent.getSibling().deactivate();
          }
          vcurrent.setHexColor("#f1f701");
          BPlusTree.snap("delete", 0, key, 30, PseudoCodeDisplay.YELLOW);
          vcurrent.setHexColor("#eeeeff");

        }

      }
    } else //key is not in the leaf
    {
      BPlusTree.snap("delete", 0, key, 41, PseudoCodeDisplay.YELLOW);
      return false;
    }

    BPlusTree.snap("delete", 0, key, 42, PseudoCodeDisplay.YELLOW);
    return true; //successfully deleted
  }
}
