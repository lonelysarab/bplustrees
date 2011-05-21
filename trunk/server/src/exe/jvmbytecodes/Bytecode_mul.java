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
* <p><code>Bytecode_mul</code> provides a representation of a "mul" bytecode in the JVM.
* Use the <code>Bytecode_mul</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_mul</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the multiplication operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//imul, lmul, fmul, dmul implemented
class Bytecode_mul extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_mul object
	 */
	Bytecode_mul(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
		next = lineNumber+1; //update the next line number


		if(opcode.contains("i")) //i mul
		{
			Integer x = popInteger();
			Integer y = popInteger();
			Integer z = (y * x); //multiplication operator
			text = "The bytecode imul performed " + y + " * " + x + " = " + z + ".";
			pushInteger(z);
		}
		else if(opcode.contains("lm")) //lmul
		{
			Long x = popLong();
			Long y = popLong();
			Long z = (y * x); //multiplication operator
			text = "The bytecode lmul performed " + y + " * " + x + " = " + z + ".";
			pushLong(z);
		}
		else if(opcode.contains("f")) //fmul
		{
			Float x = popFloat();
			Float y = popFloat();
			Float z = (y * x); //multiplication operator
			text = "The bytecode fmul performed " + y + " * " + x + " = " + z + ".";
			pushFloat(z);
		}
		else if(opcode.contains("d")) //dmul
		{
			Double x = popDouble();
			Double y = popDouble();
			Double z = (y * x); //multiplication operator
			text = "The bytecode dmul performed " + y + " * " + x + " = " + z + ".";
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
