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
* <p><code>Bytecode_dup2_1x</code> provides a representation of a "dup2_1x" bytecode in the JVM.
* Use the <code>Bytecode_dup2_1x</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_dup2_1x</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the duplication2_1x operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//dup2_1x implemented
public class Bytecode_dup2_x1 extends Bytecode_
{
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_dup2_1x object
	 */
	Bytecode_dup2_x1(String str) 
	{
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException
	{
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
		next = lineNumber+1; //update the next line number

		//get the top three items on the virtual stack
		Object x, y, z;
 		x = f._stack.pop();
		y = f._stack.pop();
		z = f._stack.pop();
		f.stack.set("",f.currentStackHeight++);
		f.stack.set("",f.currentStackHeight++);
		f.stack.set("",f.currentStackHeight++);

		//duplicate the items in the correct manner on the virtual stack
		f._stack.push(y);
		f._stack.push(x);
		f._stack.push(z);
		f._stack.push(y);
		f._stack.push(x);

		//duplicate the items in the correct manner on the visual stack
		f.stack.set(y, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(z, --f.currentStackHeight, "#999999");
		f.stack.set(y, --f.currentStackHeight, "#999999");
		f.stack.set(x, --f.currentStackHeight, "#999999");

		//write the snapshot
		writeSnap();

		//set the colors of the visual stack
		//this section must be corrected when objects are implemented
		f.stack.setColor(f.currentStackHeight, "#999999");
		f.stack.setColor(f.currentStackHeight+1, "#999999");
		f.stack.setColor(f.currentStackHeight+2, "#999999");
		f.stack.setColor(f.currentStackHeight+3, "#999999");
		f.stack.setColor(f.currentStackHeight+4, "#999999");
		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
