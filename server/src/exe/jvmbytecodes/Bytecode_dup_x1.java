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
* <p><code>Bytecode_dup_x1</code> provides a representation of a "dup_x1" bytecode in the JVM.
* Use the <code>Bytecode_dup_x1</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_dup_x1</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the duplication_x1 operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//dup_x1 implemented
public class Bytecode_dup_x1 extends Bytecode_
{
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_dup_x1 object
	 */
	Bytecode_dup_x1(String str) 
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

		//get the objects to be duplicated off the virtual stack
		Object x, y;
		x = f._stack.pop();
		y = f._stack.pop();

		//duplicate the objects on the virtual stack
		f._stack.push(x);
		f._stack.push(y);
		f._stack.push(x);

		//duplicatae the objects on the visual stack
		f.stack.set(x, f.currentStackHeight+2, Driver.CURRENT_HIGHLIGHT_COLOR);

		//write the snapshot
		writeSnap();

		//update the coloring
		//this code must be corrected when objects are implemented
		f.stack.setColor(f.currentStackHeight+2, "#999999");
		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
