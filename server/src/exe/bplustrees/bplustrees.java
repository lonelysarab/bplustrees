/**************************************************************************************************
 * bplustrees.java
 * The first .class that is called when the visualization is made.
 *
 *
 * @author William Clements
 * @version May 21, 2010
 *************************************************************************************************/
package exe.bplustrees;

import exe.*;
import java.io.*;
import java.lang.*;
import java.util.*;

/*
 * Helper to start making the visualization. This file will help sorting out arguments if expanding
 * the project is desired in the future. 
 */
public class bplustrees{

    /*
     * Takes arguments from the user about the order of the tree.
     * @param args[0]   show file
     * @param args[1]   instructions for the user to enter the desired order of the tree. 
     */
    public static void main(String args[]) throws IOException
    {
        //This is meant for user input. Can be used in the future.
    	//Hashtable hash = XMLParameterParser.parseToHash(args[2]);

    	String[] params = new String[2];
    	params[0] = args[0] + ".sho";

        //this string must be the exact same as the string in file html_root-ingen-bplustrees.igs
    	//params[1] = (String)hash.get("Please enter the order of the tree (3 or 4)");

    	BPlusTree.main(params);
    }
}
