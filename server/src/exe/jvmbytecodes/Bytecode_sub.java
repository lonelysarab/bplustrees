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
* <p><code>Bytecode_sub</code> provides a representation of a "sub" bytecode in the JVM.
* Use the <code>Bytecode_sub</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_sub</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the subtraction operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

// isub, lsub, fsub, dsub implemented
class Bytecode_sub extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_sub object
	 */
	Bytecode_sub(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		next = lineNumber + 1; //update the next line number
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame

		//isub
		if(opcode.contains("i"))
		{
			Integer x = popInteger();
			Integer y = popInteger();
			Integer z = (y - x); //subtraction operator
			text = "The bytecode isub performed " + y + " - " + x + " = " + z + ".";
			pushInteger(z);
		}
		//lsub
		else if(opcode.contains("l"))
		{
			Long x = popLong();
			Long y = popLong();
			Long z = (y - x); //subtraction operator
			text = "The bytecode lsub performed " + y + " - " + x + " = " + z + ".";
			pushLong(z);
		}
		//fsub
		else if(opcode.contains("f"))
		{
			Float x = popFloat();
			Float y = popFloat();
			Float z = (y - x); //subtraction operator
			text = "The bytecode fsub performed " + y + " - " + x + " = " + z + ".";
			pushFloat(z);
		}
		//dsub
		else if(opcode.contains("d"))
		{
			Double x, y;
			x = popDouble();
			y = popDouble();
			Double z = (y - x); //subtraction operator
			text = "The bytecode dsub performed " + y + " - " + x + " = " + z + ".";
			pushDouble(z);
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}

		f.returnAddress = next; //update return address
		return next; //return next line number
	}
}
