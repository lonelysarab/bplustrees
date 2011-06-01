/**************************************************************************************************
 * BPlusTree.java
 * B+ Tree Visualization main driver program.
 *
 *
 * @author William Clements
 * @version May 29, 2011
 *************************************************************************************************/
package exe.bplustrees;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;

/*
 * This class helps make the visualization script that is sent to the client.
 */
public class BPlusTree {

  /*
   * Title for the visualization
   */
  static final String TITLE = null;

  /*
   * Pseudocode for the algorith to insert keys into the B+ Tree
   */
  static final String INSERT = "exe/bplustrees/insert.xml";

  /*
   * Pseudocode for the algorithm to split a node or a leaf in a B+ tree after an insert.
   */
  static final String SPLIT = "exe/bplustrees/split.xml";

  /*
   * Pseudocode for the algorithm to delete a key from a leaf in a B+ tree.
   */
  static final String DELETE = "exe/bplustrees/delete.xml";

  /*
   * Pseudocode display helper
   */
  static PseudoCodeDisplay pseudo;

  /*
   * The show file created for the client that contains the snapshots for the visualization.
   */
  static ShowFile show;

  /*
   * Split can call itself recursively. This tracks how many times it has been called. If it is
   * called once and the algorithm is inside the function and integer representation is 1.
   */
  static int splitScope;

  /*
   * If the algorithm is currently inserting this is true.
   */
  static boolean isInsert;

  /*
   * The GAIGStree visual representation of a tree.
   */
  static GAIGStree visualTree;

  /*
   * A B+ tree stored in memory.
   */
  static BPT tree;

  /*
   * A JHAVE visual representation of a root node.
   */
  static TreeNode visualRoot;

  /*
   * True and false questions for the user.
   */
  static XMLtfQuestion tf;

  /*
   * Keeps track of how many questions have been asked. 
   */
  static int id;

  /*
   * Function that creates the script for the user.
   * @param args[0]   show file
   * @param args[1]   instructions for the user to enter the desired order of the tree.
   */
  public static void main(String args[]) throws IOException {

    show = new ShowFile(args[0]);
    tf = new XMLtfQuestion(show,id+"");

    //Declare a new general tree.
    visualTree = new GAIGStree(false, "B+ Tree of order " + BPT.ORDER, "#000000",
            0.3, 0.6, 0.7, 0.99, 0.08);

    //Insert a list of numbers into the tree
    /*
    int[] tempIntArray = {2,5,7,12,15,17,22,25,27,32,35,37,42,45,47,52,55,57,62,65,67,72,75,77,82,85,87,92,95,97};
    tree = new BPT(tempIntArray);
    */

    //Insert random numbers into the tree.
    Random rand = new Random();
    int[] tempIntArray = new int[40];

    //make a list of keys to add
    for (int i = 0; i < tempIntArray.length; i++)
      tempIntArray[i] = ((Math.abs(rand.nextInt())%99)+1);

    //make the tree
    tree = new BPT(tempIntArray);

    //delete keys
    for (int i = 0; i < 1; i++)
      tree.delete(tempIntArray[i]);

    /**********************************tree node examples below***********************************
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
    **********************************************************************************************/
    show.close();
  }

  /*
   * Helper for making a snapshot of the current state of the algorithm.
   * @param ISorD       a string containing "insert", "split" or "delete". signifies current state.
   * @param splitDepth  represents current depth that the spliting is at
   * @param x           the key that is being inserted or deleted
   * @param line        current line highlighted. represents current executing line in algorithm.
   * @param color       color highligher for the line. often "PseudoCodeDisplay.YELLOW"
   */
  public static String make_uri(String ISorD, int splitDepth, int x, int line, int color)
          throws IOException {
    return make_uri(ISorD, splitDepth, x, new int[]{line}, new int[]{color});
  }

  /*
   * Makes a snapshot of the visualization
   * @param ISorD       a string containing "insert", "split" or "delete". signifies current state.
   * @param splitDepth  represents current depth that the spliting is at
   * @param x           the key that is being inserted or deleted
   * @param lines       current lines highlighted. represents current executing lines in algorithm.
   * @param colors      color highlighers for the lines. often "PseudoCodeDisplay.YELLOW"
   */
  public static String make_uri(String ISorD, int splitDepth, int x, int[] lines, int[] colors)
          throws IOException {
    /*
     * Creation of the call stack.
     */
    String stack ="";

    /*
     * The value of the key to be inserted or deleted in the tree.
     */
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
            + "<head><title>B+ Trees --more info--</title></head>"
            + "<body>"
            + "<h1>Hypertextbook</h1>";
    
    if (0==currentlyInsertingSplitingOrDeleting.compareTo("insert"))
      content += "<h2>Currently Inserting a key into a B+ Tree</h2>\n"
              + "";
    else if (0==currentlyInsertingSplitingOrDeleting.compareTo("split"))
      content += "<h2>Currently Spliting in a B+ Tree</h2>\n"
              + "";
    else
      content += "<h2>Currently Deleting a key in a B+ Tree</h2>\n"
              + "";

    content += "</body></html>";

    URI uri = null;

    try {
      uri = new URI("str", content, "");
    } catch (java.net.URISyntaxException e) {
    }

    return uri.toASCIIString();
  }


  /*
   * Make a snapshot for the visualization.
   * @param state       a string containing "insert", "split" or "delete". signifies current state.
   * @param splitDepth  represents current depth that the spliting is at
   * @param x           the key that is being inserted or deleted
   * @param line        current line highlighted. represents current executing line in algorithm.
   * @param color       color highligher for the line. often "PseudoCodeDisplay.YELLOW"
   */
  public static void snap(String state, int splitDepth, int x, int line, int color) throws IOException {
    show.writeSnap(TITLE, doc_uri(state),
            make_uri(state, splitDepth, x, line, color), visualTree);
  }

  /*
   * Make a snapshot for the visualization.
   * @param state       a string containing "insert", "split" or "delete". signifies current state.
   * @param splitDepth  represents current depth that the spliting is at
   * @param x           the key that is being inserted or deleted
   * @param line        current line highlighted. represents current executing line in algorithm.
   * @param color       color highlighter
   * @param n           A new leaf or node that is made and it is about to be inserted into tree.
   */
  public static void snap(String state, int splitDepth, int x, int line, int color, TreeNode n) throws IOException {
    GAIGStree showNewNode = new GAIGStree(false, "New Node", "#000000", 0.0, 0.8, 0.2, 0.99, 0.2);
    showNewNode.setRoot(n);
    show.writeSnap(TITLE, doc_uri(state),
            make_uri(state, splitDepth, x, line, color), visualTree, showNewNode);
  }

  /*
   * Make a snapshot for the visualization.
   * @param state       a string containing "insert", "split" or "delete". signifies current state.
   * @param splitDepth  represents current depth that the spliting is at
   * @param x           the key that is being inserted or deleted
   * @param line        current line highlighted. represents current executing line in algorithm.
   * @param color       color highlighter
   * @param q           Question to quiz the user
   */
  public static void snap(String state, int splitDepth, int x, int line, int color, XMLtfQuestion q) throws IOException {
    show.writeSnap(TITLE, doc_uri(state), make_uri(state, splitDepth, x, line, color), q, visualTree);
  }

}
