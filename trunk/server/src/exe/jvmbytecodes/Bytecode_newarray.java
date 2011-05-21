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
* <p><code>Bytecode_mul</code> provides a representation of a "newarray" bytecode in the JVM.
* Use the <code>Bytecode_newarray</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_newarray</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the newarray operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//newarray implemented
class Bytecode_newarray extends Bytecode_ {
    static int[] int_array;
    static float[] float_array;
    static long[] long_array;
    static double[] double_array;
	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_newarray object
	 */
	Bytecode_newarray(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException, InvalidClassFileException {
		f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
		next = lineNumber+1; //update the next line number
		Driver.numberOfArrays += 1;
		boolean colorType = true;
		//if there is no main method, throw an error
		if(Driver.numberOfArrays > 1)
			throw new InvalidClassFileException("The input class must be limited to one array.\n" + "Your input class currently includes more than one array.\n" + "Please check your java source code and make sure only one array exists.");

		if(arguments.get(0).compareTo("int") == 0) //newarray int
		{
			Driver.arrayType = "int";
			Integer x = popInteger();
			int y = x.intValue();
    			int_array = new int[y];
			Driver.array = new GAIGSnewArray(y, "Array " + Driver.numberOfArrays, "#999999", -.05, 0.6, 0.05, 0.9, .5);
			//set values and colors in visual local variable array
			for (int i = 0; i < int_array.length; i++) {
				Driver.array.set("", i);
				Driver.array.setRowLabel(Integer.toString(i), i);
				if(colorType == true) {
					Driver.array.setColor(i, Driver.lightGray);
					colorType = false;
				}
				else {
					Driver.array.setColor(i, Driver.darkGray);
					colorType = true;
				}
			}
			String z = "Arr " + Driver.numberOfArrays;
			next += 1;
			pushString(z);
		}
		else if(arguments.get(0).compareTo("float") == 0) //newarray float
		{
			Driver.arrayType = "float";
			Integer x = popInteger();
			int y = x.intValue();
			float_array = new float[y];
			Driver.array = new GAIGSnewArray(y, "Array " + Driver.numberOfArrays, "#999999", -.05, 0.6, 0.05, 0.9, .5);
			//set values and colors in visual local variable array
			for (int i = 0; i < float_array.length; i++) {
				Driver.array.set("", i);
				Driver.array.setRowLabel(Integer.toString(i), i);
				if(colorType == true) {
					Driver.array.setColor(i, Driver.lightGray);
					colorType = false;
				}
				else {
					Driver.array.setColor(i, Driver.darkGray);
					colorType = true;
				}
			}
			String z = "Arr " + Driver.numberOfArrays;
			next += 1;
			pushString(z);
		}
		else if(arguments.get(0).compareTo("long") == 0) //newarray long
		{
			Driver.arrayType = "long";
			Integer x = popInteger();
			int y = (x.intValue()) * 2;
			long_array = new long[y];
			Driver.array = new GAIGSnewArray(y, "Array " + Driver.numberOfArrays, "#999999", -.05, 0.6, 0.05, 0.9, .5);
			//set values and colors in visual local variable array
			for (int i = 0; i < long_array.length; i++) {
				Driver.array.set("", i);
			}
			int index = 0;
			for (int j = 0; j < long_array.length; j += 2) {
				Driver.array.setRowLabel(Integer.toString(index), j);
				if(colorType == true) {
					Driver.array.setColor(j, Driver.lightGray);
					Driver.array.setColor(j+1, Driver.lightGray);
					colorType = false;
				}
				else {
					Driver.array.setColor(j, Driver.darkGray);
					Driver.array.setColor(j+1, Driver.darkGray);
					colorType = true;
				}
				index++;
			}
			String z = "Arr " + Driver.numberOfArrays;
			next += 1;
			pushString(z);
		}
		else if(arguments.get(0).compareTo("double") == 0) //newarray double
		{
			Driver.arrayType = "double";
			Integer x = popInteger();
			int y = (x.intValue()) * 2;
			double_array = new double[y];
			Driver.array = new GAIGSnewArray(y, "Array " + Driver.numberOfArrays, "#999999", -.05, 0.6, 0.05, 0.9, .5);
			//set values and colors in visual local variable array
			for (int i = 0; i < double_array.length; i++) {
				Driver.array.set("", i);
			}
			int index = 0;
			for (int j = 0; j < double_array.length; j += 2) {
				Driver.array.setRowLabel(Integer.toString(index), j);
				if(colorType == true) {
					Driver.array.setColor(j, Driver.lightGray);
					Driver.array.setColor(j+1, Driver.lightGray);
					colorType = false;
				}
				else {
					Driver.array.setColor(j, Driver.darkGray);
					Driver.array.setColor(j+1, Driver.darkGray);
					colorType = true;
				}
				index++;
			}
			String z = "Arr " + Driver.numberOfArrays;
			next += 1;
			pushString(z);
		}
		else
		{
			System.out.println("Unrecognized opcode");
		}

		f.returnAddress = next; //update the return address
		return next; //return the next line number
	}
}
