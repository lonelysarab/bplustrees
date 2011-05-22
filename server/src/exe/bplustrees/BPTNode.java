/**************************************************************************************************
 * BPTNode.java
 * A node the exists within a B+ Tree.
 *
 *
 * @author William Clements
 * @version %I%, %G%
 *************************************************************************************************/
package exe.bplustrees;

import java.io.*;
import java.util.*;
import exe.pseudocode.*;

/*
 * An Object representation of a tree node in memory.
 */
public class BPTNode {
      /*
       * The list of keys in a node.
       */
      ArrayList<Integer> keyList = new ArrayList<Integer>();

      /*
       * The pointers to nodes or leafs below this node.
       */
      ArrayList<BPTNode> pointerList = new ArrayList<BPTNode>();

      /*
       * The parent.
       */
      BPTNode parentPointer;

      /*
       * The leaf to the left of this leaf. Null if this is a node.
       */
      BPTNode leftLeaf;

      /*
       * The leaf to the right of this leaf. Null if this is a node.
       */
      BPTNode rightLeaf;

      /* 
       * Node creation.
       *
       * @param obj   The object to be put in the node
       */
      public BPTNode(Integer obj) throws IOException { 
        parentPointer = null;
        keyList.add(obj);

        pointerList.add(null);
        pointerList.add(null);

        leftLeaf = null;
        rightLeaf = null;
      }

      /*
       * The keys in the node or leaf separated by spaces.
       *
       * @return  All the keys in the node.
       */
      public String toString() {
        String returnString = "";
        for (int i=0; i<keyList.size(); i++)
          returnString += keyList.get(i)+" ";
        return returnString; 
      }

      /*
       * The parent.
       *
       * @return  The node above this node or leaf.
       */
      public BPTNode getParent() {
        return parentPointer;
      }

      /*
       * The leaf to the left of this leaf.
       *
       * @return  The sibling or left leaf. Null if this node is a node and not a leaf.
       */
      public BPTNode getSibling() {
        return rightLeaf;
      }

      /*
       * Adding a key and pointer to this node.
       *
       * @param obj               The key.
       * @param rightDownPointer  The pointer that points to the node below and to the right. The
       *                          node below represents a node that contains keys that are greater
       *                          than or equal to the obj that is being inserted into this node.
       * @return                  True if it happened.
       */
      public boolean addToNode(int obj, BPTNode rightDownPointer) throws IOException {

        int temp;
        BPTNode tempNode;

        for(int i = 0; i < keyList.size(); i++)
        {
          if (obj < keyList.get(i) ) {
            temp = keyList.get(i);
            tempNode = pointerList.get(i+1);

            keyList.set(i,obj);
            pointerList.set(i+1,rightDownPointer);
            
            obj = temp;
            rightDownPointer = tempNode;
          }
        }

        keyList.add(obj);
        pointerList.add(rightDownPointer);

        return true;
      }

}
