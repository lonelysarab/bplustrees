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
* <p><code>Bytecode_load</code> provides a representation of a "load" bytecode in the JVM.
* Use the <code>Bytecode_load</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_load</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate loading something to the local variable array.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//iload, lload, fload, dload implemented
public class Bytecode_load extends Bytecode_
{

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_load object
	 */
	Bytecode_load(String str) 
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

		if(underscore.compareTo("_") == 0 || opcode.contains("iaload") || opcode.contains("faload") || opcode.contains("laload") || opcode.contains("daload")) //update the next line number again if necessary
			;
		else
			next += 1;

		if(opcode.contains("i") && !opcode.contains("iaload")) //iload
		{
			int index = Integer.parseInt(arguments.get(0));
			loadInteger(Integer.parseInt(f._localVariableArray[index]));
		}
		else if(opcode.contains("ll") && !opcode.contains("laload")) //lload
		{
			int index = Integer.parseInt(arguments.get(0));
			loadLong(Long.parseLong(f._localVariableArray[index]));
		}
		else if(opcode.contains("f") && !opcode.contains("faload")) //fload
		{
			int index = Integer.parseInt(arguments.get(0));
			pushFloat(Float.parseFloat(f._localVariableArray[index]));
		}
		else if(opcode.contains("dl") && !opcode.contains("daload")) //dload
		{
			int index = Integer.parseInt(arguments.get(0));
			loadDouble(Double.parseDouble(f._localVariableArray[index]));
		}
		else if(opcode.contains("aload") && !opcode.contains("iaload") && !opcode.contains("faload") && !opcode.contains("laload") && !opcode.contains("daload")) //aload
		{
			int index = Integer.parseInt(arguments.get(0));
			loadString(f._localVariableArray[index]);
		}
		else if(opcode.contains("iaload")) //iaload
		{
			int index = popInteger();
			String refArray = popString();
			if(refArray.equals("Arr 1")) {
				loadArrayInteger(Bytecode_newarray.int_array[index]);
			}
		}
		else if(opcode.contains("faload")) //faload
		{
			int index = popInteger();
			String refArray = popString();
			if(refArray.equals("Arr 1")) {
				loadArrayFloat(Bytecode_newarray.float_array[index]);
			}
		}
		else if(opcode.contains("laload")) //laload
		{
			int index = popInteger();
			index = index * 2;
			String refArray = popString();
			if(refArray.equals("Arr 1")) {
				loadArrayLong(Bytecode_newarray.long_array[index]);
			}
		}
		else if(opcode.contains("daload")) //daload
		{
			int index = popInteger();
			index = index * 2;
			String refArray = popString();
			if(refArray.equals("Arr 1")) {
				loadArrayDouble(Bytecode_newarray.double_array[index]);
			}
		}



		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}

}
