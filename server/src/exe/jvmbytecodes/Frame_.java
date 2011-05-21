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
import exe.GAIGSlegend;
import exe.GAIGSItem;
import exe.GAIGStext;
import java.io.IOException;
import java.lang.InterruptedException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.*;
import java.util.*; 
import java.net.*;
import exe.*;

/*
 * A representation of a Java frame
* @author Caitlyn Pickens
* @author Cory Sheeley
 */
class Frame_{
 /**
    * The virtual operand stack
  */
    Stack _stack = new Stack();
 /**
    * The visual operand stack
  */
    GAIGSnewArray stack;
 /**
    * The virtual local variable array
  */
	String[] _localVariableArray;
 /**
    * A boolean counter for the shades of gray
  */
	boolean stackColor = false;
 /**
    * The visual local variable array
  */
    GAIGSnewArray localVariableArray;
 /**
    * The colors of the local variable array
  */
	String[] _colorLocalVariableArray;
 /**
    * The operand stack size
  */
    int stackSize = 0;
 /**
    * The current operant stack height
  */
    int currentStackHeight;
 /**
    * The method name
  */
    String methodName;
 /**
    * The return address
  */
    int returnAddress;
 /**
    * The index of the method
  */
    int methodIndex;
 /**
    * Colors
  */
	public GAIGSItem color1, color2, color3, color4;
 /**
    * Ints used for the legend
  */
	public int rows = 1, columns = 4;
 /**
    * The visual legend
  */
	public GAIGSlegend jvmLegend = new GAIGSlegend(rows, columns,"", -.15, -.08, 1.15, .55, 1.5);
 /**
    * The current frame color
  */
  	String CURRENT_FRAME_COLOR;
 /**
    * The label for an empty operand stack
  */
  	GAIGSlabel emptyStackLabel = new GAIGSlabel("Operand Stack (Empty)", 0.75, 0.05, 0.95, 0.1, 0.65); //create visual empty operand stack

 /**
    * The textBox for displaying information about the currently executing bytecode
   */
	GAIGStext textBox = new GAIGStext(.25, .9, 1, 2, .04, "#000000", " "); //create the textbox

    /**
	 * Constructor
	 * Intialize all values and colors in a frame.
     *
     * @param           The index of the current method
     */
	public Frame_(int currentMethod)
	{
		stackSize = Driver.classes[0].methods.get(currentMethod).stackSize; //set stacksize
		currentStackHeight = Driver.classes[0].methods.get(currentMethod).stackSize; //set current stack height
		methodName = Driver.classes[0].methods.get(currentMethod).name; //set method name
		stack = new GAIGSnewArray(stackSize, "Operand Stack", "#999999", 0.75, 0.1, 0.95, 0.9, 0.15); //create visual operand stack
		methodIndex = currentMethod; //set method index
		_localVariableArray = new String[Driver.classes[0].methods.get(currentMethod).numLocals]; //create virtual local variable array
		_colorLocalVariableArray = new String[Driver.classes[0].methods.get(currentMethod).numLocals]; //create color local variable array

		//if the local variable array is too big, throw an exception
		if (Driver.classes[0].methods.get(currentMethod).numLocals > 20)
			throw new InvalidClassFileException("Please limit the number of local variables in a frame of \n"
				+"the program to 20.");
		//if the operand stack is too big, throw an exception
		if (stackSize > 20)
			throw new InvalidClassFileException("Please limit the operand stack size in a frame to 20.");
		
		CURRENT_FRAME_COLOR = Driver.runTimeStackColors[Driver._runTimeStack.size()%3]; //set current frame coloe

		//set legend
		color1 = new GAIGSItem("After Execution", "#CCFFCC"); 
		color2 = new GAIGSItem("Before Execution", "#FFDDDD"); 
		color3 = new GAIGSItem("Current Frame", CURRENT_FRAME_COLOR); 
		color4 = new GAIGSItem("Objects Stored Here", "#EAC1F7");
		jvmLegend.setItem(0, 0, color1); 
		jvmLegend.setItem(0, 1, color2);
		jvmLegend.setItem(0, 2, color3);
		jvmLegend.setItem(0, 3, color4);
		jvmLegend.disableBox();

		//set stack to initial values, rather than "null"
		for (int i = 0; i < stackSize; i++)
			stack.set("", i);

		//create, set, and sort local var array
		localVariableArray = new GAIGSnewArray(Driver.classes[0].methods.get(currentMethod).numLocals,
				"Local Variables", "#999999", 0.15, 0.1, 0.55, 0.9, 0.15);

		String[][] array = Driver.classes[0].methods.get(currentMethod).localVariableTable;
		Arrays.sort(array, new Compare());

		//set values and colors in visual local variable array
		for (int i = 0; i < _localVariableArray.length; i++) {
			localVariableArray.set("", i);
			localVariableArray.setRowLabel(Integer.toString(i), i);
			localVariableArray.setColor(i, Driver.lightGray);
		}
		
		//take out extra labels
		for (int i = 0; i < Driver.classes[0].methods.get(currentMethod).localVariableTable.length; i++) {
			int index = Integer.parseInt(Driver.classes[0].methods.get(currentMethod).localVariableTable[i][0]);
			localVariableArray.setRowLabel(array[i][1] + " | " + array[i][0], index);
		}

		for (int i = 0; i < _localVariableArray.length; i++) {
			if(localVariableArray.getRowLabel(i).contains("|"))
				;
			else
				localVariableArray.setRowLabel("", i);
		}

		//set alternating gray colors in the color local variable array
		int num = 0;
		for(int j = 0; j < _colorLocalVariableArray.length; j++) {
			boolean label1 = localVariableArray.getRowLabel(j).contains("|");
			boolean label2 = false;
			if(j != _colorLocalVariableArray.length-1)
				label2 = !localVariableArray.getRowLabel(j+1).contains("|");
			else {
				if(num == 0)
					_colorLocalVariableArray[j] = Driver.lightGray;
				else
					_colorLocalVariableArray[j] = Driver.darkGray;
				break;			
			}
			if (label1 && label2) {
				if(num == 0)
				{
					_colorLocalVariableArray[j] = Driver.lightGray;
					_colorLocalVariableArray[j+1] = Driver.lightGray;
					num = 1;
					j++;
				}
				else {
					_colorLocalVariableArray[j] = Driver.darkGray;
					_colorLocalVariableArray[j+1] = Driver.darkGray;
					num = 0;
					j++;
				}
			}
			else {
				if(num == 0) {
					_colorLocalVariableArray[j] = Driver.lightGray;
					num = 1;
				}
				else {
					_colorLocalVariableArray[j] = Driver.darkGray;
					num = 0;
				}
			}
		}

		//set visual local variable array colors according to color local variable array
		for(int i = 0; i < _colorLocalVariableArray.length; i++)
			localVariableArray.setColor(i, _colorLocalVariableArray[i]);

		//set the color of the visual operand stack
		for(int i = 0; i < stackSize; i++)
			stack.setColor(i, Driver.lightGray);
	}
}
