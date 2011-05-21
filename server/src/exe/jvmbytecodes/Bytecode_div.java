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
* <p><code>Bytecode_div</code> provides a representation of a "div" bytecode in the JVM.
* Use the <code>Bytecode_div</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_div</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the division operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//idiv, ldiv, fdiv, ddiv implemented
class Bytecode_div extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_div object
	 */
	Bytecode_div(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
		next = lineNumber+1; //update the next line number
	
		//idiv
		if(opcode.contains("id"))
		{
			Integer x = popInteger();
			Integer y = popInteger();
			Integer z = (y / x); //do the division
			text = "The bytecode idiv performed " + y + " / " + x + " = " + z + ".";
			pushInteger(z);
		}
		//ldiv
		else if(opcode.contains("l"))
		{
			Long x = popLong();
			Long y = popLong();
			Long z = (y / x); //do the division
			text = "The bytecode ldiv performed " + y + " / " + x + " = " + z + ".";
			pushLong(z);
		}
		//fdiv
		else if(opcode.contains("f"))
		{
			Float x = popFloat();
			Float y = popFloat();
			Float z = (y / x); //do the division
			text = "The bytecode fdiv performed " + y + " / " + x + " = " + z + ".";
			pushFloat(z);
		}
		//ddiv
		else if(opcode.contains("dd"))
		{
			Double x = popDouble();
			Double y = popDouble();
			Double z = (y / x); //do the division
			text = "The bytecode ddiv performed " + y + " / " + x + " = " + z + ".";
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
