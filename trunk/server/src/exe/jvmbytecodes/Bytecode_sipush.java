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
* <p><code>Bytecode_sipush</code> provides a representation of a "sipush" bytecode in the JVM.
* Use the <code>Bytecode_sipush</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_sipush</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate pushing a signed integer on the operand stack.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//sipush implemented
public class Bytecode_sipush extends Bytecode_ {

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_sipush object
	 */
	Bytecode_sipush(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //get current frame
		next = lineNumber + 3; //update next line number

		int temp = Integer.parseInt(arguments.get(0));
		text = "The bytecode sipush " + temp + " pushed " + temp + " on the operand stack.";
		pushInteger(temp); //push signed int
		f.returnAddress = next; //update return address
		return next; //return next line number
	}
}
