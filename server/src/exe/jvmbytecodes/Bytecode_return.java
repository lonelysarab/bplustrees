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
* <p><code>Bytecode_return</code> provides a representation of a "return" bytecode in the JVM.
* Use the <code>Bytecode_return</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_return</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate returning from an invoked method.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//return, ireturn, lreturn, freturn, dreturn implemented
public class Bytecode_return extends Bytecode_ {

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_return object
	 */
	Bytecode_return(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException {

		if(opcode.equals("return")) //return
		{
			if(Driver.runTimeStack.size() > 1)
			{
				writeSnap(); //write snapshot
				Driver._runTimeStack.pop(); //pop frame off virtual runtime stack
				Driver.runTimeStack.pop(); //pop frame off visual runtime stack
				f = (Frame_) Driver._runTimeStack.peek(); //get current frame
				next = f.returnAddress; //next line is the return address
				String x = (String) Driver.runTimeStack.pop();
				Driver.runTimeStack.push(x, f.CURRENT_FRAME_COLOR);
				Driver.currentMethod = f.methodIndex;
			}
			else
			{
				writeSnap();  //write snapshot
				f = (Frame_) Driver._runTimeStack.pop(); //pop frame off virtual runtime stack
				Driver.runTimeStack.pop(); //pop frame off visual runtime stack
				next = -1; //end program
				writeFinalSnap(); //write final snapshot
			}
		}
		else if(opcode.equals("ireturn")) //ireturn
		{
			Frame_ f2 = (Frame_) Driver._runTimeStack.pop(); //pop frame off virtual runtime stack
			Driver.runTimeStack.pop(); //pop frame off visual runtime stack
			f = (Frame_) Driver._runTimeStack.peek(); //get current frame
			System.out.println("here");
			int var = (Integer) f2._stack.pop(); //get int return value

			next = f.returnAddress; //set next line number to return address
			Driver.currentMethod = f.methodIndex; //set current method index
			String x = (String) Driver.runTimeStack.pop();
			Driver.runTimeStack.push(x, f.CURRENT_FRAME_COLOR);
			pushInteger(var);
		}
		else if(opcode.equals("lreturn")) //lreturn
		{
			Frame_ f2 = (Frame_) Driver._runTimeStack.pop(); //pop frame off virtual runtime stack
			Driver.runTimeStack.pop(); //pop frame off visual runtime stack
			f = (Frame_) Driver._runTimeStack.peek(); //get current frame
			long var = (Long) f2._stack.pop(); //get long return value
			f2._stack.pop();

			next = f.returnAddress; //set next line number to return address
			Driver.currentMethod = f.methodIndex; //set current method index
			String x = (String) Driver.runTimeStack.pop();
			Driver.runTimeStack.push(x, f.CURRENT_FRAME_COLOR);
			pushLong(var);
		}
		else if(opcode.equals("freturn"))
		{
			Frame_ f2 = (Frame_) Driver._runTimeStack.pop(); //pop frame off virtual runtime stack
			Driver.runTimeStack.pop(); //pop frame off visual runtime stack
			f = (Frame_) Driver._runTimeStack.peek(); //get current frame
			float var = (Float) f2._stack.pop(); //get float return value

			next = f.returnAddress; //set next line number to return address
			Driver.currentMethod = f.methodIndex; //set current method index
			String x = (String) Driver.runTimeStack.pop();
			Driver.runTimeStack.push(x, f.CURRENT_FRAME_COLOR);
			pushFloat(var);
		}
		else if(opcode.equals("dreturn"))
		{
			Frame_ f2 = (Frame_) Driver._runTimeStack.pop(); //pop frame off virtual runtime stack
			Driver.runTimeStack.pop(); //pop frame off visual runtime stack
			f = (Frame_) Driver._runTimeStack.peek(); //get current frame
			double var = (Double) f2._stack.pop(); //get double return value
			f2._stack.pop();

			next = f.returnAddress; //set next line number to return address
			Driver.currentMethod = f.methodIndex; //set current method index
			String x = (String) Driver.runTimeStack.pop();
			Driver.runTimeStack.push(x, f.CURRENT_FRAME_COLOR);
			pushDouble(var);
		}
		else
			System.out.println("Unrecognized opcode");

		return next; //return next line number
	}
}
