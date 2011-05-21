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
* <p><code>Bytecode_add</code> provides a representation of an "add" bytecode in the JVM.
* Use the <code>Bytecode_add</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_add</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the add operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//irem, lrem, frem, drem
class Bytecode_rem extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_rem object
	 */
	Bytecode_rem(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
		next = lineNumber+1; //update the next line number

		//irem
		if(opcode.contains("i"))
		{
			Integer x = popInteger();
			Integer y = popInteger();
			Integer z = (y % x); //remainder operator
			text = "The bytecode irem performed " + y + " % " + x + " = " + z + ".";
			pushInteger(z);
		}
		//lrem
		else if(opcode.contains("l"))
		{
			Long x = popLong();
			Long y = popLong();
			Long z = (y % x); //remainder operator
			text = "The bytecode lrem performed " + y + " % " + x + " = " + z + ".";
			pushLong(z);
		}
		//frem
		else if(opcode.contains("f"))
		{
			Float x = popFloat();
			Float y = popFloat();
			Float z = (y % x); //remainder operator
			text = "The bytecode frem performed " + y + " % " + x + " = " + z + ".";
			pushFloat(z);
		}
		//drem
		else if(opcode.contains("d"))
		{
			Double x = popDouble();
			Double y = popDouble();
			Double z = (y % x); //remainder operator
			text = "The bytecode drem performed " + y + " % " + x + " = " + z + ".";
			pushDouble(z);
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}

		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
