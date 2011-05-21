/**************************************************************************************************
 * BPlusTree.java
 * B+ Tree Visualization main driver program.
 *
 *
 * @author William Clements
 * @version March 16 2011
 *************************************************************************************************/
package exe.bplustrees;

import java.io.*;
import java.lang.*;
import java.util.*;

import java.net.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;

public class BPlusTree {

  //for JHAVE
  static final String TITLE = null;
  static final String INSERT = "exe/bplustrees/insert.xml";
  static final String SPLIT = "exe/bplustrees/split.xml";
  static final String DELETE = "exe/bplustrees/delete.xml";
  static PseudoCodeDisplay pseudo;
  static ShowFile show;
  static int splitScope;
  static boolean isInsert;
  //the trees
  static GAIGStree visualTree;
  static BPT tree;
  //the roots
  static TreeNode visualRoot;
  static BPTNode root;

  public static void main(String args[]) throws IOException {
    // show file creation for the client
    show = new ShowFile(args[0]);

    // Set restrictions on how wide the tree will be
    if (0 < Integer.parseInt(args[1]) && Integer.parseInt(args[1]) < 5) {
      BPT.order = Integer.parseInt(args[1]);
    } else {
      BPT.order = 4;
    }

    // declare a new tree in JHAVE and make a root
    visualTree = new GAIGStree(false, "B+ Tree of order " + BPT.order, "#000000",
            0.1, 0.1, 0.9, 0.9, 0.09);
    visualRoot = new TreeNode();
    visualTree.setRoot(visualRoot);

    // initialize tree and add random numbers
    // this tests the tree
    Integer[] tempIntArray = {18,10,17,13,9,13,12,2};
    tree = new BPT(tempIntArray);
//    tree.delete(5);
//    Integer[] tempIntArray = {1,2,3,4,5,6,7,8,9};

    /*****************************************************tree node examples below
    //assign a value to the root
    visualRoot.setValue("3");
    visualRoot.setHexColor("#eeeeff");
    show.writeSnap(TITLE, doc_uri(""), make_uri("",0,1,1,1), visualTree);

    //does not work... and should not
    //TreeNode secondRoot = new TreeNode();
    //secondRoot.setValue("4");
    //visualRoot.getParent().setSiblingWithEdge(secondRoot);
    //show.writeSnap(TITLE, doc_uri(""), make_uri("",0,1,1,1), visualTree);

    //make siblings
    TreeNode tempNodeOne = new TreeNode();
    tempNodeOne.setValue("1");
    tempNodeOne.setHexColor("#eeeeff");
    visualRoot.setChildWithEdge(tempNodeOne);
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);

    TreeNode tempNodetwo = new TreeNode();
    tempNodetwo.setValue("2");
    tempNodetwo.setHexColor("#eeeeff");
    visualRoot.setChildWithEdge(tempNodetwo);
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);

    TreeNode tempNodeThree = new TreeNode();
    tempNodeThree.setValue("3");
    tempNodeThree.setHexColor("#eeeeff");
    visualRoot.setChildWithEdge(tempNodeThree);
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);

    TreeNode tempNodeFour = new TreeNode();
    tempNodeFour.setValue("4");
    tempNodeFour.setHexColor("#eeeeff");
    visualRoot.setChildWithEdge(tempNodeFour);
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);

    visualRoot.getChild().setHexColor("#f1f701");
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);
    visualRoot.getChild().getSibling().setHexColor("#f1f701");
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);


    visualRoot.setChild(visualRoot.getChild().getSibling());
    show.writeSnap(TITLE, doc_uri(4), make_uri(1, 1, 1, PseudoCodeDisplay.RED), visualTree);



     */
    show.close();
  }

  public static String make_uri(String ISorD, int splitDepth, int x, int line, int color)
          throws IOException {
    return make_uri(ISorD, splitDepth, x, new int[]{line}, new int[]{color});
  }

  /*
   * make a snapshot of the visualization
   * @param ISorD   indicates if the program is currently inserting, spliting or deleting
   */
  public static String make_uri(String ISorD, int splitDepth, int x, int[] lines, int[] colors)
          throws IOException {
    String stack ="";//creation of the call stack
    String xVal="";

    //setup the pseudo code display panel
    try {
      if (0==ISorD.compareTo("insert")) {
        pseudo = new PseudoCodeDisplay(INSERT);
        stack = "main()\n  insert(x)";
        isInsert = true;
        xVal = String.valueOf(x);
      }
      else if (0==ISorD.compareTo("split")){
        pseudo = new PseudoCodeDisplay(SPLIT);

        if (isInsert)
          stack = "main()\n  insert(";
        else
          stack = "main()\n  delete(";

        for (int i=0; i<splitDepth; i++)
          stack += "split(";
        for (int i=0; i<splitDepth; i++)
          stack += ")";
        stack +=")";

        xVal = "null";
      }
      else {
        pseudo = new PseudoCodeDisplay(DELETE);
        stack = "main()\n  delete(x)";
        isInsert = false;
        xVal = String.valueOf(x);
      }
    } catch (JDOMException e) {
      e.printStackTrace();
    }

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("current_function", stack);
    map.put("x", xVal); //number being inserted or deleted

    String uri = null;

    try {
      uri = pseudo.pseudo_uri(map, lines, colors);
    } catch (JDOMException e) {
      e.printStackTrace();
    }

    return uri;
  }

