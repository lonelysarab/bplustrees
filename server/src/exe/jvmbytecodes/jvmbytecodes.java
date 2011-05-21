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

// file: jvmbytecodes.java
// This is the front-end program for the Sort.java program in this 
// directory. It takes the command-line parameters from the server, sends the
// input-generator file to the XMLParameterParser to get the user's inputs, and
// then processes these inputs so they can be properly fed into 
// Sort.java.
package exe.jvmbytecodes;
import exe.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.jdom.JDOMException;
/*
 * Entry point for the server program.
 */
public class jvmbytecodes {
	/*
	 * Calls the Driver program.
	 */
    public static void main(String args[]) throws IOException,JDOMException,InterruptedException {

	Hashtable hash = XMLParameterParser.parseToHash( args[2] );

	String s1 = (String)hash.get("You may load one of your Java source files (and if so, make sure to replace 'EMPTY' with its name), or pick a built-in program below.");
				    
	String s2 = (String)hash.get("You may pick a built-in sample program from the drop-down list on the right. Then, click OK.");

	String[] params = new String[3];
	params[0] = args[0];

	if (s2.startsWith("Builtin_Program_")) { 
	    // the user picked a builtin example

	    params[1] = s2.substring(16);  // example name
	    params[2] = "contents are not in here";
	}
	else { // the user loaded a file of their own

	    // the first line of s is the name of the file
	    params[1] = s1.substring(0,s1.indexOf('\n') );
	    // the rest of s is the contents of the file
	    params[2] = s1.substring(s1.indexOf('\n') + 1);
	}

	/*
	System.out.println("jvmbytecodes.java args[0]: " + args[0] + " args[1]: " + args[1]);

	Hashtable hash = XMLParameterParser.parseToHash( args[2]);

	String s = (String)hash.get("Load your program and be sure that EMPTY is replaced by the name of the Java source file:");

	String[] params = new String[3];
	params[0] = args[0];
	// the first line of s is the name of the file
	params[1] = s.substring(0,s.indexOf('\n') );
	// the rest of s is the contents of the file
	params[2] = s.substring(s.indexOf('\n') + 1);
	*/

	Driver.main(params);


    }
}
