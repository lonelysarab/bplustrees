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
* <p><code>Bytecode_inc</code> provides a representation of an "inc" bytecode in the JVM.
* Use the <code>Bytecode_inc</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_inc</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate incrementing a number in the local variable array by an integer parameter.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//iinc implemented
public class Bytecode_inc extends Bytecode_ {

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_inc object
	 */
	Bytecode_inc(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame		next = lineNumber + 1; //increment line number
		next += 2; //update next line number
		
		if (opcode.contains("ii")) { //iince
			int index = Integer.parseInt(arguments.get(0));
			Integer x, y;
			x = Integer.parseInt(arguments.get(1)); //increment amount
			y = Integer.parseInt(f._localVariableArray[index]); //number to increment
			int z = x + y; //increment operator
			storeInteger(z);
		}

		f.returnAddress = next; //update return address
		return next; //return next line number
	}
}