  /* Generate hypertext information for the user
   * @param currentlyInsertingSplitingOrDeleting   what the visualization is currently doing
   * @return nothing
   */
  public static String doc_uri(String currentlyInsertingSplitingOrDeleting) {
    String content = "<html>"
            + "<head><title>B+ Trees Hypertextbook</title></head>"
            + "<body>"
            + "<h1>Hypertextbook</h1>";
    
    if (0==currentlyInsertingSplitingOrDeleting.compareTo("insert"))
      content += "<h2>Inserting a key into a B+ Tree</h2>\n"
              + "Inserting a key into a B+ Tree starts at the top node. Searching through the tree is necessary to find a proper location for the key. The very first key in the top node is compared with the key that is to be inserted. If the key to be inserted is less than key in the node the key travels down and to the left of the key in the node. There should be a pointer to a node below and to the left. If there is no node, the key is simply placed to the left of the key. Since there are no nodes below the root, the root itself is currently a leaf. However, if there are nodes below, this process is continued until a leaf is reached. Only once the leaf is reached, the key can be inserted.  \n\n"
              + "Along with this key is a pointer to a location on disk. This pointer to a location on disk is not shown in the visualization. The whole B+ tree is stored in memory to efficiently fetch pointers to locations on disk. The speed of finding something on hard drive makes theses trees useful. \n";
    else if (0==currentlyInsertingSplitingOrDeleting.compareTo("split"))
      content += "<h2>Spliting a node or a leaf in a B+ Tree</h2>\n"
              + "Keys are inserted into a B+ Tree and afterward a split is required if there are too many keys in the node. If the number of keys in the node is equal to the order of the tree then a split must occur. The order of the tree is determined by the programmer. For instructional purposes, an order of 4 is often used. Splitting a leaf generates a new leaf. All of the keys are distributed evenly between the two. The keys are still kept in numerical order. For example, if one leaf contains the keys 1 and 2 and the other leaf contains the keys 3 and 4.  The final step is to pass a key up the the parent node. The smallest key will be passed up in the newly created node. In our example, that would be 3. The parent now has the key 3 but does not have a pointer to a location on disk. Only a leaf has keys with pointers to locations on disk.\n\n "
              + "<h2>More Information</h2>\n"
              + "The splitting of a leaf generates a new leaf. This new leaf passes a key up to the parent node directly above it. If this parent node receives a key and places it within it's key list, the number of keys within the list may equal the order of the tree. The node must be split and a new node must be made along side it. Just like splitting a leaf, this new node will generate another key that must be passed up to the parent node. Splitting may occur recursively because the parent may need to be split if the number of keys in the list equals the order. \n";
    else
      content += "<h2>Deleting a key in a B+ Tree</h2>\n"
              + "Deleting a key from a B+ Tree requires a search starting at the top node. The very first key in the top node is compared with the key that needs to be deleted. If the key to be deleted is less than the key in the node the search continues on down and to the left of the key in the node. There should be a pointer to a node below and to the left. If there is no node, the key is simply deleted from the node. Since there are no nodes below the root, the root itself is currently a leaf. However, if there are nodes below, this process is continued until a leaf is reached. Only once the leaf is reached can the key be deleted from the leaf. The pointer to location on disk is deleted with the key. Only a leaf has these pointers to locations on disk. \n\n"
              + "<h2>More Information</h2>\n"
              + "The deletion of a key from a leaf sometimes constitutes a redistribution of keys so that a leaf does not become sparse in keys. If the number of keys in a key list in a leaf equals the minimum capacity number, keys must be taken from a nearby leaf. The minimum capacity number is determined by the programmer before the B+ Tree is started and is a number much lower than the order. If there is a sibling near by, for example to the right of the leaf, keys can be taken from this leaf and distributed evenly among the two. However, if keys are taken out of this sibling leaf, the sibling leaf may hit the minimum capacity number. This is not desired. The two must be combined and the key for the removed leaf must be removed from the parent node. \n";

    content += "</body></html>";

    URI uri = null;

    try {
      uri = new URI("str", content, "");
    } catch (java.net.URISyntaxException e) {
    }

    return uri.toASCIIString();
  }
}
