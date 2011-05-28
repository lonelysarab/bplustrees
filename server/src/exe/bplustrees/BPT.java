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
 * Includes functions to manage a B+ Tree.
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
    root.setHexColor("#eeeeff");
  }

  /*
   * Constructor can insert multiple items into the tree
   * @param numberArray   numbers to be inserted into the tree
   */
  public BPT(int[] numberArray) throws IOException {
    root = new TreeNode();
    root.setHexColor("#eeeeff");
    for (int i = 0; i < numberArray.length; i++) {
      insert(numberArray[i]);
    }
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

      currentNode = findLeafContainingKey(currentNode,key);

      //Update the visualization
      currentNode.setHexColor("#f1f701");
      BPlusTree.snap("insert", 0, key, 5, PseudoCodeDisplay.YELLOW);
      currentNode.setHexColor("#eeeeff");

      //"current" is at a leaf node. If the key is NOT in the leaf, insert it.
      if (isKeyAbsentInList(currentNode.getValue().split(" "), key)) {

        //update visual tree
        currentNode.setHexColor("#f1f701");//highlight it
        BPlusTree.snap("insert", 0, key, 15, PseudoCodeDisplay.YELLOW);

        //properly add it into the leaf
        addKeyToNode(currentNode, key);

        //update visual tree
        BPlusTree.id++;
        BPlusTree.tf.setQuestionText("Will a split be performed?");
        BPlusTree.snap("insert", 0, key, 16, PseudoCodeDisplay.YELLOW, BPlusTree.tf);
        currentNode.setHexColor("#eeeeff");//unhighlight it

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

    //Successfully inserted.
    BPlusTree.snap("insert", 0, key, 23, PseudoCodeDisplay.YELLOW);
    return true;
  }

  /*
   * Add a key into string containing numbers. They string will be in ascending order.
   * @param theNode       The Node you want a key added to.
   * @param int key       The integer you want to put in the list.
   * @return              nothing
   */
  public void addKeyToNode(TreeNode theNode, int key) {
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
  public boolean isKeyAbsentInList(String[] strArray, int key) {
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
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 3, PseudoCodeDisplay.YELLOW);

      //Make a new leaf node.
      TreeNode newLeafNode = new TreeNode("");
      newLeafNode.setHexColor("#eeeeff");

      //Put all the keys >= the median into the newLeafNode
      for (int i = medianIndex; i < keyList.length; i++) {
        addKeyToNode(newLeafNode, removeKeyAtIndex(currentNode, medianIndex));
      }

      //Update visualization.
      currentNode.setHexColor("#f1f701");
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 5, PseudoCodeDisplay.YELLOW, newLeafNode);
      currentNode.setHexColor("#eeeeff");

      //If the root was split, a new root is made.
      if (currentNode.getParent() == null) {
        BPlusTree.snap("split", BPlusTree.splitScope, 0, 16, PseudoCodeDisplay.YELLOW, newLeafNode);

        //Make a new parent.
        TreeNode newParentNode = new TreeNode();
        newParentNode.setHexColor("#eeeeff");
        root = newParentNode;
        BPlusTree.visualTree.setRoot(root);

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

        newLeafNode.setSibling(currentNode.getSibling());
        newLeafNode.setParent(currentNode.getParent());
        newLeafNode.setLineToParent(new Edge(currentNode.getParent(), newLeafNode));
        currentNode.setSibling(newLeafNode);

        //Pass the first key in the new node up to the parent.
        addKeyToNode(currentNode.getParent(), Integer.parseInt(newLeafNode.getValue().split(" ")[0]));

        //If there are too many keys in the parent, it must be split.
        String[] parentKeyList = currentNode.getParent().getValue().split(" ");
        if (parentKeyList.length == ORDER) {
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 27, PseudoCodeDisplay.YELLOW);
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 28, PseudoCodeDisplay.YELLOW);
          split(currentNode.getParent());
        }
      }

    } else //Split a node.
    {
      BPlusTree.snap("split", BPlusTree.splitScope, 0, 30, PseudoCodeDisplay.YELLOW); /////////////////////////////////////WORKING ON THIS

      //Make a newNode.
      TreeNode newNode = new TreeNode();
      newNode.setHexColor("#eeeeff");
      newNode.setValue("");

      //Put all the keys >= the median into the newNode. Move the pointers as well.
      for (int i = medianIndex; i < keyList.length; i++) {
        addKeyToNode(newNode, removeKeyAtIndex(currentNode, medianIndex));

        TreeNode previousNode;
        TreeNode tempNodePointer = currentNode.getChild();
        tempNodePointer = tempNodePointer.getSibling();
        previousNode = tempNodePointer = tempNodePointer.getSibling();
        tempNodePointer = tempNodePointer.getSibling();

        previousNode.setSibling(tempNodePointer.getSibling());
        tempNodePointer.setSibling(null);
        tempNodePointer.deactivate();

        newNode.setChildWithEdge(tempNodePointer);
      }

      //Pass the first key up to the parent
      int tempKeyForTheParent = removeKeyAtIndex(newNode, 0);

      //If the root was split, a new root is made.
      if (currentNode.getParent() == null) {

        //Make a new parent.
        TreeNode newParentNode = new TreeNode();
        newParentNode.setHexColor("#eeeeff");
        root = newParentNode;
        BPlusTree.visualTree.setRoot(root);
        newParentNode.setValue(String.valueOf(tempKeyForTheParent) + " ");

        //Set the pointer going down and to the left.
        newParentNode.setChildWithEdge(currentNode);
        //Set the pointer going down and to the right.
        newParentNode.setChildWithEdge(newNode);

        BPlusTree.snap("split", BPlusTree.splitScope, 0, 42, PseudoCodeDisplay.YELLOW);

      } else // A parent exist. Attach the newNode. Pass the first key in newNode up to the parent.
      {

        newNode.setSibling(currentNode.getSibling());
        newNode.setParent(currentNode.getParent());
        newNode.setLineToParent(new Edge(currentNode.getParent(), newNode));
        currentNode.setSibling(newNode);

        //Pass the first key in the new node up to the parent.
        addKeyToNode(currentNode.getParent(), tempKeyForTheParent);

        //If there are too many keys in the parent, it must be split.
        String[] parentKeyList = currentNode.getParent().getValue().split(" ");
        if (parentKeyList.length == ORDER) {
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 27, PseudoCodeDisplay.YELLOW);
          BPlusTree.snap("split", BPlusTree.splitScope, 0, 28, PseudoCodeDisplay.YELLOW);
          split(currentNode.getParent());
        }
      }
    }

    BPlusTree.splitScope--;
    return; //successfully inserted
  }

  /* Picks up a pointer in a node that points down to a child and places it in a different node.
   * @param TreeNode fromNode     This is the node that is having a pointer removed.
   * @param TreeNode targetNode   This is the node that is going to have the pointer placed in it.
   * @param int index             This is the index where the pointer resides in the fromNode. The
   *                              first pointer is at index 0.
   */
