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
import org.jdom.JDOMException;

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

//iadd, ladd, fadd, dadd implemented
class Bytecode_add extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_add object
	 */
	Bytecode_add(String str) {
		System.out.println("Enter Bytecode_add constructor");
		parse(str);
		System.out.println("Complete Bytecode_parse");
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		next = lineNumber + 1; //update lineNumber
		f = (Frame_) Driver._runTimeStack.peek(); //get current frame	

		//iadd
		if(opcode.contains("i"))
		{
			Integer x = popInteger();
			Integer y = popInteger();
			Integer z = (y + x); //do the add
			text = "The bytecode iadd performed " + y + " + " + x + " = " + z + ".";
			pushInteger(z);
		}
		//ladd
		else if(opcode.contains("l"))
		{
			Long x = popLong();
			Long y = popLong();
			Long z = (y + x); //do the add
			text = "The bytecode ladd performed " + y + " + " + x + " = " + z + ".";
			pushLong(z);
		}
		//fadd
		else if(opcode.contains("f"))
		{
			Float x = popFloat();
			Float y = popFloat();
			Float z = (y + x); //do the add
			text = "The bytecode fadd performed " + y + " + " + x + " = " + z + ".";
			pushFloat(z);
		}
		//dadd
		else if(opcode.contains("d"))
		{
			Double x = popDouble();
			Double y = popDouble();
			Double z = (y + x); //do the add
			text = "The bytecode dadd performed " + y + " + " + x + " = " + z + ".";
			pushDouble(z);
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}

		f.returnAddress = next; //set return address
		return next; //return next line number
	}
}


