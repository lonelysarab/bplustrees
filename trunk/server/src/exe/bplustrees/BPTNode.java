/**************************************************************************************************
 * BPTNode.java
 * A node the exists within a B+ Tree.
 *
 *
 * @author William Clements
 * @version March 16 2011
 *************************************************************************************************/

package exe.bplustrees;
import java.io.*;

import java.util.*;
import exe.pseudocode.*;

public class BPTNode {
      ArrayList<Integer> keyList = new ArrayList<Integer>(); 
      ArrayList<BPTNode> pointerList = new ArrayList<BPTNode>(); 
      BPTNode parentPointer;
      
      BPTNode leftLeaf;
      BPTNode rightLeaf;

      /* node creation
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

      public String toString() {
        String returnString = "";
        for (int i=0; i<keyList.size(); i++)
          returnString += keyList.get(i)+" ";
        return returnString; 
      }

      public BPTNode getParent() {
        return parentPointer;
      }

      public BPTNode getSibling() {
        return rightLeaf;
      }
      public boolean addToNode(int obj, BPTNode rightDownPointer) throws IOException {

        //add the key; add the pointer
        
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
