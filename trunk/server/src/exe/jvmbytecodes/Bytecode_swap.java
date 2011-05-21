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
* <p><code>Bytecode_swap</code> provides a representation of a "swap" bytecode in the JVM.
* Use the <code>Bytecode_swap</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_swap</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate swapping two values on the operand stack.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//swap implemented
public class Bytecode_swap extends Bytecode_
{
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_swap object
	 */
	Bytecode_swap(String str) 
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

		//get the two objects to swap
		Object x, y;
		x = f._stack.pop();
		y = f._stack.pop();
		f.stack.set("",f.currentStackHeight++);
		f.stack.set("",f.currentStackHeight++);

		//swap the objects on the virtual stack
		f._stack.push(x);
		f._stack.push(y);

		//swap the objects on the visual stack
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(y, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		writeSnap(); //write the snapshot

		//coloring must be correct when objects are implemented
		f.stack.setColor(f.currentStackHeight, "#999999");
		f.stack.setColor(f.currentStackHeight + 1, "#999999");
		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
