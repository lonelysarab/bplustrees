/**************************************************************************************************
 * BPT.java
 * Representation of a B+ Tree.
 *
 *
 * @author William Clements
 * @version May 29, 2011
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

      currentNode = findLeafContainingKey(currentNode,key,"insert");

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
          BPlusTree.tf.setAnswer(true);

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
    BPlusTree.snap("split", BPlusTree.splitScope, 0, 1, PseudoCodeDisplay.YELLOW);

    //Find the medianIndex
    String[] keyList = currentNode.getValue().split(" ");
    int medianIndex = (int) Math.floor(((double) keyList.length) / 2.0);

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

    currentNode = findLeafContainingKey(currentNode,key,"delete");
    
    //"current" is at a leaf node. If the key IS in the leaf, delete it.
    if (isKeyInList(currentNode.getValue().split(" "), key)) {
      //Key is removed.
      int index = findIndex(currentNode,key);
      removeKeyAtIndex(currentNode,index);

      //Look at how many keys are in the leaf.
      int numOfKeysInTheLeaf;
      if (currentNode.getValue().compareTo("")==0)
        numOfKeysInTheLeaf=0;
      else
        numOfKeysInTheLeaf = currentNode.getValue().split(" ").length;

      //Keys in leaves must be evenly distributed or merged into one leaf.
      if (numOfKeysInTheLeaf <= MINIMUM_CAPACITY)
      {

        if (currentNode.getParent() == null && numOfKeysInTheLeaf ==0) {
          currentNode = null;
        } else
        {

          //Look for a suitable neighbor
          TreeNode suitableNeighbor = searchForNeighbor(currentNode);
          int numKeysInNeighbor;
          if (suitableNeighbor != null)
            numKeysInNeighbor = suitableNeighbor.getValue().split(" ").length;
          else
            numKeysInNeighbor=0; //The root is a leaf.

          //REDISTRIBUTE
          if (numKeysInNeighbor > MINIMUM_CAPACITY) {

            String[] suitableNeighborKeys = suitableNeighbor.getValue().split(" ");
            String[] currentNodeKeys = currentNode.getValue().split(" ");
            //Neighbor was on the left
            if ( Integer.parseInt(suitableNeighborKeys[0]) < Integer.parseInt(currentNodeKeys[0]) )
            {
              String combinedKeys = ""+suitableNeighbor.getValue()+currentNode.getValue();
              String[] listOfCombinedKeys = combinedKeys.split(" ");
              int medianIndex = (int) Math.floor(((double) combinedKeys.split(" ").length) / 2.0);
              suitableNeighbor.setValue("");
              currentNode.setValue("");
              for (int i=0; i< listOfCombinedKeys.length; i++) {
                if (i<medianIndex)
                  addKeyToNode(suitableNeighbor,Integer.parseInt(listOfCombinedKeys[i]));
                else
                  addKeyToNode(currentNode,Integer.parseInt(listOfCombinedKeys[i]));
              }

            } else //Neighbor was on the right
            {
              String combinedKeys = ""+currentNode.getValue()+suitableNeighbor.getValue();
              String[] listOfCombinedKeys = combinedKeys.split(" ");
              int medianIndex = (int) Math.floor(((double) combinedKeys.split(" ").length) / 2.0);
              suitableNeighbor.setValue("");
              currentNode.setValue("");
              for (int i=0; i< listOfCombinedKeys.length; i++) {
                if (i<medianIndex)
                  addKeyToNode(currentNode,Integer.parseInt(listOfCombinedKeys[i]));
                else
                  addKeyToNode(suitableNeighbor,Integer.parseInt(listOfCombinedKeys[i]));
              }

            }
            fixKeys(currentNode.getParent());

          } else //DELETE LEAF
          {
            if(currentNode.getParent().getValue().split(" ").length < MINIMUM_CAPACITY)
            {
              //Deleting the leaf in this case will make only one key in the parent.
              //That is not desired so the tree needs to be restructured.
              //But not before the keys in the leaf are moved out.
              String tempKeys = currentNode.getParent().getChild().getValue();
              if (currentNode.getParent().getChild().getSibling() != null)
                tempKeys += currentNode.getParent().getChild().getSibling().getValue();
              if (currentNode.getValue().compareTo(currentNode.getParent().getValue())==0)
                currentNode.getParent().setValue("");
              currentNode.setValue(tempKeys);
              restructure(currentNode.getParent(),currentNode);
              currentNode.deactivate();
            }
            else //After deleting the leaf here, there will be at least one key in the parent.
              deleteLeaf(currentNode);

          }
        }
      }

      BPlusTree.snap("delete", 0, key, 14, PseudoCodeDisplay.YELLOW);
      
    } else //key is not in the leaf
    {
      BPlusTree.snap("delete", 0, key, 41, PseudoCodeDisplay.YELLOW);
      return false;
    }

    BPlusTree.snap("delete", 0, key, 42, PseudoCodeDisplay.YELLOW);
    return true; //successfully deleted
  }

  /*
   * Delete the leaf from it's parent.
   * @param TreeNode theNode    The node to be deleted from the parent.
   */
  public void deleteLeaf(TreeNode theNode) {

    if (theNode.getSibling() != null){
      String newKeyList = ""+theNode.getValue()+theNode.getSibling().getValue();
      theNode.setValue(newKeyList);
      theNode.setSibling(theNode.getSibling().getSibling());
    }
    else {
      TreeNode firstChild = theNode.getParent().getChild();
      TreeNode previousChild = null;
      while (firstChild.getValue().compareTo(theNode.getValue())!=0) {
        previousChild = firstChild;
        firstChild = firstChild.getSibling();
      }
      String newKeyList = ""+previousChild.getValue()+theNode.getValue();
      previousChild.setValue(newKeyList);
      previousChild.setSibling(theNode.getSibling());
    }
    fixKeys(theNode.getParent());
    theNode.deactivate();
    return;
  }

  /* Restructure the tree. Usually occurs after a leaf is deleated leaving the parent of that leaf
   * with only one child. The keys in the parent of that leaf get tossed up one level as well as
   * all the other nodes on the level above the leaves. Spliting occurs and new nodes are made.
   * What happens when the parent of the leaf is the root? The one child leaf become the root.
   * @param TreeNode parentOfALeaf    The parent of a leaf. 
   */
  public void restructure(TreeNode parentOfALeaf, TreeNode loneNode) {
    TreeNode grandparent = parentOfALeaf.getParent();
    TreeNode[] parents = new TreeNode[grandparent.getValue().split(" ").length+1];
    TreeNode parent = grandparent.getChild();
    for (int i=0; i<grandparent.getValue().split(" ").length+1; i++) {
      parents[i] = parent;
      parent = parent.getSibling();
    }
    grandparent.setChild(null);
    grandparent.setValue("");
    TreeNode child = parents[0].getChild();
    for (int i=0; i<parents.length; i++) {
      for (int j=0; j<parent.getValue().split(" ").length+1; j++) {
        grandparent.setChild(child);
        child = child.getSibling();
      }
    }
    fixKeys(grandparent);
    return;
  }

  /*
   * Takes a parent node of leaves as a parameter and fixes the keyList so that it reflects what
   * is in the leaves below it.
   * @param TreeNode theNode      The keys in this node need to be replaced with meaningful keys.
   */
  public void fixKeys(TreeNode theNode) {
    theNode.setValue("");
    TreeNode child = (theNode.getChild()).getSibling();

    String newKeyList="";
    while (child!=null)
    {
      newKeyList += child.getValue().split(" ")[0]+" ";
      child = child.getSibling();
    }
    theNode.setValue(newKeyList);
  }

  /*
   * Look at the sibling to the left and the sibling to the right and return the leaf that has
   * more keys in the keylist.
   * @param TreeNode currentLeaf    The leaf that needs to have siblings examined.
   * @return TreeNode               One of the two siblings (on the left or the right) that has
   *                                the most keys it it's key list. If they both have the same
   *                                amount, the one on the left is chosen. null is returned when
   *                                there are no neighbors.
   */
  public TreeNode searchForNeighbor(TreeNode currentLeaf) {
    //the leaf is the parent in this case
    if (currentLeaf.getParent() == null) {
      return null;
    } else {
      TreeNode child = currentLeaf.getParent().getChild();
      int numOfSiblings = currentLeaf.getParent().getValue().split(" ").length+1;
      TreeNode[] leaves = new TreeNode[numOfSiblings];
      int indexOfCurrentLeaf=0;
      for (int i=0; i<numOfSiblings; i++) {
        leaves[i] = child;
        if (leaves[i].getValue().compareTo(currentLeaf.getValue())==0)
          indexOfCurrentLeaf=i;
        child = child.getSibling();
      }

      TreeNode leftLeaf = null;
      if (indexOfCurrentLeaf > 0)
        leftLeaf = leaves[indexOfCurrentLeaf-1];

      TreeNode rightLeaf = null;
      if (indexOfCurrentLeaf < numOfSiblings-1)
        rightLeaf = leaves[indexOfCurrentLeaf+1];

      if (leftLeaf == null && rightLeaf !=null)
        return rightLeaf;
      else if(rightLeaf == null && leftLeaf != null)
        return leftLeaf;
      else if (leftLeaf==null && rightLeaf==null)
        return null;

      int numOfRightLeafKeys = rightLeaf.getValue().split(" ").length;
      int numOfLeftLeafKeys = leftLeaf.getValue().split(" ").length;

      if (numOfRightLeafKeys < numOfLeftLeafKeys)
        return leftLeaf;
      else if(numOfRightLeafKeys == numOfLeftLeafKeys)
        return leftLeaf;
      else
        return rightLeaf;

    }

  }

  /*
   * Traverse the tree looking for the key. Stop when you get to the leaf.
   * @param TreeNode currentNode    Start at this root node and search down the tree.
   * @param int key                 The key you are looking for. It will reside in a leaf.
   * @param String state            The current state of the program.
   * @return nothing
   */
  public TreeNode findLeafContainingKey (TreeNode currentNode, int key, String state) throws IOException {
    //Traverse down the tree.
      while (currentNode.getChild() != null) {
        //Grab all the keys in the node
        String[] keyList = currentNode.getValue().split(" ");
        //Look through the keys in the root node.
        int index = 0;
        while (Integer.parseInt(keyList[index]) < key && index < keyList.length - 1) {
          index++;
        }

        //Update the visualization
        currentNode.setHexColor("#f1f701");
        BPlusTree.snap(state, 0, key, 6, PseudoCodeDisplay.YELLOW);
        currentNode.setHexColor("#eeeeff");

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
