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
package exe.jvmbytecodes;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.net.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;

/*
* <p><code>Bytecode_ldc2_w</code> provides a representation of a "ldc2_w" bytecode in the JVM.
* Use the <code>Bytecode_ldc2_w</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_ldc2_w</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate loading a wide constant.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//ldc2_w implemented
public class Bytecode_ldc2_w extends Bytecode_ {

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_ldc2_w object
	 */
	Bytecode_ldc2_w(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
		next = lineNumber + 3; //update the line number

		if(objectType.contains("long")) //is it a long?
		{
			long temp = Long.parseLong(path);
			text = "The bytecode ldc2_w pushed the long " + temp + " on the operand stack.";
			pushLong(temp); //load the long
		}
		else if(objectType.contains("double")) //is it a double?
		{
			double temp = Double.parseDouble(path);
			text = "The bytecode ldc2_w pushed the double " + temp + " on the operand stack.";
			pushDouble(temp); //load the double
		}
		else
			System.out.println("Unrecognized bytecode");
	
		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
