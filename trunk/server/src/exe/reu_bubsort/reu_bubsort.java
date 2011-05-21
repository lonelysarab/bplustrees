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

// file: reu_bubsort.java

// This is the front-end program for the Sort.java program in this 
// directory. It takes the command-line parameters from the server, sends the
// input-generator file to the XMLParameterParser to get the user's inputs, and
// then processes these inputs so they can be properly fed into 
// Sort.java.

package exe.reu_bubsort;

import exe.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class reu_bubsort{
    public static void main(String args[]) throws IOException {
	// Send the XML file name from the server to the parser.
	Hashtable hash = XMLParameterParser.parseToHash(args[2]);

	String[] params = new String[2];

	params[0] = args[0] + ".sho";
	params[1] = 
	    (String)hash.get("Enter the size of the array to be sorted:");

	// In case you want to see params[1] for debugging, uncomment the following code to write it out to a file
// 	Writer out = new OutputStreamWriter(new FileOutputStream("/Users/naps/junk/foo.txt"), "UTF-8");
// 	try {
// 	    out.write(params[1]);
// 	}
// 	finally {
// 	    out.close();
// 	}

	Sort.main(params);
    }
}
