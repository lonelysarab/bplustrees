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
* <p><code>Bytecode_lcmp</code> provides a representation of a "lcmp" bytecode in the JVM.
* Use the <code>Bytecode_lcmp</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_lcmp</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate comparing two longs.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//lcmp implemented
class Bytecode_lcmp extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_lcmp object
	 */
	Bytecode_lcmp(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		next = lineNumber + 1; //update the next line number
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame

		if(opcode.contains("l"))
		{
			Long x = popLong(); //long to compare
			Long y = popLong(); //long to compare
			int z;
			
			z = y.compareTo(x); //comparison

			pushInteger(z);
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}

		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
