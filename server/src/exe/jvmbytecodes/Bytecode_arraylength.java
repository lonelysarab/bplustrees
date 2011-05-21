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
* <p><code>Bytecode_or</code> provides a representation of an "arraylength" bytecode in the JVM.
* Use the <code>Bytecode_arraylength</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_arraylength</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the bitwise or.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//arraylength implemented
class Bytecode_arraylength extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_arraylength object
	 */
	Bytecode_arraylength(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		next = lineNumber + 1; //update the next line number
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame

		//arraylength
		if(opcode.contains("arraylength"))
		{
			int length = 0;
			String refArray = popString();
			if(refArray.equals("Arr 1"))
			{
				if(Driver.arrayType.equals("int"))
				{
					length = Bytecode_newarray.int_array.length;
				}
				else if(Driver.arrayType.equals("float"))
				{
					length = Bytecode_newarray.float_array.length;
				}
				else if(Driver.arrayType.equals("long"))
				{
					length = (Bytecode_newarray.long_array.length /2);
				}
				else if(Driver.arrayType.equals("double"))
				{
					length = (Bytecode_newarray.double_array.length /2);
				}
			Integer z = length; 
			pushInteger(z);
			}
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}

		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
