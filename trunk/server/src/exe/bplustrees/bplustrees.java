
package exe.bplustrees;

import exe.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class bplustrees{
    public static void main(String args[]) throws IOException
    {
    	Hashtable hash = XMLParameterParser.parseToHash(args[2]);

    	String[] params = new String[2];

    	params[0] = args[0] + ".sho";

        //this string must be the exact same as the string in the file bplustrees.igs
    	params[1] = (String)hash.get("Please enter the order of the tree (2, 3, or 4)");

    	BPlusTree.main(params);
    }
}