/*  public void movePointer(TreeNode fromNode, TreeNode targetNode, int index) {
    TreeNode tempNodePointer = fromNode.getChild();
    TreeNode previousNode = fromNode.getChild();

    for (int i = 0; i < index; i++) {
      tempNodePointer = tempNodePointer.getSibling();
    }
    for (int i = 0; i < index - 1; i++) {
      previousNode = previousNode.getSibling();
    }

    //Remove from previous node.
    tempNodePointer.setParent(null);
    previousNode.setSibling(tempNodePointer.getSibling());
    tempNodePointer.setSibling(null);

    //Add to targetNode.
    targetNode.setChildWithEdge(tempNodePointer);
    return;
  }
*/
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
      if (i != index) {
        returnStr += originalList[i] + " ";
      }
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

    //Set up pointer named current to assist in traveling down the tree
    TreeNode currentNode = root;

    //Update the visualization
    currentNode.setHexColor("#f1f701");
    BPlusTree.snap("insert", 0, key, 5, PseudoCodeDisplay.YELLOW);
    currentNode.setHexColor("#eeeeff");

    currentNode = findLeafContainingKey(currentNode,key);

    //Update the visualization
    currentNode.setHexColor("#f1f701");
    BPlusTree.snap("insert", 0, key, 6, PseudoCodeDisplay.YELLOW);
    currentNode.setHexColor("#eeeeff");
    
    //"current" is at a leaf node. If the key IS in the leaf, delete it.
    if (isKeyInList(currentNode.getValue().split(" "), key)) {
      int index = findIndex(currentNode,key);
      removeKeyAtIndex(currentNode,index);

      //look at how many keys are in the leaf.
      int numOfKeysInTheLeaf;
      if (currentNode.getValue().compareTo("")==0)
        numOfKeysInTheLeaf=0;
      else
        numOfKeysInTheLeaf = currentNode.getValue().split(" ").length;
/*
      //Keys in leaves must be evenly distributed or merged into one leaf.
      if (numOfKeysInTheLeaf <= MINIMUM_CAPACITY) {

        //Look at the number of keys in the leaf to the right.
        int numKeysInRightLeaf;
        if (currentNode.getSibling() != null) {
          numKeysInRightLeaf = currentNode.getSibling().getValue().split(" ").length;

          //Evenly distribute the keys between this leaf and the leaf to the right.
          if (numOfKeysInTheLeaf+numKeysInRightLeaf >= ORDER) {
            
          } else //MERGE THE LEAVES
          {
            
          }
        } else //delete this leaf.
        {

        }

      }
*/
      BPlusTree.snap("delete", 0, key, 14, PseudoCodeDisplay.YELLOW);
      
    } else //key is not in the leaf
    {
      BPlusTree.snap("delete", 0, key, 41, PseudoCodeDisplay.YELLOW);
      return false;
    }


/*
    if (current.keyList.get(index) == key) { //key is, in fact, in the leaf

      //remove key from the leaf
      current.keyList.remove(index);
      current.pointerList.remove(0); //all these pointers are blank

      vcurrent.setValue(current.toString());

      // the visual tree
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
*/
    BPlusTree.snap("delete", 0, key, 42, PseudoCodeDisplay.YELLOW);
    return true; //successfully deleted
  }

  /*
   * Traverse the tree looking for the key. Stop when you get to the leaf.
   * @param TreeNode currentNode    Start at this root node and search down the tree.
   * @param int key                 The key you are looking for. It will reside in a leaf. 
   * @return nothing
   */
  public TreeNode findLeafContainingKey (TreeNode currentNode, int key) {
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
      return currentNode;
  }

  /*
   * Find the index where the key resides
   * @param TreeNode theNode    The node to search through.
   * @param int key             The key that is being searched for
   * @return index              The index where the key resides. -1 if none was found
   */
  public int findIndex(TreeNode theNode, int key) {
    int returnIndex =-1;
    String[] tempStr = theNode.getValue().split(" ");

    int i=0;
    while(i<tempStr.length) {
      if (Integer.parseInt(tempStr[i]) == key) {
        returnIndex = i;
        i=tempStr.length;
      }
      i++;
    }

    return returnIndex;
  }

  /*
   * Determine if a Key is in a string. If it is, true is returned.
   * @param String[] lst   The list that will be searched
   * @param int key        The key in question. Is this key in the list?
   * @return               True if the key is in the list.
   */
  public boolean isKeyInList(String[] lst, int key) {
    return !isKeyAbsentInList(lst,key);
  }
}
