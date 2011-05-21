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
* <p><code>Bytecode_i2</code> provides a representation of an "i2" bytecode in the JVM.
* Use the <code>Bytecode_i2</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_i2</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate converting an int to another primitive type.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/


//i2b, i2c, i2d, i2f, i2l, i2s
class Bytecode_i2 extends Bytecode_ {
	
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_i2 object
	 */
	Bytecode_i2(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {
		next = lineNumber + 1; //update lineNumber
		f = (Frame_) Driver._runTimeStack.peek(); //get current frame	

		Integer x;
		if(opcode.contains("i2d")) {
			x = popInteger();
			double y = x.doubleValue();
			pushDouble(y);
		}
		else if(opcode.contains("i2f")) {
			x = popInteger();
			float y = x.floatValue();
			pushFloat(y);
		}
		else if(opcode.contains("i2l")) {
			x = popInteger();
			long y = x.longValue();
			pushLong(y);
		}
		else if(opcode.contains("i2b") | opcode.contains("i2c") | opcode.contains("i2s"))
		{
			x = popInteger();

			//still visualized as an integer
			int y = x;
			pushInteger(y);
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}
		f.returnAddress = next; //set return address
		return next; //return next line number
	}

}
