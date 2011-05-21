/* 
   This file is part of JHAVE -- Java Hosted Algorithm Visualization
   Environment, developed by Tom Naps, David Furcy (both of the
   University of Wisconsin - Oshkosh), Myles McNally (Alma College), and
   numerous other contributors who are listed at the http://jhave.org
   site

   JHAVE is free software: you can redistribute it and/or modify it under
   the terms of the GNU General Public License as published by the Free
   Software Foundation, either version 3 of the License, or (at your
   option) any later version.

   JHAVE is distributed in the hope that it will be useful, but WITHOUT
   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
   for more details.

   You should have received a copy of the GNU General Public License
   along with the JHAVE. If not, see:
   <http://www.gnu.org/licenses/>.
*/


package exe.reu_bubsort;

import java.io.*;
import java.util.*;
import java.net.*;

import org.jdom.*;

import exe.*;
import exe.pseudocode.*;

public class Sort {

    static final String TITLE = null;   // no title
    static final String FILE = "exe/reu_bubsort/template.xml";
    static int arraySize;       // # of items to sort
    static GAIGSarray items;    // the array of items
    static PseudoCodeDisplay pseudo;	// The pseudocode
    public static void main(String args[]) throws IOException {
   
        // process program parameters and create the show file object
        ShowFile show = new ShowFile(args[0]);
        arraySize = Integer.parseInt(args[1]);

        // define the two structures in the show snapshots
        items = new GAIGSarray(arraySize, true, "BubbleSort", 
                               "#999999", 0.1, 0.1, 0.9, 0.9, 0.07);

        try {
	    pseudo = new PseudoCodeDisplay(FILE);
	} catch (JDOMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

        // initialize the array to be sorted & show it
        loadArray();
        show.writeSnap(TITLE, doc_uri(arraySize), make_uri(-1, -1, 0, PseudoCodeDisplay.RED), items);
        
        for (int pass = 1; pass < arraySize; pass++) {
            for (int i = 0; i < arraySize-pass; i++) {
            	show.writeSnap(TITLE, doc_uri(arraySize), make_uri(pass, i, 3, (pass - 1) % 7), items);
                if ((Integer)(items.get(i)) > (Integer)(items.get(i+1))) {
                    swap(i, i+1);
                    show.writeSnap(TITLE, doc_uri(arraySize), make_uri(pass, i, 4, (pass - 1) % 7), items);
                }
            }
        }

        show.writeSnap(TITLE, doc_uri(arraySize), make_uri(-1, -1, 0, PseudoCodeDisplay.RED), items);

        // visualization is done
        show.close();                    
    }

    private static String make_uri(int pass, int i, int line, int color) {
    	return make_uri(pass, i, new int[]{line}, new int[]{color});
    }

    private static String make_uri(int pass, int i, int[] lines, int[] colors) {
    	String passVal = pass == -1 ? "null" : String.valueOf(pass);
    	String iVal = i == -1 ? "null" : String.valueOf(i);
    	String stack = "main program\n" + 
	               "  bubbleSort(items)";

    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put("pass", passVal);
    	map.put("i", iVal);
    	map.put("s", stack);

    	String uri = null;
    	try {
	    uri = pseudo.pseudo_uri(map, lines, colors);
	} catch (JDOMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return uri;
    }

    // Load the array with values from 1 to the array size, then
    // shuffle these values so that they appear in random order.
    private static void loadArray () {
        Random rand = new Random();
        for (int i = 0; i < arraySize; i++)
            items.set(i+1,i);
        for (int i = 0; i < arraySize-1; i++)
            swap(i, i + (Math.abs(rand.nextInt()) 
                         % (arraySize - i)) );
    }
    

    // Swap two items in the array.
    private static void swap (int loc1, int loc2) {
        Object temp = items.get(loc1);
        items.set(items.get(loc2), loc1);
        items.set(temp, loc2);    
    }

    // Return an appropriate inline string for the info page 
    private static String doc_uri(int num_items) {
	//        String content = "<html><head><title>Bubble Sort</title></head><body><h1>Bubble Sort</h1>The famous bubble sort on a " + num_items + "-item array</body></html>";
        String content = "<html><head><title>Bubble Sort</title></head><body><h1>Bubble Sort</h1>The famous bubble sort on a " + num_items + "-item array</body></html>";
        URI uri = null;
        try {
            uri = new URI("str", content, "");
        }
        catch (java.net.URISyntaxException e) {
        }
        return uri.toASCIIString();
    }
}
